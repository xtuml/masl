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
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.BigTuple;
import org.xtuml.masl.translate.main.Mangler;

import java.util.Arrays;

/**
 * Each of the generated C++ Mapper classes used to provide the behaviour for
 * binary relationships (i.e. one-to-one, one-to-many relationships) does not
 * directly interact with the RDBMS database. This enables a seperation to be
 * maintained between the logic required to drive the relationship mapper and
 * the implementation of the actual database access. Each C++ Mapper class
 * therefore defines a MapperSQL interface class which must be implemented to
 * support the relationship mapper class.
 * <p>
 * This BinaryRelationshipMapperSqlClass class therefore takes the relationship
 * declaration and generates a C++ implementation class that adheres to the
 * MapperSQl interface of the relationship and implements all the required
 * databse logic and SQL constructs.
 */
public class BinaryRelationshipMapperSqlClass implements GeneratedRelationshipClass {

    protected String className;
    protected Class mapperSqlClass;
    protected Namespace namespace;
    protected RelationshipDeclaration relationshipDecl;

    protected DeclarationGroup executeGroup;
    protected DeclarationGroup attributeGroup;
    protected DeclarationGroup constructorGroup;
    protected DeclarationGroup getGroup;
    protected DeclarationGroup commitGroup;
    protected DeclarationGroup loadGroup;

    protected CodeFile bodyFile;
    protected CodeFile headerFile;
    protected RelationshipMapperClass relationshipMapper;

    protected Variable tableName;
    protected Variable relationshipName;
    protected Variable pepreparedLink;
    protected Variable pepreparedUnlink;

    protected PreparedStatement linkPreparedStatement;
    protected PreparedStatement unlinkPreparedStatement;

    DatabaseTraits databaseTraits;
    private BinaryRelationshipToTableTranslator tableTranslator;

    /**
     * Constructor - To be used when directly constructing an object of this class.
     * <p>
     * <p>
     * the relationship Mapper class that this MapperSQL class has been constructed
     * to support.
     * <p>
     * the C++ MapperSQL interface class of the supported relationship Mapper Class
     * <p>
     * the actual relationship declaration
     * <p>
     * the namespace any generated code should be placed in.
     */
    public BinaryRelationshipMapperSqlClass(final RelationshipMapperClass parent,
                                            final Class baseClass,
                                            final NormalRelationshipDeclaration relationship,
                                            final Namespace parentNamespace) {
        relationshipMapper = parent;
        relationshipDecl = relationship;
        databaseTraits = relationshipMapper.getDatabaseTraits();
        this.namespace = new Namespace(Mangler.mangleName(relationshipDecl.getDomain()), parentNamespace);

        className = "Relationship" + relationship.getName() + "SqlGenerator";
        mapperSqlClass = new Class(className, namespace);
        mapperSqlClass.addSuperclass(baseClass.referenceNestedType("RelSqlGeneratorType"), Visibility.PUBLIC);

        tableTranslator = databaseTraits.createBinaryRelationshipToTableTranslator(relationship);
        linkPreparedStatement = tableTranslator.createPreparedStatement(PreparedStatement.PreparedStatementType.INSERT);
        unlinkPreparedStatement =
                tableTranslator.createPreparedStatement(PreparedStatement.PreparedStatementType.DELETE);
    }

    /**
     * Constructor - To be used by dervied class (@see
     * SubTypeRelationshipMapperSqlClass).
     * <p>
     * <p>
     * the relationship Mapper class that this MapperSQL class has been constructed
     * to support.
     * <p>
     * the C++ MapperSQL interface class of the supported relationship Mapper Class
     * <p>
     * the name to be used for the generated C++ MapperSQL class
     * <p>
     * the actual relationship declaration
     * <p>
     * the namespace any generated code should be placed in.
     */
    public BinaryRelationshipMapperSqlClass(final RelationshipMapperClass parent,
                                            final Class baseClass,
                                            final String className,
                                            final SubtypeRelationshipDeclaration relationship,
                                            final Namespace parentNamespace) {
        relationshipMapper = parent;
        relationshipDecl = relationship;
        databaseTraits = relationshipMapper.getDatabaseTraits();
        this.namespace = new Namespace(Mangler.mangleName(relationshipDecl.getDomain()), parentNamespace);

        this.className = className;
        mapperSqlClass = new Class(className, namespace);
        mapperSqlClass.addSuperclass(baseClass.referenceNestedType("RelSqlGeneratorType"), Visibility.PUBLIC);
    }

