//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_StackFrame_HH
#define SWA_StackFrame_HH

#include <vector>
#include "Parameter.hh"
#include "LocalVar.hh"

namespace SWA
{
  class StackFrame
  {
    public:
      enum ActionType {  DomainService, TerminatorService, ObjectService, StateAction };

      StackFrame ( ActionType type, int domainId, int actionId );
      StackFrame ( ActionType type, int domainId, int objectId, int actionId );
      StackFrame ( ActionType type, int domainId, int objectId, int actionId, int lineNo );

      void setLine( int line ) { this->line = line; } 

      ActionType getType() const { return type; }
      int getLine() const { return line; }
      int getDomainId() const { return domainId; }
      int getObjectId() const { return objectId; }
      int getActionId() const { return actionId; }

      template<class T>
      void pushVar(int id, T& variable ) { variables.push_back(LocalVar(id,variable)); }
      void popVar() { variables.pop_back(); }

      const std::vector<LocalVar>& getLocalVars() const { return variables; }

      template<class T>
      void addParam(const T& param ) { parameters.push_back(Parameter(param)); }

      const std::vector<Parameter>& getParameters() const { return parameters; }

      void setThis ( void* thisPtr ) { this->thisPtr = thisPtr; }

      template<class T>
      T* getThis() const { return static_cast<T*>(thisPtr); }

      bool operator< ( const StackFrame& rhs ) const;

    private:
      ActionType type;
      int domainId;
      int objectId;
      int actionId;
      int line;

      void* thisPtr;
      std::vector<Parameter> parameters;
      std::vector<LocalVar> variables;
  };

}

#endif
