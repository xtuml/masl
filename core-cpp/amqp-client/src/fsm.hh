/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#pragma once
#include "condition_var.hh"
#include "spawn.hh"
#include <algorithm>
#include <cstdint>
#include <logging/log.hh>

#include <asio/awaitable.hpp>
#include <asio/detached.hpp>
#include <asio/experimental/awaitable_operators.hpp>
#include <asio/experimental/concurrent_channel.hpp>

namespace amqp_asio::fsm {
    using namespace asio::experimental::awaitable_operators;

    template <std::size_t N>
    struct StringLiteral {
        constexpr StringLiteral(const char (&str)[N]) {
            std::copy_n(str, N, value);
        }

        char value[N];
    };

    struct Terminal;
    struct Normal;

    template <StringLiteral Name, typename state_type = Normal>
    struct State {
        static constexpr auto name = Name.value;
        static constexpr auto terminal = std::same_as<state_type, Terminal>;
    };

    template <typename T>
    concept state = std::convertible_to<decltype(T::name), std::string>;

    struct Ignore;
    struct CannotHappen;

    template <typename T>
    concept destination_state = state<T> || std::same_as<T, Ignore> || std::same_as<T, CannotHappen>;

    template <StringLiteral Name, typename Value = void>
    struct Event {
        static constexpr const char *name = Name.value;
        using value_type = Value;
        Value value;
    };

    template <StringLiteral Name>
    struct Event<Name, void> {
        static constexpr const char *name = Name.value;
        using value_type = void;
    };

    template <typename T>
    concept event = std::convertible_to<decltype(T::name), std::string> && requires { typename T::value_type; };

    template <state From, destination_state To, event Event>
    struct Transition {
        using from = From;
        using to = To;
        using event = Event;
    };

    template <typename T>
    concept transition = state<typename T::from> && event<typename T::event> && destination_state<typename T::to>;

    template <typename T, typename S, typename E>
    concept matching_transition =
        state<S> && event<E> && std::same_as<S, typename T::from> && std::same_as<E, typename T::event>;

    template <typename S, typename E, typename FSM>
    struct TxDestination {
        using type = CannotHappen;
    };

    template <typename S, typename E, typename Row, typename... Rows>
    struct TxDestination<S, E, std::tuple<Row, Rows...>> {
        using type = TxDestination<S, E, std::tuple<Rows...>>::type;
    };

    template <typename S, typename E, matching_transition<S, E> Row, typename... Rows>
    struct TxDestination<S, E, std::tuple<Row, Rows...>> {
        using type = typename Row::to;
    };

    template <typename Rows, typename S, typename E>
    using transition_destination = TxDestination<S, E, Rows>::type;

    template <template <typename, typename...> typename Predicate, typename T, typename... Ts>
    struct filter_pack {
        using type = T;
    };

    template <
        template <typename, typename...>
        typename Predicate,
        template <typename...>
        class C,
        typename... Ts,
        typename U,
        typename... Us>
    struct filter_pack<Predicate, C<Ts...>, U, Us...> : std::conditional_t<
                                                            Predicate<U, Ts...>::value,
                                                            filter_pack<Predicate, C<Ts..., U>, Us...>,
                                                            filter_pack<Predicate, C<Ts...>, Us...>> {};

    template <template <typename, typename...> typename Predicate, typename T>
    struct filtered_variant;

    template <template <typename, typename...> typename Predicate, typename... Ts>
    struct filtered_variant<Predicate, std::variant<Ts...>> : filter_pack<Predicate, std::variant<>, Ts...> {};

    template <template <typename, typename...> typename Predicate, typename T>
    using filtered_variant_t = typename filtered_variant<Predicate, T>::type;

    template <typename U, typename... Ts>
    struct unique_filter {
        constexpr static bool value = !(std::same_as<U, Ts> || ...);
    };

    template <typename U, typename... Ts>
    struct state_filter {
        constexpr static bool value = state<U>;
    };

    template <transition... Tx>
    class StateMachine {
      public:
        template <typename Exec, typename... Args>
        StateMachine(const Exec &executor, fmt::format_string<Args...> name, Args &&...args)
            : log(std::move(name), std::forward<Args>(args)...),
              local_event_queue(executor, std::numeric_limits<std::size_t>::max()),
              event_queue(executor, std::numeric_limits<std::size_t>::max()),
              state_change(executor) {}

        template <typename Executor, typename... Args>
        void start(Executor &&exec, Args &&...args) {
            log.debug("Starting");
            spawn_cancellable(
                exec,
                [this, ... args = std::forward<Args>(args)]() -> asio::awaitable<void> {
                    co_await run(args...);
                },
                log
            );
        }

        void stop() {
            log.debug("Stopping");
            local_event_queue.close();
            event_queue.close();
        }

        template <typename... Args>
        asio::awaitable<void> run(Args &&...args) {
            while (!terminated_) {
                co_await co_handle_event(co_await next_event(), args...);
            }
            local_event_queue.close();
            event_queue.close();
            log.debug("Terminated");
        }

