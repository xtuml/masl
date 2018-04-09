//
// File: CodeFile.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.build.ReferencedFile;
import org.xtuml.masl.translate.build.WriteableFile;
import org.xtuml.masl.utils.Filter;
import org.xtuml.masl.utils.TextUtils;
import org.xtuml.masl.utils.TextUtils.Formatter;

import com.google.common.collect.Sets;


/**
 * A CodeFile represents a C++ header or body file. Definitions and declarations
 * of various types may be added to it. Facilities are provided for writing the
 * contents of the file to a {@link java.io.Writer}. When dumping a code file,
 * the dependencies for all declarations contained within it are checked, and
 * any required includes are added. Not all CodeFiles are intended to be dumped
 * to file - some header files may be references to external or system header
 * files. In these cases the CodeFile object is simply a placeholder to a file
 * outside the scope of the current code generation. However it is still
 * possible to dump these to file if required for other purposes, such as
 * debugging what declarations are included in the file.
 *
 */
public final class CodeFile extends ReferencedFile
    implements Comparable<CodeFile>, WriteableFile
{
  enum Type
  {
    BODY, PRIVATE_HEADER, PUBLIC_HEADER, INTERFACE_HEADER, SYSTEM_HEADER
  }

  /**
   * A string filter used to convert the filename into a header guard.
   */
  private final static Filter               headerGuardConverter = Filter.nullFilter;

  CodeFile ( final FileGroup parent, final File file, final Type type )
  {
    super(parent,file);
    this.type = type;
  }

  /**
   * Adds a class declaration to the file.
   *

   *          The class to add
   */
  public void addClassDeclaration ( final Class clazz )
  {
    addDeclaration(clazz.getDeclaration());
  }

  /**
   * Adds an enumerate declaration to the file.
   *

   *          The enumerate to add
   */
  public void addEnumerateDeclaration ( final EnumerationType enumeration )
  {
    addDeclaration(enumeration.getDeclaration());
  }

  /**
   * Adds the specified declaration to the set of forward declarations required
   * by this file. These files are in addition to the set of forward
   * declarations needed by the declarations and definitions, which are
   * calculated separately.
   *

   *          the declaration to add
   */
  public void addForwardDeclaration ( final Declaration forwardDeclaration )
  {
    forwardDeclarations.add(forwardDeclaration);
  }

  /**
   * Adds an function declaration to the file.
   *

   *          The function to add
   */
  public void addFunctionDeclaration ( final Function function )
  {
    addDeclaration(function.getDeclaration());
  }

  /**
   * Adds a function definition to the file. Definitions are output into the
   * file after any declarations, and in the order that they are added using
   * this method.
   *

   */
  public void addFunctionDefinition ( final Function function )
  {
    definitions.add(function.getDefinition());
    function.getDefinition().setDefinedIn(this);
  }


  /**
   * Adds the specified file to the set of include files required by this file.
   * These files are in addition to the set of includes needed by the
   * declarations and definitions, which are calculated separately. There would
   * normally be no need to add to these explicitly, unless creating a composite
   * header file which includes lots of others for the sole purpose of
   * simplifiying the include list of clients.
   *

   *          - the include file to add
   */
  public void addInclude ( final CodeFile include )
  {
    includes.add(include);
  }

  /**
   * Adds a macro callto the file.
   *

   *          The call to add
   */
  public void addMacroCall ( final MacroCall macro )
  {
    addDeclaration(macro.getDeclaration());
  }

  /**
   * Designates the supplied include file as being required before this one in
   * any list of include files. This effectively says that this include file
   * depends on definitions within the prerequisite. When code is generated that
   * needs this file, the prerequisite will be included first. This should not
   * generally be needed for generated headers, but some third party libraries
   * may require it.
   *

   *          the file that this one needs to be included first.
   */
  public void addPrerequisiteInclude ( final CodeFile include )
  {
    prerequisiteIncludes.add(include);
  }

  /**
   * Forces the supplied include file to be included at the top of this code
   * file, irrespective of any definitions within the file. Multiple calls to
   * this function will result in the files being included in the order that
   * they are added, possibly multiple times. This should not generally be
   * needed for generated headers, but some third party libraries may require
   * it.
   *

   *          the file to include
   */
  public void addTopInclude ( final CodeFile include )
  {
    topIncludes.add(include);
  }

  /**
   * Adds an typedef declaration to the file.
   *

   *          The typedef to add
   */
  public void addTypedefDeclaration ( final TypedefType typedef )
  {
    addDeclaration(typedef.getDeclaration());
  }

  /**
   * Adds a variable definition to the file. Definitions are output into the
   * file after any declarations, and in the order that they are added using
   * this method.
   *

   *          the variable whose definition should be added
   */
  public void addVariableDefinition ( final Variable variable )
  {
    definitions.add(variable.getDefinition());
    variable.getDefinition().setDefinedIn(this);
  }


  /**
   * Defines the sort order for code files. This is system files first, and
   * alphabetical order.
   *

   *          the file to compare against the current one
   * @return -1 0 or 1 if the current object is &lt; = or &gt; the rhs
   */
  @Override
  public int compareTo ( final CodeFile rhs )
  {
    return getFile().compareTo(rhs.getFile());
  }

  @Override
  public boolean equals ( final Object rhs )
  {
    if ( this == rhs )
    {
      return true;
    }
    if ( rhs instanceof CodeFile )
    {
      return type == ((CodeFile)rhs).type && getFile().equals(((CodeFile)rhs).getFile());
    }
    return false;
  }

  @Override
  public int hashCode ()
  {
    return getFile().hashCode();
  }

  @Override
  public String toString ()
  {
    final Writer writer = new StringWriter();
    try
    {
      writeCode(writer);
    }
    catch ( final IOException e )
    {
      e.printStackTrace();
    }
    return writer.toString();
  }


  /**
   * Writes the code for this code file to the supplied Writer. The code
   * consists of a copyright header, any required include files, any forward
   * declarations, followed by the declarations and definitions added to the
   * file.
   *

   * @throws IOException
   */
  @Override
  public void writeCode ( final Writer writer ) throws IOException
  {
    // Output the copyright notice.
    String copyrightNotice = CommandLine.INSTANCE.getCopyrightNotice();
    writer.write("//\n"
                 + "// File: "
                 + getFile().getName()
                 + "\n"
                 + ( null == copyrightNotice ? "" : "//\n// " + copyrightNotice.replaceAll("\n", "\n// ") + "\n" )
                 + "//\n");

    final String guardName = headerGuardConverter.convert(getFile().getPath());

    // If this is a header file, then output the include guard
    if ( isHeader() )
    {
      writer.write("#ifndef " + guardName + "\n" + "#define " + guardName + "\n\n");
    }

    // Output the list of includes required by the declarations and definitions
    final List<CodeFile> includes = getIncludes();

    final File parentDir = getFile().getParentFile();

    TextUtils.formatList(writer, includes, "", new Formatter<CodeFile>()
        {
          @Override
          public String format ( final CodeFile value )
          {
            return "#include " + value.getFileSpec(parentDir) + "\n";
          }

        }, "", "\n");


    // Output the list of forward declarations required by the declarations and
    // definitions
    final Set<Declaration> forwardDecs = getForwardDeclarations();

    Namespace currentNamespace = null;
    int indentLevel = 0;

    for ( final Declaration fwdDec : forwardDecs )
    {
      // No need to output this declaration if the include file it is fully
      // declared in has already been included
      if ( !includes.containsAll(fwdDec.getUsageIncludes()) )
      {
        // open and close namespaces as required to get to the one for the
        // current declaration
        final Namespace declarationNamespace = fwdDec.getParentNamespace();
        indentLevel = Namespace.openDeclaration(writer, indentLevel, currentNamespace, declarationNamespace);

        // Set the current namespace to the one for the declaration we're about
        // to write
        currentNamespace = declarationNamespace;

        // Write the forward declation
        fwdDec.writeForwardDeclaration(writer, TextUtils.getIndent(indentLevel), currentNamespace);
      }

    }

    // Output all the declarations added to the file
    for ( final Declaration dec : declarations )
    {
      // open and close namespaces as required to get to the one for the current
      // declaration
      final Namespace declarationNamespace = dec.getParentNamespace();
      indentLevel = Namespace.openDeclaration(writer, indentLevel, currentNamespace, declarationNamespace);

      // Set the current namespace to the one for the declaration we're about to
      // write
      currentNamespace = declarationNamespace;

      // Write the declation
      dec.writeDeclaration(writer, TextUtils.getIndent(indentLevel), currentNamespace);

    }

    // Output all the definitions added to the file
    for ( final Definition def : definitions )
    {
      // open and close namespaces as required to get to the one for the current
      // declaration
      final Namespace declarationNamespace = def.getParentNamespace();
      indentLevel = Namespace.openDeclaration(writer, indentLevel, currentNamespace, declarationNamespace);

      // Set the current namespace to the one for the declaration we're about to
      // write
      currentNamespace = declarationNamespace;

      // Write the declation
      def.writeDefinition(writer, TextUtils.getIndent(indentLevel), currentNamespace);
      writer.write("\n\n");
    }

    // Close off any open namespace declarations
    Namespace.openDeclaration(writer, indentLevel, currentNamespace, null);

    // Close of the include guard if necessary
    if ( isHeader() )
    {
      writer.write("#endif // " + guardName + "\n");
    }

  }

  private boolean isHeader ()
  {
    return type != Type.BODY;
  }

  /**
   * Adds a declaration to the file.
   *

   *          The declaration to add
   */
  void addDeclaration ( final Declaration declaration )
  {
    declarations.add(declaration);
    declaration.addDeclaredIn(this);
  }

  /**
   * Get the filename in a form suitable for use in a <code>#include</code>
   * statement. Returns a string containing the filename of the current file
   * contained in the correct delimiters, depending on whether or not it has
   * been flagged as a system file. If the supplied directory is the same as the
   * directory of the file, then just the name is returned, otherwise the full
   * path is returned.
   *

   *          The directory that the file is being included from.
   * @return The filename.
   */
  String getFileSpec ( final File currentDir )
  {
    final File parentDir = getFile().getParentFile();

    String relativeName;

    if ( parentDir != null && parentDir.equals(currentDir) )
    {
      relativeName = getFile().getName();
    }
    else
    {
      relativeName = getFile().getPath();
    }

    return (type == Type.SYSTEM_HEADER ? "<" : "\"") + relativeName + (type == Type.SYSTEM_HEADER ? ">" : "\"");
  }

  /**
   * Gets the set of forward declarations that the declarations and definitions
   * included in the file depend on.
   *
   * @return a set of {@link org.xtuml.masl.cppgen.Declaration}s
   */
  Set<Declaration> getForwardDeclarations ()
  {
    final Set<Declaration> result = new LinkedHashSet<Declaration>(forwardDeclarations);

    for ( final Declaration dec : declarations )
    {
      result.addAll(dec.getForwardDeclarations());
    }

    for ( final Definition def : definitions )
    {
      result.addAll(def.getForwardDeclarations());
    }

    return result;
  }

  /**
   * Gets the set of include files that the declarations and definitions
   * included in the file depend on.
   *
   * @return a set of {@link org.xtuml.masl.cppgen.CodeFile}s
   */
  List<CodeFile> getIncludes ()
  {
    final Set<CodeFile> includeSet = new TreeSet<CodeFile>(includes);

    for ( final Declaration dec : declarations )
    {
      includeSet.addAll(dec.getIncludes());
    }

    for ( final Definition def : definitions )
    {
      includeSet.addAll(def.getIncludes());
    }


    // Don't want to include ourself!
    includeSet.remove(this);

    final List<CodeFile> result = getTopIncludes();

    final Set<CodeFile> alreadyIncluded = new LinkedHashSet<CodeFile>(result);

    for ( final CodeFile include : includeSet )
    {
      if ( !alreadyIncluded.contains(include) )
      {
        for ( final CodeFile include2 : include.getPrerequisiteIncludes() )
        {
          if ( !alreadyIncluded.contains(include2) )
          {
            result.add(include2);
            alreadyIncluded.add(include2);
          }
        }
        result.add(include);
        alreadyIncluded.add(include);
      }
    }

    return result;
  }


  /**
   * Gets a list of include files that must be included before this one.
   *
   * @return a list of files that must be included before this one
   */
  List<CodeFile> getPrerequisiteIncludes ()
  {
    final List<CodeFile> result = new ArrayList<CodeFile>();
    final Set<CodeFile> alreadyIncluded = new LinkedHashSet<CodeFile>();


    for ( final CodeFile include : prerequisiteIncludes )
    {
      if ( !alreadyIncluded.contains(include) )
      {
        for ( final CodeFile include2 : include.getPrerequisiteIncludes() )
        {
          if ( !alreadyIncluded.contains(include2) )
          {
            result.add(include2);
            alreadyIncluded.add(include2);
          }
        }
        result.add(include);
        alreadyIncluded.add(include);
      }

    }

    return result;
  }


  /**
   * Gets a list of include files that must be included at the top of this file.
   *
   * @return a list of include files that must be included at the top of this
   *         file
   */
  List<CodeFile> getTopIncludes ()
  {
    final List<CodeFile> result = new ArrayList<CodeFile>();
    final Set<CodeFile> alreadyIncluded = new LinkedHashSet<CodeFile>();


    for ( final CodeFile include : topIncludes )
    {
      if ( !alreadyIncluded.contains(include) )
      {
        for ( final CodeFile include2 : include.getPrerequisiteIncludes() )
        {
          if ( !alreadyIncluded.contains(include2) )
          {
            result.add(include2);
            alreadyIncluded.add(include2);
          }
        }
        result.add(include);
        alreadyIncluded.add(include);
      }

    }

    return result;
  }

  /**
   * List of declarations contained in this file
   */
  private final List<Declaration> declarations        = new ArrayList<Declaration>();
  /**
   * List of definitions contained in this file
   */
  private final List<Definition>  definitions         = new ArrayList<Definition>();


  /**
   * Forward declarations contained in the file.
   */
  private final Set<Declaration>  forwardDeclarations = new LinkedHashSet<Declaration>();


  /**
   * A set of include files required by this file. These files are in addition
   * to the set of includes needed by the declarations and definitions, which
   * are calculated separately. There would normally be no need to add to these
   * explicitly, unless creating a composite header file which includes lots of
   * others for the sole purpose of simplifiying the include list of clients.
   */
  private final Set<CodeFile>  includes             = new LinkedHashSet<CodeFile>();

  final private Type        type;

  /**
   * List of include files that must be included before this one
   */
  private final List<CodeFile> prerequisiteIncludes = new ArrayList<CodeFile>();

  /**
   * List of include files that must be included before any others at the top of
   * this file.
   */
  private final List<CodeFile> topIncludes          = new ArrayList<CodeFile>();

  @Override
  public Set<FileGroup> getDependencies ()
  {
    final Set<FileGroup> dynamicDeps = getIncludes().stream().map(f -> f.getParent()).filter(g->g != getParent()).collect(Collectors.toSet());

    return Sets.union(dependencies,dynamicDeps);
  }

  public void addDependency ( final FileGroup dependent )
  {
    dependencies.add(dependent);
  }

  private final Set<FileGroup> dependencies = new LinkedHashSet<FileGroup>();

  public boolean isPublicHeader ()
  {
    return type == Type.PUBLIC_HEADER || type == Type.INTERFACE_HEADER;
  }

  public boolean isBodyFile ()
  {
    return type == Type.BODY;
  }

}
