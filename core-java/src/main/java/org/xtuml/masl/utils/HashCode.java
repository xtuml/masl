/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.utils;

public class HashCode {

    public static int makeHash(final Object... objects) {
        int hash = 0;
        for (int i = 0; i < objects.length; ++i) {
            if (objects[i] == null) {
                hash = combineHash(hash, 0);
            } else {
                hash = combineHash(hash, objects[i].hashCode());
            }
        }

        return hash;
    }

    public static int combineHashes(final int... hashes) {
        int hash = 0;
        for (int i = 0; i < hashes.length; ++i) {
            hash = combineHash(hash, hashes[i]);
        }

        return hash;
    }

    private static int combineHash(final int seed, final int hash) {
        // Use same algorithm as C++ Boost.Hash, as presumably they know what they
        // are talking about when designing a good hash.
        return seed ^ (hash + 0x9e377b9b + (seed << 6) + (seed >> 2));
    }

}
