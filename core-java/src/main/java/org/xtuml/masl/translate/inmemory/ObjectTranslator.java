//
// File: ObjectTranslator.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.NewExpression;
import org.xtuml.masl.cppgen.ReturnStatement;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.metamodel.expression.FindExpression;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.IdentifierDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.Types;
import org.xtuml.masl.translate.main.object.ClassAttributeTranslator;
import org.xtuml.masl.translate.main.object.ConcreteObjectTranslator;
import org.xtuml.masl.translate.main.object.HalfRelationshipTranslator;
import org.xtuml.masl.translate.main.object.IdentifierLookupTranslator;



public class ObjectTranslator extends ConcreteObjectTranslator
{

  ObjectTranslator ( final ObjectDeclaration object )
  {
    super(object);

    final DomainTranslator domainTranslator = DomainTranslator.getInstance(object.getDomain());
    namespace = new Namespace(Mangler.mangleName(objectDeclaration.getDomain()), Transient.NAMESPACE);

    headerFile = domainTranslator.getLibrary().createPrivateHeader("Transient" + Mangler.mangleFile(objectDeclaration));
    populationHeaderFile = domainTranslator.getLibrary().createPrivateHeader("Transient" + Mangler.mangleFile(objectDeclaration) + "Population");
    bodyFile = domainTranslator.getLibrary().createBodyFile("Transient" + Mangler.mangleFile(objectDeclaration));
    populationBodyFile = domainTranslator.getLibrary().createBodyFile("Transient" + Mangler.mangleFile(objectDeclaration) + "Population");

    relationshipTranslator = new HalfRelationshipTranslator(this, object)
    {

      @Override
      protected Class getToManyAssocClass ( final TypeUsage related, final TypeUsage assoc )
      {
        return Transient.toManyAssociative(related.getTemplateRefOnly(), assoc.getTemplateRefOnly());
      }

      @Override
      protected Class getToManyRelClass ( final TypeUsage related )
      {
        return Transient.toManyRelationship(related.getTemplateRefOnly());
      }

      @Override
      protected Class getToOneAssocClass ( final TypeUsage related, final TypeUsage assoc )
      {
        return Transient.toOneAssociative(related.getTemplateRefOnly(), assoc.getTemplateRefOnly());
      }

      @Override
      protected Class getToOneRelClass ( final TypeUsage related )
      {
        return Transient.toOneRelationship(related.getTemplateRefOnly());
      }

    };


    attributeTranslator = new ClassAttributeTranslator(this, object)
    {

      @Override
      protected TypeUsage getType ( final BasicType type )
      {
        return Types.getInstance().getType(type);
      }

    };


  }

  @Override
  public Expression createNewInstance ( final List<Expression> params )
  {
    return new NewExpression(new TypeUsage(mainClass), params);
  }

  @Override
  public void addRelationship ( final RelationshipSpecification spec, final RelationshipSpecification assocSpec )
  {
    relationshipTranslator.addRelationship(spec, assocSpec);
  }


  @Override
  public CodeFile getBodyFile ()
  {
    return bodyFile;
  }

  @Override
  public CodeFile getPopulationBodyFile ()
  {
    return populationBodyFile;
  }


  @Override
  protected void addArchitectureId ( final Function getter )
  {
    attributeTranslator.addArchitectureId(getter, mainObjectTranslator.getGetNextArchId().inheritInto(getMainClass())
                                                                      .asFunctionCall());
  }

  @Override
  protected void addAssignerState ( final TypeUsage stateType, final Function getter, final Function setter )
  {
    attributeTranslator.addAssignerState(stateType, getter, setter);
  }


  @Override
  protected Variable addAttribute ( final AttributeDeclaration declaration,
                                    final Function getter,
                                    final Variable constructorParameter,
                                    final Function setter )
  {
    return attributeTranslator.addAttribute(declaration, getter, constructorParameter, setter);
  }

  @Override
  protected void addCurrentState ( final TypeUsage stateType, final Function getter, final Function setter )
  {
    attributeTranslator.addCurrentState(stateType, getter, setter);
  }

  @Override
  protected void addPopulationClass ( final Class factoryClass )
  {
    queries = factoryClass.createDeclarationGroup("Queries");
    attributeTranslator.addFactoryClassDeclarationGroups();
  }

  private Function instanceCreated = null;
  private Function instanceDeleted = null;


