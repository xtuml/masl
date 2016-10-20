/*
 * Filename : TenaryRelationshipToTableTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Variable;


public interface TenaryRelationshipToTableTranslator
    extends RelationshipToTableTranslator
{

  public String getLeftColumnName ();

  public String getRightColumnName ();

  public String getAssocColumnName ();

  public void addLoadAllBody ( Function loadAllFn, Variable cachedTenaryContVar );

  public void addLoadLhsBody ( Function loadLhsFn, Variable rhsIdentityVar, Variable cachedTenaryContVar );

  public void addLoadRhsBody ( Function loadRhsFn, Variable lhsIdentityVar, Variable cachedTenaryContVar );

  public void addLoadAssBody ( Function loadAssFn, Variable assIdentityVar, Variable cachedTenaryContVar );
}
