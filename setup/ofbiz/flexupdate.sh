#!/bin/bash
# IMPORTANT:
# The following five lines should always be at line 4 to 8 of the file!!!
OFB_SETUPDIR="setup/ofbiz"
if test -f "$OFB_SETUPDIR/update-all"; then
	echo Copying current update script to .
	cp "$OFB_SETUPDIR/update-all" .
fi

# Here is the place for license / file description
# 
# @author Rene Gielen <gielen@aixcept.de>

LISTFILE="./.modlist"
# This would be appropriate for non anonymous logins
# USER=$(id -un)
USER="anonymous"
METHOD=":pserver:"
SERVER="cvs.ofbiz.sourceforge.net"
CVSDIR="/cvsroot"
UOPTS=""
RCFILE="./.updateallrc"

# If there is a rc file for default values in current dir, source it
test -f "$RCFILE" && . "$RCFILE"

while test "$1" != ""; do
	case $1 in
		"-u"|"--user")
			USER=$2
			shift 2
		;;
		"-s"|"--server")
			SERVER=$2
			shift 2
		;;
		"-m"|"--method")
			METHOD=$2
			shift 2
		;;
		"-c"|"--cvsdir")
			CVSDIR=$2
			shift 2
		;;
		"-f"|"--force")
			UOPTS="-d"
			shift 1
		;;
		"-h"|"--help")
		  cat << EOF
Update or checkout given repositories - should be executed from within the toplevel of your sandbox
Usage:
$0 [-h|--help] - Print this Helpscreen
$0 [-u|--user username] [-s|--server servername] [-c|--cvsdir cvsbasedir] [-m|--method accessmethod] [-f|--force]
   username defaults to your local id ($USER)
   servername defaults to $SERVER - use "local" to use local cvs
   cvsbasedir defaults to $CVSDIR
   method defaults to $METHOD
   -f|--force is used to force -d \$CVSROOT usage in cvs update command
EOF
			exit 0
		;;
	esac
done

if test -f "$LISTFILE"; then
	rm "$LISTFILE"
fi
echo Creating $LISTFILE ...
cat << EOF > $LISTFILE
ofbiz setup
ofbiz catalog
ofbiz commonapp
ofbiz core
ofbiz ecommerce
ofbiz lib
ofbiz ordermgr
ofbiz partymgr
ofbiz setup
ofbiz website
ofbiz webtools
ofbiz workeffort
EOF

CVSBASE="$METHOD$USER@$SERVER:$CVSDIR"
echo $CVSBASE
test $SERVER = "local" && CVSBASE=$CVSDIR

test "$UOPTS" != "" && UOPTS="$UOPTS $CVSBASE"

while read rep dir; do
	if test -d "$dir"; then
		cd $dir
		USESERVER=""
		test "$UOPTS" != "" && USESERVER="$UOPTS/$rep"
		echo Updating $dir, using CVSROOT $USESERVER
		cvs $USESERVER update -d -P
		cd ..
	else
		USESERVER="$CVSBASE/$rep"
		echo Checking out new module $dir, using CVSROOT $USESERVER
		cvs -d $USESERVER co $dir
	fi
done < $LISTFILE

if test -f "$OFB_SETUPDIR/build.xml"; then
	echo Copying current toplevel buildfile to .
	cp "$OFB_SETUPDIR/build.xml" .
fi

