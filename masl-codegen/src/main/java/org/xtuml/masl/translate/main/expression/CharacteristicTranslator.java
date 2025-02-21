/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.expression.CharacteristicExpression;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.type.*;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;

public class CharacteristicTranslator extends ExpressionTranslator {

    CharacteristicTranslator(final CharacteristicExpression characteristicReference, final Scope scope) {
        switch (characteristicReference.getCharacteristic()) {
            case FIRST:
                translateFirst(characteristicReference, scope);
                break;
            case LAST:
                translateLast(characteristicReference, scope);
                break;
            case IMAGE:
                translateImage(characteristicReference, scope);
                break;
            case GET_UNIQUE:
                translateGetUnique(characteristicReference, scope);
                break;
            case PRED:
                translatePred(characteristicReference, scope);
                break;
            case SUCC:
                translateSucc(characteristicReference, scope);
                break;
            case RANGE:
                translateRange(characteristicReference, scope);
                break;
            case LENGTH:
                translateLength(characteristicReference, scope);
                break;
            case UPPER:
                translateUpper(characteristicReference, scope);
                break;
            case LOWER:
                translateLower(characteristicReference, scope);
                break;
            case FIRSTCHARPOS:
                translateFirstCharPos(characteristicReference, scope);
                break;
            case VALUE:
                translateValue(characteristicReference, scope);
                break;
            case DATE:
                translateDatePart(characteristicReference, scope);
                break;
            case TIME:
                translateTimePart(characteristicReference, scope);
                break;
            case NOW:
                translateNow(characteristicReference, scope);
                break;
        }
    }

    void translateNow(final CharacteristicExpression characteristicReference, final Scope scope) {
        setReadExpression(Architecture.Timestamp.now);
    }

    void translateTimePart(final CharacteristicExpression characteristicReference, final Scope scope) {
        final Expression prefix = characteristicReference.getLhs();
        final org.xtuml.masl.cppgen.Expression base = createTranslator(prefix, scope).getReadExpression();
        setReadExpression(Architecture.Timestamp.getTime(base));
    }

    void translateDatePart(final CharacteristicExpression characteristicReference, final Scope scope) {
        final Expression prefix = characteristicReference.getLhs();
        final org.xtuml.masl.cppgen.Expression base = createTranslator(prefix, scope).getReadExpression();
        setReadExpression(Architecture.Timestamp.getDate(base));
    }

    void translateFirst(final CharacteristicExpression characteristicReference, final Scope scope) {
        final TypeDefinition defined = characteristicReference.getLhsType().getBasicType().getDefinedType();

        if (defined instanceof SequenceType || defined.isString()) {
            final Expression prefix = characteristicReference.getLhs();
            final org.xtuml.masl.cppgen.Expression base = createTranslator(prefix, scope).getReadExpression();
            setReadExpression(new Function("first").asFunctionCall(base, false));
        } else if (defined instanceof ArrayType) {
            setReadExpression(createTranslator(defined.getMinValue(), scope).getReadExpression());
        } else if (defined instanceof UnconstrainedArraySubtype) {
            setReadExpression(createTranslator(defined.getMinValue(), scope).getReadExpression());
        } else {
            setReadExpression(createTranslator(defined.getMinValue(), scope).getReadExpression());
        }

    }

    void translateFirstCharPos(final CharacteristicExpression characteristicReference, final Scope scope) {
        final org.xtuml.masl.cppgen.Expression
                base =
                createTranslator(characteristicReference.getLhs(), scope).getReadExpression();
        final org.xtuml.masl.cppgen.Expression
                ch =
                createTranslator(characteristicReference.getArguments().get(0), scope).getReadExpression();

        setReadExpression(new Function("firstCharPos").asFunctionCall(base, false, ch));

    }

    void translateGetUnique(final CharacteristicExpression characteristicReference, final Scope scope) {
        final ExpressionTranslator prefixTrans = createTranslator(characteristicReference.getLhs(), scope);

        final TypeUsage type = Types.getInstance().getType(characteristicReference.getType());
        setReadExpression(type.getType().callConstructor(prefixTrans.getReadExpression()));
        setWriteableExpression(type.getType().callConstructor(prefixTrans.getWriteableExpression()));

    }

    void translateImage(final CharacteristicExpression characteristicReference, final Scope scope) {
        final Expression prefix = characteristicReference.getLhs();
        final org.xtuml.masl.cppgen.Expression base = createTranslator(prefix, scope).getReadExpression();

        if (!(characteristicReference.getLhs().getType().getBasicType().getDefinedType().getActualType() ==
              ActualType.STRING)) {
            setReadExpression(Architecture.stringClass.callConstructor(Boost.lexicalCast(new TypeUsage(Std.string),
                                                                                         base)));
        } else {
            setReadExpression(base);
        }
    }