    /**
     * @return the generated C++ MApperSQl class
     */
    @Override
    public Class getSqlGenImplClass() {
        return mapperSqlClass;
    }

    /**
     * @return the SQL link table name
     */
    public String getTableName() {
        return tableTranslator.getTableName();
    }

    /**
     * @return the unique relationship name
     */
    public String getRelationshipName() {
        return relationshipDecl.getDomain().getName() + "_" + relationshipDecl.getName();
    }

    /**
     * @return the SQL column name for the object on the left hand side (LHS) of the
     * relationship.
     */
    public String getLeftColumnName() {
        return tableTranslator.getLeftColumnName();
    }

    /**
     * @return the SQL column name for the object on the right hand side (RHS) of
     * the relationship.
     */
    public String getRightColumnName() {
        return tableTranslator.getRightColumnName();
    }

    /**
     * Generate the MapperSQL C++ class
     */
    @Override
    public void initialise() {
        bodyFile = relationshipMapper.getSqlBodyFile();
        headerFile = relationshipMapper.getSqlHeaderFile();
        headerFile.addClassDeclaration(mapperSqlClass);

        constructorGroup = mapperSqlClass.createDeclarationGroup("Constructor / Destructor");
        getGroup = mapperSqlClass.createDeclarationGroup("Getter methods");
        executeGroup = mapperSqlClass.createDeclarationGroup("execute");
        commitGroup = mapperSqlClass.createDeclarationGroup("Commit methods ");
        loadGroup = mapperSqlClass.createDeclarationGroup("Load methods");
        attributeGroup = mapperSqlClass.createDeclarationGroup("Attributes");

        addDatabaseTable();
        addDataMembers();
        addConstructorMethod();
        addInitialiseMethod();
        addGetMethods();
        addExecuteMethods();
        addCommitMethods();
        addLoadMethods();
    }

    /**
     * Generate a create table statement in the MapperSQL file and get it to add
     * itself into the global schema class.
     */
    protected void addDatabaseTable() {
        final Namespace databaseAnon = new Namespace("");

        // Create cpp line:
        // const std::string createTableStatment()
        final Function createTableFn = new Function("createTableStatment", databaseAnon);
        createTableFn.setReturnType(new TypeUsage(Std.string, TypeUsage.Const));
        bodyFile.addFunctionDefinition(createTableFn);

        final String createTableStatement = tableTranslator.getCreateTableStatement();
        final Expression createTableLiteral = Literal.createStringLiteral(createTableStatement);
        createTableFn.getCode().appendStatement(new ReturnStatement(createTableLiteral));

        // Create cpp line:
        // bool registerSchema =
        // Schema::singleton().registerTable(tableNameText,createTableStatment());
        final Class schemaClass = Database.schemaClass;
        final FunctionCall singletonFn = schemaClass.callStaticFunction("singleton");
        final Function registerTableFn = new Function("registerTable");
        final Expression
                registerTableExpr =
                registerTableFn.asFunctionCall(singletonFn,
                                               false,
                                               Literal.createStringLiteral(getTableName()),
                                               createTableFn.asFunctionCall());

        final Variable
                registerSchemaVar =
                new Variable(new TypeUsage(FundamentalType.BOOL), "registerSchema", databaseAnon, registerTableExpr);
        bodyFile.addVariableDefinition(registerSchemaVar);
    }

