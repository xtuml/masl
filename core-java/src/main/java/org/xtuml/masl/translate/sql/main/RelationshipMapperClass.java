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
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.TypedefType;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.translate.main.Mangler;

import java.util.HashMap;
import java.util.Map;

/**
 * The SQL link tables used for relationships are hidden from the application by
 * a mapping layer. This class is used to generate the correct C++ relationship
 * mapping class to handle the functionality required of the relationship.
 */
public class RelationshipMapperClass {

    private final String className;
    private final Namespace namespace;

    private final DatabaseTraits databaseTraits;
    private final RelationshipTranslator relationshipTran;
    private final RelationshipDeclaration relationshipDecl;

    private TypedefType relationshipMapper = null;

    CodeFile headerFile;
    Namespace parentNamespace;

    private final Map<String, TypedefType> superToSubRelMapperList = new HashMap<String, TypedefType>();
    private final Map<String, GeneratedRelationshipClass>
            superToSubRelMapperSqlList =
            new HashMap<String, GeneratedRelationshipClass>();

    private GeneratedRelationshipClass mapperSqlClass;
    private final CodeFile sqlBodyFile;

    public CodeFile getSqlBodyFile() {
        return sqlBodyFile;
    }

    public CodeFile getSqlHeaderFile() {
        return sqlHeaderFile;
    }

    private final CodeFile sqlHeaderFile;

    /**
     * Constructor
     * <p>
     * <p>
     * the relationship specification that needs generated associated
     * Mapper and MapperSQL classes
     * <p>
     * the namespace the generated code should be placed in.
     */
    public RelationshipMapperClass(final RelationshipTranslator parent,
                                   final RelationshipDeclaration relationship,
                                   final Namespace parentNamespace) {
        relationshipTran = parent;
        relationshipDecl = relationship;
        this.parentNamespace = parentNamespace;
        this.namespace =
                new Namespace(Mangler.mangleName(relationshipTran.getFramework().getMainDomainTranslator().getDomain()),
                              parentNamespace);
        className = "Relationship" + relationshipDecl.getName() + "Mapper";
        databaseTraits = relationshipTran.getFramework().getDatabase().getDatabaseTraits();

        headerFile =
                relationshipTran.getFramework().getLibrary().createPrivateHeader("Sqlite" +
                                                                                 Mangler.mangleFile(relationshipDecl) +
                                                                                 "Mapper");
        sqlBodyFile =
                relationshipTran.getFramework().getLibrary().createBodyFile(databaseTraits.getName() +
                                                                            Mangler.mangleFile(relationshipDecl) +
                                                                            "MapperSql");
        sqlHeaderFile =
                relationshipTran.getFramework().getLibrary().createPrivateHeader(databaseTraits.getName() +
                                                                                 Mangler.mangleFile(relationshipDecl) +
                                                                                 "MapperSql");

        final RelationshipType relationshipType = RelationshipTranslator.getRelationshipType(relationshipDecl);
        switch (relationshipType) {
            case OneToOne:
            case OneToMany:
            case ManyToOne:
                final NormalRelationshipDeclaration
                        actualRelationship =
                        (NormalRelationshipDeclaration) relationshipDecl;
                generateForNormalRelationship(actualRelationship, relationshipType);
                break;

            case AssocOneToOne:
            case AssocOneToMany:
            case AssocManyToOne:
            case AssocManyToMany:
                final AssociativeRelationshipDeclaration
                        actualAssocRelationship =
                        (AssociativeRelationshipDeclaration) relationshipDecl;
                generateForAssociativeRelationship(actualAssocRelationship, relationshipType);
                break;

            case SubToSuper:
            case SuperToSub:
                final SubtypeRelationshipDeclaration
                        actualSuperSubRelationship =
                        (SubtypeRelationshipDeclaration) relationshipDecl;
                generateForSuperToSubRelationship(actualSuperSubRelationship, relationshipType);
                break;
        }
    }

    public SqlFrameworkTranslator getFramework() {
        return relationshipTran.getFramework();
    }

    public DatabaseTraits getDatabaseTraits() {
        return databaseTraits;
    }

    /**
     * @return The C++ relationship Mapper class for normal and associative
     * relationship specifications
     */
    Class getMapperClass() {
        return relationshipMapper.asClass();
    }

