/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2005-2024 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * -----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * -----------------------------------------------------------------------------
 */

#include <gtest/gtest.h>
#include <logging/log.hh>

class Environment : public ::testing::Environment {
 public:
    void SetUp() override {
        xtuml::logging::Logger::root().set_level(xtuml::logging::Logger::Level::INFO);
    }
};


int main(int argc, char** argv) {
    ::testing ::AddGlobalTestEnvironment(new Environment());
    testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}