
set newddir=.

set javastr=f:/jdk1.3.0_02/bin/java
set cpstr=%newddir%/build
REM %newddir%/lib/xml4j.jar

set gifdir=%newddir%/gif
set xmldir=%newddir%/xml
set dtddir=%newddir%/dtd

set taskxml=%xmldir%/task/testdb.xml
set taskname=testdb
set domainxml=%xmldir%/domainenv/DefaultDomain.xml

%javastr% -classpath %cpstr% -DGIFDIR=%gifdir% -DWF_XMLDIR=%xmldir% -DWF_DTDDIR=%dtddir% org.ofbiz.designer.newdesigner.NetworkEditor %taskxml% %taskname% %domainxml%
