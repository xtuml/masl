//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   Transaction.hh
//
//============================================================================//
#ifndef Sqlite_Transaction_HH
#define Sqlite_Transaction_HH

namespace SQLITE {

class Transaction 
{
   private:
      Transaction();

      void startTransaction( const std::string& name );
      void committingTransaction();
      void commitTransaction();
      void abortTransaction();

    public:
      static Transaction& getInstance();
      static bool initialise();
};

} // end namespace SQLITE

#endif
