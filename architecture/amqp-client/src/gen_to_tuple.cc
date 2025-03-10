/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include <fmt/format.h>
#include <fmt/ranges.h>
#include <numeric>
#include <vector>

int main(int argc, char **argv) {
    std::size_t max = argc > 1 ? std::atoi(argv[1]) : 100;

    fmt::print("{}", R"(
template<aggregate T>
constexpr auto tie(T &&value) noexcept {
    constexpr auto elt_count = element_count<T>();
    if constexpr (elt_count == 0 ) {
         return std::tie();
    })");

    for (std::size_t i = 1; i < max; ++i) {
        std::vector<int> n(i);
        std::iota(std::begin(n), std::end(n), 0);

        fmt::print(
            R"( else if constexpr ( elt_count == {} ) {{
        auto &[e{elts:}] = value;
        return std::tie(e{elts:});
    }})",
            i,
            fmt::arg("elts", fmt::join(n, ", e"))
        );
    }
    fmt::println("\n}}");
}