#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
simple_add_shared_library(
  NAME      Events
  SOURCES
            EventEncoder.cc
            EventCollector.cc
            EventXmlEncoder.cc
            EventInspectorWriter.cc
            EventConsoleWriter.cc
            EventFileWriter.cc
            EventContext.cc
            EventFinishedContext.cc
            ObjectServiceContext.cc
            DomainServiceContext.cc
            StateServiceContext.cc
            ProcessContext.cc
            TerminatorServiceContext.cc
  LINKS
            sockets
            MetaData
  EXPORT    MaslCore
  )

simple_add_executable (
  NAME    symbolCheck
  SOURCES symbolCheck.cc
  LINKS
            Events
            swa
  )
    
