
#!/bin/sh

# remove files from $ORION_HOME directory that came from OFBiz

if [ -z "$ORION_HOME" ] ; then
  export ORION_HOME="."
fi

# get all of the ofbiz jars
rm $ORION_HOME/lib/ofbcore-share.jar $ORION_HOME/lib/ofbcore-entity.jar $ORION_HOME/lib/ofbcore-service.jar $ORION_HOME/lib/ofbcore-extutil.jar
rm $ORION_HOME/lib/ofbcore-workflow.jar $ORION_HOME/lib/ofbcore-rules.jar $ORION_HOME/lib/ofbcore-datafile.jar $ORION_HOME/lib/ofbcore-minilang.jar
rm $ORION_HOME/lib/commonapp.jar

# get all of the ofbiz config files
rm $ORION_HOME/lib/cache.properties $ORION_HOME/lib/debug.properties $ORION_HOME/lib/security.properties
rm $ORION_HOME/lib/servicesengine.properties $ORION_HOME/lib/controlservlet.properties
rm $ORION_HOME/lib/localdtds.properties 
#rm $ORION_HOME/lib/tyrexdomain.xml

# get all of the third party jars
#rm $ORION_HOME/lib/tyrex-1.0.jar $ORION_HOME/lib/ots-jts_1.0.jar
rm $ORION_HOME/lib/castor-0.9.3.9.jar $ORION_HOME/lib/hsqldb.jar $ORION_HOME/lib/jdbc7.1-1.3.jar
rm $ORION_HOME/lib/mm.mysql-2.0.8-bin.jar $ORION_HOME/lib/log4j.jar
rm $ORION_HOME/lib/axis.jar $ORION_HOME/lib/clutil.jar $ORION_HOME/lib/wsdl4j.jar
rm $ORION_HOME/lib/bsh-1.2b3.jar $ORION_HOME/lib/jakarta-oro-2.0.4.jar
rm $ORION_HOME/lib/xerces.jar

