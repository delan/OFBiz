package org.ofbiz.commonapp.webevent;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;

/**
 * <p><b>Title:</b> JSP Tag which calls the web event dispatcher
 * <p><b>Description:</b> Custom JSP Tag which calls the WebEventDispatch dispatcher, and determines whether or not to process the rest of the page.
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
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
 *@author     David Jones
 *@created    May 21, 2001
 *@version    1.0
 */
public class WebEventDispatchTag extends TagSupport
{
  String loginRequired = null;

  /** Setter for the loginRequired attribute
   * @param loginRequired The value for loginRequired
   */  
  public void setLoginRequired(String loginRequired)
  {
    this.loginRequired = loginRequired;
  }

  /** Getter for the loginRequired attribute
   * @return The current value of the loginRequired attribute
   */  
  public String getLoginRequired()
  {
    return loginRequired;
  }

  /** Handles the start tag; does nothing, just tells the JSP container to skip the tag body.
   * @throws JspTagException Standard J2EE JspTagException
   * @return Always returns SKIP_BODY
   */  
  public int doStartTag() throws JspTagException
  {
    return SKIP_BODY;
  }

  /** Handles the eng tag. Calls the <CODE>WebEventDispatch.dispatch()</CODE> function, passing in the <CODE>request</CODE> and <CODE>response</CODE> from the current <CODE>pageContext</CODE> and the loginRequired boolean from the value of the <CODE>loginRequired</CODE> attribute.
   * @throws JspTagException Standard J2EE JspTagException
   * @return Returns EVAL_PAGE if the web event dispatcher returns true, otherwise returns SKIP_PAGE.
   */  
  public int doEndTag() throws JspTagException
  {
    boolean evalPage = true;
    boolean lrBool = true;
    if(loginRequired != null) lrBool = (loginRequired.compareToIgnoreCase("false") != 0);

    try { evalPage = WebEventDispatch.dispatch((HttpServletRequest)pageContext.getRequest(), (HttpServletResponse)pageContext.getResponse(), lrBool); }
    catch(Exception exc) { exc.printStackTrace(); }

    if(evalPage) return EVAL_PAGE;
    else return SKIP_PAGE;
  }
}