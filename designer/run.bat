@echo off

echo Running Workflow designer ...
echo ------------------------------

if "%JAVA_HOME%" == "" goto java_home_error

if "%ANT_HOME%" == "" goto ant_home_error

if "%JDOM_HOME%" == "" goto jdom_home_error

set DESIGNER_ROOT_DIR=C:/put/your/designer/dir/here

set LOCALCLASSPATH=%DESIGNER_ROOT_DIR%\dist\nrl_designer.jar;%JAVA_HOME%\lib\tools.jar;%ANT_HOME%\lib\ant.jar;%JDOM_HOME%\lib\xerces.jar;%JDOM_HOME%\build\jdom.jar;%JDOM_HOME%\lib\jaxp.jar;%JDOM_HOME%\lib\crimson.jar;%ADDITIONALCLASSPATH%

echo Running with classpath %LOCALCLASSPATH%

set gifdir=%DESIGNER_ROOT_DIR%/gif
set xmldir=%DESIGNER_ROOT_DIR%/xml
set dtddir=%DESIGNER_ROOT_DIR%/dtd

set taskxml=DEMO2level
set taskname=DEMO2level
set domainxml=DefaultDomain

REM You can comment out one of these two to run the other. It doesn't matter which 
REM one you run because you can get at the other from either one.
REM "%JAVA_HOME%\bin\java.exe" -classpath "%LOCALCLASSPATH%" -DGIFDIR=%gifdir% -DWF_XMLDIR=%xmldir% -DWF_DTDDIR=%dtddir% org.ofbiz.designer.newdesigner.TaskEditor %taskxml% %taskname% %domainxml%
"%JAVA_HOME%\bin\java.exe" -classpath "%LOCALCLASSPATH%" -DGIFDIR=%gifdir% -DWF_XMLDIR=%xmldir% -DWF_DTDDIR=%dtddir% org.ofbiz.designer.newdesigner.NetworkEditor %taskxml% %taskname% %domainxml%

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
