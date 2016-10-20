//
// File: BuiltinException.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.exception;

import java.util.HashMap;
import java.util.Map;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.NotFoundGlobal;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;


public class BuiltinException extends ExceptionReference
    implements org.xtuml.masl.metamodel.exception.BuiltinException
{


  private enum ImplType
  {
    PROGRAM_ERROR("program_error", Type.PROGRAM_ERROR),
    DEADLOCK_ERROR("deadlock_error", Type.DEADLOCK_ERROR),
    STORAGE_ERROR("storage_error", Type.STORAGE_ERROR),
    CONSTRAINT_ERROR("constraint_error", Type.CONSTRAINT_ERROR),
    RELATIONSHIP_ERROR("relationship_error", Type.RELATIONSHIP_ERROR),
    REFERENTIAL_ACCESS_ERROR("referential_access_error", Type.REFERENTIAL_ACCESS_ERROR),
    IOP_ERROR("iop_error", Type.IOP_ERROR),
    IO_ERROR("io_error", Type.IO_ERROR);


    ImplType ( final String name, final Type type )
    {
      this.name = name;
      this.type = type;
    }

    @Override
    public String toString ()
    {
      return name;
    }

    String getName ()
    {
      return name;
    }

    Type getType ()
    {
      return type;
    }

    private String name;
    private Type   type;
  }

  static Map<String, ImplType> lookup = new HashMap<String, ImplType>();

  static
  {
    for ( final ImplType type : ImplType.values() )
    {
      lookup.put(type.getName(), type);
    }
  }

  public static BuiltinException create ( final Position position, final String name ) throws SemanticError
  {
    final ImplType type = lookup.get(name);
    if ( type == null )
    {
      throw new NotFoundGlobal(SemanticErrorCode.ExceptionNotFound, position, name);
    }
    else
    {
      return new BuiltinException(position, type);
    }
  }


  private BuiltinException ( final Position position, final ImplType type )
  {
    super(position);
    this.type = type;
  }

  @Override
  public Type getType ()
  {
    return type.getType();
  }

  @Override
  public String getName ()
  {
    return type.getName();
  }


  @Override
  public String toString ()
  {
    return getName();
  }

  ImplType type;

}
