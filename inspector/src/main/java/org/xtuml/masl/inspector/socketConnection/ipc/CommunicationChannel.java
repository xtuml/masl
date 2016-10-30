// 
// Filename : CommunicationChannel.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.ipc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;


public class CommunicationChannel
{

  private final DataInput          in;
  private final DataOutputStream   out;
  private DataInputStream          din;
  private SocketChannelInputStream sin;

  public CommunicationChannel ( final java.net.Socket socket ) throws IOException
  {
    in = din = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    sin = null;
    out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
  }


  public CommunicationChannel ( final java.nio.channels.SocketChannel channel )
  {
    in = sin = new SocketChannelInputStream(channel);
    din = null;
    out = new DataOutputStream(new BufferedOutputStream(java.nio.channels.Channels.newOutputStream(channel)));
  }

  public void flush () throws IOException
  {
    out.flush();
  }

  public int available () throws IOException
  {
    return (sin != null) ? sin.available() : din.available();
  }

  public void writeData ( final Object value ) throws IOException
  {
    if ( value.getClass().isArray() )
    {
      final int length = Array.getLength(value);
      writeData(length);
      for ( int i = 0; i < length; i++ )
      {
        writeData(Array.get(value, i));
      }
    }
    else if ( value instanceof Boolean )
    {
      writeData((Boolean)value);
    }
    else if ( value instanceof Byte )
    {
      writeData((Byte)value);
    }
    else if ( value instanceof Short )
    {
      writeData((Short)value);
    }
    else if ( value instanceof Integer )
    {
      writeData((Integer)value);
    }
    else if ( value instanceof Long )
    {
      writeData((Long)value);
    }
    else if ( value instanceof Float )
    {
      writeData((Float)value);
    }
    else if ( value instanceof Double )
    {
      writeData((Double)value);
    }
    else if ( value instanceof Character )
    {
      writeData((Character)value);
    }
    else if ( value instanceof String )
    {
      writeData((String)value);
    }
    else if ( value instanceof Enum<?> )
    {
      writeData(((Enum<?>)value));
    }
    else if ( value instanceof WriteableObject )
    {
      writeData((WriteableObject)value);
    }
    else
    {
      throw new IllegalArgumentException(value.getClass() + " is not a writeable type.");
    }
  }

  public void writeData ( final Boolean value ) throws IOException
  {
    writeData(value.booleanValue());
  }

  public void writeData ( final Byte value ) throws IOException
  {
    writeData(value.byteValue());
  }

  public void writeData ( final Short value ) throws IOException
  {
    writeData(value.shortValue());
  }

  public void writeData ( final Integer value ) throws IOException
  {
    writeData(value.intValue());
  }

  public void writeData ( final Long value ) throws IOException
  {
    writeData(value.longValue());
  }

  public void writeData ( final Float value ) throws IOException
  {
    writeData(value.floatValue());
  }

  public void writeData ( final Double value ) throws IOException
  {
    writeData(value.doubleValue());
  }

  public void writeData ( final Enum<?> value ) throws IOException
  {
    writeData(value.ordinal());
  }

  public void writeData ( final Character value ) throws IOException
  {
    writeData(value.toString().getBytes()[0]);
  }

  public void writeData ( final WriteableObject object ) throws IOException
  {
    object.write(this);
  }

  public void writeData ( final boolean[] value ) throws IOException
  {
    writeData(value.length);
    for ( final boolean element : value )
    {
      writeData(element);
    }
  }

  public void writeData ( final boolean value ) throws IOException
  {
    out.writeBoolean(value);
  }

  public void writeData ( final byte[] value ) throws IOException
  {
    writeData(value.length);
    out.write(value);
  }

  public void writeData ( final byte value ) throws IOException
  {
    out.writeByte(value);
  }

  public void writeData ( final short[] value ) throws IOException
  {
    writeData(value.length);
    for ( final short element : value )
    {
      writeData(element);
    }
  }

  public void writeData ( final short value ) throws IOException
  {
    out.writeShort(value);
  }

  public void writeData ( final int[] value ) throws IOException
  {
    writeData(value.length);
    for ( final int element : value )
    {
      writeData(element);
    }
  }

  public void writeData ( final int value ) throws IOException
  {
    out.writeInt(value);
  }

  public void writeData ( final long[] value ) throws IOException
  {
    writeData(value.length);
    for ( final long element : value )
    {
      writeData(element);
    }
  }

  public void writeData ( final long value ) throws IOException
  {
    out.writeLong(value);
  }

  public void writeData ( final float[] value ) throws IOException
  {
    writeData(value.length);
    for ( final float element : value )
    {
      writeData(element);
    }
  }

  public void writeData ( final float value ) throws IOException
  {
    out.writeFloat(value);
  }

  public void writeData ( final double[] value ) throws IOException
  {
    writeData(value.length);
    for ( final double element : value )
    {
      writeData(element);
    }
  }

  public void writeData ( final double value ) throws IOException
  {
    out.writeDouble(value);
  }

  public void writeData ( final char[] value ) throws IOException
  {
    writeData(value.length);
    for ( final char element : value )
    {
      writeData(element);
    }
  }

  public void writeData ( final char value ) throws IOException
  {
    out.writeByte(("" + value).getBytes(Charset.forName("ISO-8859-1"))[0]);
  }

  public void writeData ( final String value ) throws IOException
  {
    writeData(value.getBytes(Charset.forName("ISO-8859-1")));
  }

