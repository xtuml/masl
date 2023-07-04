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
package org.xtuml.masl.translate.metadata;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.type.*;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

import java.util.ArrayList;
import java.util.List;

import static org.xtuml.masl.translate.metadata.Architecture.*;

public class TypeTranslator {

    private static int getCollectionDepth(final BasicType type) {
        final BasicType basisType = type.getBasicType();

        if (basisType instanceof CollectionType) {
            return 1 + getCollectionDepth(((CollectionType) basisType).getContainedType());
        } else {
            return 0;
        }
    }

    private static BasicType getFundamentalType(final BasicType type) {
        final BasicType basisType = type.getBasicType();

        if (basisType instanceof CollectionType) {
            return getFundamentalType(((CollectionType) basisType).getContainedType());
        } else {
            return basisType;
        }
    }

    static Expression getTypeMetaData(final BasicType type) {
        final List<Expression> params = new ArrayList<Expression>();

        final BasicType fundamentalType = getFundamentalType(type);

        params.add(getTypeFlag(fundamentalType));

        if (fundamentalType instanceof InstanceType) {
            params.add(org.xtuml.masl.translate.main.DomainTranslator.getInstance(((InstanceType) fundamentalType).getObjectDeclaration().getDomain()).getDomainId());
            params.add(ObjectTranslator.getInstance(((InstanceType) fundamentalType).getObjectDeclaration()).getObjectId());
        } else if (fundamentalType instanceof UserDefinedType) {
            params.add(org.xtuml.masl.translate.main.DomainTranslator.getInstance(((UserDefinedType) fundamentalType).getDomain()).getDomainId());
            params.add(getTypeId(fundamentalType.getTypeDeclaration()));
        } else if (fundamentalType instanceof DictionaryType) {
            params.add(getTypeMetaData(((DictionaryType) fundamentalType).getKeyType()));
            params.add(getTypeMetaData(((DictionaryType) fundamentalType).getValueType()));
        }

        final int collectionDepth = getCollectionDepth(type);
        if (collectionDepth > 0) {
            params.add(new Literal(collectionDepth));
        }

        return typeMetaData.callConstructor(params);
    }

    static private Expression getTypeFlag(final BasicType fundamentalType) {
        switch (fundamentalType.getActualType()) {
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
            case USER_DEFINED: {
                if (fundamentalType.getDefinedType() instanceof StructureType) {
                    return structureTypeFlag;
                } else if (fundamentalType.getDefinedType() instanceof EnumerateType) {
                    return enumTypeFlag;
                } else {
                    throw new IllegalArgumentException(fundamentalType.toString());
                }
            }
            default:
                throw new IllegalArgumentException(fundamentalType.toString());
        }
    }

    static private Expression getTypeId(final TypeDeclaration type) {
        final DomainTranslator domainTranslator = DomainTranslator.getInstance(type.getDomain());
        return domainTranslator.getTypeId(type);
    }

    static Expression getLocalVarMetaData(final VariableDefinition variable) {
        final List<Expression> params = new ArrayList<Expression>();
        params.add(Literal.createStringLiteral(variable.getName()));

        params.add(Literal.createStringLiteral(variable.getType().toString()));
        params.add(TypeTranslator.getTypeMetaData(variable.getType()));

        return localVarMetaData.callConstructor(params);
    }

    static Expression getParameterMetaData(final ParameterDefinition param) {
        final List<Expression> params = new ArrayList<Expression>();
        params.add(Literal.createStringLiteral(param.getName()));

        params.add(Literal.createStringLiteral(param.getType().toString()));
        params.add(TypeTranslator.getTypeMetaData(param.getType()));
        params.add((param.getMode() == ParameterDefinition.Mode.IN) ? Literal.FALSE : Literal.TRUE);

        return parameterMetaData.callConstructor(params);
    }

}
