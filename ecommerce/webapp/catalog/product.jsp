
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*, org.ofbiz.core.util.*, org.ofbiz.core.entity.*"%>
<%@ page import="org.ofbiz.core.pseudotag.*, org.ofbiz.commonapp.product.product.*"%>

<%-- ====================================================== --%>
<%-- Special Variant Code                                   --%>
<%-- ====================================================== --%>
<ofbiz:service name="getProductFeatureSet">
    <ofbiz:param name='productId' value='<%=request.getParameter("product_id")%>'/>
</ofbiz:service>
<ofbiz:service name="getProductVariantTree">
    <ofbiz:param name='productId' value='<%=request.getParameter("product_id")%>'/>
    <ofbiz:param name='featureOrder' attribute='featureSet'/>
</ofbiz:service>

<%
  List featureOrder = new LinkedList((Collection) pageContext.getAttribute("featureSet"));
  Map variantTree = (Map) pageContext.getAttribute("variantTree");
  Map imageMap = (Map) pageContext.getAttribute("variantSample");
  Debug.logInfo("Setup variables: " + featureOrder + " / " + variantTree + " / " + imageMap);
%>


<%!
    public static String buildNext(Map map, List order, String current, String prefix) {
        Set keySet = map.keySet();
        int ct = 0;
        Iterator i = keySet.iterator();
        StringBuffer buf = new StringBuffer();
        buf.append("function list" + current + prefix + "() { ");
        buf.append("document.forms[\"addform\"].elements[\"" + current + "\"].options.length = 1;");
        buf.append("document.forms[\"addform\"].elements[\"" + current + "\"].options[0] = new Option(\"" + current + "\",\"\",true,true);");
        while (i.hasNext()) {
            Object key = i.next();
            Object value = map.get(key);
            Debug.logInfo("" + key + " value: " + value);
            String optValue = null;
            if (order.indexOf(current) == (order.size()-1)) {
                optValue = ((String) ((List)value).iterator().next());
            } else {
                optValue = prefix + "" + ct;
            }
            buf.append("document.forms[\"addform\"].elements[\"" + current + "\"].options[" + (ct + 1) + "] = new Option(\"" + key + "\",\"" + optValue + "\");");
            ct++;
        }
        buf.append(" }");
        if (order.indexOf(current) < (order.size()-1)) {
            Iterator i2 = keySet.iterator();
            ct = 0;
            while (i2.hasNext()) {
                String nextOrder = (String) order.get(order.indexOf(current)+1);
                Object key = i2.next();
                Map value = (Map) map.get(key);
                String newPrefix = prefix + "_" + ct;
                buf.append(buildNext(value, order, nextOrder, newPrefix));
            }
        }
        return buf.toString();
    }
%>

<script language="JavaScript">
<!--
var IMG = new Array(<%=variantTree.size()%>);
var OPT = new Array(<%=featureOrder.size()%>);
<% for (int li = 0; li < featureOrder.size(); li++) { %>
  OPT[<%=li%>] = "<%=featureOrder.get(li)%>";
<% } %>

<%-- Build the top level --%>
<% String topLevelName = (String) featureOrder.get(0);%>
function list<%=topLevelName%>() {
     document.forms["addform"].elements["<%=topLevelName%>"].options.length = 1;
     document.forms["addform"].elements["<%=topLevelName%>"].options[0] = new Option("<%=topLevelName%>","",true,true);
  <%
    if (variantTree != null) {
        Set vTreeKeySet = variantTree.keySet();
        Iterator vti = vTreeKeySet.iterator();
        int counter = 0;
        while (vti.hasNext()) {
            Object key = vti.next();
            Object value = variantTree.get(key);
            String opt = null;
            if (featureOrder.size() == 1)
                opt = ((String) ((List)value).iterator().next());
            else
                opt = "" + counter;

  %>
     document.forms["addform"].elements["<%=topLevelName%>"].options[<%=counter+1%>] = new Option("<%=key%>","<%=opt%>");
     IMG[<%=counter%>] = "<%=((GenericValue) imageMap.get(key)).getString("largeImageUrl")%>";
  <%
            counter++;
        }
    }
  %>
}

<%-- Start of Dyno-Gen --%>
<%
  if (variantTree != null) {
    Set topLevelKeys = variantTree.keySet();
    Iterator tli = topLevelKeys.iterator();
    int topLevelKeysCt=0;
    while (tli.hasNext()) {
        String cnt = "" + topLevelKeysCt;
%>
<%=buildNext((Map)variantTree.get(tli.next()), featureOrder, (String)featureOrder.get(1), cnt)%>
<%
        topLevelKeysCt++;
    }
  }
