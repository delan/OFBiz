importPackage( Packages.java.util );
importPackage( Packages.java.lang );
importPackage( Packages.com.wspublisher.accounts );

System.out.println("Test");
var account = request.getParameter("accountname");
var password = request.getParameter("password");
if ( account != null ) 
{

	var userManager = site.getUserManager();
	var user = userManager.getUser( account );
	var userok = false;
	
	if ( user != null )
	{
	    // Save our logged-in user in the session,
	    // because we use it again later.
	    if ( password != null )
	    {
	        userok = userManager.authenticate( user, password );
	    }
	}
	
	if ( userok == false )
	{
	    System.out.println( "User " + account + " could not be logged in" );
	    request.setAttribute("wsp-exception","Invalid Logon");
	    
	}
	else
	{
	    session.setAttribute( "username", account );
	    System.out.println("Test");
	    url_util.redirect("/index.html?wsp-action=setEditMode&editMode=true" );
	    
	}
}