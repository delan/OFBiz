#!/bin/sh
# -----------------------------------------------------------------------------
# ofbiz.sh - OFBIZ Wrapper for the Start/Stop Script for the CATALINA Server
#
# Environment Variable Prequisites
#
#   OFBIZ_HOME    (Optional) May point at your ofbiz directory.
#                 If not present, $CATALINA_HOME/../ofbiz is assumed, in
#                 other words #CATALINA_HOME and the top 'ofbiz' directory
#                 are sibling directories.
#
#   JAVA_HOME     Must point at your Java Development Kit installation.
#
# $Id$
# -----------------------------------------------------------------------------

# Do this for Linux/Java conflicts
ulimit -s 2048

# ----- Verify and Set Required Environment Variables -------------------------

if [ -z "$OFBIZ_HOME" ] ; then
  echo You must set OFBIZ_HOME to point at your Open For Business installation
  exit 1
  # export OFBIZ_HOME="../ofbiz"
fi

if [ -z "$JAVA_HOME" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

echo "ofbiz.sh - Running Orion with the following options:"
echo " JAVA_HOME=$JAVA_HOME"
echo " OFBIZ_HOME=$OFBIZ_HOME"
echo
#echo "CLASSPATH=$CLASSPATH"
echo
echo " -- RUNNING $JAVA_HOME/bin/java -Dofbiz.home=$OFBIZ_HOME -jar orion.jar $1 --"

$JAVA_HOME/bin/java -Dofbiz.home=$OFBIZ_HOME -jar orion.jar $1

