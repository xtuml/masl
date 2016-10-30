//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.StringType;


public class StringLiteral extends LiteralExpression
    implements org.xtuml.masl.metamodel.expression.StringLiteral
{

  public static StringLiteral create ( final Position position, final String literal )
  {
    if ( literal == null )
    {
      return null;
    }
    try
    {
      return new StringLiteral(position, literal);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }

  }

  public StringLiteral ( final String lhs, final String rhs )
  {
    super(null);
    this.original = "\"" + lhs + rhs + "\"";
    this.value = lhs + rhs;
  }

  private StringLiteral ( final Position position, final String literal ) throws SemanticError
  {
    super(position);
    original = literal;

    final StringBuilder builder = new StringBuilder(literal.length() - 2);

    final String noQuotes = literal.substring(1, literal.length() - 1);

    for ( int i = 0; i < noQuotes.length(); ++i )
    {
      final String unParsed = noQuotes.substring(i);

      if ( unParsed.charAt(0) == '\\' )
      {
        if ( unParsed.length() > 1 )
        {
          switch ( unParsed.charAt(1) )
          {
            case 'n':
              builder.append('\n');
              ++i;
              break;
            case 'r':
              builder.append('\r');
              ++i;
              break;
            case 't':
              builder.append('\t');
              ++i;
              break;
            case 'b':
              builder.append('\b');
              ++i;
              break;
            case 'f':
              builder.append('\f');
              ++i;
              break;
            case '\'':
              builder.append('\'');
              ++i;
              break;
            case '"':
              builder.append('"');
              ++i;
              break;
            case '\\':
              builder.append('\\');
              ++i;
              break;
            case 'u':
            {
              // Unicode escape
              if ( unParsed.length() < 6 )
              {
                throw new SemanticError(SemanticErrorCode.InvalidEscapeSequence, position);
              }
              i += 5;
              try
              {
                builder.append((char)Integer.parseInt(unParsed.substring(2, 6), 16));
              }
              catch ( final NumberFormatException e )
              {
                throw new SemanticError(SemanticErrorCode.InvalidEscapeSequence, position);
              }
            }
              break;
            case '0':
            case '1':
            case '2':
            case '3':
            {
              char ch = '\0';
              try
              {
                ch = (char)Integer.parseInt(unParsed.substring(1, 2), 8);
                ++i;
                ch = (char)Integer.parseInt(unParsed.substring(1, 3), 8);
                ++i;
                ch = (char)Integer.parseInt(unParsed.substring(1, 4), 8);
                ++i;
              }
              catch ( final NumberFormatException e )
              {
                // Do nothing... last valid parse is what we want
              }
              catch ( final StringIndexOutOfBoundsException e )
              {
                // Do nothing... last valid parse is what we want
              }
              builder.append(ch);
            }
              break;
            default:
              throw new SemanticError(SemanticErrorCode.InvalidEscapeSequence, position);

          }
        }
        else
        {
          throw new SemanticError(SemanticErrorCode.InvalidEscapeSequence, position);
        }
      }
      else
      {
        builder.append(unParsed.charAt(0));
      }

    }
    value = builder.toString();

  }


  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof StringLiteral) )
    {
      return false;
    }
    else
    {
      final StringLiteral obj2 = (StringLiteral)obj;

      return value.equals(obj2.value);
    }
  }

  @Override
  public BasicType getType ()
  {
    return StringType.createAnonymous();
  }

  @Override
  public String getValue ()
  {
    return value;
  }

  @Override
  public int hashCode ()
  {

    return value.hashCode();
  }

  @Override
  public String toString ()
  {
    return original;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitStringLiteral(this, p);
  }


  final private String value;
  final private String original;

}