    /**
     * @return The C++ relationship MapperSQL class for normal and associative
     * relationship specifications
     */
    Class getMapperSqlClass() {
        return mapperSqlClass.getSqlGenImplClass();
    }

    /**
     * Multiple C++ one-to-one relationship mapper classes are generated for
     * subtype relationships. Therefore use the specified supertype and subtype
     * objects to return the required mapper class.
     * <p>
     * <p>
     * the supertype object from he subtype relationship
     * <p>
     * a subtype object from the subtype relationship
     *
     * @return The C++ relationship Mapper class for the specified subtype
     * relationship object pair.
     */
    Class getSuperToSubMapperClass(final ObjectDeclaration superObject, final ObjectDeclaration subtype) {
        final String mapperName = getSuperToSubRelClassName(relationshipDecl.getName(), superObject, subtype);
        return superToSubRelMapperList.get(mapperName).asClass();
    }

    /**
     * Multiple C++ one-to-one relationship MapperSQL classes are generated for
     * subtype relationships. Therefore use the specified supertype and subtype
     * objects to return the required MapperSQL class.
     * <p>
     * <p>
     * the supertype object from he subtype relationship
     * <p>
     * a subtype object from the subtype relationship
     *
     * @return The C++ relationship Mapper class for the specified subtype
     * relationship object pair.
     */
    Class getSuperToSubMapperSqlClass(final ObjectDeclaration superObject, final ObjectDeclaration subtype) {
        final String mapperName = getSuperToSubRelClassName(relationshipDecl.getName(), superObject, subtype);
        return superToSubRelMapperSqlList.get(mapperName).getSqlGenImplClass();
    }

    /**
     * Generate the mapper and MapperSQL classes for Normal Relationship
     * Declarations.
     * <p>
     * <p>
     * the relationship specification
     * <p>
     * enum representing the type of relationship
     */
    private void generateForNormalRelationship(final NormalRelationshipDeclaration actualRelationship,
                                               final RelationshipType relationshipType) {
        // Get the details about the left handside object
        final ObjectDeclaration leftObjectDeclaration = actualRelationship.getLeftObject();
        final ObjectTranslator
                leftObjectTranslator =
                relationshipTran.getFramework().getObjectTranslator(leftObjectDeclaration);

        // Get the details about the right handside object
        final ObjectDeclaration rightObjectDeclaration = actualRelationship.getRightObject();
        final ObjectTranslator
                rightObjectTranslator =
                relationshipTran.getFramework().getObjectTranslator(rightObjectDeclaration);
        if (relationshipType == RelationshipType.OneToOne) {
            relationshipMapper =
                    Relationship.oneToOneRelationshipMapper(RelationshipTranslator.getRelationshipNumber(
                                                                    relationshipDecl),
                                                            leftObjectTranslator.getClass("ImplementationClass"),
                                                            rightObjectTranslator.getClass("ImplementationClass"),
                                                            actualRelationship.getLeftConditional(),
                                                            actualRelationship.getRightConditional(),
                                                            className,
                                                            namespace);
            headerFile.addTypedefDeclaration(relationshipMapper);
            mapperSqlClass =
                    new BinaryRelationshipMapperSqlClass(this,
                                                         relationshipMapper.asClass(),
                                                         actualRelationship,
                                                         parentNamespace);
        } else if (relationshipType == RelationshipType.OneToMany) {
            relationshipMapper =
                    Relationship.oneToManyRelationshipMapper(RelationshipTranslator.getRelationshipNumber(
                                                                     relationshipDecl),
                                                             leftObjectTranslator.getClass("ImplementationClass"),
                                                             rightObjectTranslator.getClass("ImplementationClass"),
                                                             actualRelationship.getLeftConditional(),
                                                             actualRelationship.getRightConditional(),
                                                             className,
                                                             namespace);
            headerFile.addTypedefDeclaration(relationshipMapper);
            mapperSqlClass =
                    new BinaryRelationshipMapperSqlClass(this,
                                                         relationshipMapper.asClass(),
                                                         actualRelationship,
                                                         parentNamespace);
        } else if (relationshipType == RelationshipType.ManyToOne) {
            relationshipMapper =
                    Relationship.ManyToOneRelationshipMapper(RelationshipTranslator.getRelationshipNumber(
                                                                     relationshipDecl),
                                                             leftObjectTranslator.getClass("ImplementationClass"),
                                                             rightObjectTranslator.getClass("ImplementationClass"),
                                                             actualRelationship.getLeftConditional(),
                                                             actualRelationship.getRightConditional(),
                                                             className,
                                                             namespace);
            headerFile.addTypedefDeclaration(relationshipMapper);
            mapperSqlClass =
                    new BinaryRelationshipMapperSqlClass(this,
                                                         relationshipMapper.asClass(),
                                                         actualRelationship,
                                                         parentNamespace);
        }
        mapperSqlClass.initialise();
    }

