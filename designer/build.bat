@echo off

echo Workflow designer Build system
echo ------------------------------

if "%JAVA_HOME%" == "" goto java_home_error

if "%ANT_HOME%" == "" goto ant_home_error

if "%JDOM_HOME%" == "" goto jdom_home_error

set LOCALCLASSPATH=%JAVA_HOME%\lib\tools.jar;%ANT_HOME%\lib\ant.jar;%JDOM_HOME%\lib\xerces.jar;%JDOM_HOME%\build\jdom.jar;%JDOM_HOME%\lib\jaxp.jar;%JDOM_HOME%\lib\crimson.jar;%ADDITIONALCLASSPATH%

echo Building with classpath %LOCALCLASSPATH%

echo Starting Ant...

"%JAVA_HOME%\bin\java.exe" -Dant.home="%ANT_HOME%" -classpath "%LOCALCLASSPATH%" org.apache.tools.ant.Main %1 %2 %3 %4 %5

goto end

:java_home_error

echo JAVA_HOME: JAVA_HOME not found in your environment.
echo Please, set the JAVA_HOME variable in your environment to match the
echo location of the Java Virtual Machine you want to use.
goto end

:ant_home_error

echo ANT_HOME: ANT_HOME not found in your environment.
echo Please, set the ANT_HOME variable in your environment to match the
echo location of the Ant you want to use.
goto end

:jdom_home_error

echo JDOM_HOME: JDOM_HOME not found in your environment.
echo Please, set the JDOM_HOME variable in your environment to match the
echo location of the Jdom you want to use.

:end

set LOCALCLASSPATH=
