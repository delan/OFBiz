#!/bin/sh
#set path=$path:(JXUnit installation directory)\bin
#uncomment below to set QuickJars
#set QuickJARs=(JXUnit installation directory)\JARs
JXTEST_HOME=$OFBIZ_HOME/components/tests
QuickJARS=$JXTEST_HOME/lib
OFBIZ_BASE=$OFBIZ_HOME/base
OFBIZ_COMP=$OFBIZ_HOME/components

# JXTEST_HOME=$OFBIZ_HOME/components/tests
# QuickJARS=$JXTEST_HOME$/lib
path=$path:$JXTEST_HOME/bin


JXUNIT_CP=$QuickJARs/Quick4rt.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/Quick4util.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/Quick4rt.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/dom4j-full.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/jxunit3.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/jxweb.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/httpunit.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/Tidy.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/junitperf.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/junit.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/crimson.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/jakarta-regexp-1.2.jar:
JXUNIT_CP=$JXUNIT_CP/$QuickJARS/dtdparser113.jar:

OFBIZ_CP=$JXTEST_HOME/build/lib/ofbiz-tests.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/build/lib/ofbiz-base.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/build/lib/ofbiz.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/lib/xerces.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/lib/servlet.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/lib/activate.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/lib/logging/log4j.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/lib/logging/commons-logging.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/lib/scripting/bsh.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/lib/scripting/bsf.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/lib/scripting/js.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/lib/scripting/oro.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_BASE/config
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/entity/dtd
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/entity/build/lib/ofbiz-entity.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/entityext/dtd
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/entity/lib/jdbc/mm.mysql-2.0.14-bin.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/entity/lib/jotm/jotm.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/entity/lib/jotm/jotm_iiop_stubs.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/entity/lib/jotm/jotm_jrmp_stubs.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/entity/lib/dbcp/commons-pool.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/entity/lib/dbcp/commons-dbcp.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/entity/lib/dbcp/commons-collections.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/lib/common/classes12.zip
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/minerva/lib/jta_1.0.1.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/minerva/lib/oswego-concurrent.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/minerva/build/lib/ofbiz-minerva.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/service/build/lib/ofbiz-service.jar
OFBIZ_CP=$OFBIZ_CP:$OFBIZ_COMP/security/build/lib/ofbiz-security.jar

# Third party libraries can go here.
# set OFBIZ_CP=$OFBIZ_CP:$OFBIZ_HOME/somejar.jar

CP=$JXUNIT_CP:$OFBIZ_CP:$classpath
# set CP=$OFBIZ_CP:$classpath
# echo CLASSPATH=$CP
echo OFBIZ_HOME=$OFBIZ_HOME
echo OFBIZ_BASE=$OFBIZ_BASE

# java -Dofbiz.home=$OFBIZ_HOME -cp $CP junit.textui.TestRunner net.sourceforge.jxunit.JXTestCase

java -cp $CP junit.textui.TestRunner net.sourceforge.jxunit.JXTestCase
