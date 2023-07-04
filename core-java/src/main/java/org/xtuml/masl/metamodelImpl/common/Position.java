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
package org.xtuml.masl.metamodelImpl.common;

import java.util.IdentityHashMap;

public abstract class Position {

    public abstract String getText();

    public abstract String getContext();

    public abstract int getLineNumber();

    @Override
    public String toString() {
        return getText();
    }

    public static Position getPosition(final String key) {
        return positionLookup.get(key);
    }

    public static void registerPosition(final String key, final Position position) {
        positionLookup.put(key, position);
    }

    private static final IdentityHashMap<String, Position> positionLookup = new IdentityHashMap<String, Position>();

    public static Position NO_POSITION = null;
}
