/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

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
