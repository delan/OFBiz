<%--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones
 *@created    Sep 10 2001
 *@version    1.0
--%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*, org.ofbiz.core.util.*, org.ofbiz.core.entity.*"%>
<%@ page import="org.ofbiz.core.pseudotag.*, org.ofbiz.commonapp.product.product.*"%>
<%@ page import="org.ofbiz.commonapp.product.catalog.*"%>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<%-- ====================================================== --%>
<%-- Get the requested product                              --%>
<%-- ====================================================== --%>
<%-- The product.jsp puts the product it finds in the request attribute "product" --%>

<%String contentPathPrefix = CatalogWorker.getContentPathPrefix(pageContext);%>
<ofbiz:if name="product">
    <ofbiz:object name="product" property="product"/>
    <%String productId = product.getString("productId");%>
    <%pageContext.setAttribute("product_id", productId);%>
    <%String productTypeId = product.getString("productTypeId");%>
    <%Map featureTypes = new HashMap();%>
    <%List featureOrder = null;%>

    <%String categoryId = request.getParameter("category_id");%>
    <%if (categoryId == null) categoryId = product.getString("primaryProductCategoryId");%>
    <%if (categoryId != null) pageContext.setAttribute("category_id", categoryId);%>

    <ofbiz:if name="category_id">
        <ofbiz:service name='getPreviousNextProducts'>
            <ofbiz:param name='categoryId' attribute='category_id'/>
            <ofbiz:param name='productId' attribute='product_id'/>
        </ofbiz:service>
    </ofbiz:if>

    <%-- calculate the "your" price --%>
    <ofbiz:service name='calculateProductPrice'>
        <ofbiz:param name='product' attribute='product'/>
        <ofbiz:param name='prodCatalogId' value='<%=CatalogWorker.getCurrentCatalogId(pageContext)%>'/>
        <ofbiz:param name='autoUserLogin' attribute='autoUserLogin'/>
        <%-- don't need to pass the partyId because it will use the one from the currently logged in user, if there user logged in --%>
        <%-- returns: isSale, price, orderItemPriceInfos and optionally: listPrice, defaultPrice, averageCost --%>
    </ofbiz:service>

    <%-- ====================================================== --%>
    <%-- Special Variant Code                                   --%>
    <%-- ====================================================== --%>
    <% if ("Y".equals(product.getString("isVirtual"))) {%>
        <ofbiz:service name="getProductFeatureSet">
            <ofbiz:param name='productId' value='<%=productId%>'/>
        </ofbiz:service>
        <ofbiz:if name="featureSet" size="0">
            <ofbiz:service name="getProductVariantTree">
                <ofbiz:param name='productId' value='<%=productId%>'/>
                <ofbiz:param name='featureOrder' attribute='featureSet'/>
                <ofbiz:param name='prodCatalogId' value='<%=CatalogWorker.getCurrentCatalogId(request)%>'/>
            </ofbiz:service>
 
            <ofbiz:if name="variantTree" size="0">

            <%
              featureOrder = new LinkedList((Collection) pageContext.getAttribute("featureSet"));
              Map variantTree = (Map) pageContext.getAttribute("variantTree");
              Map imageMap = (Map) pageContext.getAttribute("variantSample");
              Debug.logVerbose("Setup variables: " + featureOrder + " / " + variantTree + " / " + imageMap);

              Iterator foi = featureOrder.iterator();
              while (foi.hasNext()) {
                  String featureKey = (String) foi.next();
                  GenericValue featureValue = delegator.findByPrimaryKeyCache("ProductFeatureType", UtilMisc.toMap("productFeatureTypeId", featureKey));
                  String fValue = featureValue.get("description") != null ? featureValue.getString("description") : featureValue.getString("productFeatureTypeId");
                  featureTypes.put(featureKey, fValue);
              }
            %>


            <%!
                public static String buildNext(Map map, List order, String current, String prefix, Map featureTypes) {
                    Set keySet = map.keySet();
                    int ct = 0;
                    Iterator i = keySet.iterator();
                    StringBuffer buf = new StringBuffer();
                    buf.append("function list" + current + prefix + "() { ");
                    buf.append("document.forms[\"addform\"].elements[\"" + current + "\"].options.length = 1;");
                    buf.append("document.forms[\"addform\"].elements[\"" + current + "\"].options[0] = new Option(\"" + featureTypes.get(current) + "\",\"\",true,true);");
                    while (i.hasNext()) {
                        Object key = i.next();
                        Object value = map.get(key);
                        Debug.logVerbose("" + key + " value: " + value);
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
                            buf.append(buildNext(value, order, nextOrder, newPrefix, featureTypes));
                        }
                    }
                    return buf.toString();
                }
            %>

           <script language="JavaScript">
           <%-- NOTE: this JavaScript section is not in a comment because Java scriptlets (the <%...%> ones) are ignored in comments --%>
                var IMG = new Array(<%=variantTree.size()%>);
                var OPT = new Array(<%=featureOrder.size()%>);

                <% for (int li = 0; li < featureOrder.size(); li++) {%>
                     OPT[<%=li%>] = "<%=featureOrder.get(li)%>";
                <% } %>

                <%-- Build the top level --%>
                <% String topLevelName = (String) featureOrder.get(0);%>
                function list<%=topLevelName%>() {
                    document.forms["addform"].elements["<%=topLevelName%>"].options.length = 1;
                    document.forms["addform"].elements["<%=topLevelName%>"].options[0] = new Option("<%=featureTypes.get(topLevelName)%>","",true,true);
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
                          Object varTree = variantTree.get(tli.next());
                          if (varTree instanceof Map) {
                %>
                            <%=buildNext((Map)varTree, featureOrder, (String)featureOrder.get(1), cnt, featureTypes)%>
                <%
                         }
                          topLevelKeysCt++;
                      }
                  }
                %>
                <%-- End of Dynamic Gen --%>
    
                function findIndex(name) {
                   for (i=0; i<OPT.length; i++) {
                       if (OPT[i] == name)
                           return i;         
                   }
                   return -1;
                }
    
                function getList(name, value, src) {
                    var value2 = 'NULL';
                    currentOrderIndex = findIndex(name);                    
                    if (src == 1 && OPT.length == 1) {
                        value2 = document.forms["addform"].elements[name].options[(value*1)+1].value;                        
                    }                    
                    if (currentOrderIndex < 0 || value == "")                      
                        return;
                    if (currentOrderIndex < (OPT.length - 1) || OPT.length == 1) {                        
                        if (IMG[value] != null) {
                            if (document.images['mainImage'] != null)
                                document.images['mainImage'].src = IMG[value];
                            document.addform.<%=topLevelName%>.selectedIndex = (value*1)+1;
                        }
                        if (OPT.length != 1) {
                            eval("list" + OPT[currentOrderIndex+1] + value + "()");                        
                            document.addform.add_product_id.value = 'NULL';
                        } else {
                            if (value2 == 'NULL')
                                value2 = value;
                            document.addform.add_product_id.value = value2;
                        }
                    } else {
                        document.addform.add_product_id.value = value;
                    }
                }
            </script>
            </ofbiz:if>
        </ofbiz:if>
    <%}%>
    <%-- ====================================================== --%>
    <%-- End Special Variant Code                               --%>
    <%-- ====================================================== --%>

    <script language="JavaScript">
    <!--
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

  <%-- ======================== --%>
  <%-- Begin Basic Product Info --%>
  <%-- ======================== --%>

  <br>
  <table border="0" width="100%" cellpadding="2" cellspacing='0'>
    <ofbiz:if name="category">
      <tr>
        <td colspan="2" align="right">
          <ofbiz:if name="previousProductId">
              <a href='<ofbiz:url>/product?category_id=<%=categoryId%>&product_id=<ofbiz:print attribute="previousProductId"/></ofbiz:url>' class="buttontext">[Previous]</a>&nbsp;|&nbsp;
          </ofbiz:if>
          <a href="<ofbiz:url>/category?category_id=<%=categoryId%></ofbiz:url>" class="buttontext"><%EntityField.run("category", "description", pageContext);%></a>
          <ofbiz:if name="nextProductId">
              &nbsp;|&nbsp;<a href='<ofbiz:url>/product?category_id=<%=categoryId%>&product_id=<ofbiz:print attribute="nextProductId"/></ofbiz:url>' class="buttontext">[Next]</a>
          </ofbiz:if>
        </td>
      </tr>
    </ofbiz:if>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td align="left" valign="top" width="0">
        <%if (UtilValidate.isNotEmpty(product.getString("largeImageUrl"))) {%>
            <img src='<ofbiz:contenturl><%=contentPathPrefix%><%=product.getString("largeImageUrl")%></ofbiz:contenturl>' name='mainImage' vspace='5' hspace='5' border='1' width='200' align=left>
        <%}%>
      </td>
      <td align="right" valign="top">
        <div class="head2"><%EntityField.run("product", "productName", pageContext);%></div>
        <div class="tabletext"><%EntityField.run("product", "description", pageContext);%></div>
        <div class="tabletext"><b><%EntityField.run("product", "productId", pageContext);%></b></div>
        <%-- for prices:
                - if price < listPrice, show
                - if price < defaultPrice and defaultPrice < listPrice, show default
                - if isSale show price with salePrice style and print "On Sale!"
        --%>
        <%if (pageContext.getAttribute("listPrice") != null && pageContext.getAttribute("price") != null && 
                ((Double) pageContext.getAttribute("price")).doubleValue() < ((Double) pageContext.getAttribute("listPrice")).doubleValue()) {%>
            <div class="tabletext">List price: <span class='basePrice'><ofbiz:field attribute="listPrice" type="currency"/></span></div>
        <%}%>
        <%if (pageContext.getAttribute("listPrice") != null && pageContext.getAttribute("defaultPrice") != null && pageContext.getAttribute("price") != null && 
                ((Double) pageContext.getAttribute("price")).doubleValue() < ((Double) pageContext.getAttribute("defaultPrice")).doubleValue() &&
                ((Double) pageContext.getAttribute("defaultPrice")).doubleValue() < ((Double) pageContext.getAttribute("listPrice")).doubleValue()) {%>
            <div class="tabletext">Regular price: <span class='basePrice'><ofbiz:field attribute="defaultPrice" type="currency"/></span></div>
        <%}%>
        <div class="tabletext"><b>
            <ofbiz:if name="isSale"><span class='salePrice'>On Sale!</span></ofbiz:if>
            Your price: <span class='<ofbiz:if name="isSale">salePrice</ofbiz:if><ofbiz:unless name="isSale">normalPrice</ofbiz:unless>'><ofbiz:field attribute="price" type="currency"/></span>
        </b></div>
        <%if (product.get("quantityIncluded") != null && product.getDouble("quantityIncluded").doubleValue() != 0) {%>
            <div class="tabletext">Size:
              <%EntityField.run("product", "quantityIncluded", pageContext);%>
              <%EntityField.run("product", "quantityUomId", pageContext);%>
            </div>
        <%}%>
        </div>
        <%if (product.get("piecesIncluded") != null && product.getLong("piecesIncluded").longValue() != 0) {%>
            <ofbiz:entityfield attribute="product" field="piecesIncluded" prefix="<div class='tabletext'>Pieces: " suffix="</div>"/>
        <%}%>

        <p>&nbsp;</p>

        <form method="POST" action="<ofbiz:url>/additem<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="addform" style='margin: 0;'>
        <%boolean inStock = true;%>
          <%-- ================= --%>
          <%-- Variant Selection --%>
          <%-- ================= --%>
          <%if ("Y".equals(product.getString("isVirtual"))) {%>
              <ofbiz:if name="variantTree" size="0">            
                <ofbiz:iterator name="currentType" property="featureSet" type="java.lang.String">
                  <%Debug.logVerbose("CurrentType: " + currentType);%>
                  <div class="tabletext">
                    <select name="<%=currentType%>" onChange="getList(this.name, this.options[this.selectedIndex].value)">
                      <option><%=featureTypes.get(currentType)%></option>
                    </select>
                  </div>
                </ofbiz:iterator>
                <input type='hidden' name="product_id" value='<%EntityField.run("product", "productId", pageContext);%>'>
                <input type='hidden' name="add_product_id" value='NULL'>

              </ofbiz:if>
              <ofbiz:unless name="variantTree" size="0">
                <!-- inventory check is a bit different here, instead of showing drop-downs show message -->
                <input type='hidden' name="product_id" value='<%EntityField.run("product", "productId", pageContext);%>'>
                <input type='hidden' name="add_product_id" value='NULL'>
                <div class='tabletext'><b>This item is out of stock.</b></div>
                <%inStock = false;%>
              </ofbiz:unless>
          <%} else {%>
            <input type='hidden' name="product_id" value='<%EntityField.run("product", "productId", pageContext);%>'>
            <input type='hidden' name="add_product_id" value='<%EntityField.run("product", "productId", pageContext);%>'>

            <%if (!CatalogWorker.isCatalogInventoryAvailable(request, productId, 1.0)) {%>
                <%if (CatalogWorker.isCatalogInventoryRequired(request, product)) {%>
                    <div class='tabletext'><b>This item is out of stock.</b></div>
                    <%inStock = false;%>
                <%} else {%>
                    <div class='tabletext'><b><%EntityField.run("product", "inventoryMessage", pageContext);%></b></div>
                <%}%>
            <%}%>
          <%}%>
          <%-- ======================== --%>
          <%-- End of Variant Selection --%>
          <%-- ======================== --%>

          <p>&nbsp;</p>
          <%java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();%>
          <%if (product.get("introductionDate") != null && nowTimestamp.before(product.getTimestamp("introductionDate"))) {%>
              <%-- check to see if introductionDate hasn't passed yet --%>
              <div class='tabletext' style='color: red;'>This product has not yet been made available for sale.</div>
          <%} else if (product.get("salesDiscontinuationDate") != null && nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"))) {%>
              <%-- check to see if salesDiscontinuationDate has passed --%>
              <div class='tabletext' style='color: red;'>This product is no longer available for sale.</div>
          <%} else {%>
              <%if (inStock) {%>
                  <a href="javascript:addItem()" class="buttontext"><nobr>[Add to Cart]</nobr></a>&nbsp;
                  <input type="text" size="5" name="quantity" value="1">
              <%}%>

              <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
          <%}%>
        </form>

        <%-- =========================== --%>
        <%-- Prefill The First Top Level --%>
        <%-- =========================== --%>
        <ofbiz:if name="variantTree" size="0">
          <script language="JavaScript">eval("list" + "<%=featureOrder.get(0)%>" + "()");</script>
        </ofbiz:if>
                
        <%-- =============== --%>
        <%-- Optional Images --%>
        <%-- =============== --%>
        <ofbiz:if name="variantSample" size="0">
          <% Map imageMap = (Map) pageContext.getAttribute("variantSample"); %>
          <% Set imageSet = imageMap.keySet(); %>
          <p>&nbsp;</p>
          <table cellspacing="0" cellpadding="0">
            <tr>
              <%int ii=0; Iterator imIt=imageSet.iterator();%>
              <%while(imIt.hasNext()){%>
              <%String featureDescription = (String) imIt.next();%>
              <%String imageUrl = ((GenericValue)imageMap.get(featureDescription)).getString("smallImageUrl");%>
              <%if (imageUrl != null && imageUrl.length() > 0){%>
                <td>
                  <table cellspacing="0" cellpadding="0">
                    <tr><td><a href="#"><img src="<ofbiz:contenturl><%=contentPathPrefix%><%=imageUrl%></ofbiz:contenturl>" border="0" width="60" height="60" onclick="javascript:getList('<%=featureOrder.get(0)%>','<%=ii%>',1);"></a></td></tr>
                    <tr><td align="center" valign="top"><span class="tabletext"><%=featureDescription%></span></td></tr>
                  </table>
                </td>
              <%}ii++;}%>
            </tr>
          </table>
        </ofbiz:if>
        <%-- ====================== --%>
        <%-- End of optional images --%>
        <%-- ====================== --%>

      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td colspan="2">
        <div class="tabletext"><%EntityField.run("product", "longDescription", pageContext);%></div>
      </td>
    </tr>
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
  </table>

  <%-- =========================== --%>
  <%-- Upgrades/Cross-sell/Up-sell --%>
  <%-- =========================== --%>
