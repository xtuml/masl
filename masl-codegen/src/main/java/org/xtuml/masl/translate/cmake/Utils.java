/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.cmake;

import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.translate.building.FileGroup;
import org.xtuml.masl.translate.building.ReferencedFile;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Utils {

    public static List<SingleArgument> getSourcePathArgs(final Iterable<? extends ReferencedFile> files) {

        return StreamSupport.stream(files.spliterator(), false).map(Utils::getSourcePathArg).collect(Collectors.toList());
    }

    public static List<SingleArgument> getNameArgs(final Iterable<? extends FileGroup> targets) {

        return StreamSupport.stream(targets.spliterator(), false).map(Utils::getNameArg).filter(t -> t != null).collect(
                Collectors.toList());
    }

    public static SingleArgument getNameArg(final FileGroup target) {

        if (target.getName() == null) {
            return null;
        } else if (target.getParent() == null ||
                   target.getParent().getName() == null ||
                   (target instanceof Library && !((Library) target).isExport())) {
            return new SingleArgument(target.getName());
        } else {
            return new SingleArgument(target.getParent().getName() + "::" + target.getName());
        }

    }

    public static SingleArgument getSourcePathArg(final ReferencedFile file) {
        return getPathArg(new File("src",file.getFile().getPath()));
    }

    public static SingleArgument getPathArg(final File file) {

        return new SingleArgument(file.getPath());
    }
}
