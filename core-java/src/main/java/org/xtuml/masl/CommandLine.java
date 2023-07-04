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

import org.xtuml.masl.utils.CopyrightUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public enum CommandLine {
    INSTANCE;

    public File getModelFile() {
        return modelFile;
    }

    public boolean isProject() {
        return isProject;
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

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public String getCustomBuildFile() {
        return customBuildFile;
    }

    public String getCopyrightNotice() {
        return copyrightNotice;
    }

    public String getRawCopyrightNotice() {
        return rawCopyrightNotice;
    }

    public boolean getDisableCustomTranslator() {
        return disableCustomTranslator;
    }

    private boolean isProject = false;
    private File modelFile = null;
    private String domainParser = null;
    private String buildTranslator = null;
    private String outputDirectory = ".";
    private boolean buildDisable = false;
    private boolean forTest = false;
    private boolean serialFileWrites = false;
    private boolean forceBuild = false;
    private boolean disableCustomTranslator = false;
    private final List<String> skiptranslators = new ArrayList<String>();
    private final List<String> addtranslators = new ArrayList<String>();
    private boolean overrideTranslators = false;
    private final List<String> domainPaths = new ArrayList<String>();
    private String copyrightNotice = null;
    private String rawCopyrightNotice = null;
    private String customBuildFile = null;

    /**
     * Display a usage message.
     */
    private void help() {
        System.err.println("usage: java " + Main.class.getName() + " [args]");
        System.err.println("  -help                      print out this message");
        System.err.println("  -domainpath <domainpath>   set the domain path");
        System.err.println("  -mod <model file>          the model file to parse");
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
            } else if (args[i].equals("-prj")) {
                modelFile = new File(args[++i]);
                isProject = true;
            } else if (args[i].equals("-output")) {
                outputDirectory = args[++i];
            } else if (args[i].equals("-domainParser")) {
                domainParser = args[++i];
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
            } else if (args[i].equals("-copyright")) {
                copyrightNotice = CopyrightUtil.getCopyrightNotice(args[++i]);
                rawCopyrightNotice = CopyrightUtil.getRawCopyrightNotice(args[i]);
            } else if (args[i].equals("-custombuildfile")) {
                customBuildFile = args[++i];
            } else if (args[i].equals("-domainpath")) {
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
