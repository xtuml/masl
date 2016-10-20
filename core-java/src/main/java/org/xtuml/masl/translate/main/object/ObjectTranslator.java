//
// File: ObjectTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.EnumerationType;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.IfStatement;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.EnumerationType.Enumerator;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.expression.FindExpression;
import org.xtuml.masl.metamodel.expression.InstanceOrderingExpression;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.IdentifierDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.statemodel.TransitionTable;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.BigTuple;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.DomainNamespace;
import org.xtuml.masl.translate.main.DomainTranslator;
import org.xtuml.masl.translate.main.Mangler;



public class ObjectTranslator
{

  class FindKey
  {

    public FindKey ( final Expression predicate, final FindExpression.Type type )
    {
      this.predicate = predicate;
      this.type = type;
    }

    @Override
    public boolean equals ( final Object rhs )
    {
      return type == ((FindKey)rhs).type
             && (predicate == null ? ((FindKey)rhs).predicate == null : predicate.equals(((FindKey)rhs).predicate));
    }

    @Override
    public int hashCode ()
    {
      return (predicate == null ? 0 : predicate.hashCode()) ^ type.hashCode();
    }

    Expression          predicate;
    FindExpression.Type type;
  }

  public ObjectTranslator ( final ObjectDeclaration object, final org.xtuml.masl.cppgen.Expression objectId )
  {
    domainTranslator = DomainTranslator.getInstance(object.getDomain());
    this.objectDeclaration = object;
    this.objectId = objectId;

    main = new Main(this);

    pointerType = new TypeUsage(Architecture.objectPtr(new TypeUsage(main.getTheClass())));
  }

  private final org.xtuml.masl.cppgen.Expression objectId;

  public org.xtuml.masl.cppgen.Expression getObjectId ()
  {
    return objectId;
  }

  public void getSuperTypeEvents ( final ObjectDeclaration subtypeObj, final List<EventDeclaration> eventList )
  {
    for ( final ObjectDeclaration supertypeObj : subtypeObj.getSupertypes() )
    {
      eventList.addAll(supertypeObj.getEvents());
      getSuperTypeEvents(supertypeObj, eventList);
    }
  }

  private final List<SubtypeRelationshipDeclaration> polymorphisms = new ArrayList<SubtypeRelationshipDeclaration>();

  public void addPolymorphism ( final SubtypeRelationshipDeclaration rel )
  {
    polymorphisms.add(rel);
  }

  public void translatePolymorphisms ()
  {
    for ( final SubtypeRelationshipDeclaration rel : polymorphisms )
    {
      // recurse all the object supertypes to get all the
      // event declarations for the whole supertype/subtype tree.
      final List<EventDeclaration> allEvents = new ArrayList<EventDeclaration>();
      getSuperTypeEvents(objectDeclaration, allEvents);

      allEvents.addAll(objectDeclaration.getEvents());

      for ( final EventDeclaration event : allEvents )
      {
        // Don't pass creation event through polymorphically, as they have no
        // meaning in the subtype.
        if ( event.getType() == EventDeclaration.Type.NORMAL )
        {
          final Function processFunction = getEventTranslator(event).getProcessFunction();

          Statement result = null;

          final List<org.xtuml.masl.cppgen.Expression> args = new ArrayList<org.xtuml.masl.cppgen.Expression>();

          for ( final Variable arg : processFunction.getParameters() )
          {
            args.add(arg.asExpression());
          }

          // Generate a cascading if to pass the event through to the correct
          // subtype by checking whether an object exists down each
          // relationship.
          // Could be improved by adding a current subtype enumerate and
          // attribute, set on a link, and using a case statement here.
          for ( final ObjectDeclaration subType : rel.getSubtypes() )
          {
            final ObjectTranslator subObj = domainTranslator.getObjectTranslator(subType);

            final Function relationshipNavigator = relationshipTranslators.get(rel.getSuperToSubSpec(subType))
                                                                          .getPublicAccessors()
                                                                          .getNavigateFunction();

            final org.xtuml.masl.cppgen.Expression related = relationshipNavigator.asFunctionCall();
            final Function toCall = subObj.getEventTranslator(event).getProcessFunction();

            final Statement cascadeCall = new ExpressionStatement(toCall.asFunctionCall(related, true, args));

            result = new IfStatement(related, cascadeCall, result);


          }
          processFunction.getCode().appendStatement(result);
        }
      }

      for ( final ObjectService service : objectDeclaration.getServices() )
      {
        final Function serviceFunction = getService(service);

        if ( service.isDeferred() && service.getRelationship() == rel )
        {
          final List<org.xtuml.masl.cppgen.Expression> args = new ArrayList<org.xtuml.masl.cppgen.Expression>();

          for ( final Variable arg : serviceFunction.getParameters() )
          {
            args.add(arg.asExpression());
          }

          Statement result = null;

          // Generate a cascading if to pass the event through to the correct
          // subtype by checking whether an object exists down each
          // relationship.
          // Could be improved by adding a current subtype enumerate and
          // attribute, set on a link, and using a case statement here.
          for ( final ObjectService subService : service.getDeferredTo() )
          {
            final ObjectTranslator subObj = domainTranslator.getObjectTranslator(subService.getParentObject());
            final Function relationshipNavigator = relationshipTranslators.get(rel.getSuperToSubSpec(subService.getParentObject()))
                                                                          .getPublicAccessors()
                                                                          .getNavigateFunction();
            final org.xtuml.masl.cppgen.Expression related = relationshipNavigator.asFunctionCall();

            final Function toCall = subObj.getService(subService);

            final Statement cascadeCall = new ExpressionStatement(toCall.asFunctionCall(related, true, args));

            result = new IfStatement(related, cascadeCall, result);

          }

          serviceFunction.getCode().appendStatement(result);
          final CodeFile file = domainTranslator.getLibrary().createBodyFile(Mangler.mangleFile(service));
          file.addFunctionDefinition(serviceFunction);

        }

      }
    }

  }

