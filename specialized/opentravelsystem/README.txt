
Welcome to the OpenTravelsystem Component

Introduction.
------------
This component is intended for the travel and tourist industry and is further documented
at http://www.opentravelsystem.org . Currently it consists out of a hotel reservation site
consisting out of a 'frontend' application and to maintain product and customers the 
backend application.

how to activate?
----------------
1. copy this (opentravelsystem) directory into the 'hot-deploy' directory so a subdirectory 
opentravelsystem will be created under hot-deploy.
2. copy the directories under opentravelsystem/images into the 
component/images/webapp/images directory.
(did not yet find a way to display directly from the opentravelsystem/images directory
	please tell me if you know)
3. run the command 'ant run-install' from the commandline to create the test data and to
compile.

How to try.
----------
1. the frontend application can be started with:
http://localhost:8080/frontend
2. the backend application can be started with:
https://localhost:8434/backend

General comments.
--------------------
The intention is to create webapplications which only contain files which needed to be changed. Files which 
are not changed are directly used from the original/existing OFBiz application. This is only possible when 
you use the 'screen' widget. In the controller.xml there is a directive to *screens.xml file and this file is 
pointing to a form or for the older applications to a ftl/bsh file. pagedef/*.xml files should not be used 
anymore, this is done in the screen widget.

However we do not live in an ideal world, so currently the ecommerce application (not the component) is
completely copied and changed the opentravelsystem needs.
In the backend application we could do this, so you will only find the files which have been changed. 
It uses functions from the partymgr, product/catalog, order components.

The frontend component can be copied under another name and used on the same system for a different hotel.
You only have to set the webSiteId parameter in the webxml file to point to the websitename and create a new
application in the ofbiz-coponent.xml

The backend application can be used for different hotels without being copied. The productStoreID 
in the commonscreens.xml file defines on what data the application should work on. Although not 
implemented yet a userid can be connected to the product store id, so when he logs on the system 
know which product store to use.

The future.
------------
Although we cannot look in the future we can tell you what our plans are. These plans are highly 
dependent on the customers we can get. If a customer comes in with substantial funds, he will get priority.

The planned next steps:
	1. To create a travel agent demo reservation site.
	2. to implement a hotel property management system.
	3. to implement an event ticketing site.
	4. implement demo's for car/boat rental
	
If you are interested to contribute, you are highly welcome. If you find a customer who like to 
use the system, we can support you with the implementation and customisation for very good rates
because we are located in Thailand. see www.antwebsystem.com for more info.

Regards,
Hans Bakker.
