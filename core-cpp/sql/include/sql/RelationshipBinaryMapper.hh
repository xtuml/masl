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

#ifndef Sql_RelationshipBinaryMapper_HH
#define Sql_RelationshipBinaryMapper_HH

#include <string>
#include <sstream>

#include "boost/bind/bind.hpp"
#include "boost/shared_ptr.hpp"

#include "swa/types.hh"
#include "swa/ObjectPtr.hh"

#include "Util.hh"
#include "Exception.hh"
#include "ResourceMonitor.hh"
#include "WriteOnChangeEnabler.hh"
#include "RelationshipContainers.hh"
#include "ResourceMonitorObserver.hh"
#include "RelationshipSqlRepository.hh"
#include "RelationshipMapperUnitOfWork.hh"
#include "RelationshipBinarySqlGenerator.hh"

namespace SQL {

  using namespace boost::placeholders;

// ***********************************************************************
//! @Brief Class to handle binary relationships and their associated link tables.
//!
//! In MASL relationship definitions look something like the example below.
//!
//! example MASL Relationship Definitions:
//! 
//!     relationship R11 is Aerial_Switch unconditionally feed_signals_to many Receiver_Unit,
//!                         Receiver_Unit unconditionally is_connected_to one Aerial_Switch;
//! 
//!     relationship R16 is Receiver_Unit unconditionally belongs_to one Receiver_Group,
//!                         Receiver_Group unconditionally groups_together one Receiver_Unit;
//!
//! The placement of the one or many multipicity does not follow a set pattern, i.e. the
//! many side always comes first. As a result the following class is used to handle the
//! relationship definitions that define any combination of multipicity for a Binary Relationship.
//!
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
class RelationshipBinaryMapper : public ResourceMonitorObserver
{
    public:
       typedef  typename LhsRelContainer::PsObjectPtr LhsPsObjectPtr;
       typedef  typename RhsRelContainer::PsObjectPtr RhsPsObjectPtr;

       typedef typename LhsRelContainer::PsObjectPtrSet LhsPsObjectPtrSet;
       typedef typename RhsRelContainer::PsObjectPtrSet RhsPsObjectPtrSet;

       typedef typename LhsRelContainer::NavigatedType    NavigatedLhsType;
       typedef typename RhsRelContainer::NavigatedType    NavigatedRhsType;
       typedef typename LhsRelContainer::NavigatedSetType NavigatedSetType;

       typedef RelationshipBinarySqlGenerator<LhsRelContainer,RhsRelContainer> RelSqlGeneratorType;
       typedef RelationshipMapperUnitOfWork<RelationshipBinaryMapper>          UnitOfWorkType;

    public:
       enum { identity = rel };

       static RelationshipBinaryMapper& singleton();

       // *****************************************************************
       //! Initialise the relationship mapper and load all the link data
       //! associated with the database link table for the relationship 
       //!
       //! @param generator The SQL implementation for this relationship mapper.
       //!
       // *****************************************************************
       void initialise(const ::boost::shared_ptr<RelSqlGeneratorType>& generator);

       // *****************************************************************
       //! @return Whether the mapper has been initialised.
       // *****************************************************************
       bool isInitialised() const;

       // *****************************************************************
       //! Method can be used to configure the mapper so that link information
       //! for the relationship is only loaded when required, rather than it all
       //! being loaded upon start-up. This can be more effecient for heavily 
       //! populated relationship tables.
       //! @param isEnabled turn on(true) and off(false) on demand loading
       // *****************************************************************
       void setToLoadOnDemand(bool isEnabled);

       // *****************************************************************
       //! Method used to associate an object on the one side of the relationship,
       //! defined by the mapper class, with another object on the many side. 
       //! 
       //! On link error will throw SqlException.
       //!
       //! @param lhsObj object on the lhs end of the relationship
       //! @param rhsObj object on the rhs end of the relationship
       // *****************************************************************
       void linkFromLhsToRhs(const LhsPsObjectPtr& lhsObj, const RhsPsObjectPtr& rhsObj); 

       // *****************************************************************
       //! Method used to associate an object on the many side of the relationship,
       //! defined by the mapper class, with another object on the one side. 
       //! 
       //! On link error will throw SqlException.
       //!
       //! @param rhsObj object on the rhs end of the relationship
       //! @param lhsObj object on the lhs end of the relationship
       // *****************************************************************
       void linkFromRhsToLhs(const RhsPsObjectPtr& rhsObj, const LhsPsObjectPtr& lhsObj);

