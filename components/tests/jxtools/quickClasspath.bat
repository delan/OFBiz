@echo off
REM Sets the quickClasspath variable
REM Called by other .bat files

REM Note: The QuickJARs variable must have been set, as well as the classpath.

set quickClasspath=%classpath%;%QuickJARs%\Quick4rt.jar;%QuickJARs%\Quick4util.jar;%QuickJARs%\dtdparser113.jar;%QuickJARs%\crimson.jar

