/*
 * $Id$
 *
 * Copyright (c) 2001 Sun Microsystems Inc., published in "Advanced Java Server Pages" by Prentice Hall PTR
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.ofbiz.core.region;

import java.net.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Tag to put a section in a region
 *
 *@author     David M. Geary in the book "Advanced Java Server Pages"
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 26, 2002
 *@version    1.0
 */
public class PutTag extends BodyTagSupport {
    private String section, role, permission, action, content, direct = null;
    
    public void setSection(String section) { this.section = section; }
    public void setRole(String role) { this.role = role; }
    public void setPermission(String permission) { this.permission = permission; }
    public void setAction(String action) { this.action = action; }
    public void setDirect(String direct) { this.direct = direct; }
    public void setContent(String cntnt) { this.content = cntnt; }
    
    public String getSection() { return section; }
    public String getRole() { return role; }
    public String getPermission() { return permission; }
    public String getAction() { return action; }
    public String getContent() { return content; }
    public String getDirect() { return direct; }
    
    public int doAfterBody() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        URL regionFile = null;
        try {
            regionFile = pageContext.getServletContext().getResource(Region.regionsFileName);
        } catch (java.net.MalformedURLException e) {
            throw new IllegalArgumentException("regions.xml file URL invalid: " + e.getMessage());
        }
        
        if(role != null && !request.isUserInRole(role))
            return EVAL_PAGE;
        
        RegionTag regionTag = (RegionTag) findAncestorWithClass(this, RegionTag.class);
        if(regionTag == null)
            throw new JspException("No RegionTag ancestor");
        
        regionTag.put(new Section(section, getActualContent(), isDirect(), regionFile));
        return SKIP_BODY;
    }
    
    public String isDirect() {
        if(hasBody())
            return "true";
        else
            return direct == null ? "false" : "true";
    }
    
    public void release() {
        super.release();
        section = content = direct = role = null;
    }
    
    private String getActualContent() throws JspException {
        String bodyAndContentMismatchError =
                "Please specify template content in this tag's body " +
                "or with the content attribute, but not both.";
        String bodyAndDirectMismatchError =
                "If content is specified in the tag body, the " +
                "direct attribute must be true.";
        
        boolean hasBody = hasBody();
        boolean contentSpecified = (content != null);
        
        if((hasBody && contentSpecified) || (!hasBody && !contentSpecified))
            throw new JspException(bodyAndContentMismatchError);
        
        if(hasBody && direct != null && direct.equalsIgnoreCase("false"))
            throw new JspException(bodyAndDirectMismatchError);
        
        return hasBody ? bodyContent.getString() : content;
    }
    
    private boolean hasBody() {
        if (bodyContent == null)
            return (false);
        
        return !bodyContent.getString().equals("");
    }
}
