if [ -z "$OFBIZ_HOME" ] ; then
  export OFBIZ_HOME="."
fi
cd $OFBIZ_HOME/core
echo "--- core ---"
cvs update -P -d
cd ../commonapp
echo "--- commonapp ---"
cvs update -P -d
cd ../webtools
echo "--- webtools ---"
cvs update -P -d
cd ../ecommerce
echo "--- ecommerce ---"
cvs update -P -d
cd ../catalog
echo "--- catalog ---"
cvs update -P -d
cd ../ordermgr
echo "--- ordermgr ---"
cvs update -P -d
cd ../partymgr
echo "--- partymgr ---"
cvs update -P -d
cd ../workeffort
echo "--- workeffort ---"
cvs update -P -d
cd ../accounting
echo "--- accounting ---"
cvs update -P -d
cd ../facility
echo "--- facility ---"
cvs update -P -d
cd ../marketing
echo "--- marketing ---"
cvs update -P -d
cd ../setup
echo "--- setup ---"
cvs update -P -d
cd ../website
echo "--- website ---"
cvs update -P -d
cd ..
