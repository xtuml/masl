//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.statemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.code.CodeBlock;
import org.xtuml.masl.metamodelImpl.common.CheckedLookup;
import org.xtuml.masl.metamodelImpl.common.LocalVariableCollector;
import org.xtuml.masl.metamodelImpl.common.ParameterDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.error.AlreadyDefined;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.name.NameLookup;
import org.xtuml.masl.metamodelImpl.name.Named;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;


public class State extends Positioned
    implements org.xtuml.masl.metamodel.statemodel.State, Named
{

  public static void create ( final Position position,
                              final ObjectDeclaration object,
                              final String name,
                              final StateType type,
                              final List<ParameterDefinition> parameters,
                              final PragmaList pragmas )
  {
    if ( object == null || name == null || type == null )
    {
      return;
    }
    try
    {
      object.addState(new State(position, object, name, type, parameters, pragmas));
    }
    catch ( final SemanticError e )
    {
      e.report();
    }
  }

  private State ( final Position position,
                  final ObjectDeclaration object,
                  final String name,
                  final StateType type,
                  final List<ParameterDefinition> parameters,
                  final PragmaList pragmas )
  {
    super(position);
    this.name = name;
    this.parentObject = object;
    this.type = type;
    this.params = new CheckedLookup<ParameterDefinition>(SemanticErrorCode.ParameterAlreadyDefinedOnState,
                                                         SemanticErrorCode.ParameterNotFoundOnState,
                                                         this);
    for ( final ParameterDefinition param : parameters )
    {
      try
      {
        if ( param != null )
        {
          addParameter(param);
        }
      }
      catch ( final AlreadyDefined e )
      {
        // Report error, and ignore parameter
        e.report();
      }
    }
    this.declarationPragmas = pragmas;

  }

  public void addParameter ( final ParameterDefinition param ) throws AlreadyDefined
  {
    nameLookup.addName(param);
    params.put(param.getName(), param);
    signature.add(param.getType());
  }

  
  @Override
  public CodeBlock getCode ()
  {
    return this.code;
  }

  @Override
  public PragmaList getDeclarationPragmas ()
  {
    return declarationPragmas;
  }

  @Override
  public PragmaList getDefinitionPragmas ()
  {
    return definitionPragmas;
  }

  @Override
  public String getFileHash ()
  {
    return fileHash;
  }

  @Override
  public String getFileName ()
  {
    return parentObject.getKeyLetters() + (isAssigner() ? "-A_" : "_") + name + ".al";
  }

  @Override
  public String getName ()
  {
    return name;
  }

  public NameLookup getNameLookup ()
  {
    return nameLookup;
  }

  @Override
  public List<ParameterDefinition> getParameters ()
  {
    return Collections.unmodifiableList(params.asList());
  }


  @Override
  public List<org.xtuml.masl.metamodel.code.VariableDefinition> getLocalVariables ()
  {
    if ( code == null )
    {
      return Collections.<org.xtuml.masl.metamodel.code.VariableDefinition>emptyList();
    }
    else
    {
      return new LocalVariableCollector(code).getLocalVariables();
    }
  }

  @Override
  public ObjectDeclaration getParentObject ()
  {
    return parentObject;
  }

  @Override
  public String getQualifiedName ()
  {
    return parentObject.getDomain().getName() + "::" + parentObject.getName() + "." + name;
  }

  @Override
  public Type getType ()
  {
    return type.getType();
  }

  public boolean isAssigner ()
  {
    return type == StateType.ASSIGNER_START || type == StateType.ASSIGNER;
  }

  public boolean isInstance ()
  {
    return type == StateType.NORMAL || type == StateType.TERMINAL;
  }


  /**

   *          The code to set.
   */
  public void setCode ( final CodeBlock code )
  {
    this.code = code;
  }

  public void setDefinitionPragmas ( final PragmaList pragmas )
  {
    definitionPragmas = pragmas;
  }

  public void setFileHash ( final String fileHash )
  {
    this.fileHash = fileHash;
  }

  @Override
  public String toString ()
  {
    final String title = type + (type == StateType.NORMAL ? "" : " ") + "state\t" + name + "\t(\t";
    return title
           + org.xtuml.masl.utils.TextUtils.formatList(params.asList(), "", ",\n\t\t\t", "")
           + " );\n"
           + declarationPragmas;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitState(this, p);
  }


  private final String                             name;

  private final StateType                          type;

  private final CheckedLookup<ParameterDefinition> params;

  private final PragmaList                         declarationPragmas;

  private CodeBlock                                code              = null;

  private PragmaList                               definitionPragmas = null;

  private final NameLookup                         nameLookup        = new NameLookup();

  private final List<BasicType>                    signature         = new ArrayList<BasicType>();

  private final ObjectDeclaration                  parentObject;

  private String                                   fileHash          = null;

}
