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
import org.ofbiz.core.util.*;


/**
 * Tag to put a section in a region
 *
 *@author     David M. Geary in the book "Advanced Java Server Pages"
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 26, 2002
 *@version    1.0
 */
public class PutTag extends BodyTagSupport {
    private String section, info, role, permission, action, content, type = null;

    public void setSection(String section) {
        this.section = section;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String cntnt) {
        this.content = cntnt;
    }

    public String getSection() {
        return section;
    }

    public String getInfo() {
        return info;
    }

    public String getRole() {
        return role;
    }

    public String getPermission() {
        return permission;
    }

    public String getAction() {
        return action;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public int doAfterBody() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        URL regionFile = null;

        try {
            regionFile = pageContext.getServletContext().getResource(SiteDefs.REGIONS_CONFIG_LOCATION);
        } catch (java.net.MalformedURLException e) {
            throw new IllegalArgumentException("regions.xml file URL invalid: " + e.getMessage());
        }

        if (role != null && !request.isUserInRole(role))
            return EVAL_PAGE;

        RegionTag regionTag = (RegionTag) findAncestorWithClass(this, RegionTag.class);

        if (regionTag == null)
            throw new JspException("No RegionTag ancestor");

        regionTag.put(new Section(section, info, getActualContent(), getRealType(), regionFile));
        return SKIP_BODY;
    }

    public String getRealType() {
        if (hasBody()) {
            return "direct";
        } else {
            if (type == null) return "default";
            else return type;
        }
    }

    public void release() {
        super.release();
        section = info = content = type = permission = role = action = null;
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

        if ((hasBody && contentSpecified) || (!hasBody && !contentSpecified))
            throw new JspException(bodyAndContentMismatchError);

        if (hasBody && type != null && type.equalsIgnoreCase("false"))
            throw new JspException(bodyAndDirectMismatchError);

        return hasBody ? bodyContent.getString() : content;
    }

    private boolean hasBody() {
        if (bodyContent == null)
            return (false);

        return !bodyContent.getString().equals("");
    }
}
