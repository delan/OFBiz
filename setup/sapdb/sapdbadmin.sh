#!/bin/sh
###
# create a SAPDB database
#   written by Fabian Moerchen <fabian@mybytes.de>
#   modified by Andy Zeneski <jaz@jflow.net>
#   version 0.1.0
#   based on create_demo_db.sh included in the SAPDB distribution
###

# name of the database
DBNAME=OFBIZ

# name or ip of database server
DBSERVER=localhost

# usernames and password
USERNAME=ofbiz
PASSWORD=ofbiz

DBMUSER=dbm
DBMPWD=dbm

DBAUSER=dba
DBAPWD=dba

# size of database and logs in MB
DATASIZE=30
LOGSIZE=12

# path to sapdb
DEPEND=/opt/sapdb/depend
INDEPP=/opt/sapdb/indep_prog
INDEPD=/var/sapdb/indep_data

# path to database files
DATA=$HOME
LOG=$HOME
BACKUP=$HOME

# more advanced database parameters

# catalog cache memory size in 8KB pages for all user tasks
CATCACHESUPPLY=300
# size of the data cache in 8KB pages
DATACACHE=2500
# maximum number of data devspaces
MAXDATADEVSPACES=5
# maximum size of database in MB
MAXDBSIZE=8000
# maximum number of users allowed to connect
MAXUSERTASKS=10

# logfile for output of this script
LOGFILE=$0.log

###
# end of configuration
# no need to go below this line unless something is wrong
###

# database name always capitalized
DBNAME=`echo $DBNAME|tr a-z A-Z`

