<%--
 *  Copyright (c) 2004 The Open For Business Project and respected authors.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 *
 * @author Brian Johnson (bmj@camfour.com)
 * @author Ray Barlow (ray.barlow@makeyour-point.com)
 * @author David E. Jones (jonesde@ofbiz.org)
--%>

<%@ page import="java.util.*, java.net.*, java.io.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.model.*, org.ofbiz.entity.util.*, org.ofbiz.entity.condition.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<%
  String path = request.getParameter("path");
%>

<h3>XML Import to DataSource(s)</h3>
<div>This page can be used to import exported Entity Engine XML documents. These documents all have a root tag of "&lt;entity-engine-xml&gt;".</div>
<hr>
<%if(security.hasPermission("ENTITY_MAINT", session)){%>
  <h3>Import:</h3>

  <FORM method=POST action='<ofbiz:url>/xmldsimportdir</ofbiz:url>'>
    <div>Absolute directory path:</div>
    <INPUT type=text class='inputBox' size='60' name='path' value='<%=UtilFormatOut.checkNull(path)%>'>
    <INPUT type=submit value='Import Files'>
  </FORM>
  <hr>
    <h3>Results:</h3>

  <%if (path != null && path.length() > 0) {%>
  <%
    File baseDir = new File(path);

    if (baseDir.isDirectory() && baseDir.canRead()){
	File[] fileArray = baseDir.listFiles();
        ArrayList files = new ArrayList(fileArray.length);
	for (int a=0; a<fileArray.length; a++){
            files.add(fileArray[a]);
        }
        boolean importedOne = false;
        int fileListMarkedSize = files.size();
        int passes = 0;
        for (int a=0; a<files.size(); a++){
        // Infinite loop defense
        if ( a == fileListMarkedSize ) {
          passes++;
          fileListMarkedSize = files.size();
          %> <div>Pass <%=passes%> complete</div> <%
          // This means we've done a pass
          if ( false == importedOne ) {
            // We've failed to make any imports
            %> <div>Dropping out as we failed to make any imports on the last pass</div> <%
            a = files.size();
            continue;
          }
          importedOne = false;
        }
            File curFile = (File)files.get(a);
	    try{
		URL url = curFile.toURL();
		EntitySaxReader reader = new EntitySaxReader(delegator);
		long numberRead = reader.parse(url);
		%><div>Got <%=numberRead%> entities from <%=curFile%></div><%
      importedOne = true;
                curFile.delete();
	    }catch (Exception ex){ 
                %> <div>Error trying to read from <%=curFile%>: <%=ex%> <%
                if (ex.toString().indexOf("referential integrity violation") > -1 || 
                  ex.toString().indexOf("Integrity constraint violation") > -1){
                    //It didn't work because object it depends on are still
                    //missing from the DB. Retry later.
                    //
                    //FIXME: Of course this is a potential infinite loop.

                    %> <div>Looks like referential integrity violation, will retry</div> <%
                    files.add(curFile);
                }
            }
	}
    }else{ %><div> path not found or can't be read </div> <% }
  %>
  <%} else {%>
    <div>No path specified, doing nothing.</div>
  <%}%>
<%}else{%>
  <div>You do not have permission to use this page (ENTITY_MAINT needed)</div>
<%}%>