    /**
     * Generate the mapper and MapperSQL classes for Normal Relationship
     * Declarations.
     * <p>
     * <p>
     * the associative relationship specification
     * <p>
     * enum representing the type of relationship
     */
    private void generateForAssociativeRelationship(final AssociativeRelationshipDeclaration actualAssocRelationship,
                                                    final RelationshipType relationshipType) {
        final ObjectDeclaration leftAssocObjectDecl = actualAssocRelationship.getLeftObject();
        final ObjectDeclaration rightAssocObjectDecl = actualAssocRelationship.getRightObject();
        final ObjectDeclaration assocObjectDecl = actualAssocRelationship.getAssocObject();

        final ObjectTranslator
                leftAssocObjTranslator =
                relationshipTran.getFramework().getObjectTranslator(leftAssocObjectDecl);
        final ObjectTranslator
                rightAssocObjTranslator =
                relationshipTran.getFramework().getObjectTranslator(rightAssocObjectDecl);
        final ObjectTranslator
                assocObjTranslator =
                relationshipTran.getFramework().getObjectTranslator(assocObjectDecl);

        if (relationshipType == RelationshipType.AssocOneToOne) {
            relationshipMapper =
                    Relationship.assocOneToOneRelationshipMapper(RelationshipTranslator.getRelationshipNumber(
                                                                         relationshipDecl),
                                                                 leftAssocObjTranslator.getClass("ImplementationClass"),
                                                                 rightAssocObjTranslator.getClass("ImplementationClass"),
                                                                 assocObjTranslator.getClass("ImplementationClass"),
                                                                 actualAssocRelationship.getLeftConditional(),
                                                                 actualAssocRelationship.getRightConditional(),
                                                                 className,
                                                                 namespace);
            headerFile.addTypedefDeclaration(relationshipMapper);
            mapperSqlClass =
                    new TenaryRelationshipMapperSqlClass(this,
                                                         relationshipMapper.asClass(),
                                                         actualAssocRelationship,
                                                         parentNamespace);
        } else if (relationshipType == RelationshipType.AssocOneToMany) {
            relationshipMapper =
                    Relationship.assocOneToManyRelationshipMapper(RelationshipTranslator.getRelationshipNumber(
                                                                          relationshipDecl),
                                                                  leftAssocObjTranslator.getClass("ImplementationClass"),
                                                                  rightAssocObjTranslator.getClass("ImplementationClass"),
                                                                  assocObjTranslator.getClass("ImplementationClass"),
                                                                  actualAssocRelationship.getLeftConditional(),
                                                                  actualAssocRelationship.getRightConditional(),
                                                                  className,
                                                                  namespace);
            headerFile.addTypedefDeclaration(relationshipMapper);
            mapperSqlClass =
                    new TenaryRelationshipMapperSqlClass(this,
                                                         relationshipMapper.asClass(),
                                                         actualAssocRelationship,
                                                         parentNamespace);
        } else if (relationshipType == RelationshipType.AssocManyToOne) {
            relationshipMapper =
                    Relationship.assocManyToOneRelationshipMapper(RelationshipTranslator.getRelationshipNumber(
                                                                          relationshipDecl),
                                                                  leftAssocObjTranslator.getClass("ImplementationClass"),
                                                                  rightAssocObjTranslator.getClass("ImplementationClass"),
                                                                  assocObjTranslator.getClass("ImplementationClass"),
                                                                  actualAssocRelationship.getLeftConditional(),
                                                                  actualAssocRelationship.getRightConditional(),
                                                                  className,
                                                                  namespace);
            headerFile.addTypedefDeclaration(relationshipMapper);
            mapperSqlClass =
                    new TenaryRelationshipMapperSqlClass(this,
                                                         relationshipMapper.asClass(),
                                                         actualAssocRelationship,
                                                         parentNamespace);
        } else if (relationshipType == RelationshipType.AssocManyToMany) {
            relationshipMapper =
                    Relationship.assocManyToManyRelationshipMapper(RelationshipTranslator.getRelationshipNumber(
                                                                           relationshipDecl),
                                                                   leftAssocObjTranslator.getClass("ImplementationClass"),
                                                                   rightAssocObjTranslator.getClass(
                                                                           "ImplementationClass"),
                                                                   assocObjTranslator.getClass("ImplementationClass"),
                                                                   actualAssocRelationship.getLeftConditional(),
                                                                   actualAssocRelationship.getRightConditional(),
                                                                   className,
                                                                   namespace);
            headerFile.addTypedefDeclaration(relationshipMapper);
            mapperSqlClass =
                    new TenaryRelationshipMapperSqlClass(this,
                                                         relationshipMapper.asClass(),
                                                         actualAssocRelationship,
                                                         parentNamespace);
        }
        mapperSqlClass.initialise();
    }

