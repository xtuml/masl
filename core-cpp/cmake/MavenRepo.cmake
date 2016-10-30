#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
function(find_java_package)

  cmake_parse_arguments(ARGS 
    "" "NAME" "ARTIFACTS"
    ${ARGN})

  set(outputDir ${CMAKE_BINARY_DIR}/cmake-java)
  set(pomFile ${outputDir}/${ARGS_NAME}.xml)
  file(MAKE_DIRECTORY ${outputDir})

 file(WRITE ${pomFile} 
"
<?xml version='1.0' encoding='UTF-8'?>
<project xmlns='http://maven.apache.org/POM/4.0.0' 
         xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' 
         xsi:schemaLocation='http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd'>
  <modelVersion>4.0.0</modelVersion>
  <packaging>pom</packaging>
  <groupId>none</groupId>
  <artifactId>none</artifactId>
  <version>0</version>
  <dependencies>
" )
 

  foreach(artifact IN LISTS ARGS_ARTIFACTS)

    string(REPLACE ":" ";" artifactItems ${artifact})


    list(GET artifactItems 0 groupId)
    list(GET artifactItems 1 artifactId)
    list(GET artifactItems 2 version)

    file(APPEND ${pomFile}
"
    <dependency>
      <groupId>${groupId}</groupId>
      <artifactId>${artifactId}</artifactId>
      <version>${version}</version>
    </dependency>
" )

  endforeach()
  
    
 file(APPEND ${pomFile} 
"
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>2.10</version>
        <configuration>
          <overWriteSnapshots>true</overWriteSnapshots>
          <includeScope>runtime</includeScope>
          <outputDirectory>.</outputDirectory>
          <outputFile>${ARGS_NAME}.classpath</outputFile>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
" )

  set(mvn_goals "dependency:build-classpath")

  execute_process(
    COMMAND mvn ${mvn_goals} -B -q -f ${pomFile}
    WORKING_DIRECTORY ${outputDir}
    RESULT_VARIABLE mvn_success
    )
  
  if( ${mvn_success} EQUAL 0 )
    message(STATUS "found java module ${ARGS_NAME}")
    set(${name}_FOUND TRUE PARENT_SCOPE)
    file(READ ${outputDir}/${ARGS_NAME}.classpath ${ARGS_NAME}_CLASSPATH)
    set(${ARGS_NAME}_CLASSPATH "${${ARGS_NAME}_CLASSPATH}" PARENT_SCOPE)
  else()
    message(STATUS "java module ${ARGS_NAME} not found")
  endif()  

endfunction(find_java_package)




