/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public enum CommandLine {
    INSTANCE;

    public File getModelFile() {
        return modelFile;
    }

    public boolean isProject() {
        return isProject;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public List<String> getDomainPaths() {
        return domainPaths;
    }

    public boolean isOverrideTranslators() {
        return overrideTranslators;
    }

    public List<String> getSkipTranslators() {
        return skiptranslators;
    }

    public List<String> getAddTranslators() {
        return addtranslators;
    }

    public String getDefaultPackage() {
        return defaultPackage;
    }

    public Map<String,String> getPackageLookup() {
        return packageLookup;
    }

    public String getBuildTranslator() {
        return buildTranslator;
    }

    public boolean getSerialFileWrites() {
        return serialFileWrites;
    }

    public boolean getForceBuild() {
        return forceBuild;
    }

    public boolean getBuildDisable() {
        return buildDisable;
    }

    public boolean isForTest() {
        return forTest;
    }

    public String getVersion() {
        return version;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public String getCustomBuildFile() {
        return customBuildFile;
    }

    public boolean getDisableCustomTranslator() {
        return disableCustomTranslator;
    }

    private boolean isProject = false;
    private boolean isInterface = false;
    private File modelFile = null;
    private String defaultPackage = null;
    private Map<String,String> packageLookup = new HashMap<>();
    private String domainParser = null;
    private String buildTranslator = null;
    private String outputDirectory = ".";
    private boolean buildDisable = false;
    private boolean forTest = false;
    private boolean serialFileWrites = false;
    private boolean forceBuild = false;
    private boolean disableCustomTranslator = false;
    private final List<String> skiptranslators = new ArrayList<>();
    private final List<String> addtranslators = new ArrayList<>();
    private boolean overrideTranslators = false;
    private final List<String> domainPaths = new ArrayList<>();
    private String version = "0.0.0";
    private String customBuildFile = null;
    /**
     * Display a usage message.
     */
    private void help() {
        System.err.println("usage: java " + Main.class.getName() + " [args]");
        System.err.println("  -help                      print out this message");
        System.err.println("  -domainpath <domainpath>   set the domain path");
        System.err.println("  -defaultPackage <name>     set the default build package name");
        System.err.println("  -domainPackage <domain> <pkg>  set the build package for a domain");
        System.err.println("  -mod <model file>          the model file to parse");
        System.err.println("  -int <interface file>      the interface file to parse");
        System.err.println("  -prj <project file>        the project file to parse");
        System.err.println("  -output                    output directory for generated code");

        System.err.println(
                "  -serial-file-writes        do not thread out the writing of generated files (mitigate eclipse memory issues)");
        System.err.println("  -force                     force generated files to be regenerated, even if identical");
        System.err.println("  -builder                   the build target to use (imake/ant/ e.t.c)");
        System.err.println("  -builder-disable           disable the generation of all build files");
        System.err.println("  -disable-custom-translator disable parsing of translator.xml");
        System.err.println(
                "  -overridetranslators       ignore all default translators and only run those specified on command line");
        System.err.println(
                "  -skiptranslator            skip the specified translator(s) (include dependents) from the run list");
        System.err.println(
                "  -addtranslator             add  the specified translator(s) (include dependents) to   the run list");
        System.err.println("  -test                      generate code for test methods");
        System.err.println("  -copyright <file>          the file containing a copyright notice");
        System.err.println("  -custombuildfile           a build file for custom code");

    }

    public void parseArguments(final String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-help")) {
                help();
                System.exit(0);
            } else if (args[i].equals("-serial-file-writes")) {
                serialFileWrites = true;
            } else if (args[i].equals("-mod")) {
                modelFile = new File(args[++i]);
            } else if (args[i].equals("-int")) {
                modelFile = new File(args[++i]);
                isInterface = true;
            } else if (args[i].equals("-prj")) {
                modelFile = new File(args[++i]);
                isProject = true;
            } else if (args[i].equals("-output")) {
                outputDirectory = args[++i];
            } else if (args[i].equals("-domainParser")) {
                domainParser = args[++i];
            } else if (args[i].equals("-defaultPackage")) {
                defaultPackage = args[++i];
            } else if (args[i].equals("-domainPackage")) {
                String domain = args[++i];
                String pkg = args[++i];
                packageLookup.put(domain,pkg);
            } else if (args[i].equals("-quick")) {
                forceBuild = false;
            } else if (args[i].equals("-force")) {
                forceBuild = true;
            } else if (args[i].equals("-disable-custom-translator")) {
                disableCustomTranslator = true;
            } else if (args[i].equals("-builder-disable")) {
                buildDisable = true;
            } else if (args[i].equals("-test")) {
                forTest = true;
            } else if (args[i].equals("-builder")) {
                buildTranslator = args[++i];
            } else if (args[i].equals("-custombuildfile")) {
                customBuildFile = args[++i];
            }else if (args[i].equals("-domainpath")) {
                // -domainpath <domainpath>
                // SetVariable the domain path. If -domainpath is not specified, the domain
                // path is the current directory.

                if (i + 1 < args.length) {
                    i++;
                    for (final StringTokenizer tokeniser = new StringTokenizer(args[i], ":");
                         tokeniser.hasMoreTokens(); ) {
                        domainPaths.add(tokeniser.nextToken());
                    }
                } else {
                    help();
                    System.exit(1);
                }
            } else if (args[i].equals("-overridetranslators")) {
                overrideTranslators = true;
            } else if (args[i].equals("-onlytranslator")) {
                overrideTranslators = true;
                if (i + 1 < args.length) {
                    i++;
                    for (final StringTokenizer tokeniser = new StringTokenizer(args[i], ":");
                         tokeniser.hasMoreTokens(); ) {
                        addtranslators.add(tokeniser.nextToken());
                    }
                } else {
                    help();
                    System.exit(1);
                }
            } else if (args[i].equals("-skiptranslator")) {
                if (i + 1 < args.length) {
                    i++;
                    for (final StringTokenizer tokeniser = new StringTokenizer(args[i], ":");
                         tokeniser.hasMoreTokens(); ) {
                        skiptranslators.add(tokeniser.nextToken());
                    }
                } else {
                    help();
                    System.exit(1);
                }
            } else if (args[i].equals("-addtranslator")) {
                if (i + 1 < args.length) {
                    i++;
                    for (final StringTokenizer tokeniser = new StringTokenizer(args[i], ":");
                         tokeniser.hasMoreTokens(); ) {
                        addtranslators.add(tokeniser.nextToken());
                    }
                } else {
                    help();
                    System.exit(1);
                }
            } else if (args[i].equals("-version")) {
                version = args[++i];
            } else {
                help();
                System.exit(1);
            }
        }
    }

    public String getDomainParser() {
        return domainParser;
    }

}
