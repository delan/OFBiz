#!/bin/sh
####
# $Id$
####

# shutdown settings
ADMIN_PORT=10523
ADMIN_KEY=so3du5kasd5dn

# console log file
OFBIZ_LOG=logs/console.log

# delete the last log
rm -f $OFBIZ_LOG

# VM args
ADMIN="-Dofbiz.admin.port=$ADMIN_PORT -Dofbiz.admin.key=$ADMIN_KEY"
#DEBUG="-Dsun.rmi.server.exceptionTrace=true"
#RMIIF="-Djava.rmi.server.hostname=<set your IP address here>"
MEMIF="-Xms128M -Xmx128M"
VMARGS="$MEMIF $DEBUG $RMIIF $ADMIN"

# Worldpay Config
#VMARGS="-Xbootclasspath/p:applications/accounting/lib/cryptix.jar $VMARGS"

# location of java executable
if [ -e $JAVA_HOME ]; then
  JAVA=$JAVA_HOME/bin/java
else
  JAVA=java
fi

# start ofbiz
$JAVA $VMARGS -jar ofbiz.jar $* >>$OFBIZ_LOG 2>>$OFBIZ_LOG&
#$JAVA $VMARGS -jar ofbiz.jar
exit 0

