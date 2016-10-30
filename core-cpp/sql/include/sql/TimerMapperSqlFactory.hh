//============================================================================//
// UK Crown Copyright (c) 2008. All rights reserved.
//
// File:   TimerMapperSqlFactory.hh
//
//============================================================================//
#ifndef Sql_TimermapperSqlFactory_HH
#define Sql_TimermapperSqlFactory_HH

#include "boost/shared_ptr.hpp"

namespace SQL {

class TimerMapperSql;
class TimerMapperSqlFactory
{
   public:
     static TimerMapperSqlFactory& singleton();
     
     bool registerImpl(const boost::shared_ptr<TimerMapperSql>& impl);

     boost::shared_ptr<TimerMapperSql>& getImpl();

   private:
       TimerMapperSqlFactory();
      ~TimerMapperSqlFactory();

   private:
      TimerMapperSqlFactory(const TimerMapperSqlFactory& rhs);
      TimerMapperSqlFactory& operator=(const TimerMapperSqlFactory& rhs);

   private:
     boost::shared_ptr<TimerMapperSql> impl_;
};

}  // end namespace SQL

#endif
