#!/bin/sh
####
# $Id: startofbiz.sh,v 1.7 2004/07/31 21:54:00 ajzeneski Exp $
####

# shutdown settings
ADMIN_PORT=10523
ADMIN_KEY=so3du5kasd5dn

# console log file
OFBIZ_LOG=logs/console.log

# delete the last log
rm -f $OFBIZ_LOG

# VM args
VMARGS="-Xmx128M -Dofbiz.admin.port=$ADMIN_PORT -Dofbiz.admin.key=$ADMIN_KEY"

# Worldpay Config
#VMARGS="-Xbootclasspath/p:components/accounting/lib/cryptix.jar $VMARGS"

# location of java executable
if [ -e $JAVA_HOME ]; then
  JAVA=$JAVA_HOME/bin/java
else
  JAVA=java
fi

# start ofbiz
$JAVA $VMARGS -jar ofbiz.jar >>$OFBIZ_LOG 2>>$OFBIZ_LOG&
#$JAVA $VMARGS -jar ofbiz.jar
exit 0

