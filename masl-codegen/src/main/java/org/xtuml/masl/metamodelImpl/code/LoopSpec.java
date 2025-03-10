/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.*;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.SequenceType;

public abstract class LoopSpec implements org.xtuml.masl.metamodel.code.LoopSpec {

    public static LoopSpec create(final String name, final boolean reverse, final Expression range) {
        if (name == null || range == null) {
            return null;
        }

        try {
            if (range instanceof MinMaxRange) {
                return new org.xtuml.masl.metamodelImpl.code.FromToRange(name, reverse, (MinMaxRange) range);
            } else if (range instanceof ElementsExpression) {
                return new org.xtuml.masl.metamodelImpl.code.VariableElements(name,
                                                                              reverse,
                                                                              ((ElementsExpression) range).getCollection());
            } else if (range instanceof CharacteristicExpression charac) {
                if (charac.getCharacteristic() == CharacteristicExpression.Type.RANGE) {
                    if (charac.getLhs() instanceof TypeNameExpression) {
                        return new org.xtuml.masl.metamodelImpl.code.TypeRange(name,
                                                                               reverse,
                                                                               ((TypeNameExpression) charac.getLhs()).getReferencedType());
                    } else {
                        return new org.xtuml.masl.metamodelImpl.code.VariableRange(name, reverse, charac.getLhs());
                    }
                } else {
                    throw new SemanticError(SemanticErrorCode.InvalidLoopSpec, range.getPosition(), range.getType());
                }
            } else if (range.getType().getPrimitiveType() instanceof SequenceType) {
                return new org.xtuml.masl.metamodelImpl.code.VariableElements(name, reverse, range);
            } else {
                throw new SemanticError(SemanticErrorCode.InvalidLoopSpec, range.getPosition(), range.getType());
            }
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public LoopSpec(final String loopVariable, final boolean reverse, final BasicType type) {
        this.loopVariable = loopVariable;
        this.reverse = reverse;
        this.loopVarDef = new VariableDefinition(loopVariable, type, true, null, new PragmaList());
    }

    final public String getLoopVariable() {
        return this.loopVariable;
    }

    @Override
    final public VariableDefinition getLoopVariableDef() {
        return loopVarDef;
    }

    @Override
    final public boolean isReverse() {
        return this.reverse;
    }

    @Override
    public String toString() {
        return loopVariable + " in" + (reverse ? " reverse" : "");
    }

    private final String loopVariable;

    private final boolean reverse;

    private final VariableDefinition loopVarDef;

}
