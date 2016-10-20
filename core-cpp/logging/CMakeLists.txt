#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
find_package(Poco REQUIRED COMPONENTS XML Foundation)


simple_add_shared_library(
  NAME     logging           
  SOURCES 
           FilterChannel.cc  
           Logger.cc         
           XMLExtractor.cc   
  LINKS   
           Poco::Foundation  
           Poco::XML         
  INCLUDES 
           logging/Logger.hh 
           logging/Logging.hh 
  EXPORT   MaslCore
  )

install (
  FILES 
    share/full_example.xml
    share/simple_example.xml
    share/configuration.xsd
  DESTINATION share/logging )
