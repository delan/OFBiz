@echo off

rem uncomment the following and set them properly. if you want...
set OFBIZ_HOME=c:\work\ofbiz
set RESIN_HOME=c:\resin

if "%OFBIZ_HOME%" == "" goto setofbizhome
goto doneofbizhome
:setofbizhome
set OFBIZ_HOME=..\ofbiz
:doneofbizhome

if "%RESIN_HOME%" == "" goto setresinhome
goto doneresinhome
:setresinhome
set RESIN_HOME=.
:doneresinhome

rem ----- Set the CLASSPATH ----------------------------------------------------

set _CLASSPATH=%CLASSPATH%
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\axis.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\batik.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\cactus.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\clutil.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\cvslib.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\dom4j.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\fop.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\jakarta-oro-2.0.6.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\jakarta-poi-1.5.1-final.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\jaxrpc.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\junit.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\lucene-1.2.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\saaj.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\xalan.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\freemarker.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\EdenLib.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\JPublish.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\velocity-1.3.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\velocity-dep-1.3.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\share\wsdl4j.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\scripting\bsh.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\scripting\bsf.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\scripting\js.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\scripting\jacl.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\scripting\jython.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\jotm\carol.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\jotm\commons-cli.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\jotm\enhydra-jdbc.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\jotm\jonas_timer.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\jotm\jotm_iiop_stubs.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\jotm\jotm_jrmp_stubs.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\jotm\jotm.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\jotm\monolog.jar
rem set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\tyrex\castor-0.9.3.9.jar
rem set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\tyrex\tyrex-1.0.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-beanutils.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-collections.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-dbcp.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-digester.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-discovery.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-fileupload.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-httpclient.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-logging.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-pool.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-vfs.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\commons-vfs-providers.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\hsqldb.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\jms_1.0.2a.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\jta_1.0.1.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\log4j.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\mm.mysql-2.0.14-bin.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\ots-jts_1.0.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\postgresql.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\common\sapdbc.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\compile\xerces.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\compile\mail.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\compile\activation.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\datavision\DataVision.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\datavision\MinML.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\datavision\jcalendar.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\jasperreports\jasperreports.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\jasperreports\itext-0.81.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\lib\weka\weka.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\core\lib\ofbcore-share.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\core\lib\ofbcore-entity.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\core\lib\ofbcore-service.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\core\lib\ofbcore-extutil.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\core\lib\ofbcore-extentity.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\core\lib\ofbcore-webapp.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\core\lib\ofbcore-workflow.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\core\lib\ofbcore-rules.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\core\lib\ofbcore-datafile.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\core\lib\ofbcore-minilang.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\commonapp\lib\commonapp.jar
set CLASSPATH=%CLASSPATH%;%OFBIZ_HOME%\commonapp\etc

rem ----- Set RESIN_OPTS and Start Resin ----------------------------------------

set _RESIN_OPTS=%RESIN_OPTS%
set RESIN_OPTS=%RESIN_OPTS% -Dofbiz.home=%OFBIZ_HOME%
rem #export RESIN_OPTS="$RESIN_OPTS -Dofbiz.home=$OFBIZ_HOME -classpath $CP" 

echo Running Resin with the following options:
echo JAVA_HOME=%JAVA_HOME%
echo OFBIZ_HOME=%OFBIZ_HOME%
echo.
echo CLASSPATH=%CLASSPATH%
echo.
echo RESIN_OPTS=%RESIN_OPTS%
echo RESIN_HOME=%RESIN_HOME%
echo.

%RESIN_HOME%\bin\httpd %RESIN_OPTS% %1 %2 %3 %4 %5

set CLASSPATH=%_CLASSPATH%
set RESIN_OPTS=%_RESIN_OPTS%
