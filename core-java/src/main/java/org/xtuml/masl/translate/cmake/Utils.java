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
package org.xtuml.masl.translate.cmake;

import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.translate.building.FileGroup;
import org.xtuml.masl.translate.building.ReferencedFile;
import org.xtuml.masl.translate.cmake.language.arguments.SingleArgument;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Utils {

    public static List<SingleArgument> getPathArgs(final Iterable<? extends ReferencedFile> files) {

        return StreamSupport.stream(files.spliterator(), false).map(Utils::getPathArg).collect(Collectors.toList());
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

    public static SingleArgument getPathArg(final ReferencedFile file) {

        return getPathArg(file.getFile());
    }

    public static SingleArgument getPathArg(final File file) {

        return new SingleArgument(file.getPath());
    }
}
