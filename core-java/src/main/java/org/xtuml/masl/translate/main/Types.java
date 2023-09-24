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
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.expression.RangeExpression;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.type.*;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

import java.util.*;

public class Types {

    public final static String FILE_NAME = "types";
    public final static Types instance = new Types();

    static public Types getInstance() {
        return instance;
    }

    private Types() {
    }

    private final Map<TypeDeclaration, TypeUsage> types = new LinkedHashMap<>();
    private final Map<TypeDeclaration, EnumerationTranslator> enums = new LinkedHashMap<>();
    private final Map<TypeDeclaration, Structure> structs = new LinkedHashMap<>();

    TypeUsage declareType(final TypeDeclaration declaration) {
        TypeUsage result = types.get(declaration);
        if (result != null) {
            return result;
        }

        TypeDefinition definition = declaration.getTypeDefinition();

        if (definition instanceof BasicType) {
            final TypeUsage aliasFor = getType((BasicType) definition);
            final TypedefType
                    typedef =
                    new TypedefType(Mangler.mangleName(declaration),
                                    DomainNamespace.get(declaration.getDomain()),
                                    aliasFor);
            result = new TypeUsage(typedef);
            types.put(declaration, result);

            // Put collection or dictionary definitions in with value type
            while (definition instanceof CollectionType || definition instanceof DictionaryType) {
                if (definition instanceof CollectionType) {
                    definition = ((CollectionType) definition).getContainedType();
                } else {
                    definition = ((DictionaryType) definition).getValueType();
                }
            }

            CodeFile header = getType((BasicType) definition).getType().getDeclaredIn();

            if (header == null ||
                definition.getTypeDeclaration() == null ||
                !declaration.getDomain().equals(definition.getTypeDeclaration().getDomain())) {
                header =
                        DomainTranslator.getInstance(declaration.getDomain()).getTypeHeaderFile(declaration.getVisibility());
            }
            header.addTypedefDeclaration(typedef);
        } else if (definition instanceof EnumerateType) {
            final EnumerationTranslator enumeration = new EnumerationTranslator((EnumerateType) definition);
            result = enumeration.getType();
            types.put(declaration, result);
            enums.put(declaration, enumeration);
        } else if (definition instanceof StructureType) {
            final Structure struct = new Structure(declaration);
            result = struct.getType();
            types.put(declaration, result);
            structs.put(declaration, struct);
            struct.translate();
        } else if (definition instanceof ConstrainedType) {
            // TODO add constraints and checking etc
            final BasicType fullType = ((ConstrainedType) definition).getFullType();

            final TypeUsage aliasFor = getType(fullType);
            final TypedefType
                    typedef =
                    new TypedefType(Mangler.mangleName(declaration),
                                    DomainNamespace.get(declaration.getDomain()),
                                    aliasFor);
            result = new TypeUsage(typedef);
            types.put(declaration, result);

            CodeFile header = aliasFor.getType().getDeclaredIn();

            if (header == null ||
                fullType.getTypeDeclaration() == null ||
                !declaration.getDomain().equals(fullType.getTypeDeclaration().getDomain())) {
                header =
                        DomainTranslator.getInstance(declaration.getDomain()).getTypeHeaderFile(declaration.getVisibility());
            }
            header.addTypedefDeclaration(typedef);
        } else {
            result = new TypeUsage(org.xtuml.masl.cppgen.FundamentalType.VOID, TypeUsage.Pointer);
            types.put(declaration, result);
        }
        return result;
    }

    void defineType(final TypeDeclaration declaration) {
        var struct = structs.get(declaration);
        if ( struct != null ) {
            struct.addDefinitionToHeader();
        }
    }


        /**
         * @return
         */
    public CodeFile getSourceFile(final TypeDeclaration declaration) {
        CodeFile sourceFile = null;
        final TypeDefinition definition = declaration.getTypeDefinition();

        if (definition instanceof BasicType || definition instanceof ConstrainedType) {
            // no source file defined
        } else if (definition instanceof EnumerateType) {
            final EnumerationTranslator enumTranslator = enums.get(declaration);
            sourceFile = enumTranslator.bodyFile;
        } else if (definition instanceof StructureType) {
            final Structure struct = structs.get(declaration);
            sourceFile = struct.getBodyFile();
        }
        return sourceFile;
    }

    public EnumerationTranslator getEnumerateTranslator(final TypeDeclaration type) {
        return enums.get(type);
    }

    public Structure getStructureTranslator(final TypeDeclaration type) {
        return structs.get(type);
    }

