package org.ofbiz.core.entity;

import java.rmi.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.entity.model.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Web Event for doing updates on Generic Entities
 * <p><b>Description:</b> none
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
 *@author     <a href='mailto:jonesde@ofbiz.org'>David E. Jones (jonesde@ofbiz.org)</a>
 *@created    Aug 18 2001
 *@version    1.0
 */
public class GenericWebEvent {
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
    if(entityName == null || entityName.length() <= 0) {
      request.setAttribute("ERROR_MESSAGE", "The entityName was not specified, but is required.");
      Debug.logWarning("[GenericWebEvent.updateGeneric] The entityName was not specified, but is required.");
      return "error";
    }
    
    Security security = (Security)request.getAttribute("security");
    GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    if(security == null) {
      request.setAttribute("ERROR_MESSAGE", "The security object was not found in the request, please check the control servlet init.");
      Debug.logWarning("[updateGeneric] The security object was not found in the request, please check the control servlet init.");
      return "error";
    }
    if(delegator == null) {
      request.setAttribute("ERROR_MESSAGE", "The delegator object was not found in the request, please check the control servlet init.");
      Debug.logWarning("[updateGeneric] The delegator object was not found in the request, please check the control servlet init.");
      return "error";
    }

    ModelReader reader = delegator.getModelReader();
    ModelEntity entity = reader.getModelEntity(entityName);
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0) {
      request.setAttribute("ERROR_MESSAGE", "Update Mode was not specified, but is required.");
      Debug.logWarning("[updateGeneric] Update Mode was not specified, but is required; entityName: " + entityName);
      return "error";
    }
    
    //check permissions before moving on...
    if(!security.hasEntityPermission(entity.tableName, "_" + updateMode, request.getSession())) {
      request.setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " " + entity.entityName + " (" + entity.tableName + "_" + updateMode + " or " + entity.tableName + "_ADMIN needed).");
      //not really successful, but error return through ERROR_MESSAGE, so quietly fail
      return "error";
    }
    
    GenericEntity findByEntity = new GenericEntity(entity);
    
    //get the primary key parameters...
    for(int fnum=0; fnum<entity.pks.size(); fnum++) {
      ModelField field = (ModelField)entity.pks.get(fnum);
      ModelFieldType type = null;
      try { type = delegator.getEntityFieldType(entity, field.type); }
      catch(GenericEntityException e) { Debug.logWarning(e); errMsg += "<li> Fatal error: field type \"" + field.type + "\" not found"; }
      if(type.javaType.equals("Timestamp") || type.javaType.equals("java.sql.Timestamp")) {
        String fvalDate = request.getParameter(field.name + "_DATE");
        String fvalTime = request.getParameter(field.name + "_TIME");
        if(fvalDate != null && fvalDate.length() > 0) {
          try { findByEntity.setString(field.name, fvalDate + " " + fvalTime); }
          catch(Exception e) {
            errMsg = errMsg + "<li>" + field.colName + " conversion failed: \"" + fvalDate + " " + fvalTime + "\" is not a valid " + type.javaType;
            Debug.logWarning("[updateGeneric] " + field.colName + " conversion failed: \"" + fvalDate + " " + fvalTime + "\" is not a valid " + type.javaType + "; entityName: " + entityName);
          }
        }
      }
      else {
        String fval = request.getParameter(field.name);
        if(fval != null && fval.length() > 0) {
          try { findByEntity.setString(field.name, fval); }
          catch(Exception e) {
            errMsg = errMsg + "<li>" + field.colName + " conversion failed: \"" + fval + "\" is not a valid " + type.javaType;
            Debug.logWarning("[updateGeneric] " + field.colName + " conversion failed: \"" + fval + "\" is not a valid " + type.javaType + "; entityName: " + entityName);
          }
        }
      }
    }
    
    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE")) {
      //Remove associated/dependent entries from other tables here
      //Delete actual main entity last, just in case database is set up to do a cascading delete, caches won't get cleared
      try { delegator.removeByPrimaryKey(findByEntity.getPrimaryKey()); }
      catch(GenericEntityException e) { 
        Debug.logWarning(e); 
        request.setAttribute("ERROR_MESSAGE", "Delete failed (write error)");
        return "error";
      }

      return "success";
    }
    
    //get the non-primary key parameters
    for(int fnum=0; fnum<entity.nopks.size(); fnum++) {
      ModelField field = (ModelField)entity.nopks.get(fnum);
      ModelFieldType type = null;
      try { type = delegator.getEntityFieldType(entity, field.type); }
      catch(GenericEntityException e) { Debug.logWarning(e); errMsg += "<li> Fatal error: field type \"" + field.type + "\" not found"; }
      if(type.javaType.equals("Timestamp") || type.javaType.equals("java.sql.Timestamp")) {
        String fvalDate = request.getParameter(field.name + "_DATE");
        String fvalTime = request.getParameter(field.name + "_TIME");
        if(fvalDate != null && fvalDate.length() > 0) {
          try { findByEntity.setString(field.name, fvalDate + " " + fvalTime); }
          catch(Exception e) {
            errMsg = errMsg + "<li>" + field.colName + " conversion failed: \"" + fvalDate + " " + fvalTime + "\" is not a valid " + type.javaType;
            Debug.logWarning("[updateGeneric] " + field.colName + " conversion failed: \"" + fvalDate + " " + fvalTime + "\" is not a valid " + type.javaType + "; entityName: " + entityName);
          }
        }
      }
      else {
        String fval = request.getParameter(field.name);
        if(fval != null && fval.length() > 0) {
          try { findByEntity.setString(field.name, fval); }
          catch(Exception e) {
            errMsg = errMsg + "<li>" + field.colName + " conversion failed: \"" + fval + "\" is not a valid " + type.javaType;
            Debug.logWarning("[updateGeneric] " + field.colName + " conversion failed: \"" + fval + "\" is not a valid " + type.javaType + "; entityName: " + entityName);
          }
        }
      }
    }
    
    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.equals("CREATE")) {
      GenericValue tempEntity = null;
      try { tempEntity = delegator.findByPrimaryKey(findByEntity.getPrimaryKey()); }
      catch(GenericEntityException e) { 
        Debug.logWarning(e); 
        request.setAttribute("ERROR_MESSAGE", "Create failed while checking if exists (read error)");
        return "error";
      }
      if(tempEntity != null) {
        errMsg = errMsg + "<li>" + entity.entityName + " already exists with primary key: " + findByEntity.getPrimaryKey().toString() + "; please change.";
        Debug.logWarning("[updateGeneric] " + entity.entityName + " already exists with primary key: " + findByEntity.getPrimaryKey().toString() + "; please change.");
      }
    }
    
    //Validate parameters...
    for(int fnum=0; fnum<entity.fields.size(); fnum++) {
      ModelField field = (ModelField)entity.fields.get(fnum);
      
      for(int j=0;j<field.validators.size();j++) {
        String curValidate=(String)field.validators.elementAt(j);
        Class[] paramTypes = new Class[] {String.class};
        Object[] params = new Object[] {findByEntity.get(field.name).toString()};
        
        String className = "org.ofbiz.core.util.UtilValidate";
        String methodName = curValidate;
        if(curValidate.indexOf('.') > 0) {
          className = curValidate.substring(0, curValidate.lastIndexOf('.'));
          methodName = curValidate.substring(curValidate.lastIndexOf('.') + 1);
        }
        Class valClass;
        try { valClass = Class.forName(className); }
        catch(ClassNotFoundException cnfe) { Debug.logError("[updateGeneric] Could not find validation class: " + className + "; ignoring."); continue; }
        Method valMethod;
        try { valMethod = valClass.getMethod(methodName, paramTypes); }
        catch(NoSuchMethodException cnfe) { Debug.logError("[updateGeneric] Could not find validation method: " + methodName + " of class " + className + "; ignoring."); continue; }
        
        Boolean resultBool;
        try { resultBool = (Boolean)valMethod.invoke(null,params); }
        catch(Exception e) {
          Debug.logError("[updateGeneric] Could not access validation method: " + methodName + " of class " + className + "; returning true.");
          resultBool = new Boolean(true);
        }
        
        if(!resultBool.booleanValue()) {
          Field msgField;
          String message;
          try {
            msgField = valClass.getField(curValidate + "Msg");
            message = (String)msgField.get(null);
          }
          catch(Exception e) {
            Debug.logError("[updateGeneric] Could not find validation message field: " + curValidate + "Msg of class " + className + "; returning generic validation failure message.");
            message = "validation failed.";
          }
          errMsg = errMsg + "<li>" + field.colName + " " + curValidate + " failed: " + message;
          Debug.logWarning("[updateGeneric] " + field.colName + " " + curValidate + " failed: " + message);
        }
      }
    }
    
    if(errMsg.length() > 0) {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      return "error";
    }
    
    if(updateMode.equals("CREATE")) {
      GenericValue value;
      try { value = delegator.create(findByEntity.entityName, findByEntity.fields); }
      catch(GenericEntityException e) { Debug.logWarning(e); value = null; }
      if(value == null) {
        request.setAttribute("ERROR_MESSAGE", "Creation of " + entity.entityName + " failed for entity: " + findByEntity.toString());
        return "error";
      }
    }
    else if(updateMode.equals("UPDATE")) {
      GenericValue value = delegator.makeValue(findByEntity.entityName, findByEntity.fields);
      try { value.store(); }
      catch(GenericEntityException e) {
        Debug.logWarning(e);
        request.setAttribute("ERROR_MESSAGE", "Update of " + entity.entityName + " failed for value: " + value.toString());
        return "error";
      }
    }
    else {
      request.setAttribute("ERROR_MESSAGE", "Update Mode specified (" + updateMode + ") was not valid.");
      Debug.logWarning("updateGeneric: Update Mode specified (" + updateMode + ") was not valid for entity: " + findByEntity.toString());
      return "error";
    }
    
    return "success";
  }
}
