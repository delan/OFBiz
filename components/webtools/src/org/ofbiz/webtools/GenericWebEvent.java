/*
 * $Id: GenericWebEvent.java,v 1.5 2004/07/09 16:52:02 jonesde Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 */
package org.ofbiz.webtools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelFieldType;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.security.Security;

/**
 * Web Event for doing updates on Generic Entities
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.5 $
 * @since      2.0
 */
public class GenericWebEvent {
    
    public static final String module = GenericWebEvent.class.getName();
    /**
     * Contains the property file name for translation of error
     * messages.
     */
    public static final String RESOURCE = "WebtoolsErrorUiLabel";
    /**
     * Language setting.
     */
    private static Locale locale;

    /** An HTTP WebEvent handler that updates a Generic entity
     *
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return Returns a String specifying the outcome state of the event. This is used to decide which event to run next or which view to display. If null no event is run nor view displayed, allowing the event to call a forward on a RequestDispatcher.
     * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
     * @exception java.rmi.RemoteException Standard RMI Remote Exception
     * @exception java.io.IOException Standard IO Exception
     */
    public static String updateGeneric(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";

        String entityName = request.getParameter("entityName");
        GenericWebEvent.locale = UtilHttp.getLocale(request); 

        if (entityName == null || entityName.length() <= 0) {
            errMsg = UtilProperties.getMessage(GenericWebEvent.RESOURCE, "genericWebEvent.entity_name_not_specified", (locale != null ? locale : Locale.getDefault())) + ".";            
            request.setAttribute("_ERROR_MESSAGE_", errMsg);            
            Debug.logWarning("[GenericWebEvent.updateGeneric] The entityName was not specified, but is required.", module);
            return "error";
        }

        Security security = (Security) request.getAttribute("security");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        if (security == null) {
            errMsg = UtilProperties.getMessage(GenericWebEvent.RESOURCE,"genericWebEvent.security_object_not_found", (locale != null ? locale : Locale.getDefault())) + ".";            
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            Debug.logWarning("[updateGeneric] The security object was not found in the request, please check the control servlet init.", module);
            return "error";
        }
        if (delegator == null) {
            errMsg = UtilProperties.getMessage(GenericWebEvent.RESOURCE, "genericWebEvent.delegator_object_not_found", (locale != null ? locale : Locale.getDefault())) + ".";            
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            Debug.logWarning("[updateGeneric] The delegator object was not found in the request, please check the control servlet init.", module);
            return "error";
        }

        ModelReader reader = delegator.getModelReader();
        ModelEntity entity = null;

        try {
            entity = reader.getModelEntity(entityName);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        String updateMode = request.getParameter("UPDATE_MODE");

        if (updateMode == null || updateMode.length() <= 0) {
            errMsg = UtilProperties.getMessage(GenericWebEvent.RESOURCE, "genericWebEvent.update_mode_not_specified", (locale != null ? locale : Locale.getDefault())) + ".";            
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            Debug.logWarning("[updateGeneric] Update Mode was not specified, but is required; entityName: " + entityName, module);
            return "error";
        }

        // check permissions before moving on...
        if (!security.hasEntityPermission("ENTITY_DATA", "_" + updateMode, request.getSession()) &&
            !security.hasEntityPermission(entity.getPlainTableName(), "_" + updateMode, request.getSession())) {
                Map messageMap = UtilMisc.toMap("updateMode", updateMode, "entityName", entity.getEntityName(), "entityPlainTableName", entity.getPlainTableName());
                errMsg = UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                        "genericWebEvent.not_sufficient_permissions_01", (locale != null ? locale : Locale.getDefault()));
                errMsg += UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                        "genericWebEvent.not_sufficient_permissions_02", messageMap, (locale != null ? locale : Locale.getDefault())) + ".";                                                
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
            // not really successful, but error return through ERROR_MESSAGE, so quietly fail
            return "error";
        }

        GenericValue findByEntity = delegator.makeValue(entityName, null);