<%pageContext.setAttribute("productValue", pageContext.getAttribute("product"));%>
  <table width='100%'>
    <%int listIndex = 1;%>

    <%-- obsolete --%>
    <ofbiz:service name='getAssociatedProducts'>
        <ofbiz:param name='productId' value='<%=productId%>'/>
        <ofbiz:param name='type' value='<%="PRODUCT_OBSOLESCENCE"%>'/>
    </ofbiz:service>
    <ofbiz:if name="assocProducts" size="0">
        <tr><td>&nbsp;</td></tr>
        <tr><td colspan="2"><div class="head2"><%EntityField.run("productValue", "productName", pageContext);%> is made obsolete by these products:</div></td></tr>
        <tr><td><hr class='sepbar'></td></tr>

        <ofbiz:iterator name="productAssoc" property="assocProducts">
            <tr><td>
              <div class="tabletext">
                <a href='<ofbiz:url>/product?product_id=<%EntityField.run("productAssoc", "productIdTo", pageContext);%></ofbiz:url>' class="buttontext">
                  <%EntityField.run("productAssoc", "productIdTo", pageContext);%>
                </a>
                - <b><%EntityField.run("productAssoc", "reason", pageContext);%></b>
              </div>
            </td></tr>

              <%{%>
                <%GenericValue asscProduct = productAssoc.getRelatedOneCache("AssocProduct");%>
                <%pageContext.setAttribute("product", asscProduct);%>
                <tr>
                  <td>
                    <%@ include file="/catalog/productsummary.jsp"%>
                  </td>
                </tr>
                <%listIndex++;%>
              <%}%>

            <tr><td><hr class='sepbar'></td></tr>
        </ofbiz:iterator>
        <%pageContext.removeAttribute("assocProducts");%>
    </ofbiz:if>

    <%-- cross sell --%>
    <ofbiz:service name='getAssociatedProducts'>
        <ofbiz:param name='productId' value='<%=productId%>'/>
        <ofbiz:param name='type' value='<%="PRODUCT_COMPLEMENT"%>'/>
    </ofbiz:service>
    <ofbiz:if name="assocProducts" size="0">
        <tr><td>&nbsp;</td></tr>
        <tr><td colspan="2"><div class="head2">You might be interested in these as well:</div></td></tr>
        <tr><td><hr class='sepbar'></td></tr>

        <ofbiz:iterator name="productAssoc" property="assocProducts">
            <tr><td>
              <div class="tabletext">
                <a href='<ofbiz:url>/product?product_id=<%EntityField.run("productAssoc", "productIdTo", pageContext);%></ofbiz:url>' class="buttontext">
                  <%EntityField.run("productAssoc", "productIdTo", pageContext);%>
                </a>
                - <b><%EntityField.run("productAssoc", "reason", pageContext);%></b>
              </div>
            </td></tr>

              <%try {%>
                <%GenericValue asscProduct = productAssoc.getRelatedOneCache("AssocProduct");%>
                <%pageContext.setAttribute("product", asscProduct);%>
                <tr>
                  <td>
                    <%@ include file="/catalog/productsummary.jsp"%>
                  </td>
                </tr>
                <%listIndex++;%>
              <%} catch (Exception e) { Debug.logError(e); throw e; }%>

            <tr><td><hr class='sepbar'></td></tr>
        </ofbiz:iterator>
        <%pageContext.removeAttribute("assocProducts");%>
    </ofbiz:if>

    <%-- up sell --%>
    <ofbiz:service name='getAssociatedProducts'>
        <ofbiz:param name='productId' value='<%=productId%>'/>
        <ofbiz:param name='type' value='<%="PRODUCT_UPGRADE"%>'/>
    </ofbiz:service>
    <ofbiz:if name="assocProducts" size="0">
        <tr><td>&nbsp;</td></tr>
        <tr><td colspan="2"><div class="head2">Try these instead of <%EntityField.run("productValue", "productName", pageContext);%>:</div></td></tr>
        <tr><td><hr class='sepbar'></td></tr>

        <ofbiz:iterator name="productAssoc" property="assocProducts">
            <tr><td>
              <div class="tabletext">
                <a href='<ofbiz:url>/product?product_id=<%EntityField.run("productAssoc", "productIdTo", pageContext);%></ofbiz:url>' class="buttontext">
                  <%EntityField.run("productAssoc", "productIdTo", pageContext);%>
                </a>
                - <b><%EntityField.run("productAssoc", "reason", pageContext);%></b>
              </div>
            </td></tr>

              <%{%>
                <%GenericValue asscProduct = productAssoc.getRelatedOneCache("AssocProduct");%>
                <%pageContext.setAttribute("product", asscProduct);%>
                <tr>
                  <td>
                    <%@ include file="/catalog/productsummary.jsp"%>
                  </td>
                </tr>
                <%listIndex++;%>
              <%}%>

            <tr><td><hr class='sepbar'></td></tr>
        </ofbiz:iterator>
        <%pageContext.removeAttribute("assocProducts");%>
    </ofbiz:if>

    <%-- obsolescence --%>
    <ofbiz:service name='getAssociatedProducts'>
        <ofbiz:param name='productIdTo' value='<%=productId%>'/>
        <ofbiz:param name='type' value='<%="PRODUCT_OBSOLESCENCE"%>'/>
    </ofbiz:service>
    <ofbiz:if name="assocProducts" size="0">
        <tr><td>&nbsp;</td></tr>
        <tr><td colspan="2"><div class="head2"><%EntityField.run("productValue", "productName", pageContext);%> makes these products obsolete:</div></td></tr>
        <tr><td><hr class='sepbar'></td></tr>

        <ofbiz:iterator name="productAssoc" property="assocProducts">
            <tr><td>
              <div class="tabletext">
                <a href='<ofbiz:url>/product?product_id=<%EntityField.run("productAssoc", "productIdTo", pageContext);%></ofbiz:url>' class="buttontext">
                  <%EntityField.run("productAssoc", "productIdTo", pageContext);%>
                </a>
                - <b><%EntityField.run("productAssoc", "reason", pageContext);%></b>
              </div>
            </td></tr>

              <%{%>
                <%GenericValue asscProduct = productAssoc.getRelatedOneCache("MainProduct");%>
                <%pageContext.setAttribute("product", asscProduct);%>
                <tr>
                  <td>
                    <%@ include file="/catalog/productsummary.jsp"%>
                  </td>
                </tr>
                <%listIndex++;%>
              <%}%>

            <tr><td><hr class='sepbar'></td></tr>
        </ofbiz:iterator>
        <%pageContext.removeAttribute("assocProducts");%>
    </ofbiz:if>
  </table>
</ofbiz:if>
