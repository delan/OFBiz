#!/bin/sh
# -----------------------------------------------------------------------------
# ofbiz.sh - OFBIZ Wrapper for the Start/Stop Script for the CATALINA Server
#
# Environment Variable Prequisites
#
#   OFBIZ_HOME    (Optional) May point at your Catalina "build" directory.
#                 If not present, $CATALINA_HOME/../ofbiz is assumed, in
#                 other words #CATALINA_HOME and the top 'ofbiz' directory
#                 are sibling directories.
#
#   CATALINA_HOME (Optional) May point at your Catalina "build" directory.
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
[ -r ~/ofbiz.rc ] && ~ ~/ofbiz.rc
[ -r ./ofbiz.rc ] && . ./ofbiz.rc

if [ -z "$CATALINA_HOME" ] ; then
  export CATALINA_HOME=".."
fi

if [ -z "$OFBIZ_HOME" ] ; then
  export OFBIZ_HOME="$CATALINA_HOME/../ofbiz"
fi

if [ -z "$JAVA_HOME" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

# ----- Set CATALINA_OPTS and Start Catalina -----------------------------------------

export CATALINA_OPTS="$CATALINA_OPTS -Dofbiz.home=$OFBIZ_HOME"

echo "ofbiz.sh - Running Catalina with the following options:"
echo " JAVA_HOME=$JAVA_HOME"
echo " CATALINA_HOME=$CATALINA_HOME"
echo " OFBIZ_HOME=$OFBIZ_HOME"
echo " CATALINA_OPTS=$CATALINA_OPTS"
echo " -- RUNNING $CATALINA_HOME/bin/catalina.sh $1 --"

$CATALINA_HOME/bin/catalina.sh $@
