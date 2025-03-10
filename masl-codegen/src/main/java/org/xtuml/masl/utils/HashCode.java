/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.utils;

public class HashCode {

    public static int makeHash(final Object... objects) {
        int hash = 0;
        for (final Object object : objects) {
            if (object == null) {
                hash = combineHash(hash, 0);
            } else {
                hash = combineHash(hash, object.hashCode());
            }
        }

        return hash;
    }

    public static int combineHashes(final int... hashes) {
        int hash = 0;
        for (final int hashe : hashes) {
            hash = combineHash(hash, hashe);
        }

        return hash;
    }

    private static int combineHash(final int seed, final int hash) {
        // Use same algorithm as C++ Boost.Hash, as presumably they know what they
        // are talking about when designing a good hash.
        return seed ^ (hash + 0x9e377b9b + (seed << 6) + (seed >> 2));
    }

}
