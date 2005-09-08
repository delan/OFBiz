Last changed: September 7 2005
Welcome to the OpenTravelsystem Component!			
Introduction.
This component is intended for the travel and tourist industry and is further documented at http://www.opentravelsystem.org . Currently it consists out of a hotel reservation site with a frontend application for Ecommerce and a backend system for the maintenance of products and customers.

We also no have implemented a super simple site called HotelSimple. This site only provides an example screen how to introduce links in an existing website for reservation of specific rooms and/or a list of rooms.

How to activate?
1.Edit the  component-load.xml file (in specialized directory) and activate (un-comment) the line: 
<load-component component-location="${ofbiz.home}/specialized/opentravelsystem"/>
2.Edit the build.xml in the Ofbiz directory and add to the 
   <filelist id="application-builds" dir="applications" files=
the reference to  ../specialized/opentravelsystem/build.xml
in a similar way as the other applications.
3.Run the command 'ant run-install' from the command line to create the test data and to compile.

How to start.
The hotel demo site can be started with: https://localhost:8443/hotelbackend
Clicking on the WebSite tab, will start the Hotel E-commerce frontend application. The frontend application can be started directly with http://localhost:8080/hotelfrontend
General comments.
The intention is to create specialized web applications which only contain files which needed to be changed. Files which are not changed are directly used from the original/existing OFBiz application. This is only possible when you use the 'screen' widget. In the controller.xml there is a <view-map link to the *screens.xml file and this file is pointing to a form or for the older applications to a ftl/bsh file. WEB-INF/pagedef/*.xml files should not be used anymore, this functionality is taken over by the 'actions' in the screen or form widget.

In the backend application we only have the changed screens, it uses functions from the partymgr, product/catalog and order components. The frontend component contains only the required files from the ecommerce application and i guess that is less than 5%.

The frontend component can be copied under another name and used on the same system for a different hotel. You only have to set the webSiteId parameter in the webxml file to point to the websitename and create a new application/images the ofbiz-component.xml.

The backend application can be used for different hotels without being copied. The productStoreID in the commonscreens.xml file defines on what data the application should work on. If a party ID has set the 'admin' role on the productstore, all userid's of that party has access to the backend store and the productStore name is set at logon time.... If a user has access to more stores a selection list is shown.

The future.
Although we cannot look in the future we can tell you what our plans are. These plans are highly 
dependent on the customers we can get. If a customer comes in with substantial funds, he will get priority.
The planned next steps:
1.To create a travel agent demo reservation site.
2.to implement a hotel property management system.
3.to implement an event ticketing site.
4.implement demo's for car/boat rental
	
If you are interested to contribute, you are highly welcome. If you find a customer who like to use the system, we can support you with the implementation and customization for very good rates
because we are located in Thailand where the salaries are very competitive. See www.antwebsystems.com for more info.

Regards,
Hans Bakker.

