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
package org.xtuml.masl.translate.inspector;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.translate.main.Architecture;

public class Inspector {

    static Namespace inspectorNamespace = new Namespace("Inspector");
    static Library library = new InterfaceLibrary("Inspector").inBuildSet(Architecture.buildSet);

    static CodeFile processHandlerInc = library.createInterfaceHeader("inspector/ProcessHandler.hh");
    static CodeFile domainHandlerInc = library.createInterfaceHeader("inspector/DomainHandler.hh");
    static CodeFile genericObjectHandlerInc = library.createInterfaceHeader("inspector/GenericObjectHandler.hh");
    static CodeFile typesInc = library.createInterfaceHeader("inspector/types.hh");
    static CodeFile bufferedIOInc = library.createInterfaceHeader("inspector/BufferedIO.hh");
    static CodeFile commChannelInc = library.createInterfaceHeader("inspector/CommunicationChannel.hh");
    static CodeFile objectHandlerInc = library.createInterfaceHeader("inspector/ObjectHandler.hh");
    static CodeFile actionHandlerInc = library.createInterfaceHeader("inspector/ActionHandler.hh");
    static CodeFile eventHandlerInc = library.createInterfaceHeader("inspector/EventHandler.hh");
    static CodeFile terminatorHandlerInc = library.createInterfaceHeader("inspector/TerminatorHandler.hh");

    static Class bufferedInputStream = new Class("BufferedInputStream", inspectorNamespace, bufferedIOInc);
    static Class bufferedOutputStream = new Class("BufferedOutputStream", inspectorNamespace, bufferedIOInc);
    static Class commChannel = new Class("CommunicationChannel", inspectorNamespace, commChannelInc);
    static Class processHandlerClass = new Class("ProcessHandler", inspectorNamespace, processHandlerInc);
    static Class domainHandlerClass = new Class("DomainHandler", inspectorNamespace, domainHandlerInc);
    static Class
            genericObjectHandlerClass =
            new Class("GenericObjectHandler", inspectorNamespace, genericObjectHandlerInc);
    static Class objectHandlerClass = new Class("ObjectHandler", inspectorNamespace, objectHandlerInc);
    static Class terminatorHandlerClass = new Class("TerminatorHandler", inspectorNamespace, terminatorHandlerInc);
    static Class callable = new Class("Callable", inspectorNamespace, typesInc);

    static Function
            getProcessHandlerFn =
            processHandlerClass.createStaticFunction(processHandlerClass.createDeclarationGroup(),
                                                     "getInstance",
                                                     Visibility.PUBLIC);
    static Function
            getDomainHandlerFn =
            processHandlerClass.createMemberFunction(processHandlerClass.createDeclarationGroup(),
                                                     "getDomainHandler",
                                                     Visibility.PUBLIC);
    static Function
            getTerminatorHandlerFn =
            domainHandlerClass.createMemberFunction(domainHandlerClass.createDeclarationGroup(),
                                                    "getTerminatorHandler",
                                                    Visibility.PUBLIC);

    static {
        getProcessHandlerFn.setReturnType(new TypeUsage(processHandlerClass, TypeUsage.Reference));
        getDomainHandlerFn.setReturnType(new TypeUsage(domainHandlerClass, TypeUsage.Reference));
        getTerminatorHandlerFn.setReturnType(new TypeUsage(terminatorHandlerClass, TypeUsage.Reference));
    }

    static Class getObjectHandlerClass(final Class mainClass) {
        final Class handlerClass = new Class("ObjectHandler", inspectorNamespace, objectHandlerInc);
        handlerClass.addTemplateSpecialisation(new TypeUsage(mainClass));
        return handlerClass;
    }

    static Class actionHandlerClass = new Class("ActionHandler", inspectorNamespace, actionHandlerInc);
    static Class eventHandlerClass = new Class("EventHandler", inspectorNamespace, eventHandlerInc);

    static Expression getProcessHandler() {
        return getProcessHandlerFn.asFunctionCall();
    }

    static Expression getDomainHandler(final Expression domainId) {
        return getDomainHandlerFn.asFunctionCall(getProcessHandler(), false, domainId);
    }

    static Expression getObjectHandler(final Expression domainId, final Expression objectId, final Class objClass) {
        final Function getter = new Function("getObjectHandler");
        getter.setReturnType(new TypeUsage(getObjectHandlerClass(objClass), TypeUsage.Reference));
        getter.addTemplateSpecialisation(new TypeUsage(objClass));
        return getter.asFunctionCall(getDomainHandler(domainId), false, objectId);
    }

    static Expression getTerminatorHandler(final Expression domainId, final Expression terminatorId) {
        return getTerminatorHandlerFn.asFunctionCall(getDomainHandler(domainId), false, terminatorId);
    }
}
