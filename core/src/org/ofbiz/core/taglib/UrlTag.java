/*
 * $id$
 * $Log$
 * Revision 1.3  2001/09/21 11:15:17  jonesde
 * Updates related to Tomcat 4 update, bug fixes.
 *
 * Revision 1.2  2001/08/06 00:45:09  azeneski
 * minor adjustments to tag files. added new format tag.
 *
 * Revision 1.1  2001/08/05 00:48:47  azeneski
 * Added new core JSP tag library. Non-application specific taglibs.
 *
 */

package org.ofbiz.core.taglib;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspException;
import java.io.IOException;

import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.Debug;

/**
 * <p><b>Title:</b> UrlTag.java
 * <p><b>Description:</b> Custom JSP Tag to EncodeURL and add CONTROL_PATH to a URL.
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on August 4, 2001, 8:21 PM
 */
public class UrlTag extends BodyTagSupport {
  
  public int doEndTag() throws JspException {
    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
    BodyContent body = getBodyContent();
    
    String controlPath = (String) request.getAttribute(SiteDefs.CONTROL_PATH);
    String baseURL = body.getString();
    String newURL = controlPath + baseURL;
    //Debug.logInfo("baseURL: " + baseURL + "; newURL: " + newURL);
    
    body.clearBody();
    
    try {
      String encodedURL = response.encodeURL(newURL);
      getPreviousOut().print(encodedURL);
    }
    catch (IOException e) {
      throw new JspException(e.getMessage());
    }
    return SKIP_BODY;
  }
}



