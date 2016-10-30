//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.common;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;


public class PragmaDefinition
    implements org.xtuml.masl.metamodel.common.PragmaDefinition
{

  String       name;
  List<String> values = null;

  public PragmaDefinition ( final String name, final List<String> values )
  {
    this.name = name;
    this.values = values;
  }

  @Override
  public List<String> getValues ()
  {
    return new ArrayList<String>(values);
  }

  @Override
  public String getName ()
  {
    return name;
  }

  @Override
  public String toString ()
  {
    return "pragma "
           + name
           + " ("
           + org.xtuml.masl.utils.TextUtils.formatList(values, "",
                                                      ",", "") + ");";
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitPragmaDefinition(this, p);
  }

}
