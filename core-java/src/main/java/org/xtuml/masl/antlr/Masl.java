//
// Filename : Masl.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.antlr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.xtuml.masl.CommandLine;
import org.xtuml.masl.error.ErrorListener;
import org.xtuml.masl.error.ErrorLog;
import org.xtuml.masl.error.ErrorType;
import org.xtuml.masl.error.MaslError;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.domain.DomainService;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminator;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminatorService;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectService;
import org.xtuml.masl.metamodelImpl.project.Project;
import org.xtuml.masl.metamodelImpl.project.ProjectDomain;
import org.xtuml.masl.metamodelImpl.project.ProjectTerminator;
import org.xtuml.masl.metamodelImpl.project.ProjectTerminatorService;
import org.xtuml.masl.metamodelImpl.statemodel.State;
import org.xtuml.masl.translate.build.BuildSet;


public class Masl
{

  public static final String  PROJECT_DEF_EXTENSION      = ".prj";
  public static final String  DOMAIN_MODEL_EXTENSION     = ".mod";
  public static final String  DOMAIN_DEPENDENT_EXTENSION = ".dep";
  public static final String  DOMAIN_DIRECTORY_EXTENSION = "_OOA";

  private static final String DOMAIN_INTERFACE_EXTENSION = ".int";

  /**
   * Constructs a new Masl
   */
  public Masl ( final List<String> domainPaths )
  {
    this.domainPaths = domainPaths;
  }


  // Methods
  // -------

  public File getInterfaceFile ( final String domainName )
  {
    final File result =  findFile(domainName, domainName + DOMAIN_INTERFACE_EXTENSION);
    return result == null ? getModelFile(domainName) : result;
  }

  public File getModelFile ( final String domainName )
  {
    return findFile(domainName, domainName + DOMAIN_MODEL_EXTENSION);
  }


  private static String getMd5Sum ( final File file ) throws IOException
  {
    try(final InputStream input = new BufferedInputStream(new FileInputStream(file)))
    {
      final MessageDigest md = MessageDigest.getInstance("MD5");
      final byte[] bytes = new byte[1024];
      int read = input.read(bytes);
      while ( read != -1 )
      {
        md.update(bytes, 0, read);
        read = input.read(bytes);
      }
      final String hex = new BigInteger(1, md.digest()).toString(16);
      return "00000000000000000000000000000000".substring(hex.length()) + hex;
    }
    catch ( final NoSuchAlgorithmException e )
    {
      assert false : "MD5 algorithm not found";
      return null;
    }
  }

  public void parseAction ( final ProjectTerminatorService service ) throws RecognitionException, IOException
  {

    final File file = new File(sourceDir,service.getFileName());

    // Check file for current directory or fully qualified.
    if ( file.canRead() )
    {
      buildSet.addFileDependent(file);

      service.setFileHash(getMd5Sum(file));
      new Walker(this, file).projectTerminatorServiceDefinition(service);
    }
  }

  public void parseAction ( final DomainService service ) throws RecognitionException, IOException
  {

    final File file = new File(sourceDir,service.getFileName());

    // Check file for current directory or fully qualified.
    if ( file.canRead() )
    {
      buildSet.addFileDependent(file);

      service.setFileHash(getMd5Sum(file));
      new Walker(this, file).domainServiceDefinition(service);
    }
  }

  public void parseAction ( final DomainTerminatorService service ) throws RecognitionException, IOException
  {

    final File file = new File(sourceDir,service.getFileName());

    // Check file for current directory or fully qualified.
    if ( file.canRead() )
    {
      buildSet.addFileDependent(file);

      service.setFileHash(getMd5Sum(file));
      new Walker(this, file).terminatorServiceDefinition(service);
    }
  }


  public void parseAction ( final ObjectService service ) throws RecognitionException, IOException
  {

    final File file = new File(sourceDir,service.getFileName());

    // Check file for current directory or fully qualified.
    if ( file.canRead() )
    {
      buildSet.addFileDependent(file);

      service.setFileHash(getMd5Sum(file));
      new Walker(this, file).objectServiceDefinition(service);
    }
  }