  public org.xtuml.masl.cppgen.Expression createPointer ( final org.xtuml.masl.cppgen.Expression... rhs )
  {
    return Architecture.objectPtr(new TypeUsage(main.getTheClass())).callConstructor(rhs);
  }


  
  public StateMachineTranslator getAssignerFsm ()
  {
    return this.assignerFsm;
  }


  public Function getAttributeGetter ( final AttributeDeclaration att )
  {
    return attributeGetters.get(att);
  }

  public Function getAttributeSetter ( final AttributeDeclaration att )
  {
    return attributeSetters.get(att);
  }

  
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

  public Function getGetPopulationSize ()
  {
    return getPopulationSize;
  }

  public Function getFindFunction ( final Expression predicate, final FindExpression.Type type )
  {
    final FindKey key = new FindKey(predicate, type);
    Function findFn = findFunctions.get(key);

    if ( findFn == null )
    {
      findFn = main.addFindFunction(predicate, type);
      findFunctions.put(key, findFn);
    }
    return findFn;
  }

  public Function getFindPredicate ( final Expression predicate )
  {
    Function predicateFn = findPredicates.get(predicate);

    if ( predicateFn == null )
    {
      predicateFn = main.addFindPredicate(predicate);
      findPredicates.put(predicate, predicateFn);
    }

    return predicateFn;
  }

  public org.xtuml.masl.cppgen.Expression getBoundPredicate ( final Expression predicate,
                                                             final List<org.xtuml.masl.cppgen.Expression> findArgs )
  {
    final Function predicateFn = getFindPredicate(predicate);

    final List<org.xtuml.masl.cppgen.Expression> bindArgs = new ArrayList<org.xtuml.masl.cppgen.Expression>();
    bindArgs.add(predicateFn.asFunctionPointer());
    // &SWA::ObjectPtr<OBJECT>::get
    final org.xtuml.masl.cppgen.Expression ptrGetFunction = Architecture.objectPtr(new TypeUsage(getMainClass()))
                                                                       .getFunctionPointer("deref");

    // boost::bind([ptrGetFunction],boost::_1)
    bindArgs.add(Boost.bind.asFunctionCall(ptrGetFunction, Boost.bind_1));

    // If too many parameters for boost bind to cope with (not forgetting
    // the bound object), then wrap in a tuple. Note that the predicate
    // function should already be done!
    if ( findArgs.size() + 1 > Boost.MAX_BIND_PARAMS )
    {
      bindArgs.add(BigTuple.getMakeTuple(findArgs));
    }
    else
    {
      bindArgs.addAll(findArgs);
    }

    return Boost.bind.asFunctionCall(bindArgs);
  }


