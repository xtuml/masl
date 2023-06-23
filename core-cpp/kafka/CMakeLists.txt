add_library(rdkafka SHARED IMPORTED)
set_property(TARGET rdkafka PROPERTY IMPORTED_LOCATION "/opt/rdkafka/lib/librdkafka.so")
include_directories("/opt/rdkafka/include")
add_library(cppkafka SHARED IMPORTED)
set_property(TARGET cppkafka PROPERTY IMPORTED_LOCATION "/opt/cppkafka/lib/libcppkafka.so")
include_directories("/opt/cppkafka/include")

simple_add_shared_library (
  NAME      Kafka
  SOURCES   
            BufferedIO.cc
            Consumer.cc
            Kafka.cc
            ProcessHandler.cc
            Producer.cc
            ServiceHandler.cc
  LINKS     swa
            cppkafka
            rdkafka
            uuid
  EXPORT    MaslCore
  INCLUDES  
            kafka/BufferedIO.hh
            kafka/Consumer.hh
            kafka/Kafka.hh
            kafka/ProcessHandler.hh
            kafka/Producer.hh
            kafka/ServiceHandler.hh
  )
