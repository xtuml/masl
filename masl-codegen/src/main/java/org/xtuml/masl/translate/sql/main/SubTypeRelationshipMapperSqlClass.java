/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.translate.main.Architecture;

import java.util.ArrayList;
import java.util.List;

/**
 * The MASL language allows constructs to support generalisations
 * (supertype/subtype relationships). An example of this is given below:
 * relationship R29 is Test_Object_E is_a (Object_H, Object_I, Object_J,
 * Object_K);
 * <p>
 * This defines a supertype object Test_Object_E with several derived objects
 * that are linked using the relationship R29.
 * <p>
 * To be consistent when interpreting and generating the code to support these
 * kind of relationships the relationship is read from the superType (LHS) to
 * the baseType (RHS).
 * <p>
 * The relationships between the base type and its generalisations can be
 * simulated as multiple one-to-one relationships; a one-to-one relationship
 * mapper for each supertype derived type pair. The relationship above could
 * therefore be implemneted using something like the relationship mapper
 * definitions below.
 * <p>
 * OneToOneRelationship<Test_Object_E,Object_H,true,true>
 * R29_Test_Object_E_Object_HMapper;
 * OneToOneRelationship<Test_Object_E,Object_I,true,true>
 * R29_Test_Object_E_Object_IMapper;
 * OneToOneRelationship<Test_Object_E,Object_J,true,true>
 * R29_Test_Object_E_Object_JMapper;
 * OneToOneRelationship<Test_Object_E,Object_K,true,true>
 * R29_Test_Object_E_Object_KMapper;
 * <p>
 * Usually each one-to-one relations would have its own supporting SQL LINK
 * table. But for generalisation relationships only a single link table is
 * required, with three columns rather than the usual two. The additional column
 * is a type field to give information on the kind of subtype a row in the table
 * relates to.
 * <p>
 * This class therefore extends the BinaryRelationshipMapperSqlClass to override
 * its behaviour to provide a one-to-one mapper implementation for each
 * base/derived type pair.
 */
public class SubTypeRelationshipMapperSqlClass extends BinaryRelationshipMapperSqlClass {

    /**
     * Keep a list of all the subtype relationship declarations that have been
     * actioned so that only one SQL CREATE TABLE statement is created for each
     * specification.
     */
    private static final List<SubtypeRelationshipDeclaration> createTableGeneratedList = new ArrayList<>();

    private final ObjectDeclaration derivedObject;
    private final SubtypeRelationshipDeclaration subtypeRelDecl;
    private int typeColumnValue;

    private final SubTypeRelationshipToTableTranslator subtypeTableTranslator;

    /**
     * Constructor
     * <p>
     * <p>
     * the main parent mapper class.
     * <p>
     * define the base MapperSQL class that this generated class must inherit from .
     * <p>
     * the specification for the relationship.
     * <p>
     * a derived object that participates in the relationship declaration.
     * <p>
     * the namespace any generated code needs to be placed in.
     */
    SubTypeRelationshipMapperSqlClass(final RelationshipMapperClass parent,
                                      final Class baseClass,
                                      final SubtypeRelationshipDeclaration relationshipDecl,
                                      final ObjectDeclaration derivedObject,
                                      final Namespace parentNamespace) {
        super(parent,
              baseClass,
              getSqlGeneratorClassName(relationshipDecl, derivedObject),
              relationshipDecl,
              parentNamespace);
        this.derivedObject = derivedObject;
        this.subtypeRelDecl = relationshipDecl;
        setTypeColumnValue();

        subtypeTableTranslator =
                databaseTraits.createSubTypeRelationshipToTableTranslator(relationshipDecl, derivedObject);
        linkPreparedStatement =
                subtypeTableTranslator.createPreparedStatement(PreparedStatement.PreparedStatementType.INSERT);
        unlinkPreparedStatement =
                subtypeTableTranslator.createPreparedStatement(PreparedStatement.PreparedStatementType.DELETE);

    }

    /**
     * the specification for the relationship.
     * <p>
     * a derived object that participates in the relationship declaration.
     *
     * @return the name of the one-to-one relationship mapper for the current pair
     * of particupating objects.
     */
    static public String getSqlGeneratorClassName(final SubtypeRelationshipDeclaration relationshipDecl,
                                                  final ObjectDeclaration derivedObject) {
        return "Relationship" +
               relationshipDecl.getName() +
               relationshipDecl.getSupertype().getName() +
               "_" +
               derivedObject.getName() +
               "SqlGenerator";
    }

