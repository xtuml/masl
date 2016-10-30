#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
find_package(Poco REQUIRED COMPONENTS XML)

simple_add_shared_library(
  NAME    CodeCoverage 
  SOURCES 
          CodeCoverage.cc
  LINKS
          MetaData
          Poco::XML
  EXPORT  MaslCore
  )

simple_add_executable (
  NAME    CodeCoverageReport
  SOURCES
          Report.cc
  LINKS
          Poco::XML
  INSTALL
  )

install ( 
  PROGRAMS 
    scripts/merge_coverage 
    scripts/coverage_summary
  DESTINATION bin
  )
