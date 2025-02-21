/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_ListenerPriority_HH
#define SWA_ListenerPriority_HH

namespace SWA {
class ListenerPriority {
  public:
    static const ListenerPriority &getMinimum();
    static const ListenerPriority &getLow();
    static const ListenerPriority &getNormal();
    static const ListenerPriority &getHigh();
    static const ListenerPriority &getMaximum();

    int getValue() const { return priority; }

  private:
    ListenerPriority(int priority);
    ListenerPriority(const ListenerPriority &low, const ListenerPriority &high);
    int priority;
};

} // namespace SWA

#endif
