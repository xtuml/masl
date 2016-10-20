//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File: RelationshipAtomicity.hh 
//
//============================================================================//

#ifndef Sql_RelationshipAtomicity_HH
#define Sql_RelationshipAtomicity_HH

namespace SQL {

// ***********************************************************************
//! Define an empty class who's type information can be used by the 
//! RelationshipAtomicity class to determine what operations it should be 
//! configured to support an atomic link. 
// ***********************************************************************
class LinkPolicy   {};

// ***********************************************************************
//! Define an empty class who's type information can be used by the 
//! RelationshipAtomicity class to determine what operations it should be 
//! configured to support an atomic unlink. 
// ***********************************************************************
class UnlinkPolicy {};

// ***********************************************************************
//! @brief Provide Atomic operations on relationship updates.
//!
//! Changes to the containers that are used to represent the links/unlinks between
//! a tenary relationship definition need to be managed so that they are either 
//! all updated or in the face of any error the containers are rolled-back to
//! their previous state. The modification and rollback operations are performed 
//! by this class.
//!
//! Notice that the actions required for managing the atomicity of a link or unlink
//! operation are exactly the same apart from the configuration of the standard and
//! failure actions. The OperationPolicy template parameter is therefore used as a type 
//! switch to determine the configuration of these actions. 
// ***********************************************************************
template <class Container, class ObjectFirst, class ObjectSecond, class OperationPolicy>
class RelationshipAtomicity
{
  public:
     RelationshipAtomicity(Container& cont, const ::SWA::IdType& key, const ObjectFirst& first, const ObjectSecond& second):
              modified(false),
              complete(false),
              container(cont),
              key(key),
              first(first),
              second(second)
    {  
       configureOperations(OperationPolicy());
       standardAction(); 
       modified = true;
    }

    ~RelationshipAtomicity() 
    { 
       // If this object is being destroyed validate that
       // if the container has been modified but not marked 
       // as completed the changes made are backed-out.
       if (complete == false && modified == true){
           try{
             failureAction();
           }
           catch(...){

           }
       }   
   }

   // ***********************************************************************
   //! The group operation that was being marshalled by a set of these objects
   //! has been successfully completed by all parties. Therefore mark the 
   //! container modification undertaken by this object as being required. This
   //! will prevent the destructor from backing out the change. 
   // ***********************************************************************
   void completed() { complete = true; }


   private:
      // prevent copy and assignment
      RelationshipAtomicity(const  RelationshipAtomicity& rhs);
      RelationshipAtomicity& operator=(const RelationshipAtomicity& rhs);

      void addLinks()
      {
          typename Container::iterator linkItr = container.find(key);
          if (linkItr == container.end()){ 
              typename Container::mapped_type linkContainer;
              linkItr = container.insert(std::make_pair(key,linkContainer)).first;
          }
          linkItr->second.link(first,second);
      }

      void removeLinks()
      {
         typename Container::iterator linkItr = container.find(key);
         if (linkItr == container.end()){ 
            throw SqlException("RelationshipAtomicity::removeLinks(...) failed : no link to remove");
         }
         linkItr->second.unlink(first,second);
      }
      
      void configureOperations(LinkPolicy link)
      {
         standardAction = boost::bind(&RelationshipAtomicity::addLinks,::boost::ref(*this));
         failureAction  = boost::bind(&RelationshipAtomicity::removeLinks,::boost::ref(*this));
      }

      void configureOperations(UnlinkPolicy unlink)
      {
          standardAction = boost::bind(&RelationshipAtomicity::removeLinks,::boost::ref(*this));
          failureAction  = boost::bind(&RelationshipAtomicity::addLinks,::boost::ref(*this));
      }

   private:
       bool modified;
       bool complete;

       Container&   container;

       const ::SWA::IdType& key;
       const ObjectFirst&  first;
       const ObjectSecond& second;

       ::boost::function<void ()> standardAction;
       ::boost::function<void ()> failureAction;
};

} // end namepsace SQL
#endif
