
if "%JBOSS_HOME%" == "" set JBOSS_HOME=..\..\jboss
if "%TOMCAT_HOME%" == "" set "%TOMCAT_HOME=..\..\tomcat

cd core
call ant deploy
cd ..\commonapp
call ant
cd ..
