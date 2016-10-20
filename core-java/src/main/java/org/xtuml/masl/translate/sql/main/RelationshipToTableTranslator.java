/*
 * Filename : RelationshipToTableTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Function;


public interface RelationshipToTableTranslator
{

  public String getTableName ();

  public String getCreateTableStatement ();

  public void createRowCountQuery ( String className, Function implFunction );

  public PreparedStatement createPreparedStatement ( PreparedStatement.PreparedStatementType classification );
}