  public <Type> Type readData ( final Class<Type> type ) throws IOException
  {
    return type.cast(readDataUnchecked(type));
  }

  private Object readDataUnchecked ( final Class<?> type ) throws IOException
  {
    if ( type.isArray() )
    {
      final Class<?> innerType = type.getComponentType();
      final int length = readInt();
      final Object data = Array.newInstance(innerType, length);
      for ( int i = 0; i < length; i++ )
      {
        Array.set(data, i, readData(innerType));
      }
      return data;
    }
    else if ( type == Boolean.class || type == boolean.class )
    {
      return new Boolean(readBoolean());
    }
    else if ( type == Byte.class || type == byte.class )
    {
      return new Byte(readByte());
    }
    else if ( type == Short.class || type == short.class )
    {
      return new Short(readShort());
    }
    else if ( type == Integer.class || type == int.class )
    {
      return new Integer(readInt());
    }
    else if ( type == Long.class || type == long.class )
    {
      return new Long(readLong());
    }
    else if ( type == Float.class || type == float.class )
    {
      return new Float(readFloat());
    }
    else if ( type == Double.class || type == double.class )
    {
      return new Double(readDouble());
    }
    else if ( type == Character.class || type == char.class )
    {
      return new Character(readChar());
    }
    else if ( type == String.class )
    {
      return readString();
    }
    else if ( ReadableObject.class.isAssignableFrom(type) )
    {
      return readObjectGeneric(type);
    }
    else
    {
      throw new IllegalArgumentException(type + " is not a readable type.");
    }
  }

  public boolean readBoolean () throws IOException
  {
    return in.readBoolean();
  }

  public byte readByte () throws IOException
  {
    return in.readByte();
  }

  public short readShort () throws IOException
  {
    return in.readShort();
  }

  public int readInt () throws IOException
  {
    return in.readInt();
  }

  public long readLong () throws IOException
  {
    return in.readLong();
  }

  public float readFloat () throws IOException
  {
    return in.readFloat();
  }

  public double readDouble () throws IOException
  {
    return in.readDouble();
  }

  public char readChar () throws IOException
  {
    final byte ch = in.readByte();

    return new String(new byte[]
      { ch }, Charset.forName("ISO-8859-1")).charAt(0);
  }

  public <Type extends ReadableObject> Type readObject ( final Class<Type> type ) throws IOException
  {
    Type obj = null;
    try
    {
      obj = type.newInstance();
      obj.read(this);
    }
    catch ( final IllegalAccessException e )
    {
      e.printStackTrace();
    }
    catch ( final InstantiationException e )
    {
      e.printStackTrace();
    }

    return obj;
  }

  public <Type extends ReadableObject> Type[] readObjectArray ( final Class<Type[]> type, final Class<Type> contained ) throws IOException
  {
    final int length = readInt();
    final Type[] result = type.cast(Array.newInstance(contained, length));
    for ( int i = 0; i < length; i++ )
    {
      Array.set(result, i, readObject(contained));
    }

    return result;
  }


  private <Type> ReadableObject readObjectGeneric ( final Class<Type> type ) throws IOException
  {
    ReadableObject obj = null;
    try
    {
      obj = (ReadableObject)type.newInstance();
      obj.read(this);
    }
    catch ( final IllegalAccessException e )
    {
      e.printStackTrace();
    }
    catch ( final InstantiationException e )
    {
      e.printStackTrace();
    }

    return obj;
  }


  public String readString () throws IOException
  {
    final byte[] bytes = readByteArray();
    return new String(bytes, Charset.forName("ISO-8859-1"));
  }

  public boolean[] readBooleanArray () throws IOException
  {
    final int length = in.readInt();
    final boolean[] data = new boolean[length];
    for ( int i = 0; i < length; i++ )
    {
      data[i] = readBoolean();
    }
    return data;
  }

  public byte[] readByteArray () throws IOException
  {
    final int length = in.readInt();
    final byte[] data = new byte[length];
    in.readFully(data);
    return data;
  }

  public short[] readShortArray () throws IOException
  {
    final int length = in.readInt();
    final short[] data = new short[length];
    for ( int i = 0; i < length; i++ )
    {
      data[i] = readShort();
    }
    return data;
  }

  public int[] readIntArray () throws IOException
  {
    final int length = in.readInt();
    final int[] data = new int[length];
    for ( int i = 0; i < length; i++ )
    {
      data[i] = readInt();
    }
    return data;
  }

  public long[] readLongArray () throws IOException
  {
    final int length = in.readInt();
    final long[] data = new long[length];
    for ( int i = 0; i < length; i++ )
    {
      data[i] = readLong();
    }
    return data;
  }

  public float[] readFloatArray () throws IOException
  {
    final int length = in.readInt();
    final float[] data = new float[length];
    for ( int i = 0; i < length; i++ )
    {
      data[i] = readFloat();
    }
    return data;
  }

  public double[] readDoubleArray () throws IOException
  {
    final int length = in.readInt();
    final double[] data = new double[length];
    for ( int i = 0; i < length; i++ )
    {
      data[i] = readDouble();
    }
    return data;
  }

  public char[] readCharArray () throws IOException
  {
    final int length = in.readInt();
    final char[] data = new char[length];
    for ( int i = 0; i < length; i++ )
    {
      data[i] = readChar();
    }
    return data;
  }


}