       // *****************************************************************
       //! Method used to unassociate an object on the one side of the relationship,
       //! defined by the mapper class, with another object on the many side. 
       //! 
       //! On unlink error will throw SqlException.
       //!
       //! @param lhsObj object on the lhs end of the relationship
       //! @param rhsObj object on the rhs end of the relationship
       // *****************************************************************
       void unlinkFromLhsToRhs(const LhsPsObjectPtr& lhsObj, const RhsPsObjectPtr& rhsObj); 

       // *****************************************************************
       //! Method used to unassociate an object on the many side of the relationship,
       //! defined by the mapper class, with another object on the one side. 
       //! 
       //! On unlink error will throw SqlException.
       //!
       //! @param rhsObj object on the rhs end of the relationship
       //! @param lhsObj object on the lhs end of the relationship
       // *****************************************************************
       void unlinkFromRhsToLhs(const RhsPsObjectPtr& rhsObj, const LhsPsObjectPtr& lhsObj);

       // *****************************************************************
       //! Method used to count the number relationship links for the 
       //! currently specified objects.
       //! 
       //! @param lhsObj object on the lhs end of the relationship
       //! @param rhsObj object on the rhs end of the relationship
       // *****************************************************************
       ::std::size_t countFromLhsToRhs(const LhsPsObjectPtr& lhsObj); 

       // *****************************************************************
       //! Method used to count the number relationship links for the 
       //! currently specified objects.
       //! 
       //! @param lhsObj object on the lhs end of the relationship
       //! @param rhsObj object on the rhs end of the relationship
       // *****************************************************************
       ::std::size_t countFromRhsToLhs(const RhsPsObjectPtr& rhsObj);

       // *****************************************************************
       //! Navigate the relationship from the specified object and return the 
       //! object(s) that are currently linked. Will return an empty object/set
       //! if no link(s) found.
       //! 
       //! @param   lhsObj the source object to navigate from
       //! @returns object(s) linked to the source object
       // *****************************************************************
       const NavigatedRhsType navigateFromLhsToRhs(const LhsPsObjectPtr& lhsObj);

       // *****************************************************************
       //! Navigate the relationship using the specified set of lhs objects 
       //! and return a set of all the rhs sided objects that are linked.
       //! Will return an empty set if no links found.
       //!
       //! @param   lhsObjSet the source objects to navigate from
       //! @returns set of rhs objects that were found to be linked
       // *****************************************************************
       const NavigatedSetType navigateFromLhsToRhs(const LhsPsObjectPtrSet& lhsObjSet); 

       // *****************************************************************
       //! Navigate the relationship using the specified rhs sided object
       //! and return the lhs sided object(s) that are linked. Will return 
       // an empty set/object if no link found.
       //!
       //! @param   rhsObj the source object to navigate from
       //! @returns set of lhs objects that were found to be linked.
       // *****************************************************************
       const NavigatedLhsType navigateFromRhsToLhs(const RhsPsObjectPtr& rhsObj);

       // *****************************************************************
       //! Navigate the relationship using the specified set of rhs sided  
       //! objects and return a set of all the lhs sided objects that are 
       //! linked. Will return an empty set if no links found.
       //!
       //! @param   rhsObjSet the source objects to navigate from
       //! @returns set of lhs objects that were found to be linked.
       // *****************************************************************
       const NavigatedSetType navigateFromRhsToLhs(const RhsPsObjectPtrSet& rhsObjSet);

       // *****************************************************************
       //! Callback method used by the associated unit-of-work class to 
       //! inform this mapper that the database is aborting the current
       //! transaction and that it needs to clean up any cached link values.
       //!        
       // *****************************************************************
       void abortLinks ();

       // *****************************************************************
       //! Callback method used by the associated unit-of-work class to 
       //! inform this mapper that the database is committing the current
       //! transaction and that the associated set of objects passed to the call
       //! have pending link operations that need to be applied to the database.
       //!
       //! On unlink error will throw SqlException.
       //!
       //! @param objects list of dirty objects that have pending links
       // *****************************************************************
       void commitLinks   (const typename UnitOfWorkType::LinkHashSetType&   objects);

       // *****************************************************************
       //! Callback method used by the associated unit-of-work class to 
       //! inform this mapper that the database is committing the current
       //! transaction and that the associated set of objects passed to the call
       //! have pending unlink operations that need to be applied to the database.
       //!
       //! On unlink error will throw SqlException.
       //!
       //! @param objects list of dirty objects that have pending unlinks
       // *****************************************************************
       void commitUnlinks (const typename UnitOfWorkType::UnLinkHashSetType& objects);

