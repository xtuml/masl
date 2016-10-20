/*
 * Filename : ObjectToTableTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import java.util.List;

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;


public interface ObjectToTableTranslator
{

  public String getTableName ();

  public int getArchIdColumnIndex ();

  public int getArchIdBindIndex ();

  public List<String> getColumnNameList ();

  public List<String> getAttributeNameList ();

  public String getCreateTableStatement ();

  public boolean isBlobColumn ( AttributeDeclaration attribute );

  public int getColumnIndex ( AttributeDeclaration attribute );

  public int getBindIndex ( AttributeDeclaration attribute );

  public boolean hasCurrentStateColumn ();

  public int getCurrentStateBindIndex ();

  PreparedStatement createPreparedStatement ( PreparedStatement.PreparedStatementType classification );

  void addGetMaxFnBody ( String className, Variable attributeName, Function executeGetMaxFn );

  void addGetMaxIdFnBody ( String className, Function executeGetMaxFn );

  void addGetRowCountFnBody ( String className, Function executeGetRowCountFn );

  void addExecuteSelectBody ( Function selectFn, Variable cacheParameter, Variable criteriaParameter, Variable resultParameter );

  void addExecuteSelectBody ( Function selectFn, Variable cacheParameter, Variable criteriaParameter );

}
