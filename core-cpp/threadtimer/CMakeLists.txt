#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
simple_add_shared_library (
  NAME      ThreadTimer
  SOURCES   ThreadTimer.cc
  LINKS     MetaData
  EXPORT    MaslCore
  )