       // *****************************************************************
       //! This method is called just before an object on the lhs side of the 
       //! relationship is deleted. It enables a check to be carried out as
       //! to whether the specified object is still participating in any 
       //! relationships. If it is then an SqlException is thrown to signal 
       //! a dangling relationship error. It also enables the link caches to 
       //! be purged of any reference to this object.
       //!
       //! On error will throw SqlException.
       //!
       //! @param lhsObj the object that is being deleted
       // *****************************************************************
       void objectDeletedLhs (const LhsPsObjectPtr& lhsObj);

       // *****************************************************************
       //! This method is called just before an object on the rhs side of the 
       //! relationship is deleted. It enables a check to be carried out as
       //! to whether the specified object is still participating in any 
       //! relationships. If it is then an SqlException is thrown to signal 
       //! a dangling relationship error. It also enables the link caches to 
       //! be purged of any reference to this object.
       //!
       //! On error will throw SqlException.
       //!
       //! @param rhsObj the object that is being deleted
       // *****************************************************************
       void objectDeletedRhs (const RhsPsObjectPtr& rhsObj);

       // *****************************************************************
       //! @param lhsObjobject to be checked 
       //! @returns true if the lhs object is linked to another rhs object 
       // *****************************************************************
       bool hasLinksLhs (const LhsPsObjectPtr& lhsObj);

       // *****************************************************************
       //! @param rhsObj object to be checked 
       //! @returns true if the rhs object is linked to another lhs object 
       // *****************************************************************
       bool hasLinksRhs (const RhsPsObjectPtr& rhsObj);

       // *****************************************************************
       //! Write any pending relationship changes to the database. 
       // *****************************************************************
       void forceFlush();

       // resource monitor interface
       void report  (ResourceMonitorContext& context);
       void compact (ResourceMonitorContext& context);
       void release (ResourceMonitorContext& context);

    protected:
        RelationshipBinaryMapper();
       ~RelationshipBinaryMapper();

    private:
         typedef boost::unordered_map< ::SWA::IdType, RhsRelContainer> LhsToRhsLinksType;
         typedef boost::unordered_map< ::SWA::IdType, LhsRelContainer> RhsToLhsLinksType;
         typedef std::pair< typename LhsToRhsLinksType::iterator, typename RhsToLhsLinksType::iterator > IteratorPairType;

    private:
        void loadAll ();
        void flush   ();

        IteratorPairType  loadLinks (const ::SWA::IdType& lhsObjId, const ::SWA::IdType& rhsObjId);

        typename LhsToRhsLinksType::iterator loadLhsToRhsLinks (const ::SWA::IdType& lhsObjId);
        typename RhsToLhsLinksType::iterator loadRhsToLhsLinks (const ::SWA::IdType& rhsObjId);

    private:
        RelationshipBinaryMapper(const RelationshipBinaryMapper& rhs);
        RelationshipBinaryMapper& operator=(const RelationshipBinaryMapper& rhs);
        
    private:
         bool               allLoaded;
         bool               loadOnDemand;        
         bool               writeOnChange;
         UnitOfWorkType     unitOfWork;

         LhsToRhsLinksType  lhsToRhsLinks;
         RhsToLhsLinksType  rhsToLhsLinks;
         ::boost::shared_ptr<RelSqlGeneratorType> relSqlGenerator;

