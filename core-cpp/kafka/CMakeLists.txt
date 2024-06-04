find_package(CppKafka CONFIG REQUIRED QUIET)
find_package(RdKafka CONFIG REQUIRED QUIET)
find_package(libuuid REQUIRED)
find_package(log4cplus REQUIRED)
find_package(fmt REQUIRED)
find_package(nlohmann_json REQUIRED)

simple_add_shared_library (
  NAME      Kafka
  SOURCES
            Consumer.cc
            DataConsumer.cc
            Kafka.cc
            ProcessHandler.cc
            Producer.cc
            ServiceHandler.cc
            LogAppender.cc
  LINKS     swa
            CppKafka::cppkafka
            RdKafka::rdkafka
            libuuid::libuuid
            fmt::fmt
            log4cplus::log4cplus
            nlohmann_json::nlohmann_json
  EXPORT    MaslCore
  INCLUDES
            kafka/Consumer.hh
            kafka/DataConsumer.hh
            kafka/Kafka.hh
            kafka/ProcessHandler.hh
            kafka/Producer.hh
            kafka/ServiceHandler.hh
  )
