#!/bin/bash
#uncomment below to set QuickJars
#export  PATH=$PATH:(jxunit installation directory)/bin
#export QuickJARs=(jxunit installation directory)/JARs
java -cp $CLASSPATH:$QuickJARs/jxunit3.jar:$QuickJARs/Quick4rt.jar:$QuickJARs/Quick4util.jar:$QuickJARs/junit.jar:$QuickJARs/crimson.jar:$QuickJARs/jaxp.jar:$QuickJARs/jakarta-regexp-1.2.jar junit.textui.TestRunner net.sourceforge.jxunit.JXTestCase
echo $PATH

