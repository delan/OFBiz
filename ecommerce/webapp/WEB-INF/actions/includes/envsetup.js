importPackage(Packages.java.lang);
importPackage(Packages.java.net);
importPackage(Packages.java.util);
importPackage(Packages.org.ofbiz.core.util);
importPackage(Packages.org.ofbiz.core.entity);
importClass(Packages.org.ofbiz.commonapp.product.catalog.CatalogWorker);
importClass(Packages.org.ofbiz.commonapp.common.CommonWorkers);
importPackage(Packages.org.ofbiz.commonapp.order.shoppingcart);

var userLogin = session.getAttribute(SiteDefs.USER_LOGIN);

var ecommercePropertiesUrl = application.getResource("/WEB-INF/ecommerce.properties");
var layoutSettings = new HashMap();
context.put("layoutSettings", layoutSettings);

layoutSettings.put("companyName", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name"));
layoutSettings.put("companySubtitle", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.subtitle"));
layoutSettings.put("headerImageUrl", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.image.url"));
layoutSettings.put("headerMiddleBackgroundUrl", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.middle.background.url"));
layoutSettings.put("headerRightBackgroundUrl", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.right.background.url"));

var prodCatalog = CatalogWorker.getProdCatalog(request);
if (prodCatalog != null) {
    var catalogStyleSheet = prodCatalog.get("styleSheet");
    if (catalogStyleSheet != null) context.put("catalogStyleSheet", catalogStyleSheet);
    var catalogHeaderLogo = prodCatalog.get("headerLogo");
    if (catalogHeaderLogo != null) context.put("catalogHeaderLogo", catalogHeaderLogo);
}

context.put("checkLoginUrl", CommonWorkers.makeLoginUrl(request, "checkLogin"));
context.put("catalogQuickaddUse", CatalogWorker.getCatalogQuickaddUse(request));

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
