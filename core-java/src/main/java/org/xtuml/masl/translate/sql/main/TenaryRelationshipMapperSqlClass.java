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
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.BigTuple;
import org.xtuml.masl.translate.main.Mangler;

import java.util.Arrays;
import java.util.List;

/**
 * The MASL language allows constructs to support associative relationships.
 * Examples of these are given below:
 * <p>
 * relationship R18 is One_To_Many_Link_Test_Object_I unconditionally slave many
 * One_To_Many_Link_Test_Object_J, One_To_Many_Link_Test_Object_J
 * unconditionally master one One_To_Many_Link_Test_Object_I using
 * One_To_Many_Link_Test_Object_K;
 * <p>
 * relationship R31 is One_To_One_Link_Test_Object_V unconditionally master one
 * One_To_One_Link_Test_Object_V, One_To_One_Link_Test_Object_V unconditionally
 * slave many One_To_One_Link_Test_Object_V using One_To_One_Link_Test_Object_W;
 * <p>
 * To be consistent when interpreting and generating the code to support these
 * kind of relationships the relationship is read from the left hand side (LHS)
 * to the right hand side (RHS). Taking the first example above (R18), this
 * would mean that the LHS object is One_To_Many_Link_Test_Object_I and the RHS
 * objectis One_To_Many_Link_Test_Object_J. The generated code makes extensive
 * use of this concept. Using this kind of interpretation the first relationship
 * (R18) would be defined as an AssocManyToOneRelationship and the second (R31)
 * would be defined as an AssocOneToManyRelationship.
 * <p>
 * As three objects take part in the relationship it is also known as a Tenary
 * relationship and this gives rise to the name for this class.
 * <p>
 * Each associative relationship will have an associated C++ Mapper class that
 * provides the logic required to support the tenary relationship class, it does
 * not access database directly. The actual interface required to support the
 * database and SQL functionality is abstracted into a MapperSQL class (which is
 * defined in the Mapper class).
 * <p>
 * The TenaryRelationshipMapperSqlClass therefore provides the required
 * functionality to generate an implementation for the MapperSQL intefrace.
 */
public class TenaryRelationshipMapperSqlClass implements GeneratedRelationshipClass {

    protected String className;
    protected Class mapperSqlClass;
    protected Namespace namespace;
    protected AssociativeRelationshipDeclaration relationshipDecl;

    protected DeclarationGroup executeGroup;
    protected DeclarationGroup attributeGroup;
    protected DeclarationGroup constructorGroup;
    protected DeclarationGroup getGroup;
    protected DeclarationGroup commitGroup;
    protected DeclarationGroup loadGroup;

    protected CodeFile bodyFile;
    protected CodeFile headerFile;
    protected RelationshipMapperClass relationshipMapper;

    Variable tableName;
    Variable relationshipName;
    Variable pepreparedLink;
    Variable pepreparedUnlink;

    PreparedStatement linkPreparedStatement;
    PreparedStatement unlinkPreparedStatement;

    DatabaseTraits databaseTraits;
    TenaryRelationshipToTableTranslator tableTranslator;

    /**
     * Constructor
     * <p>
     * <p>
     * the Mapper object that this class must generate an SQL
     * implementation for.
     * <p>
     * generated C++ Mapper Interface class that this generated MapperSQL
     * class must adhere to
     * <p>
     * the actual associative relationship
     * <p>
     * the namespace that any generated code should be placed in.
     */
    public TenaryRelationshipMapperSqlClass(final RelationshipMapperClass parent,
                                            final Class baseClass,
                                            final AssociativeRelationshipDeclaration relationship,
                                            final Namespace parentNamespace) {
        relationshipMapper = parent;
        relationshipDecl = relationship;
        databaseTraits = relationshipMapper.getDatabaseTraits();
        this.namespace = new Namespace(Mangler.mangleName(relationshipDecl.getDomain()), parentNamespace);

        className = "Relationship" + relationship.getName() + "SqlGenerator";
        mapperSqlClass = new Class(className, namespace);

        mapperSqlClass.addSuperclass(baseClass.referenceNestedType("RelSqlGeneratorType"), Visibility.PUBLIC);

        tableTranslator = databaseTraits.createTenaryRelationshipToTableTranslator(relationshipDecl);
        linkPreparedStatement = tableTranslator.createPreparedStatement(PreparedStatement.PreparedStatementType.INSERT);
        unlinkPreparedStatement =
                tableTranslator.createPreparedStatement(PreparedStatement.PreparedStatementType.DELETE);
    }

    /**
     * @return the generated C++ MApperSQl class
     */
    @Override
    public Class getSqlGenImplClass() {
        return mapperSqlClass;
    }

    public String getRelationshipName() {
        return relationshipDecl.getDomain().getName() + "_" + relationshipDecl.getName();
    }

    /**
     * @return the SQL link table name
     */
    public String getTableName() {
        return tableTranslator.getTableName();
    }

