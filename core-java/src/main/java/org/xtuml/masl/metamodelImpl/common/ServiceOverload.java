//
// File: ServiceOverload.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.metamodelImpl.error.AlreadyDefined;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.name.Name;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.utils.TextUtils;


public abstract class ServiceOverload<T extends Service> extends Name
{

  public enum ServiceType
  {
    Service, Function
  }

  public interface ErrorSource
  {

    AlreadyDefined getAlreadyDefined ( Position position, String name, Position prevPosition );
  }

  private final Map<List<ParameterDefinition>, T> svcParamLookup = new HashMap<List<ParameterDefinition>, T>();
  private final Map<List<ParameterDefinition>, T> fnParamLookup  = new HashMap<List<ParameterDefinition>, T>();
  private final Map<List<BasicType>, T>           svcTypeLookup  = new HashMap<List<BasicType>, T>();
  private final Map<List<BasicType>, T>           fnTypeLookup   = new HashMap<List<BasicType>, T>();

  private final List<T>                           list           = new ArrayList<T>();

  private final SemanticErrorCode                 alreadyDefined;

  public ServiceOverload ( final String name, final SemanticErrorCode alreadyDefined )
  {
    super(name);
    this.alreadyDefined = alreadyDefined;
  }

  public T get ( final List<ParameterDefinition> params, final boolean isFunction )
  {
    if ( isFunction )
    {
      return fnParamLookup.get(params);
    }
    else
    {
      return svcParamLookup.get(params);
    }
  }

  @SuppressWarnings("unused")
  protected void checkCompatible ( final T service ) throws SemanticError
  {
    // Allow extra checks in subclass
  }

  public void add ( final T service ) throws AlreadyDefined, SemanticError
  {
    final Map<List<ParameterDefinition>, T> paramLookup = service.isFunction() ? fnParamLookup : svcParamLookup;
    final Map<List<BasicType>, T> typeLookup = service.isFunction() ? fnTypeLookup : svcTypeLookup;

    checkCompatible(service);
    final T previousDef = typeLookup.get(service.getSignature());
    if ( previousDef != null )
    {
      final String signature = service.getName() + "(" + TextUtils.formatList(service.getSignature(), "", ",", "") + ")";
      throw new AlreadyDefined(alreadyDefined, Position.getPosition(service.getName()), signature, previousDef.getPosition());
    }
    else
    {
      paramLookup.put(service.getParameters(), service);
      typeLookup.put(service.getSignature(), service);
      list.add(service);

      service.setOverloadNo(list.size() - 1);

    }
  }

  static final class ArgumentTypeFormatter
      implements TextUtils.Formatter<Expression>
  {

    @Override
    public String format ( final Expression value )
    {
      return value.getType().toString();
    }

  }

  static final class ParameterTypeFormatter
      implements TextUtils.Formatter<ParameterDefinition>
  {

    @Override
    public String format ( final ParameterDefinition value )
    {
      return value.getType().toString();
    }

  }

  final class ServiceSignatureFormatter
      implements TextUtils.Formatter<T>
  {

    @Override
    public String format ( final T value )
    {
      return value.getPosition() + ": "
             + value.getName()
             + "("
             + TextUtils.formatList(value.getParameters(), "", new ParameterTypeFormatter(), ", ", "")
             + ")";
    }

  }

  public T getCallable ( final Position position, final ServiceType type, final List<Expression> arguments ) throws SemanticError
  {
    final List<T> matches = new ArrayList<T>();
    final List<T> exactMatches = new ArrayList<T>();
    final List<T> nonMatches = new ArrayList<T>();
    for ( final T service : list )
    {
      final List<ParameterDefinition> parameters = service.getParameters();
      boolean match = (type == ServiceType.Function) == service.isFunction() && service.getParameters().size() == arguments.size();
      boolean exactMatch = match;

      final Iterator<Expression> argIt = arguments.iterator();

      for ( int i = 0; match && argIt.hasNext(); ++i )
      {
        final Expression arg = argIt.next();

        match = parameters.get(i).getType().isAssignableFrom(arg, true);
        exactMatch &= parameters.get(i).getType().isAssignableFrom(arg);
      }

      if ( match )
      {
        matches.add(service);
        if ( exactMatch )
        {
          exactMatches.add(service);
        }
      }
      else if ( (type == ServiceType.Function) == service.isFunction() )
      {
        nonMatches.add(service);
      }
    }


    if ( matches.size() == 0 )
    {
      if ( nonMatches.size() == 0 )
      {
        // All must have been wrong type, as if we are here there must have been
        // some with at least the correct name
        throw new SemanticError(type == ServiceType.Function ? SemanticErrorCode.NoFunctionCall : SemanticErrorCode.NoServiceCall,
                                position);
      }
      final String passedArgs = TextUtils.formatList(arguments, "(", new ArgumentTypeFormatter(), ",", ")");
      final String nonMatchArgs = TextUtils.formatList(nonMatches, "\n > ", new ServiceSignatureFormatter(), "\n > ", "");
      throw new SemanticError(type == ServiceType.Function ? SemanticErrorCode.NoMatchingFunctionCall
                                                          : SemanticErrorCode.NoMatchingServiceCall,
                              position,
                              passedArgs,
                              nonMatchArgs);
    }
    else if ( matches.size() > 1 )
    {
      if ( exactMatches.size() == 1 )
      {
        return exactMatches.get(0);
      }
      else
      {
        final String passedArgs = TextUtils.formatList(arguments, "(", new ArgumentTypeFormatter(), ",", ")");
        final String matchArgs = TextUtils.formatList(matches, "\n > ", new ServiceSignatureFormatter(), "\n > ", "");
        throw new SemanticError(type == ServiceType.Function ? SemanticErrorCode.AmbiguousFunctionCall
                                                            : SemanticErrorCode.AmbiguousServiceCall,
                                position,
                                passedArgs,
                                matchArgs);
      }
    }

    return matches.get(0);
  }

  public List<T> asList ()
  {
    return Collections.unmodifiableList(list);
  }

}