%>
<%-- End of Dynamic Gen --%>

function findIndex(name) {
  for (i=0; i<OPT.length; i++) {
    if (OPT[i] == name) {
      return i;
    }
  }
  return -1;
}

function getList(name, value) {
  currentOrderIndex = findIndex(name);
  if (currentOrderIndex < 0 || value == "")
    return;
  if (currentOrderIndex < (OPT.length - 1)) {
    if (IMG[value] != null) {
      document.images['mainImage'].src = IMG[value];
    }
    eval("list" + OPT[currentOrderIndex+1] + value + "()");
    document.addform.add_product_id.value = 'NULL';
  } else {
    document.addform.add_product_id.value = value;
  }
}
    
function addItem() {
  if (document.addform.add_product_id.value == 'NULL') {
    alert("Please enter all the required information.");
    return;
  } else {
    document.addform.submit();
  }
}

//-->
</script>
<%-- ====================================================== --%>
<%-- End Special Variant Code                                   --%>
<%-- ====================================================== --%>

<%
  // Get the value object of the request product id.
  ProductWorker.getProduct(pageContext,"productValue",request.getParameter("product_id"));
  ProductWorker.getAssociatedProducts(pageContext,"productValue","");
%>

<ofbiz:unless name="productValue">
  <center><h2>Product not found for Product ID "<%=UtilFormatOut.checkNull(request.getParameter("product_id"))%>"!</h2></center>
</ofbiz:unless>

<ofbiz:if name="productValue">
  <ofbiz:object name="productValue" property="productValue" />
  <br>
  <table border="0" width="100%" cellpadding="3">
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td align="left" valign="top" width="0">
        <ofbiz:entityfield attribute="productValue" field="largeImageUrl" prefix="<img src='" suffix="' name='mainImage' vspace='5' hspace='5' border='1' width='200' align=left>"/>
      </td>
      <td align="right" valign="top">
        <div class="head2"><ofbiz:entityfield attribute="productValue" field="productName"/></div>
        <div class="tabletext"><ofbiz:entityfield attribute="productValue" field="description"/></div>
        <div class="tabletext"><b><ofbiz:entityfield attribute="productValue" field="productId"/></b></div>
        <div class="tabletext"><b>Our price: <font color="#126544"><ofbiz:entityfield attribute="productValue" field="defaultPrice"/></font></b>
           (Reg. <ofbiz:entityfield attribute="productValue" field="defaultPrice"/>)</div>
        <div class="tabletext">Size:
            <%if (productValue.get("quantityIncluded") != null && productValue.getDouble("quantityIncluded").doubleValue() != 0) {%>
                <ofbiz:entityfield attribute="productValue" field="quantityIncluded"/>
            <%}%>
            <ofbiz:entityfield attribute="productValue" field="quantityUomId"/>
        </div>
        <%if (productValue.get("piecesIncluded") != null && productValue.getLong("piecesIncluded").longValue() != 0) {%>
            <ofbiz:entityfield attribute="productValue" field="piecesIncluded" prefix="<div class='tabletext'>Pieces: " suffix="</div>"/>
        <%}%>

        <p>&nbsp;</p>

        <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="addform" style='margin: 0;'>

          <%-- Variant Selection --%>
          <ofbiz:if name="variantTree" size="0">
            <%Debug.logInfo("FeatureOrder: " + featureOrder.size() + " / " + featureOrder);%>
            <ofbiz:iterator name="currentType" property="featureSet" type="java.lang.String">
              <%Debug.logInfo("CurrentType: " + currentType);%>
              <div class="tabletext">
                <select name="<%=currentType%>" onChange="getList(this.name, this.options[this.selectedIndex].value)">
                  <option><%=currentType%></option>
                </select>
              </div>
            </ofbiz:iterator>
              <input type='hidden' name="product_id" value='<ofbiz:entityfield attribute="productValue" field="productId"/>'>
              <input type='hidden' name="add_product_id" value='NULL'>
          </ofbiz:if>
          <ofbiz:unless name="variantTree" size="0">
            <input type='hidden' name="product_id" value='<ofbiz:entityfield attribute="productValue" field="productId"/>'>
            <input type='hidden' name="add_product_id" value='<ofbiz:entityfield attribute="productValue" field="productId"/>'>
          </ofbiz:unless>
          <%-- End of Variant Selection --%>

          <p>&nbsp;</p>
          <a href="javascript:addItem()" class="buttontext"><nobr>[Add to Cart]</nobr></a>&nbsp;
          <input type="text" size="5" name="quantity" value="1">

          <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
        </form>
        <script language="JavaScript">eval("list" + "<%=featureOrder.get(0)%>" + "()");</script>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td colspan="2">
        <div class="tabletext"><ofbiz:entityfield attribute="productValue" field="longDescription"/></div>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
  </table>

  <table width='100%'>
