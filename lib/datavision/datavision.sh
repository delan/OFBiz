#! /bin/sh

classpath="$CLASSPATH":DataVision.jar:MinML.jar:jcalendar.jar

cd `dirname $0`
java -classpath $classpath jimm.datavision.DataVision $*
