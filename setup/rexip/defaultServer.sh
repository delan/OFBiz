#!/bin/bash
export REXIP_HOME=/home/jaz/appsvr/TCC/rexip

# Script to start server
#############################

#############################
# Set the rexip directory
#############################

export REXIP_HOME=$REXIP_HOME

#############################
# Set the domain directory
#############################
# The default is current directory

export REXIP_DOMAIN_HOME=.

#############################
# Set the rexip mode
#############################
# There are two valid rexip modes. They are "admin" and "managedserver".
#  Mode "admin" incdicates that admin webapp and managed webapp will be included at
#  server startup. Mode "managedserver" indicates that only managed webapp will be 
#  included at server startup. 

export REXIP_MODE=admin

#############################
# Set the server
#############################
# It specifies the server to be started.
export REXIP_SERVER=defaultServer

#############################
# Call the rexip home environment
#############################
. $REXIP_HOME/bin/rexipenv.sh

#############################
# Setup the OFB paths
#############################
if [ -z "$OFBIZ_HOME" ] ; then
  export OFBIZ_HOME="../../../../ofbiz"
fi
CP=`ls $OFBIZ_HOME/lib/share/*.jar | paste -s -d":" - `
CP=$CP:`ls $OFBIZ_HOME/lib/common/*.jar | paste -s -d":" - `
CP=$CP:$OFBIZ_HOME/lib/compile/xerces.jar:$OFBIZ_HOME/lib/compile/mail.jar
CP=$CP:$OFBIZ_HOME/lib/datavision/DataVision.jar:$OFBIZ_HOME/lib/datavision/MinML.jar:$OFBIZ_HOME/lib/datavision/jcalendar.jar
CP=$CP:$OFBIZ_HOME/lib/jasperreports/jasperreports.jar:$OFBIZ_HOME/lib/jasperreports/itext-0.81.jar
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

"$JAVA_HOME/bin/java" -classpath "$CP:$REXIP_HOME"/lib/tcc.jar:"$JAVA_HOME"/lib/tools.jar:"$REXIP_DOMAIN_HOME"/lib/hsqldb.jar -Drexip.home=$REXIP_HOME -Drexip.server=$REXIP_SERVER -Drexip.domain.home=$REXIP_DOMAIN_HOME -Drexip.mode=$REXIP_MODE -Dofbiz.home=$OFBIZ_HOME com.tcc.Server