        // get the primary key parameters...
        for (int fnum = 0; fnum < entity.getPksSize(); fnum++) {
            ModelField field = entity.getPk(fnum);
            ModelFieldType type = null;

            try {
                type = delegator.getEntityFieldType(entity, field.getType());
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
                Map messageMap = UtilMisc.toMap("fieldType", field.getType());
                errMsg += "<li>" + UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                        "genericWebEvent.fatal_error_param", messageMap, (locale != null ? locale : Locale.getDefault())) + ".";
            }
            String fval = request.getParameter(field.getName());

            if (fval != null && fval.length() > 0) {
                try {
                    findByEntity.setString(field.getName(), fval);
                } catch (Exception e) {
                    Map messageMap = UtilMisc.toMap("fval", fval);
                    errMsg = errMsg + "<li>" + field.getColName() + UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                            "genericWebEvent.conversion_failed", messageMap, (locale != null ? locale : Locale.getDefault())) + type.getJavaType() + ".";
                    Debug.logWarning("[updateGeneric] " + field.getColName() + " conversion failed: \"" + fval + "\" is not a valid " + type.getJavaType() + "; entityName: " + entityName, module);
                }
            }
        }

        // if this is a delete, do that before getting all of the non-pk parameters and validating them
        if (updateMode.equals("DELETE")) {
            // Remove associated/dependent entries from other tables here
            // Delete actual main entity last, just in case database is set up to do a cascading delete, caches won't get cleared
            try {
                delegator.removeByPrimaryKey(findByEntity.getPrimaryKey());
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
                errMsg = UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                        "genericWebEvent.delete_failed", (locale != null ? locale : Locale.getDefault()));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }

            return "success";
        }

        // get the non-primary key parameters
        for (int fnum = 0; fnum < entity.getNopksSize(); fnum++) {
            ModelField field = (ModelField) entity.getNopk(fnum);
            ModelFieldType type = null;

            try {
                type = delegator.getEntityFieldType(entity, field.getType());
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
                Map messageMap = UtilMisc.toMap("fieldType", field.getType());
                errMsg += "<li>" + UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                        "genericWebEvent.fatal_error_param", messageMap, (locale != null ? locale : Locale.getDefault())) + ".";
            }
            String fval = request.getParameter(field.getName());

            if (fval != null && fval.length() > 0) {
                try {
                    findByEntity.setString(field.getName(), fval);
                } catch (Exception e) {
                    Map messageMap = UtilMisc.toMap("fval", fval);
                    errMsg = errMsg + "<li>" + field.getColName() + UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                            "genericWebEvent.conversion_failed", messageMap, (locale != null ? locale : Locale.getDefault())) + type.getJavaType() + ".";
                    Debug.logWarning("[updateGeneric] " + field.getColName() + " conversion failed: \"" + fval + "\" is not a valid " + type.getJavaType() + "; entityName: " + entityName, module);
                }
            }
        }

        // if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
        if (updateMode.equals("CREATE")) {
            GenericValue tempEntity = null;

            try {
                tempEntity = delegator.findByPrimaryKey(findByEntity.getPrimaryKey());
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
                errMsg = UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                        "genericWebEvent.create_failed_by_check", (locale != null ? locale : Locale.getDefault()));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
            if (tempEntity != null) {
                Map messageMap = UtilMisc.toMap("primaryKey", findByEntity.getPrimaryKey().toString());
                errMsg = errMsg + "<li>" + entity.getEntityName() + UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                        "genericWebEvent.already_exists_pk", messageMap, (locale != null ? locale : Locale.getDefault()))+ ".";
                Debug.logWarning("[updateGeneric] " + entity.getEntityName() + " already exists with primary key: " + findByEntity.getPrimaryKey().toString() + "; please change.", module);
            }
        }

        // Validate parameters...
        for (int fnum = 0; fnum < entity.getFieldsSize(); fnum++) {
            ModelField field = entity.getField(fnum);

            for (int j = 0; j < field.getValidatorsSize(); j++) {
                String curValidate = field.getValidator(j);
                Class[] paramTypes = new Class[] {String.class};
                Object[] params = new Object[] {findByEntity.get(field.getName()).toString()};

                String className = "org.ofbiz.base.util.UtilValidate";
                String methodName = curValidate;

                if (curValidate.indexOf('.') > 0) {
                    className = curValidate.substring(0, curValidate.lastIndexOf('.'));
                    methodName = curValidate.substring(curValidate.lastIndexOf('.') + 1);
                }
                Class valClass;

                try {
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    valClass = loader.loadClass(className);
                } catch (ClassNotFoundException cnfe) {
                    Debug.logError("[updateGeneric] Could not find validation class: " + className + "; ignoring.", module);
                    continue;
                }
                Method valMethod;

                try {
                    valMethod = valClass.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException cnfe) {
                    Debug.logError("[updateGeneric] Could not find validation method: " + methodName + " of class " + className + "; ignoring.", module);
                    continue;
                }

                Boolean resultBool;

                try {
                    resultBool = (Boolean) valMethod.invoke(null, params);
                } catch (Exception e) {
                    Debug.logError("[updateGeneric] Could not access validation method: " + methodName + " of class " + className + "; returning true.", module);
                    resultBool = Boolean.TRUE;
                }

                if (!resultBool.booleanValue()) {
                    Field msgField;
                    String message;

                    try {
                        msgField = valClass.getField(curValidate + "Msg");
                        message = (String) msgField.get(null);
                    } catch (Exception e) {
                        Debug.logError("[updateGeneric] Could not find validation message field: " + curValidate + "Msg of class " + className + "; returning generic validation failure message.", module);
                        message = UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                                "genericWebEvent.validation_failed", (locale != null ? locale : Locale.getDefault())) + ".";
                    }
                    errMsg = errMsg + "<li>" + field.getColName() + " " + curValidate + " " + UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                            "genericWebEvent.failed", (locale != null ? locale : Locale.getDefault()))+ ": " + message;
                    Debug.logWarning("[updateGeneric] " + field.getColName() + " " + curValidate + " failed: " + message, module);
                }
            }
        }

        if (errMsg.length() > 0) {
            errMsg = "<br><b>" + UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                    "genericWebEvent.following_error_occurred", (locale != null ? locale : Locale.getDefault())) + ":</b><ul>" + errMsg + "</ul>";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        if (updateMode.equals("CREATE")) {
            GenericValue value;

            try {
                value = delegator.create(findByEntity.getEntityName(), findByEntity.getAllFields());
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
                value = null;
            }
            if (value == null) {
                Map messageMap = UtilMisc.toMap("entityName", entity.getEntityName());
                errMsg = UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                        "genericWebEvent.creation_param_failed", messageMap, (locale != null ? locale : Locale.getDefault()))+ ": " + findByEntity.toString();                
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        } else if (updateMode.equals("UPDATE")) {
            GenericValue value = delegator.makeValue(findByEntity.getEntityName(), findByEntity.getAllFields());

            try {
                value.store();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
                Map messageMap = UtilMisc.toMap("entityName", entity.getEntityName());
                errMsg = UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                        "genericWebEvent.update_of_param_failed", messageMap, (locale != null ? locale : Locale.getDefault()))+ ": " + value.toString();                
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        } else {
            Map messageMap = UtilMisc.toMap("updateMode", updateMode);
            errMsg = UtilProperties.getMessage(GenericWebEvent.RESOURCE,
                    "genericWebEvent.update_of_param_failed", messageMap, (locale != null ? locale : Locale.getDefault()))+ ".";                
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            Debug.logWarning("updateGeneric: Update Mode specified (" + updateMode + ") was not valid for entity: " + findByEntity.toString(), module);
            return "error";
        }

        return "success";
    }
}
