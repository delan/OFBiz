/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.product.promo;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Promotions Services
 */
public class PromoServices {

    public final static String module = PromoServices.class.getName();

    public static Map createProductPromoCodeSet(DispatchContext dctx, Map context) {
        //GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Long quantity = (Long) context.get("quantity");
        //Long useLimitPerCode = (Long) context.get("useLimitPerCode");
        //Long useLimitPerCustomer = (Long) context.get("useLimitPerCustomer");
        //GenericValue promoItem = null;
        //GenericValue newItem = null;

        StringBuffer bankOfNumbers = new StringBuffer();
        for (long i = 0; i < quantity.longValue(); i++) {
            Map createProductPromoCodeMap = null;
            try {
                createProductPromoCodeMap = dispatcher.runSync("createProductPromoCode", dctx.makeValidContext("createProductPromoCode", "IN", context));
            } catch (GenericServiceException err) {
                return ServiceUtil.returnError("Could not create a bank of promo codes", null, null, createProductPromoCodeMap);
            }
            if (ServiceUtil.isError(createProductPromoCodeMap)) {
                // what to do here? try again?
                return ServiceUtil.returnError("Could not create a bank of promo codes", null, null, createProductPromoCodeMap);
            }
            bankOfNumbers.append((String) createProductPromoCodeMap.get("productPromoCodeId"));
            bankOfNumbers.append("<br/>");
        }

        return ServiceUtil.returnSuccess(bankOfNumbers.toString());
    }

    public static Map purgeOldStoreAutoPromos(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String productStoreId = (String) context.get("productStoreId");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        List condList = new LinkedList();
        if (UtilValidate.isEmpty(productStoreId)) {
            condList.add(new EntityExpr("productStoreId", EntityOperator.EQUALS, productStoreId));
        }
        condList.add(new EntityExpr("userEntered", EntityOperator.EQUALS, "Y"));
        condList.add(new EntityExpr("thruDate", EntityOperator.NOT_EQUAL, null));
        condList.add(new EntityExpr("thruDate", EntityOperator.LESS_THAN, nowTimestamp));
        EntityCondition cond = new EntityConditionList(condList, EntityOperator.AND);
        
        try {
            EntityListIterator eli = delegator.findListIteratorByCondition("ProductStorePromoAndAppl", cond, null, null);
            GenericValue productStorePromoAndAppl = null;
            while ((productStorePromoAndAppl = (GenericValue) eli.next()) != null) {
                GenericValue productStorePromo = delegator.makeValue("ProductStorePromoAppl");
                productStorePromo.setAllFields(productStorePromoAndAppl, true, null, null);
                productStorePromo.remove();
            }
            eli.close();
        } catch (GenericEntityException e) {
            String errMsg = "Error removing expired ProductStorePromo records: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        return ServiceUtil.returnSuccess();
    }

    public static Map importPromoCodesFromFile(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();

        // check the uploaded file
        ByteWrapper wrapper = (ByteWrapper) context.get("uploadedFile");
        if (wrapper == null) {
            return ServiceUtil.returnError("Uploaded file not valid or corrupted");
        }

        // get the createProductPromoCode Model
        ModelService promoModel;
        try {
            promoModel = dispatcher.getDispatchContext().getModelService("createProductPromoCode");
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // make a temp context for invocations
        Map invokeCtx = promoModel.makeValid(context, ModelService.IN_PARAM);

        // read the bytes into a reader
        BufferedReader reader = new BufferedReader(new StringReader(new String(wrapper.getBytes())));
        List errors = FastList.newInstance();
        int lines = 0;
        String line;

        // read the uploaded file and process each line
        try {
            while ((line = reader.readLine()) != null) {
                // check to see if we should ignore this line
                if (line.length() > 0 && !line.startsWith("#")) {
                    if (line.length() > 0 && line.length() <= 20) {
                        // valid promo code
                        Map inContext = FastMap.newInstance();
                        inContext.putAll(invokeCtx);
                        inContext.put("productPromoCodeId", line);
                        Map result = dispatcher.runSync("createProductPromoCode", inContext);
                        if (result != null && ServiceUtil.isError(result)) {
                            errors.add(line + ": " + ServiceUtil.getErrorMessage(result));
                        }
                    } else {
                        // not valid ignore and notify
                        errors.add(line + ": is not a valid promo code; must be between 1 and 20 characters");
                    }
                    ++lines;
                }
            }
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                Debug.logError(e, module);
            }
        }

        // return errors or success
        if (errors.size() > 0) {
            return ServiceUtil.returnError(errors);
        } else if (lines == 0) {
            return ServiceUtil.returnError("Empty file; nothing to do");
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map importPromoCodeEmailsFromFile(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();

        String productPromoCodeId = (String) context.get("productPromoCodeId");
        ByteWrapper wrapper = (ByteWrapper) context.get("uploadedFile");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        if (wrapper == null) {
            return ServiceUtil.returnError("Uploaded file not valid or corrupted");
        }

        // read the bytes into a reader
        BufferedReader reader = new BufferedReader(new StringReader(new String(wrapper.getBytes())));
        List errors = FastList.newInstance();
        int lines = 0;
        String line;

        // read the uploaded file and process each line
        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0 && !line.startsWith("#")) {
                    if (UtilValidate.isEmail(line)) {
                        // valid email address
                        Map result = dispatcher.runSync("createProductPromoCodeEmail", UtilMisc.<String, Object>toMap("productPromoCodeId",
                                productPromoCodeId, "emailAddress", line, "userLogin", userLogin));
                        if (result != null && ServiceUtil.isError(result)) {
                            errors.add(line + ": " + ServiceUtil.getErrorMessage(result));
                        }
                    } else {
                        // not valid ignore and notify
                        errors.add(line + ": is not a valid email address");
                    }
                    ++lines;
                }
            }
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                Debug.logError(e, module);
            }
        }

        // return errors or success
        if (errors.size() > 0) {
            return ServiceUtil.returnError(errors);
        } else if (lines == 0) {
            return ServiceUtil.returnError("Empty file; nothing to do");
        }

        return ServiceUtil.returnSuccess();
    }
}
