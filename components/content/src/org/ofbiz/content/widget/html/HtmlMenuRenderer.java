/*
 * $Id: HtmlMenuRenderer.java,v 1.1 2004/03/15 14:53:57 byersa Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 */
package org.ofbiz.content.widget.html;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.security.Security;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.content.webapp.taglib.ContentUrlTag;
import org.ofbiz.content.widget.menu.MenuStringRenderer;
import org.ofbiz.content.widget.menu.ModelMenu;
import org.ofbiz.content.widget.menu.ModelMenuItem;
import org.ofbiz.content.widget.menu.ModelMenuItem.MenuTarget;
import org.ofbiz.content.content.ContentPermissionServices;

/**
 * Widget Library - HTML Menu Renderer implementation
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.1 $
 * @since      2.2
 */
public class HtmlMenuRenderer implements MenuStringRenderer {

    HttpServletRequest request;
    HttpServletResponse response;
    protected String userLoginIdAtPermGrant;
    protected boolean userLoginIdHasChanged = true;

    protected HtmlMenuRenderer() {}

    public HtmlMenuRenderer(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public void appendWhitespace(StringBuffer buffer) {
        // appending line ends for now, but this could be replaced with a simple space or something
        buffer.append("\r\n");
        //buffer.append(' ');
    }

    public void appendOfbizUrl(StringBuffer buffer, String location) {
        ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
        // make and append the link
        buffer.append(rh.makeLink(this.request, this.response, location));
    }

    public void appendContentUrl(StringBuffer buffer, String location) {
        ContentUrlTag.appendContentPrefix(this.request, buffer);
        buffer.append(location);
    }

    public void appendTooltip(StringBuffer buffer, Map context, ModelMenuItem modelMenuItem) {
        // render the tooltip, in other methods too
        String tooltip = modelMenuItem.getTooltip(context);
        if (UtilValidate.isNotEmpty(tooltip)) {
            buffer.append("<span");
            String tooltipStyle = modelMenuItem.getTooltipStyle();
            if (UtilValidate.isNotEmpty(tooltipStyle)) {
                buffer.append(" class=\"");
                buffer.append(tooltipStyle);
                buffer.append("\"");
            }
            buffer.append("> -[");
            buffer.append(tooltip);
            buffer.append("]- </span>");
        }
    }

    public void renderMenuItem(StringBuffer buffer, Map context, ModelMenuItem menuItem) {
        
        boolean hideThisItem = isHideIfSelected(menuItem);
            Debug.logInfo("in HtmlMenuRendererImage, hideThisItem:" + hideThisItem,"");
        if (hideThisItem)
            return;

        boolean bHasPermission = permissionCheck(menuItem, context);
        if (!bHasPermission) 
            return;
            Debug.logInfo("in HtmlMenuRendererImage, bHasPermission(2):" + bHasPermission,"");

        String orientation = menuItem.getModelMenu().getOrientation();
        if (orientation.equalsIgnoreCase("vertical"))
            buffer.append("<tr>");
        String cellWidth = menuItem.getCellWidth();
        String widthStr = "";
        if (UtilValidate.isNotEmpty(cellWidth)) 
            widthStr = " width=\"" + cellWidth + "\" ";
        
        buffer.append("<td " + widthStr + ">");
        MenuTarget target = menuItem.getCurrentMenuTarget();
        String divStr = buildDivStr(menuItem, context);
        String url = target.renderAsUrl( context);
        buffer.append("<a href=\""); 
        appendOfbizUrl(buffer,  url);
        buffer.append("\">" + divStr + "</a>");
        buffer.append("</td>");
        if (orientation.equalsIgnoreCase("vertical"))
            buffer.append("</tr>");
        this.appendWhitespace(buffer);
        return;
    }

    public String buildDivStr(ModelMenuItem menuItem, Map context) {
        String divStr = "";
        String titleStyle = menuItem.getTitleStyle();
        divStr = "<div class='" + titleStyle + "'>" + menuItem.getTitle(context) + "</div>";
        return divStr;
    }

    public void renderMenuOpen(StringBuffer buffer, Map context, ModelMenu modelMenu) {

        userLoginIdHasChanged = userLoginIdHasChanged(); 
        String menuWidth = modelMenu.getMenuWidth();
        String widthStr = "";
        if (UtilValidate.isNotEmpty(menuWidth)) 
            widthStr = " width=\"" + menuWidth + "\" ";
        
        buffer.append("<table border=\"0\" " + widthStr + "> ");
        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.menu.MenuStringRenderer#renderMenuClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.menu.ModelMenu)
     */
    public void renderMenuClose(StringBuffer buffer, Map context, ModelMenu modelMenu) {
        buffer.append("</table> ");
        this.appendWhitespace(buffer);
        
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        if (userLogin != null) {
            String userLoginId = userLogin.getString("userLoginId");
            request.getSession().setAttribute("userLoginIdAtPermGrant", userLoginId);
        } else {
            request.getSession().setAttribute("userLoginIdAtPermGrant", null);
        }
    }

    public void renderFormatSimpleWrapperOpen(StringBuffer buffer, Map context, ModelMenu modelMenu) {
        String orientation = modelMenu.getOrientation();
        if (orientation.equalsIgnoreCase("horizontal"))
            buffer.append("<tr>");

        this.appendWhitespace(buffer);
    }

    public void renderFormatSimpleWrapperClose(StringBuffer buffer, Map context, ModelMenu modelMenu) {
        String orientation = modelMenu.getOrientation();
        if (orientation.equalsIgnoreCase("horizontal"))
            buffer.append("</tr>");
        this.appendWhitespace(buffer);
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }


    /**
     * @param string
     */
    public void setUserLoginIdAtPermGrant(String string) {
        this.userLoginIdAtPermGrant = string;
    }

    /**
     * @return
     */
    public String getUserLoginIdAtPermGrant() {
        return this.userLoginIdAtPermGrant;
    }

    public boolean isHideIfSelected( ModelMenuItem menuItem) {

        ModelMenu menu = menuItem.getModelMenu();
        String currentMenuItemName = menu.getCurrentMenuItemName();
        String currentItemName = menuItem.getName();
        Boolean hideIfSelected = menuItem.getHideIfSelected();
            Debug.logInfo("in HtmlMenuRenderer, currentMenuItemName:" + currentMenuItemName
                           + " currentItemName:" + currentItemName + " hideIfSelected:" + hideIfSelected,"");
        if (hideIfSelected != null && hideIfSelected.booleanValue() && currentMenuItemName != null && currentMenuItemName.equals(currentItemName)) 
            return true;
        else
            return false;
    }

    public boolean permissionCheck(ModelMenuItem menuItem, Map context) {
        // Permission is cached in each menuItem object, but it can change when the user
        // logs in, so when a change in userLogin is dedected, recalc permissions
        
        Boolean hasPerm = menuItem.getHasPermission();
        boolean bHasPermission = false;
        if (hasPerm == null || userLoginIdHasChanged) {
            bHasPermission = doPermissionCheck(menuItem, context);
            menuItem.setHasPermission(new Boolean(bHasPermission));
        } else {
            bHasPermission = hasPerm.booleanValue();
        }
        return bHasPermission;
    }

    public boolean userLoginIdHasChanged() {

        boolean hasChanged = false;
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        userLoginIdAtPermGrant = (String)request.getSession().getAttribute("userLoginIdAtPermGrant");
        String userLoginId = null;
        if (userLogin != null)
            userLoginId = userLogin.getString("userLoginId");
        Debug.logInfo("in HtmlMenuRenderer, userLoginId:" + userLoginId + " userLoginIdAtPermGrant:" + userLoginIdAtPermGrant ,"");
        if ((userLoginId == null && userLoginIdAtPermGrant != null)
           || (userLoginId != null && userLoginIdAtPermGrant == null)
           || ((userLoginId != null && userLoginIdAtPermGrant != null)
              && !userLoginId.equals(userLoginIdAtPermGrant))) {
            hasChanged = true;
        }
        return hasChanged;
    }

    public boolean doPermissionCheck( ModelMenuItem menuItem, Map context) {

        String permissionOperation = menuItem.getPermissionOperation();
            Debug.logInfo("in HtmlMenuRenderer, permissionOperation:" + permissionOperation,"");
        if (UtilValidate.isEmpty(permissionOperation)) 
            return true;

        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        String associatedContentId = menuItem.getAssociatedContentId(context);
        GenericValue content = null;
        try {
            content = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", associatedContentId));
        } catch(GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
        }
        String contentId = content.getString("contentId");
            Debug.logInfo("in HtmlMenuRenderer, contentId:" + contentId,"");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Security security = (Security) request.getAttribute("security");
        List targetOperations = UtilMisc.toList(permissionOperation);
        String entityAction = menuItem.getPermissionEntityAction();
        String privilegeEnumId = menuItem.getPrivilegeEnumId();
        String permissionStatusId = menuItem.getPermissionStatusId();
        List passedPurposes = null;
        List passedRoles = null;

            Debug.logInfo("in HtmlMenuRenderer, contentId:" + contentId
                    + " content:" + content
                    + " permissionStatusId:" + permissionStatusId
                    + " userLogin:" + userLogin
                    + " passedPurposes:" + passedPurposes
                    + " targetOperations:" + targetOperations
                    + " passedRoles:" + passedRoles
                    + " security:" + security
                    + " entityAction:" + entityAction
              ,"");
        Map results = ContentPermissionServices.checkPermission(content, permissionStatusId, userLogin, passedPurposes, targetOperations, passedRoles, delegator , security, entityAction, privilegeEnumId );
        String permissionStatus = (String)results.get("permissionStatus");
            Debug.logInfo("in HtmlMenuRenderer, permissionStatus:" + permissionStatus,"");
            Debug.logInfo("in HtmlMenuRenderer, results:" + results,"");
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted"))
            return true;
        else
            return false;

    }

}

