#!/bin/sh

# console log file
OFBIZ_LOG=logs/console.log

# delete the last log
rm -f $OFBIZ_LOG

# VM args
VMARGS=-Xmx128M

# location of java executable
if [ -e $JAVA_HOME ]; then
  JAVA=$JAVA_HOME/bin/java
else
  JAVA=java
fi

# start ofbiz
$JAVA $VMARGS -jar ofbiz.jar >>$OFBIZ_LOG 2>>$OFBIZ_LOG&
exit 0

