Installing WorldPay Select Pro

- All worldpay configuration files are located in lib/worldpay
- Edit select.properties and change the basedir to point to the physical location of that dir
- Edit web.xml in ecommerce (or your store app) and enable the worldpay servlet
- Start the server: you may need to pass:
  '-Xbootclasspath:[PATH TO]/cryptix.jar:[PATH TO]/rt.jar:[PATH TO]/i18n.jar '
- Check the logs make sure the servlet loaded - an exception will show if not
- Have WorldPay 'initialize' your installation, this will generate keys and create an instId
- Configure payment.properties with the instId supplied by WorldPay

example startup:
java -Xbootclasspath:[PATH TO]/cryptix.jar:[PATH TO]/rt.jar:[PATH TO]/i18n.jar -jar ofbiz.jar
Jetty and Orion will require the bootclasspath setting, only tested with these two.

Submit questions to:
users@ofbiz.dev.java.net
