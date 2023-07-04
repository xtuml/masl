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
package org.xtuml.masl.javagen.astimpl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class ChildNodeList<C extends ASTNodeImpl> extends AbstractList<C> {

    @Override
    public void add(final int index, final C element) {
        parent.addChildNode(element);
        nodes.add(index, element);
    }

    @Override
    public C get(final int index) {
        return nodes.get(index);
    }

    @Override
    public C remove(final int index) {
        parent.removeChildNode(nodes.get(index));
        return nodes.remove(index);
    }

    @Override
    public C set(final int index, final C element) {
        parent.removeChildNode(nodes.get(index));
        parent.addChildNode(element);
        return nodes.set(index, element);
    }

    @Override
    public int size() {
        return nodes.size();
    }

    ChildNodeList(final ASTNodeImpl parent) {
        this.parent = parent;
    }

    private final ASTNodeImpl parent;
    private final List<C> nodes = new ArrayList<C>();
}
