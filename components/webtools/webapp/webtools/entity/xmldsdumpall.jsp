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
 * @author David E. Jones (jonesde@ofbiz.org)
--%>

<%@ page import="java.util.*, java.io.*, java.net.*" %>
<%@ page import="org.w3c.dom.*" %>
<%-- For pre-3.0 series OFBiz
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.model.*" %>
--%>

<%-- For 3.0 and later series OFBiz --%>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.model.*, org.ofbiz.entity.util.*" %>


<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%-- For pre-3.0 series OFBiz
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
--%>

<%-- For 3.0 and later series OFBiz --%>
<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />

<h3>XML Export from DataSource(s)</h3>
<div>
	This page can be used to export data from the database. 
	The exported documents will have a root tag of "&lt;entity-engine-xml&gt;".
	There will be one file for each Entity in the configured delegator for this webapp.
</div>
<hr>
    
<%if (security.hasPermission("ENTITY_MAINT", session)) {%>
<h3>Results:</h3>
<%
  String outpath = request.getParameter("outpath");

  ModelReader reader = delegator.getModelReader();
  Collection ec = reader.getEntityNames();
  TreeSet entityNames = new TreeSet(ec);
  Collection results = new ArrayList();

  int fileNumber = 1;
  long numberWritten = 0;

  if (outpath != null){
      File outdir = new File(outpath);
      if(!outdir.exists()){
          outdir.mkdir();
      }
      if(outdir.isDirectory() && outdir.canWrite()) {
        Iterator passedEntityNames= entityNames.iterator();
        
        while(passedEntityNames.hasNext()) { 
            numberWritten = 0;
            String curEntityName = (String)passedEntityNames.next();
            EntityListIterator values = null;
            try{
                ModelEntity me = delegator.getModelEntity(curEntityName);
                if (me instanceof ModelViewEntity) {
                    results.add("["+fileNumber +"] [vvv] " + curEntityName + " skipping view entity");
                    continue;
                }
                values = delegator.findListIteratorByCondition(curEntityName, null, null, null, me.getPkFieldNames(), null);
                
                //Don't bother writing the file if there's nothing
                //to put into it
                if (values.hasNext()) {
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outdir, curEntityName +".xml")), "UTF-8")));
                    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    writer.println("<entity-engine-xml>");

                    GenericValue value = null;
                    while ((value = (GenericValue) values.next()) != null) {
                        value.writeXmlText(writer, "");
                        numberWritten++;
                    }
                    writer.println("</entity-engine-xml>");
                    writer.close();
                    results.add("["+fileNumber +"] [" + numberWritten + "] " + curEntityName + " wrote " + numberWritten + " records");
                } else {
                    results.add("["+fileNumber +"] [---] " + curEntityName + " has no records, not writing file");
                }
                values.close();
            } catch (Exception ex) {
                if (values != null) {
                    values.close();
                }
                results.add("["+fileNumber +"] [xxx] Error when writing " + curEntityName + ": " + ex);
            }
            fileNumber++;
        }
    }
  }
%>    
    <%Iterator re = results.iterator();%>
    <%while (re.hasNext()){%>
        <div><%=(String)re.next()%> </div>
    <%}%>
      <hr>
    
      <h3>Export:</h3>
      <FORM method=POST action='<ofbiz:url>/xmldsdumpall</ofbiz:url>'>
        <div>Output Directory: <INPUT type=text class='inputBox' size='60' name='outpath' value='<%=UtilFormatOut.checkNull(outpath)%>'></div>
        <br>
        <INPUT type=submit value='Export'>
      </FORM>
    <%} else {%>
      <div>You do not have permission to use this page (ENTITY_MAINT needed)</div>
    <%}%>


