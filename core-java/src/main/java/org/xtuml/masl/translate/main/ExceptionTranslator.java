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
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.exception.BuiltinException;
import org.xtuml.masl.metamodel.exception.ExceptionDeclaration;
import org.xtuml.masl.metamodel.exception.ExceptionReference;
import org.xtuml.masl.metamodel.exception.UserDefinedException;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ExceptionTranslator {

    public final static Class getArchitectureException(final BuiltinException.Type type) {
        return archExceptions.get(type);
    }

    public final static Map<BuiltinException.Type, Class> archExceptions = new EnumMap<>(BuiltinException.Type.class);

    static {
        archExceptions.put(BuiltinException.Type.PROGRAM_ERROR, Architecture.programError);
        archExceptions.put(BuiltinException.Type.STORAGE_ERROR, Architecture.storageError);
        archExceptions.put(BuiltinException.Type.CONSTRAINT_ERROR, Architecture.constraintError);
        archExceptions.put(BuiltinException.Type.RELATIONSHIP_ERROR, Architecture.relationshipError);
        archExceptions.put(BuiltinException.Type.REFERENTIAL_ACCESS_ERROR, Architecture.refAccessError);
        archExceptions.put(BuiltinException.Type.IOP_ERROR, Architecture.iopError);
        archExceptions.put(BuiltinException.Type.IO_ERROR, Architecture.ioError);
    }

    static public Class getExceptionClass(final ExceptionReference exception) {
        if (exception instanceof BuiltinException) {
            return getArchitectureException(((BuiltinException) exception).getType());
        } else {
            final ExceptionDeclaration declaration = ((UserDefinedException) exception).getException();
            return DomainTranslator.getInstance(declaration.getDomain()).getExceptionClass(declaration);
        }
    }

    public ExceptionTranslator(final ExceptionDeclaration exception) {
        this.declaration = exception;
        final DomainTranslator domainTranslator = DomainTranslator.getInstance(exception.getDomain());
        headerFile = domainTranslator.getInterfaceLibrary().createInterfaceHeader(Mangler.mangleFile(declaration));

        final Namespace namespace = DomainNamespace.get(declaration.getDomain());

        name = Mangler.mangleName(declaration);
        exceptionClass = new Class(name, namespace);
        exceptionClass.addSuperclass(Architecture.maslException, Visibility.PUBLIC);
        final DeclarationGroup constructors = exceptionClass.createDeclarationGroup();
        headerFile.addClassDeclaration(exceptionClass);

        final Function defaultConstructor = exceptionClass.createConstructor(constructors, Visibility.PUBLIC);
        defaultConstructor.declareInClass(true);

        final Function msgConstructor = exceptionClass.createConstructor(constructors, Visibility.PUBLIC);
        final Variable
                message =
                msgConstructor.createParameter(new TypeUsage(Std.string, TypeUsage.ConstReference), "message");
        msgConstructor.setSuperclassArgs(Architecture.maslException, Collections.singletonList(message.asExpression()));
        msgConstructor.declareInClass(true);

    }

    public Class getExceptionClass() {
        return exceptionClass;
    }

    private final ExceptionDeclaration declaration;

    private final Class exceptionClass;

    private final CodeFile headerFile;

    private final String name;

}