# command line arguments
CMDLINE=$@
if [ $# = 0 ]; then
    CMDLINE=--help
fi
_test=`echo $CMDLINE | grep recreate`
if [ "$_test" != "" ]; then
    CMDLINE=createdropdelete$CMDLINE
fi

# help
if [ "$CMDLINE" = "--help" ]; then
  echo
  echo "SAPDB administration script ($DBNAME on $DBSERVER)"
  echo
  echo "  --start              start the database"
  echo "  --stop               stop the database"
  echo "  --state              show database info state"
  echo
  echo "  --create             create database"
  echo "  --delete             delete database files (dangerous!)"
  echo "  --drop               drop database"
  echo "  --recreate           drop, delete, create database"
  echo
  echo "  --execute FILE       execute FILE with SQL commands"
  echo "  --password USER NEW  change password of USER to NEW"
  echo
  echo "  --load TABLE FILE    load TABLE data from FILE"
  echo "  --save TABLE FILE    save TABLE data to FILE"
  echo
  echo "  --dump PREFIX        save schema and data into files with PREFIX"
  echo "  --rebuild PREFIX     rebuild schema and data from files with PREFIX"
  echo
  echo "  --backup             make backup"
  echo "  --recover            recover last backup"
  echo
  echo "  --help               display this help"
  echo
  exit 0
fi

# calculate database pages from sizes
DATAPAGES=`cat <<EOF | bc
$DATASIZE*128
EOF`
LOGPAGES=`cat <<EOF | bc
$LOGSIZE*128
EOF`
MAXDATAPAGES=`cat <<EOF | bc
$MAXDBSIZE*128
EOF`

# script shouldn't be run as root
id=`id | sed s/\(.*// | sed s/uid=//`
if [ "$id" = "0" ]; then
  echo "Don't start script as root!" 1>&2
  exit 1
fi

# set variables
INSTROOT=$DEPEND
PATH=$INDEPP/bin:$DEPEND/bin:$PATH
export PATH

# print header
echo "SAPDB administration script ($DBNAME on $DBSERVER)" | tee -a $LOGFILE
echo -n `hostname` | tee -a $LOGFILE
echo -n " " | tee -a $LOGFILE
echo `date` | tee -a $LOGFILE
echo | tee -a $LOGFILE

# stop the database -- Andy Zeneski jaz@jflow.net
_test=`echo $CMDLINE | grep stop`
if [ "$_test" != "" ]; then
  echo "Stopping the database ($DBNAME)..."
  dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD db_stop
  exit 0
fi

_test=`echo $1 | grep execute`
if [ "$_test" != "" ]; then
  test -e $2
  if [ $? != 0 ]; then
    echo "File not found: $2" | tee -a $LOGFILE
    exit 1
  fi
  echo -n "Executing SQL file $2..." | tee -a $LOGFILE
  _o=`repmcli -n $DBSERVER -d $DBNAME -u $USERNAME,$PASSWORD -b $2 2>&1`
  _test=`echo $_o | grep error`
  if [ "$_test" != "" ]; then
    echo "failed." | tee -a $LOGFILE
    echo $_o >> $LOGFILE
    echo "See $LOGFILE for details."
    exit 1
  else
    echo "ok" | tee -a $LOGFILE
  fi
  echo
  echo "All done, see $LOGFILE for details."
  echo
  exit 0
fi

_test=`echo $1 | grep load`
if [ "$_test" != "" ]; then
  test -e $3
  if [ $? != 0 ]; then
    echo "File not found: $3" | tee -a $LOGFILE
    exit 1
  fi
  echo -n "Loading data from $3 into table $2..." | tee -a $LOGFILE
  TMPFILE=/tmp/$0.tmp
  echo "DATALOAD TABLE $2 INFILE '$3'" > $TMPFILE
  _o=`repmcli -n $DBSERVER -d $DBNAME -u $USERNAME,$PASSWORD -b $TMPFILE 2>&1`
  _test=`echo $_o | grep error`
  if [ "$_test" != "" ]; then
    echo "failed." | tee -a $LOGFILE
    echo $_o >> $LOGFILE
    echo "See $LOGFILE for details."
    exit 1
  else
    echo "ok" | tee -a $LOGFILE
  fi
  echo
  echo "All done, see $LOGFILE for details."
  echo
  exit 0
fi

_test=`echo $1 | grep save`
if [ "$_test" != "" ]; then
  test -e $3
  if [ $? != 0 ]; then
    echo "File not found: $3" | tee -a $LOGFILE
    exit 1
  fi
  echo -n "Saving from table $2 into $3..." | tee -a $LOGFILE
  TMPFILE=/tmp/$0.tmp
  echo "DATAEXTRACT * FROM $2 OUTFILE '$3'" > $TMPFILE
  _o=`repmcli -n $DBSERVER -d $DBNAME -u $USERNAME,$PASSWORD -b $TMPFILE 2>&1`
  _test=`echo $_o | grep error`
  if [ "$_test" != "" ]; then
    echo "failed." | tee -a $LOGFILE
    echo $_o >> $LOGFILE
    echo "See $LOGFILE for details."
    exit 1
  else
    echo "ok" | tee -a $LOGFILE
  fi
  echo
  echo "All done, see $LOGFILE for details."
  echo
  exit 0
fi

_test=`echo $1 | grep dump`
if [ "$_test" != "" ]; then
  echo -n "Dumping schema and data in files $2[NNNN]..." | tee -a $LOGFILE
  TMPFILE=/tmp/$0.tmp
  echo "CATALOGEXTRACT USER OUTFILE '$2-cat'" > $TMPFILE
  echo >> $TMPFILE
  echo "TABLEEXTRACT USER OUTFILE '$2-tab'" >> $TMPFILE
  _o=`repmcli -n $DBSERVER -d $DBNAME -u $USERNAME,$PASSWORD -b $TMPFILE 2>&1`
  _test=`echo $_o | grep error`
  if [ "$_test" != "" ]; then
    echo "failed." | tee -a $LOGFILE
    echo $_o >> $LOGFILE
    echo "See $LOGFILE for details."
    exit 1
  else
    echo "ok" | tee -a $LOGFILE
  fi
  echo
  echo "All done, see $LOGFILE for details."
  echo
  exit 0
fi

_test=`echo $1 | grep rebuild`
if [ "$_test" != "" ]; then
  echo -n "Loading schema and data from files $2..." | tee -a $LOGFILE
  TMPFILE=/tmp/$0.tmp
  echo "TABLELOAD USER INFILE '$2-tab' OUTFILE '$2.restart'" > $TMPFILE
  echo >> $TMPFILE
  echo "CATALOGLOAD USER INFILE '$2-cat'" >> $TMPFILE
  _o=`repmcli -n $DBSERVER -d $DBNAME -u $USERNAME,$PASSWORD -b $TMPFILE 2>&1`
  _test=`echo $_o | grep error`
  if [ "$_test" != "" ]; then
    echo "failed." | tee -a $LOGFILE
    echo $_o >> $LOGFILE
    echo "See $LOGFILE for details."
    exit 1
  else
    echo "ok" | tee -a $LOGFILE
  fi
  echo
  echo "All done, see $LOGFILE for details."
  echo
  exit 0
fi

_test=`echo $1 | grep password`
if [ "$_test" != "" ]; then
  if [ "$2" = "$DBMUSER" ]; then
    echo -n "Changing password for dbm user $2..." | tee -a $LOGFILE
    cat <<EOF | dbmcli -n $DBSERVER -d $DBNAME -u $DBAUSER,$DBAPWD >> $LOGFILE 2>&1
db_stop
user_put $DBMUSER PASSWORD=$3
db_warm
quit
EOF
  else
    if [ $2 = $DBAUSER ]; then
      echo -n "Changing password for dba user $2..." | tee -a $LOGFILE
      cat <<EOF | dbmcli -n $DBSERVER -d $DBNAME -u $DBAUSER,$DBAPWD >> $LOGFILE 2>&1
user_put $DBAUSER PASSWORD=$3
quit
EOF
    else
      echo -n "Changing password for SQL user $2..." | tee -a $LOGFILE
      cat <<EOF | dbmcli -n $DBSERVER -d $DBNAME -u $DBAUSER,$DBAPWD >> $LOGFILE 2>&1
sql_connect $DBAUSER,$DBAPWD
sql_execute ALTER PASSWORD $2 $3
EOF
    fi
  fi
  _test=`tail -n 10 $LOGFILE | grep ERR`
  if [ "$_test" != "" ]; then
    echo "failed!" | tee -a $LOGFILE
    echo "See $LOGFILE for details."
    exit 1
  else
    echo "ok" | tee -a $LOGFILE
    echo
    echo "Please remember to change password also in this script ($0)" | tee -a $LOGFILE
  fi
  echo
  echo "All done, see $LOGFILE for details."
  echo
  exit 0
fi

_test=`echo $1 | grep backup`
if [ "$_test" != "" ]; then
  echo -n "Performing migration backup of $DBNAME..." | tee -a $LOGFILE
  cat <<EOF | dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD >> $LOGFILE 2>&1
util_connect dbm,$DBMPWD
autolog_off
backup_start data MIGRATION DATA
autolog_on
EOF
  _test=`tail -n 10 $LOGFILE | grep ERR`
  if [ "$_test" != "" ]; then
    echo "failed!" | tee -a $LOGFILE
    echo "See $LOGFILE for details."
    exit 1
  else
    echo "ok" | tee -a $LOGFILE
    SRC=$BACKUP/$DBNAME/datasave
    DST=$SRC-`date +%Y%m%d-%H%M%S`
    echo "Rotating backup file:" | tee -a $LOGFILE
    echo "$SRC to" | tee -a $LOGFILE
    echo $DST | tee -a $LOGFILE
    cp $SRC $DST
    if [ $? != 0 ]; then
      exit 1
    else
      echo "ok" | tee -a $LOGFILE
    fi
  fi
  echo
  echo "All done, see $LOGFILE for details."
  echo
  exit 0
fi

_test=`echo $1 | grep recover`
if [ "$_test" != "" ]; then
  echo -n "Performing recovery of $DBNAME..." | tee -a $LOGFILE
  cat <<EOF | dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD >> $LOGFILE 2>&1
db_cold
util_connect dbm,$DBMPWD
util_execute init config
recover_start data
util_release
db_warm
EOF
  _test=`tail -n 10 $LOGFILE | grep ERR`
  if [ "$_test" != "" ]; then
    echo "failed!" | tee -a $LOGFILE
    echo "See $LOGFILE for details."
    exit 1
  else
    echo "ok" | tee -a $LOGFILE
  fi
  echo
  echo "All done, see $LOGFILE for details."
  echo
  exit 0
fi

# start remote communication server
echo -n "Starting SAPDB database server..." | tee -a $LOGFILE
_o=`x_server start 2>&1`
_test=`echo $_o | grep 10004`
if [ "$_test" = "" ]; then
  # ignore if maybe already running
  _test=`echo $_o | grep 10107`
  if [ "$_test" = "" ]; then
    echo "failed." | tee -a $LOGFILE
    echo $_o >> $LOGFILE
    exit 1
  else
    echo "ok" | tee -a $LOGFILE
  fi
fi

# show the state of the database -- Andy Zeneski jaz@jflow.net
_test=`echo $CMDLINE | grep state`
if [ "$_test" != "" ]; then
  echo "Database info state ($DBNAME)..."
  dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD -uSQL -c info state
  exit 0
fi

# start (warm start) the database -- Andy Zeneski jaz@jflow.net
_test=`echo $CMDLINE | grep start`
if [ "$_test" != "" ]; then
  echo -n "Warm starting the database ($DBNAME)..."
  dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD db_warm
  exit 0
fi

# start (cold start) the database -- Andy Zeneski jaz@jflow.net
_test=`echo $CMDLINE | grep cold`
if [ "$_test" != "" ]; then
  echo -n "Cold starting the database ($DBNAME)..."
  dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD db_cold
  exit 0
fi

# try to stop existing database
echo -n "Stopping database $DBNAME..." | tee -a $LOGFILE
_o=`dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD db_offline 2>&1`
_test=`echo $_o | grep OK`
if [ "$_test" = "" ]; then
  echo "failed." | tee -a $LOGFILE
  echo $_o >> $LOGFILE
  echo "Maybe it didn't exists, so continuing." | tee -a $LOGFILE
else
  echo "ok" | tee -a $LOGFILE
fi

_test=`echo $CMDLINE | grep drop`
if [ "$_test" != "" ]; then
  # try to drop existing database
  echo -n "Dropping database $DBNAME..." | tee -a $LOGFILE
  _o=`dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD db_drop 2>&1`
  _test=`echo $_o | grep OK`
  if [ "$_test" = "" ]; then
    echo "failed." | tee -a $LOGFILE
    echo $_o >> $LOGFILE
    echo "Maybe it didn't exists, so continuing." | tee -a $LOGFILE
  else
    echo "ok" | tee -a $LOGFILE
  fi
fi

_test=`echo $CMDLINE | grep delete`
if [ "$_test" != "" ]; then
  echo "Deleting database directories:" | tee -a $LOGFILE
  echo -n $DATA/$DBNAME... | tee -a $LOGFILE
  rm -rf $DATA/$DBNAME
  echo "ok" | tee -a $LOGFILE
  echo -n $LOG/$DBNAME... | tee -a $LOGFILE
  rm -rf $LOG/$DBNAME
  echo "ok" | tee -a $LOGFILE
  echo -n $BACKUP/$DBNAME... | tee -a $LOGFILE
  rm -rf $BACKUP/$DBNAME
  echo "ok" | tee -a $LOGFILE
fi

# the rest is about creating
# to avoid a huge if just quit if is not in options
_test=`echo $CMDLINE | grep create`
if [ "$_test" = "" ]; then
  echo
  echo "All done, see $LOGFILE for details."
  echo
  exit 0
fi

# create new demo database
echo -n "Creating database $DBNAME..." | tee -a $LOGFILE
_o=`dbmcli -s -R  $INSTROOT db_create $DBNAME $DBMUSER,$DBMPWD 2>&1`
_test=`echo $_o | grep OK`
if [ "$_test" = "" ]; then
        echo "failed!" | tee -a $LOGFILE
        echo $_o | tee -a $LOGFILE
        exit 1
fi
echo "ok" | tee -a $LOGFILE

# create directories for database files
echo -n "Creating directories..." | tee -a $LOGFILE
mkdir -p $DATA/$DBNAME
mkdir -p $LOG/$DBNAME
mkdir -p $BACKUP/$DBNAME
echo "ok" | tee -a $LOGFILE

# setup database parameters
echo -n "Setting parameters for $DBNAME..." | tee -a $LOGFILE
cat <<EOF | dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD >> $LOGFILE 2>&1
param_rmfile
param_startsession
param_init OLTP
param_put LOG_MODE SINGLE
param_put CAT_CACHE_SUPPLY $CATCACHESUPPLY
param_put DATA_CACHE $DATACACHE
param_put MAXDATADEVSPACES $MAXDATADEVSPACES
param_put MAXDATAPAGES $MAXDATAPAGES
param_put MAXUSERTASKS $MAXUSERTASKS
param_checkall
param_commitsession
param_adddevspace 1 SYS  $DATA/$DBNAME/DISKS01   F
param_adddevspace 1 DATA $DATA/$DBNAME/DISKD0001 F $DATAPAGES
param_adddevspace 1 LOG  $LOG/$DBNAME/DISKL001   F $LOGPAGES
quit
EOF
_test=`tail -n 40 $LOGFILE | grep ERR`
if [ "$_test" != "" ]; then
        echo "failed!" | tee -a $LOGFILE
	echo "See $LOGFILE for details."
        exit 1
fi
echo "ok" | tee -a $LOGFILE

# startup database
echo -n "Starting database $DBNAME..." | tee -a $LOGFILE
_o=`dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD db_start 2>&1`
_test=`echo $_o | grep OK`
if [ "$_test" = "" ]; then
        echo "failed!" | tee -a $LOGFILE
        echo $_o | tee -a $LOGFILE
        exit 1
fi
echo "ok" | tee -a $LOGFILE

# initialize database files
echo -n "Initializing database files..." | tee -a $LOGFILE
cat <<EOF | dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD >> $LOGFILE 2>&1
util_connect $DBMUSER,$DBMPWD
util_execute init config
util_activate $DBAUSER,$DBAPWD
quit
EOF
_test=`tail -n 20 $LOGFILE | grep ERR`
if [ "$_test" != "" ]; then
        echo "failed!" | tee -a $LOGFILE
	echo "See $LOGFILE for details."
        exit 1
fi
echo "ok" | tee -a $LOGFILE

# load database system tables
echo -n "Loading system tables..." | tee -a $LOGFILE
_o=`dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD load_systab -u $DBAUSER,$DBAPWD -ud domain 2>&1`
_test=`echo $_o | grep OK`
if [ "$_test" = "" ]; then
        echo "failed!" | tee -a $LOGFILE
        echo $_o | tee -a $LOGFILE
        exit 1
fi
echo "ok" | tee -a $LOGFILE

# create database demo user
echo -n "Creating database user $USERNAME..." | tee -a $LOGFILE
cat <<EOF | dbmcli -n $DBSERVER -d $DBNAME -u $DBAUSER,$DBAPWD >> $LOGFILE 2>&1
sql_connect $DBAUSER,$DBAPWD
sql_execute CREATE USER $USERNAME PASSWORD $PASSWORD DBA NOT EXCLUSIVE
EOF
_test=`tail -n 7 $LOGFILE | grep ERR`
if [ "$_test" != "" ]; then
        echo "failed!" | tee -a $LOGFILE
	echo "See $LOGFILE for details."
        exit 1
fi
echo "ok" | tee -a $LOGFILE

# define backup medium
echo -n "Setting backup parameters..." | tee -a $LOGFILE
cat <<EOF | dbmcli -n $DBSERVER -d $DBNAME -u $DBMUSER,$DBMPWD >> $LOGFILE 2>&1
medium_put data $BACKUP/$DBNAME/datasave FILE DATA 0 8 YES
medium_put auto $BACKUP/$DBNAME/autosave FILE AUTO
util_connect $DBMUSER,$DBMPWD
backup_save data
autosave_on
quit
EOF
_test=`tail -n 40 $LOGFILE | grep ERR`
if [ "$_test" != "" ]; then
        echo "failed!" | tee -a $LOGFILE
	echo "See $LOGFILE for details."
        exit 1
fi
echo "ok" | tee -a $LOGFILE

echo
echo "All done, see $LOGFILE for details."
echo

exit 0

# (rebuild)
# xload deprecated?
# repmcli picky on returns?
# sed: add new lines when executing SQL
# feature: repmcli with cat instead of -b?
