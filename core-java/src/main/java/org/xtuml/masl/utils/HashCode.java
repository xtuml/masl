//
// File: HashCode.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.utils;


public class HashCode
{

  public static int makeHash ( final Object... objects )
  {
    int hash = 0;
    for ( int i = 0; i < objects.length; ++i )
    {
      if ( objects[i] == null )
      {
        hash = combineHash(hash, 0);
      }
      else
      {
        hash = combineHash(hash, objects[i].hashCode());
      }
    }

    return hash;
  }

  public static int combineHashes ( final int... hashes )
  {
    int hash = 0;
    for ( int i = 0; i < hashes.length; ++i )
    {
      hash = combineHash(hash, hashes[i]);
    }

    return hash;
  }

  private static int combineHash ( final int seed, final int hash )
  {
    // Use same algorithm as C++ Boost.Hash, as presumably they know what they
    // are talking about when designing a good hash.
    return seed ^ (hash + 0x9e377b9b + (seed << 6) + (seed >> 2));
  }


}
