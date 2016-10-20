//
// File: ObjectTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.exception.BuiltinException;
import org.xtuml.masl.metamodel.exception.ExceptionDeclaration;
import org.xtuml.masl.metamodel.exception.ExceptionReference;
import org.xtuml.masl.metamodel.exception.UserDefinedException;



public class ExceptionTranslator
{

  public final static Class getArchitectureException ( final BuiltinException.Type type )
  {
    return archExceptions.get(type);
  }

  public final static Map<BuiltinException.Type, Class> archExceptions = new EnumMap<BuiltinException.Type, Class>(BuiltinException.Type.class);

  static
  {
    archExceptions.put(BuiltinException.Type.PROGRAM_ERROR, Architecture.programError);
    archExceptions.put(BuiltinException.Type.STORAGE_ERROR, Architecture.storageError);
    archExceptions.put(BuiltinException.Type.CONSTRAINT_ERROR, Architecture.constraintError);
    archExceptions.put(BuiltinException.Type.RELATIONSHIP_ERROR, Architecture.relationshipError);
    archExceptions.put(BuiltinException.Type.REFERENTIAL_ACCESS_ERROR, Architecture.refAccessError);
    archExceptions.put(BuiltinException.Type.IOP_ERROR, Architecture.iopError);
    archExceptions.put(BuiltinException.Type.IO_ERROR, Architecture.ioError);
  }


  static public Class getExceptionClass ( final ExceptionReference exception )
  {
    if ( exception instanceof BuiltinException )
    {
      return getArchitectureException(((BuiltinException)exception).getType());
    }
    else
    {
      final ExceptionDeclaration declaration = ((UserDefinedException)exception).getException();
      return DomainTranslator.getInstance(declaration.getDomain()).getExceptionClass(declaration);
    }
  }


  public ExceptionTranslator ( final ExceptionDeclaration exception )
  {
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
    final Variable message = msgConstructor.createParameter(new TypeUsage(Std.string, TypeUsage.ConstReference), "message");
    msgConstructor.setSuperclassArgs(Architecture.maslException, Arrays.<Expression>asList(message.asExpression()));
    msgConstructor.declareInClass(true);

  }

  public Class getExceptionClass ()
  {
    return exceptionClass;
  }

  private final ExceptionDeclaration declaration;


  private final Class                exceptionClass;


  private final CodeFile             headerFile;

  private final String               name;


}
