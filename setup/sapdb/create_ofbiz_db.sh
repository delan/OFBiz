#!/bin/sh
###
# create OFBiz database with
#   - 20 MB data devspace and 8 MB log devspace
#   - demo database user test (with password test)
###



id=`id | sed s/\(.*// | sed s/uid=//`

if [ "$id" = "0" ]; then
        echo "dont start script as root " 1>&2
        exit 1
fi


if [ "$HOME" = "" ]; then
	USER=`id | sed s/^.*\(// | sed s/\).*//`;export USER
	HOME=`echo ~$USER`;export HOME
	if [ "$HOME" = "" ]; then
		echo "cannot find ${USER}s home  directory - set HOME in environment and retry" 1>&2
		exit 1
	fi
fi


INSTROOT=/opt/sapdb/depend
PATH=/opt/sapdb/indep_prog/bin:$PATH
export PATH

# name of the database
SID=ofbiz

# Database Manager / Password
DBM=ofbdbm
DBMPW=ofbdbm

# Database Administrator / Password
DBA=ofbdba
DBAPW=ofbdba

# Database User / Password
DBU=ofbiz
DBUPW=ofbiz

# start remote communication server
x_server start >/dev/null 2>&1

# stop and drop probably existing demo database
dbmcli -d $SID -u $DBM,$DBMPW db_offline >/dev/null 2>&1
dbmcli -d $SID -u $DBM,$DBMPW db_drop >/dev/null 2>&1


# create new demo database
echo "create database $SID..."
_o=`dbmcli -s -R  $INSTROOT db_create $SID $DBM,$DBMPW 2>&1`
_test=`echo $_o | grep OK`
if [ "$_test" = "" ]; then
        echo "create $SID failed: $_o" 1>&2
        exit 1
fi


# create directory where to put the database files
mkdir -p $HOME/$SID

# setup database parameters
echo "set parameters for $SID..."
_o=`cat <<EOF | dbmcli -d $SID -u $DBM,$DBMPW 2>&1
param_rmfile
param_startsession
param_init OLTP
param_put LOG_MODE SINGLE
param_put CAT_CACHE_SUPPLY 300
param_put DATA_CACHE 2500
param_put MAXDATADEVSPACES 5
param_put MAXDATAPAGES 1024000
param_checkall
param_commitsession
param_adddevspace 1 SYS  $HOME/$SID/DISKS01   F
param_adddevspace 1 DATA $HOME/$SID/DISKD0001 F 2560
param_adddevspace 1 LOG  $HOME/$SID/DISKL001  F 1024
quit
EOF`
_test=`echo $_o | grep ERR`
if [ "$_test" != "" ]; then
        echo "set parameters failed: $_o" 1>&2
        exit 1
fi

 

# startup database
echo "start $SID..."
_o=`dbmcli -d $SID -u $DBM,$DBMPW db_start 2>&1`
_test=`echo $_o | grep OK`
if [ "$_test" = "" ]; then
        echo "start $SID failed: $_o" 1>&2
        exit 1
fi

# initialize database files
echo "initialize $SID..."
_o=`cat <<EOF | dbmcli -d $SID -u $DBM,$DBMPW 2>&1
util_connect $DBM,$DBMPW
util_execute init config
util_activate $DBA,$DBAPW
quit
EOF`
_test=`echo $_o | grep ERR`
if [ "$_test" != "" ]; then
        echo "initializing $SID failed: $_o" 1>&2
        exit 1
fi

# load database system tables
echo "load system tables..."
_o=`dbmcli -d $SID -u $DBM,$DBMPW load_systab -u $DBA,$DBAPW -ud domain 2>&1`
_test=`echo $_o | grep OK`
if [ "$_test" = "" ]; then
        echo "load system tables failed: $_o" 1>&2
        exit 1
fi

# create database demo user
echo "create database demo user..."
_o=`cat <<EOF | dbmcli -d $SID -u $DBA,$DBAPW
sql_connect $DBA,$DBAPW
sql_execute CREATE USER $DBU PASSWORD $DBUPW DBA NOT EXCLUSIVE
EOF`
_test=`echo $_o | grep ERR`
if [ "$_test" != "" ]; then
        echo "create db user failed: $_o" 1>&2
        exit 1
fi

echo "set backup parameters..."
_o=`cat <<EOF | dbmcli -d $SID -u $DBM,$DBMPW 2>&1
medium_put data $HOME/$SID/datasave FILE DATA 0 8 YES
medium_put auto $HOME/$SID/autosave FILE AUTO
util_connect $DBM,$DBMPW
backup_save data
autosave_on
quit
EOF`
_test=`echo $_o | grep ERR`
if [ "$_test" != "" ]; then
        echo "set backup parameters failed: $_o" 1>&2
        exit 1
fi


exit 0