  /**
   * Given a find expression returns the identifier that matches the expression.
   * If no identifier matches null is returned. An identifier is deemed to match
   * if the expression checks for equality on all the attributes in the
   * identifier, and nothing else.
   *

   *          the expression to check
   * @return the matching identifier, null otherwise
   */
  public IdentifierDeclaration getFindIdentifier ( final Expression predicate )
  {
    final List<? extends AttributeDeclaration> equalAtts = predicate.getFindEqualAttributes();

    if ( equalAtts != null )
    {
      final Set<AttributeDeclaration> equalAttSet = new HashSet<AttributeDeclaration>(equalAtts);
      for ( final IdentifierDeclaration identifier : objectDeclaration.getIdentifiers() )
      {
        if ( equalAttSet.equals(new HashSet<AttributeDeclaration>(identifier.getAttributes())) )
        {
          return identifier;
        }
      }
    }
    return null;

  }

  private void addStreamOperator ()
  {
    final Function streamOperator = new Function("operator<<", DomainNamespace.get(objectDeclaration.getDomain()));
    streamOperator.setReturnType(new TypeUsage(Std.ostream, TypeUsage.Reference));
    final org.xtuml.masl.cppgen.Expression stream = streamOperator.createParameter(new TypeUsage(Std.ostream, TypeUsage.Reference),
                                                                                  "stream")
                                                                 .asExpression();
    final org.xtuml.masl.cppgen.Expression obj = streamOperator.createParameter(new TypeUsage(getMainClass(),
                                                                                             TypeUsage.ConstReference),
                                                                               "obj").asExpression();

    org.xtuml.masl.cppgen.Expression separator = Literal.createStringLiteral("(");
    final org.xtuml.masl.cppgen.Expression comma = Literal.createStringLiteral(",");

    for ( final AttributeDeclaration att : objectDeclaration.getAttributes() )
    {

      streamOperator.getCode().appendStatement(new BinaryExpression(stream, BinaryOperator.LEFT_SHIFT, separator).asStatement());
      final Statement outputValue = new BinaryExpression(stream,
                                                         BinaryOperator.LEFT_SHIFT,
                                                         getAttributeGetter(att).asFunctionCall(obj, false)).asStatement();

      if ( att.isIdentifier() || !att.isReferential() )
      {
        streamOperator.getCode().appendStatement(outputValue);
      }
      else
      {
        final Statement outputNull = new BinaryExpression(stream, BinaryOperator.LEFT_SHIFT, Literal.createStringLiteral("<null>")).asStatement();

        org.xtuml.masl.cppgen.Expression hasRelated = null;
        for ( final ReferentialAttributeDefinition refAtt : att.getRefAttDefs() )
        {
          final org.xtuml.masl.cppgen.Expression relatedObj = getRelationshipTranslator(refAtt.getRelationship()).getPublicAccessors()
                                                                                                                .getNavigateFunction()
                                                                                                                .asFunctionCall(obj,
                                                                                                                                false);
          if ( hasRelated == null )
          {
            hasRelated = relatedObj;

          }
          else
          {
            hasRelated = new BinaryExpression(hasRelated, BinaryOperator.OR, relatedObj);
          }

        }
        streamOperator.getCode()
                      .appendStatement(new IfStatement(hasRelated,
                                                       outputValue,
                                                       outputNull));

      }
      separator = comma;
    }
    streamOperator.getCode().appendStatement(new BinaryExpression(stream,
                                                                  BinaryOperator.LEFT_SHIFT,
                                                                  Literal.createStringLiteral(")")).asStatement());

    streamOperator.getCode().appendStatement(new ReturnStatement(stream));

    main.getHeaderFile().addFunctionDeclaration(streamOperator);
    main.getBodyFile().addFunctionDefinition(streamOperator);
  }

  
  public Function getGetId ()
  {
    return this.getId;
  }

  public Function getGetNextArchId ()
  {
    return getNextArchId;
  }


  public Function getGetUniqueId ( final AttributeDeclaration att )
  {
    return this.getUniqueIds.get(att);
  }


  public Function getUseUniqueId ( final AttributeDeclaration att )
  {
    return this.useUniqueIds.get(att);
  }

  public Function getGetInstance ()
  {
    return this.getInstance;
  }

  public TypeUsage getIdType ()
  {
    return idType;
  }

  public Class getMainClass ()
  {
    return main.getTheClass();
  }


  public RelationshipTranslator getRelationshipTranslator ( final RelationshipSpecification spec )
  {
    return relationshipTranslators.get(spec);
  }

  
  public StateMachineTranslator getNormalFsm ()
  {
    return this.normalFsm;
  }

  
  public ObjectDeclaration getObjectDeclaration ()
  {
    return this.objectDeclaration;
  }

