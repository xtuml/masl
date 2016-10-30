//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.xtuml.masl.utils.TextUtils;


/**
 * Allows child declarations in a class to be grouped together. Each group may
 * have a mixture of public, protected and private members, and is headed by a
 * comment if required. Instances of this class are created by
 * {@link Class#createDeclarationGroup()} or
 * {@link Class#createDeclarationGroup(String)}.
 */
public class DeclarationGroup
{

  /**
   * Creates a declaration group ready for declarations to be added.
   */
  DeclarationGroup ()
  {
    this(null);
  }

  /**
   * Creates a declaration group with the supplied comment
   * 

   *          the comment to display at the top of the group. No comment will be
   *          displayed if this is null.
   */
  DeclarationGroup ( final String comment )
  {
    if ( comment != null )
    {
      startComment = Comment.createComment(comment);
    }
  }

  @Override
  public String toString ()
  {
    final Writer writer = new StringWriter();
    try
    {
      write(writer, "", null);
    }
    catch ( final IOException e )
    {
      e.printStackTrace();
    }
    return writer.toString();
  }


  /**
   * Adds a declaration to the group. Declarations will be written in the same
   * order that they were added.
   * 

   */
  void add ( final Declaration declaration )
  {
    declarations.add(declaration);
  }

  /**
   * Adds a variable to the group. The variable list is maintained in the same
   * order as the declarations, and is used by the constructor code to
   * initialise the variable values.
   * 

   *          The variable to add to the list
   */
  void addVariable ( final Variable variable )
  {
    variables.add(variable);
  }

  /**
   * Gets the list of declarations that were added to this group in the order
   * they were added.
   * 
   * @return the list of declarations
   */
  List<Declaration> getDeclarations ()
  {
    return new ArrayList<Declaration>(declarations);
  }


  /**
   * Finds all the forward declarations required to make all the child
   * declarations.
   * 
   * @return a set of required include files
   */
  Set<Declaration> getForwardDeclarations ()
  {
    final Set<Declaration> result = new LinkedHashSet<Declaration>();

    for ( final Declaration dec : declarations )
    {
      result.addAll(dec.getForwardDeclarations());
    }

    return result;
  }

  /**
   * Finds all the include files required to make all the child declarations.
   * 
   * @return a set of required include files
   */
  Set<CodeFile> getIncludes ()
  {
    final Set<CodeFile> result = new LinkedHashSet<CodeFile>();

    for ( final Declaration dec : declarations )
    {
      result.addAll(dec.getIncludes());
    }

    return result;
  }

  /**
   * Gets the list of variables declared in this group, which is used by the
   * constructor code to initialise the variable values.
   * 
   * @return The list of variables in this group
   */
  List<Variable> getVariables ()
  {
    return variables;
  }

  /**
   * Writes the group of declarations to the supplied writer at the required
   * indent level. The declarations will be written in the order that they were
   * added to the group. The visibility will be written each time it is
   * different to the previous declaration's visibility.
   * 

   *          the writer to write to

   *          indetentation for each declaration

   *          the namspace that is currently open to write the declaration group
   *          into
   * @throws IOException
   */
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    if ( declarations.size() == 0 )
    {
      return;
    }

    if ( startComment != null )
    {
      startComment.write(writer, indent, currentNamespace);
      writer.write("\n");
    }

    Visibility oldVisibility = null;

    for ( final Declaration declaration : declarations )
    {
      // Check to see whether the visibility has changed, and start a new
      // section if necessary.
      if ( declaration.getVisibility() != oldVisibility )
      {
        oldVisibility = declaration.getVisibility();
        writer.write(indent + declaration.getVisibility() + ":\n");
      }

      declaration.writeDeclaration(writer, indent + TextUtils.getIndent(), currentNamespace);
    }
    writer.write("\n\n");
  }

  /**
   * A list of the delcarations.
   */
  private final List<Declaration> declarations = new ArrayList<Declaration>();
  /**
   * A comment to display at the top of the group
   */
  private Comment                 startComment = null;

  /**
   * List of variables declared in this group
   */
  private final List<Variable>    variables    = new ArrayList<Variable>();


}
