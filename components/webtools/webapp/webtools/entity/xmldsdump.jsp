<%--
 *  Copyright (c) 2001 The Open For Business Project and respected authors.
 
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
 * @author David E. Jones (jonesde@ofbiz.org)
 * @version 1.0
--%>

<%@ page import="java.util.*, java.io.*, java.net.*" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.model.*, org.ofbiz.entity.util.*, org.ofbiz.entity.transaction.*, org.ofbiz.entity.condition.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<%
  String outpath = request.getParameter("outpath");
  String filename = request.getParameter("filename");
  String[] entityName = request.getParameterValues("entityName");

  TreeSet passedEntityNames = new TreeSet();
  if (entityName != null && entityName.length > 0) {
    for(int inc=0; inc<entityName.length; inc++) {
      passedEntityNames.add(entityName[inc]);
    }
  }
  
  String preConfiguredSetName = request.getParameter("preConfiguredSetName");
  if ("Product1".equals(preConfiguredSetName)) {
    passedEntityNames.add("DataResource");
    passedEntityNames.add("Facility");
    passedEntityNames.add("ProdCatalog");
    passedEntityNames.add("Product");
    passedEntityNames.add("ProductCategory");
    passedEntityNames.add("ProductFeatureCategory");
    passedEntityNames.add("ProductFeatureType");
    passedEntityNames.add("ProductPriceRule");
    passedEntityNames.add("ProductPromo");
  } else if ("Product2".equals(preConfiguredSetName)) {
    passedEntityNames.add("Content");
    passedEntityNames.add("ElectronicText");
    passedEntityNames.add("FacilityLocation");
    passedEntityNames.add("ProdCatalogCategory");
    passedEntityNames.add("ProdCatalogRole");
    passedEntityNames.add("ProductAssoc");
    passedEntityNames.add("ProductAttribute");
    passedEntityNames.add("ProductCategoryMember");
    passedEntityNames.add("ProductCategoryRollup");
    passedEntityNames.add("ProductFacility");
    passedEntityNames.add("ProductFeature");
    passedEntityNames.add("ProductFeatureCategoryAppl");
    passedEntityNames.add("ProductKeyword");
    passedEntityNames.add("ProductPrice");
    passedEntityNames.add("ProductPriceAction");
    passedEntityNames.add("ProductPriceCond");
    passedEntityNames.add("ProductPromoCode");
    passedEntityNames.add("ProductPromoCategory");
    passedEntityNames.add("ProductPromoProduct");
    passedEntityNames.add("ProductPromoRule");
  } else if ("Product3".equals(preConfiguredSetName)) {
    passedEntityNames.add("ProdCatalogInvFacility");
    passedEntityNames.add("ProductContent");
    passedEntityNames.add("ProductFacilityLocation");
    passedEntityNames.add("ProductFeatureAppl");
    passedEntityNames.add("ProductFeatureContent");
    passedEntityNames.add("ProductFeatureGroup");
    passedEntityNames.add("ProductPriceChange");
    passedEntityNames.add("ProductPromoAction");
    passedEntityNames.add("ProductPromoCodeEmail");
    passedEntityNames.add("ProductPromoCodeParty");
    passedEntityNames.add("ProductPromoCond");
  } else if ("Product4".equals(preConfiguredSetName)) {
    passedEntityNames.add("InventoryItem");
    passedEntityNames.add("ProductFeatureCatGrpAppl");
    passedEntityNames.add("ProductFeatureGroupAppl");
  }
  
  boolean checkAll = "true".equals(request.getParameter("checkAll"));
  boolean tobrowser = request.getParameter("tobrowser")!=null?true:false;
