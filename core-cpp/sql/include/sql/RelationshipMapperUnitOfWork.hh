/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_RelationshipMapperUnitOfWork_HH
#define Sql_RelationshipMapperUnitOfWork_HH

#include <string>
#include <utility>
#include <vector>

#include <boost/unordered_set.hpp>

#include "Util.hh"
#include "DatabaseFactory.hh"
#include "UnitOfWorkObserver.hh"

namespace SQL {

template <class T>
class RelationshipMapperUnitOfWork : public UnitOfWorkObserver
{
    public:
        typedef typename T::LhsPsObjectPtr  LhsPsObjectPtr; 
        typedef typename T::RhsPsObjectPtr  RhsPsObjectPtr; 

        typedef std::pair<LhsPsObjectPtr,RhsPsObjectPtr> LinkedPairType;

        typedef boost::unordered_set<LinkedPairType> LinkHashSetType;
        typedef boost::unordered_set<LinkedPairType> UnLinkHashSetType;
 
    public:
        RelationshipMapperUnitOfWork(T& parent);
       ~RelationshipMapperUnitOfWork();

        void initialise();

        void registerLink   (const LhsPsObjectPtr&  lhsObj, const RhsPsObjectPtr& rhsObj);
        void registerUnLink (const LhsPsObjectPtr&  lhsObj, const RhsPsObjectPtr& rhsObj);

        void linkStatement   (const std::string& statement);
        void unlinkStatement (const std::string& statement);

        // UnitOfWorkObserver
        void flush             (UnitOfWorkContext& context);
        void committed         (UnitOfWorkContext& context);
        void startTransaction  (UnitOfWorkContext& context);
        void abortTransaction  (UnitOfWorkContext& context);
        void commitTransaction (UnitOfWorkContext& context);

        // flush any dirty object changes to the database. These
        // will still not be committed until the database unit of 
        // work commits.
        void flush();

    private:
         bool isDirty();
         void clear();
         void primeParentForChanges();

    private:
        T&                parent;
        LinkHashSetType   linkSet;
        UnLinkHashSetType unlinkSet;

        std::string linkStatements;
        std::string unlinkStatements;
        bool unitOfWorkDirty;
};

// ***********************************************************************
// ***********************************************************************
template <class T>
RelationshipMapperUnitOfWork<T>::RelationshipMapperUnitOfWork(T& parent):
   parent(parent),
   unitOfWorkDirty(false)
{

}

// ***********************************************************************
// ***********************************************************************
template <class T>
RelationshipMapperUnitOfWork<T>::~RelationshipMapperUnitOfWork()
{
   clear();
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::initialise()
{

}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::linkStatement (const std::string& statement)
{
   primeParentForChanges();
   linkStatements += statement;
   linkStatements += "\n";
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::unlinkStatement (const std::string& statement)
{
   primeParentForChanges();
   unlinkStatements += statement;
   unlinkStatements += "\n";
}

// ***********************************************************************
// ***********************************************************************
template <class T>
bool RelationshipMapperUnitOfWork<T>::isDirty()
{
   return unitOfWorkDirty;
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::primeParentForChanges()
{
   // This method is called before this mapper is due to become
   // dirty, i.e. the data controlled by the mapper has been modified.
   // The Master Unit Of Work needs to be informed of this so it can
   // commit the changes at the required time. 
   if(isDirty() == false){
      DatabaseFactory::singleton().getImpl().getCurrentUnitOfWork().registerDirtyObserver(this);
      unitOfWorkDirty = true;
   }
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::registerLink(const LhsPsObjectPtr&  lhsObj, const RhsPsObjectPtr& rhsObj)
{
    primeParentForChanges();
    typename UnLinkHashSetType::value_type pairedValue(lhsObj,rhsObj);
    if (unlinkSet.find(pairedValue) == unlinkSet.end()){
        linkSet.insert(std::make_pair(lhsObj,rhsObj));
    }
    else{
       // An unlink request for the supplied link id's has already been 
       // received within the scope of the current transaction. Therefore
       // this link request negates the inital unlink request so they cancel
       // each other out. Therefore remove inital unlink.
       unlinkSet.erase(pairedValue);
    }
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::registerUnLink(const LhsPsObjectPtr&  lhsObj, const RhsPsObjectPtr& rhsObj) 
{
    primeParentForChanges();
    typename UnLinkHashSetType::value_type pairedValue(lhsObj,rhsObj);
    if (linkSet.find(pairedValue) == linkSet.end()){
        unlinkSet.insert(pairedValue);
    }
    else{
       // A link between the same two objects on the this relationship 
       // was already undertaken during the current transaction. This
       // unlink essentially nullifies the orginal link.
       linkSet.erase(pairedValue);
    }
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::startTransaction  (UnitOfWorkContext& context)
{
    if (isDirty() == true){
        throw SqlException("RelationshipMapperUnitOfWork<T>::start_transaction : mapper is not clean");
    }
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::commitTransaction (UnitOfWorkContext& context)
{
    parent.commitLinks(linkSet);
    context.getStatements() += linkStatements;

    parent.commitUnlinks(unlinkSet);
    context.getStatements() += unlinkStatements;
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::abortTransaction  (UnitOfWorkContext& context)
{
   // Clear the cache if any of the containers are non empty.
   parent.abortLinks();
   clear();
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::flush  (UnitOfWorkContext& context)
{
   commitTransaction(context); 
   clear();
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::committed  (UnitOfWorkContext& context)
{
  clear();
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::flush()
{
  // Force the main unit of work instance to flush any pending 
  // changes held by this mapper, but only if the mapper contains
  // dirty objects.
  if (isDirty() == true){
      DatabaseFactory::singleton().getImpl().getCurrentUnitOfWork().flushObserver(this);
  }
}

// ***********************************************************************
// ***********************************************************************
template <class T>
void RelationshipMapperUnitOfWork<T>::clear()
{
   linkSet.clear();
   unlinkSet.clear();

   linkStatements.clear();
   unlinkStatements.clear(); 
   unitOfWorkDirty = false;
}

} // end namepsace DB_LAYER

#endif