    /**
     * Add the data members to the generated class. This includes two prepared
     * statement classes as the binary interface to the database is being used for
     * the link and unlink operations.
     */
    protected void addDataMembers() {
        tableName =
                mapperSqlClass.createMemberVariable(attributeGroup,
                                                    "tableName",
                                                    new TypeUsage(Std.string),
                                                    Visibility.PRIVATE);
        relationshipName =
                mapperSqlClass.createMemberVariable(attributeGroup,
                                                    "relationshipName",
                                                    new TypeUsage(Std.string),
                                                    Visibility.PRIVATE);

        pepreparedLink =
                mapperSqlClass.createMemberVariable(attributeGroup,
                                                    "pepreparedLink",
                                                    new TypeUsage(linkPreparedStatement.getClassType()),
                                                    Visibility.PRIVATE);
        pepreparedUnlink =
                mapperSqlClass.createMemberVariable(attributeGroup,
                                                    "pepreparedUnlink",
                                                    new TypeUsage(unlinkPreparedStatement.getClassType()),
                                                    Visibility.PRIVATE);
    }

    /**
     * Add the constructor to class
     */
    protected void addConstructorMethod() {
        final Function constructor = mapperSqlClass.createConstructor(constructorGroup, Visibility.PUBLIC);
        constructor.setInitialValue(tableName, Literal.createStringLiteral(getTableName()));
        constructor.setInitialValue(relationshipName, Literal.createStringLiteral(getRelationshipName()));
        constructor.setInitialValue(pepreparedLink, Literal.createStringLiteral(linkPreparedStatement.getStatement()));
        constructor.setInitialValue(pepreparedUnlink,
                                    Literal.createStringLiteral(unlinkPreparedStatement.getStatement()));
        bodyFile.addFunctionDefinition(constructor);
    }

    /**
     * Generate the initialise method.
     */
    protected void addInitialiseMethod() {
        final Function initFn = mapperSqlClass.createMemberFunction(constructorGroup, "initialise", Visibility.PUBLIC);

        initFn.getCode().appendExpression(linkPreparedStatement.prepare(pepreparedLink.asExpression()));
        initFn.getCode().appendExpression(unlinkPreparedStatement.prepare(pepreparedUnlink.asExpression()));

        bodyFile.addFunctionDefinition(initFn);
    }

    /**
     * Add any required getter methods
     */
    protected void addGetMethods() {
        final Function
                getTableNameFn =
                mapperSqlClass.createMemberFunction(getGroup, "getTableName", Visibility.PUBLIC);
        getTableNameFn.setReturnType(new TypeUsage(Std.string, TypeUsage.ConstReference));
        getTableNameFn.setConst(true);
        getTableNameFn.getCode().appendStatement(new ReturnStatement(tableName.asExpression()));
        bodyFile.addFunctionDefinition(getTableNameFn);

        final Function
                getDomainNameFn =
                mapperSqlClass.createMemberFunction(getGroup, "getDomainName", Visibility.PUBLIC);
        getDomainNameFn.setReturnType(new TypeUsage(Std.string, TypeUsage.Const));
        getDomainNameFn.setConst(true);
        getDomainNameFn.getCode().appendStatement(new ReturnStatement(Literal.createStringLiteral(relationshipDecl.getDomain().getName())));
        bodyFile.addFunctionDefinition(getDomainNameFn);

        final Function
                getLhsColumnName =
                mapperSqlClass.createMemberFunction(getGroup, "getLhsColumnName", Visibility.PUBLIC);
        getLhsColumnName.setReturnType(new TypeUsage(Std.string, TypeUsage.Const));
        getLhsColumnName.setConst(true);
        getLhsColumnName.getCode().appendStatement(new ReturnStatement(Literal.createStringLiteral(getLeftColumnName())));
        bodyFile.addFunctionDefinition(getLhsColumnName);

        final Function
                getRhsColumnName =
                mapperSqlClass.createMemberFunction(getGroup, "getRhsColumnName", Visibility.PUBLIC);
        getRhsColumnName.setReturnType(new TypeUsage(Std.string, TypeUsage.Const));
        getRhsColumnName.setConst(true);
        getRhsColumnName.getCode().appendStatement(new ReturnStatement(Literal.createStringLiteral(getRightColumnName())));
        bodyFile.addFunctionDefinition(getRhsColumnName);

        final Function
                getRelationshipNameFn =
                mapperSqlClass.createMemberFunction(getGroup, "getRelationshipName", Visibility.PUBLIC);
        getRelationshipNameFn.setReturnType(new TypeUsage(Std.string, TypeUsage.ConstReference));
        getRelationshipNameFn.setConst(true);
        getRelationshipNameFn.getCode().appendStatement(new ReturnStatement(relationshipName.asExpression()));
        bodyFile.addFunctionDefinition(getRelationshipNameFn);
    }