    @Override
    public String getTableName() {
        return subtypeTableTranslator.getTableName();
    }

    /**
     * @return the SQL column name for the object on the left hand side (LHS) of the
     * relationship.
     */
    @Override
    public String getLeftColumnName() {
        return subtypeTableTranslator.getLeftColumnName();
    }

    /**
     * @return the SQL column name for the object on the right hand side (RHS) of
     * the relationship.
     */
    @Override
    public String getRightColumnName() {
        return subtypeTableTranslator.getRightColumnName();
    }

    /**
     * @return the table column name for the derived object type field.
     */
    public String getTypeColumnName() {
        return subtypeTableTranslator.getTypeColumnName();
    }

    /**
     * Override the BinaryRelationshipMapperSqlClass so that a three column link
     * table can be created, rather than the normal two fielded implementation. Also
     * make sure the link table is only added to the generated body once.
     */
    @Override
    protected void addDatabaseTable() {
        // For a supertype subtype relationship. An instance of this class is
        // created
        // for each derived type that takes part in the generalisation. This enables
        // a one-to-one relationship mapper class to be created to manage the links
        // between
        // the base class and a specific derived class. Even though each dervied
        // class
        // will use its own relationship mapper class, the SQL table used by these
        // classes
        // will be the same. Therefore only allow the create table statement to be
        // generated once.

        if (!createTableGeneratedList.contains(subtypeRelDecl)) {
            final Namespace databaseAnon = new Namespace("");

            // Create cpp line:
            // const std::string createTableStatment()
            final Function createTableFn = new Function("createTableStatment", databaseAnon);
            createTableFn.setReturnType(new TypeUsage(Std.string, TypeUsage.Const));
            bodyFile.addFunctionDefinition(createTableFn);

            final String createTableStatement = subtypeTableTranslator.getCreateTableStatement();
            final Expression createTableLiteral = Literal.createStringLiteral(createTableStatement);
            createTableFn.getCode().appendStatement(new ReturnStatement(createTableLiteral));

            // Create cpp line:
            // bool registerSchema =
            // SQLITE::Schema::singleton().registerTable(tableNameText,createTableStatment());
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
                    new Variable(new TypeUsage(FundamentalType.BOOL),
                                 "registerSchema",
                                 databaseAnon,
                                 registerTableExpr);
            bodyFile.addVariableDefinition(registerSchemaVar);
            createTableGeneratedList.add(subtypeRelDecl);
        }
    }

    /**
     * Add a method to access the database to get the current number of rows in the
     * link table.
     */
    @Override
    protected void addExecuteMethods() {
        final Function
                executeGetRowCountFn =
                mapperSqlClass.createMemberFunction(executeGroup, "executeGetRowCount", Visibility.PUBLIC);
        executeGetRowCountFn.setReturnType(new TypeUsage(Architecture.ID_TYPE));
        executeGetRowCountFn.setConst(true);

        subtypeTableTranslator.createRowCountQuery(mapperSqlClass.getName(), executeGetRowCountFn);
        bodyFile.addFunctionDefinition(executeGetRowCountFn);
    }

    /**
     * Add the loadAll method defined by the interface. This will access the
     * database to read all the link table information serialise the data into the
     * supplied cache.
     */
    @Override
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

