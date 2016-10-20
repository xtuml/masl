#!/bin/bash
#
# UK Crown Copyright (c) 2016. All Rights Reserved
#

#
# Calculate and store the platform infomration the test 
# cases are being run on
#
PLATFORM=$(uname -s)-$(uname -m)


#
# Check that the two required directories are present.
#
if [ -d lib ] && [ -d src ]; then

    #
    # Create the Makefile in all of the build directories
    # and run the top level makefile
    #
    hmake;

    #
    # Execute all found testcases from the src directory.
    # all test cases should follow the same naming convension
    # of swaTestCases_[0-9]*
    #
    for aTestCase in $(ls src/$PLATFORM/swaTestCases_[1-9]*)
    do
       if [ -x $aTestCase ]; then
          $aTestCase;
       else
         echo "Failed to execute test executable $aTestCase";
       fi
    done;

    #
    # Report the code coverage metrics for the tested source.
    #
    echo 
    echo 
    echo "Reporting Code Coverage Stats ..."
    (cd lib/$PLATFORM && hmake gcov)
    
else
  echo "Failed to find test directory lib or src";
fi