         NavigatedRhsType emptyLhsToRhsLinks;
         NavigatedLhsType emptyRhsToLhsLinks;
};

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::RelationshipBinaryMapper():
      allLoaded(false),
      loadOnDemand(false),
      writeOnChange(false),
      unitOfWork(*this),
      lhsToRhsLinks(rel),
      rhsToLhsLinks(rel)
{

}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::~RelationshipBinaryMapper()
{

}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>& RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::singleton()
{
   static RelationshipBinaryMapper instance;
   return instance;
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::initialise(const ::boost::shared_ptr<RelSqlGeneratorType>& generator) 
{
  relSqlGenerator = generator;
  unitOfWork.initialise(); 
  relSqlGenerator->initialise();
  RelationshipSqlRepository::getInstance().registerRelationshipSql(relSqlGenerator.get());

  writeOnChange = WriteOnChangeEnabler(relSqlGenerator->getTableName()).isEnabled();

  // Always cache any persisted object links. This is the default operation,
  // This class will still function correctly if the cache is loaded on demand. 
  loadAll();
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
bool RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::isInitialised() const
{
  return relSqlGenerator.get() != 0;
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::forceFlush()
{
   unitOfWork.flush(); 
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::setToLoadOnDemand(bool isEnabled)
{
  if (loadOnDemand == false && isEnabled == true){
      loadOnDemand = isEnabled;
      allLoaded    = false;
      lhsToRhsLinks.clear();
      rhsToLhsLinks.clear();
  }
  else if (loadOnDemand == true && isEnabled == false){
       loadOnDemand = isEnabled;
       loadAll();
  }
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::linkFromLhsToRhs (const LhsPsObjectPtr& lhsObj, const RhsPsObjectPtr& rhsObj) 
{  
    const ::SWA::IdType& lhsObjId = lhsObj.getChecked()->getArchitectureId();
    const ::SWA::IdType& rhsObjId = rhsObj.getChecked()->getArchitectureId();

   // Check that the required link data is loaded before
   // undertaking any kind of processing on the cache.
   IteratorPairType itrPair = loadLinks(lhsObjId,rhsObjId);

   if (itrPair.first == lhsToRhsLinks.end()){
       itrPair.first = lhsToRhsLinks.insert(std::make_pair(lhsObjId,RhsRelContainer())).first;
   }

   if (itrPair.second == rhsToLhsLinks.end()){
       itrPair.second = rhsToLhsLinks.insert(std::make_pair(rhsObjId,LhsRelContainer())).first;
   }

   RhsRelContainer&  rhsContainer = itrPair.first->second;
   LhsRelContainer&  lhsContainer = itrPair.second->second;

   // The two link operations need to be atomic, so undertake
   // the required recovery should the links fail. 
   rhsContainer.link(rhsObj);    
   try{
      lhsContainer.link(lhsObj);
   }
   catch(SqlException se){
       rhsContainer.unlink(rhsObj);
       throw;
   }  
   unitOfWork.registerLink(lhsObj,rhsObj);     
   flush();
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::linkFromRhsToLhs(const RhsPsObjectPtr& rhsObj, const LhsPsObjectPtr& lhsObj)
{
   linkFromLhsToRhs(lhsObj,rhsObj);
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::unlinkFromLhsToRhs  (const LhsPsObjectPtr& lhsObj,  const RhsPsObjectPtr& rhsObj) 
{ 
   const ::SWA::IdType& lhsObjId = lhsObj.getChecked()->getArchitectureId();
   const ::SWA::IdType& rhsObjId = rhsObj.getChecked()->getArchitectureId();

   // Check that the required link data is loaded before
   // undertaking any kind of processing on the cache.
   IteratorPairType itrPair = loadLinks(lhsObjId,rhsObjId);

   if (itrPair.first == lhsToRhsLinks.end() || itrPair.second == rhsToLhsLinks.end()){
       throw SqlException("RelationshipBinaryMapper::unlinkFromLhsToRhs - no links to unlink");
   }

   RhsRelContainer&  rhsContainer = itrPair.first->second;
   LhsRelContainer&  lhsContainer = itrPair.second->second;

   // The two unlink operations need to be atomic, so undertake
   // the required recovery should the unlinks fail. 
   rhsContainer.unlink(rhsObj);
   try{
     lhsContainer.unlink(lhsObj);  
   }
   catch(SqlException se){
       rhsContainer.link(rhsObj);
       throw;
   }       
   unitOfWork.registerUnLink(lhsObj,rhsObj);
   flush();
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::unlinkFromRhsToLhs (const RhsPsObjectPtr& rhsObj, const LhsPsObjectPtr& lhsObj)
{ 
  unlinkFromLhsToRhs(lhsObj,rhsObj); 
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
::std::size_t RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::countFromLhsToRhs(const LhsPsObjectPtr& lhsObj)
{
   typename LhsToRhsLinksType::iterator lhsToRhsLinksItr = lhsToRhsLinks.find(lhsObj.getChecked()->getArchitectureId());
   return lhsToRhsLinksItr != lhsToRhsLinks.end() ? lhsToRhsLinksItr->second.linkCount() : 0;
} 

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
::std::size_t RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::countFromRhsToLhs(const RhsPsObjectPtr& rhsObj)
{
  typename RhsToLhsLinksType::iterator rhsToLhsLinksItr = rhsToLhsLinks.find(rhsObj.getChecked()->getArchitectureId());
  return   rhsToLhsLinksItr != rhsToLhsLinks.end() ? rhsToLhsLinksItr->second.linkCount() : 0;
} 

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::abortLinks()
{
   // The set of cached links will contain some cached values
   // that have not been committed to the database. Therefore
   // clear the cache and repopulate as required.
   lhsToRhsLinks.clear();
   rhsToLhsLinks.clear();
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::commitLinks (const typename UnitOfWorkType::LinkHashSetType& objects)
{
  std::for_each(objects.begin(),objects.end(),boost::bind(&RelSqlGeneratorType::commitLink,boost::ref(relSqlGenerator),_1));
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::commitUnlinks (const typename UnitOfWorkType::UnLinkHashSetType& objects)
{
  std::for_each(objects.begin(),objects.end(),boost::bind(&RelSqlGeneratorType::commitUnlink,boost::ref(relSqlGenerator),_1));
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::objectDeletedLhs  (const LhsPsObjectPtr& lhsObj)
{
   const ::SWA::IdType architectureId = lhsObj.getChecked()->getArchitectureId();

   // The specified object is being deleted
   if (hasLinksLhs(lhsObj) == true){
       std::ostringstream errorMsgStrm;
       errorMsgStrm << " Found dangling relationship(s) in relationship R" <<  identity;
       errorMsgStrm <<  " : LHS  objectId (" << architectureId << ")";
       throw SqlException(errorMsgStrm.str()); 
   }
   lhsToRhsLinks.erase(architectureId);
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::objectDeletedRhs (const RhsPsObjectPtr& rhsObj)
{
   const ::SWA::IdType architectureId = rhsObj.getChecked()->getArchitectureId();

   // The specified object is being deleted
   if (hasLinksRhs(rhsObj) == true){
       std::ostringstream errorMsgStrm;
       errorMsgStrm << " Found dangling relationship(s) in one-to-many relationship R" <<  identity;
       errorMsgStrm <<  " : RHS  objectId (" << architectureId << ")";
       throw SqlException(errorMsgStrm.str()); 
   }
   rhsToLhsLinks.erase(architectureId);
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
bool RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::hasLinksLhs(const LhsPsObjectPtr& lhsObj)
{ 
  typename LhsToRhsLinksType::iterator lhsToRhsLinkItr = loadLhsToRhsLinks(lhsObj.getChecked()->getArchitectureId());
  return lhsToRhsLinkItr != lhsToRhsLinks.end() ? lhsToRhsLinkItr->second.isLinked() : false; 
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
bool RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::hasLinksRhs(const RhsPsObjectPtr& rhsObj)
{ 
  typename RhsToLhsLinksType::iterator rhsToLhsLinkItr = loadRhsToLhsLinks(rhsObj.getChecked()->getArchitectureId());
  return  rhsToLhsLinkItr != rhsToLhsLinks.end() ? rhsToLhsLinkItr->second.isLinked() : false;
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
const typename  RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::NavigatedRhsType
  RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::navigateFromLhsToRhs(const LhsPsObjectPtr& lhsObj) 
{  
   const ::SWA::IdType architectureId = lhsObj.getChecked()->getArchitectureId();

   // Check that the required link data is loaded before
   // undertaking any kind of processing on the cache.
   typename LhsToRhsLinksType::iterator lhsToRhsLinkItr = loadLhsToRhsLinks(architectureId);

   // Do not check the conditionality here as conditionality 
   // only needs to be consistent at the end of a transaction
   if (lhsToRhsLinkItr == lhsToRhsLinks.end()){
       return emptyLhsToRhsLinks;
   }
   return lhsToRhsLinkItr->second.navigate(); 
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
const typename  RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::NavigatedSetType  
  RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::navigateFromLhsToRhs(const LhsPsObjectPtrSet& lhsObjSet) 
{  
   NavigatedSetType rhsObjectSet;
   typename LhsPsObjectPtrSet::iterator objItr = lhsObjSet.begin();
   typename LhsPsObjectPtrSet::iterator objEnd = lhsObjSet.end();
   for(;objItr != objEnd; ++objItr){
      const LhsPsObjectPtr& lhsObjPtr = *objItr;
      typename LhsToRhsLinksType::iterator lhsToRhsLinkItr = loadLhsToRhsLinks(lhsObjPtr.getChecked()->getArchitectureId());
      if (lhsToRhsLinkItr != lhsToRhsLinks.end()){
          lhsToRhsLinkItr->second.navigateWithSet(rhsObjectSet);
      }
   }
   rhsObjectSet.forceUnique();
   return rhsObjectSet; 
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
const typename RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::NavigatedLhsType
RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::navigateFromRhsToLhs(const RhsPsObjectPtr& rhsObj)
{ 
   const ::SWA::IdType architectureId = rhsObj.getChecked()->getArchitectureId();

   // Check that the required link data is loaded before
   // undertaking any kind of processing on the cache.
   typename RhsToLhsLinksType::iterator rhsToLhsLinkItr = loadRhsToLhsLinks(architectureId);

   // Do not check the conditionality here as conditionality 
   // only needs to be consistent at the end of a transaction
   if (rhsToLhsLinkItr == rhsToLhsLinks.end()){
       return emptyRhsToLhsLinks;
   }
  return rhsToLhsLinkItr->second.navigate();
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
const typename RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::NavigatedSetType
RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::navigateFromRhsToLhs(const RhsPsObjectPtrSet& rhsObjSet)
{ 
   NavigatedSetType objectSet;
   typename RhsPsObjectPtrSet::iterator objItr = rhsObjSet.begin();
   typename RhsPsObjectPtrSet::iterator objEnd = rhsObjSet.end();

   for(;objItr != objEnd; ++objItr){
      const RhsPsObjectPtr& rhsObjPtr = *objItr;
      typename RhsToLhsLinksType::iterator rhsToLhsLinkItr = loadRhsToLhsLinks(rhsObjPtr.getChecked()->getArchitectureId());
      if (rhsToLhsLinkItr != rhsToLhsLinks.end()){
          rhsToLhsLinkItr->second.navigateWithSet(objectSet);
      }
   }

   objectSet.forceUnique();
   return objectSet; 
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::report  (ResourceMonitorContext& context)
{

}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::compact (ResourceMonitorContext& context)
{

}


// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::release (ResourceMonitorContext& context)
{

}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::loadAll()
{
  // Load the many side of a one-to-many relationship. Before attempting load
  // check that the many objects for the specified one object has not already 
  // been loaded and cached.
  if (allLoaded == false){
       relSqlGenerator->loadAll(lhsToRhsLinks,rhsToLhsLinks);
       allLoaded = true;
  }
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
typename RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::IteratorPairType
 RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::loadLinks(const ::SWA::IdType& lhsObjId, const ::SWA::IdType& rhsObjId)
{
   typename LhsToRhsLinksType::iterator lhsToRhsLinksItr = loadLhsToRhsLinks(lhsObjId);
   typename RhsToLhsLinksType::iterator rhsToLhsLinksItr = loadRhsToLhsLinks(rhsObjId);

   return IteratorPairType(lhsToRhsLinksItr, rhsToLhsLinksItr);
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
typename RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::LhsToRhsLinksType::iterator
 RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::loadLhsToRhsLinks(const ::SWA::IdType& lhsObjId)
{
  typename LhsToRhsLinksType::iterator lhsToRhsLinksItr = lhsToRhsLinks.find(lhsObjId);
  if (allLoaded == false && lhsToRhsLinksItr == lhsToRhsLinks.end()){ 
      relSqlGenerator->loadRhs(lhsObjId,lhsToRhsLinks,rhsToLhsLinks);
      lhsToRhsLinksItr = lhsToRhsLinks.find(lhsObjId);
  } 
  return lhsToRhsLinksItr;   
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
typename RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::RhsToLhsLinksType::iterator 
 RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::loadRhsToLhsLinks(const ::SWA::IdType& rhsObjId)
{
  typename RhsToLhsLinksType::iterator rhsToLhsLinksItr = rhsToLhsLinks.find(rhsObjId);
  if (allLoaded == false && rhsToLhsLinksItr == rhsToLhsLinks.end()){ 
      relSqlGenerator->loadLhs(rhsObjId,lhsToRhsLinks,rhsToLhsLinks);
      rhsToLhsLinksItr = rhsToLhsLinks.find(rhsObjId);
  }
  return rhsToLhsLinksItr;
}

// ***********************************************************************
// ***********************************************************************
template <int rel, class LhsRelContainer, class RhsRelContainer, bool lhsC, bool rhsC>
void RelationshipBinaryMapper<rel,LhsRelContainer,RhsRelContainer,lhsC,rhsC>::flush ()
{
   if (writeOnChange == true){
       unitOfWork.flush();
   }   
}

} // end namepsace SQL
#endif
