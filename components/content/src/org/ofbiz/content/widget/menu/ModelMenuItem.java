/*
 * $Id$
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
package org.ofbiz.content.widget.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.content.content.EntityPermissionChecker;
import org.w3c.dom.Element;

/**
 * Widget Library - Form model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev:$
 * @since      2.2
 */
public class ModelMenuItem {

    public static final String module = ModelMenuItem.class.getName();

    protected ModelMenu modelMenu;

    protected Map dataMap = new HashMap();
    protected String name;
    protected String entityName;
    protected FlexibleStringExpander title;
    protected FlexibleStringExpander tooltip;
    protected String titleStyle;
    protected String disabledTitleStyle;
    protected String widgetStyle;
    protected String tooltipStyle;
    protected String selectedStyle;
    protected Integer position = null;

    protected Map targetMap = new HashMap();
    protected List targetList = new ArrayList();
    protected String defaultMenuTargetName;
    protected String currentMenuTargetName;
    protected String permissionOperation;
    protected String permissionStatusId;
    protected String permissionEntityAction;
    protected String privilegeEnumId;
    protected FlexibleStringExpander associatedContentId;
    protected String cellWidth;
    protected Boolean hideIfSelected;
    protected Boolean hasPermission;
    protected MenuImage menuImage;
    protected String disableIfEmpty;
    protected ModelMenu subMenu;
    protected Link link;

    public static String DEFAULT_TARGET_TYPE = "intra-app";
    
    protected EntityPermissionChecker permissionChecker;
    // ===== CONSTRUCTORS =====
    /** Default Constructor */
    public ModelMenuItem(ModelMenu modelMenu) {
        this.modelMenu = modelMenu;
    }

