#! /bin/sh

classpath="$CLASSPATH":../common/postgresql.jar:../common/hsqldb.jar:DataVision.jar:MinML.jar:jcalendar.jar

cd `dirname $0`
$JAVA_HOME/bin/java -classpath $classpath jimm.datavision.DataVision $*

