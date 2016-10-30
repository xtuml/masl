//
// File: DomainTranslator.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.translate.sql.sqlite;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Executable;
import org.xtuml.masl.cppgen.InterfaceLibrary;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.SharedLibrary;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.main.ASN1;
import org.xtuml.masl.translate.sql.main.Database;
import org.xtuml.masl.translate.sql.main.SqlFrameworkTranslator;

import com.google.common.collect.Iterables;


@Alias("Sqlite")
@Default
public class DomainTranslator extends SqlFrameworkTranslator
{


  private CodeFile               sqliteMbusCodeFile;

  public static DomainTranslator getInstance ( final Domain domain )
  {
    return getInstance(DomainTranslator.class, domain);
  }

  private final Library standaloneDeps;
  private final Library standaloneExecutable;

  private DomainTranslator ( final Domain domain )
  {
    super(domain);

    database = new Database(new SqliteTraits());

    library = new SharedLibrary(getMainDomainTranslator().getLibrary().getName() + "_"
                                                                 + database.getDatabaseTraits().getLibrarySuffix()).inBuildSet(getMainDomainTranslator().getBuildSet()).withCCDefaultExtensions();

    library.addDependency(getMainDomainTranslator().getLibrary());
    getLibrary().addDependency(SqliteDatabase.sqlitelib);
    getLibrary().addDependency(ASN1.library);

    if ( getProperties().getProperty("standalone", "true") == "true" &&
         getMainDomainTranslator().getProperties().getProperty("standalone", "true") == "true" )
    {
      standaloneDeps = new InterfaceLibrary(getMainDomainTranslator().getLibrary().getName() + "_"
                                                                + database.getDatabaseTraits().getLibrarySuffix()
                                                                + "_standalone_deps").inBuildSet(getMainDomainTranslator().getBuildSet());

      standaloneExecutable = new Executable(getMainDomainTranslator().getLibrary().getName() + "_"
                                                                + database.getDatabaseTraits().getLibrarySuffix()
                                                                + "_standalone").asPrivate().inBuildSet(getMainDomainTranslator().getBuildSet()).withCCDefaultExtensions();
      standaloneExecutable.addDependency(standaloneDeps);
      standaloneDeps.addDependency(library);
    }
    else
    {
      standaloneDeps = null;
      standaloneExecutable = null;
    }
  }

  @Override
  public void translate ()
  {
    super.translate();
    if ( Iterables.isEmpty(library.getBodyFiles()) )
    {
      // Some build systems complain if there's not at least one file in a library
      library.createBodyFile("sqlite_dummy");
    }
    if ( standaloneExecutable != null )
    {
      standaloneExecutable.includeGroup(getMainDomainTranslator().getStandaloneExecutableSkeleton());

      for ( final Domain refDomain : domain.getReferencedInterfaces() )
      {
        standaloneDeps.addDependency(DomainTranslator.getInstance(refDomain).standaloneDeps);
      }
    }
  }

  public Library getStandaloneExecutable ()
  {
    return standaloneExecutable;
  }

  public Collection<CodeFile> getAdditionalSource ()
  {
    Collection<CodeFile> files = Collections.emptyList();
    if ( sqliteMbusCodeFile != null )
    {
      files = Arrays.asList(sqliteMbusCodeFile);
    }
    return files;
  }

  @Override
  public Library getLibrary ()
  {
    return library;
  }


  @Override
  public Database getDatabase ()
  {
    return database;
  }

  private final Library library;
  private final Database  database;


}