    /**
     * @return the SQL column name for the object on the left hand side (LHS) of
     * the relationship.
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
     * @return the SQL column name for the associative object (ASS) of the
     * relationship.
     */
    public String getAssocColumnName() {
        return tableTranslator.getAssocColumnName();
    }

    /**
     * Generate the MapperSQL C++ class according to the interface defined in the
     * Mapper.
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

        // Define the SQL for the link table.
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
     * Add the constructor to class and initialise all data members
     */
    protected void addConstructorMethod() {
        final Function constructor = mapperSqlClass.createConstructor(constructorGroup, Visibility.PUBLIC);
        constructor.setInitialValue(tableName, Literal.createStringLiteral(getTableName()));
        constructor.setInitialValue(relationshipName, Literal.createStringLiteral(getRelationshipName()));
        constructor.setInitialValue(pepreparedLink,
                                    Literal.createStringLiteral("INSERT INTO " +
                                                                getTableName() +
                                                                " VALUES(:1,:2,:3);"));
        constructor.setInitialValue(pepreparedUnlink,
                                    Literal.createStringLiteral("DELETE FROM " +
                                                                getTableName() +
                                                                " WHERE " +
                                                                getLeftColumnName() +
                                                                " = :1 AND " +
                                                                getRightColumnName() +
                                                                "= :2 AND " +
                                                                getAssocColumnName() +
                                                                " = :3;"));
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
        getLhsColumnName.getCode().appendStatement(new ReturnStatement(Literal.createStringLiteral(tableTranslator.getLeftColumnName())));
        bodyFile.addFunctionDefinition(getLhsColumnName);

        final Function
                getRhsColumnName =
                mapperSqlClass.createMemberFunction(getGroup, "getRhsColumnName", Visibility.PUBLIC);
        getRhsColumnName.setReturnType(new TypeUsage(Std.string, TypeUsage.Const));
        getRhsColumnName.setConst(true);
        getRhsColumnName.getCode().appendStatement(new ReturnStatement(Literal.createStringLiteral(tableTranslator.getRightColumnName())));
        bodyFile.addFunctionDefinition(getRhsColumnName);

        final Function
                getAssocColumnName =
                mapperSqlClass.createMemberFunction(getGroup, "getAssocColumnName", Visibility.PUBLIC);
        getAssocColumnName.setReturnType(new TypeUsage(Std.string, TypeUsage.Const));
        getAssocColumnName.setConst(true);
        getAssocColumnName.getCode().appendStatement(new ReturnStatement(Literal.createStringLiteral(tableTranslator.getAssocColumnName())));
        bodyFile.addFunctionDefinition(getAssocColumnName);

        final Function
                getRelationshipNameFn =
                mapperSqlClass.createMemberFunction(getGroup, "getRelationshipName", Visibility.PUBLIC);
        getRelationshipNameFn.setReturnType(new TypeUsage(Std.string, TypeUsage.ConstReference));
        getRelationshipNameFn.setConst(true);
        getRelationshipNameFn.getCode().appendStatement(new ReturnStatement(relationshipName.asExpression()));
        bodyFile.addFunctionDefinition(getRelationshipNameFn);
    }

