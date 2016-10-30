//
// File: Population.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.EnumerationType;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.expression.FindExpression;
import org.xtuml.masl.metamodel.expression.FindParameterExpression;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.DomainNamespace;
import org.xtuml.masl.translate.main.DomainTranslator;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.Types;
import org.xtuml.masl.translate.main.expression.PredicateNameMangler;



public class Population
{

  private static final String    CURRENT_STATE = "currentState";

  private static final String    ID            = "id";

  private static final TypeUsage idType        = new TypeUsage(Architecture.ID_TYPE);

  
  public Population ( final ObjectTranslator translator )
  {
    this.translator = translator;
    domainTranslator = translator.getDomainTranslator();
    bodyFile = domainTranslator.getLibrary().createBodyFile(Mangler.mangleFile(translator.getObjectDeclaration()) + "Population");
    headerFile = domainTranslator.getLibrary().createPrivateHeader(Mangler.mangleFile(translator.getObjectDeclaration()) + "Population");

    final Namespace namespace = DomainNamespace.get(translator.getObjectDeclaration().getDomain());

    theClass = new Class(Mangler.mangleName(translator.getObjectDeclaration()) + "Population", namespace);
    headerFile.addClassDeclaration(theClass);

    creators = theClass.createDeclarationGroup("Instance Creation");
    retrieval = theClass.createDeclarationGroup("Instance Retrieval");
    constructors = theClass.createDeclarationGroup("Constructors and Destructors");
    copyPreventers = theClass.createDeclarationGroup("Prevent copy");
    assignerStateMachine = theClass.createDeclarationGroup("Assigner State Machine");
    generateEvent = theClass.createDeclarationGroup("Generate Creation/Assigner Events");

    final Architecture.DynamicSingleton singleton = new Architecture.DynamicSingleton(theClass);

    theClass.addSuperclass(singleton.getClazz(), Visibility.PUBLIC);
    // theClass.addSuperclass(new Architecture.Population(new
    // TypeUsage(translator.getMainClass())).getClazz(), Visibility.PUBLIC);

    getSingleton = singleton.getGetSingleton().inheritInto(getTheClass());

    final Function constructor = theClass.createConstructor(constructors, Visibility.PROTECTED);
    bodyFile.addFunctionDefinition(constructor);
    final Function destructor = theClass.createDestructor(constructors, Visibility.PROTECTED);
    bodyFile.addFunctionDefinition(destructor);
    destructor.setVirtual(true);

    theClass.createCopyConstructor(copyPreventers, Visibility.PRIVATE);
    theClass.createAssignmentOperator(copyPreventers, Visibility.PRIVATE);


  }

  /**
   * @return Returns the createInstance method.
   */
  public Function getCreateInstance ()
  {
    return this.createInstance;
  }

  /**
   * @return
   */
  public Function getDeleteInstance ()
  {
    return deleteInstance;
  }


  public Function getGetInstance ()
  {
    return getInstance;
  }

  public Function getGetUniqueId ( final AttributeDeclaration att )
  {
    return getUniqueIds.get(att);
  }


  public Function getUseUniqueId ( final AttributeDeclaration att )
  {
    return useUniqueIds.get(att);
  }


  public Function getGetNextArchId ()
  {
    return getNextArchId;
  }

  
  public Class getTheClass ()
  {
    return this.theClass;
  }


  Function addCreateInstance ()
  {
    createInstance = theClass.createMemberFunction(creators, "createInstance", Visibility.PUBLIC);
    createInstance.setPure(true);
    createInstance.setReturnType(translator.getPointerType());

    for ( final AttributeDeclaration att : translator.getObjectDeclaration().getAttributes() )
    {
      if ( att.isIdentifier() || !att.isReferential() )
      {
        final TypeUsage type = Types.getInstance().getType(att.getType());
        final Variable param = createInstance.createParameter(type.getOptimalParameterType(), Mangler.mangleName(att));
        createInstanceParams.put(att, param.asExpression());
      }
    }

    if ( translator.getObjectDeclaration().hasCurrentState() )
    {
      final TypeUsage type = new TypeUsage(translator.getNormalFsm().getStateEnum());
      createInstance.createParameter(type.getOptimalParameterType(), CURRENT_STATE);
    }

    return createInstance;
  }

  public Expression getCreateInstanceParam ( final AttributeDeclaration att )
  {
    return createInstanceParams.get(att);
  }

  Map<AttributeDeclaration, Expression> createInstanceParams = new HashMap<AttributeDeclaration, Expression>();

  Function addDeleteInstance ()
  {
    deleteInstance = theClass.createMemberFunction(creators, "deleteInstance", Visibility.PUBLIC);
    deleteInstance.setPure(true);
    deleteInstance.createParameter(translator.getPointerType(), "instance");

    return deleteInstance;
  }

  Function addSize ()
  {
    getPopulationSize = theClass.createMemberFunction(creators, "size", Visibility.PUBLIC);
    getPopulationSize.setConst(true);
    getPopulationSize.setPure(true);
    getPopulationSize.setReturnType(new TypeUsage(Std.size_t));

    return getPopulationSize;
  }

  Function addGetAssignerState ( final EnumerationType stateEnum )
  {
    final TypeUsage type = new TypeUsage(stateEnum);
    final Function getter = theClass.createMemberFunction(assignerStateMachine, "getAssignerState", Visibility.PUBLIC);
    getter.setPure(true);
    getter.setReturnType(type);
    getter.setConst(true);
    getAssignerState = getter;
    return getter;
  }

  Function addSetAssignerState ( final EnumerationType stateEnum )
  {
    final TypeUsage type = new TypeUsage(stateEnum);
    final Function setter = theClass.createMemberFunction(assignerStateMachine, "setAssignerState", Visibility.PUBLIC);
    setter.setPure(true);
    setter.createParameter(type.getOptimalParameterType(), "newState");
    setAssignerState = setter;
    return setter;
  }

