#!/bin/sh
#
# drop the OFBIZ database
###########################################

PATH=/opt/sapdb/indep_prog/bin:$PATH
export PATH

set -x

# name of the database
SID=ofbiz

# Database Manager / Password
DBM=ofbdbm
DBMPW=ofbdbm

# start remote communication server
x_server start >/dev/null 2>&1

# stop and drop probably existing demo database
dbmcli -d $SID -u $DBM,$DBMPW db_offline >/dev/null 2>&1
dbmcli -s -d $SID -u $DBM,$DBMPW db_drop >/dev/null 2>&1

rmdir $HOME/$SID  >/dev/null 2>&1
exit 0
