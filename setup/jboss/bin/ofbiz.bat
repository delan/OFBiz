@echo off
@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set JBOSS_CONFIG=ofbiz
set JAVA_HOME=F:\jdk1.3.0_02
set JBOSS_HOME=..
set TOMCAT_HOME=..\..\tomcat
set APP_HOME=..\..\ofbiz

echo CLASSPATH before setup is %CLASSPATH%

set CLASSPATH=%CLASSPATH%;%APP_HOME%\entitygen\src
set CLASSPATH=%CLASSPATH%;%APP_HOME%\core\src
set CLASSPATH=%CLASSPATH%;%APP_HOME%\commonapp\src
set CLASSPATH=%CLASSPATH%;%APP_HOME%\commonapp\conf

set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\bin\run.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\crimson.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\jaas.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\jaxp.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\jboss-jaas.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\jdbc2_0-stdext.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\jmxri.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\parser.jar

set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\activation.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\awt.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\classes12.zip
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\connector.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\dynaserver.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\ejb.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\exolabcore-0.1.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\gnu-regexp-1.0.8.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\hsql.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\idb.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\jboss.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\jbosscx-0.2.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\jbossmq.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\jbosssx.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\jms.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\jmxtools.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\jndi.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\jnpserver.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\jpl-util-0_5b.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\jta-spec1_0_1.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\log4j.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\mail.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\minerva-1_0b3.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\mm.mysql-2.0.4-bin.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\openjms-client-patched-0.5.1.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\openjms-patched-0.5.1.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\openjms-pool.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\openjms-rmi-patched-0.5.1.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\tomcat-service.jar
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\lib\ext\xml.jar

set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\bin
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\conf
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\log
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\tmp
set CLASSPATH=%CLASSPATH%;%JBOSS_HOME%\db

set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\jasper.jar
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\jaxp.jar
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\parser.jar
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\servlet.jar
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\webserver.jar
set CLASSPATH=%CLASSPATH%;%TOMCAT_HOME%\lib\com.sun.net.ssl.jar

set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar

echo CLASSPATH after setup is %CLASSPATH%

set JBOSS_CLASSPATH=%CLASSPATH%
echo JBOSS_CLASSPATH after setup is %JBOSS_CLASSPATH%

echo JBOSS_CONFIG=%JBOSS_CONFIG%
echo JAVA_HOME=%JAVA_HOME%
echo JBOSS_HOME=%JBOSS_HOME%
echo TOMCAT_HOME=%TOMCAT_HOME%
echo APP_HOME=%APP_HOME%

%JAVA_HOME%\bin\java -classpath %CLASSPATH% -Dtomcat.home=%TOMCAT_HOME%  -Duser.dir=%JBOSS_HOME%/bin org.jboss.Main %JBOSS_CONFIG%
