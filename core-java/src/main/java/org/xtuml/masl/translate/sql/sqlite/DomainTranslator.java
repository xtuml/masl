/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.sqlite;

import com.google.common.collect.Iterables;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.main.ASN1;
import org.xtuml.masl.translate.sql.main.Database;
import org.xtuml.masl.translate.sql.main.SqlFrameworkTranslator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Alias("Sqlite")
@Default
public class DomainTranslator extends SqlFrameworkTranslator {

    private CodeFile sqliteMbusCodeFile;

    public static DomainTranslator getInstance(final Domain domain) {
        return getInstance(DomainTranslator.class, domain);
    }

    private final Library standaloneDeps;
    private final Library standaloneExecutable;

    private DomainTranslator(final Domain domain) {
        super(domain);

        database = new Database(new SqliteTraits());

        library =
                new SharedLibrary(getMainDomainTranslator().getLibrary().getName() +
                                  "_" +
                                  database.getDatabaseTraits().getLibrarySuffix()).inBuildSet(getMainDomainTranslator().getBuildSet()).withCCDefaultExtensions();

        library.addDependency(getMainDomainTranslator().getLibrary());
        getLibrary().addDependency(SqliteDatabase.sqlitelib);
        getLibrary().addDependency(ASN1.library);

        if (getProperties().getProperty("standalone", "true") == "true" &&
            getMainDomainTranslator().getProperties().getProperty("standalone", "true") == "true") {
            standaloneDeps =
                    new InterfaceLibrary(getMainDomainTranslator().getLibrary().getName() +
                                         "_" +
                                         database.getDatabaseTraits().getLibrarySuffix() +
                                         "_standalone_deps").inBuildSet(getMainDomainTranslator().getBuildSet());

            standaloneExecutable =
                    new Executable(getMainDomainTranslator().getLibrary().getName() +
                                   "_" +
                                   database.getDatabaseTraits().getLibrarySuffix() +
                                   "_standalone").asPrivate().inBuildSet(getMainDomainTranslator().getBuildSet()).withCCDefaultExtensions();
            standaloneExecutable.addDependency(standaloneDeps);
            standaloneDeps.addDependency(library);
        } else {
            standaloneDeps = null;
            standaloneExecutable = null;
        }
    }

    @Override
    public void translate() {
        super.translate();
        if (Iterables.isEmpty(library.getBodyFiles())) {
            // Some build systems complain if there's not at least one file in a library
            library.createBodyFile("sqlite_dummy");
        }
        if (standaloneExecutable != null) {
            standaloneExecutable.includeGroup(getMainDomainTranslator().getStandaloneExecutableSkeleton());

            for (final Domain refDomain : domain.getReferencedInterfaces()) {
                standaloneDeps.addDependency(DomainTranslator.getInstance(refDomain).standaloneDeps);
            }
        }
    }

    public Library getStandaloneExecutable() {
        return standaloneExecutable;
    }

    public Collection<CodeFile> getAdditionalSource() {
        Collection<CodeFile> files = Collections.emptyList();
        if (sqliteMbusCodeFile != null) {
            files = List.of(sqliteMbusCodeFile);
        }
        return files;
    }

    @Override
    public Library getLibrary() {
        return library;
    }

    @Override
    public Database getDatabase() {
        return database;
    }

    private final Library library;
    private final Database database;

}
