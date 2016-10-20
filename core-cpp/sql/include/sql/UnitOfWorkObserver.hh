//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   UnitOfWorkObserver.hh
//
// Description:             
// 
//============================================================================//
#ifndef Sql_UnitOfWorkObserver_HH
#define Sql_UnitOfWorkObserver_HH

#include <string>

namespace SQL {

// ***********************************************************************
// ***********************************************************************
class UnitOfWorkContext
{
   public:
       UnitOfWorkContext(std::string& statements):statements_(statements){}
      ~UnitOfWorkContext(){}

      std::string&  getStatements() { return statements_; }

   private:
       std::string& statements_;
};


// ***********************************************************************
// ***********************************************************************
class UnitOfWorkObserver
{
   public:
        virtual void flush             (UnitOfWorkContext& context) = 0;
        virtual void committed         (UnitOfWorkContext& context) = 0;
        virtual void startTransaction  (UnitOfWorkContext& context) = 0;
        virtual void commitTransaction (UnitOfWorkContext& context) = 0;
        virtual void abortTransaction  (UnitOfWorkContext& context) = 0;

   private:
       // Disable copy and assignment
       UnitOfWorkObserver(const UnitOfWorkObserver& rhs);
       UnitOfWorkObserver& operator=(const UnitOfWorkObserver& rhs);

   protected:
               UnitOfWorkObserver(){}
      virtual ~UnitOfWorkObserver(){}   // do not allow deletion using a pointer to this base class
};


} // end namepsace SQL

#endif
