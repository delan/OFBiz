importPackage(Packages.java.lang);
importPackage(Packages.java.net);
importPackage(Packages.java.util);
importPackage(Packages.org.ofbiz.core.util);
importPackage(Packages.org.ofbiz.core.entity);
importPackage(Packages.org.ofbiz.commonapp.product.catalog);
importPackage(Packages.org.ofbiz.commonapp.order.shoppingcart);

var userLogin = session.getAttribute(SiteDefs.USER_LOGIN);
if (userLogin != null) request.setAttribute("userLogin", userLogin);

var ecommercePropertiesUrl = application.getResource("/WEB-INF/ecommerce.properties");
var layoutSettings = new HashMap();
request.setAttribute("layoutSettings", layoutSettings);

layoutSettings.put("companyName", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name"));
layoutSettings.put("companySubtitle", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.subtitle"));
layoutSettings.put("headerImageUrl", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.image.url"));
layoutSettings.put("headerMiddleBackgroundUrl", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.middle.background.url"));
layoutSettings.put("headerRightBackgroundUrl", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.right.background.url"));

var prodCatalog = CatalogWorker.getProdCatalog(request);
if (prodCatalog != null) {
    var catalogStyleSheet = prodCatalog.get("styleSheet");
    if (catalogStyleSheet != null) request.setAttribute("catalogStyleSheet", catalogStyleSheet);
    var catalogHeaderLogo = prodCatalog.get("headerLogo");
    if (catalogHeaderLogo != null) request.setAttribute("catalogHeaderLogo", catalogHeaderLogo);
}

var eventMsgReq = request.getAttribute(SiteDefs.EVENT_MESSAGE);
var errorMsgReq = request.getAttribute(SiteDefs.ERROR_MESSAGE);
var errorMsgSes = session.getAttribute(SiteDefs.ERROR_MESSAGE);

if (eventMsgReq != null) {
    request.setAttribute("eventMsgReq", UtilFormatOut.replaceString(eventMsgReq, "\n", "<br>"));
    request.removeAttribute(SiteDefs.EVENT_MESSAGE);
}
if (errorMsgReq != null) {
    request.setAttribute("errorMsgReq", UtilFormatOut.replaceString(errorMsgReq, "\n", "<br>"));
    request.removeAttribute(SiteDefs.ERROR_MESSAGE);
}
if (errorMsgSes != null) {
    request.setAttribute("errorMsgSes", UtilFormatOut.replaceString(errorMsgSes, "\n", "<br>"));
    session.removeAttribute(SiteDefs.ERROR_MESSAGE);
}

//shopping cart prep
var shoppingCart = session.getAttribute(SiteDefs.SHOPPING_CART);
if (shoppingCart != null) {
    request.setAttribute("shoppingCartSize", new Integer(shoppingCart.size()));
    request.setAttribute("shoppingCartGrandTotal", new Double(shoppingCart.getGrandTotal()));
    var cartLines = shoppingCart.items();
    request.setAttribute("shoppingCartLines", cartLines);
} else {
    request.setAttribute("shoppingCartSize", new Integer(0));
}