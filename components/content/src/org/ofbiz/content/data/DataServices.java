package org.ofbiz.content.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.File;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.content.webapp.ftl.FreeMarkerWorker;

import freemarker.template.SimpleHash;

/**
 * DataServices Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Revision: 1.8 $
 * @since 3.0
 * 
 *  
 */
public class DataServices {

    public static final String module = DataServices.class.getName();

    /**
     * A top-level service for creating a DataResource and ElectronicText together.
     */
    public static Map createDataResourceAndText(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        context.put("entityOperation", "_CREATE");
        List targetOperations = new ArrayList();
        targetOperations.add("CREATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        String permissionStatus = DataResourceWorker.callDataResourcePermissionCheck(delegator, dispatcher, context);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            context.put("skipPermissionCheck", "granted");
            Map thisResult = createDataResourceMethod(dctx, context);
            if (thisResult.get(ModelService.RESPONSE_MESSAGE) != null) {
                return ServiceUtil.returnError((String) thisResult.get(ModelService.ERROR_MESSAGE));
            }
            result.put("dataResourceId", thisResult.get("dataResourceId"));
            context.put("dataResourceId", thisResult.get("dataResourceId"));

            String dataResourceTypeId = (String) context.get("dataResourceTypeId");
            if (dataResourceTypeId != null && dataResourceTypeId.equals("ELECTRONIC_TEXT")) {
                thisResult = createElectronicText(dctx, context);
                if (thisResult.get(ModelService.RESPONSE_MESSAGE) != null) {
                    return ServiceUtil.returnError((String) thisResult.get(ModelService.ERROR_MESSAGE));
                }
            }
        }

        return result;
    }

