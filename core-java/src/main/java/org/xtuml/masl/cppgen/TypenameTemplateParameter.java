//
// File: TypenameTemplateParameter.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.utils.TextUtils;



public class TypenameTemplateParameter extends TemplateParameter
{

  private final TemplateType            type;
  private final List<TemplateParameter> templateParameters = new ArrayList<TemplateParameter>();

  
  public TypenameTemplateParameter ()
  {
    this.type = null;
  }

  public TypenameTemplateParameter ( final TemplateType type )
  {
    this.type = type;
  }

  public void addTemplateParameter ( final TemplateParameter param )
  {
    templateParameters.add(param);
  }

  @Override
  public String getName ()
  {
    final List<String> params = new ArrayList<String>();
    for ( final TemplateParameter param : templateParameters )
    {
      params.add(param.getName());
    }
    return TextUtils.formatList(params, "template<", ", ", "> ") + (type != null ? "class " + type.getName() : "");
  }

}
