//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.metamodel.ASTNodeVisitor;


public final class PragmaList
implements org.xtuml.masl.metamodel.common.PragmaList
{

  private final List<PragmaDefinition>    pragmas      = new ArrayList<PragmaDefinition>();
  private final Map<String, List<String>> pragmaLookup = new HashMap<String, List<String>>();

  static public final String              EXTERNAL     = "external";
  static public final String              SCENARIO     = "scenario";
  static public final String              KEY_LETTER   = "key_letter";
  static public final String              FILENAME     = "filename";
  static public final String              TEST_ONLY    = "test_only";
  static public final String              BUILD_SET    = "build_set";

  public PragmaList ( final List<PragmaDefinition> pragmas )
  {
    addPragmas(pragmas);
  }

  public PragmaList ()
  {
    this(Collections.<PragmaDefinition>emptyList());
  }

  @Override
  public List<String> getPragmaValues ( final String name )
  {
    return pragmaLookup.get(name);
  }

  @Override
  public List<String> getPragmaValues ( final String name, final boolean allowValueList )
  {
    List<String> pragmaValues = getPragmaValues(name);
    if ( pragmaValues != null && allowValueList )
    {
      // Each pragma value might actual contain a list of values
      // i.e. soa_pass_parameter("context,attribute,properties")
      // This needs to be flattened out so the returned list contains
      // the individual values.
      final List<String> flattenedValues = new ArrayList<String>();
      for ( final String pragmaValue : pragmaValues )
      {
        // split on non-word character
        final String[] individualValues = pragmaValue.split("\\W");
        Collections.addAll(flattenedValues, individualValues);
      }
      pragmaValues = flattenedValues;
    }
    return pragmaValues;
  }

  @Override
  public String getValue ( final String name )
  {
    final List<String> defs = getPragmaValues(name);
    if ( defs != null && defs.size() > 0 )
    {
      return defs.get(0);
    }
    else
    {
      return "";
    }
  }

  @Override
  public boolean hasPragma ( final String name )
  {
    return getPragmaValues(name) != null;
  }

  @Override
  public boolean hasValue ( final String name )
  {
    return getPragmaValues(name) != null && getPragmaValues(name).size() > 0;
  }

  public void addPragmas ( final List<PragmaDefinition> pragmas )
  {
    this.pragmas.addAll(pragmas);

    for ( final PragmaDefinition pragma : this.pragmas )
    {
      if ( pragmaLookup.containsKey(pragma.getName()) )
      {
        pragmaLookup.get(pragma.getName()).addAll(pragma.getValues());
      }
      else
      {
        pragmaLookup.put(pragma.getName(), pragma.getValues());
      }
    }
  }

  @Override
  public List<PragmaDefinition> getPragmas ()
  {
    return Collections.unmodifiableList(pragmas);
  }

  @Override
  public String toString ()
  {
    return (org.xtuml.masl.utils.TextUtils.formatList(pragmas, "", "\n", "\n"));
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitPragmaList(this, p);
  }
}
