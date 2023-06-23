find_package(CppKafka CONFIG REQUIRED QUIET)
find_package(RdKafka CONFIG REQUIRED QUIET)
find_package(libuuid REQUIRED)

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
            CppKafka::cppkafka
            RdKafka::rdkafka
            libuuid::libuuid
  EXPORT    MaslCore
  INCLUDES  
            kafka/BufferedIO.hh
            kafka/Consumer.hh
            kafka/Kafka.hh
            kafka/ProcessHandler.hh
            kafka/Producer.hh
            kafka/ServiceHandler.hh
  )
