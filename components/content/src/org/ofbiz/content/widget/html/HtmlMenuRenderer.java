/*
 * $Id: HtmlMenuRenderer.java,v 1.7 2004/04/13 04:56:14 byersa Exp $
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.content.ContentPermissionServices;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.content.webapp.taglib.ContentUrlTag;
import org.ofbiz.content.widget.menu.MenuStringRenderer;
import org.ofbiz.content.widget.menu.ModelMenu;
import org.ofbiz.content.widget.menu.ModelMenuItem;
import org.ofbiz.content.widget.menu.ModelMenuItem.MenuTarget;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;

/**
 * Widget Library - HTML Menu Renderer implementation
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.7 $
 * @since      2.2
 */
public class HtmlMenuRenderer implements MenuStringRenderer {

    HttpServletRequest request;
    HttpServletResponse response;
    protected String userLoginIdAtPermGrant;
    protected boolean userLoginIdHasChanged = true;
    protected String permissionErrorMessage = "";

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
        if (ctx == null) {
            //if (Debug.infoOn()) Debug.logInfo("in appendOfbizUrl, ctx is null(0): buffer=" + buffer.toString() + " location:" + location, "");
            HttpSession session = request.getSession();
            if (session != null) {
                ctx = session.getServletContext();
            } else {
                if (Debug.infoOn()) Debug.logInfo("in appendOfbizUrl, session is null(1)", "");
            }
            if (ctx == null) {
                throw new RuntimeException("ctx is null. buffer=" + buffer.toString() + " location:" + location);
            }
                //if (Debug.infoOn()) Debug.logInfo("in appendOfbizUrl, ctx is NOT null(2)", "");
        }
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        if (delegator == null) {
                //if (Debug.infoOn()) Debug.logInfo("in appendOfbizUrl, delegator is null(5)", "");
        }
        RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
        // make and append the link
        String s = rh.makeLink(this.request, this.response, location);
            if (s.indexOf("null") >= 0) {
                if (Debug.infoOn()) Debug.logInfo("in appendOfbizUrl(3), url: " + s, "");
            }
        buffer.append(s);
    }

    public void appendContentUrl(StringBuffer buffer, String location) {
        ServletContext ctx = (ServletContext) this.request.getAttribute("servletContext");
        if (ctx == null) {
            //if (Debug.infoOn()) Debug.logInfo("in appendContentUrl, ctx is null(0): buffer=" + buffer.toString() + " location:" + location, "");
            HttpSession session = request.getSession();
            if (session != null) {
                ctx = session.getServletContext();
            } else {
                //if (Debug.infoOn()) Debug.logInfo("in appendContentUrl, session is null(1)", "");
            }
            if (ctx == null) {
                throw new RuntimeException("ctx is null. buffer=" + buffer.toString() + " location:" + location);
            }
                //if (Debug.infoOn()) Debug.logInfo("in appendContentUrl, ctx is NOT null(2)", "");
            this.request.setAttribute("servletContext", ctx);
        }
        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        if (delegator == null) {
                //if (Debug.infoOn()) Debug.logInfo("in appendContentUrl, delegator is null(6)", "");
        }
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

    public void renderFormatSimpleWrapperRows(StringBuffer buffer, Map context, Object menuObj) {

        List menuItemList = ((ModelMenu)menuObj).getMenuItemList();
        Iterator menuItemIter = menuItemList.iterator();
        ModelMenuItem currentMenuItem = null;

        while (menuItemIter.hasNext()) {
            currentMenuItem = (ModelMenuItem)menuItemIter.next();
            renderMenuItem(buffer, context, currentMenuItem);
        }
        return;
    }

    public void renderMenuItem(StringBuffer buffer, Map context, ModelMenuItem menuItem) {
        
            Debug.logInfo("in renderMenuItem, menuItem:" + menuItem.getName() + " context:" + context ,"");
        boolean hideThisItem = isHideIfSelected(menuItem);
            //if (Debug.infoOn()) Debug.logInfo("in HtmlMenuRendererImage, hideThisItem:" + hideThisItem,"");
        if (hideThisItem)
            return;

        boolean bHasPermission = permissionCheck(menuItem, context);
        if (!bHasPermission) {
            if (!permissionErrorMessage.equalsIgnoreCase("SKIP")) {
                //buffer.append(permissionErrorMessage);
            }
            permissionErrorMessage = "";
            return;
        }
        if (Debug.infoOn()) Debug.logInfo("in HtmlMenuRendererImage, bHasPermission(2):" + bHasPermission,"");

        String orientation = menuItem.getModelMenu().getOrientation();
        if (orientation.equalsIgnoreCase("vertical"))
            buffer.append("<tr>");
        String cellWidth = menuItem.getCellWidth();
        String widthStr = "";
        if (UtilValidate.isNotEmpty(cellWidth)) 
            widthStr = " width=\"" + cellWidth + "\" ";
        
        buffer.append("<td " + widthStr + ">");
        MenuTarget target = selectMenuTarget(menuItem, context);
        if (Debug.infoOn()) Debug.logInfo("in HtmlMenuRendererImage, target(0):" + target.getMenuTargetName(),"");
        if (target != null) {
            String divStr = buildDivStr(menuItem, context);
            String url = target.renderAsUrl( context);
            String titleStyle = menuItem.getTitleStyle();
            buffer.append("<a  class=\"" + titleStyle + "\" href=\""); 
            appendOfbizUrl(buffer,  url);
            buffer.append("\">" + divStr + "</a>");
            buffer.append("</td>");
            if (orientation.equalsIgnoreCase("vertical"))
                buffer.append("</tr>");
            this.appendWhitespace(buffer);
        }
        return;
    }

    public MenuTarget selectMenuTarget(ModelMenuItem menuItem, Map context) {
   
        MenuTarget menuTarget = null;
        String currentMenuTargetName = menuItem.getCurrentMenuTargetName();
        Map targetMap = menuItem.getMenuTargetMap();
        if (UtilValidate.isNotEmpty(currentMenuTargetName)) {
            menuTarget = (MenuTarget)targetMap.get(currentMenuTargetName);
            if (menuTarget != null) {
                String resultMsg = doMenuTargetPermissionCheck(menuItem, menuTarget, context);
                if (UtilValidate.isNotEmpty(resultMsg)) 
                    menuTarget = null;
            }
            if (Debug.infoOn()) Debug.logInfo("in selectMenuTarget menuItemName:" + menuItem.getName() + " currentMenuTargetName:" + currentMenuTargetName + ", target(0):" + menuTarget,"");
        }
 
        if (menuTarget == null) {
            String defaultMenuTargetName = menuItem.getDefaultMenuTargetName();
            if (UtilValidate.isNotEmpty(defaultMenuTargetName)) {
                menuTarget = (MenuTarget)targetMap.get(defaultMenuTargetName);
                if (menuTarget != null) {
                    String resultMsg = doMenuTargetPermissionCheck(menuItem, menuTarget, context);
                    if (UtilValidate.isNotEmpty(resultMsg)) 
                        menuTarget = null;
                }
            }
            if (Debug.infoOn()) Debug.logInfo("in selectMenuTarget menuItemName:" + menuItem.getName() + " defaultMenuTargetName:" + defaultMenuTargetName + ", target(1):" + menuTarget,"");
        }
 
        if (menuTarget == null) {
            List targetList = menuItem.getMenuTargetList();
            Iterator iter = targetList.iterator();
            while (iter.hasNext()) {
                menuTarget = (MenuTarget)iter.next();
                if (menuTarget != null) {
                    String resultMsg = doMenuTargetPermissionCheck(menuItem, menuTarget, context);
                    if (UtilValidate.isEmpty(resultMsg)) {
                        if (Debug.infoOn()) Debug.logInfo("in selectMenuTarget menuTarget:" + menuTarget.getMenuTargetName(),"");
                        break;
                    }
                }
            }
        }

        return menuTarget;
    }

    public String buildDivStr(ModelMenuItem menuItem, Map context) {
        String divStr = "";
        divStr =  menuItem.getTitle(context);
        return divStr;
    }

    public void renderMenuOpen(StringBuffer buffer, Map context, ModelMenu modelMenu) {

        if (!userLoginIdHasChanged)
            userLoginIdHasChanged = userLoginIdHasChanged(); 

            Debug.logInfo("in HtmlMenuRenderer, userLoginIdHasChanged:" + userLoginIdHasChanged,"");
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
        
        userLoginIdHasChanged = userLoginIdHasChanged(); 
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        if (userLogin != null) {
            String userLoginId = userLogin.getString("userLoginId");
            //request.getSession().setAttribute("userLoginIdAtPermGrant", userLoginId);
            setUserLoginIdAtPermGrant(userLoginId);
            //Debug.logInfo("in HtmlMenuRenderer, userLoginId(Close):" + userLoginId + " userLoginIdAtPermGrant:" + request.getSession().getAttribute("userLoginIdAtPermGrant"),"");
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
            //Debug.logInfo("in HtmlMenuRenderer,  userLoginIdAtPermGrant(setUserLoginIdAtPermGrant):" + string,"");
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
            //Debug.logInfo("in HtmlMenuRenderer, currentMenuItemName:" + currentMenuItemName + " currentItemName:" + currentItemName + " hideIfSelected:" + hideIfSelected,"");
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
            String sHasPermission = doMenuItemPermissionCheck(menuItem, context);
            if (UtilValidate.isEmpty(sHasPermission)) {
                bHasPermission = true;
            } else {
                permissionErrorMessage += sHasPermission;
            }
            menuItem.setHasPermission(new Boolean(bHasPermission));
        } else {
            bHasPermission = hasPerm.booleanValue();
        }
        return bHasPermission;
    }

    public boolean userLoginIdHasChanged() {

        boolean hasChanged = false;
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        userLoginIdAtPermGrant = getUserLoginIdAtPermGrant();
        //userLoginIdAtPermGrant = (String)request.getSession().getAttribute("userLoginIdAtPermGrant");
        String userLoginId = null;
        if (userLogin != null)
            userLoginId = userLogin.getString("userLoginId");
            //Debug.logInfo("in HtmlMenuRenderer, userLoginId:" + userLoginId + " userLoginIdAtPermGrant:" + userLoginIdAtPermGrant ,"");
        if ((userLoginId == null && userLoginIdAtPermGrant != null)
           || (userLoginId != null && userLoginIdAtPermGrant == null)
           || ((userLoginId != null && userLoginIdAtPermGrant != null)
              && !userLoginId.equals(userLoginIdAtPermGrant))) {
            hasChanged = true;
        } else {
            if (userLoginIdAtPermGrant != null)
               hasChanged = true;
            else
               hasChanged = false;

            userLoginIdAtPermGrant = null;
        }
        return hasChanged;
    }

    public String doMenuTargetPermissionCheck(ModelMenuItem menuItem, MenuTarget menuTarget, Map context) {

        String permissionOperation = menuTarget.getPermissionOperation();
        Debug.logInfo("in doMenuTargetPermissionCheck, menuItem:" + menuItem.getName() + " permissionOperation:" + permissionOperation,"");
        if (UtilValidate.isEmpty(permissionOperation)) 
            return "";
        String associatedContentId = menuItem.getAssociatedContentId(context);
        String entityAction = menuTarget.getPermissionEntityAction();
        String privilegeEnumId = menuTarget.getPrivilegeEnumId();
        String permissionStatusId = menuTarget.getPermissionStatusId();
        String b = doPermissionCheck(associatedContentId, permissionOperation, entityAction, privilegeEnumId, permissionStatusId);
        if (UtilValidate.isNotEmpty(b))
            b += b + ">>>> in doMenuTargetPermissionCheck, menuItem:" + menuItem.getName()+" <<<";
        return b;
    }

    public String doMenuItemPermissionCheck( ModelMenuItem menuItem, Map context) {
        String permissionOperation = menuItem.getPermissionOperation();
        Debug.logInfo("in doMenuItemPermissionCheck, menuItem:" + menuItem.getName() + " permissionOperation:" + permissionOperation,"");
        if (UtilValidate.isEmpty(permissionOperation)) 
            return "";
        String associatedContentId = menuItem.getAssociatedContentId(context);
        String entityAction = menuItem.getPermissionEntityAction();
        String privilegeEnumId = menuItem.getPrivilegeEnumId();
        String permissionStatusId = menuItem.getPermissionStatusId();
        String b = doPermissionCheck(associatedContentId, permissionOperation, entityAction, privilegeEnumId, permissionStatusId);
        Debug.logInfo("in doMenuItemPermChk, menuItemName:" + menuItem.getName() + " permissionOperation:" + permissionOperation + " associatedContentId:" + associatedContentId + " entityAction:" + entityAction + " privilegeEnumId:" + privilegeEnumId + " permissionStatusId:" + permissionStatusId,"");
        if (UtilValidate.isNotEmpty(b))
            b += b + ">>>> in doMenuItemPermissionCheck, menuItem:" + menuItem.getName() +"<<<";
        return b;
    }

    public String doPermissionCheck( String associatedContentId, String permissionOperation, String entityAction, String privilegeEnumId, String permissionStatusId) {

        GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
        GenericValue content = null;
        String contentId = null;
        try {
            content = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", associatedContentId));
        } catch(GenericEntityException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (content != null)
            contentId = content.getString("contentId");
                //Debug.logInfo("in HtmlMenuRenderer, contentId:" + contentId,"");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Security security = (Security) request.getAttribute("security");
        List targetOperations = StringUtil.split(permissionOperation, "|");
        List passedPurposes = null;
        List passedRoles = null;

        //Debug.logInfo("in doPermissionCheck, content:" + content,"");
        Debug.logInfo("in doPermissionCheck, targetOperations:" + targetOperations,"");
        Map results = ContentPermissionServices.checkPermission(content, permissionStatusId, userLogin, passedPurposes, targetOperations, passedRoles, delegator , security, entityAction, privilegeEnumId );
        String permissionStatus = (String)results.get("permissionStatus");
                Debug.logInfo("in doPermissionCheck, permissionStatus:" + permissionStatus,"");
                //Debug.logInfo("in HtmlMenuRenderer, results:" + results,"");
        String errorMessage = null;
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            return "";
        } else {
            errorMessage = ContentWorker.prepPermissionErrorMsg(results);
                //Debug.logInfo("in doPermissionCheck, errorMessage:" + errorMessage,"");
            return errorMessage;
        }

    }

    public void setUserLoginIdHasChanged(boolean b) {
        userLoginIdHasChanged = b;
    }
}

