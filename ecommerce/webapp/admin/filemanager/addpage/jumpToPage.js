importPackage( Packages.java.lang );

var dest = request.getParameter( "destinationPath" );
System.out.println( "Sending : " + dest );

context.put("redirect", dest);
