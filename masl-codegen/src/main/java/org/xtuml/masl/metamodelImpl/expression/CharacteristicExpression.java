/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.type.EnumerateType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.*;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CharacteristicExpression extends Expression
        implements org.xtuml.masl.metamodel.expression.CharacteristicExpression {

    public class GetUnique extends TypeDecoder {

        @Override
        public BasicType getType() {
            return SetType.createAnonymous(getLhs().getType().getPrimitiveType().getContainedType());
        }
    }

    public class Image extends TypeDecoder {

        @Override
        public BasicType getType() {
            return StringType.createAnonymous();
        }
    }

    public class Index extends TypeDecoder {

        @Override
        public BasicType getType() {
            if (getLhs() instanceof TypeNameExpression) {
                final TypeDefinition defined = ((TypeNameExpression) getLhs()).getReferencedType().getDefinedType();
                if (defined instanceof ArrayType) {
                    return ((ArrayType) defined).getRange().getType();
                } else if (defined instanceof UnconstrainedArraySubtype) {
                    return ((UnconstrainedArraySubtype) defined).getRange().getType();
                } else {
                    return ((TypeNameExpression) getLhs()).getReferencedType();
                }
            } else {
                return IntegerType.createAnonymous();
            }
        }
    }

    public class Length extends TypeDecoder {

        @Override
        public BasicType getType() {
            return IntegerType.createAnonymous();
        }
    }

    public class Range extends TypeDecoder {

        @Override
        public BasicType getType() {
            return RangeType.createAnonymous(indexDecoder.getType());
        }

        final Index indexDecoder = new Index();

    }

    enum TypeLookup {
        any(null),

        elements(null, 0), parse(null), first(Type.FIRST, 0), firstcharpos(Type.FIRSTCHARPOS,
                                                                           1), get_unique(Type.GET_UNIQUE, 0), image(
                Type.IMAGE,
                0), last(Type.LAST, 0), length(Type.LENGTH, 0), lower(Type.LOWER, 0), pred(Type.PRED,
                                                                                           0), range(Type.RANGE,
                                                                                                     0), succ(Type.SUCC,
                                                                                                              0), upper(
                Type.UPPER,
                0), value(Type.VALUE, 1),

        eof(null),

        split(null, true), combine(null, true),

        nanoseconds(null), microseconds(null), milliseconds(null), seconds(null), minutes(null), hours(null), days(null), weeks(
                null),

        now(Type.NOW, 0), time(Type.TIME, 0), date(Type.DATE, 0),

        add_years(null, 1), add_months(null, 1),

        year(null, 0), month_of_year(null, 0), day_of_month(null, 0),

        day_of_year(null, 0),

        week_year(null, 0), week_of_year(null, 0), day_of_week(null, 0),

        hour_of_day(null, 0), minute_of_hour(null, 0), second_of_minute(null, 0),

        millisecond_of_second(null, 0), microsecond_of_second(null, 0), nanosecond_of_second(null, 0),

        microsecond_of_millisecond(null, 0), nanosecond_of_millisecond(null, 0),

        nanosecond_of_microsecond(null, 0),

        scheduled(null, 0), expired(null, 0), scheduled_at(null, 0), expired_at(null, 0), delta(null, 0), missed(null,
                                                                                                                 0),

        contains(null, 1), keys(null, 0), values(null, 0),

        ;

        TypeLookup(final Type type) {
            this.type = type;
            this.noArgs = -1;
            this.allowAsPrefix = false;
        }

        TypeLookup(final Type type, final boolean allowAsPrefix) {
            this.type = type;
            this.noArgs = -1;
            this.allowAsPrefix = allowAsPrefix;
        }

        TypeLookup(final Type type, final int noArgs) {
            this.type = type;
            this.noArgs = noArgs;
            this.allowAsPrefix = false;
        }

        int getNoArgs() {
            return noArgs;
        }

        Type getType() {
            return type;
        }

        boolean allowAsPrefix() {
            return allowAsPrefix;
        }

        private final Type type;

        private final int noArgs;

        private final boolean allowAsPrefix;

    }

    private class DateDecoder extends TypeDecoder {

        @Override
        public BasicType getType() {
            return getLhs().getType();
        }
    }

    private class NowDecoder extends TypeDecoder {

        @Override
        public BasicType getType() {
            return ((TypeNameExpression) getLhs()).getReferencedType();
        }
    }

    private class SameType extends TypeDecoder {

        @Override
        public BasicType getType() {
            return getLhs().getType();
        }
    }

    private class TimeDecoder extends TypeDecoder {

        @Override
        public BasicType getType() {
            return DurationType.createAnonymous();
        }
    }

    private abstract class TypeDecoder {

        abstract BasicType getType();
    }

    private class ValueType extends TypeDecoder {

        @Override
        public BasicType getType() {
            if (getLhs() instanceof TypeNameExpression) {
                return ((TypeNameExpression) getLhs()).getReferencedType();
            } else if (getLhs().getType().getBasicType().getDefinedType() instanceof EnumerateType) {
                return IntegerType.createAnonymous();
            } else {
                return null;
            }
        }
    }

    public static Expression create(final Position position,
                                    final Expression lhs,
                                    final String characteristic,
                                    final List<Expression> arguments) {
        if (lhs == null || characteristic == null) {
            return null;
        }

        try {
            TypeLookup characType = null;
            try {
                characType = TypeLookup.valueOf(characteristic);
            } catch (final IllegalArgumentException e) {
                try {
                    characType = TypeLookup.valueOf(characteristic.split("_", 2)[0]);
                    if (!characType.allowAsPrefix()) {
                        throw new SemanticError(SemanticErrorCode.CharacteristicNotFound, position, characteristic);
                    }
                } catch (final IllegalArgumentException e2) {
                    throw new SemanticError(SemanticErrorCode.CharacteristicNotFound, position, characteristic);
                }
            }

            if (characType.getNoArgs() >= 0 && arguments.size() != characType.getNoArgs()) {
                throw new SemanticError(SemanticErrorCode.CharacteristicArgumentCountMismatch,
                                        position,
                                        characteristic,
                                        String.valueOf(characType.getNoArgs()),
                                        arguments.size());
            }

            switch (characType) {
                case eof:
                    return new EofExpression(position, lhs);

                case any:
                    if (arguments.size() == 0) {
                        return new AnyExpression(position, lhs);
                    } else if (arguments.size() == 1) {
                        return new AnyExpression(position, lhs, arguments.get(0));
                    } else {
                        throw new SemanticError(SemanticErrorCode.CharacteristicArgumentCountMismatch,
                                                position,
                                                characteristic,
                                                "0..1",
                                                arguments.size());
                    }

                case elements:
                    return new ElementsExpression(position, lhs);

                case parse:
                    if (arguments.size() == 1) {
                        return new ParseExpression(position, lhs, arguments.get(0));
                    } else if (arguments.size() == 2) {
                        return new ParseExpression(position, lhs, arguments.get(0), arguments.get(1));
                    } else {
                        throw new SemanticError(SemanticErrorCode.CharacteristicArgumentCountMismatch,
                                                position,
                                                characteristic,
                                                "1..2",
                                                arguments.size());
                    }

                case add_years:
                case add_months:
                    return new TimestampDeltaExpression(position, lhs, characteristic, arguments.get(0));

                case split:
                case combine:
                    return new SplitExpression(position, lhs, characteristic, arguments);

                case nanoseconds:
                case microseconds:
                case milliseconds:
                case seconds:
                case minutes:
                case hours:
                case days:
                case weeks:
                    if (arguments.size() == 1) {
                        return new CreateDurationExpression(position, lhs, characteristic, arguments.get(0));
                    } else if (arguments.size() == 0) {
                        return new TimeFieldExpression(position, lhs, characteristic);
                    } else {
                        throw new SemanticError(SemanticErrorCode.CharacteristicArgumentCountMismatch,
                                                position,
                                                characteristic,
                                                "0..1",
                                                arguments.size());
                    }

                case year:
                case month_of_year:
                case day_of_month:
                case day_of_year:
                case week_year:
                case week_of_year:
                case day_of_week:
                case hour_of_day:
                case minute_of_hour:
                case second_of_minute:
                case millisecond_of_second:
                case microsecond_of_second:
                case nanosecond_of_second:
                case microsecond_of_millisecond:
                case nanosecond_of_millisecond:
                case nanosecond_of_microsecond:
                    return new TimeFieldExpression(position, lhs, characteristic);

                case scheduled:
                case expired:
                case delta:
                case scheduled_at:
                case expired_at:
                case missed:
                    return new TimerFieldExpression(position, lhs, characteristic);

                case contains:
                    return new DictionaryContainsExpression(position, lhs, arguments.get(0));

                case keys:
                    return new DictionaryKeysExpression(position, lhs);

                case values:
                    return new DictionaryValuesExpression(position, lhs);

                case get_unique:
                    if (lhs.getType().getPrimitiveType() instanceof CollectionType) {
                        return new CharacteristicExpression(position,
                                                            lhs,
                                                            TypeLookup.valueOf(characteristic).getType(),
                                                            arguments);
                    } else {
                        throw new SemanticError(SemanticErrorCode.CharacteristicNotValid,
                                                position,
                                                characteristic,
                                                lhs.getType());
                    }

                default:
                    return new CharacteristicExpression(position,
                                                        lhs,
                                                        TypeLookup.valueOf(characteristic).getType(),
                                                        arguments);
            }
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public CharacteristicExpression(final Position position,
                                    final Expression lhs,
                                    final Type characteristic,
                                    final List<Expression> arguments) {
        super(position);
        this.characteristic = characteristic;
        this.lhs = lhs;
        this.arguments = arguments;

        switch (characteristic) {
            case RANGE:
                typeDecoder = new Range();
                break;
            case VALUE:
                typeDecoder = new ValueType();
                break;
            case GET_UNIQUE:
                typeDecoder = new GetUnique();
                break;
            case IMAGE:
                typeDecoder = new Image();
                break;
            case LENGTH:
                typeDecoder = new Length();
                break;
            case UPPER:
            case LOWER:
            case PRED:
            case SUCC:
                typeDecoder = new SameType();
                break;

            case FIRST:
            case LAST:
            case FIRSTCHARPOS:
                typeDecoder = new Index();
                break;

            case DATE:
                typeDecoder = new DateDecoder();
                break;

            case TIME:
                typeDecoder = new TimeDecoder();
                break;

            case NOW:
                typeDecoder = new NowDecoder();
                break;

            default:
                assert false;
                typeDecoder = null;

        }

    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }
            if (obj.getClass() == getClass()) {
                final CharacteristicExpression obj2 = ((CharacteristicExpression) obj);
                return lhs.equals(obj2.lhs) &&
                       getCharacteristic() == obj2.getCharacteristic() &&
                       arguments == obj2.arguments;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public List<Expression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public Type getCharacteristic() {
        return this.characteristic;
    }

    @Override
    public int getFindAttributeCount() {
        int count = 0;
        if (lhs != null) {
            count += lhs.getFindAttributeCount();
        }
        for (final Expression argument : arguments) {
            count += argument.getFindAttributeCount();
        }

        return count;
    }

    @Override
    public Expression getFindSkeletonInner() {
        List<Expression> args = null;
        args = new ArrayList<>();
        for (final Expression arg : arguments) {
            args.add(arg.getFindSkeleton());
        }
        return new CharacteristicExpression(getPosition(), lhs.getFindSkeleton(), characteristic, args);

    }

    @Override
    public Expression getLhs() {
        return lhs;
    }

    @Override
    public BasicType getLhsType() {
        if (lhs instanceof TypeNameExpression) {
            return ((TypeNameExpression) lhs).getReferencedType();
        } else {
            return lhs.getType();
        }
    }

    @Override
    public BasicType getType() {
        return typeDecoder.getType();
    }

    @Override
    public int hashCode() {
        return characteristic.hashCode();
    }

    @Override
    public String toString() {
        return lhs + "'" + characteristic.toString().toLowerCase() + TextUtils.formatList(arguments, "(", ", ", ")");
    }

    @Override
    protected List<Expression> getFindArgumentsInner() {
        final List<Expression> params = new ArrayList<>();
        if (lhs != null) {
            params.addAll(lhs.getFindArguments());
        }

        for (final Expression arg : arguments) {
            params.addAll(arg.getFindArguments());
        }
        return params;

    }

    @Override
    protected List<FindParameterExpression> getFindParametersInner() {
        final List<FindParameterExpression> params = new ArrayList<>();
        if (lhs != null) {
            params.addAll(lhs.getConcreteFindParameters());
        }
        for (final Expression arg : arguments) {
            params.addAll(arg.getConcreteFindParameters());
        }

        return params;

    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitCharacteristicExpression(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(lhs, arguments);
    }

    private final Expression lhs;
    private final List<Expression> arguments;
    private final TypeDecoder typeDecoder;
    private final Type characteristic;

}
