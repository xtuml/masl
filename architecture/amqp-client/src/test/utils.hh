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
#include <gtest/gtest.h>

#include "amqp_asio/types.hh"
#include "../codec.hh"
#include <boost/algorithm/hex.hpp>
#include <boost/algorithm/string/erase.hpp>
#include <regex>

using namespace amqp_asio::types;
using namespace std::literals;

namespace amqp_asio::types {

    inline void PrintTo(any_t data, std::ostream *s) {
        *s << fmt::format("{}", data);
    }

    inline void PrintTo(const any_value_t &value, std::ostream *s) {
        *s << fmt::format("{}", value);
    }

    template<typename T, typename Tag>
    inline void PrintTo(const wrapper_t<T, Tag> &value, std::ostream *s) {
        *s << fmt::format("{}", value.value);
    }

    inline void PrintTo(const NumericDescriptor &value, std::ostream *s) {
        *s << fmt::format("{}", value);
    }

}

namespace amqp_asio::messages {
    template<typename T>
    concept json_convertible = requires(T t) {
        nlohmann::json(t);
    };

    template<json_convertible T>
    inline void PrintTo(const T &value, std::ostream *s) {
        *s << fmt::format("{}", nlohmann::json(value).dump());
    }

}

namespace testing::internal {
    template <>
    class UniversalPrinter<std::byte> {
    public:
        static void Print(std::byte value, ::std::ostream* os) {
            *os << fmt::format("{:02x}", value);
        }
    };

    template <>
    class UniversalPrinter<std::vector<std::byte>> {
    public:
        static void Print(std::vector<std::byte> value, ::std::ostream* os) {
            *os << fmt::format("{:02x}", fmt::join(value, " "));
        }
    };
}


inline std::string replace_ascii(std::string hex) {
    // look for quoted strings and replace with ascii hex codes
    static const std::regex finder{R"('([^']*)')"};
    std::smatch match;
    while ( std::regex_search(hex, match, finder)) {
        std::string text = match[1].str();
        std::string text_hex = "";
        for ( auto c : text ) {
            text_hex += fmt::format("{:02x}",c);
        }
        hex.replace(match[0].first, match[0].second, text_hex);
    }
    return hex;
}


inline std::vector<std::byte> vector_from_hex(std::string hex) {
    hex = replace_ascii(std::move(hex));

    boost::algorithm::erase_all(hex, " ");
    boost::algorithm::erase_all(hex, "\n");
    boost::algorithm::erase_all(hex, "(");
    boost::algorithm::erase_all(hex, ")");

    auto cbuffer = boost::algorithm::unhex(hex);
    std::vector<std::byte> bbuffer{cbuffer.size()};
    std::transform(cbuffer.begin(), cbuffer.end(), bbuffer.begin(), [](auto v) { return static_cast<std::byte>(v); });
    return bbuffer;
}

template<std::size_t N>
inline std::array<std::byte, N> array_from_hex(std::string hex) {
    boost::algorithm::erase_all(hex, " ");
    auto cbuffer = boost::algorithm::unhex(hex);
    std::array<std::byte, N> bbuffer{};
    cbuffer.resize(N);
    std::transform(cbuffer.begin(), cbuffer.end(), bbuffer.begin(), [](auto v) { return static_cast<std::byte>(v); });
    return bbuffer;
}

template<class T>
struct CodecTestParams {
    std::string hex;
    T value;
    std::string name;
    Descriptors descriptors;
};


inline auto test_name(const testing::TestParamInfo<CodecTestParams<any_value_t>> &p) {
    static std::vector<std::string> type_names = {
            "null",
            "boolean",
            "ubyte",
            "ushort",
            "uint",
            "ulong",
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "decimal32",
            "decimal64",
            "decimal128",
            "char",
            "timestamp",
            "uuid",
            "binary",
            "string",
            "symbol",
            "array",
            "list",
            "map"};

    auto [hex, expected, name, descriptors] = p.param;

    boost::algorithm::erase_all(hex, " ");
    boost::algorithm::erase_all(hex, "\n");


    return type_names[expected.index()] + "_" + (name.empty() ? hex : name);
}
