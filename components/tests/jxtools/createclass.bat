rem Run in directory jxunit/src/net/sourceforge/jxunit
call quickClasspath.bat
java -cp %quickClasspath%;%QuickJARs%\jxunit3.jar com.jxml.quick.config.Main %1
