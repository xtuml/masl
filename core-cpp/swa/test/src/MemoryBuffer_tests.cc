//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "boost/test/unit_test.hpp"

#include "MemoryBuffer.hh"

// *************************************************************************************
// *************************************************************************************
BOOST_AUTO_TEST_SUITE( CodecBuffer_Test_Suite );

// *************************************************************************************
// *************************************************************************************
BOOST_AUTO_TEST_CASE( testcase_1 ) 
{ 
   // General usage test 1
   int testData1    = 1224;
   unsigned char testData2[] = "hello world";

   ::SWA::MemoryBuffer buffer1;
   buffer1.add(reinterpret_cast<unsigned char*>(&testData1),sizeof(testData1));
   buffer1.add(testData2,sizeof(testData2));

   BOOST_CHECK_EQUAL(buffer1.getUsed(),sizeof(testData1) + sizeof(testData2) );

   buffer1.clear();

   BOOST_CHECK_EQUAL(buffer1.getUsed(),0);

}


// *************************************************************************************
// *************************************************************************************
BOOST_AUTO_TEST_CASE( testcase_2 ) 
{ 
   // General usage test 2
   int testData1    = 1224;
   unsigned char testData2[] = "hello world";

   ::SWA::MemoryBuffer buffer1(reinterpret_cast<unsigned char*>(&testData1),sizeof(testData1));
   BOOST_CHECK_EQUAL(buffer1.getUsed(),sizeof(testData1) );   
   buffer1.clear();
   
   const int32_t packedIntCount = 1000;
   for(int32_t x = 0; x < packedIntCount; ++x){
       buffer1.add(reinterpret_cast<unsigned char*>(&x),sizeof(x));
   }

   BOOST_CHECK_EQUAL(buffer1.getUsed(),sizeof(int32_t)*packedIntCount);

   buffer1.clear();

   BOOST_CHECK_EQUAL(buffer1.getUsed(),0);
}

// *************************************************************************************
// *************************************************************************************
BOOST_AUTO_TEST_SUITE_END();
