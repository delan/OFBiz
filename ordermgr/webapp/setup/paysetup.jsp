<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<% pageContext.setAttribute("PageName", "Main Page"); %>

<BR>

<div class='tabContainer'>
  <%if(security.hasEntityPermission("SHIPRATE", "_VIEW", session)) {%>
  <a href="<ofbiz:url>/shipsetup</ofbiz:url>" class='tabButton'>Ship&nbsp;Rate&nbsp;Setup</a>
  <%}%>
  <%if(security.hasEntityPermission("TAXRATE", "_VIEW", session)) {%>
  <a href="<ofbiz:url>/taxsetup</ofbiz:url>" class='tabButton'>Tax&nbsp;Rate&nbsp;Setup</a>
  <%}%>
  <%if(security.hasEntityPermission("PAYPROC", "_VIEW", session)) {%>
  <a href="<ofbiz:url>/paysetup</ofbiz:url>" class='tabButtonSelected'>Payment&nbsp;Setup</a>
  <%}%>
</div>

<%if(security.hasEntityPermission("PAYPROC", "_VIEW", session)) {%>

<% 
	List paymentSetup = delegator.findAll("WebSitePaymentSettingView", UtilMisc.toList("webSiteId", "paymentMethodTypeId"));
	if (paymentSetup != null) pageContext.setAttribute("paymentSetups", paymentSetup);
	String viewStr = new String();
%>

<TABLE border=0 width='100%' cellpadding='0' cellspacing=0 class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxtop'>
        <tr>
          <TD align=left width='90%' >
            <div class='boxhead'>&nbsp;Payment Processor Setup</div>
          </TD>
          <TD align=right width='10%'>&nbsp;</TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
        <tr>
          <td>
          <!-- Inside the box -->
          
            <table width="100%" cellpadding="2" cellspacing="2" border="0">
              <tr>
                <td nowrap><div class="tableheadtext"><b>WebSite</b></div></td>
                <td nowrap><div class="tableheadtext"><b>Payment Method Type</b></div></td>
                <td nowrap><div class="tableheadtext"><b>Auth Service Name</b></div></td>
                <td nowrap><div class="tableheadtext"><b>Capture Service Name</b></div></td>
                <td nowrap><div class="tableheadtext"><b>Payment Config</b></div></td>               
                <td nowrap><div class="tableheadtext">&nbsp;</div></td>
              </tr>
              <tr><td colspan="5"><hr class="sepbar"></td></tr>
              <ofbiz:unless name="paymentSetups">
                <tr>
                  <td colspan="5"><div class="tabletext">No settings found.</div></td>
                </tr>
              </ofbiz:unless>
              <ofbiz:iterator name="paymentSetting" property="paymentSetups">
              <tr class="<%= viewStr = viewStr == "viewManyTR1" ? "viewManyTR2" : "viewManyTR1" %>">
                <td><div class="tabletext"><ofbiz:entityfield attribute="paymentSetting" field="siteName"/></div></td>
                <td><div class="tabletext"><ofbiz:entityfield attribute="paymentSetting" field="description"/></div></td>
                <td><div class="tabletext"><ofbiz:entityfield attribute="paymentSetting" field="paymentAuthService"/></div></td>
                <td><div class="tabletext"><ofbiz:entityfield attribute="paymentSetting" field="paymentCaptureService"/></div></td>
                <td><div class="tabletext"><ofbiz:entityfield attribute="paymentSetting" field="paymentConfiguration"/></div></td>                
                <td nowrap>
                  <div class="tabletext">&nbsp;
                    <%if(security.hasEntityPermission("PAYPROC", "_UPDATE", session)) {%>
                    <a href="<ofbiz:url>/paysetup?webSiteId=<ofbiz:entityfield attribute="paymentSetting" field="webSiteId"/>&paymentMethodTypeId=<ofbiz:entityfield attribute="paymentSetting" field="paymentMethodTypeId"/></ofbiz:url>" class="buttontext">[Edit]</a>&nbsp;
                    <%}%>
                    <%if(security.hasEntityPermission("PAYPROC", "_DELETE", session)) {%>
                    <a href="<ofbiz:url>/removeWebSitePaymentSetting?webSiteId=<ofbiz:entityfield attribute="paymentSetting" field="webSiteId"/>&paymentMethodTypeId=<ofbiz:entityfield attribute="paymentSetting" field="paymentMethodTypeId"/></ofbiz:url>" class="buttontext">[Remove]</a>&nbsp;
                    <%}%>
                  </div>
                </td>
              </tr>
              </ofbiz:iterator> 
            </table>   
		  <!-- End of Inside -->
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE> 

<%if(security.hasEntityPermission("PAYPROC", "_CREATE", session)) {%>

<%
	String webSiteId = request.getParameter("webSiteId");
	String paymentMethodTypeId = request.getParameter("paymentMethodTypeId");
	Debug.logError("w: " + webSiteId + " p: " + paymentMethodTypeId);
	GenericValue webSitePayment = delegator.findByPrimaryKey("WebSitePaymentSettingView", UtilMisc.toMap("webSiteId", webSiteId, "paymentMethodTypeId", paymentMethodTypeId));
	if (webSitePayment != null) pageContext.setAttribute("webSitePayment", webSitePayment);
	
	List webSites = delegator.findAll("WebSite", UtilMisc.toList("siteName"));
	if (webSites != null) pageContext.setAttribute("webSites", webSites);
	List paymentMethodTypes = delegator.findAll("PaymentMethodType", UtilMisc.toList("description"));
	if (paymentMethodTypes != null) pageContext.setAttribute("paymentMethodTypes", paymentMethodTypes);
%>

<br>
<TABLE border=0 width='100%' cellpadding='0' cellspacing=0 class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxtop'>
        <tr>
          <TD align=left width='90%'>
            <ofbiz:if name="webSitePayment">
            <div class='boxhead'>&nbsp;Update&nbsp;Setting</div>
            </ofbiz:if>
            <ofbiz:unless name="webSitePayment">
            <div class='boxhead'>&nbsp;Add&nbsp;New&nbsp;Setting</div>
            </ofbiz:unless>
          </TD>
          <ofbiz:unless name="webSitePayment">
          <TD align='right' width='10%'>&nbsp;</TD>
          </ofbiz:unless>
          <ofbiz:if name="webSitePayment">
          <TD align='right' width='10%'><a href="<ofbiz:url>/paysetup</ofbiz:url>" class="lightbuttontext">[Add New]</a></TD>
          </ofbiz:if>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='0' cellspacing='0' class='boxbottom'>
        <tr>
          <td>
          <!-- Inside the box -->
            <ofbiz:if name="webSitePayment">
            <form method="POST" action="<ofbiz:url>/updateWebSitePaymentSetting</ofbiz:url>">
            </ofbiz:if>
            <ofbiz:unless name="webSitePayment">
            <form method="POST" action="<ofbiz:url>/createWebSitePaymentSetting</ofbiz:url>">
            </ofbiz:unless>
              <table border='0' cellpadding='2' cellspacing='0'>
                <tr>
                  <td width="26%" align=right><div class="tabletext">WebSite</div></td>
                  <td>&nbsp;</td>
                  <td width="74%">
                    <ofbiz:unless name="webSitePayment">
                    <select name="webSiteId">                                            
                      <ofbiz:iterator name="nextWebSite" property="webSites">
                        <option value='<ofbiz:inputvalue entityAttr="nextWebSite" field="webSiteId"/>'><ofbiz:inputvalue entityAttr="nextWebSite" field="siteName"/></option>
                      </ofbiz:iterator>
                    </select>
                    </ofbiz:unless>
                    <ofbiz:if name="webSitePayment">
                    <input type='hidden' name='webSiteId' value='<ofbiz:inputvalue entityAttr="webSitePayment" field="webSiteId"/>'>
                    <div class="tabletext">
                      <b><%=webSitePayment.getString("siteName")%></b> (This cannot be changed without re-creating the setting.)
                    </div>
                    </ofbiz:if>
                  </td>
                </tr>
                <tr>
                  <td width="26%" align=right><div class="tabletext">Payment Method Type</div></td>
                  <td>&nbsp;</td>
                  <td width="74%">
                    <ofbiz:unless name="webSitePayment">
                    <select name="paymentMethodTypeId">
                      <ofbiz:iterator name="nextPayType" property="paymentMethodTypes">
                        <option value='<ofbiz:inputvalue entityAttr="nextPayType" field="paymentMethodTypeId"/>'><ofbiz:inputvalue entityAttr="nextPayType" field="description"/></option>
                      </ofbiz:iterator>
                    </select>
                    </ofbiz:unless>
                    <ofbiz:if name="webSitePayment">
                    <input type='hidden' name='paymentMethodTypeId' value='<ofbiz:inputvalue entityAttr="webSitePayment" field="paymentMethodTypeId"/>'>
                    <div class="tabletext">
                      <b><%=webSitePayment.getString("description")%></b> (This cannot be changed without re-creating the setting.)
                    </div>
                    </ofbiz:if>                    
                  </td>
                </tr>
                <tr>
                  <td width="26%" align=right><div class="tabletext">Processor Auth Service</div></td>
                  <td>&nbsp;</td>
                  <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="webSitePayment" field="paymentAuthService" fullattrs="true"/> size="30" maxlength="60"></td>                
                </tr>    
                <tr>
                  <td width="26%" align=right><div class="tabletext">Processor Capture Service</div></td>
                  <td>&nbsp;</td>
                  <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="webSitePayment" field="paymentCaptureService" fullattrs="true"/> size="30" maxlength="60"></td>                
                </tr>                         
                <tr>
                  <td width="26%" align=right><div class="tabletext">Processor Properties URL</div></td>
                  <td>&nbsp;</td>
                  <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="webSitePayment" field="paymentConfiguration" fullattrs="true"/> size="30" maxlength="60"></td>                
                </tr>  
                <tr>
                  <td colspan='2'>&nbsp;</td>
                  <td colspan='1' align=left><input type="submit" value="Update"></td>
                </tr>            
              </table>               
            </form>
		  <!-- End of Inside -->
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE> 
<%}%>
         

<%}else{%>
  <br>
  <h3>You do not have permission to view this page. ("PAYSETUP_VIEW" or "PAYSETUP_ADMIN" needed)</h3>
<%}%>
