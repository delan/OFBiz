#! /bin/sh

classpath="$CLASSPATH":lib/DataVision.jar:lib/MinML2.jar:lib/jcalendar.jar:lib/jruby.jar:lib/gnu-regexp-1.1.4.jar

cd `dirname $0`
java -classpath $classpath jimm.datavision.DataVision $*
