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
package org.xtuml.masl.translate.stacktrack;

import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.metamodel.project.ProjectTerminator;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Alias("StackTrack")
@Default
public class ProjectTranslator extends org.xtuml.masl.translate.ProjectTranslator {

    public static ProjectTranslator getInstance(final Project project) {
        return getInstance(ProjectTranslator.class, project);
    }

    private ProjectTranslator(final Project project) {
        super(project);
        mainProjectTranslator = org.xtuml.masl.translate.main.ProjectTranslator.getInstance(project);
    }

    /**
     * @return
     * @see org.xtuml.masl.translate.Translator#getPrerequisites()
     */
    @Override
    public Collection<org.xtuml.masl.translate.ProjectTranslator> getPrerequisites() {
        return Collections.singletonList(mainProjectTranslator);
    }

    @Override
    public void translate() {
        for (final ProjectDomain domain : project.getDomains()) {
            for (final ProjectTerminator terminator : domain.getTerminators()) {
                for (final ProjectTerminatorService service : terminator.getServices()) {
                    final ActionTranslator
                            serviceTranslator =
                            new ActionTranslator(mainProjectTranslator.getServiceTranslator(service));
                    serviceTranslator.translate();
                }
            }
        }
    }

    ObjectTranslator getObjectTranslator(final ObjectDeclaration object) {
        return objectTranslators.get(object);
    }

    Map<ObjectDeclaration, ObjectTranslator> objectTranslators = new HashMap<>();

    private final org.xtuml.masl.translate.main.ProjectTranslator mainProjectTranslator;

}
