#!/bin/sh
# $Id$
#
# This script can be used to start WebLogic Server. This script ensures
# that the server is started using the config.xml file found in this
# directory and that the CLASSPATH is set correctly. This script contains
# the following variables:
#
# OFBIZ_HOME     - Must point at your ofbiz directory.
# WL_HOME        - The root directory of your WebLogic Server
#                  installation
# JAVA_HOME      - Determines the version of Java used to start
#                  WebLogic Server. This variable must point to the
#                  root directory of a JDK installation and will be set
#                  for you by the WebLogic Server installer.
#                  See the WebLogic platform support
#                  page (http://e-docs.bea.com/wls/platforms/index.html)
#                  for an up-to-date list of supported JVMs on your platform.
# JAVA_OPTIONS   - Java command-line options for running the server.
#
# jDriver for Oracle users: This script assumes that native libraries required 
# for jDriver for Oracle have been installed in the proper location and that 
# your os specific library path variable (i.e. LD_LIBRARY_PATH/solaris, 
# SHLIB_PATH/hpux, etc...) has been set appropriately.  Also note that this 
# script defaults to the oci816_8 version of the shared libraries. If this is 
# not the version you need, please adjust the library path variable 
# accordingly.  

# For additional information, refer to Installing and Setting up WebLogic 
# Server (http://e-docs.bea.com/wls/docs61/install/index.html).

if [ -z "$OFBIZ_HOME" ] ; then
  echo You must set OFBIZ_HOME to point at your Open For Business installation
  exit 1
fi

# Set user-defined variables.
JAVA_HOME=/usr/java/jdk1.3.1_01
WL_HOME=/ofbiz/bea/wlserver6.1
JAVA_OPTIONS="-ms64m -mx64m -Dofbiz.home=$OFBIZ_HOME"


# Check that script is being run from the appropriate directory
if [ ! -f config.xml ]; then
  echo "startWeblogic.sh: must be run from the config/mydomain directory." 1>&2

# Check for classes
elif [ ! -f $WL_HOME/lib/weblogic.jar ]; then
  echo "The weblogic.jar file was not found in directory $WL_HOME/lib." 1>&2

# Check for JDK
elif [ ! -f $JAVA_HOME/bin/javac ]; then
  echo "The JDK wasn't found in directory $JAVA_HOME." 1>&2

else
cd ../..

# Grab some file descriptors.
if [ "`uname -s`" != "OSF1" ]; then
  maxfiles=`ulimit -H -n`
else
  maxfiles=`ulimit -n`
fi
if [ !$? -a "$maxfiles" != 1024 ]; then
  if [ "$maxfiles" = "unlimited" ]; then
    maxfiles=1025
  fi
  if [ "$maxfiles" -lt 1024 ]; then
    ulimit -n $maxfiles
  else
    ulimit -n 1024
  fi
fi

# Figure out how to use our shared libraries
case `uname -s` in
AIX)
  if [ -n "$LIBPATH" ]; then
    LIBPATH=$LIBPATH:$WL_HOME/lib/aix:$WL_HOME/lib/aix/oci816_8
  else
    LIBPATH=$WL_HOME/lib/aix:$WL_HOME/lib/aix/oci816_8
  fi
  PATH=$WL_HOME/lib/aix:$PATH
  export LIBPATH PATH
  echo "LIBPATH=$LIBPATH"
  export AIXTHREAD_SCOPE=S
  JAVA_OPTIONS="-Xbootclasspath/p:$WL_HOME/lib/aix/4346224.jar $JAVA_OPTIONS"
;;
HP-UX)
  if [ -n "$SHLIB_PATH" ]; then
    SHLIB_PATH=$SHLIB_PATH:$WL_HOME/lib/hpux11:$WL_HOME/lib/hpux11/oci816_8
  else
    SHLIB_PATH=$WL_HOME/lib/hpux11:$WL_HOME/lib/hpux11/oci816_8
  fi
  PATH=$WL_HOME/lib/hpux11:$PATH
  export SHLIB_PATH PATH
  echo "SHLIB_PATH=$SHLIB_PATH"
