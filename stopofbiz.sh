#!/bin/sh
####
# $Id$
# ofbiz.admin.key and ofbiz.admin.port must match that which OFBIZ was started with
####

# shutdown settings
ADMIN_PORT=10523
ADMIN_KEY=so3du5kasd5dn

$JAVA_HOME/bin/java -Dofbiz.admin.port=$ADMIN_PORT -Dofbiz.admin.key=$ADMIN_KEY -jar ofbiz.jar -shutdown