    /**
     * Generate the mapper and MapperSQL classes for subtype Relationship
     * Declarations.
     * <p>
     * <p>
     * the subtype relationship specification
     * <p>
     * enum representing the type of relationship
     */
    private void generateForSuperToSubRelationship(final SubtypeRelationshipDeclaration actualSuperSubRelationship,
                                                   final RelationshipType relationshipType) {
        final ObjectDeclaration superTypeObjDecl = actualSuperSubRelationship.getSupertype();
        final ObjectTranslator
                superObjTranslator =
                relationshipTran.getFramework().getObjectTranslator(actualSuperSubRelationship.getSupertype());
        for (final ObjectDeclaration derivedObjDecl : actualSuperSubRelationship.getSubtypes()) {
            final ObjectTranslator
                    derivedObjTranslator =
                    relationshipTran.getFramework().getObjectTranslator(derivedObjDecl);
            final String
                    relClassName =
                    getSuperToSubRelClassName(actualSuperSubRelationship.getName(), superTypeObjDecl, derivedObjDecl);
            final TypedefType
                    relationshipMapper =
                    Relationship.oneToOneRelationshipMapper(RelationshipTranslator.getRelationshipNumber(
                                                                    relationshipDecl),
                                                            superObjTranslator.getClass("ImplementationClass"),
                                                            derivedObjTranslator.getClass("ImplementationClass"),
                                                            true,
                                                            true,
                                                            relClassName,
                                                            namespace);
            headerFile.addTypedefDeclaration(relationshipMapper);
            final SubTypeRelationshipMapperSqlClass
                    subtypeMapperSql =
                    new SubTypeRelationshipMapperSqlClass(this,
                                                          relationshipMapper.asClass(),
                                                          actualSuperSubRelationship,
                                                          derivedObjDecl,
                                                          parentNamespace);
            subtypeMapperSql.initialise();
            superToSubRelMapperList.put(relClassName, relationshipMapper);
            superToSubRelMapperSqlList.put(relClassName, subtypeMapperSql);
        }
    }

    /**
     * A subtype relationship declaration will cause the generation of multiple
     * one-to-one relationship mapper classes. As the normal mapper name is not
     * enough to identify each generated subtype relationship mapper class,
     * incorporate the supertype and dervied object names into the class name.
     * <p>
     * <p>
     * The name of the relationship (i.e. R4, R17 ...)
     * <p>
     * the supertype object
     * <p>
     * a dervied object
     *
     * @return a unique relationship mapper class name for the specified supertype
     * and subtype object pair.
     */
    static public String getSuperToSubRelClassName(final String relationshipName,
                                                   final ObjectDeclaration superObjDecl,
                                                   final ObjectDeclaration subtypeObjDecl) {
        return "Relationship" +
               relationshipName +
               "_" +
               superObjDecl.getName() +
               "_" +
               subtypeObjDecl.getName() +
               "Mapper";
    }
}
