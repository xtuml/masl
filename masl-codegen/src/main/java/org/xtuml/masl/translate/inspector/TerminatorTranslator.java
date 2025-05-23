/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.inspector;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;

import java.util.HashMap;
import java.util.Map;

class TerminatorTranslator {

    private final org.xtuml.masl.translate.main.TerminatorTranslator termTrans;

    private final CodeFile codeFile;
    private final CodeFile headerFile;
    private final Class handlerClass;

    private final Map<DomainTerminatorService, ActionTranslator> serviceTranslators = new HashMap<>();
    private final DomainTranslator domainTranslator;

    private final Namespace namespace;

    public Namespace getNamespace() {
        return namespace;
    }

    TerminatorTranslator(final DomainTerminator terminator) {
        domainTranslator = DomainTranslator.getInstance(terminator.getDomain());
        namespace = new Namespace(Mangler.mangleName(terminator), domainTranslator.getNamespace());

        termTrans = org.xtuml.masl.translate.main.TerminatorTranslator.getInstance(terminator);
        this.codeFile = domainTranslator.getLibrary().createBodyFile("Inspector" + Mangler.mangleFile(terminator));
        this.headerFile =
                domainTranslator.getLibrary().createPrivateHeader("Inspector" + Mangler.mangleFile(terminator));

        this.handlerClass = new Class(Mangler.mangleName(terminator) + "Handler", namespace);
        handlerClass.addSuperclass(Inspector.terminatorHandlerClass, Visibility.PUBLIC);
        headerFile.addClassDeclaration(handlerClass);

        final DeclarationGroup constructors = handlerClass.createDeclarationGroup("Constructors");

        for (final DomainTerminatorService service : terminator.getServices()) {
            final ActionTranslator trans = new ActionTranslator(service, this);
            serviceTranslators.put(service, trans);
            trans.translate();
        }

        final Function constructor = handlerClass.createConstructor(constructors, Visibility.PUBLIC);

        codeFile.addFunctionDefinition(constructor);

        final Class actionPtrType = Std.shared_ptr(new TypeUsage(Inspector.actionHandlerClass));
        final Function registerServiceHandler = new Function("registerServiceHandler");
        for (final DomainTerminatorService service : terminator.getServices()) {
            final ActionTranslator servTrans = serviceTranslators.get(service);
            final Expression id = termTrans.getServiceTranslator(service).getServiceId();
            final Class servHandlerClass = servTrans.getHandlerClass();

            constructor.getCode().appendExpression(registerServiceHandler.asFunctionCall(id,
                                                                                         actionPtrType.callConstructor(
                                                                                                 new NewExpression(new TypeUsage(
                                                                                                         servHandlerClass)))));
        }

    }

    void translate() {
    }

    Class getHandlerClass() {
        return handlerClass;
    }

    public CodeFile getCodeFile() {
        return codeFile;
    }

    public CodeFile getHeaderFile() {
        return headerFile;
    }

    public static TerminatorTranslator getInstance(final DomainTerminator terminator) {
        return DomainTranslator.getInstance(terminator.getDomain()).getTerminatorTranslator(terminator);
    }

}
