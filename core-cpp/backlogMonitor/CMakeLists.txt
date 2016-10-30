#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
simple_add_shared_library(
  NAME    BacklogMonitor 
  SOURCES 
          BacklogMonitor.cc
  LINKS
          swa-headers
  EXPORT  MaslCore
  )

