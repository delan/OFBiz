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

importPackage(Packages.java.lang);
importPackage(Packages.java.util);
importPackage(Packages.org.ofbiz.core.util);

var dispatcher = request.getAttribute("dispatcher");
var delegator = request.getAttribute("delegator");

var userLogin = session.getAttribute("userLogin");
var person = null;
if (userLogin != null) {
    person = userLogin.getRelatedOne("Person");
}

var tryEntity = true;
var errorMessage = request.getAttribute(SiteDefs.ERROR_MESSAGE);
if (errorMessage != null && errorMessage.length() > 0) {
    tryEntity = false;    
}
var personData = person;
if (!tryEntity) {
    personData = UtilHttp.getParameterMap(request);
}

var donePage = request.getParameter("DONE_PAGE");
if (donePage == null || donePage.length() == 0) { donePage = "viewprofile" }

context.put("person", person);
context.put("personData", personData);
context.put("donePage", donePage);

