/*
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@version    1.0
 */

importClass(Packages.java.util.HashMap);
importClass(Packages.org.ofbiz.core.util.SiteDefs);
importClass(Packages.org.ofbiz.core.util.UtilHttp);

var userLogin = session.getAttribute("userLogin");

var tryEntity = true;
var errorMessage = request.getAttribute(SiteDefs.ERROR_MESSAGE);
if (errorMessage != null && errorMessage.length() > 0) {
    tryEntity = false;    
}

var donePage = request.getParameter("DONE_PAGE");
if (donePage == null || donePage.length() == 0) donePage = "viewprofile";

var userLoginData = userLogin;
if (!tryEntity) userLoginData = UtilHttp.getParameterMap(request);
if (userLoginData == null) userLoginData = new HashMap();

context.put("donePage", donePage);
context.put("userLoginData", userLoginData);