    public TypeUsage getType(final BasicType type) {
        if (type == null) {
            return new TypeUsage(org.xtuml.masl.cppgen.FundamentalType.VOID);
        } else if (type instanceof UserDefinedType) {
            return getUserDefinedType((UserDefinedType) type);
        } else if (type instanceof BuiltinType) {
            return getBuiltinType((BuiltinType) type);
        } else if (type instanceof InstanceType instance) {
            final ObjectDeclaration objDec = instance.getObjectDeclaration();
            return DomainTranslator.getInstance(instance.getObjectDeclaration().getDomain()).getObjectTranslator(objDec).getPointerType();
        } else if (type instanceof DictionaryType dictionary) {
            return new TypeUsage(Architecture.dictionary(getType(dictionary.getKeyType()),
                                                         getType(dictionary.getValueType())));
        } else if (type instanceof CollectionType collection) {
            final BasicType contained = collection.getContainedType();
            final TypeUsage cppContained = getType(contained);
            if (type instanceof SequenceType) {
                return new TypeUsage(Architecture.sequence(cppContained));
            } else if (type instanceof SetType) {
                return new TypeUsage(Architecture.set(cppContained));
            } else if (type instanceof BagType) {
                return new TypeUsage(Architecture.bag(cppContained));
            } else if (type instanceof ArrayType) {
                final RangeExpression range = ((ArrayType) type).getRange();

                final Expression min = ExpressionTranslator.createTranslator(range.getMin(), null).getReadExpression();
                final Expression max = ExpressionTranslator.createTranslator(range.getMax(), null).getReadExpression();

                return new TypeUsage(Boost.array(cppContained,
                                                 new BinaryExpression(new BinaryExpression(max,
                                                                                           BinaryOperator.MINUS,
                                                                                           min),
                                                                      BinaryOperator.PLUS,
                                                                      Literal.ONE)));

            } else {
                throw new UnsupportedOperationException("Unrecognised CollectionType " + type.getClass().getName());
            }
        } else if (type instanceof AnonymousStructure) {
            final List<TypeUsage> tupleTypes = new ArrayList<>();

            for (final BasicType element : ((AnonymousStructure) type).getElements()) {
                tupleTypes.add(Types.getInstance().getType(element));
            }

            return new BigTuple(tupleTypes).getTupleType();

        } else {
            return new TypeUsage(org.xtuml.masl.cppgen.FundamentalType.VOID, TypeUsage.Pointer);
        }
    }

    private static final Map<ActualType, TypeUsage> builtinTypes = new EnumMap<>(ActualType.class);

    static {
        builtinTypes.put(ActualType.STRING, new TypeUsage(Architecture.stringClass));
        builtinTypes.put(ActualType.INTEGER, new TypeUsage(Std.int64));
        builtinTypes.put(ActualType.SMALL_INTEGER, new TypeUsage(Std.int32));
        builtinTypes.put(ActualType.DURATION, new TypeUsage(Architecture.Duration.durationClass));
        builtinTypes.put(ActualType.TIMESTAMP, new TypeUsage(Architecture.Timestamp.timestampClass));
        builtinTypes.put(ActualType.BOOLEAN, new TypeUsage(org.xtuml.masl.cppgen.FundamentalType.BOOL));
        builtinTypes.put(ActualType.REAL, new TypeUsage(org.xtuml.masl.cppgen.FundamentalType.DOUBLE));
        builtinTypes.put(ActualType.BYTE, new TypeUsage(Std.uint8));
        builtinTypes.put(ActualType.CHARACTER, new TypeUsage(org.xtuml.masl.cppgen.FundamentalType.CHAR));
        builtinTypes.put(ActualType.DEVICE, new TypeUsage(Architecture.deviceClass));
        builtinTypes.put(ActualType.WCHARACTER, new TypeUsage(org.xtuml.masl.cppgen.FundamentalType.CHAR));
        builtinTypes.put(ActualType.WSTRING, new TypeUsage(Architecture.stringClass));
        builtinTypes.put(ActualType.EVENT, new TypeUsage(Architecture.event.getEventPtr()));
        builtinTypes.put(ActualType.TIMER, new TypeUsage(Architecture.Timer.timerHandle));
        builtinTypes.put(ActualType.ANY_INSTANCE,
                         new TypeUsage(Architecture.objectPtr(new TypeUsage(FundamentalType.VOID))));
    }

    private TypeUsage getBuiltinType(final BuiltinType type) {
        return builtinTypes.get(type.getActualType());
    }

    private TypeUsage getUserDefinedType(final UserDefinedType type) {
        return declareType(type.getTypeDeclaration());
    }

}