  public void parseAction ( final State state ) throws RecognitionException, IOException
  {

    final File file = new File(sourceDir, state.getFileName());

    // Check file for current directory or fully qualified.
    if ( file.canRead() )
    {
      buildSet.addFileDependent(file);

      state.setFileHash(getMd5Sum(file));
      new Walker(this, file).stateDefinition(state);
    }
  }


  public void parseActions ( final Domain domain ) throws RecognitionException, IOException
  {
    for ( final DomainService service : domain.getServices() )
    {
      parseAction(service);
    }

    for ( final DomainTerminator term : domain.getTerminators() )
    {
      for ( final DomainTerminatorService service : term.getServices() )
      {
        parseAction(service);
      }
    }

    for ( final ObjectDeclaration obj : domain.getObjects() )
    {
      for ( final ObjectService service : obj.getServices() )
      {
        parseAction(service);
      }

      for ( final State state : obj.getStates() )
      {
        parseAction(state);
      }

    }
  }

  public Project parseProject ( final File prjFile ) throws RecognitionException, IOException
  {
    System.out.println("Parsing project");

    final long millis = System.currentTimeMillis();
    sourceDir = prjFile.getParentFile();
    final Project project = parsePackage(prjFile);
    parseTerminators(project);

    System.out.println("Parsed (" + (System.currentTimeMillis() - millis) / 1000.0 + "secs)");
    return project;
  }

  private void parseTerminators ( final Project project ) throws RecognitionException, IOException
  {
    for ( final ProjectDomain domain : project.getDomains() )
    {
      for ( final ProjectTerminator term : domain.getTerminators() )
      {
        for ( final ProjectTerminatorService service : term.getServices() )
        {
          parseAction(service);
        }
      }
    }
  }

  public Domain parseDomain ( final File modFile ) throws RecognitionException, IOException
  {
    System.err.println("Parsing model");
    final long millis = System.currentTimeMillis();

    sourceDir = modFile.getParentFile();
    buildSet.addFileDependent(modFile);

    Domain domain = null;
    if ( CommandLine.INSTANCE.getDomainParser() != null )
    {
      try
      {
        domain = Class.forName(CommandLine.INSTANCE.getDomainParser())
                      .asSubclass(DomainParser.class)
                      .getDeclaredConstructor(Masl.class,
                                              File.class)
                      .newInstance(this, modFile)
                      .getDomain();
      }
      catch ( final ReflectiveOperationException | SecurityException e )
      {
        e.printStackTrace();
      }
    }
    else
    {
      domain = parseModel(modFile);
    }

    parseActions(domain);

    System.err.println("Parsed (" + (System.currentTimeMillis() - millis) / 1000.0 + "secs)");

    return domain;
  }

  public Domain parseInterface ( final String domainName ) throws RecognitionException, IOException
  {
    final File ifaceFile = getInterfaceFile(domainName);

    if ( ifaceFile == null )
    {
      // Could not locate the required interface file in the
      // current directory or the search path.
      new SemanticError(SemanticErrorCode.InterfaceNotFound, Position.getPosition(domainName), domainName).report();
      return null;
    }
    else
    {
      final Domain domain = parseModel(ifaceFile);
      domain.setInterface(true);
      if (domain.getPragmas().hasPragma(PragmaList.BUILD_SET)) {
        BuildSet.addBuildSet(domain, new BuildSet(domain.getPragmas().getValue(PragmaList.BUILD_SET)));
      }
      return domain;
    }
  }

  public Domain parseProjectDomain ( final String domainName ) throws RecognitionException, IOException
  {
    File modelFile = getModelFile(domainName);

    if ( modelFile == null )
    {
      System.err.println("Warning - "
                         + domainName
                         + DOMAIN_MODEL_EXTENSION
                         + " not found. Using "
                         + domainName
                         + DOMAIN_INTERFACE_EXTENSION);
      modelFile = getInterfaceFile(domainName);
    }

    if ( modelFile == null )
    {
      // Could not locate the required model file in the
      // current directory or the search path.
      new SemanticError(SemanticErrorCode.ModelNotFound, Position.getPosition(domainName), domainName).report();
      return null;
    }
    else
    {
      final Domain domain = parseModel(modelFile);
      if (domain.getPragmas().hasPragma(PragmaList.BUILD_SET)) {
        BuildSet.addBuildSet(domain, new BuildSet(domain.getPragmas().getValue(PragmaList.BUILD_SET)));
      }
      return domain;
    }
  }


