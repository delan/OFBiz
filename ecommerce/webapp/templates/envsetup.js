importPackage(Packages.org.ofbiz.core.util);
importPackage(Packages.java.lang);
importPackage(Packages.java.net);
importPackage(Packages.java.util);

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
