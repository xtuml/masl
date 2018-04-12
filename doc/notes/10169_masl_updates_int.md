---

This work is licensed under the Creative Commons CC0 License

---

# MASL compiler issues
### xtUML Project Implementation Note

### 1. Abstract

During the conversion of GPS Watch to MASL, several MASL compiler issues were
observed. This issue serves as the parent task for them.

The following issues are covered in this note:
- #10170 MASL compiler generates a copyright notice
- #10171 Deprecate 'function' keyword
- #10172 Header is not visible when using types from another domain
- #10173 Hand craft code handling with the MASL compile

### 2. Document References

<a id="2.1"></a>2.1 [#10169 MASL compiler issues](https://support.onefact.net/issues/10169) Parent issue  
<a id="2.2"></a>2.2 [#10170 MASL compiler generates a copyright notice](https://support.onefact.net/issues/10170) Sub issue  
<a id="2.3"></a>2.3 [#10171 Deprecate 'function' keyword](https://support.onefact.net/issues/10171) Sub issue  
<a id="2.4"></a>2.4 [#10172 Header is not visible when using types from another domain](https://support.onefact.net/issues/10172) Sub issue  
<a id="2.5"></a>2.5 [#10173 Hand craft code handling with the MASL compile](https://support.onefact.net/issues/10173) Sub issue  
<a id="2.6"></a>2.6 [#8808 deprecate 'function' keyword in MASL](https://support.onefact.net/issues/8808)  

### 3. Background

The reader should briefly review the discussion in each sub issue. In addition,
read the following background information:

3.1 Copyright notice

Currently, the MASL compiler generates a file header containing a hard coded
copyright notice into every generated C++ file. Obviously, this behavior is
undesirable for any new MASL users, however the ability to generate a copyright
notice into each file is a valuable feature. The current capabilities should
continue to be supported, but be extended to have a configurable message.

3.1.1 Current mechanism

The current mechanism has a few characteristics that are valuable that should be
maintained. 

3.1.1.1 Copyright notices are generated with the current four digit year value
inserted.  
3.1.1.2 A new value for the year is not generated into the file if that is the
only change made (i.e. if the project is regenerated a year later and no changes
have been made to the source models, the generated artifacts are not all changed
with updated copyright year values).  

3.1.2 `.old` files

In conjunction with 3.1.1, the MASL compiler implements a mechanism to overwrite
generated files while maintaining the previous version in a file called
`<filename>.old`. If the generation would not result in any changes to the file,
the file is not touched and no `.old` file is created. This is convenient for
the downstream build tools as the file timestamps are not modified.

As mentioned above, the copyright year is ignored when an existing file is
compared with a new generated file, therefore if the copyright year is the only
change, no updated file is generated (and the original copyright year value
remains in the file).

3.2 `function` keyword

In older versions of MASL, the `function` keyword was used with slightly
different semantics to the `service` keyword. Functions returned values while
services did not. File naming was slightly different (`.fn` extension for
function definition files and `.svc` extension for service definition files).
The MASL architecture also treats them slightly different in the way they are
registered in a system (a corollary to this is that functions are not allowed in
terminators).

In issue #8808 [[2.6]](#2.6), the `function` keyword was officially deprecated
in the xtUML tools for MASL, using instead the `service` keyword for both
functions and services, and naming files accordingly. The compiler needs to be
brought up to speed with this change as currently the existence of a "service"
which returns a value will result in a parse error.

3.3 Interface headers for MetaData

A bug exists in the compiler where invalid C++ is generated for domains which
refer to types in other domains. Please read the discussion in the sub issue
[[2.4]](#2.4).

3.4 Custom code

The MASL compiler provides a mechanism for including handwritten C++ code in the
build of a domain or project, however it is not sufficient for the needs of
BridgePoint users.

A typical MASL domain will consist of a directory (usually named something like
`<domain_name>_OOA` which contains a domain model file (`<domain_name>.mod`) and
any associated service and state definition files. When the MASL compiler runs,
C++ code is generated along with supporting build files (the default is CMake).
In the generated build files a statement is automatically generated to include
`custom/custom.cmake` in the build. This path is relative to the domain
directory. To include handwritten C++ in the build a user is expected to create
a directory called `custom` within the domain directory. Handwritten source and
header files should be stored in this directory and a `custom.cmake` file should
be written which defines how these source files are hooked into the larger
build.

This mechanism does not work for BridgePoint users. The xtUML to MASL exporter
tool generates MASL source files into a directory called `masl` at the root of
an xtUML Eclipse project. The contents of this directory get overwritten each
time an export is performed, therefore if a user were to create a `custom`
directory, it would get removed each time the export occurs. Furthermore,
BridgePoint users are more accustomed with using the `gen` folder at the root of
the project for storing handwritten code. The current mechanism can be left in
place, but a new mechanism should be introduced which is more convenient and
natural for BridgePoint users.

### 4. Requirements

4.1 The MASL compiler shall provide a configurable mechanism for including
copyright notices in generated files.  
4.1.1 The default behavior shall be to exclude any type of copyright notice.  
4.1.2 The implemented mechanism shall be configurable to support the existing
behavior including special handling of copyright year.  

4.2 The `function` keyword shall be deprecated.  
4.2.1 The `function` keyword shall continue to be supported as a synonym of the
`service` keyword to ensure backwards compatibility.  

4.3 For models which reference types defined in other domains, the MASL compiler
shall generate valid (able to compile) C++ code.  

4.4 The MASL compiler shall provide a mechanism for including custom hand
written code.  
4.4.1 The mechanism shall allow a user to store all custom files in the `gen/`
folder of an xtUML project.  

### 5. Work Required

5.1 Copyright notice mechanism  
5.1.1 Create a new class `CopyrightUtil` with utility methods that accept a file
name and return a string copyright notice.  
5.1.1.1 `getCopyrightNotice` returns the string contents of the specified file
with leading and trailing whitespace trimmed. Additionally, any instance of the
exact string "yyyy" in the file will be replaced by the four decimal digits
representing the current calendar year.  
5.1.1.2 `getRawCopyrightNotice` returns the same as `getCopyrightNotice` except
it does not perform the find and replace for the "yyyy" string.  
5.1.2 Add a command line argument "-copyright" to the `CommandLine` class.
Introduce accessors to get both the normal copyright notice and the raw
copyright notice. The default value if the argument is not passed is `null`.  
5.1.3 Modify the `CodeFile` class and the `TextFile` class to get the copyright
notice from the command line arguments. If `null` is returned for the copyright
notice, do not include a notice.  
5.1.4 Modify the `BuildSet` class to get the copyright notice from the command
line arguments for the purpose of pattern matching. This file is responsible for
the mechanism described in section 3.1.1.2. It simply matches the text of the
copyright in the old code file and replaces the year value with the current year
before comparing with the newly generated code. For simplicity, this
implementation assumes that the copyright notice will contain only one instance
of "yyyy". If it contains the year more than once, the feature to prevent
unnecessary regenerations of the file will be bypassed.  

5.2 `function` keyword  
5.2.1 Remove all references to functions in the parse grammar. Update the
service production rules to parse an optional return type.  
5.2.2 Remove the `FUNCTION` lexer token. Add an alternative to the `SERVICE`
token to match "function". Note that this makes "function" and "service"
synonymous to the parser. The xtUML MASL tools only use "service" however making
them synonyms allows old models that still use the `function` keyword to
continue to be compilable.  
5.2.3 Update metamodel details which refer specifically to functions to refer to
services.  

5.3 MetaModel header fix  
5.3.1 In `metadata.DomainTranslator`, add a new interface library called
"<domain_name>_common_headers". Add the header files to this library instead of
creating private headers.  
5.3.2 In the CMake build translator add an "EXPORT" declaration for interface
libraries. This was necessary for the build of the utility domains and the
example domains in the `masl` repository itself.  

5.4 Custom code  
5.4.1 Add a command line argument "-custombuildfile" to the `CommandLine` class.
Introduce accessors to get the string value.  
5.4.2 Add a section in the CMake translator class to generate an optional
include of the file specified by the new command line argument. This allows a
user to specify a `.cmake` file to include via the MASL compiler command line
options. If the argument is not included the current behavior is maintained.  

### 6. Implementation Comments

None.

### 7. Unit Test

7.0 Preparation  
7.0.1 Download the testing models `test_models.zip` from the parent issue
[[2.1]](#2.1). Unzip them.  
7.0.2 Clone the `masl` repository. These tests require the
`leviathan747/10169_masl_updates` branch to be cloned and checked out at
`~/git/masl`.  

7.1 Copyright notice test  
7.1.1 Navigate to the `Copyright_OOA` directory.  
7.1.2 Execute `generate-masl.sh`. Open some generated files in `build/src` and
`build/include`. Verify that no copyright notice was generated.  
7.1.3 Open `generate-masl.sh` in an editor and uncomment the "-copyright"
argument.  
7.1.4 Execute `generate-masl.sh` again. Open some generated files in `build/src`
and `build/include` and verify that a copyright notice has been generated.  
7.1.5 Open `build/src/__Copyright.cc` in an editor and modify the copyright year
to be "2017". Execute `generate-masl.sh` again.  
7.1.6 Verify that no files have been regenerated and that the copyright year in
`__Copyright.cc` has not been changed.  
7.1.7 Open `build/src/__Copyright.cc` in an editor and modify the file by adding
a new comment line anywhere in the file. Execute `generate-masl.sh` again.  
7.1.8 Verify that `__Copyright.cc` has been updated, the comment line has been
removed, and the year has been changed back to the current year. Verify that the
old version of `__Copyright.cc` still exists in `__Copyright.cc.old`.  

7.2 `function` keyword test  
7.2.1 Navigate to the `Function_OOA` directory.  
7.2.2 Execute `generate-masl.sh`. Verify that code generation completes with no
parse errors.  
7.2.3 Build the project by executing `make`.  
7.2.4 Run the executable with the command `./build/Function_transient_standalone
-postinit test.sch`. Verify the following output:  
```
Starting Process Function
Hello, World!
```

7.3 MetaData header test  
7.3.1 Navigate to the `MetaDataHeader_OOA` directory.  
7.3.2 Execute `generate-masl.sh`.  
7.3.3 Build the project by executing `make`.  
7.3.4 Run the executable with the command
`./build/MetaDataHeader/MetaDataHeader_transient_standalone -postinit test.sch`.
Verify the following output:  
```
Starting Process MetaDataHeader
ONE
```

7.4 Custom code test  
7.4.1 Navigate to the `CustomCode_OOA` directory.  
7.4.2 Execute `generate-masl.sh`.  
7.4.3 Build the project by executing `make`.  
7.4.4 Run the executable with the command
`./build/CustomCode_transient_standalone -postinit test.sch`.
Verify the following output:  
```
Starting Process CustomCode
Default implementation
```
7.4.5 Delete `bar.scn`  
7.4.6 Execute `generate-masl.sh` again.  
7.4.7 Build the project by executing `make`. Verify that the project fails to
build.  
7.4.8 Open `generate-masl.sh` in an editor and uncomment the "-custombuildfile"
argument.  
7.4.9 Execute `generate-masl.sh` again.  
7.4.10 Build the project by executing `make`. Verify that the project
successfully builds.  
7.4.11 Run the executable with the command
`./build/CustomCode_transient_standalone -postinit test.sch`.
Verify the following output:  
```
Starting Process CustomCode
Custom implementation
```

### 8. User Documentation

This engineering document shall be sufficient documentation of these changes.  

### 9. Code Changes

Fork/Repository: leviathan747/masl  
Branch: 10169_masl_updates  

<pre>

 core-java/src/main/antlr/org/xtuml/masl/antlr/MaslP.g                             | 111 ++++++++++++---------------------------------------------------------------------
 core-java/src/main/java/org/xtuml/masl/CommandLine.java                           |  29 ++++++++++++++++++++++
 core-java/src/main/java/org/xtuml/masl/cppgen/CodeFile.java                       |  12 +++------
 core-java/src/main/java/org/xtuml/masl/cppgen/TextFile.java                       |   7 +++---
 core-java/src/main/java/org/xtuml/masl/metamodel/CodeWriter.java                  |   2 +-
 core-java/src/main/java/org/xtuml/masl/metamodelImpl/domain/DomainService.java    |   4 ---
 core-java/src/main/java/org/xtuml/masl/metamodelImpl/object/ObjectService.java    |   2 +-
 core-java/src/main/java/org/xtuml/masl/translate/build/BuildSet.java              |  18 ++++++++------
 core-java/src/main/java/org/xtuml/masl/translate/cmake/BuildInterfaceLibrary.java |   5 +++-
 core-java/src/main/java/org/xtuml/masl/translate/cmake/Translator.java            |   7 ++++++
 core-java/src/main/java/org/xtuml/masl/translate/metadata/DomainTranslator.java   |   7 +++++-
 core-java/src/main/java/org/xtuml/masl/utils/CopyrightUtil.java                   |  52 ++++++++++++++++++++++++++++++++++++++
 doc/notes/10169_masl_updates_int.md                                               | 287 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 13 files changed, 419 insertions(+), 124 deletions(-)

</pre>

### End