;;
IRIX)
  if [ -n "$LD_LIBRARY_PATH" ]; then
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$WL_HOME/lib/irix
  else
    LD_LIBRARY_PATH=$WL_HOME/lib/irix
  fi
  PATH=$WL_HOME/lib/irix:$PATH
  export LD_LIBRARY_PATH PATH
  echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH"
;;
LINUX|Linux)
  arch=`uname -m`
  if [ -n "$LD_LIBRARY_PATH" ]; then
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$WL_HOME/lib/linux/$arch:$WL_HOME/lib/linux/$arch/oci816_8
  else
    LD_LIBRARY_PATH=$WL_HOME/lib/linux/$arch:$WL_HOME/lib/linux/$arch/oci816_8
  fi
  PATH=$WL_HOME/lib/linux:$PATH
  export LD_LIBRARY_PATH PATH
  echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH"
;;
OSF1)
  if [ -n "$LD_LIBRARY_PATH" ]; then
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$WL_HOME/lib/tru64unix
  else
    LD_LIBRARY_PATH=$WL_HOME/lib/tru64unix
  fi
  PATH=$WL_HOME/lib/tru64unix:$PATH
  export LD_LIBRARY_PATH PATH
  echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH"
;;
SunOS)
  if [ -n "$LD_LIBRARY_PATH" ]; then
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$WL_HOME/lib/solaris:$WL_HOME/lib/solaris/oci816_8
  else
    LD_LIBRARY_PATH=$WL_HOME/lib/solaris:$WL_HOME/lib/solaris/oci816_8
  fi
  PATH=$WL_HOME/lib/solaris:$PATH
  export LD_LIBRARY_PATH PATH
  echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH"
  JAVA_OPTIONS="-hotspot $JAVA_OPTIONS"
;;
*)
  echo "$0: Don't know how to set the shared library path for `uname -s`.  "
esac

CLASSPATH=$WL_HOME:$WL_HOME/lib/weblogic_sp.jar:$WL_HOME/lib/weblogic.jar

# get all of the ofbiz jars
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/core/lib/ofbcore-share.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/core/lib/ofbcore-entity.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/core/lib/ofbcore-service.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/core/lib/ofbcore-extutil.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/core/lib/ofbcore-workflow.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/core/lib/ofbcore-rules.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/core/lib/ofbcore-datafile.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/core/lib/ofbcore-minilang.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/commonapp/lib/commonapp.jar
# get all of the ofbiz config files
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/commonapp/etc

# get all of the third party jars
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/lib/common/castor-0.9.3.9.jar:$OFBIZ_HOME/lib/common/hsqldb.jar:$OFBIZ_HOME/lib/common/pgjdbc2.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/lib/common/mm.mysql-2.0.8-bin.jar:$OFBIZ_HOME/lib/common/log4j.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/lib/share/axis.jar:$OFBIZ_HOME/lib/share/clutil.jar:$OFBIZ_HOME/lib/share/wsdl4j.jar
CLASSPATH=$CLASSPATH:$OFBIZ_HOME/lib/share/bsh-1.2b3.jar:$OFBIZ_HOME/lib/share/jakarta-oro-2.0.4.jar

echo
echo CLASSPATH=$CLASSPATH

PATH=$WL_HOME/bin:$JAVA_HOME/jre/bin:$JAVA_HOME/bin:$PATH
echo
echo "***************************************************"
echo "*  To start WebLogic Server, use the password     *"
echo "*  assigned to the system user.  The system       *"
echo "*  username and password must also be used to     *"
echo "*  access the WebLogic Server console from a web  *"
echo "*  browser.                                       *"
echo "***************************************************"

# Set WLS_PW equal to your system password for no password prompt server startup.
WLS_PW=

# Set Production Mode.  When set to true, the server starts up in production 
# mode.  When set to false, the server starts up in development mode.  If 
# not set, it is defaulted to false
STARTMODE=true

java $JAVA_OPTIONS -classpath $CLASSPATH -Dweblogic.Domain=mydomain -Dweblogic.Name=myserver -Dbea.home=/ofbiz/bea -Dweblogic.management.password=$WLS_PW -Dweblogic.ProductionModeEnabled=$STARTMODE -Djava.security.policy==/ofbiz/bea/wlserver6.1/lib/weblogic.policy weblogic.Server

cd config/mydomain

fi