  private Function getAssignerState;
  private Function setAssignerState;

  public Function getGetCurrentState ()
  {
    return getAssignerState;
  }

  public Function getSetCurrentState ()
  {
    return setAssignerState;
  }

  Function addGetInstance ()
  {
    final TypeUsage returnType = translator.getPointerType();

    getInstance = theClass.createMemberFunction(retrieval, "getInstance", Visibility.PUBLIC);
    getInstance.setPure(true);
    getInstance.setConst(true);
    getInstance.setReturnType(returnType);
    getInstance.createParameter(idType.getOptimalParameterType(), ID);

    return getInstance;
  }


  public class FindFunction
  {

    public Function                                      function;
    public org.xtuml.masl.metamodel.expression.Expression predicate;
    public FindExpression.Type                           type;

    public FindFunction ( final Function function,
                          final org.xtuml.masl.metamodel.expression.Expression predicate,
                          final FindExpression.Type type )
    {
      this.function = function;
      this.predicate = predicate;
      this.type = type;
    }
  }


  Collection<FindFunction> findFunctions = new ArrayList<FindFunction>();

  public Collection<FindFunction> getFindFunctions ()
  {
    return findFunctions;
  }

  Function addFindFunction ( final org.xtuml.masl.metamodel.expression.Expression predicate, final FindExpression.Type type )
  {
    String name;
    TypeUsage returnType;

    switch ( type )
    {
      case FIND_ONLY:
        name = "findOnly";
        returnType = translator.getPointerType();
        break;
      case FIND_ONE:
        name = "findOne";
        returnType = translator.getPointerType();
        break;
      case FIND:
        name = (predicate == null ? "findAll" : "find");
        returnType = new TypeUsage(Architecture.set(translator.getPointerType()));
        break;
      default:
        throw new IllegalArgumentException("Unrecognised find type: " + type);
    }

    Function findFn;

    if ( predicate == null )
    {
      findFn = theClass.createMemberFunction(retrieval, name, Visibility.PUBLIC);
    }
    else
    {
      findFn = theClass.createMemberFunction(retrieval,
                                             name + "_" + PredicateNameMangler.createMangler(predicate).getName(),
                                             Visibility.PUBLIC);
      for ( final FindParameterExpression maslParam : predicate.getFindParameters() )
      {
        findFn.createParameter(domainTranslator.getTypes().getType(maslParam.getType())
                                               .getOptimalParameterType(), maslParam.getName());

      }
      findFn.setComment("MASL find: " + predicate.toString());
    }

    findFn.setConst(true);
    findFn.setPure(true);
    findFn.setReturnType(returnType);

    findFunctions.add(new FindFunction(findFn, predicate, type));

    return findFn;
  }

  Function addGetUniqueId ( final AttributeDeclaration att )
  {
    final TypeUsage type = Types.getInstance().getType(att.getType());
    final Function getUniqueId = theClass.createMemberFunction(creators, "getUnique_" + Mangler.mangleName(att), Visibility.PUBLIC);
    getUniqueId.setPure(true);
    getUniqueId.setReturnType(type);

    getUniqueIds.put(att, getUniqueId);

    return getUniqueId;
  }

  Function addUseUniqueId ( final AttributeDeclaration att )
  {
    final TypeUsage type = Types.getInstance().getType(att.getType());
    final Function useUniqueId = theClass.createMemberFunction(creators, "useUnique_" + Mangler.mangleName(att), Visibility.PUBLIC);
    useUniqueId.setPure(true);
    useUniqueId.createParameter(type, "id");

    useUniqueIds.put(att, useUniqueId);

    return useUniqueId;
  }

  Function addGetNextArchId ()
  {
    getNextArchId = theClass.createMemberFunction(creators, "getNextArchId", Visibility.PUBLIC);
    getNextArchId.setPure(true);
    getNextArchId.setReturnType(translator.getIdType());

    return getNextArchId;
  }

  Function addCreateEventFunction ( final EventDeclaration event )
  {
    if ( event.getType() == EventDeclaration.Type.NORMAL )
    {
      throw new IllegalStateException("Normal Event must be associated with Main class : " + event);
    }

    final Function createFunction = theClass.createStaticFunction(generateEvent,
                                                                  "create_"
                                                                      + Mangler.mangleName(event.getParentObject())
                                                                      + "_"
                                                                      + Mangler.mangleName(event),
                                                                  Visibility.PUBLIC);
    bodyFile.addFunctionDefinition(createFunction);
    return createFunction;
  }

  Expression getSingleton ()
  {
    return getSingleton.asFunctionCall();
  }

  private final DomainTranslator                    domainTranslator;
  private final ObjectTranslator                    translator;
  private final DeclarationGroup                    assignerStateMachine;
  private final DeclarationGroup                    constructors;
  private final DeclarationGroup                    copyPreventers;
  private final DeclarationGroup                    generateEvent;
  private Function                                  createInstance;
  private final DeclarationGroup                    creators;
  private Function                                  deleteInstance;
  private Function                                  getPopulationSize;


  private Function                                  getInstance;

  private final Function                            getSingleton;
  private final Map<AttributeDeclaration, Function> getUniqueIds = new HashMap<AttributeDeclaration, Function>();
  private final Map<AttributeDeclaration, Function> useUniqueIds = new HashMap<AttributeDeclaration, Function>();
  private Function                                  getNextArchId;

  private final DeclarationGroup                    retrieval;
  private final CodeFile                            bodyFile;
  private final CodeFile                            headerFile;

  private final Class                               theClass;
}
