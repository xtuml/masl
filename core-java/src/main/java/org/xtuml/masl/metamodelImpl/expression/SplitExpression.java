//
// File: SplitExpression.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.DurationType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.SequenceType;
import org.xtuml.masl.metamodelImpl.type.TimestampType;
import org.xtuml.masl.utils.HashCode;
import org.xtuml.masl.utils.TextUtils;


public class SplitExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.SplitExpression
{

  private final static Map<String, Set<Field>> tsDateLookup        = new HashMap<String, Set<Field>>();
  private final static Map<String, Set<Field>> tsTimeLookup        = new HashMap<String, Set<Field>>();

  private final static Map<Character, Field>   durDayLookup        = new HashMap<Character, Field>();
  private final static Map<Character, Field>   durTimeLookup       = new HashMap<Character, Field>();
  private final static Map<Character, Field>   durFractionalLookup = new HashMap<Character, Field>();
  private final static Map<String, Type>       typeLookup          = new HashMap<String, Type>();

  static
  {
    tsDateLookup.put("y", EnumSet.of(Field.CalendarYear));
    tsDateLookup.put("ym", EnumSet.of(Field.CalendarYear, Field.MonthOfYear));
    tsDateLookup.put("ymd", EnumSet.of(Field.CalendarYear, Field.MonthOfYear, Field.DayOfMonth));
    tsDateLookup.put("yd", EnumSet.of(Field.CalendarYear, Field.DayOfYear));
    tsDateLookup.put("yw", EnumSet.of(Field.WeekYear, Field.WeekOfYear));
    tsDateLookup.put("ywd", EnumSet.of(Field.WeekYear, Field.WeekOfYear, Field.DayOfWeek));
    tsDateLookup.put("", EnumSet.noneOf(Field.class));

    tsTimeLookup.put("h", EnumSet.of(Field.HourOfDay));
    tsTimeLookup.put("hm", EnumSet.of(Field.HourOfDay, Field.MinuteOfHour));
    tsTimeLookup.put("hms", EnumSet.of(Field.HourOfDay, Field.MinuteOfHour, Field.SecondOfMinute));
    tsTimeLookup.put("hmsf", EnumSet.of(Field.HourOfDay, Field.MinuteOfHour, Field.SecondOfMinute));
    tsTimeLookup.put("hmsfm", EnumSet.of(Field.HourOfDay, Field.MinuteOfHour, Field.SecondOfMinute, Field.MilliOfSecond));
    tsTimeLookup.put("hmsfmu", EnumSet.of(Field.HourOfDay,
                                          Field.MinuteOfHour,
                                          Field.SecondOfMinute,
                                          Field.MilliOfSecond,
                                          Field.MicroOfMilli));
    tsTimeLookup.put("hmsfmun", EnumSet.of(Field.HourOfDay,
                                           Field.MinuteOfHour,
                                           Field.SecondOfMinute,
                                           Field.MilliOfSecond,
                                           Field.MicroOfMilli,
                                           Field.NanoOfMicro));
    tsTimeLookup.put("hmsfmn", EnumSet.of(Field.HourOfDay,
                                          Field.MinuteOfHour,
                                          Field.SecondOfMinute,
                                          Field.MilliOfSecond,
                                          Field.NanoOfMilli));
    tsTimeLookup.put("hmsfu", EnumSet.of(Field.HourOfDay, Field.MinuteOfHour, Field.SecondOfMinute, Field.MicroOfSecond));
    tsTimeLookup.put("hmsfun", EnumSet.of(Field.HourOfDay,
                                          Field.MinuteOfHour,
                                          Field.SecondOfMinute,
                                          Field.MicroOfSecond,
                                          Field.NanoOfMicro));
    tsTimeLookup.put("hmsfn", EnumSet.of(Field.HourOfDay, Field.MinuteOfHour, Field.SecondOfMinute, Field.NanoOfSecond));
    tsTimeLookup.put("", EnumSet.noneOf(Field.class));


    durDayLookup.put('w', Field.Weeks);
    durDayLookup.put('d', Field.Days);

    durTimeLookup.put('h', Field.Hours);
    durTimeLookup.put('m', Field.Minutes);
    durTimeLookup.put('s', Field.Seconds);

    durFractionalLookup.put('m', Field.Millis);
    durFractionalLookup.put('u', Field.Micros);
    durFractionalLookup.put('n', Field.Nanos);

    typeLookup.put("split", Type.SPLIT);
    typeLookup.put("combine", Type.COMBINE);
  }

