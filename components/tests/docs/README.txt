OFBiz/JXUnit Extensions
Updated Nov. 23, 2003

Introduction

This test component is a suite of JXUnit extensions for testing OFBiz modules.
JXUnit is a JUnit extension that allows you to separate test data from test logic.
This allows a someone to create test in XML without requiring testing code.  The
OFBiz extensions are test drivers for creating OFBiz entities and running services.

JXUnit is not a replacement for any of the existing JUnit frameworks such as HttpUnit 
and Cactus.  Instead it is a different approach to testing. JXUnit just provides a convenient 
method for separating the data from the test.
 
You could use JXUnit to run an HttpUnit or Cactus test.


How It Works

JXUnit uses a special test driver called JXTestCase (net.sourceforge.jxunit.JXTestCase).
JXTestCase is a JUnit Test that can be run by the JUnit TestRunner. JXTestCase starts 
at the current directory and traverses the directory tree searching for a test.jxu 
file.  The test.jxu file contains the test for the directory.  If an error occurs in the
the test file (i.e. test.jxu) JXTestCase will go on to the next directory.  After JXTestCase
traverses the entire tree you will see a report showing the number of test passed and failed.
The easiest way to run a JXUnit test is to put the JXTest.bat in your %PATH% environment 
(see Installing below).  Then you can type JXTest anywhere in your test case tree and all 
tests in the current directory and below will be executed.



Installing

1. Unzip the tests directory into the ofbiz 3.0 components directory.  Your test directory
structure should look something like:

$OFBIZ_HOME/components/tests
$OFBIZ_HOME/components/tests/bin
$OFBIZ_HOME/components/tests/src
$OFBIZ_HOME/components/tests/docs
$OFBIZ_HOME/components/tests/scripts
$OFBIZ_HOME/components/tests/lib

2. Add the $OFBIZ_HOME/components/tests/bin directory to your $PATH environment variable.
This will allow you to run jxtest.bat (sorry I don't have a jxtest.sh script done yet) 
from any directory.


Running a Test

From the command line go to the tests/scripts directory and type jxtest.bat.  This 
will execute a test in the sample directory.  The sample test demonstrates how you
can create ofbiz entities and run ofbiz services by simplying creating some XML files.
You can create new tests by simply changing the data.  

