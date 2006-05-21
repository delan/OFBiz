
<%
/* 	
	all domains assigned to this ofbiz installation will arrive here when this webapp is activated.
	Here is the possibility to rerout them to the proper path keeping the requested domainname...
		or do some other stuff, this info should be forwarded by apache if you use that as a frontend.
*/	

// check if test parameter there, remove it and redirect



String serverName 	= 	request.getServerName();	// name of the host
String serverPath 	= 	request.getServletPath();	// path of the requested servlet
String scheme		=	getScheme(); 				// http or https or ftp
String addr			=	getRemoteAddr();			// the ip address of the requester
String topParam		=	getPrameter("top");			// top screen of the backend

// these paths need to be switched to https mode and if required to a different port
if (scheme.equals("http") && (serverPath.equals("/backend") || serverPath.equals("/webtools") || serverPath.equals("/partymgr"))) 
	response.sendRedirect("https://" + serverName + ":8443/" + serverpath + "/control/main" toParam!=null?"?top=" + topParam:" ");

if (serverName.equals("ant.co.th")) response.sendRedirect("/antwebsystems");
if (serverName.equals("openwinkel.co.th")) response.sendRedirect("/ow");
if (serverName.equals("anet.co.th")) response.sendRedirect("/anet");


%>
