//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.ParameterDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.common.Visibility;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.exception.ExceptionReference;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;


public class ObjectService extends Service
    implements org.xtuml.masl.metamodel.object.ObjectService
{

  public static void create ( final Position position,
                                       final ObjectDeclaration object,
                                       final String name,
                                       final Visibility type,
                                       final boolean isInstance,
                                       final RelationshipDeclaration.Reference relRef,
                                       final List<ParameterDefinition> parameters,
                                       final BasicType returnType,
                                       final List<ExceptionReference> exceptionSpecs,
                                       final PragmaList pragmas )
  {
    if ( object == null || name == null || type == null || parameters == null || exceptionSpecs == null || pragmas == null )
    {
      return;
    }

    try
    {
      object.addService(new ObjectService(position,
                                          object,
                                          name,
                                          type,
                                          isInstance,
                                          relRef,
                                          parameters,
                                          returnType,
                                          exceptionSpecs,
                                          pragmas));
    }
    catch ( final SemanticError e )
    {
      e.report();
    }
  }


  private ObjectService ( final Position position,
                          final ObjectDeclaration object,
                          final String name,
                          final Visibility type,
                          final boolean isInstance,
                          final RelationshipDeclaration.Reference relRef,
                          final List<ParameterDefinition> parameters,
                          final BasicType returnType,
                          final List<ExceptionReference> exceptionSpecs,
                          final PragmaList pragmas ) throws SemanticError
  {
    super(position, name, type, parameters, returnType, exceptionSpecs, pragmas);
    this.parentObject = object;
    this.isInstance = isInstance;

    SubtypeRelationshipDeclaration subrel = null;

    if ( relRef != null )
    {
      if ( relRef.getRelationship() instanceof SubtypeRelationshipDeclaration
           && object == ((SubtypeRelationshipDeclaration)relRef.getRelationship()).getSupertype() )
      {
        subrel = (SubtypeRelationshipDeclaration)relRef.getRelationship();
      }
      else
      {
        throw new SemanticError(SemanticErrorCode.OnlyDeferToSubtype, relRef.getPosition());
      }
    }

    this.relationship = subrel;
  }

  @Override
  public String getFileName ()
  {
    if ( getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME) != null && getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME)
                                                                                                        .size() > 0 )
    {
      return getDeclarationPragmas().getPragmaValues(PragmaList.FILENAME).get(0);
    }
    else
    {
      return parentObject.getKeyLetters() + "_"
             + getName()
             + (getOverloadNo() > 0 ? "." + getOverloadNo() : "")
             + ".svc";
    }
  }

  @Override
  public boolean isInstance ()
  {
    return isInstance;
  }

  @Override
  public ObjectDeclaration getParentObject ()
  {
    return parentObject;
  }


  @Override
  public String getQualifiedName ()
  {
    return parentObject.getDomain().getName() + "::" + parentObject.getName() + "." + getName();
  }

  @Override
  public RelationshipDeclaration getRelationship ()
  {
    return relationship;
  }

  @Override
  public boolean isDeferred ()
  {
    return relationship != null;
  }

  @Override
  public List<ObjectService> getDeferredTo ()
  {
    final List<ObjectService> deferredTo = new ArrayList<ObjectService>();
    for ( final ObjectDeclaration subObject : relationship.getSubtypes() )
    {
      try
      {
        deferredTo.add(subObject.getPolymorphicService(this));
      }
      catch ( final NotFound e )
      {
        e.report();
      }
    }
    return Collections.unmodifiableList(deferredTo);
  }

  @Override
  public String getServiceType ()
  {
    return (isInstance ? "instance " : "") + (isDeferred() ? "deferred (" + relationship.getName() + ") " : "");
  }

  private final boolean                        isInstance;

  private final SubtypeRelationshipDeclaration relationship;


  private final ObjectDeclaration              parentObject;


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitObjectService(this, p);
  }

}