    /**
     * A service wrapper for the createDataResourceMethod method. Forces permissions to be checked.
     */
    public static Map createDataResource(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_CREATE");
        List targetOperations = new ArrayList();
        targetOperations.add("CREATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = createDataResourceMethod(dctx, context);
        return result;
    }

    public static Map createDataResourceMethod(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String permissionStatus = DataResourceWorker.callDataResourcePermissionCheck(delegator, dispatcher, context);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String userLoginId = (String) userLogin.get("userLoginId");
            String createdByUserLogin = userLoginId;
            String lastModifiedByUserLogin = userLoginId;
            Timestamp createdDate = UtilDateTime.nowTimestamp();
            Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();

            // If textData exists, then create DataResource and return dataResourceId
            String dataResourceId = (String) context.get("dataResourceId");
            if (dataResourceId == null)
                dataResourceId = delegator.getNextSeqId("DataResource").toString();
            GenericValue dataResource = delegator.makeValue("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
            dataResource.setNonPKFields(context);
            dataResource.put("createdByUserLogin", createdByUserLogin);
            dataResource.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
            dataResource.put("createdDate", createdDate);
            dataResource.put("lastModifiedDate", lastModifiedDate);
            try {
                dataResource.create();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
            result.put("dataResourceId", dataResourceId);
            result.put("dataResource", dataResource);
        }
        return result;
    }

    /**
     * A service wrapper for the createElectronicTextMethod method. Forces permissions to be checked.
     */
    public static Map createElectronicText(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_CREATE");
        List targetOperations = new ArrayList();
        targetOperations.add("CREATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = createElectronicTextMethod(dctx, context);
        return result;
    }

    public static Map createElectronicTextMethod(DispatchContext dctx, Map context) {
        HashMap result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        //Debug.logInfo("in create ETextMethod context:" + context, null);
        String permissionStatus = DataResourceWorker.callDataResourcePermissionCheck(delegator, dispatcher, context);
        //Debug.logInfo("in create ETextMethod permissionStatus:" + permissionStatus, null);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            String dataResourceId = (String) context.get("dataResourceId");
            String textData = (String) context.get("textData");
            if (textData != null && textData.length() > 0) {
                GenericValue electronicText = delegator.makeValue("ElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId, "textData", textData));
                try {
                    electronicText.create();
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError(e.getMessage());
                }
            }
        }

        return result;
    }

    /**
     * A service wrapper for the createFileMethod method. Forces permissions to be checked.
     */
    public static Map createFile(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_CREATE");
        List targetOperations = new ArrayList();
        targetOperations.add("CREATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = null;
        try {
            result = createFileMethod(dctx, context);
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    public static Map createFileMethod(DispatchContext dctx, Map context) throws GenericServiceException {
        HashMap result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        //Debug.logInfo("in create FileMethod context:" + context, module);
        String permissionStatus = DataResourceWorker.callDataResourcePermissionCheck(delegator, dispatcher, context);
        //Debug.logInfo("in create FileMethod permissionStatus:" + permissionStatus, module);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            GenericValue dataResource = (GenericValue) context.get("dataResource");
            //String dataResourceId = (String) dataResource.get("dataResourceId");
            String dataResourceTypeId = (String) dataResource.get("dataResourceTypeId");
            String objectInfo = (String) dataResource.get("objectInfo");
            String textData = (String) context.get("textData");
            String prefix = "";
            File file = null;
            if (textData != null && textData.length() > 0) {
                //String fileName = "";
                String sep = "";
                try {
                    if (UtilValidate.isEmpty(dataResourceTypeId) || dataResourceTypeId.equals("LOCAL_FILE")) {
                        file = new File(objectInfo);
                        //Debug.logInfo("in create FileMethod file:" + file, module);
                        if (!file.isAbsolute()) {
                            throw new GenericServiceException("File: " + file + " is not absolute");
                        }
                    } else if (dataResourceTypeId.equals("OFBIZ_FILE")) {
                        prefix = System.getProperty("ofbiz.home");
                        if (objectInfo.indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                            sep = "/";
                        }
                        file = new File(prefix + sep + objectInfo);
                    } else if (dataResourceTypeId.equals("CONTEXT_FILE")) {
                        prefix = (String) context.get("rootDir");
                        if (objectInfo.indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                            sep = "/";
                        }
                        file = new File(prefix + sep + objectInfo);
                    }
                    if (file == null) {
                        throw new IOException("File: " + file + " is null");
                    }
                    FileWriter out = new FileWriter(file);
                    out.write(textData);
                    out.close();
                } catch (IOException e) {
                    Debug.logWarning(e, module);
                    throw new GenericServiceException(e.getMessage());
                }
            }
        }

        return result;
    }

    /**
     * A top-level service for updating a DataResource and ElectronicText together.
     */
    public static Map updateDataResourceAndText(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        context.put("entityOperation", "_UPDATE");
        List targetOperations = new ArrayList();
        targetOperations.add("UPDATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        String permissionStatus = DataResourceWorker.callDataResourcePermissionCheck(delegator, dispatcher, context);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            context.put("skipPermissionCheck", "granted");
            Map thisResult = updateDataResourceMethod(dctx, context);
            if (thisResult.get(ModelService.RESPONSE_MESSAGE) != null) {
                return ServiceUtil.returnError((String) thisResult.get(ModelService.ERROR_MESSAGE));
            }
            context.put("dataResourceId", thisResult.get("dataResourceId"));

            String dataResourceTypeId = (String) context.get("dataResourceTypeId");
            if (dataResourceTypeId != null && dataResourceTypeId.equals("ELECTRONIC_TEXT")) {
                thisResult = updateElectronicText(dctx, context);
                if (thisResult.get(ModelService.RESPONSE_MESSAGE) != null) {
                    return ServiceUtil.returnError((String) thisResult.get(ModelService.ERROR_MESSAGE));
                }
            }
        }

        return result;
    }

    /**
     * A service wrapper for the updateDataResourceMethod method. Forces permissions to be checked.
     */
    public static Map updateDataResource(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_CREATE");
        List targetOperations = new ArrayList();
        targetOperations.add("CREATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = updateDataResourceMethod(dctx, context);
        return result;
    }

    public static Map updateDataResourceMethod(DispatchContext dctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue dataResource = null;
        //Locale locale = (Locale) context.get("locale");
        String permissionStatus = DataResourceWorker.callDataResourcePermissionCheck(delegator, dispatcher, context);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String userLoginId = (String) userLogin.get("userLoginId");
            String lastModifiedByUserLogin = userLoginId;
            Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();

            // If textData exists, then create DataResource and return dataResourceId
            String dataResourceId = (String) context.get("dataResourceId");
            try {
                dataResource = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
                return ServiceUtil.returnError("dataResource.update.read_failure" + e.getMessage());
            }

            dataResource.setNonPKFields(context);
            dataResource.put("lastModifiedByUserLogin", lastModifiedByUserLogin);
            dataResource.put("lastModifiedDate", lastModifiedDate);
            try {
                dataResource.store();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        result.put("dataResource", dataResource);
        return result;
    }

    /**
     * A service wrapper for the updateElectronicTextMethod method. Forces permissions to be checked.
     */
    public static Map updateElectronicText(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_UPDATE");
        List targetOperations = new ArrayList();
        targetOperations.add("UPDATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = updateElectronicTextMethod(dctx, context);
        return result;
    }

    public static Map updateElectronicTextMethod(DispatchContext dctx, Map context) {
        HashMap result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue electronicText = null;
        //Locale locale = (Locale) context.get("locale");
        String permissionStatus = DataResourceWorker.callDataResourcePermissionCheck(delegator, dispatcher, context);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            String dataResourceId = (String) context.get("dataResourceId");
            String textData = (String) context.get("textData");
            if (textData != null && textData.length() > 0) {
                try {
                    electronicText = delegator.findByPrimaryKey("ElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId));
                    electronicText.put("textData", textData);
                    electronicText.store();
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                    return ServiceUtil.returnError("electronicText.update.read_failure" + e.getMessage());
                }
            }
        }

        return result;
    }

    /**
     * A service wrapper for the updateFileMethod method. Forces permissions to be checked.
     */
    public static Map updateFile(DispatchContext dctx, Map context) {
        context.put("entityOperation", "_UPDATE");
        List targetOperations = new ArrayList();
        targetOperations.add("UPDATE_CONTENT");
        context.put("targetOperationList", targetOperations);
        context.put("skipPermissionCheck", null);
        Map result = null;
        try {
            result = updateFileMethod(dctx, context);
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    public static Map updateFileMethod(DispatchContext dctx, Map context) throws GenericServiceException {
        HashMap result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        //GenericValue fileText = null;
        //Locale locale = (Locale) context.get("locale");
        String permissionStatus = DataResourceWorker.callDataResourcePermissionCheck(delegator, dispatcher, context);
        if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
            GenericValue dataResource = (GenericValue) context.get("dataResource");
            //String dataResourceId = (String) dataResource.get("dataResourceId");
            String dataResourceTypeId = (String) dataResource.get("dataResourceTypeId");
            String objectInfo = (String) dataResource.get("objectInfo");
            String textData = (String) context.get("textData");
            String prefix = "";
            File file = null;
            String fileName = "";
            String sep = "";
            try {
                if (UtilValidate.isEmpty(dataResourceTypeId) || dataResourceTypeId.equals("LOCAL_FILE")) {
                    fileName = prefix + sep + objectInfo;
                    file = new File(fileName);
                    if (file == null) {
                        throw new GenericServiceException("File: " + fileName + " is null.");
                    }
                    if (!file.isAbsolute()) {
                        throw new GenericServiceException("File: " + fileName + " is not absolute.");
                    }
                } else if (dataResourceTypeId.equals("OFBIZ_FILE")) {
                    prefix = System.getProperty("ofbiz.home");
                    if (objectInfo.indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                        sep = "/";
                    }
                    file = new File(prefix + sep + objectInfo);
                } else if (dataResourceTypeId.equals("CONTEXT_FILE")) {
                    prefix = (String) context.get("rootDir");
                    if (objectInfo.indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                        sep = "/";
                    }
                    file = new File(prefix + sep + objectInfo);
                }
                if (file == null) {
                    throw new IOException("File: " + file + " is null");
                }
                FileWriter out = new FileWriter(file);
                out.write(textData);
                out.close();
            } catch (IOException e) {
                Debug.logWarning(e, module);
                throw new GenericServiceException(e.getMessage());
            }
        }

        return result;
    }

    public static void renderDataResourceAsText(DispatchContext dctx, Map context) throws GeneralException, IOException {
        //Map results = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Writer out = (Writer) context.get("outWriter");
        SimpleHash templateContext = (SimpleHash) context.get("templateContext");
        //GenericValue userLogin = (GenericValue) context.get("userLogin");
        String dataResourceId = (String) context.get("dataResourceId");
        if (templateContext != null && UtilValidate.isEmpty(dataResourceId)) {
            dataResourceId = (String) FreeMarkerWorker.get(templateContext, "dataResourceId");
        }
        String mimeTypeId = (String) context.get("mimeTypeId");
        if (templateContext != null && UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = (String) FreeMarkerWorker.get(templateContext, "mimeTypeId");
        }

        Locale locale = (Locale) context.get("locale");

        if (templateContext == null) {
            templateContext = new SimpleHash();
        }

        GenericValue view = (GenericValue) context.get("subContentDataResourceView");
        DataResourceWorker.renderDataResourceAsText(delegator, dataResourceId, out, templateContext, view, locale, mimeTypeId);
        return;
    }

    public static void renderDataResourceAsHtml(DispatchContext dctx, Map context) throws GeneralException, IOException {
        //Map results = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Writer out = (Writer) context.get("outWriter");
        SimpleHash templateContext = (SimpleHash) context.get("templateContext");
        //GenericValue userLogin = (GenericValue) context.get("userLogin");
        String dataResourceId = (String) context.get("dataResourceId");
        if (templateContext != null && UtilValidate.isEmpty(dataResourceId)) {
            dataResourceId = (String) FreeMarkerWorker.get(templateContext, "dataResourceId");
        }
        String mimeTypeId = (String) context.get("mimeTypeId");
        if (templateContext != null && UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = (String) FreeMarkerWorker.get(templateContext, "mimeTypeId");
        }

        Locale locale = (Locale) context.get("locale");

        if (templateContext == null) {
            templateContext = new SimpleHash();
        }

        GenericValue view = (GenericValue) context.get("subContentDataResourceView");
        DataResourceWorker.renderDataResourceAsHtml(delegator, dataResourceId, out, templateContext, view, locale, mimeTypeId);
        return;
    }
}