  public Function getOrderByPredicate ( final List<? extends InstanceOrderingExpression.Component> attributeOrder )
  {
    Function predicateFn = orderByPredicates.get(attributeOrder);

    if ( predicateFn == null )
    {
      predicateFn = main.addOrderByPredicate(attributeOrder);
      orderByPredicates.put(attributeOrder, predicateFn);
    }

    return predicateFn;
  }

  public TypeUsage getPointerType ()
  {
    return pointerType;
  }

  public Population getPopulation ()
  {
    return population;
  }

  public Class getPopulationClass ()
  {
    return population.getTheClass();
  }

  public EventTranslator getEventTranslator ( final EventDeclaration event )
  {
    return eventTranslators.get(event);
  }

  public Function getGenerateDelayedEventFn ()
  {
    return generateDelayedEvent;
  }

  public Function getService ( final ObjectService service )
  {
    return serviceTranslators.get(service).getFunction();
  }

  public ObjectServiceTranslator getServiceTranslator ( final ObjectService service )
  {
    return serviceTranslators.get(service);
  }

  @Override
  public String toString ()
  {
    return "ObjectTranslator: " + objectDeclaration.getName();
  }

  public void translate ()
  {
    population = main.addPopulation();
    getId = main.addArchitectureId();
    getInstance = main.addGetInstance();
    addAttributes();
    addRelationships();
    addStateMachine();
    addEventGenerators();
    main.addConstructors();
    createInstance = main.addCreateInstance();
    deleteInstance = main.addDeleteInstance();
    getPopulationSize = main.addGetPopulationSize();
    addServices();

    getFindFunction(null, FindExpression.Type.FIND);
    getFindFunction(null, FindExpression.Type.FIND_ONE);
    getFindFunction(null, FindExpression.Type.FIND_ONLY);

  }


  public void addRelationships ()
  {
    for ( final RelationshipSpecification spec : objectDeclaration.getRelationships() )
    {
      relationshipTranslators.put(spec, new RelationshipTranslator(this, spec));
    }
  }

  public void translateCode ()
  {
    addReferentialGetters();
    translateServiceCode();
    translateStateMachineCode();
    translateRelationshipCode();
    addStreamOperator();
  }

  Main getMain ()
  {
    return main;
  }


  private void addAttributes ()
  {
    getNextArchId = main.addGetNextArchId();

    for ( final AttributeDeclaration att : objectDeclaration.getAttributes() )
    {
      final Function getter = main.addAttributeGetter(att);
      attributeGetters.put(att, getter);
      if ( !att.isIdentifier() && !att.isReferential() )
      {
        final Function setter = main.addAttributeSetter(att);
        attributeSetters.put(att, setter);
      }

      if ( att.isUnique() )
      {
        getUniqueIds.put(att, main.addGetUniqueId(att));
        useUniqueIds.put(att, main.addUseUniqueId(att));
      }

    }

  }

  private void addReferentialGetters ()
  {
    for ( final AttributeDeclaration att : objectDeclaration.getAttributes() )
    {
      if ( !att.isIdentifier() && att.isReferential() )
      {
        main.addReferentialAttributeGetter(att, attributeGetters.get(att));
      }
    }
  }

  private void addEventGenerators ()
  {
    final EnumerationType eventsEnum = new EnumerationType("EventIds");
    main.addIdEnumeration(eventsEnum);

    addEventGenerators(objectDeclaration, eventsEnum);
  }

  private final Map<EventDeclaration, org.xtuml.masl.cppgen.Expression> eventIds = new HashMap<EventDeclaration, org.xtuml.masl.cppgen.Expression>();


  public org.xtuml.masl.cppgen.Expression getEventId ( final EventDeclaration event )
  {
    return eventIds.get(event);
  }

  private void addEventGenerators ( final ObjectDeclaration fromObject, final EnumerationType eventsEnum )
  {

    for ( final EventDeclaration event : fromObject.getEvents() )
    {
      final Enumerator eventId = eventsEnum.addEnumerator("eventId_"
                                                          + Mangler.mangleName(event.getParentObject())
                                                          + "_"
                                                          + Mangler.mangleName(event), null);
      eventIds.put(event, eventId.asExpression());

      final StateMachineTranslator smTranslator = event.getType() == EventDeclaration.Type.ASSIGNER ? assignerFsm : normalFsm;

      final EventTranslator eventTranslator = new EventTranslator(this, smTranslator, event, eventId.asExpression());
      eventTranslators.put(event, eventTranslator);
      eventTranslator.translate();
    }

    for ( final ObjectDeclaration supObj : fromObject.getSupertypes() )
    {
      addEventGenerators(supObj, eventsEnum);
    }
  }

