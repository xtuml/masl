// 
// Filename : ServiceMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class ServiceMetaData extends ExecutableSource
    implements Comparable<ServiceMetaData>
{

  public enum ServiceType
  {
    Scenario, External, Domain, Terminator, Object, Instance, ProjectTerminator
  }

  public int compareTo ( final ServiceMetaData lhs )
  {
    // Compare names
    int res = name.compareTo(lhs.name);
    if ( res != 0 )
    {
      return res;
    }

    // Names are the same so compare parameter names
    for ( int i = 0; i < getParameters().length; i++ )
    {
      // Check for less parameters on lhs, in which case we are bigger
      if ( i >= lhs.getParameters().length )
      {
        return 1;
      }

      // Compare the next parameter position
      res = getParameters()[i].getName().compareTo(lhs.getParameters()[i].getName());
      if ( res != 0 )
      {
        return res;
      }
    }

    // Check for more parameters on lhs, in which case we are smaller
    if ( getParameters().length < lhs.getParameters().length )
    {
      return -1;
    }
    else
    {
      return 0;
    }
  }

  public String getName ()
  {
    return name;
  }

  public abstract TypeMetaData getReturnType ();

  public String getReturnTypeName ()
  {
    return returnTypeName;
  }

  public ServiceType getType ()
  {
    return type;
  }

  public boolean isFunction ()
  {
    return isFunction;
  }

  @Override
  public String toString ()
  {

    final StringBuffer res = new StringBuffer(name);

    if ( getParameters() != null )
    {
      res.append(" ( ");
      for ( int i = 0; i < getParameters().length; i++ )
      {
        if ( i != 0 )
        {
          res.append(", ");
        }
        res.append(getParameters()[i].toString());
      }
      res.append(" )");
    }

    if ( isFunction )
    {
      res.append(" returns " + returnTypeName);
    }
    return res.toString();
  }

  protected String      name;

  protected ServiceType type;

  protected boolean     isFunction;

  protected String      returnTypeName;

}
