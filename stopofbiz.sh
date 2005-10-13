#!/bin/sh
####
# $Id$
# ofbiz.admin.key and ofbiz.admin.port must match that which OFBIZ was started with
####

# location of java executable
if [ -f "$JAVA_HOME/bin/java" ]; then
  JAVA=$JAVA_HOME/bin/java
else
  JAVA=java
fi                                                                                                                                                                                         

# shutdown settings
ADMIN_PORT=10523
ADMIN_KEY=so3du5kasd5dn

$JAVA -Dofbiz.admin.port=$ADMIN_PORT -Dofbiz.admin.key=$ADMIN_KEY -jar ofbiz.jar -shutdown

