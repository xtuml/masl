/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#pragma once

#include <functional>
#include <vector>

namespace transient {
class ThreadListener {
  public:
    void addCleanup(const std::function<void()> function);
    static ThreadListener &getInstance();
    static bool initialise();

  private:
    ThreadListener();
    void performCleanup();
    std::vector<std::function<void()>> cleanupRoutines;
};

} // namespace transient
