#!/bin/sh
# -----------------------------------------------------------------------------
# ofbiz.sh - OFBIZ Wrapper for the Start/Stop Script for the RESIN Server
#
# Environment Variable Prequisites
#
#   OFBIZ_HOME    (Optional) May point at your Catalina "build" directory.
#                 If not present, $RESIN_HOME/../ofbiz is assumed, in
#                 other words #RESIN_HOME and the top 'ofbiz' directory
#                 are sibling directories.
#
#   JAVA_HOME     Must point at your Java Development Kit installation.
#
# $Id$
# -----------------------------------------------------------------------------

# Do this for Linux/Java conflicts
ulimit -s 2048

# ----- Verify and Set Required Environment Variables -------------------------
[ -r ~/ofbiz.rc ] && ~ ~/ofbiz.rc
[ -r ./ofbiz.rc ] && . ./ofbiz.rc

if [ -z "$OFBIZ_HOME" ] ; then
  export OFBIZ_HOME="../ofbiz"
fi

if [ -z "$JAVA_HOME" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

# ----- Set the CLASSPATH ----------------------------------------------------

CP=`ls $OFBIZ_HOME/lib/share/*.jar | paste -s -d":" - `
CP=$CP:`ls $OFBIZ_HOME/lib/common/*.jar | paste -s -d":" - `
CP=$CP:$OFBIZ_HOME/lib/compile/xerces.jar:$OFBIZ_HOME/lib/compile/mail.jar
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

# ----- Set RESIN_OPTS and Start Resin ----------------------------------------

export RESIN_OPTS="$RESIN_OPTS -Dofbiz.home=$OFBIZ_HOME" 
#export RESIN_OPTS="$RESIN_OPTS -Dofbiz.home=$OFBIZ_HOME -classpath $CP" 
# 

echo "ofbiz.sh - Running Resin with the following options:"
echo " JAVA_HOME=$JAVA_HOME"
echo " OFBIZ_HOME=$OFBIZ_HOME"
echo
echo " CLASSPATH=$CLASSPATH"
echo
echo " RESIN_OPTS=$RESIN_OPTS"
echo " -- RUNNING ./bin/httpd.sh $RESIN_OPTS $1 --"

./bin/httpd.sh $RESIN_OPTS $@