  public interface DomainParser
  {

    Domain getDomain ();
  }

  public Domain parseModel ( final File file ) throws RecognitionException, IOException
  {
    buildSet.addFileDependent(file);
    return new Walker(this, file).domainDefinition();
  }

  public Project parsePackage ( final File file ) throws RecognitionException, IOException
  {
    buildSet.addFileDependent(file);
    return new Walker(this, file).projectDefinition();
  }



  public File findFile ( final String domainName, final String fileName )
  {
    File foundFile = null;
    for ( final String currentPath : domainPaths )
    {
      final File file = new File(currentPath + System.getProperty("file.separator") + fileName);
      if ( file.canRead() )
      {
        foundFile = file;
        break;
      }
    }

    if ( foundFile == null )
    {
      for ( final String currentPath : domainPaths )
      {
        final File file = new File(currentPath
                                   + System.getProperty("file.separator")
                                   + domainName
                                   + DOMAIN_DIRECTORY_EXTENSION
                                   + System.getProperty("file.separator")
                                   + fileName);
        if ( file.canRead() )
        {
          foundFile = file;
          break;
        }
      }
    }


    // As a last resort, type looking at the same level as the current directory
    if ( foundFile == null )
    {
      final File file = new File(sourceDir.getParentFile()
                                 + System.getProperty("file.separator")
                                 + fileName);
      if ( file.canRead() )
      {
        foundFile = file;
      }
    }

    if ( foundFile == null )
    {
      final File file = new File(sourceDir.getParentFile()
                                 + System.getProperty("file.separator")
                                 + domainName
                                 + DOMAIN_DIRECTORY_EXTENSION
                                 + System.getProperty("file.separator")
                                 + fileName);
      if ( file.canRead() )
      {
        foundFile = file;
      }

    }

    if ( foundFile != null )
    {
      buildSet.addFileDependent(foundFile);
    }

    return foundFile;

  }

  static public File getDependentFile ( final String domainName )
  {
    File file = null;
    for ( final String currentPath : CommandLine.INSTANCE.getDomainPaths() )
    {
      file = new File(currentPath + System.getProperty("file.separator") + domainName + Masl.DOMAIN_DEPENDENT_EXTENSION);
      if ( file.canRead() )
      {
        break;
      }
      else
      {
        file = null;
      }
    }
    return file;
  }


  public int warningCount ()
  {
    return errorCounter.getCount(ErrorType.Warning);
  }

  public int errorCount ()
  {
    return errorCounter.getCount(ErrorType.Error);
  }


  private static class ErrorCounter
      implements ErrorListener
  {

    private ErrorCounter ()
    {
      ErrorLog.getInstance().addErrorListener(this);
    }

    private static Map<ErrorType, Integer> counts = new EnumMap<ErrorType, Integer>(ErrorType.class);

    private int getCount ( final ErrorType type )
    {
      final Integer result = counts.get(type);
      return result == null ? 0 : result;
    }

    @Override
    public void errorReported ( final MaslError error )
    {
      final ErrorType type = error.getErrorCode().getErrorType();

      counts.put(type, getCount(type) + 1);
    }

  }


  private static class ErrorReporter
      implements ErrorListener
  {

    private ErrorReporter ()
    {
      ErrorLog.getInstance().addErrorListener(this);
    }

    @Override
    public void errorReported ( final MaslError error )
    {
      final ErrorType type = error.getErrorCode().getErrorType();

      switch ( type )
      {
        case Error:
          System.err.println(error.getMessage() + "\n");
          break;
        case Warning:
          System.err.println(error.getMessage() + "\n");
          break;
        case Info:
          System.out.println(error.getMessage() + "\n");
          break;
        case Ignore:
          break;
      }
    }

  }



  public BuildSet getBuildSet ()
  {
    return buildSet;
  }

  private final ErrorCounter  errorCounter  = new ErrorCounter();

  @SuppressWarnings("unused")
  private final ErrorReporter errorReporter = new ErrorReporter();

  private final List<String>  domainPaths;
  private final BuildSet buildSet = new BuildSet(null);

  private File sourceDir;
}
