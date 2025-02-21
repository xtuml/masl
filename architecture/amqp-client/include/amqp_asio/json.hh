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
#include "aggregate.hh"
#include <chrono>
#include <fmt/chrono.h>
#include <fmt/format.h>
#include <nlohmann/json.hpp>
#include <variant>

namespace amqp_asio::types {

    namespace detail {
        template <typename T>
        auto amqp_value_to_json(const T &v) {
            return nlohmann::json(v);
        }

        inline auto amqp_value_to_json(const std::monostate &) {
            return nlohmann::json(nullptr);
        }
        template <typename C, typename D>
        auto amqp_value_to_json(const std::chrono::time_point<C, D> &v) {
            return nlohmann::json(fmt::format("{:%FT%TZ}", v));
        }

        template <typename R, typename P>
        auto amqp_value_to_json(const std::chrono::duration<R, P> &v) {
            return nlohmann::json(fmt::format("{:%S}s", v));
        }

        template <typename... T>
        auto amqp_value_to_json(const std::variant<T...> &v) {
            return std::visit(
                [](const auto &v) {
                    return amqp_value_to_json(v);
                },
                v
            );
        }

        template <typename K, typename V>
        auto amqp_value_to_json(const std::vector<std::pair<K, V>> &map) {
            auto j = nlohmann::json::array();
            for (auto &[k, v] : map) {
                j.push_back({amqp_value_to_json(k), amqp_value_to_json(v)});
            }
            return j;
        }

        template <typename V>
        auto amqp_value_to_json(const std::vector<std::pair<std::string, V>> &map) {
            auto j = nlohmann::json::object();
            for (auto &[k, v] : map) {
                j[k] = amqp_value_to_json(v);
            }
            return j;
        }

        template <typename K, typename V>
            requires requires(K k) {
                { k.value() } -> std::convertible_to<std::string>;
            }
        auto amqp_value_to_json(const std::vector<std::pair<K, V>> &map) {
            auto j = nlohmann::json::object();
            for (auto &[k, v] : map) {
                j[k.value()] = amqp_value_to_json(v);
            }
            return j;
        }

        template <typename K, typename V>
        void tuple_elt_to_json(nlohmann::json &j, const char *name, const std::vector<std::pair<K, V>> &map) {
            j[name] = amqp_value_to_json(map);
        }

        template <typename T>
        void tuple_elt_to_json(nlohmann::json &j, const char *name, const T &v) {
            j[name] = amqp_value_to_json(v);
        }

        template <typename T>
        void tuple_elt_to_json(nlohmann::json &j, const char *name, const std::optional<T> &v) {
            if (v) {
                j[name] = amqp_value_to_json(v.value());
            }
        }

        template <typename T>
        void tuple_elt_to_json(nlohmann::json &j, const char *name, const std::vector<T> &v) {
            if (!v.empty()) {
                j[name] = amqp_value_to_json(v);
            }
        }

        template <typename... T>
        void tuple_to_json(nlohmann::json &j, const char *const names[sizeof...(T)], const T &...v) {
            std::size_t i = 0;
            (..., tuple_elt_to_json(j, names[i++], v));
        }

    } // namespace detail

    template <detail::aggregate T>
        requires requires { T::elt_names; }
    void to_json(nlohmann::json &j, const T &v) {
        if constexpr (requires { T::amqp_descriptor; }) {
            j["_type"] = T::amqp_descriptor.name;
        }
        std::apply(
            [&](const auto &...vs) {
                detail::tuple_to_json(j, T::elt_names, vs...);
            },
            detail::tie(v)
        );
    }

} // namespace amqp_asio::types