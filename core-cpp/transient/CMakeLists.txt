#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
simple_add_shared_library(
  NAME      transient 
  SOURCES 
            ThreadListener.cc
            EventTimers.cc
  LINKS
            swa-headers
  EXPORT    MaslCore
  INCLUDES  
            transient/PairFirstIterator.hh
            transient/PairSecondIterator.hh
            transient/Population.hh
            transient/ThreadListener.hh
            transient/ToManyAssociative.hh
            transient/ToManyRelationship.hh
            transient/ToOneAssociative.hh
            transient/ToOneRelationship.hh
  )

