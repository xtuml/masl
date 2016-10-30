//
// File: DomainTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Properties;



@Alias(stripPrefix = "org.xtuml.masl.translate.", stripSuffix = ".Translator", value = "")
public abstract class Translator<ItemType>
{

  private static Map<String, Properties>                                         properties = new HashMap<String, Properties>();
  private static Map<Class<? extends Translator<?>>, Map<Object, Translator<?>>> instances  = new HashMap<Class<? extends Translator<?>>, Map<Object, Translator<?>>>();

  public static <T extends Translator<ItemType>, ItemType> T getInstance ( final Class<T> translatorClass,
                                                                           final Class<? extends ItemType> itemClass,
                                                                           final ItemType item ) throws Exception
  {
    Map<Object, Translator<?>> lookup = instances.get(translatorClass);
    if ( lookup == null )
    {
      lookup = new IdentityHashMap<Object, Translator<?>>();
      instances.put(translatorClass, lookup);
    }
    T translator = translatorClass.cast(lookup.get(item));
    if ( translator == null )
    {
      final Constructor<T> constructor = translatorClass.getDeclaredConstructor(itemClass);
      constructor.setAccessible(true);
      translator = constructor.newInstance(item);
      lookup.put(item, translator);
    }
    return translator;
  }

  static public void addProperties ( final Map<String, Properties> properties )
  {
    Translator.properties.putAll(properties);
  }


  @SuppressWarnings("unchecked")
  public Properties getProperties ()
  {
    return getProperties((Class<Translator<ItemType>>)this.getClass());
  }

  public Properties getProperties ( final Class<? extends Translator<ItemType>> transClass )
  {
    final Properties translatorProperties = new Properties();
    if ( properties.get(transClass.getName()) != null )
    {
      translatorProperties.putAll(properties.get(transClass.getName()));
    }
    if ( properties.get(Alias.Util.getAlias(transClass)) != null )
    {
      translatorProperties.putAll(properties.get(Alias.Util.getAlias(transClass)));
    }
    return translatorProperties;
  }

  public void doTranslation ()
  {
    System.out.println("Translating " + getName() + ".");
    final long millis = System.currentTimeMillis();

    translate();

    System.out.println("Translated  " + getName() + " (" + (System.currentTimeMillis() - millis) / 1000.0 + "secs)");

  }

  public abstract void translate ();

  public Collection<? extends Translator<ItemType>> getPrerequisites ()
  {
    return Collections.<Translator<ItemType>>emptySet();
  }

  public final String getName ()
  {
    return Alias.Util.getAlias(getClass());
  }
}