    void translateLast(final CharacteristicExpression characteristicReference, final Scope scope) {
        final TypeDefinition defined = characteristicReference.getLhsType().getBasicType().getDefinedType();

        if (defined instanceof SequenceType || defined.isString()) {
            final Expression prefix = characteristicReference.getLhs();
            final org.xtuml.masl.cppgen.Expression base = createTranslator(prefix, scope).getReadExpression();
            setReadExpression(new Function("last").asFunctionCall(base, false));
        } else if (defined instanceof ArrayType) {
            setReadExpression(createTranslator(defined.getMaxValue(), scope).getReadExpression());
        } else if (defined instanceof UnconstrainedArraySubtype) {
            setReadExpression(createTranslator(defined.getMaxValue(), scope).getReadExpression());
        } else {
            setReadExpression(createTranslator(defined.getMaxValue(), scope).getReadExpression());
        }

    }

    void translateLength(final CharacteristicExpression characteristicReference, final Scope scope) {
        final Expression prefix = characteristicReference.getLhs();
        final org.xtuml.masl.cppgen.Expression base = createTranslator(prefix, scope).getReadExpression();
        setReadExpression(new Function("size").asFunctionCall(base, false));
    }

    void translateLower(final CharacteristicExpression characteristicReference, final Scope scope) {
        setReadExpression(new Function("lower").asFunctionCall(createTranslator(characteristicReference.getLhs(),
                                                                                scope).getReadExpression(), false));
    }

    void translatePred(final CharacteristicExpression characteristicReference, final Scope scope) {
        setReadExpression(new UnaryExpression(UnaryOperator.PREDECREMENT,
                                              createTranslator(characteristicReference.getLhs(),
                                                               scope).getReadExpression()));
    }

    void translateRange(final CharacteristicExpression characteristicReference, final Scope scope) {
        org.xtuml.masl.cppgen.Expression minExp;
        org.xtuml.masl.cppgen.Expression maxExp;

        final TypeDefinition defined = characteristicReference.getLhsType().getBasicType().getDefinedType();

        if (defined instanceof SequenceType || defined.isString()) {
            final Expression prefix = characteristicReference.getLhs();
            final org.xtuml.masl.cppgen.Expression base = createTranslator(prefix, scope).getReadExpression();
            minExp = new Function("first").asFunctionCall(base, false);
            maxExp = new Function("last").asFunctionCall(base, false);
        } else if (defined instanceof ArrayType) {
            minExp = createTranslator(defined.getMinValue(), scope).getReadExpression();
            maxExp = createTranslator(defined.getMaxValue(), scope).getReadExpression();
        } else if (defined instanceof UnconstrainedArraySubtype) {
            minExp = createTranslator(defined.getMinValue(), scope).getReadExpression();
            maxExp = createTranslator(defined.getMaxValue(), scope).getReadExpression();
        } else {
            minExp = createTranslator(defined.getMinValue(), scope).getReadExpression();
            maxExp = createTranslator(defined.getMaxValue(), scope).getReadExpression();
        }
        final BasicType contained = ((CollectionType) characteristicReference.getType()).getContainedType();
        final TypeUsage type = Types.getInstance().getType(contained);
        setReadExpression(Architecture.sequence(type).callConstructor(Architecture.range(type).callConstructor(minExp,
                                                                                                               maxExp)));
    }

    void translateSucc(final CharacteristicExpression characteristicReference, final Scope scope) {
        setReadExpression(new UnaryExpression(UnaryOperator.PREINCREMENT,
                                              createTranslator(characteristicReference.getLhs(),
                                                               scope).getReadExpression()));
    }

    void translateUpper(final CharacteristicExpression characteristicReference, final Scope scope) {
        setReadExpression(new Function("upper").asFunctionCall(createTranslator(characteristicReference.getLhs(),
                                                                                scope).getReadExpression(), false));
    }

    void translateValue(final CharacteristicExpression characteristicReference, final Scope scope) {
        if (characteristicReference.getArguments().size() > 0) {
            final TypeUsage type = Types.getInstance().getType(characteristicReference.getType());
            setReadExpression(type.getType().callConstructor(createTranslator(characteristicReference.getArguments().get(
                    0), scope).getReadExpression()));
        } else {
            setReadExpression(new Function("getValue").asFunctionCall(createTranslator(characteristicReference.getLhs(),
                                                                                       scope).getReadExpression(),
                                                                      false));
        }
    }
}