    /**
     * Add a method to access the database to get the current number of rows in
     * the link table.
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
                commitLinkFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("LinkedTenaryType"),
                                                           TypeUsage.ConstReference), "linkObjects");
        commitLinkFn.setConst(true);
        final Function executeFn = new Function("execute");
        final Function getCheckedFn = new Function("getChecked");
        final Function getArhIdFn = new Function("getArchitectureId");

        final Expression
                getLinkFirstArhIdFnCall =
                getArhIdFn.asFunctionCall(getCheckedFn.asFunctionCall(new Function("get<0>").asFunctionCall(
                        linkObjectsVar.asExpression(),
                        false), false), true);
        final Expression
                getLinkSecondArhIdFnCall =
                getArhIdFn.asFunctionCall(getCheckedFn.asFunctionCall(new Function("get<1>").asFunctionCall(
                        linkObjectsVar.asExpression(),
                        false), false), true);
        final Expression
                getLinkThirdArhIdFnCall =
                getArhIdFn.asFunctionCall(getCheckedFn.asFunctionCall(new Function("get<2>").asFunctionCall(
                        linkObjectsVar.asExpression(),
                        false), false), true);
        final List<Expression>
                commitLinkTuple =
                BigTuple.getTupleList((Arrays.asList(getLinkFirstArhIdFnCall,
                                                     getLinkSecondArhIdFnCall,
                                                     getLinkThirdArhIdFnCall)));
        final Expression
                LinkExecuteFnCall =
                executeFn.asFunctionCall(pepreparedLink.asExpression(), false, commitLinkTuple);
        commitLinkFn.getCode().appendExpression(LinkExecuteFnCall);
        bodyFile.addFunctionDefinition(commitLinkFn);

        final Function
                commitUnlinkFn =
                mapperSqlClass.createMemberFunction(commitGroup, "commitUnlink", Visibility.PUBLIC);
        final Variable
                unlinkObjectsVar =
                commitUnlinkFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("LinkedTenaryType"),
                                                             TypeUsage.ConstReference), "unlinkObjects");
        commitUnlinkFn.setConst(true);
        final Expression
                getunLinkFirstArhIdFnCall =
                getArhIdFn.asFunctionCall(getCheckedFn.asFunctionCall(new Function("get<0>").asFunctionCall(
                        unlinkObjectsVar.asExpression(),
                        false), false), true);
        final Expression
                getunLinkSecondArhIdFnCall =
                getArhIdFn.asFunctionCall(getCheckedFn.asFunctionCall(new Function("get<1>").asFunctionCall(
                        unlinkObjectsVar.asExpression(),
                        false), false), true);
        final Expression
                getunLinkThirdArhIdFnCall =
                getArhIdFn.asFunctionCall(getCheckedFn.asFunctionCall(new Function("get<2>").asFunctionCall(
                        unlinkObjectsVar.asExpression(),
                        false), false), true);
        final List<Expression>
                commitUnlinkTuple =
                BigTuple.getTupleList((Arrays.asList(getunLinkFirstArhIdFnCall,
                                                     getunLinkSecondArhIdFnCall,
                                                     getunLinkThirdArhIdFnCall)));
        final Expression
                unLinkExecuteFnCall =
                executeFn.asFunctionCall(pepreparedUnlink.asExpression(), false, commitUnlinkTuple);
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
        addLoadAss();
    }

    /**
     * Add the loadAll method defined by the interface. This will access the
     * database to read all the link table information and serialise the data into
     * the supplied cache.
     */
    private void addLoadAll() {
        final Function loadAllFn = mapperSqlClass.createMemberFunction(loadGroup, "loadAll", Visibility.PUBLIC);
        loadAllFn.setConst(true);
        final Variable
                cachedTenaryContVar =
                loadAllFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("CachedTenaryContainerSet"),
                                                        TypeUsage.Reference), "cachedTenaryContainers");

        tableTranslator.addLoadAllBody(loadAllFn, cachedTenaryContVar);
        bodyFile.addFunctionDefinition(loadAllFn);
    }

    /**
     * Add the loadLhs method defined by the interface. This will access the
     * database to read the link table information using a where clause based on
     * the value of a rhs object id.
     */
    private void addLoadLhs() {
        final Function loadLhsFn = mapperSqlClass.createMemberFunction(loadGroup, "loadLhs", Visibility.PUBLIC);
        loadLhsFn.setConst(true);
        final Variable
                rhsIdentityVar =
                loadLhsFn.createParameter(new TypeUsage(Architecture.ID_TYPE, TypeUsage.ConstReference), "rhsIdentity");
        final Variable
                cachedTenaryContVar =
                loadLhsFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("CachedTenaryContainerSet"),
                                                        TypeUsage.Reference), "cachedTenaryContainers");

        tableTranslator.addLoadLhsBody(loadLhsFn, rhsIdentityVar, cachedTenaryContVar);
        bodyFile.addFunctionDefinition(loadLhsFn);
    }

    /**
     * Add the loadRhs method defined by the interface. This will access the
     * database to read the link table information using a where clause based on
     * the value of a lhs object id.
     */
    private void addLoadRhs() {
        final Function loadRhsFn = mapperSqlClass.createMemberFunction(loadGroup, "loadRhs", Visibility.PUBLIC);
        loadRhsFn.setConst(true);
        final Variable
                lhsIdentityVar =
                loadRhsFn.createParameter(new TypeUsage(Architecture.ID_TYPE, TypeUsage.ConstReference), "lhsIdentity");
        final Variable
                cachedTenaryContVar =
                loadRhsFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("CachedTenaryContainerSet"),
                                                        TypeUsage.Reference), "cachedTenaryContainers");

        tableTranslator.addLoadRhsBody(loadRhsFn, lhsIdentityVar, cachedTenaryContVar);
        bodyFile.addFunctionDefinition(loadRhsFn);
    }

    /**
     * Add the loadAss method defined by the interface. This will access the
     * database to read the link table information using a where clause based on
     * the value of a ass object id.
     */
    private void addLoadAss() {
        final Function loadAssFn = mapperSqlClass.createMemberFunction(loadGroup, "loadAss", Visibility.PUBLIC);
        loadAssFn.setConst(true);
        final Variable
                assIdentityVar =
                loadAssFn.createParameter(new TypeUsage(Architecture.ID_TYPE, TypeUsage.ConstReference), "assIdentity");
        final Variable
                cachedTenaryContVar =
                loadAssFn.createParameter(new TypeUsage(mapperSqlClass.referenceNestedType("CachedTenaryContainerSet"),
                                                        TypeUsage.Reference), "cachedTenaryContainers");

        tableTranslator.addLoadRhsBody(loadAssFn, assIdentityVar, cachedTenaryContVar);
        bodyFile.addFunctionDefinition(loadAssFn);
    }

}