    /** XML Constructor */
    public ModelMenuItem(Element fieldElement, ModelMenu modelMenu) {
        this.modelMenu = modelMenu;
        this.name = fieldElement.getAttribute("name");
        this.entityName = fieldElement.getAttribute("entity-name");
        this.setTitle(fieldElement.getAttribute("title"));
        this.setTooltip(fieldElement.getAttribute("tooltip"));
        this.titleStyle = fieldElement.getAttribute("title-style");
        this.disabledTitleStyle = fieldElement.getAttribute("disabled-title-style");
        this.widgetStyle = fieldElement.getAttribute("widget-style");
        this.tooltipStyle = fieldElement.getAttribute("tooltip-style");
        this.selectedStyle = fieldElement.getAttribute("selected-style");
        this.defaultMenuTargetName = fieldElement.getAttribute("default-target-name");
        this.setHideIfSelected(fieldElement.getAttribute("hide-if-selected"));
        this.disableIfEmpty = fieldElement.getAttribute("disable-if-empty");

        String positionStr = fieldElement.getAttribute("position");
        try {
            if (positionStr != null && positionStr.length() > 0) {
                position = Integer.valueOf(positionStr);
            }
        } catch (Exception e) {
            Debug.logError(
                e,
                "Could not convert position attribute of the field element to an integer: [" + positionStr + "], using the default of the menu renderer",
                module);
        }

        this.permissionOperation = fieldElement.getAttribute("permission-operation");
        this.permissionStatusId = fieldElement.getAttribute("permission-status-id");
        this.permissionEntityAction = fieldElement.getAttribute("permission-entity-action");
        this.setAssociatedContentId( fieldElement.getAttribute("associated-content-id"));
        this.cellWidth = fieldElement.getAttribute("cell-width");
        this.privilegeEnumId = fieldElement.getAttribute("privilege-enum-id");

        dataMap.put("name", this.name);
        //dataMap.put("associatedContentId", this.associatedContentId);

        // read in add target defs, add/override one by one using the targetList and targetMap
        List targetElements = UtilXml.childElementList(fieldElement, "target");
        Iterator targetElementIter = targetElements.iterator();
        while (targetElementIter.hasNext()) {
            Element targetElement = (Element) targetElementIter.next();
            MenuTarget target = new MenuTarget(targetElement, this);
            this.addUpdateMenuTarget(target);
            //Debug.logInfo("Added target " + modelMenuItem.getName() + " from def, mapName=" + modelMenuItem.getMapName(), module);
        }
        List imgElements = UtilXml.childElementList(fieldElement, "img");
        Iterator imgElementIter = imgElements.iterator();
        while (imgElementIter.hasNext()) {
            Element imgElement = (Element) imgElementIter.next();
            menuImage = new MenuImage(imgElement);
            if (Debug.infoOn()) Debug.logInfo("in new ModelMenuItem, menuImage:" + menuImage, module);
        }
        Element subMenuElement = UtilXml.firstChildElement(fieldElement, "sub-menu");
        if (subMenuElement != null) {
            String subMenuLocation = subMenuElement.getAttribute("location");
            String subMenuName = subMenuElement.getAttribute("name");
            try {
                this.subMenu = MenuFactory.getMenuFromLocation(subMenuLocation, subMenuName, modelMenu.getDelegator(), modelMenu.getDispacher());
            } catch (IOException e) {
                String errMsg = "Error getting subMenu in menu named [" + this.modelMenu.getName() + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch (SAXException e2) {
                String errMsg = "Error getting subMenu in menu named [" + this.modelMenu.getName() + "]: " + e2.toString();
                Debug.logError(e2, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch (ParserConfigurationException e3) {
                String errMsg = "Error getting subMenu in menu named [" + this.modelMenu.getName() + "]: " + e3.toString();
                Debug.logError(e3, errMsg, module);
                throw new RuntimeException(errMsg);
            }
        }
        Element linkElement = UtilXml.firstChildElement(fieldElement, "link");
        if (Debug.infoOn()) Debug.logInfo("in ModelMenuItem, linkElement:" + linkElement, module);
        if (linkElement != null) {
            link = new Link(linkElement, this);
        }
        Element permissionElement = UtilXml.firstChildElement(fieldElement, "if-entity-permission");
        if (permissionElement != null)
            permissionChecker = new EntityPermissionChecker(permissionElement);

    }

    public void setHideIfSelected(String val) {
        if (UtilValidate.isNotEmpty(val))
            if (val.equalsIgnoreCase("true"))
                hideIfSelected = new Boolean(true);
            else
                hideIfSelected = new Boolean(false);
        else
            hideIfSelected = null;

        return;
    }


    /**
     * add/override modelMenuItem using the targetList and targetMap
     *
     * @return The same ModelMenuItem, or if merged with an existing target, the existing target.
     */
    public void addUpdateMenuTarget(MenuTarget target) {

            // not a conditional target, see if a named target exists in Map
            MenuTarget existingMenuTarget = (MenuTarget) this.targetMap.get(target.getMenuTargetName());
            if (existingMenuTarget != null) {
                // does exist, update the target by doing a merge/override
                //existingMenuTarget.mergeOverrideMenuTarget(target);
            } else {
                // does not exist, add to List and Map
                this.targetList.add(target);
                this.targetMap.put(target.getMenuTargetName(), target);
            }
    }

    public void mergeOverrideModelMenuItem(ModelMenuItem overrideModelMenuItem) {
        if (overrideModelMenuItem == null)
            return;
/*
        // incorporate updates for values that are not empty in the overrideMenuItem
        if (UtilValidate.isNotEmpty(overrideMenuItem.name))
            this.name = overrideMenuItem.name;
        if (UtilValidate.isNotEmpty(overrideMenuItem.entityName))
            this.entityName = overrideMenuItem.entityName;
        if (overrideMenuItem.entryAcsr != null && !overrideMenuItem.entryAcsr.isEmpty())
            this.entryAcsr = overrideMenuItem.entryAcsr;
        if (UtilValidate.isNotEmpty(overrideMenuItem.attributeName))
            this.attributeName = overrideMenuItem.attributeName;
        if (overrideMenuItem.title != null && !overrideMenuItem.title.isEmpty())
            this.title = overrideMenuItem.title;
        if (overrideMenuItem.tooltip != null && !overrideMenuItem.tooltip.isEmpty())
            this.tooltip = overrideMenuItem.tooltip;
        if (UtilValidate.isNotEmpty(overrideMenuItem.titleStyle))
            this.titleStyle = overrideMenuItem.titleStyle;
        if (UtilValidate.isNotEmpty(overrideMenuItem.selectedStyle))
            this.selectedStyle = overrideMenuItem.selectedStyle;
        if (UtilValidate.isNotEmpty(overrideMenuItem.widgetStyle))
            this.widgetStyle = overrideMenuItem.widgetStyle;
        if (overrideMenuItem.position != null)
            this.position = overrideMenuItem.position;
*/
        return;
    }

    public void renderMenuItemString(StringBuffer buffer, Map context, MenuStringRenderer menuStringRenderer) {
      	boolean passed = true;
    	if (permissionChecker != null)
    		passed = permissionChecker.runPermissionCheck(context);

            Debug.logInfo("in ModelMenu, name:" + this.getName(), module);
        if (passed) {
            menuStringRenderer.renderMenuItem(buffer, context, this);
        }
    }


    /**
     * @return
     */
    public ModelMenu getModelMenu() {
        return modelMenu;
    }


    /**
     * @return
     */
    public String getEntityName() {
        if (UtilValidate.isNotEmpty(this.entityName)) {
            return this.entityName;
        } else {
            return this.modelMenu.getDefaultEntityName();
        }
    }


    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public int getPosition() {
        if (this.position == null) {
            return 1;
        } else {
            return position.intValue();
        }
    }

    /**
     * @return
     */
    public String getTitle(Map context) {
            return title.expandString(context);
    }

    /**
     * @return
     */
    public String getTitleStyle() {
        if (UtilValidate.isNotEmpty(this.titleStyle)) {
            return this.titleStyle;
        } else {
            return this.modelMenu.getDefaultTitleStyle();
        }
    }

    /**
     * @return
     */
    public String getDisabledTitleStyle() {
        if (UtilValidate.isNotEmpty(this.disabledTitleStyle)) {
            return this.disabledTitleStyle;
        } else {
            return this.modelMenu.getDefaultDisabledTitleStyle();
        }
    }

    /**
     * @return
     */
    public String getSelectedStyle() {
        if (UtilValidate.isNotEmpty(this.selectedStyle)) {
            return this.selectedStyle;
        } else {
            return this.modelMenu.getDefaultSelectedStyle();
        }
    }

    /**
     * @return
     */
    public String getTooltip(Map context) {
        if (tooltip != null && !tooltip.isEmpty()) {
            return tooltip.expandString(context);
        } else {
            return "";
        }
    }


    /**
     * @return
     */
    public String getWidgetStyle() {
        if (UtilValidate.isNotEmpty(this.widgetStyle)) {
            return this.widgetStyle;
        } else {
            return this.modelMenu.getDefaultWidgetStyle();
        }
    }

    /**
     * @return
     */
    public String getTooltipStyle() {
        if (UtilValidate.isNotEmpty(this.tooltipStyle)) {
            return this.tooltipStyle;
        } else {
            return this.modelMenu.getDefaultTooltipStyle();
        }
    }

    /**
     * @param string
     */
    public void setEntityName(String string) {
        entityName = string;
    }

    /**
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

    /**
     * @param i
     */
    public void setPosition(int i) {
        position = new Integer(i);
    }


    /**
     * @param string
     */
    public void setTitle(String string) {
        this.title = new FlexibleStringExpander(string);
    }

    /**
     * @param string
     */
    public void setTitleStyle(String string) {
        this.titleStyle = string;
    }

    /**
     * @param string
     */
    public void setTooltip(String string) {
        this.tooltip = new FlexibleStringExpander(string);
    }


    /**
     * @param string
     */
    public void setWidgetStyle(String string) {
        this.widgetStyle = string;
    }

    /**
     * @param string
     */
    public void setTooltipStyle(String string) {
        this.tooltipStyle = string;
    }

    /**
     * @param string
     */
    public void setCurrentMenuTargetName(String target) {
        this.currentMenuTargetName = target;
        //if (Debug.infoOn()) Debug.logInfo("in ModelMenuItem, setCurrentMenuTargetItem:" + currentMenuTargetName, module);
    }

    public String getCurrentMenuTargetName() {
        //if (Debug.infoOn()) Debug.logInfo("in ModelMenuItem, getCurrentMenuTargetItem:" + currentMenuTargetName, module);
        return this.currentMenuTargetName;
    }

    public String getDefaultMenuTargetName() {
        return this.defaultMenuTargetName;
    }

    /**
     * @return
     */
    public MenuTarget getCurrentMenuTarget() {
        
        MenuTarget target = null;
        if (subMenu != null ) {
            ModelMenuItem subMenuItem = subMenu.getCurrentMenuItem();
            target = subMenuItem.getCurrentMenuTarget();
        } else {
            target = (MenuTarget)targetMap.get(currentMenuTargetName);
            //if (Debug.infoOn()) Debug.logInfo("in getCurrentMenuTarget, target: " + target + " targetMap:" + targetMap, module);
            if (target == null) {
                target = (MenuTarget)targetMap.get(defaultMenuTargetName);
            //if (Debug.infoOn()) Debug.logInfo("in getCurrentMenuTarget, target(2): " + target + " defaultMenuTargetName:" + defaultMenuTargetName, module);
                if (target == null) {
                   if (targetList.size() > 0) {
                       target = (MenuTarget)targetList.get(0);
            //if (Debug.infoOn()) Debug.logInfo("in getCurrentMenuTarget, target(3): " + target + " targetList:" + targetList, module);
                   } 
                }
            }
        }
        return target;
    }

    public Map getMenuTargetMap() {
        return this.targetMap;
    }

    public List getMenuTargetList() {
        return this.targetList;
    }

    /**
     * @param string
     */
    public void setAssociatedContentId(String string) {
        this.associatedContentId = new FlexibleStringExpander(string);
    }

    /**
     * @return
     */
    public String getAssociatedContentId(Map context) {
        String retStr = null;
        if (this.associatedContentId != null) {
            retStr = associatedContentId.expandString(context);
        }
        if (UtilValidate.isEmpty(retStr)) {
            retStr = this.modelMenu.getDefaultAssociatedContentId(context);
        }
        return retStr;
    }

    /**
     * @param string
     */
    public void setPermissionOperation(String string) {
        this.permissionOperation = string;
    }

    /**
     * @return
     */
    public String getPermissionOperation() {
        if (UtilValidate.isNotEmpty(this.permissionOperation )) {
            return this.permissionOperation ;
        } else {
            return this.modelMenu.getDefaultPermissionOperation ();
        }
    }

    /**
     * @param string
     */
    public void setPermissionStatusId(String string) {
        this.permissionStatusId = string;
    }

    /**
     * @return
     */
    public String getPermissionStatusId() {
        if (UtilValidate.isNotEmpty(this.permissionStatusId )) {
            return this.permissionStatusId ;
        } else {
            return this.modelMenu.getDefaultPermissionStatusId ();
        }
    }

    /**
     * @param string
     */
    public void setPrivilegeEnumId(String string) {
        this.privilegeEnumId = string;
    }

    /**
     * @return
     */
    public String getPrivilegeEnumId() {
        if (UtilValidate.isNotEmpty(this.privilegeEnumId )) {
            return this.privilegeEnumId ;
        } else {
            return this.modelMenu.getDefaultPrivilegeEnumId ();
        }
    }

    /**
     * @param string
     */
    public void setPermissionEntityAction(String string) {
        this.permissionEntityAction = string;
    }

    /**
     * @return
     */
    public String getPermissionEntityAction() {
        if (UtilValidate.isNotEmpty(this.permissionEntityAction )) {
            return this.permissionEntityAction ;
        } else {
            return this.modelMenu.getDefaultPermissionEntityAction ();
        }
    }

    /**
     * @param string
     */
    public void setCellWidth(String string) {
        this.cellWidth = string;
    }

    /**
     * @return
     */
    public String getCellWidth() {
        if (UtilValidate.isNotEmpty(this.cellWidth )) {
            return this.cellWidth ;
        } else {
            return this.modelMenu.getDefaultCellWidth ();
        }
    }

    /**
     * @param boolean
     */
    public void setHideIfSelected(Boolean val) {
        this.hideIfSelected = val;
    }

    /**
     * @return
     */
    public Boolean getHideIfSelected() {
        if (hideIfSelected != null) {
            return this.hideIfSelected;
        } else {
            return this.modelMenu.getDefaultHideIfSelected();
        }
    }

    /**
     * @return
     */
    public String getDisableIfEmpty() {
            return this.disableIfEmpty;
    }

    /**
     * @param boolean
     */
    public void setHasPermission(Boolean val) {
        this.hasPermission = val;
    }

    /**
     * @return
     */
    public Boolean getHasPermission() {
        return this.hasPermission;
    }

    public void dump(StringBuffer buffer ) {
        buffer.append("ModelMenuItem:" 
            + "\n     title=" + this.title
            + "\n     name=" + this.name
            + "\n     entityName=" + this.entityName
            + "\n     titleStyle=" + this.titleStyle
            + "\n     widgetStyle=" + this.widgetStyle
            + "\n     tooltipStyle=" + this.tooltipStyle
            + "\n     selectedStyle=" + this.selectedStyle
            + "\n     defaultMenuTargetName=" + this.defaultMenuTargetName
            + "\n     currentMenuTargetName=" + this.currentMenuTargetName
            + "\n\n");
     
        Iterator iter = targetList.iterator();
        while (iter.hasNext()) {
            MenuTarget item = (MenuTarget)iter.next();
            item.dump(buffer);
        }
            
        return;
    }
    public MenuImage getMenuImage() {
       return this.menuImage;
    }

    public Link getLink() {
       return this.link;
    }

    public class MenuTarget {

        protected String targetName;
        protected String targetTitle;
        protected String requestName;
        protected String requestType;
        protected String targetType;
        protected String permissionOperation;
        protected String permissionStatusId;
        protected String permissionEntityAction;
        protected String privilegeEnumId;
        protected MenuImage menuImage;
    
        protected Map paramMap = new HashMap();
        protected List paramList = new ArrayList();
        protected ModelMenuItem modelMenuItem;

        public MenuTarget() {
        }
    
        /** XML Constructor */
        public MenuTarget(Element fieldElement, ModelMenuItem item) {

            this.modelMenuItem = item;
            this.targetName = fieldElement.getAttribute("name");
            this.targetTitle = fieldElement.getAttribute("title");
            this.requestName = fieldElement.getAttribute("request-name");
            this.requestType = fieldElement.getAttribute("request-type");
            this.targetType = fieldElement.getAttribute("target-type");
            this.permissionOperation = fieldElement.getAttribute("permission-operation");
            this.permissionStatusId = fieldElement.getAttribute("permission-status-id");
            this.permissionEntityAction = fieldElement.getAttribute("permission-entity-action");
            this.privilegeEnumId = fieldElement.getAttribute("privilege-enum-id");
    
            // read in add param defs, add/override one by one using the paramList and paramMap
            List subElements = UtilXml.childElementList(fieldElement, "param");
            Iterator subElementIter = subElements.iterator();
            while (subElementIter.hasNext()) {
                Element subElement = (Element) subElementIter.next();
                MenuParam sub =  new MenuParam(subElement);
                this.addUpdateMenuParam(sub);
            }
            subElements = UtilXml.childElementList(fieldElement, "img");
            subElementIter = subElements.iterator();
            while (subElementIter.hasNext()) {
                Element subElement = (Element) subElementIter.next();
                menuImage = new MenuImage(subElement);
            }
    
        }

        /**
         * @param string
         */
        public void setPermissionOperation(String string) {
            this.permissionOperation = string;
        }
    
        /**
         * @return
         */
        public String getPermissionOperation() {
            if (UtilValidate.isNotEmpty(this.permissionOperation )) {
                return this.permissionOperation ;
            } else {
                return this.modelMenuItem.getPermissionOperation();
            }
        }
    
        /**
         * @param string
         */
        public void setPermissionStatusId(String string) {
            this.permissionStatusId = string;
        }
    
        /**
         * @return
         */
        public String getPermissionStatusId() {
            if (UtilValidate.isNotEmpty(this.permissionStatusId )) {
                return this.permissionStatusId ;
            } else {
                return this.modelMenuItem.getPermissionStatusId ();
            }
        }
    
        /**
         * @param string
         */
        public void setPrivilegeEnumId(String string) {
            this.privilegeEnumId = string;
        }
    
        /**
         * @return
         */
        public String getPrivilegeEnumId() {
            if (UtilValidate.isNotEmpty(this.privilegeEnumId )) {
                return this.privilegeEnumId ;
            } else {
                return this.modelMenuItem.getPrivilegeEnumId ();
            }
        }
    
        /**
         * @param string
         */
        public void setPermissionEntityAction(String string) {
            this.permissionEntityAction = string;
        }
    
        /**
         * @return
         */
        public String getPermissionEntityAction() {
            if (UtilValidate.isNotEmpty(this.permissionEntityAction )) {
                return this.permissionEntityAction ;
            } else {
                return this.modelMenuItem.getPermissionEntityAction ();
            }
        }

    public void dump(StringBuffer buffer ) {
        buffer.append("        MenuTarget:" 
            + "\n     targetName=" + this.targetName
            + "\n     targetTitle=" + this.targetTitle
            + "\n     requestName=" + this.requestName
            + "\n     requestType=" + this.requestType
            + "\n\n");
            
        return;
    }

        public void addUpdateMenuParam(MenuParam param) {

            this.paramList.add(param);
            this.paramMap.put(param.getName(), param);
        }

        public String getMenuTargetTitle() {
            return this.targetTitle;
        }

        public String getMenuTargetTitle(Map context) {
            if (UtilValidate.isNotEmpty(this.targetTitle)) {
                return this.targetTitle;
            } else {
                return modelMenuItem.getTitle(context);
            }
        }

        public String getRequestName() {
            return this.requestName;
        }

        public String getTargetType() {
            if (UtilValidate.isNotEmpty(this.targetType)) {
                return this.targetType;
            } else {
                return ModelMenuItem.DEFAULT_TARGET_TYPE;
            }
        }

        public MenuImage getMenuImage() {
            if (this.menuImage != null) 
                return this.menuImage;
            else
                return this.modelMenuItem.getMenuImage();
        }

        public List getParamList() {
            return this.paramList;
        }

        /**
         * @return
         */
        public String getMenuTargetName() {
                return targetName;
        }

    }


        public class MenuParam {
        
            protected MenuParamInfo menuParamInfo;
            protected String paramName;

            public MenuParam() {
            }

            public MenuParam(Element fieldElement) {
                paramName = fieldElement.getAttribute("name");
                String paramType = fieldElement.getAttribute("type");
                if (paramType != null && paramType.equals("map")) {
                    menuParamInfo = new MenuParamInfoMap(fieldElement, this);
                } else {
                    menuParamInfo = new MenuParamInfoNameValue(fieldElement, this);
                }
            }

            /**
             * @return
             */
            public String getName() {
                return this.paramName;
            }


           public Map getParamMap(Map context) {
               Map map = menuParamInfo.getParamMap(context);
               return map;
           }

        }

        public class MenuParamInfo {

            protected MenuParam menuParam;
            protected String infoName;
            protected FlexibleStringExpander paramValue;
            protected String defaultValue;

            public MenuParamInfo() {
            }
            public MenuParamInfo(Element fieldElement, MenuParam menuParam) {
                this.menuParam = menuParam;
                this.infoName = fieldElement.getAttribute("name");
                this.paramValue = setParamValue(fieldElement.getAttribute("value"));
                this.defaultValue = fieldElement.getAttribute("default-value");
            }

           public Map getParamMap(Map context) {
               Map map = new HashMap();
               return map;
           }

            /**
             * @return
             */
            public String getName() {
                return this.infoName;
            }

            /**
             * @return
             */
            public Object getValue(Map context) {

                String s = paramValue.expandString(context);

                if (UtilValidate.isEmpty(s))
                    s = paramValue.expandString(dataMap);

                if (UtilValidate.isEmpty(s))
                    s = this.defaultValue;

                return s;
            }

            /**
             * @param string
             */
            public FlexibleStringExpander setParamValue(String string) {
                this.paramValue = new FlexibleStringExpander(string);
                return this.paramValue;
            }



        }

        public class MenuParamInfoNameValue extends MenuParamInfo {
        
    
            public MenuParamInfoNameValue() {
            }
        
            /** XML Constructor */
            public MenuParamInfoNameValue(Element fieldElement, MenuParam menuParam) {
                super(fieldElement, menuParam);
            }
           public Map getParamMap(Map context) {
               Map map = new HashMap();
               map.put(infoName, (String)getValue(context));
               //if (Debug.infoOn()) Debug.logInfo("in getParamMap, context: " + context + " map:" + map, module);
               return map;
           }

        }

        public class MenuParamInfoMap extends MenuParamInfo {
    
            public MenuParamInfoMap() {
            }
        
            /** XML Constructor */
            public MenuParamInfoMap(Element fieldElement, MenuParam menuParam) {
                super(fieldElement, menuParam);
            }

           public Map getParamMap(Map context) {

               Map map = null;
               Object obj = getValue(context);
               if (obj != null && obj instanceof Map)
                   map = (Map)getValue(context);
               return map;
           }

        }


        public class MenuImage {

        protected FlexibleStringExpander requestName;
        protected FlexibleStringExpander disabledRequestName;
        protected String targetType;
        protected Map paramMap = new HashMap();
        protected List paramList = new ArrayList();
        protected ModelMenuItem modelMenuItem;


        public MenuImage(Element fieldElement) {

            setRequestName(fieldElement.getAttribute("request-name"));
            setDisabledRequestName(fieldElement.getAttribute("disabled-request-name"));
                Debug.logInfo("in new MenuImage, requestName:" + requestName.getOriginal(), module);
            this.targetType = fieldElement.getAttribute("target-type");

            // read in add param defs, add/override one by one using the paramList and paramMap
            List subElements = UtilXml.childElementList(fieldElement, "param");
            Iterator subElementIter = subElements.iterator();
            while (subElementIter.hasNext()) {
                Element subElement = (Element) subElementIter.next();
                MenuParam sub =  new MenuParam(subElement);
                this.addUpdateMenuParam(sub);
            }
    
        }

        /**
         * @param string
         */
        public void setRequestName(String string) {
            this.requestName = new FlexibleStringExpander(string);
        }

        /**
         * @param string
         */
        public void setDisabledRequestName(String string) {
            this.disabledRequestName = new FlexibleStringExpander(string);
        }

        public String getRequestName(Map context) {
            String s = requestName.expandString(context);
            return s;
        }

        public String getDisabledRequestName(Map context) {
            String s = disabledRequestName.expandString(context);
            return s;
        }

        public String getTargetType() {
            return this.targetType;
        }

        public void addUpdateMenuParam(MenuParam param) {

            this.paramList.add(param);
            this.paramMap.put(param.getName(), param);
        }

        public List getParamList() {
            return this.paramList;
        }


    }

        public static class Link {

            protected ModelMenuItem parentMenuItem;
            protected FlexibleStringExpander textExdr;
            protected FlexibleStringExpander idExdr;
            protected FlexibleStringExpander styleExdr;
            protected FlexibleStringExpander targetExdr;
            protected FlexibleStringExpander targetWindowExdr;
            protected FlexibleStringExpander prefixExdr;
            protected Image image;
            protected String urlMode = "ofbiz";
            protected boolean fullPath = false;
            protected boolean secure = false;
            protected boolean encode = false;
            
            public Link( Element linkElement, ModelMenuItem parentMenuItem) {
    
                this.parentMenuItem = parentMenuItem;
                setText(linkElement.getAttribute("text"));
                setId(linkElement.getAttribute("id"));
                setStyle(linkElement.getAttribute("style"));
                setTarget(linkElement.getAttribute("target"));
                setTargetWindow(linkElement.getAttribute("target-window"));
                setPrefix(linkElement.getAttribute("prefix"));
                setUrlMode(linkElement.getAttribute("url-mode"));
                setFullPath(linkElement.getAttribute("full-path"));
                setSecure(linkElement.getAttribute("secure"));
                setEncode(linkElement.getAttribute("encode"));
                Element imageElement = UtilXml.firstChildElement(linkElement, "image");
                if (imageElement != null) {
                    this.image = new Image(imageElement);
                }
    
            }
    
            public void renderLinkString(StringBuffer buffer, Map context, MenuStringRenderer menuStringRenderer) {
                menuStringRenderer.renderLink(buffer, context, this);
            }
            
            public String getText(Map context) {
                String txt =  this.textExdr.expandString(context);
                if (UtilValidate.isEmpty(txt))
                    txt = parentMenuItem.getTitle(context);
                return txt;
            }
            
            public String getId(Map context) {
                return this.idExdr.expandString(context);
            }
            
            public String getStyle(Map context) {
                return this.styleExdr.expandString(context);
            }
            
            public String getTarget(Map context) {
                return this.targetExdr.expandString(context);
            }
            
            public String getTargetWindow(Map context) {
                return this.targetWindowExdr.expandString(context);
            }
            
            public String getUrlMode() {
                return this.urlMode;
            }
            
            public String getPrefix(Map context) {
                return this.prefixExdr.expandString(context);
            }
            
            public boolean getFullPath() {
                return this.fullPath;
            }
            
            public boolean getSecure() {
                return this.secure;
            }
            
            public boolean getEncode() {
                return this.encode;
            }
            
            public Image getImage() {
                return this.image;
            }

            public void setText( String val ) {
                String textAttr = UtilFormatOut.checkNull(val);
                this.textExdr = new FlexibleStringExpander(textAttr);
            }
            public void setId( String val ) {
                this.idExdr = new FlexibleStringExpander(val);
            }
            public void setStyle( String val ) {
                this.styleExdr = new FlexibleStringExpander(val);
            }
            public void setTarget( String val ) {
                this.targetExdr = new FlexibleStringExpander(val);
            }
            public void setTargetWindow( String val ) {
                this.targetWindowExdr = new FlexibleStringExpander(val);
            }
            public void setPrefix( String val ) {
                this.prefixExdr = new FlexibleStringExpander(val);
            }
            public void setUrlMode( String val ) {
                if (UtilValidate.isEmpty(val))
                    this.urlMode = "ofbiz";
                else
                    this.urlMode = val;
            }
            public void setFullPath( String val ) {
                String sFullPath = val;
                if (sFullPath != null && sFullPath.equalsIgnoreCase("true"))
                    this.fullPath = true;
                else
                    this.fullPath = false;
            }

            public void setSecure( String val ) {
                String sSecure = val;
                if (sSecure != null && sSecure.equalsIgnoreCase("true"))
                    this.secure = true;
                else
                    this.secure = false;
            }

            public void setEncode( String val ) {
                String sEncode = val;
                if (sEncode != null && sEncode.equalsIgnoreCase("true"))
                    this.encode = true;
                else
                    this.encode = false;
            }
            public void setImage( Image img ) {
                this.image = img;
            }
                
        }

        public static class Image {

            protected FlexibleStringExpander srcExdr;
            protected FlexibleStringExpander idExdr;
            protected FlexibleStringExpander styleExdr;
            protected FlexibleStringExpander widthExdr;
            protected FlexibleStringExpander heightExdr;
            protected FlexibleStringExpander borderExdr;
            protected String urlMode;
            
            public Image( Element imageElement) {
    
                setSrc(imageElement.getAttribute("src"));
                setId(imageElement.getAttribute("id"));
                setStyle(imageElement.getAttribute("style"));
                setWidth(imageElement.getAttribute("width"));
                setHeight(imageElement.getAttribute("height"));
                setBorder(UtilFormatOut.checkEmpty(imageElement.getAttribute("border"), "0"));
                setUrlMode(UtilFormatOut.checkEmpty(imageElement.getAttribute("url-mode"), "content"));
    
            }
    
            public void renderImageString(StringBuffer buffer, Map context, MenuStringRenderer menuStringRenderer) {
                menuStringRenderer.renderImage(buffer, context, this);
            }
            
            public String getSrc(Map context) {
                return this.srcExdr.expandString(context);
            }
            
            public String getId(Map context) {
                return this.idExdr.expandString(context);
            }
            
            public String getStyle(Map context) {
                return this.styleExdr.expandString(context);
            }

            public String getWidth(Map context) {
                return this.widthExdr.expandString(context);
            }

            public String getHeight(Map context) {
                return this.heightExdr.expandString(context);
            }

            public String getBorder(Map context) {
                return this.borderExdr.expandString(context);
            }
            
            public String getUrlMode() {
                return this.urlMode;
            }
            
            public void setSrc( String val ) {
                String textAttr = UtilFormatOut.checkNull(val);
                this.srcExdr = new FlexibleStringExpander(textAttr);
            }
            public void setId( String val ) {
                this.idExdr = new FlexibleStringExpander(val);
            }
            public void setStyle( String val ) {
                this.styleExdr = new FlexibleStringExpander(val);
            }
            public void setWidth( String val ) {
                this.widthExdr = new FlexibleStringExpander(val);
            }
            public void setHeight( String val ) {
                this.heightExdr = new FlexibleStringExpander(val);
            }
            public void setBorder( String val ) {
                this.borderExdr = new FlexibleStringExpander(val);
            }
            public void setUrlMode( String val ) {
                if (UtilValidate.isEmpty(val))
                    this.urlMode = "content";
                else
                    this.urlMode = val;
            }
                
        }

    
}
