/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate;

import java.lang.reflect.Constructor;
import java.util.*;

@Alias(stripPrefix = "org.xtuml.masl.translate.", stripSuffix = ".Translator", value = "")
public abstract class Translator<ItemType> {

    private static final Map<String, Properties> properties = new HashMap<>();
    private static final Map<Class<? extends Translator<?>>, Map<Object, Translator<?>>> instances = new HashMap<>();

    public static <T extends Translator<ItemType>, ItemType> T getInstance(final Class<T> translatorClass,
                                                                           final Class<? extends ItemType> itemClass,
                                                                           final ItemType item) throws Exception {
        Map<Object, Translator<?>> lookup = instances.get(translatorClass);
        if (lookup == null) {
            lookup = new IdentityHashMap<>();
            instances.put(translatorClass, lookup);
        }
        T translator = translatorClass.cast(lookup.get(item));
        if (translator == null) {
            final Constructor<T> constructor = translatorClass.getDeclaredConstructor(itemClass);
            constructor.setAccessible(true);
            translator = constructor.newInstance(item);
            lookup.put(item, translator);
        }
        return translator;
    }

    static public void addProperties(final Map<String, Properties> properties) {
        Translator.properties.putAll(properties);
    }

    @SuppressWarnings("unchecked")
    public Properties getProperties() {
        return getProperties((Class<Translator<ItemType>>) this.getClass());
    }

    public Properties getProperties(final Class<? extends Translator<ItemType>> transClass) {
        final Properties translatorProperties = new Properties();
        if (properties.get(transClass.getName()) != null) {
            translatorProperties.putAll(properties.get(transClass.getName()));
        }
        if (properties.get(Alias.Util.getAlias(transClass)) != null) {
            translatorProperties.putAll(properties.get(Alias.Util.getAlias(transClass)));
        }
        return translatorProperties;
    }

    public void doTranslation() {
        System.out.println("Translating " + getName() + ".");
        final long millis = System.currentTimeMillis();

        translate();
    }

    public abstract void translate();

    public Collection<? extends Translator<ItemType>> getPrerequisites() {
        return Collections.emptySet();
    }

    public final String getName() {
        return Alias.Util.getAlias(getClass());
    }
}
