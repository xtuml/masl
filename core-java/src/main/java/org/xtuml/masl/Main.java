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
package org.xtuml.masl;

import org.xtuml.masl.antlr.Masl;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.translate.*;
import org.xtuml.masl.translate.building.BuildSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class Main {

    private static final Map<String, Class<? extends DomainTranslator>> allDomainTranslators = new HashMap<>();
    private static final Map<String, Class<? extends ProjectTranslator>> allProjectTranslators = new HashMap<>();
    private static final Map<String, Class<? extends BuildTranslator>> allBuildTranslators = new HashMap<>();

    private static void populateTranslatorLookups() {
        try {
            final String resourceName = "META-INF/translators";
            ClassLoader loader = Main.class.getClassLoader();
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }

            final Enumeration<URL> configFiles = loader.getResources(resourceName);

            while (configFiles.hasMoreElements()) {
                final URL url = configFiles.nextElement();

                final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

                while (reader.ready()) {
                    final String className = reader.readLine().trim();
                    if (className.length() > 0 && className.charAt(0) != '#') {
                        try {
                            final Class<?> translatorClass = Class.forName(className);
                            final String alias = Alias.Util.getAlias(translatorClass);
                            if (DomainTranslator.class.isAssignableFrom(translatorClass)) {
                                allDomainTranslators.put(alias, translatorClass.asSubclass(DomainTranslator.class));
                            } else if (ProjectTranslator.class.isAssignableFrom(translatorClass)) {
                                allProjectTranslators.put(alias, translatorClass.asSubclass(ProjectTranslator.class));
                            } else if (BuildTranslator.class.isAssignableFrom(translatorClass)) {
                                allBuildTranslators.put(alias, translatorClass.asSubclass(BuildTranslator.class));
                            } else {
                                System.err.println("Class " +
                                                   className +
                                                   " referenced in " +
                                                   url +
                                                   " is not a translator.");
                            }
                        } catch (final ClassNotFoundException e) {
                            System.err.println("Class " + className + " referenced in " + url + " not found.");
                        }
                    }
                }
            }

        } catch (final IOException e) {

        }
    }

    private static TranslatorParser customTranslatorXML;

    public static void main(final String[] args) throws Exception {
        System.exit(masl(args));
    }

    public static int masl(final String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");

        // We are not interested in warnings from system prefs. If they are not
        // writeable, that's OK.
        Logger.getLogger("java.util.prefs").setLevel(Level.SEVERE);

        populateTranslatorLookups();

        CommandLine.INSTANCE.parseArguments(args);

        final File modelFile = CommandLine.INSTANCE.getModelFile();

        if (modelFile != null) {
            if (CommandLine.INSTANCE.getDisableCustomTranslator()) {
                customTranslatorXML = new TranslatorParser();
            } else {
                customTranslatorXML = new TranslatorParser(new File(modelFile.getParent(), "custom/translator.xml"));
                CommandLine.INSTANCE.parseArguments(customTranslatorXML.getCmdLineArgs());
            }

            if (modelFile.canRead()) {
                System.out.println("Masling " + modelFile.getPath());
                if (CommandLine.INSTANCE.isProject()) {
                    return parseProject(modelFile);
                } else if (CommandLine.INSTANCE.isInterface()) {
                    return parseInterface(modelFile);
                } else {
                    return parseDomain(modelFile);
                }
            } else {
                System.err.println("Cannot read " + modelFile.getPath());
                return 1;
            }

        } else {
            System.err.println("Parse failed no MASL definition file found");
            return 1;
        }
    }

    static public Set<BuildTranslator> getBuildTranslators() throws Exception {
        TranslatorParser topLevelPreferences;
        if (System.getProperty("topLevel") != null) {
            topLevelPreferences =
                    new TranslatorParser(new File(System.getProperty("topLevel") + "/build_translator.xml"));
        } else {
            topLevelPreferences = new TranslatorParser();
        }

        return getBuildTranslators(Arrays.asList(new DefaultTranslatorPrefs<>(allBuildTranslators),
                                                 new JavaPreferences(Preferences.systemRoot().node(
                                                         "/masl/buildTranslators")),
                                                 new XMLPreferences(topLevelPreferences),
                                                 new JavaPreferences(Preferences.userRoot().node(
                                                         "/masl/buildTranslators")),
                                                 new CommandLineBuildPrefs()));
    }

    static private Set<BuildTranslator> getBuildTranslators(final List<TranslatorPreferences> preferences) {
        final Set<Class<? extends BuildTranslator>> translators = new LinkedHashSet<>();

        for (final TranslatorPreferences pref : preferences) {
            if (pref.isOverride()) {
                translators.clear();
            }

            for (final String translatorName : pref.getRunTranslators()) {
                try {
                    Class<? extends BuildTranslator> transClass = allBuildTranslators.get(translatorName);
                    if (transClass == null) {
                        transClass = Class.forName(translatorName).asSubclass(BuildTranslator.class);
                        allBuildTranslators.put(transClass.getName(), transClass);
                        allBuildTranslators.put(Alias.Util.getAlias(transClass), transClass);
                    }
                    if (translators.add(transClass)) {
                        System.out.println("Build Translator " +
                                           Alias.Util.getAlias(transClass) +
                                           " added by " +
                                           pref.getName() +
                                           ".");
                    }
                } catch (final ClassNotFoundException e) {
                    System.err.println("Build Translator " +
                                       translatorName +
                                       " not found. Add request by " +
                                       pref.getName() +
                                       " ignored.");
                }
            }

            for (final String toSkipNameName : pref.getSkipTranslators()) {
                try {
                    Class<? extends BuildTranslator> toSkipClass = allBuildTranslators.get(toSkipNameName);
                    if (toSkipClass == null) {
                        toSkipClass = Class.forName(toSkipNameName).asSubclass(BuildTranslator.class);
                        allBuildTranslators.put(toSkipClass.getName(), toSkipClass);
                        allBuildTranslators.put(Alias.Util.getAlias(toSkipClass), toSkipClass);
                    }
                    if (translators.remove(toSkipClass)) {
                        System.out.println("Build Translator " +
                                           Alias.Util.getAlias(toSkipClass) +
                                           " skipped by " +
                                           pref.getName() +
                                           ".");
                    }
                } catch (final ClassNotFoundException e) {
                    System.err.println("Build Translator " +
                                       toSkipNameName +
                                       " not found. Skip request by " +
                                       pref.getName() +
                                       " ignored.");
                }
            }
        }

        final Set<BuildTranslator> result = new LinkedHashSet<>();
        for (final Class<? extends BuildTranslator> transClass : translators) {
            try {
                result.add(transClass.newInstance());
            } catch (final Exception e) {
                System.err.println("Build Translator " +
                                   Alias.Util.getAlias(transClass) +
                                   " could not be instantiated. Ignored");
            }
        }

        return result;
    }

    static public Set<DomainTranslator> getDomainTranslators(final Domain domain) {
        TranslatorParser topLevelPreferences;
        if (System.getProperty("topLevel") != null) {
            topLevelPreferences =
                    new TranslatorParser(new File(System.getProperty("topLevel") + "/domain_translator.xml"));
        } else {
            topLevelPreferences = new TranslatorParser();
        }

        final List<TranslatorPreferences>
                prefList =
                Arrays.asList(new DefaultTranslatorPrefs<>(allDomainTranslators),
                              new JavaPreferences(Preferences.systemRoot().node("/masl/domainTranslators")),
                              new XMLPreferences(topLevelPreferences),
                              new JavaPreferences(Preferences.userRoot().node("/masl/domainTranslators")),
                              new PragmaPrefs(domain.getPragmas()),
                              new XMLPreferences(customTranslatorXML),
                              new CommandLinePrefs());

        for (final TranslatorPreferences preference : prefList) {
            final Map<String, Properties> currentProperties = preference.getTranslatorProperties();
            Translator.addProperties(currentProperties);
        }

        return getTranslators(domain, prefList, Domain.class, DomainTranslator.class, allDomainTranslators);
    }

    static class DefaultTranslatorPrefs<TranslatorType> implements TranslatorPreferences {

        DefaultTranslatorPrefs(final Map<String, Class<? extends TranslatorType>> allTranslators) {
            for (final Map.Entry<String, Class<? extends TranslatorType>> entry : allTranslators.entrySet()) {
                if (entry.getValue().getAnnotation(Default.class) != null) {
                    runTranslators.add(entry.getKey());
                }
            }
        }

        @Override
        public String getName() {
            return "default";
        }

        @Override
        public List<String> getRunTranslators() {
            return runTranslators;
        }

        @Override
        public List<String> getSkipTranslators() {
            return Collections.emptyList();
        }

        @Override
        public Map<String, Properties> getTranslatorProperties() {
            return Collections.emptyMap();
        }

        @Override
        public boolean isOverride() {
            return false;
        }

        private final List<String> runTranslators = new ArrayList<>();
    }

    static public Set<ProjectTranslator> getProjectTranslators(final Project project) {
        TranslatorParser topLevelPreferences;
        if (System.getProperty("topLevel") != null) {
            topLevelPreferences =
                    new TranslatorParser(new File(System.getProperty("topLevel") + "/project_translator.xml"));
        } else {
            topLevelPreferences = new TranslatorParser();
        }

        final List<TranslatorPreferences>
                prefList =
                Arrays.asList(new DefaultTranslatorPrefs<>(allProjectTranslators),
                              new JavaPreferences(Preferences.systemRoot().node("/masl/projectTranslators")),
                              new XMLPreferences(topLevelPreferences),
                              new JavaPreferences(Preferences.userRoot().node("/masl/projectTranslators")),
                              new PragmaPrefs(project.getPragmas()),
                              new XMLPreferences(customTranslatorXML),
                              new CommandLinePrefs());

        for (final TranslatorPreferences preference : prefList) {
            final Map<String, Properties> currentProperties = preference.getTranslatorProperties();
            Translator.addProperties(currentProperties);
        }

        return getTranslators(project, prefList, Project.class, ProjectTranslator.class, allProjectTranslators);
    }

    static private <TranslatorType extends Translator<ItemType>, ItemType> Set<TranslatorType> getTranslators(final ItemType item,
                                                                                                              final List<TranslatorPreferences> preferences,
                                                                                                              final Class<? extends ItemType> itemClass,
                                                                                                              final Class<? extends TranslatorType> translatorClass,
                                                                                                              final Map<String, Class<? extends TranslatorType>> translatorLookup) {

        final Set<Class<? extends TranslatorType>> translators = new LinkedHashSet<>();

        for (final TranslatorPreferences pref : preferences) {
            if (pref.isOverride()) {
                System.out.println("Translators overridden by " + pref.getName() + ".");
                translators.clear();
            }

            for (final String translatorName : pref.getRunTranslators()) {
                try {
                    Class<? extends TranslatorType> transClass = translatorLookup.get(translatorName);
                    if (transClass == null) {
                        transClass = Class.forName(translatorName).asSubclass(translatorClass);
                        translatorLookup.put(transClass.getName(), transClass);
                        translatorLookup.put(Alias.Util.getAlias(transClass), transClass);
                    }
                    if (translators.add(transClass)) {
                        System.out.println("Translator " +
                                           Alias.Util.getAlias(transClass) +
                                           " added by " +
                                           pref.getName() +
                                           ".");
                    }
                } catch (final ClassNotFoundException e) {
                    System.err.println("Translator " +
                                       translatorName +
                                       " not found. Add request by " +
                                       pref.getName() +
                                       " ignored.");
                }
            }

            for (final String toSkipNameName : pref.getSkipTranslators()) {
                try {
                    Class<? extends TranslatorType> toSkipClass = translatorLookup.get(toSkipNameName);
                    if (toSkipClass == null) {
                        toSkipClass = Class.forName(toSkipNameName).asSubclass(translatorClass);
                        translatorLookup.put(toSkipClass.getName(), toSkipClass);
                        translatorLookup.put(Alias.Util.getAlias(toSkipClass), toSkipClass);
                    }
                    if (translators.remove(toSkipClass)) {
                        System.out.println("Translator " +
                                           Alias.Util.getAlias(toSkipClass) +
                                           " skipped by " +
                                           pref.getName() +
                                           ".");
                    }
                } catch (final ClassNotFoundException e) {
                    System.err.println("Translator " +
                                       toSkipNameName +
                                       " not found. Skip request by " +
                                       pref.getName() +
                                       " ignored.");
                }
            }
        }

        final List<Class<? extends TranslatorType>> optionalTranslators = new ArrayList<>();
        final Set<String> activeTranslators = new HashSet<>();

        final LinkedHashSet<TranslatorType> result = new LinkedHashSet<>();
        for (final Class<? extends TranslatorType> transClass : translators) {
            if (transClass.getAnnotation(Requires.class) == null) {
                try {
                    addTranslator(result, activeTranslators, Translator.getInstance(transClass, itemClass, item));
                } catch (final Exception e) {
                    System.err.println("Translator " +
                                       Alias.Util.getAlias(transClass) +
                                       " could not be instantiated. Ignored");
                    e.printStackTrace();
                }
            } else {
                optionalTranslators.add(transClass);
            }
        }

        for (final Class<? extends TranslatorType> transClass : optionalTranslators) {
            // Check that all required translators are present
            boolean allowed = true;
            for (final String required : transClass.getAnnotation(Requires.class).value()) {
                if (!activeTranslators.contains(required)) {
                    try {
                        final Class<?> reqClass = Class.forName(required);
                        if (transClass.getAnnotation(Alias.class) != null &&
                            !activeTranslators.contains(Alias.Util.getAlias(reqClass))) {
                            allowed = false;
                        }
                    } catch (final ClassNotFoundException e) {
                        allowed = false;
                    }
                }
            }
            if (allowed) {
                try {
                    addTranslator(result, activeTranslators, Translator.getInstance(transClass, itemClass, item));
                } catch (final Exception e) {
                    System.err.println("Translator " +
                                       Alias.Util.getAlias(transClass) +
                                       " could not be instantiated. Ignored");
                }
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static <TranslatorType extends Translator<ItemType>, ItemType> void addTranslator(final LinkedHashSet<TranslatorType> result,
                                                                                              final Set<String> requiredTranslators,
                                                                                              final TranslatorType translator) {
        for (final Translator<ItemType> pre : translator.getPrerequisites()) {
            addTranslator(result, requiredTranslators, (TranslatorType) pre);
        }
        result.add(translator);
        requiredTranslators.add(translator.getName());
    }

    static private int parseInterface(final File modFile) throws Exception {
        final long millis = System.currentTimeMillis();

        final Masl masl = new Masl(CommandLine.INSTANCE.getDomainPaths());
        final Domain domain = masl.parseInterface(modFile);

        if (masl.warningCount() > 0) {
            System.err.println("***" + masl.warningCount() + " Warnings");
        }
        if (masl.errorCount() == 0) {
            final Set<DomainTranslator> translators = getDomainTranslators(domain);
            for (final DomainTranslator translator : translators) {
                translator.doTranslation();
            }

            final File dumpDir = new File(CommandLine.INSTANCE.getOutputDirectory());

            for (final BuildTranslator buildTranslator : getBuildTranslators()) {
                buildTranslator.setBuildSet(BuildSet.getBuildSet(domain));
                if (!CommandLine.INSTANCE.getBuildDisable()) {
                    buildTranslator.translate(modFile.getParentFile());
                    for (final DomainTranslator translator : translators) {
                        buildTranslator.translateBuild(translator, modFile.getParentFile());
                    }
                }
                buildTranslator.dump(dumpDir);
            }
        } else {
            System.err.println("***" + masl.errorCount() + " Errors");
        }
        // System.out.println("Total Time: " + (System.currentTimeMillis() - millis) /
        // 1000.0);
        return masl.errorCount();
    }

    static private int parseDomain(final File modFile) throws Exception {
        final long millis = System.currentTimeMillis();

        final Masl masl = new Masl(CommandLine.INSTANCE.getDomainPaths());
        final Domain domain = masl.parseDomain(modFile);

        if (masl.warningCount() > 0) {
            System.err.println("***" + masl.warningCount() + " Warnings");
        }
        if (masl.errorCount() == 0) {
            final Set<DomainTranslator> translators = getDomainTranslators(domain);
            for (final DomainTranslator translator : translators) {
                translator.doTranslation();
            }

            final File dumpDir = new File(CommandLine.INSTANCE.getOutputDirectory());

            for (final BuildTranslator buildTranslator : getBuildTranslators()) {
                buildTranslator.setBuildSet(BuildSet.getBuildSet(domain));
                if (!CommandLine.INSTANCE.getBuildDisable()) {
                    buildTranslator.translate(modFile.getParentFile());
                    for (final DomainTranslator translator : translators) {
                        buildTranslator.translateBuild(translator, modFile.getParentFile());
                    }
                }
                buildTranslator.dump(dumpDir);
            }
        } else {
            System.err.println("***" + masl.errorCount() + " Errors");
        }
        return masl.errorCount();
    }

    static private int parseProject(final File prjFile) throws Exception {
        final long millis = System.currentTimeMillis();

        final Masl masl = new Masl(CommandLine.INSTANCE.getDomainPaths());
        final Project project = masl.parseProject(prjFile);

        if (masl.warningCount() > 0) {
            System.err.println("***" + masl.warningCount() + " Warnings");
        }

        if (masl.errorCount() == 0) {
            final Set<ProjectTranslator> translators = getProjectTranslators(project);
            for (final ProjectTranslator translator : translators) {
                translator.doTranslation();
            }
            final File dumpDir = new File(CommandLine.INSTANCE.getOutputDirectory());
            for (final BuildTranslator buildTranslator : getBuildTranslators()) {
                buildTranslator.setBuildSet(BuildSet.getBuildSet(project));
                if (!CommandLine.INSTANCE.getBuildDisable()) {
                    buildTranslator.translate(prjFile.getParentFile());
                    for (final ProjectTranslator translator : translators) {
                        buildTranslator.translateBuild(translator, prjFile.getParentFile());
                    }
                }
                buildTranslator.dump(dumpDir);
            }
        } else {
            System.err.println("***" + masl.errorCount() + " Errors");
        }

        return masl.errorCount();
    }

}
