
if "%JBOSS_HOME%" == "" set JBOSS_HOME=..\..\jboss
if "%TOMCAT_HOME%" == "" set TOMCAT_HOME=..\..\tomcat
if "%OFBIZ_HOME%" == "" set OFBIZ_HOME=.

cd core
call ant
cd ..\commonapp
call ant
cd ..
call ant
