#!/bin/sh
# -----------------------------------------------------------------------------
# ofbiz.sh - OFBIZ Wrapper for the Start/Stop Script for the JETTY Server
#
# Environment Variable Prequisites
#
#   OFBIZ_HOME    (Optional) May point at your ofbiz directory.
#                 If not present, $CATALINA_HOME/../ofbiz is assumed, in
#                 other words #CATALINA_HOME and the top 'ofbiz' directory
#                 are sibling directories.
#
#   JETTY_HOME    (Optional) May point at your Jetty "build" directory.
#                 If not present, the parent directory of the current working 
#                 directory is assumed.
#
#   JAVA_HOME     Must point at your Java Development Kit installation.
#
# $Id$
# -----------------------------------------------------------------------------

# Do this for Linux/Java conflicts
ulimit -s 2048

# ----- Verify and Set Required Environment Variables -------------------------

if [ -z "$JETTY_HOME" ] ; then
  export JETTY_HOME=".."
fi

if [ -z "$OFBIZ_HOME" ] ; then
  export OFBIZ_HOME="$JETTY_HOME/../ofbiz"
fi

if [ -z "$JAVA_HOME" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

# ----- Set JAVA_OPTIONS and Start Catalina -----------------------------------------

export JAVA_OPTIONS="$JAVA_OPTIONS -Dofbiz.home=$OFBIZ_HOME"

CP=`ls $OFBIZ_HOME/lib/share/*.jar | paste -s -d":" - `
CP=$CP:`ls $OFBIZ_HOME/lib/common/*.jar | paste -s -d":" - `
CP=$CP:$OFBIZ_HOME/lib/compile/xerces.jar:$OFBIZ_HOME/lib/compile/mail.jar
CP=$CP:$OFBIZ_HOME/lib/compile/jboss-j2ee.jar:$OFBIZ_HOME/lib/compile/jdbc2_0-stdext.jar
CP=$CP:$OFBIZ_HOME/lib/datavision/DataVision.jar:$OFBIZ_HOME/lib/datavision/MinML.jar:$OFBIZ_HOME/lib/datavision/jcalendar.jar
CP=$CP:$OFBIZ_HOME/lib/weka/weka.jar
CP=$CP:$OFBIZ_HOME/core/lib/ofbcore-share.jar
CP=$CP:$OFBIZ_HOME/core/lib/ofbcore-entity.jar
CP=$CP:$OFBIZ_HOME/core/lib/ofbcore-service.jar
CP=$CP:$OFBIZ_HOME/core/lib/ofbcore-extutil.jar
CP=$CP:$OFBIZ_HOME/core/lib/ofbcore-workflow.jar
CP=$CP:$OFBIZ_HOME/core/lib/ofbcore-rules.jar
CP=$CP:$OFBIZ_HOME/core/lib/ofbcore-datafile.jar
CP=$CP:$OFBIZ_HOME/core/lib/ofbcore-minilang.jar
CP=$CP:$OFBIZ_HOME/commonapp/lib/commonapp.jar 
CP=$CP:$OFBIZ_HOME/commonapp/etc

export CLASSPATH=$CLASSPATH:$CP

echo
echo CLASSPATH=$CLASSPATH

echo "ofbiz.sh - Running Jetty with the following options:"
echo " JAVA_HOME=$JAVA_HOME"
echo " JETTY_HOME=$JETTY_HOME"
echo " OFBIZ_HOME=$OFBIZ_HOME"
echo " JAVA_OPTIONS=$JAVA_OPTIONS"
echo " -- RUNNING $JETTY_HOME/bin/jetty.sh $1 --"

$JETTY_HOME/bin/jetty.sh $1 $JETTY_HOME/etc/ofbiz.xml

