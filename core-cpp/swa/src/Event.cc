//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "swa/Event.hh"

namespace SWA
{

  Event::Event()
    : hasDest(false),
      destInstanceId(),
      hasSource(false),
      sourceObjectId(-1),
      sourceInstanceId() {}

  Event::~Event() {}

  void Event::setSource ( int objectId, IdType instanceId )
  {
    hasSource = true;
    sourceObjectId = objectId;
    sourceInstanceId = instanceId;
  }

  void Event::setDest ( IdType instanceId )
  {
    hasDest = true;
    destInstanceId = instanceId;
  }

}