        subtypeTableTranslator.addLoadAllBody(loadAllFn, lhsToRhsLinkSet, rhsToLhsLinkSet);
        bodyFile.addFunctionDefinition(loadAllFn);
    }

    /**
     * Add the loadRhs method defined by the interface. This will access the
     * database to read the link table information using a where clause based on the
     * value of a lhs object id.
     */
    @Override
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

        subtypeTableTranslator.addLoadRhsBody(loadRhsFn, identityVar, lhsToRhsLinkSet, rhsToLhsLinkSet);
        bodyFile.addFunctionDefinition(loadRhsFn);
    }

    /**
     * Add the loadLhs method defined by the interface. This will access the
     * database to read the link table information using a where clause based on the
     * value of a rhs object id.
     */
    @Override
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

        subtypeTableTranslator.addLoadLhsBody(loadLhsFn, identityVar, lhsToRhsLinkSet, rhsToLhsLinkSet);
        bodyFile.addFunctionDefinition(loadLhsFn);
    }

    /**
     * Override the BinaryRelationshipMapperSqlClass implementation to handle the
     * three columns in the link table.
     *
     * @return an expression representing the SQL for a link operation
     */
    protected Expression generatePreparedLinkSql() {
        return Literal.createStringLiteral("INSERT INTO " + getTableName() + " VALUES(:1,:2," + typeColumnValue + ");");
    }

    /**
     * Override the BinaryRelationshipMapperSqlClass implementation to handle the
     * three columns in the link table.
     *
     * @return an expression representing the SQL for a unlink operation
     */
    protected Expression generatePreparedUnLinkSql() {
        return Literal.createStringLiteral("DELETE FROM " +
                                           getTableName() +
                                           " WHERE " +
                                           getTypeColumnName() +
                                           " = " +
                                           typeColumnValue +
                                           " AND " +
                                           getLeftColumnName() +
                                           " = :1 AND " +
                                           getRightColumnName() +
                                           "= :2;");
    }

    /**
     * Override the BinaryRelationshipMapperSqlClass implementation to handle the
     * three columns in the link table.
     *
     * @return an expression representing the SQL for loading all the data from the
     * link table
     */
    protected Expression generateLoadAllSelectSQL() {
        final Expression
                queryText =
                Literal.createStringLiteral("SELECT " +
                                            getLeftColumnName() +
                                            "," +
                                            getRightColumnName() +
                                            " FROM " +
                                            getTableName() +
                                            " WHERE " +
                                            getTypeColumnName() +
                                            " = " +
                                            typeColumnValue +
                                            ";");
        return queryText;
    }

    /**
     * Override the BinaryRelationshipMapperSqlClass implementation to handle the
     * three columns in the link table.
     *
     * @return an expression representing the SQL for loading all the LHS object ids
     * from the link table
     */
    protected Expression generateLoadLhsSelectSQL(final Variable queryVar, final Variable identityVar) {
        // Create cpp line:
        // query <<
        // "SELECT R5_lhs FROM R5_LINK_TABLE WHERE R5_type = 1 AND R5_rhs = " <<
        // rhsIdentity << ";";
        final List<Expression> streamExprList = new ArrayList<>();
        streamExprList.add(queryVar.asExpression());
        streamExprList.add(Literal.createStringLiteral("SELECT " +
                                                       getLeftColumnName() +
                                                       " FROM " +
                                                       getTableName() +
                                                       " WHERE " +
                                                       getTypeColumnName() +
                                                       " = " +
                                                       typeColumnValue +
                                                       " AND " +
                                                       getRightColumnName() +
                                                       " = "));
        streamExprList.add(identityVar.asExpression());
        streamExprList.add(Literal.createStringLiteral(";"));
        final Expression streamExpr = Std.ostreamExpression(streamExprList);
        return streamExpr;
    }

    /**
     * Override the BinaryRelationshipMapperSqlClass implementation to handle the
     * three columns in the link table.
     *
     * @return an expression representing the SQL for loading all the RHS object ids
     * from the link table
     */
    protected Expression generateLoadRhsSelectSQL(final Variable queryVar, final Variable identityVar) {
        final List<Expression> streamExprList = new ArrayList<>();
        streamExprList.add(queryVar.asExpression());
        streamExprList.add(Literal.createStringLiteral("SELECT " +
                                                       getRightColumnName() +
                                                       " FROM " +
                                                       getTableName() +
                                                       " WHERE " +
                                                       getTypeColumnName() +
                                                       " = " +
                                                       typeColumnValue +
                                                       " AND " +
                                                       getLeftColumnName() +
                                                       " = "));
        streamExprList.add(identityVar.asExpression());
        streamExprList.add(Literal.createStringLiteral(";"));
        final Expression streamExpr = Std.ostreamExpression(streamExprList);
        return streamExpr;
    }

    /**
     * The additional column of the link table is used to represent the type of
     * object each inserted row relates to.Rather than adding a text representation
     * that could prove slow, use the ordering of the dervied object list to extract
     * a unique type value for each derived object taking part in the
     * generalisation.
     */
    private void setTypeColumnValue() {
        // Use the order of the dervied objects in the relationship Spec list,
        // to set the type column value to be used by the SQL.
        typeColumnValue = subtypeRelDecl.getSubtypes().indexOf(derivedObject);
    }
}
