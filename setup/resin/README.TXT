# $Id$
# Andy Zeneski (jaz@zsolv.com)

The Resin setup is different then the Tomcat setup.
The OFBiz JAR files are used in place for ease during development.
The OFBiz properties file are use directly from $OFBIZ_HOME/commonapp/etc
This is only an example setup.  There may be a better way to do this.

---------------------
2002-02-06 David Jones
A few small updates:

The ofbiz.sh wasn't working with the newest version of Resin because the Resin
default/root directory was messed up. The ofbiz.sh file is now if resin instead
of resin/bin where is was.

Also, the newer ofbiz.sh uses commonapp.jar rather than ofbcommonapp.jar since
it was recently changed.
---------------------
2002-02-09 David Jones

Resin does not include the activation.jar file from Sun, which is needed for 
Java Mail. This is only an issue with certain parts of OFBiz like the email
order confirmation in eCommerce.

To fix this, just add the activation.jar file to the resin/lib directory.
---------------------