        using any_event = filtered_variant_t<unique_filter, std::variant<typename Tx::event...>>;
        asio::awaitable<void> generate_event(any_event e) {
            if (log.enabled(xtuml::logging::Logger::Level::DEBUG)) {
                std::visit(
                    [this]<event E>(const E &) {
                        log.debug("Queueing event {}", E::name);
                    },
                    e
                );
            }
            co_await event_queue.async_send(std::error_code{}, std::move(e));
        }

        asio::awaitable<void> generate_local_event(any_event e) {
            if (log.enabled(xtuml::logging::Logger::Level::DEBUG)) {
                std::visit(
                    [this]<event E>(const E &) {
                        log.debug("Queueing local event {}", E::name);
                    },
                    e
                );
            }
            co_await local_event_queue.async_send(std::error_code{}, std::move(e));
        }

        using any_state = filtered_variant_t<
            state_filter,
            filtered_variant_t<unique_filter, std::variant<typename Tx::from..., typename Tx::to...>>>;

        any_state current_state() const {
            return current_state_;
        }

        template <typename... S>
        asio::awaitable<void> wait_for_state() {
            co_await state_change.wait([this]() {
                return in_state<S...>();
            });
        }

        template <typename... S>
        bool in_state() const {
            return (std::holds_alternative<S>(current_state_) || ...);
        }

      private:
        using stt = std::tuple<Tx...>;

        template <state S, event E>
        using destination = TxDestination<std::decay_t<S>, std::decay_t<E>, stt>::type;

        template <typename... Args>
        asio::awaitable<void> co_handle_event(any_event ev, Args &&...args) {
            co_await std::visit(
                [... args = std::forward<Args>(args),
                 this]<state S, event E>(const S &from, E ev) mutable -> asio::awaitable<void> {
                    using to = destination<S, E>;
                    if constexpr (std::same_as<to, CannotHappen>) {
                        log.error("Event {} cannot happen in state {}", E::name, S::name);
                    } else if constexpr (std::same_as<to, Ignore>) {
                        log.debug("Event {} ignored in state {}", E::name, S::name);
                    } else {
                        log.debug("Event {} transitions {} --> {}", E::name, S::name, to::name);
                        current_state_ = to{};
                        try {
                            if constexpr (std::invocable<to, Args..., typename E::value_type>) {
                                co_await std::invoke(
                                    std::get<to>(current_state_), std::forward<Args>(args)..., std::move(ev.value)
                                );
                            } else if constexpr (std::invocable<to, Args...>) {
                                co_await std::invoke(std::get<to>(current_state_), std::forward<Args>(args)...);
                            } else {
                                log.debug("No state action for {}", to::name);
                            };
                        } catch (const std::exception &e) {
                            log.error("Error executing state action {}: {}", to::name, e.what());
                        }
                        terminated_ = to::terminal;
                        state_change.notify();
                    }
                },
                current_state_,
                std::move(ev)
            );
        }

        asio::awaitable<any_event> next_event() {

            if (local_event_queue.ready()) {
                co_return co_await local_event_queue.async_receive();
            } else {
                auto msg = co_await (
                    local_event_queue.async_receive(asio::use_awaitable) ||
                    event_queue.async_receive(asio::use_awaitable)
                );
                co_return std::visit(
                    [](auto &&value) -> any_event {
                        return value;
                    },
                    msg
                );
            }
        }

        any_state current_state_{};
        bool terminated_{};

        xtuml::logging::Logger log;
        asio::experimental::concurrent_channel<void(std::error_code, any_event)> local_event_queue;
        asio::experimental::concurrent_channel<void(std::error_code, any_event)> event_queue;
        ConditionVar state_change;
        friend class sanity_check;
    };

    template <>
    class StateMachine<> {
      public:
        template <typename Exec, typename... Args>
        StateMachine(const Exec &executor, fmt::format_string<Args...> name, Args &&...args) {}

        using any_event = std::variant<std::monostate>;
        using any_state = std::variant<std::monostate>;

        template <typename Executor, typename... Args>
        void start(Executor &&exec, Args &&...args) {}

        void stop() {}
    };

    class sanity_check {
        using S1 = State<"S1">;
        struct S2 : State<"S2"> {};

        using E1 = Event<"E1">;
        using E2 = Event<"E2", int>;

        using EmptyFsm = fsm::StateMachine<>;

        using ExampleFsm = fsm::StateMachine<
            fsm::Transition<S1, S1, E1>,
            fsm::Transition<S1, S2, E2>,
            fsm::Transition<S2, fsm::Ignore, E2>>;

        static_assert(fsm::state<S1>);
        static_assert(fsm::matching_transition<fsm::Transition<S1, S1, E1>, S1, E1>);

        static_assert(std::same_as<S1, ExampleFsm::destination<S1, E1>>);
        static_assert(std::same_as<S2, ExampleFsm::destination<S1, E2>>);
        static_assert(std::same_as<Ignore, ExampleFsm::destination<S2, E2>>);
        static_assert(std::same_as<CannotHappen, ExampleFsm::destination<S2, E1>>);
        static_assert(std::same_as<std::variant<E1, E2>, ExampleFsm::any_event>);
        static_assert(std::same_as<std::variant<S1, S2>, ExampleFsm::any_state>);
    };

} // namespace amqp_asio::fsm