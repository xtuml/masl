#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
simple_add_shared_library (
  NAME      MetaData
  SOURCES   MetaData.cc
  LINKS     swa
  EXPORT    MaslCore
  INCLUDES  metadata/MetaData.hh
  )

