/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.common;

import org.xtuml.masl.metamodelImpl.error.*;
import org.xtuml.masl.metamodelImpl.name.Named;

import java.util.*;

public class CheckedLookup<T extends Positioned> implements Iterable<T> {

    private final Map<String, T> lookup = new LinkedHashMap<>();
    private final List<T> list = new ArrayList<>();

    private final Named parent;
    private final SemanticErrorCode alreadyDefined;
    private final SemanticErrorCode notFound;

    public CheckedLookup(final SemanticErrorCode alreadyDefined, final SemanticErrorCode notFound, final Named parent) {
        this.alreadyDefined = alreadyDefined;
        this.notFound = notFound;
        this.parent = parent;
    }

    public CheckedLookup(final SemanticErrorCode alreadyDefined, final SemanticErrorCode notFound) {
        this(alreadyDefined, notFound, null);
    }

    public void put(final String name, final T value) throws AlreadyDefined {
        final T previousDef = lookup.get(name);
        if (previousDef != null) {
            throw new AlreadyDefined(alreadyDefined, Position.getPosition(name), name, previousDef.getPosition());
        } else {
            lookup.put(name, value);
            list.add(value);
        }
    }

    public T find(final String name) {
        return lookup.get(name);
    }

    public final T get(final String name) throws NotFound {
        final T ret = find(name);
        if (ret == null) {
            if (parent == null) {
                throw new NotFoundGlobal(notFound, Position.getPosition(name), name);
            } else {
                throw new NotFoundOnParent(notFound, Position.getPosition(name), name, parent.getName());
            }
        }
        return ret;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    public List<T> asList() {
        return list;
    }

    public int size() {
        return list.size();
    }
}
