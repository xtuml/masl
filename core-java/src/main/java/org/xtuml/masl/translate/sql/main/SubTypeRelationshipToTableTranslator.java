/*
 * Filename : SubTypeRelationshipToTableTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Variable;


public interface SubTypeRelationshipToTableTranslator
    extends RelationshipToTableTranslator
{

  public String getLeftColumnName ();

  public String getRightColumnName ();

  public String getTypeColumnName ();

  public void addLoadAllBody ( Function loadAllFn, Variable lhsToRhsLinkSet, Variable rhsToLhsLinkSet );

  public void addLoadLhsBody ( Function loadLhsFn, Variable identityVar, Variable lhsToRhsLinkSet, Variable rhsToLhsLinkSet );

  public void addLoadRhsBody ( Function loadRhsFn, Variable identityVar, Variable lhsToRhsLinkSet, Variable rhsToLhsLinkSet );
}
