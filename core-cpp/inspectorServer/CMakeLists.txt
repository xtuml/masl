#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
simple_add_shared_library(
  NAME      Inspector
  SOURCES 
            Inspector.cc
            BufferedIO.cc
            ConsoleRedirection.cc
            CommunicationChannel.cc
            DomainHandler.cc
            GenericObjectHandler.cc
            TerminatorHandler.cc
            EventHandler.cc
            ActionHandler.cc
            ProcessHandler.cc
            MetaData.cc
  LINKS   
            MetaData
            sockets
            swa-headers
  EXPORT    MaslCore
  INCLUDES
            inspector/ActionHandler.hh
            inspector/BufferedIO.hh
            inspector/CommunicationChannel.hh
            inspector/DomainHandler.hh
            inspector/EventHandler.hh
            inspector/GenericObjectHandler.hh
            inspector/ObjectHandler.hh
            inspector/ProcessHandler.hh
            inspector/TerminatorHandler.hh
            inspector/types.hh

  )

