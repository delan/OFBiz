#!/bin/sh

# Do this for Linux/Java conflicts
ulimit -s 2048

# ----- Verify and Set Required Environment Variables -------------------------

if [ -z "$OFBIZ_HOME" ] ; then
  export OFBIZ_HOME="../../ofbiz"
fi

if [ -z "$JAVA_HOME" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

# ----- Set the CLASSPATH ----------------------------------------------------

JBOSS_CLASSPATH=$JBOSS_CLASSPATH:$JAVA_HOME/lib/tools.jar
export JBOSS_CLASSPATH

# Minimal jar file to get JBoss started.

JBOSS_CLASSPATH=$JBOSS_CLASSPATH:run.jar

# Add all login modules for JAAS-based security
# and all libraries that are used by them here
JBOSS_CLASSPATH=$JBOSS_CLASSPATH

# Check for SUN(tm) JVM w/ HotSpot support
#
HOTSPOT=`java -version 2>&1 | grep HotSpot`"x"
if [ "$HOTSPOT" != "x" ]; then
       HOTSPOT="-server"
else
       HOTSPOT=""
fi

# Add the XML parser jars and set the JAXP factory names
# Crimson parser JAXP setup(default)
JBOSS_CLASSPATH=$JBOSS_CLASSPATH:../lib/crimson.jar
JAXP=-Djavax.xml.parsers.DocumentBuilderFactory=org.apache.crimson.jaxp.DocumentBuilderFactoryImpl
JAXP="$JAXP -Djavax.xml.parsers.SAXParserFactory=org.apache.crimson.jaxp.SAXParserFactoryImpl"

echo "ofbiz.sh - Running JBoss with the following options:"
echo " JAVA_HOME=$JAVA_HOME"
echo " OFBIZ_HOME=$OFBIZ_HOME"
echo
echo "  JBOSS_CLASSPATH=$JBOSS_CLASSPATH"
echo
echo " -- RUNNING java $HOTSPOT $JAXP -Dofbiz.home=$OFBIZ_HOME -classpath $JBOSS_CLASSPATH org.jboss.Main ofbiz $@ --"
echo

java $HOTSPOT $JAXP -Dofbiz.home=$OFBIZ_HOME -classpath $JBOSS_CLASSPATH org.jboss.Main ofbiz $@
    
