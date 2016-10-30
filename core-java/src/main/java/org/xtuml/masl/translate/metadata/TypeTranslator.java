//
// File: TypeTranslator.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.translate.metadata;

import static org.xtuml.masl.translate.metadata.Architecture.anyInstanceTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.booleanTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.byteTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.characterTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.deviceTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.dictionaryTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.durationTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.enumTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.eventTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.instanceTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.integerTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.localVarMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.parameterMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.realTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.smallIntegerTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.stringTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.structureTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.timerTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.timestampTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.typeMetaData;
import static org.xtuml.masl.translate.metadata.Architecture.wcharacterTypeFlag;
import static org.xtuml.masl.translate.metadata.Architecture.wstringTypeFlag;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.metamodel.type.CollectionType;
import org.xtuml.masl.metamodel.type.DictionaryType;
import org.xtuml.masl.metamodel.type.EnumerateType;
import org.xtuml.masl.metamodel.type.InstanceType;
import org.xtuml.masl.metamodel.type.StructureType;
import org.xtuml.masl.metamodel.type.TypeDeclaration;
import org.xtuml.masl.metamodel.type.UserDefinedType;
import org.xtuml.masl.translate.main.object.ObjectTranslator;


public class TypeTranslator
{

  private static int getCollectionDepth ( final BasicType type )
  {
    final BasicType basisType = type.getBasicType();

    if ( basisType instanceof CollectionType )
    {
      return 1 + getCollectionDepth(((CollectionType)basisType).getContainedType());
    }
    else
    {
      return 0;
    }
  }


  private static BasicType getFundamentalType ( final BasicType type )
  {
    final BasicType basisType = type.getBasicType();

    if ( basisType instanceof CollectionType )
    {
      return getFundamentalType(((CollectionType)basisType).getContainedType());
    }
    else
    {
      return basisType;
    }
  }

  static Expression getTypeMetaData ( final BasicType type )
  {
    final List<Expression> params = new ArrayList<Expression>();

    final BasicType fundamentalType = getFundamentalType(type);

    params.add(getTypeFlag(fundamentalType));

    if ( fundamentalType instanceof InstanceType )
    {
      params.add(org.xtuml.masl.translate.main.DomainTranslator.getInstance(((InstanceType)fundamentalType).getObjectDeclaration()
                                                                                                          .getDomain())
                                                              .getDomainId());
      params.add(ObjectTranslator.getInstance(((InstanceType)fundamentalType).getObjectDeclaration()).getObjectId());
    }
    else if ( fundamentalType instanceof UserDefinedType )
    {
      params.add(org.xtuml.masl.translate.main.DomainTranslator.getInstance(((UserDefinedType)fundamentalType).getDomain())
                                                              .getDomainId());
      params.add(getTypeId(fundamentalType.getTypeDeclaration()));
    }
    else if ( fundamentalType instanceof DictionaryType )
    {
      params.add(getTypeMetaData(((DictionaryType)fundamentalType).getKeyType()));
      params.add(getTypeMetaData(((DictionaryType)fundamentalType).getValueType()));
    }

    final int collectionDepth = getCollectionDepth(type);
    if ( collectionDepth > 0 )
    {
      params.add(new Literal(collectionDepth));
    }

    return typeMetaData.callConstructor(params);
  }

  static private Expression getTypeFlag ( final BasicType fundamentalType )
  {
    switch ( fundamentalType.getActualType() )
    {
      case BOOLEAN:
        return booleanTypeFlag;
      case BYTE:
        return byteTypeFlag;
      case CHARACTER:
        return characterTypeFlag;
      case DEVICE:
        return deviceTypeFlag;
      case EVENT:
        return eventTypeFlag;
      case DURATION:
        return durationTypeFlag;
      case INTEGER:
        return integerTypeFlag;
      case SMALL_INTEGER:
        return smallIntegerTypeFlag;
      case REAL:
        return realTypeFlag;
      case STRING:
        return stringTypeFlag;
      case TIMESTAMP:
        return timestampTypeFlag;
      case WCHARACTER:
        return wcharacterTypeFlag;
      case WSTRING:
        return wstringTypeFlag;
      case ANY_INSTANCE:
        return anyInstanceTypeFlag;
      case INSTANCE:
        return instanceTypeFlag;
      case DICTIONARY:
        return dictionaryTypeFlag;
      case TIMER:
        return timerTypeFlag;
      case USER_DEFINED:
      {
        if ( fundamentalType.getDefinedType() instanceof StructureType )
        {
          return structureTypeFlag;
        }
        else if ( fundamentalType.getDefinedType() instanceof EnumerateType )
        {
          return enumTypeFlag;
        }
        else
        {
          throw new IllegalArgumentException(fundamentalType.toString());
        }
      }
      default:
        throw new IllegalArgumentException(fundamentalType.toString());
    }
  }

  static private Expression getTypeId ( final TypeDeclaration type )
  {
    final DomainTranslator domainTranslator = DomainTranslator.getInstance(type.getDomain());
    return domainTranslator.getTypeId(type);
  }

  static Expression getLocalVarMetaData ( final VariableDefinition variable )
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.add(Literal.createStringLiteral(variable.getName()));

    params.add(Literal.createStringLiteral(variable.getType().toString()));
    params.add(TypeTranslator.getTypeMetaData(variable.getType()));

    return localVarMetaData.callConstructor(params);
  }

  static Expression getParameterMetaData ( final ParameterDefinition param )
  {
    final List<Expression> params = new ArrayList<Expression>();
    params.add(Literal.createStringLiteral(param.getName()));

    params.add(Literal.createStringLiteral(param.getType().toString()));
    params.add(TypeTranslator.getTypeMetaData(param.getType()));
    params.add((param.getMode() == ParameterDefinition.Mode.IN) ? Literal.FALSE : Literal.TRUE);

    return parameterMetaData.callConstructor(params);
  }


}
