@echo off
rem set path=%path%;(JXUnit installation directory)\bin
rem uncomment below to set QuickJars
rem set QuickJARs=(JXUnit installation directory)\JARs
set JXTEST_HOME=c:\dev\ofbiz\ofbiz.30\ofbiz\components\tests
set QuickJARS=c:\dev\ofbiz\ofbiz.30\ofbiz\components\tests\lib
set OFBIZ_BASE=c:\dev\ofbiz\ofbiz.30\ofbiz\base
set OFBIZ_COMP=c:\dev\ofbiz\ofbiz.30\ofbiz\components

rem JXTEST_HOME=%OFBIZ_HOME%\components\tests
rem QuickJARS=%JXTEST_HOME%\lib
set path=%path%;%JXTEST_HOME%\bin


set JXUNIT_CP=%QuickJARs%\Quick4rt.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\Quick4util.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\Quick4rt.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\dom4j-full.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\jxunit3.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\jxweb.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\httpunit.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\Tidy.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\junitperf.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\junit.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\crimson.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\jakarta-regexp-1.2.jar;
set JXUNIT_CP=%JXUNIT_CP%\%QuickJARS%\dtdparser113.jar;

set OFBIZ_CP=%JXTEST_HOME%\build\lib\ofbiz-tests.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\build\lib\ofbiz-base.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\build\lib\ofbiz.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\lib\xerces.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\lib\servlet.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\lib\activate.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\lib\logging\log4j.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\lib\logging\commons-logging.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\lib\scripting\bsh.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\lib\scripting\bsf.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\lib\scripting\js.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\lib\scripting\oro.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_BASE%\config
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\entity\dtd
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\entity\build\lib\ofbiz-entity.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\entityext\dtd
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\entity\lib\jdbc\mm.mysql-2.0.14-bin.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\entity\lib\jotm\jotm.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\lib\common\classes12.zip
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\minerva\lib\jta_1.0.1.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\minerva\lib\oswego-concurrent.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\minerva\build\lib\ofbiz-minerva.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\service\build\lib\ofbiz-service.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_COMP%\security\build\lib\ofbiz-security.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\lib\common\commons-logging.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\lib\jotm\monolog.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\lib\jotm\enhydra-jdbc.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\lib\compile\xerces.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\lib\compile\servlet.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\lib\scripting\bsh.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\core\lib\ofbcore-datafile.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\core\lib\ofbcore-entity.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\core\lib\ofbcore-extentity.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\core\lib\ofbcore-extutil.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\core\lib\ofbcore-service.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\core\lib\ofbcore-share.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\core\lib\ofbcore-minilang.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\commonapp\lib\commonapp.jar
rem set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\commonapp\etc

rem Third party libraries can go here.
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\in2mcommon\build\in2mcommon.jar
set OFBIZ_CP=%OFBIZ_CP%;%OFBIZ_HOME%\in2mcommon\lib\teknowledge\TPClient2.jar

set CP=%JXUNIT_CP%;%OFBIZ_CP%;%classpath%
rem set CP=%OFBIZ_CP%;%classpath%
echo CLASSPATH=%CP%
echo OFBIZ_HOME=%OFBIZ_HOME%
echo OFBIZ_BASE=%OFBIZ_BASE%

java -Dofbiz.home=%OFBIZ_HOME% -cp %CP% junit.textui.TestRunner net.sourceforge.jxunit.JXTestCase
