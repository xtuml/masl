/*
 * Filename : DomainTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate;

import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.translate.main.Types;


@Alias(stripPrefix = "org.xtuml.masl.translate.", stripSuffix = ".DomainTranslator", value = "")
public abstract class DomainTranslator extends Translator<Domain>
{

  public static <T extends DomainTranslator> T getInstance ( final Class<T> translatorClass, final Domain domain )
  {
    try
    {
      return getInstance(translatorClass, Domain.class, domain);
    }
    catch ( final Exception e )
    {
      assert false : e.getMessage();
      return null;
    }
  }

  public DomainTranslator ( final Domain domain )
  {
    this.domain = domain;
  }

  public Domain getDomain ()
  {
    return domain;
  }

  protected final Domain domain;

  protected final Types  types = Types.getInstance();

  public Types getTypes ()
  {
    return types;
  }


}
