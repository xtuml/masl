package org.xtuml.masl.translate.conan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Iterables;
import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.BuildTranslator;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.building.BuildSet;
import org.xtuml.masl.translate.building.FileGroup;
import org.xtuml.masl.translate.cmake.CMakeLists;
import org.xtuml.masl.translate.cmake.Utils;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Alias("conan")
@Default
public class Translator extends BuildTranslator {

    @Override
    public void translate(final File sourceDirectory) {

        List<TargetInfo> targets = new ArrayList<>();
        getBuildSet().getFileGroups()
                     .stream()
                     .filter(SharedLibrary.class::isInstance)
                     .map(SharedLibrary.class::cast)
                     .filter(
                             Library::isExport)
                     .forEach(lib -> {
                         targets.add(new TargetInfo(lib.getName(),
                                                List.of(lib.getName()),
                                                getDependencies(lib)));
                     });
        getBuildSet().getFileGroups()
                     .stream()
                     .filter(InterfaceLibrary.class::isInstance)
                     .map(InterfaceLibrary.class::cast)
                     .filter(
                             Library::isExport)
                     .forEach(lib -> {
                         targets.add(new TargetInfo(lib.getName(),
                                                Collections.emptyList(),
                                                getDependencies(lib)));
                     });
        if ( getDomain() != null ) {
            domainInfo = new DomainInfo(getBuildSet().getName(), getDomain().getName(), targets);
        } else {
            projectInfo = new ProjectInfo(getBuildSet().getName(), getProject().getProjectName(), targets);
        }
    }

    private List<Dependency> getDependencies(FileGroup library) {
        return library.getDependencies().stream().filter(this::exportDependency).map(Dependency::new).toList();
    }

    private boolean exportDependency(FileGroup dep) {
        return dep.getName() != null &&
                dep.getParent() != null &&
                dep.getParent().getName() != null &&
                !(dep instanceof Library lib && !lib.isExport());
    }

    @Override
    public void translateBuild(final org.xtuml.masl.translate.Translator<?> parent, final File sourceDirectory) {

    }

    @Override
    public void dump(final File directory) {
        try {
            var writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
            if ( domainInfo != null ) {
                writer.writeValue(new File(directory, domainInfo.domain + ".conan_info"), domainInfo);
            } else {
                writer.writeValue(new File(directory, projectInfo.project + ".conan_info"), projectInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private record Dependency(String pkg, String name) {
        Dependency(FileGroup dep) {
            this(dep.getParent().getPackage(), dep.getName());
        }
    }


    private record TargetInfo(String name, List<String> libs, List<Dependency> requires) {

    }

    private record DomainInfo(String pkg, String domain, List<TargetInfo> targets) {
    }

    private record ProjectInfo(String pkg, String project, List<TargetInfo> targets) {
    }

    private DomainInfo domainInfo;
    private ProjectInfo projectInfo;

}
