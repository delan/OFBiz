Installing WorldPay Select Pro

- Copy the contents of this directory into the web application WEB-INF directory
- The web.xml is an example on how to use with the e-commerce application
- Configure the select.properties and point the base directory to the WEB-INF directory
- Have WorldPay 'initialize' your installation, this will generate keys and create an instId
- Configure payment.properties with the instId supplied by WorldPay

- Some application servers may have problems with the cryptix.jar file not being in the front 
of the classpath. To fix this WorldPay has suggested adding the following to the startup:
'-Xbootclasspath:[PATH TO]/cryptix.jar:[PATH TO]/rt.jar:[PATH TO]/i18n.jar '