  private void addServices ()
  {
    final EnumerationType servicesEnum = new EnumerationType("ServiceIds");
    main.addIdEnumeration(servicesEnum);

    for ( final ObjectService service : objectDeclaration.getServices() )
    {
      final Enumerator serviceId = servicesEnum.addEnumerator("serviceId_" + Mangler.mangleName(service), null);

      final ObjectServiceTranslator translator = new ObjectServiceTranslator(this, service, serviceId.asExpression());
      serviceTranslators.put(service, translator);
    }
  }

  private void addStateMachine ()
  {
    statesEnum = new EnumerationType("StateIds");
    main.addIdEnumeration(statesEnum);

    final TransitionTable stateMachine = objectDeclaration.getStateMachine();
    if ( stateMachine != null )
    {
      normalFsm = new StateMachineTranslator(stateMachine, this);
    }

    final TransitionTable assignerStateMachine = objectDeclaration.getAssignerStateMachine();
    if ( assignerStateMachine != null )
    {
      assignerFsm = new StateMachineTranslator(assignerStateMachine, this);
    }

  }

  EnumerationType getStatesEnum ()
  {
    return statesEnum;
  }

  private void translateServiceCode ()
  {
    for ( final ObjectService service : objectDeclaration.getServices() )
    {
      serviceTranslators.get(service).translate();
    }
  }

  private void translateRelationshipCode ()
  {
    for ( final RelationshipSpecification spec : objectDeclaration.getRelationships() )
    {
      relationshipTranslators.get(spec).translate();
    }
  }


  private void translateStateMachineCode ()
  {
    if ( assignerFsm != null )
    {
      assignerFsm.translateCode();
    }
    if ( normalFsm != null )
    {
      normalFsm.translateCode();
    }
  }

  public StateActionTranslator getStateActionTranslator ( final State state )
  {
    return actionTranslators.get(state);
  }

  void addStateActionTranslator ( final State state, final StateActionTranslator translator )
  {
    actionTranslators.put(state, translator);
  }

  private EnumerationType                                                           statesEnum;

  Map<EventDeclaration, EventTranslator>                                            eventTranslators        = new HashMap<EventDeclaration, EventTranslator>();
  private final Map<State, StateActionTranslator>                                   actionTranslators       = new HashMap<State, StateActionTranslator>();


  private StateMachineTranslator                                                    assignerFsm             = null;
  private final Map<AttributeDeclaration, Function>                                 attributeGetters        = new HashMap<AttributeDeclaration, Function>();
  private final Map<AttributeDeclaration, Function>                                 attributeSetters        = new HashMap<AttributeDeclaration, Function>();
  private Function                                                                  createInstance;
  private Function                                                                  deleteInstance;
  private Function                                                                  getPopulationSize;

  private final Map<FindKey, Function>                                              findFunctions           = new HashMap<FindKey, Function>();
  private final Map<Expression, Function>                                           findPredicates          = new HashMap<Expression, Function>();
  private Function                                                                  getId;
  private Function                                                                  getNextArchId;
  private final Map<AttributeDeclaration, Function>                                 getUniqueIds            = new HashMap<AttributeDeclaration, Function>();
  private final Map<AttributeDeclaration, Function>                                 useUniqueIds            = new HashMap<AttributeDeclaration, Function>();
  private Function                                                                  getInstance;
  private Function                                                                  generateDelayedEvent;

  private final TypeUsage                                                           idType                  = new TypeUsage(Architecture.ID_TYPE);

  private final Main                                                                main;
  private StateMachineTranslator                                                    normalFsm               = null;
  private final ObjectDeclaration                                                   objectDeclaration;
  private final Map<List<? extends InstanceOrderingExpression.Component>, Function> orderByPredicates       = new HashMap<List<? extends InstanceOrderingExpression.Component>, Function>();
  private final TypeUsage                                                           pointerType;
  private Population                                                                population;

  private final Map<ObjectService, ObjectServiceTranslator>                         serviceTranslators      = new HashMap<ObjectService, ObjectServiceTranslator>();
  private final Map<RelationshipSpecification, RelationshipTranslator>              relationshipTranslators = new HashMap<RelationshipSpecification, RelationshipTranslator>();
  private final DomainTranslator                                                    domainTranslator;

  public DomainTranslator getDomainTranslator ()
  {
    return domainTranslator;
  }

  public static ObjectTranslator getInstance ( final ObjectDeclaration objectDeclaration )
  {
    return DomainTranslator.getInstance(objectDeclaration.getDomain()).getObjectTranslator(objectDeclaration);
  }

}
