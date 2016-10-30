#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
simple_add_shared_library ( 
  NAME    Trace
  SOURCES Trace.cc
  LINKS   MetaData
  EXPORT  MaslCore
  )