  private Function getInstanceCreated ()
  {
    if ( instanceCreated == null )
    {
      instanceCreated = populationClass.createMemberFunction(queries, "instanceCreated", Visibility.PRIVATE);
      instanceCreated.setVirtual(true);
      instanceCreated.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(mainObjectTranslator.getMainClass()))),
                                      "instance");
      getPopulationBodyFile().addFunctionDefinition(instanceCreated);
    }
    return instanceCreated;

  }

  private Function getInstanceDeleted ()
  {
    if ( instanceDeleted == null )
    {
      instanceDeleted = populationClass.createMemberFunction(queries, "instanceDeleted", Visibility.PRIVATE);
      instanceDeleted.setVirtual(true);
      instanceDeleted.createParameter(new TypeUsage(Architecture.objectPtr(new TypeUsage(mainObjectTranslator.getMainClass()))),
                                      "instance");
      getPopulationBodyFile().addFunctionDefinition(instanceDeleted);
    }
    return instanceDeleted;

  }

  private final Map<IdentifierDeclaration, IdentifierLookupTranslator> identifierLookupTranslators = new HashMap<IdentifierDeclaration, IdentifierLookupTranslator>();

  IdentifierLookupTranslator getIdentifierLookupTranslator ( final IdentifierDeclaration identifier )
  {
    IdentifierLookupTranslator lookupTranslator = identifierLookupTranslators.get(identifier);

    if ( lookupTranslator == null )
    {
      lookupTranslator = new IdentifierLookupTranslator(this, objectDeclaration, identifier)
      {

        @Override
        public DeclarationGroup getDeclarationGroup ()
        {
          return queries;
        }

        @Override
        public CodeBlock getInstanceCreatedCode ()
        {
          return getInstanceCreated().getCode();
        }

        @Override
        public Expression getInstanceCreatedInstance ()
        {
          return getInstanceCreated().getParameters().get(0).asExpression();
        }

        @Override
        public CodeBlock getInstanceDeletedCode ()
        {
          return getInstanceDeleted().getCode();
        }

        @Override
        public Expression getInstanceDeletedInstance ()
        {
          return getInstanceDeleted().getParameters().get(0).asExpression();
        }

        @Override
        public TypeUsage getStoredType ()
        {
          return new TypeUsage(Architecture.objectPtr(new TypeUsage(mainObjectTranslator.getMainClass())));
        }

      };

      identifierLookupTranslators.put(identifier, lookupTranslator);
    }

    return lookupTranslator;
  }

  @Override
  protected Function getIdentifierCheck ( final IdentifierDeclaration identifier )
  {
    return getIdentifierLookupTranslator(identifier).getChecker();
  }

  private void createIdentifierFind ( final IdentifierDeclaration identifier,
                                      final org.xtuml.masl.metamodel.expression.Expression predicate,
                                      final Function function,
                                      final FindExpression.Type type )
  {
    final Function identifierLookup = getIdentifierLookupTranslator(identifier).getFinder();

    final List<? extends AttributeDeclaration> attributes = predicate.getFindEqualAttributes();

    final Iterator<Variable> param = function.getParameters().iterator();

    final Map<AttributeDeclaration, Expression> paramLookup = new HashMap<AttributeDeclaration, Expression>();
    for ( final AttributeDeclaration att : attributes )
    {
      paramLookup.put(att, param.next().asExpression());
    }

    final List<Expression> orderedParams = new ArrayList<Expression>(attributes.size());

    for ( final AttributeDeclaration identAttribute : identifier.getAttributes() )
    {
      orderedParams.add(paramLookup.get(identAttribute));
    }

    Expression result = identifierLookup.asFunctionCall(orderedParams);
    if ( type == FindExpression.Type.FIND )
    {
      result = function.getReturnType().getType().callConstructor(result);
    }
    function.getCode().appendStatement(new ReturnStatement(result));
    populationBodyFile.addFunctionDefinition(function);

  }


  protected void createPredicateFind ( final org.xtuml.masl.metamodel.expression.Expression predicate,
                                       final Function function,
                                       final FindExpression.Type type )
  {
    final List<Expression> findArgs = new ArrayList<Expression>(function.getParameters().size());
    for ( final Variable param : function.getParameters() )
    {
      findArgs.add(param.asExpression());
    }

    final Expression predicateArg = mainObjectTranslator.getBoundPredicate(predicate, findArgs);

    if ( type == FindExpression.Type.FIND )
    {
      final Variable result = new Variable(function.getReturnType(), "result");
      final Expression finder = Architecture.copy_if.asFunctionCall(new Function("begin").asFunctionCall(),
                                                                    new Function("end")
                                                                                       .asFunctionCall(),
                                                                    new Function("inserter").asFunctionCall(result.asExpression(),
                                                                                                            false),
                                                                    predicateArg);
      function.getCode().appendStatement(result.asStatement());
      function.getCode().appendStatement(finder.asStatement());
      function.getCode().appendStatement(new Function("forceUnique").asFunctionCall(result.asExpression(), false).asStatement());
      function.getCode().appendStatement(new ReturnStatement(result.asExpression()));
    }
    else
    {
      final Expression finder = (type == FindExpression.Type.FIND_ONLY ? Architecture.FIND_ONLY : Architecture.FIND_ONE)
                                                                                                                        .asFunctionCall(new Function("begin").asFunctionCall(),
                                                                                                                                        new Function("end").asFunctionCall(),
                                                                                                                                        predicateArg);

      function.getCode().appendStatement(new ReturnStatement(finder));
    }

    populationBodyFile.addFunctionDefinition(function);


  }

  @Override
  protected void addFindFunction ( final org.xtuml.masl.metamodel.expression.Expression predicate,
                                   final Function function,
                                   final FindExpression.Type type )
  {
    final IdentifierDeclaration identifier = mainObjectTranslator.getFindIdentifier(predicate);

    if ( identifier == null )
    {
      // No matching identifier, so use default finds and predicate functions
      createPredicateFind(predicate, function, type);
    }
    else
    {
      // The find is looking for an exact match on an identifier
      createIdentifierFind(identifier, predicate, function, type);
    }

  }


  @Override
  protected void addMainClass ( final Class mainClass )
  {
    attributeTranslator.addMainClassDeclarationGroups();
    relationshipTranslator.addRelationshipDeclarationGroups();
  }

  @Override
  protected void addRelationshipCorr ( final RelationshipSpecification spec, final Function correlator )
  {
    relationshipTranslator.addRelationshipCorr(spec, correlator);
  }

  @Override
  protected void addRelationshipLink ( final RelationshipSpecification spec, final Function linker, final Function unlinker )
  {
    relationshipTranslator.addRelationshipLink(spec, linker, false);
    relationshipTranslator.addRelationshipLink(spec, unlinker, true);
  }


  @Override
  protected void addRelationshipNav ( final RelationshipSpecification spec, final Function navigator )
  {
    relationshipTranslator.addRelationshipNav(spec, navigator);
  }

  @Override
  protected void addRelationshipNav ( final RelationshipSpecification spec,
                                      final Function navigator,
                                      final org.xtuml.masl.metamodel.expression.Expression predicate )
  {
    relationshipTranslator.addRelationshipNav(spec, navigator, predicate);
  }


  @Override
  protected void addRelationshipCount ( final RelationshipSpecification spec, final Function counter )
  {
    relationshipTranslator.addRelationshipCount(spec, counter);
  }

  @Override
  protected void addUniqueId ( final AttributeDeclaration declaration, final Function getter, final Function user )
  {
    attributeTranslator.addUniqueId(declaration, getter, user);
  }


  @Override
  protected Class getPopulationSuperclass ()
  {
    return Transient.population(new TypeUsage(mainObjectTranslator.getMainClass()),
                                new TypeUsage(mainObjectTranslator
                                                                  .getPopulationClass()));
  }

  @Override
  protected CodeFile getHeaderFile ()
  {
    return headerFile;
  }

  @Override
  protected CodeFile getPopulationHeaderFile ()
  {
    return populationHeaderFile;
  }

  @Override
  protected Namespace getNamespace ()
  {
    return namespace;
  }

  private final CodeFile                   bodyFile;
  private final CodeFile                   populationBodyFile;

  private final CodeFile                   headerFile;
  private final CodeFile                   populationHeaderFile;

  private final Namespace                  namespace;

  private final HalfRelationshipTranslator relationshipTranslator;
  private final ClassAttributeTranslator   attributeTranslator;
  private DeclarationGroup                 queries;

  @Override
  public List<Expression> getPopulationConstructorSuperclassParams ()
  {
    return new ArrayList<Expression>();
  }
}