    /**
     * Add a method to access the database to get the current number of rows in the
     * link table.
     */
    protected void addExecuteMethods() {
        final Function
                executeGetRowCountFn =
                mapperSqlClass.createMemberFunction(executeGroup, "executeGetRowCount", Visibility.PUBLIC);
        executeGetRowCountFn.setReturnType(new TypeUsage(Architecture.ID_TYPE));
        executeGetRowCountFn.setConst(true);

        tableTranslator.createRowCountQuery(mapperSqlClass.getName(), executeGetRowCountFn);
        bodyFile.addFunctionDefinition(executeGetRowCountFn);
    }

    /**
     * Add the commit method
     */
    protected void addCommitMethods() {
        final Function commitLinkFn = mapperSqlClass.createMemberFunction(commitGroup, "commitLink", Visibility.PUBLIC);
        final Variable
                linkObjectsVar =
                commitLinkFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("LinkedPairType"),
                                                           TypeUsage.ConstReference), "linkObjects");
        commitLinkFn.setConst(true);
        final Function executeFn = new Function("execute");
        final Function getCheckedFn = new Function("getChecked");
        final Function getArhIdFn = new Function("getArchitectureId");

        final Expression
                getLinkFirstArhIdFnCall =
                getArhIdFn.asFunctionCall(getCheckedFn.asFunctionCall(new Variable("first").asMemberReference(
                        linkObjectsVar.asExpression(),
                        false), false), true);
        final Expression
                getLinkSecondArhIdFnCall =
                getArhIdFn.asFunctionCall(getCheckedFn.asFunctionCall(new Variable("second").asMemberReference(
                        linkObjectsVar.asExpression(),
                        false), false), true);
        final Expression
                LinkExecuteFnCall =
                executeFn.asFunctionCall(pepreparedLink.asExpression(),
                                         false,
                                         BigTuple.getTupleList((Arrays.asList(getLinkFirstArhIdFnCall,
                                                                              getLinkSecondArhIdFnCall))));
        commitLinkFn.getCode().appendExpression(LinkExecuteFnCall);
        bodyFile.addFunctionDefinition(commitLinkFn);

        final Function
                commitUnlinkFn =
                mapperSqlClass.createMemberFunction(commitGroup, "commitUnlink", Visibility.PUBLIC);
        final Variable
                unlinkObjectsVar =
                commitUnlinkFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("LinkedPairType"),
                                                             TypeUsage.ConstReference), "unlinkObjects");
        commitUnlinkFn.setConst(true);
        final Expression
                getunLinkFirstArhIdFnCall =
                getArhIdFn.asFunctionCall(getCheckedFn.asFunctionCall(new Variable("first").asMemberReference(
                        unlinkObjectsVar.asExpression(),
                        false), false), true);
        final Expression
                getunLinkSecondArhIdFnCall =
                getArhIdFn.asFunctionCall(getCheckedFn.asFunctionCall(new Variable("second").asMemberReference(
                        unlinkObjectsVar.asExpression(),
                        false), false), true);
        final Expression
                unLinkExecuteFnCall =
                executeFn.asFunctionCall(pepreparedUnlink.asExpression(),
                                         false,
                                         BigTuple.getTupleList((Arrays.asList(getunLinkFirstArhIdFnCall,
                                                                              getunLinkSecondArhIdFnCall))));
        commitUnlinkFn.getCode().appendExpression(unLinkExecuteFnCall);
        bodyFile.addFunctionDefinition(commitUnlinkFn);
    }

    /**
     * Add the load methods
     */
    private void addLoadMethods() {
        addLoadAll();
        addLoadLhs();
        addLoadRhs();
    }

    /**
     * Add the loadAll method defined by the interface. This will access the
     * database to read all the link table information serialise the data into the
     * supplied cache.
     */
    protected void addLoadAll() {
        final Function loadAllFn = mapperSqlClass.createMemberFunction(loadGroup, "loadAll", Visibility.PUBLIC);
        loadAllFn.setConst(true);
        final Variable
                lhsToRhsLinkSet =
                loadAllFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("LhsToRhsContainerType"),
                                                        TypeUsage.Reference), "lhsToRhsLinkSet");
        final Variable
                rhsToLhsLinkSet =
                loadAllFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("RhsToLhsContainerType"),
                                                        TypeUsage.Reference), "rhsToLhsLinkSet");

        tableTranslator.addLoadAllBody(loadAllFn, lhsToRhsLinkSet, rhsToLhsLinkSet);
        bodyFile.addFunctionDefinition(loadAllFn);
    }

    /**
     * Add the loadRhs method defined by the interface. This will access the
     * database to read the link table information using a where clause based on the
     * value of a lhs object id.
     */
    protected void addLoadRhs() {
        final Function loadRhsFn = mapperSqlClass.createMemberFunction(loadGroup, "loadRhs", Visibility.PUBLIC);
        loadRhsFn.setConst(true);
        final Variable
                identityVar =
                loadRhsFn.createParameter(new TypeUsage(Architecture.ID_TYPE, TypeUsage.ConstReference), "lhsIdentity");
        final Variable
                lhsToRhsLinkSet =
                loadRhsFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("LhsToRhsContainerType"),
                                                        TypeUsage.Reference), "lhsToRhsLinkSet");
        final Variable
                rhsToLhsLinkSet =
                loadRhsFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("RhsToLhsContainerType"),
                                                        TypeUsage.Reference), "rhsToLhsLinkSet");

        tableTranslator.addLoadRhsBody(loadRhsFn, identityVar, lhsToRhsLinkSet, rhsToLhsLinkSet);
        bodyFile.addFunctionDefinition(loadRhsFn);
    }

    /**
     * Add the loadLhs method defined by the interface. This will access the
     * database to read the link table information using a where clause based on the
     * value of a rhs object id.
     */
    protected void addLoadLhs() {
        final Function loadLhsFn = mapperSqlClass.createMemberFunction(loadGroup, "loadLhs", Visibility.PUBLIC);
        loadLhsFn.setConst(true);
        final Variable
                identityVar =
                loadLhsFn.createParameter(new TypeUsage(Architecture.ID_TYPE, TypeUsage.ConstReference), "rhsIdentity");
        final Variable
                lhsToRhsLinkSet =
                loadLhsFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("LhsToRhsContainerType"),
                                                        TypeUsage.Reference), "lhsToRhsLinkSet");
        final Variable
                rhsToLhsLinkSet =
                loadLhsFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("RhsToLhsContainerType"),
                                                        TypeUsage.Reference), "rhsToLhsLinkSet");

        tableTranslator.addLoadLhsBody(loadLhsFn, identityVar, lhsToRhsLinkSet, rhsToLhsLinkSet);
        bodyFile.addFunctionDefinition(loadLhsFn);
    }

}