<%int listIndex = 1;%>
<!-- obsolete by -->
    <ofbiz:if name="obsoleteby">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2"><ofbiz:entityfield attribute="productValue" field="productName"/> is made obsolete by these products:</div></td></tr>
      <tr><td><hr class='sepbar'></td></tr>

      <ofbiz:iterator name="productAssoc" property="obsoleteby">
        <tr><td>
          <div class="tabletext">
            <a href='<ofbiz:url>/product?product_id=<ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></ofbiz:url>' class="buttontext"><ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></a>
            - <b><ofbiz:entityfield attribute="productAssoc" field="reason"/></b>
          </div>
        </td></tr>
        <%{%>
          <%GenericValue product = productAssoc.getRelatedOneCache("AssocProduct");%>
          <%pageContext.setAttribute("product", product);%>
          <tr>
            <td>
              <%@ include file="/catalog/productsummary.jsp"%>
            </td>
          </tr>
          <%listIndex++;%>
        <%}%>
        <tr><td><hr class='sepbar'></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

<!-- cross sells -->
    <ofbiz:if name="complement">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2">You might be interested in these as well:</div></td></tr>
      <tr><td><hr class='sepbar'></td></tr>

      <ofbiz:iterator name="productAssoc" property="complement">
        <tr><td>
          <div class="tabletext">
            <a href='<ofbiz:url>/product?product_id=<ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></ofbiz:url>' class="buttontext"><ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></a>
            - <b><ofbiz:entityfield attribute="productAssoc" field="reason"/></b>
          </div>
        </td></tr>
        <%{%>
          <%GenericValue product = productAssoc.getRelatedOneCache("AssocProduct");%>
          <%pageContext.setAttribute("product", product);%>
          <tr>
            <td>
              <%@ include file="/catalog/productsummary.jsp"%>
            </td>
          </tr>
          <%listIndex++;%>
        <%}%>
        <tr><td><hr class='sepbar'></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

<!-- up sells -->
    <ofbiz:if name="upgrade">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2">Try these instead of <ofbiz:entityfield attribute="productValue" field="productName"/>:</div></td></tr>
      <tr><td><hr class='sepbar'></td></tr>

      <ofbiz:iterator name="productAssoc" property="upgrade">
        <tr><td>
          <div class="tabletext">
            <a href='<ofbiz:url>/product?product_id=<ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></ofbiz:url>' class="buttontext"><ofbiz:entityfield attribute="productAssoc" field="productIdTo"/></a>
            - <b><ofbiz:entityfield attribute="productAssoc" field="reason"/></b>
          </div>
        </td></tr>
        <%{%>
          <%GenericValue product = productAssoc.getRelatedOneCache("AssocProduct");%>
          <%pageContext.setAttribute("product", product);%>
          <tr>
            <td>
              <%@ include file="/catalog/productsummary.jsp"%>
            </td>
          </tr>
          <%listIndex++;%>
        <%}%>
        <tr><td><hr class='sepbar'></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

<!-- obsolescence -->
    <ofbiz:if name="obsolescence">
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2"><div class="head2"><ofbiz:entityfield attribute="productValue" field="productName"/> makes these products obsolete:</div></td></tr>
      <tr><td><hr class='sepbar'></td></tr>

      <ofbiz:iterator name="productAssoc" property="obsolescence">
        <tr><td>
          <div class="tabletext">
            <a href="<ofbiz:url>/product?product_id=<%=productAssoc.getString("productId")%></ofbiz:url>" class="buttontext"><%=productAssoc.getString("productId")%></a>
            - <b><ofbiz:entityfield attribute="productAssoc" field="reason"/></b>
          </div>
        </td></tr>
        <%{%>
          <%GenericValue product = productAssoc.getRelatedOneCache("MainProduct");%>
          <%pageContext.setAttribute("product", product);%>
          <tr>
            <td>
              <%@ include file="/catalog/productsummary.jsp"%>
            </td>
          </tr>
          <%listIndex++;%>
        <%}%>
        <tr><td><hr class='sepbar'></td></tr>
      </ofbiz:iterator>
    </ofbiz:if>

  </table>
</ofbiz:if>
