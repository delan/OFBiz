JBOSS_CONFIG=ofbiz
JAVA_HOME=/usr/local/jdk
JBOSS_HOME=..
TOMCAT_HOME=../../tomcat
APP_HOME=../../ofbiz
export JBOSS_CONFIG JAVA_HOME TOMCAT_HOME APP_HOME

echo CLASSPATH before setup is $CLASSPATH

CLASSPATH=$CLASSPATH:$APP_HOME/entitygen/src
CLASSPATH=$CLASSPATH:$APP_HOME/commonapp/src
CLASSPATH=$CLASSPATH:$APP_HOME/commonapp/conf

CLASSPATH=$CLASSPATH:$JBOSS_HOME/bin/run.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/crimson.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/jaas.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/jaxp.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/jboss-jaas.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/jdbc2_0-stdext.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/jmxri.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/parser.jar

CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/activation.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/awt.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/classes12.zip
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/connector.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/dynaserver.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/ejb.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/exolabcore-0.1.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/gnu-regexp-1.0.8.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/hsql.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/idb.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/jboss.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/jbosscx-0.2.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/jbossmq.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/jbosssx.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/jms.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/jmxtools.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/jndi.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/jnpserver.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/jpl-util-0_5b.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/jta-spec1_0_1.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/log4j.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/mail.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/minerva-1_0b3.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/mm.mysql-2.0.4-bin.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/openjms-client-patched-0.5.1.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/openjms-patched-0.5.1.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/openjms-pool.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/openjms-rmi-patched-0.5.1.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/tomcat-service.jar
CLASSPATH=$CLASSPATH:$JBOSS_HOME/lib/ext/xml.jar

CLASSPATH=$CLASSPATH:$JBOSS_HOME/bin
CLASSPATH=$CLASSPATH:$JBOSS_HOME/conf
CLASSPATH=$CLASSPATH:$JBOSS_HOME/log
CLASSPATH=$CLASSPATH:$JBOSS_HOME/tmp
CLASSPATH=$CLASSPATH:$JBOSS_HOME/db

CLASSPATH=$CLASSPATH:$TOMCAT_HOME/lib/jasper.jar
CLASSPATH=$CLASSPATH:$TOMCAT_HOME/lib/jaxp.jar
CLASSPATH=$CLASSPATH:$TOMCAT_HOME/lib/parser.jar
CLASSPATH=$CLASSPATH:$TOMCAT_HOME/lib/servlet.jar
CLASSPATH=$CLASSPATH:$TOMCAT_HOME/lib/webserver.jar
CLASSPATH=$CLASSPATH:$TOMCAT_HOME/lib/com.sun.net.ssl.jar

CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/tools.jar
export CLASSPATH

echo CLASSPATH after setup is $CLASSPATH

JBOSS_CLASSPATH=$CLASSPATH
echo JBOSS_CLASSPATH after setup is $JBOSS_CLASSPATH

echo JBOSS_CONFIG=$JBOSS_CONFIG
echo JAVA_HOME=$JAVA_HOME
echo JBOSS_HOME=$JBOSS_HOME
echo TOMCAT_HOME=$TOMCAT_HOME
echo APP_HOME=$APP_HOME

nohup $JAVA_HOME/bin/java -server -classpath $CLASSPATH -Dtomcat.home=$TOMCAT_HOME  -Duser.dir=$JBOSS_HOME/bin org.jboss.Main $JBOSS_CONFIG > ../log/jboss.log 2>&1 &

echo > ../log/jboss.pids
i=0
for foo in $( ps -ef | grep " $$ " | grep jdk | grep -v grep )
do
 i=`expr $i + 1`
 if [ "$i" = "2" ]; then
   echo $foo > ../log/jboss.pid
 fi
 echo $foo >> ../log/jboss.pids
done                                                                            
