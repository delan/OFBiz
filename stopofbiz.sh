####
# $Id: stopofbiz.sh,v 1.2 2004/07/31 21:42:27 ajzeneski Exp $
# ofbiz.admin.key and ofbiz.admin.port must match that which OFBIZ was started with
####

# shutdown settings
ADMIN_PORT=10523
ADMIN_KEY=so3du5kasd5dn

$JAVA_HOME/bin/java -Dofbiz.admin.port=$ADMIN_PORT -Dofbiz.admin.key=$ADMIN_KEY -jar ofbiz.jar -shutdown

