
#!/bin/sh

# copy files from $OFBIZ_HOME to the orion directory to get them onto the classpath

if [ -z "$OFBIZ_HOME" ] ; then
  export OFBIZ_HOME="../ofbiz"
fi
if [ -z "$ORION_HOME" ] ; then
  export ORION_HOME="."
fi

# get all of the ofbiz jars
cp -i $OFBIZ_HOME/core/lib/ofbcore-share.jar $OFBIZ_HOME/core/lib/ofbcore-entity.jar $OFBIZ_HOME/core/lib/ofbcore-service.jar $OFBIZ_HOME/core/lib/ofbcore-extutil.jar $ORION_HOME/lib
cp -i $OFBIZ_HOME/core/lib/ofbcore-workflow.jar $OFBIZ_HOME/core/lib/ofbcore-rules.jar $OFBIZ_HOME/core/lib/ofbcore-datafile.jar $OFBIZ_HOME/core/lib/ofbcore-minilang.jar $ORION_HOME/lib
cp -i $OFBIZ_HOME/commonapp/lib/commonapp.jar $ORION_HOME/lib

# get all of the ofbiz config files
cp -i $OFBIZ_HOME/commonapp/etc/cache.properties $OFBIZ_HOME/commonapp/etc/debug.properties $OFBIZ_HOME/commonapp/etc/security.properties $ORION_HOME/lib
cp -i $OFBIZ_HOME/commonapp/etc/servicesengine.properties $OFBIZ_HOME/commonapp/etc/controlservlet.properties $ORION_HOME/lib
cp -i $OFBIZ_HOME/commonapp/etc/localdtds.properties $ORION_HOME/lib
#cp -i $OFBIZ_HOME/commonapp/etc/tyrexdomain.xml $ORION_HOME/lib

# get all of the third party jars
#cp -i $OFBIZ_HOME/lib/common/tyrex-1.0.jar $OFBIZ_HOME/lib/common/ots-jts_1.0.jar $ORION_HOME/lib
cp -i $OFBIZ_HOME/lib/common/castor-0.9.3.9.jar $OFBIZ_HOME/lib/common/hsqldb.jar $OFBIZ_HOME/lib/common/jdbc7.1-1.3.jar $ORION_HOME/lib
cp -i $OFBIZ_HOME/lib/common/mm.mysql-2.0.8-bin.jar $OFBIZ_HOME/lib/common/log4j.jar $ORION_HOME/lib
cp -i $OFBIZ_HOME/lib/share/axis.jar $OFBIZ_HOME/lib/share/clutil.jar $OFBIZ_HOME/lib/share/wsdl4j.jar $ORION_HOME/lib
cp -i $OFBIZ_HOME/lib/share/bsh-1.2b3.jar $OFBIZ_HOME/lib/share/jakarta-oro-2.0.4.jar $ORION_HOME/lib
cp -i $OFBIZ_HOME/lib/compile/xerces.jar $ORION_HOME/lib

