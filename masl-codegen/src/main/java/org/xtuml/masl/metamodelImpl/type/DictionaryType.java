/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;

import java.util.List;

public class DictionaryType extends BasicType implements org.xtuml.masl.metamodel.type.DictionaryType {

    public static DictionaryType create(final Position position,
                                        BasicType keyType,
                                        BasicType valueType,
                                        final boolean anonymous) {
        if (keyType == null) {
            keyType = StringType.createAnonymous();
        }
        if (valueType == null) {
            valueType = StringType.createAnonymous();
        }

        return new DictionaryType(position, keyType, valueType, anonymous);
    }

    public static DictionaryType createAnonymous(final BasicType keyType, final BasicType valueType) {
        return new DictionaryType(null, keyType, valueType, true);
    }

    private DictionaryType(final Position position,
                           final BasicType keyType,
                           final BasicType valueType,
                           final boolean anonymous) {
        super(position, anonymous);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public DictionaryType getPrimitiveType() {
        return DictionaryType.createAnonymous(getKeyType(), getValueType());
    }

    @Override
    public String toString() {
        return (isAnonymousType() ? "anonymous " : "") + "dictionary " + keyType + " of " + valueType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DictionaryType other = (DictionaryType) obj;
        if (keyType == null) {
            if (other.keyType != null) {
                return false;
            }
        } else if (!keyType.equals(other.keyType)) {
            return false;
        }
        if (valueType == null) {
            return other.valueType == null;
        } else {
            return valueType.equals(other.valueType);
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = ((keyType == null) ? 0 : keyType.hashCode());
        result = PRIME * result + ((valueType == null) ? 0 : valueType.hashCode());
        return result;
    }

    @Override
    public BasicType getKeyType() {
        return keyType;
    }

    @Override
    public BasicType getValueType() {
        return valueType;
    }

    @Override
    public BasicType getContainedType() {
        return valueType;
    }

    @Override
    public DictionaryType getBasicType() {
        return new DictionaryType(null, keyType.getBasicType(), valueType.getBasicType(), true);
    }

    @Override
    public ActualType getActualType() {
        return ActualType.DICTIONARY;
    }

    @Override
    public void checkCanBePublic() {
        keyType.checkCanBePublic();
        valueType.checkCanBePublic();
    }

    private final BasicType keyType;
    private final BasicType valueType;

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitDictionaryType(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(keyType, valueType);
    }

}