%>
<%if (tobrowser) {%>
<%
    session.setAttribute("xmlrawdump_entitylist", entityName);
%>   
    <h3>XML Export from DataSource(s)</h3>
    <div>This page can be used to export data from the database. The exported documents will have a root tag of "&lt;entity-engine-xml&gt;".</div>
    <hr>
    <%if(security.hasPermission("ENTITY_MAINT", session)) {%>
        <a href='<ofbiz:url>/xmldsrawdump</ofbiz:url>' class='buttontext' target='_blank'>Click Here to Get Data (or save to file)</a>
    <%} else {%>
      <div>You do not have permission to use this page (ENTITY_MAINT needed)</div>
    <%}%>
<%} else {%>
<%
  EntityFindOptions efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
  ModelReader reader = delegator.getModelReader();
  Collection ec = reader.getEntityNames();
  TreeSet entityNames = new TreeSet(ec);

  int numberOfEntities = passedEntityNames.size();
  long numberWritten = 0;

  // single file
  if(filename != null && filename.length() > 0 && numberOfEntities > 0) {
    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")));
    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    writer.println("<entity-engine-xml>");

    Iterator i = passedEntityNames.iterator();
    while(i.hasNext()) { 
        boolean beganTransaction = TransactionUtil.begin(3600);
        try {
            String curEntityName = (String)i.next();
            EntityListIterator values = delegator.findListIteratorByCondition(curEntityName, null, null, null, null, efo);

            GenericValue value = null;
            long curNumberWritten = 0;
            while ((value = (GenericValue) values.next()) != null) {
                value.writeXmlText(writer, "");
                numberWritten++;
                curNumberWritten++;
                if (curNumberWritten % 500 == 0 || curNumberWritten == 1) {
                    Debug.log("Records written [" + curEntityName + "]: " + curNumberWritten + " Total: " + numberWritten);
                }
            }
            values.close();
            Debug.log("Wrote [" + curNumberWritten + "] from entity : " + curEntityName);
            TransactionUtil.commit(beganTransaction);
        } catch (Exception e) {
            Debug.logError(e, "Error reading data for XML export:", "JSP");
            TransactionUtil.rollback(beganTransaction);
        }
    }
    writer.println("</entity-engine-xml>");
    writer.close();
    Debug.log("Total records written from all entities: " + numberWritten);
  }

  // multiple files in a directory
  Collection results = new ArrayList();
  int fileNumber = 1;

  if (outpath != null){
      File outdir = new File(outpath);
      if(!outdir.exists()){
          outdir.mkdir();
      }
      if(outdir.isDirectory() && outdir.canWrite()) {
        Iterator i= passedEntityNames.iterator();

        while(i.hasNext()) {
            numberWritten = 0;
            String curEntityName = (String)i.next();
            EntityListIterator values = null;
            boolean beganTransaction = TransactionUtil.begin(3600);
            try{
                ModelEntity me = delegator.getModelEntity(curEntityName);
                if (me instanceof ModelViewEntity) {
                    results.add("["+fileNumber +"] [vvv] " + curEntityName + " skipping view entity");
                    continue;
                }
                values = delegator.findListIteratorByCondition(curEntityName, null, null, null, me.getPkFieldNames(), efo);

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
                        if (numberWritten % 500 == 0 || numberWritten == 1) {
                           Debug.log("Records written [" + curEntityName + "]: " + numberWritten);
                        }

                    }
                    writer.println("</entity-engine-xml>");
                    writer.close();
                    String thisResult = "["+fileNumber +"] [" + numberWritten + "] " + curEntityName + " wrote " + numberWritten + " records";
                    Debug.log(thisResult);
                    results.add(thisResult);
                } else {
                    String thisResult = "["+fileNumber +"] [---] " + curEntityName + " has no records, not writing file";
                    Debug.log(thisResult);
                    results.add(thisResult);
                }
                values.close();
                TransactionUtil.commit(beganTransaction);
            } catch (Exception ex) {
                if (values != null) {
                    values.close();
                }
                String thisResult = "["+fileNumber +"] [xxx] Error when writing " + curEntityName + ": " + ex;
                Debug.log(thisResult);
                results.add(thisResult);
                TransactionUtil.rollback(beganTransaction);
            }            
            fileNumber++;
        }
    }
  }

%>
    <h3>XML Export from DataSource(s)</h3>
    <div>This page can be used to export data from the database. The exported documents will have a root tag of "&lt;entity-engine-xml&gt;".</div>
    <hr>
    <%if(security.hasPermission("ENTITY_MAINT", session)) {%>
      <h3>Results:</h3>
    
    
      <%if(filename != null && filename.length() > 0 && numberOfEntities > 0) {%>
        <div>Wrote XML for all data in <%=numberOfEntities%> entities.</div>
        <div>Wrote <%=numberWritten%> records to XML file <%=filename%></div>
      <%} else if (outpath != null && numberOfEntities > 0) {%>
        <%Iterator re = results.iterator();%>
        <%while (re.hasNext()){%>
            <div><%=(String)re.next()%> </div>
        <%}%>
      <%} else {%>
        <div>No filename specified or no entity names specified, doing nothing.</div>
      <%}%>
    
      <hr>
    
      <h3>Export:</h3>
      <FORM method=POST action='<ofbiz:url>/xmldsdump</ofbiz:url>'>
        <div>Output Directory&nbsp;: <INPUT type=text class='inputBox' size='60' name='outpath' value='<%=UtilFormatOut.checkNull(outpath)%>'></div>
        <div>Single Filename&nbsp;&nbsp;: <INPUT type=text class='inputBox' size='60' name='filename' value='<%=UtilFormatOut.checkNull(filename)%>'></div>
        <div>OR Out to Browser: <INPUT type=checkbox name='tobrowser' <%=tobrowser?"checked":""%>></div>
        <br>
        <div>Entity Names:</div>
        <INPUT type=submit value='Export'>
        <A href='<ofbiz:url>/xmldsdump?checkAll=true</ofbiz:url>' class='buttontext'>Check All</A>
        <A href='<ofbiz:url>/xmldsdump</ofbiz:url>' class='buttontext'>Un-Check All</A>
        <br/>
        Pre-configured set: 
        <select name="preConfiguredSetName">
            <option value="">None</option>
            <option value="Product1">Product Part 1</option>
            <option value="Product2">Product Part 2</option>
            <option value="Product3">Product Part 3</option>
            <option value="Product4">Product Part 4</option>
        </select>
        <TABLE>
          <TR>
            <%Iterator iter = entityNames.iterator();%>
            <%int entCount = 0;%>
            <%while(iter.hasNext()) {%>
              <%String curEntityName = (String)iter.next();%>
              <%if(entCount % 3 == 0) {%></TR><TR><%}%>
              <%entCount++;%>
              <%-- don't check view entities... --%>
              <%boolean check = checkAll;%>
              <%if (check) {%>
                <%ModelEntity curModelEntity = delegator.getModelEntity(curEntityName);%>
                <%if (curModelEntity instanceof ModelViewEntity) check = false;%>
              <%}%>
              <TD><INPUT type=checkbox name='entityName' value='<%=curEntityName%>' <%=check?"checked":""%>><%=curEntityName%></TD>
            <%}%>
          </TR>
        </TABLE>
    
        <INPUT type=submit value='Export'>
        <A href='<ofbiz:url>/xmldsdump?checkAll=true</ofbiz:url>' class='buttontext'>Check All</A>
        <A href='<ofbiz:url>/xmldsdump</ofbiz:url>' class='buttontext'>Un-Check All</A>
      </FORM>
    <%} else {%>
      <div>You do not have permission to use this page (ENTITY_MAINT needed)</div>
    <%}%>
<%}%>
