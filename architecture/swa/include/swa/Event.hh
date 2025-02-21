/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_Event_HH
#define SWA_Event_HH

#include "Parameter.hh"
#include "types.hh"

#include <vector>

namespace SWA {
class Event {
  public:
    Event();
    virtual ~Event();

    void setSource(int objectId, IdType instanceId);
    void setDest(IdType instanceId);

    bool getHasDest() const { return hasDest; }
    IdType getDestInstanceId() const { return destInstanceId; }

    bool getHasSource() const { return hasSource; }
    int getSourceObjectId() const { return sourceObjectId; }
    IdType getSourceInstanceId() const { return sourceInstanceId; }

    virtual int getDomainId() const = 0;
    virtual int getObjectId() const = 0;
    virtual int getEventId() const = 0;

    virtual void invoke() const = 0;
    virtual std::vector<Parameter> getParameters() const { return parameters; }

  protected:
    template <class T> void addParam(const T &param) {
        parameters.push_back(Parameter(param));
    }

  private:
    bool hasDest;
    IdType destInstanceId;

    bool hasSource;
    int sourceObjectId;
    IdType sourceInstanceId;

    std::vector<Parameter> parameters;

    // Prevent copy, otherwise Parameter pointers will be invalid
    Event(const Event &);
    Event &operator=(const Event &);
};
} // namespace SWA

#endif