  SplitExpression ( final Position position, final Expression lhs, final String characteristic, final List<Expression> arguments ) throws SemanticError
  {
    super(position);
    this.arguments = arguments;
    this.lhs = lhs;
    this.characteristic = characteristic;

    final String[] parts = characteristic.split("_", 2);

    String suffix = null;
    if ( parts.length == 2 )
    {
      this.splitType = typeLookup.get(parts[0]);
      suffix = parts[1];
    }
    else
    {
      throw new SemanticError(SemanticErrorCode.CharacteristicNotFound, position, characteristic);
    }

    switch ( splitType )
    {
      case COMBINE:
      {
        if ( lhs instanceof TypeNameExpression )
        {
          resultType = ((TypeNameExpression)lhs).getReferencedType();
          if ( resultType.getPrimitiveType() instanceof TimestampType )
          {
            decodeTimestampSuffix(suffix);
          }
          else if ( resultType.getPrimitiveType() instanceof DurationType )
          {
            decodeDurationSuffix(suffix);
          }
          else
          {
            throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, position, characteristic, lhs.getType());
          }
        }
        else
        {
          throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, lhs.getPosition(), characteristic, lhs.getType());
        }

        checkParameters(getFieldTypes());
      }
        break;

      case SPLIT:
      {
        if ( TimestampType.createAnonymous().isAssignableFrom(lhs) )
        {
          decodeTimestampSuffix(suffix);
        }
        else if ( DurationType.createAnonymous().isAssignableFrom(lhs) )
        {
          decodeDurationSuffix(suffix);
        }
        else
        {
          throw new SemanticError(SemanticErrorCode.CharacteristicNotValid, position, characteristic, lhs.getType());
        }

        this.resultType = SequenceType.createAnonymous(IntegerType.createAnonymous());

        if ( arguments.size() != 0 )
        {
          throw new SemanticError(SemanticErrorCode.CharacteristicArgumentCountMismatch,
                                  position,
                                  characteristic,
                                  0,
                                  arguments.size());
        }

        break;
      }
      default:
        resultType = null;
        assert false;
    }

  }

  private SplitExpression ( final Position position,
                            final Expression lhs,
                            final Type splitType,
                            final String characteristic,
                            final Set<Field> fields,
                            final List<Expression> args,
                            final BasicType resultType )
  {
    super(position);
    this.characteristic = characteristic;
    this.lhs = lhs;
    this.splitType = splitType;
    this.fields.addAll(fields);
    this.arguments = args;

    this.resultType = resultType;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( obj != null )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj.getClass() == getClass() )
      {
        final SplitExpression obj2 = ((SplitExpression)obj);
        return lhs.equals(obj2.lhs)
               && splitType == obj2.splitType
               && characteristic == obj2.characteristic
               && arguments == obj2.arguments;
      }
      else
      {
        return false;
      }
    }
    return false;
  }

  @Override
  public List<Expression> getArguments ()
  {
    return Collections.unmodifiableList(arguments);
  }

  @Override
  public Set<Field> getFields ()
  {
    return fields;
  }

  @Override
  public int getFindAttributeCount ()
  {
    int count = 0;
    if ( lhs != null )
    {
      count += lhs.getFindAttributeCount();
    }
    for ( final Expression argument : arguments )
    {
      count += argument.getFindAttributeCount();
    }
    return count;
  }

  @Override
  public Expression getFindSkeletonInner ()
  {
    List<Expression> args = null;
    args = new ArrayList<Expression>();
    for ( final Expression arg : arguments )
    {
      args.add(arg.getFindSkeleton());
    }
    return new SplitExpression(getPosition(), lhs.getFindSkeleton(), splitType, characteristic, fields, args, resultType);

  }


  @Override
  public Expression getLhs ()
  {
    return lhs;
  }

  @Override
  public Type getSplitType ()
  {
    return splitType;
  }


  @Override
  public BasicType getType ()
  {
    return resultType;
  }

  @Override
  public int hashCode ()
  {
    return HashCode.combineHashes(fields.hashCode(), lhs.hashCode(), arguments.hashCode(), splitType.hashCode());
  }

  @Override
  public String toString ()
  {
    return lhs + "'" + characteristic + TextUtils.formatList(arguments, "(", ", ", ")");
  }

  @Override
  protected List<Expression> getFindArgumentsInner ()
  {
    final List<Expression> params = new ArrayList<Expression>();
    if ( lhs != null )
    {
      params.addAll(lhs.getFindArguments());
    }

    for ( final Expression arg : arguments )
    {
      params.addAll(arg.getFindArguments());
    }
    return params;

  }

  @Override
  protected List<FindParameterExpression> getFindParametersInner ()
  {
    final List<FindParameterExpression> params = new ArrayList<FindParameterExpression>();
    if ( lhs != null )
    {
      params.addAll(lhs.getConcreteFindParameters());
    }
    for ( final Expression arg : arguments )
    {
      params.addAll(arg.getConcreteFindParameters());
    }

    return params;

  }

  private void checkParameters ( final List<BasicType> fieldTypes ) throws SemanticError
  {
    if ( arguments.size() != fieldTypes.size() )
    {
      throw new SemanticError(SemanticErrorCode.CharacteristicArgumentCountMismatch,
                              getPosition(),
                              characteristic,
                              fieldTypes
                                        .size(),
                              arguments.size());
    }

    for ( int i = 0; i < arguments.size(); ++i )
    {
      fieldTypes.get(i).checkAssignable(arguments.get(i));
    }
  }

  private void decodeDurationSuffix ( final String suffix ) throws SemanticError
  {
    boolean inTime = false;
    boolean inFractional = false;

    for ( final char ch : suffix.toCharArray() )
    {
      if ( !inTime )
      {
        if ( ch == 't' )
        {
          inTime = true;
        }
        else if ( ch == 'f' )
        {
          inTime = true;
          inFractional = true;
        }
        else
        {
          final Field f = durDayLookup.get(ch);
          if ( f == null )
          {
            throw new SemanticError(SemanticErrorCode.CharacteristicSuffixInvalid, getPosition(), suffix, lhs.getType());
          }
          else
          {
            this.fields.add(f);
          }
        }
      }
      else if ( !inFractional )
      {
        if ( ch == 'f' )
        {
          inFractional = true;
        }
        else
        {
          final Field f = durTimeLookup.get(ch);
          if ( f == null )
          {
            throw new SemanticError(SemanticErrorCode.CharacteristicSuffixInvalid, getPosition(), suffix, lhs.getType());
          }
          else
          {
            this.fields.add(f);
          }
        }
      }
      else
      {
        final Field f = durFractionalLookup.get(ch);
        if ( f == null )
        {
          throw new SemanticError(SemanticErrorCode.CharacteristicSuffixInvalid, getPosition(), suffix, lhs.getType());
        }
        else
        {
          this.fields.add(f);
        }
      }
    }
  }


  private void decodeTimestampSuffix ( final String suffix ) throws SemanticError
  {
    final String[] dtSplit = suffix.split("t", 2);
    final Set<Field> dateFields = tsDateLookup.get(dtSplit[0]);
    final Set<Field> timeFields = tsTimeLookup.get(dtSplit.length == 2 ? dtSplit[1] : "");

    if ( dateFields == null
         || timeFields == null
         || (dateFields.size() == 0 && timeFields.size() == 0)
         || (timeFields.size() > 0 && !(dateFields.contains(Field.DayOfMonth) || dateFields.contains(Field.DayOfWeek) || dateFields
                                                                                                                                   .contains(Field.DayOfYear))) )
    {
      throw new SemanticError(SemanticErrorCode.CharacteristicSuffixInvalid, getPosition(), suffix, lhs.getType());
    }
    this.fields.addAll(dateFields);
    this.fields.addAll(timeFields);
  }

  private List<BasicType> getFieldTypes ()
  {
    final List<BasicType> eltypes = new ArrayList<BasicType>(this.fields.size());
    for ( int i = 0; i < this.fields.size(); ++i )
    {
      eltypes.add(IntegerType.createAnonymous());
    }

    return eltypes;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitSplitExpression(this, p);
  }

  private final Expression       lhs;
  private final List<Expression> arguments;
  private final BasicType        resultType;
  private final Type             splitType;
  private final String           characteristic;

  private final Set<Field>       fields = EnumSet.noneOf(Field.class);

  @Override
  public List<Expression> getChildExpressions ()
  {
    final List<Expression> result = new ArrayList<Expression>();
    result.add(lhs);
    result.addAll(arguments);
    return result;
  }


}
