
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="helper" type="org.ofbiz.core.entity.GenericHelper" scope="application" />

<%
  Iterator prods = UtilMisc.toIterator(helper.findByAnd("Product", null, null));
  while(prods.hasNext())
  {
    GenericValue prod1 = (GenericValue)prods.next();
    KeywordSearch.induceKeywords(prod1, helper);
  }
  
  if(request.getParameter("makeall") != null)
  {
    String[] wordBag = {"a", "product", "big", "ugly", "pretty", "small", "under", "over", "one", "two", "three", "four", "five", "six", "seven", "eight", "tree"};
    String[] longWordBag = {"b", "item", "little", "cute", "frightening", "massive", "top", "btoom", "bush", "shrub", "gadget"};

    for(int cat=1; cat<=400; cat++)
    {
      String parentId = cat<=20?"CATALOG1":"" + (cat/20);
      helper.create("ProductCategory", UtilMisc.toMap("productCategoryId", "" + cat, "primaryParentCategoryId", parentId, "description", "Category " + cat));
      helper.create("ProductCategoryRollup", UtilMisc.toMap("productCategoryId", "" + cat, "parentProductCategoryId", parentId));
      for(int prod=1; prod<=50; prod++)
      {
        String desc = "Cool Description";
        for(int i=0; i<10; i++) {
          int wordNum = (int)(Math.random()*(wordBag.length-1));
          desc += (" " + wordBag[wordNum]);
        }
        String longDesc = "Cool LONG Description";
        for(int i=0; i<50; i++) {
          int wordNum = (int)(Math.random()*(longWordBag.length-1));
          longDesc += (" " + longWordBag[wordNum]);
        }
        Double price = new Double(2.99 + prod);
        GenericValue product = helper.create("Product", UtilMisc.toMap("productId", "" + (cat*100 + prod), "primaryProductCategoryId", "" + (cat), "name", "Product " + "" + (cat*100 + prod), "description", desc, "longDescription", longDesc, "defaultPrice", price));
        KeywordSearch.induceKeywords(product, helper);
        helper.create("ProductCategoryMember", UtilMisc.toMap("productId", "" + (cat*100 + prod), "productCategoryId", "" + (cat)));
      }
    }
  }
%>
