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

package org.ofbiz.accounting.finaccount;

import java.sql.Timestamp;
import java.util.Map;
import java.util.List;
import java.math.BigDecimal;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import org.ofbiz.order.finaccount.FinAccountHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import javolution.util.FastMap;

public class FinAccountServices {
    
    public static final String module = FinAccountServices.class.getName();
    
    public static Map createFinAccountForStore(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String) context.get("productStoreId");
        String finAccountTypeId = (String) context.get("finAccountTypeId");        
        
        try {
            // get the product store id and use it to generate a unique fin account code
            GenericValue productStoreFinAccountSetting = delegator.findByPrimaryKeyCache("ProductStoreFinActSetting", UtilMisc.toMap("productStoreId", productStoreId, "finAccountTypeId", finAccountTypeId));
            if (productStoreFinAccountSetting == null) {
                return ServiceUtil.returnError("No settings found for store [" + productStoreId + "] for fin account type [" + finAccountTypeId + "]");
            }
            
            Long accountCodeLength = productStoreFinAccountSetting.getLong("accountCodeLength");
            Long accountValidDays = productStoreFinAccountSetting.getLong("accountValidDays");
            Long pinCodeLength = productStoreFinAccountSetting.getLong("pinCodeLength");
            String requirePinCode = productStoreFinAccountSetting.getString("requirePinCode");

            // automatically set the parameters for the create fin account service
            ModelService createService = dctx.getModelService("createFinAccount");
            Map inContext = createService.makeValid(context, ModelService.IN_PARAM);
            Timestamp now = UtilDateTime.nowTimestamp();

            // now use our values
            String finAccountCode = FinAccountHelper.getNewFinAccountCode(accountCodeLength.intValue(), delegator);
            inContext.put("finAccountCode", finAccountCode);

            // with pin codes, the account code becomes the ID and the pin becomes the code
            if ("Y".equalsIgnoreCase(requirePinCode)) {
                String pinCode = FinAccountHelper.getNewFinAccountCode(pinCodeLength.intValue(), delegator);
                inContext.put("finAccountPin", pinCode);
            }

            // set the dates/userlogin
            inContext.put("thruDate", UtilDateTime.getDayEnd(now, accountValidDays.intValue()));
            inContext.put("fromDate", now);
            inContext.put("userLogin", userLogin);

            // product store payToPartyId
            String payToPartyId = ProductStoreWorker.getProductStorePayToPartyId(productStoreId, delegator);
            inContext.put("organizationPartyId", payToPartyId);

            Map createResult = dispatcher.runSync("createFinAccount", inContext);
            
            if (ServiceUtil.isError(createResult)) {
                return createResult;
            } else {
                Map result = ServiceUtil.returnSuccess();
                result.put("finAccountId", createResult.get("finAccountId"));
                result.put("finAccountCode", finAccountCode);
                return result;
            }
        } catch (GenericEntityException ex) {
            return ServiceUtil.returnError(ex.getMessage());
        } catch (GenericServiceException ex) {
            return ServiceUtil.returnError(ex.getMessage());
        }
    }

    public static Map checkFinAccountBalance(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String finAccountId = (String) context.get("finAccountId");
        String finAccountCode = (String) context.get("finAccountCode");

        GenericValue finAccount;
        if (finAccountId == null) {
            try {
                finAccount = FinAccountHelper.getFinAccountFromCode(finAccountCode, delegator);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        } else {
            try {
                finAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", finAccountId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        if (finAccount == null) {
            return ServiceUtil.returnError("Unable to locate financial account");
        }

        // get the balance
        BigDecimal availableBalance = finAccount.getBigDecimal("availableBalance");
        BigDecimal balance = finAccount.getBigDecimal("actualBalance");
        if (availableBalance == null) {
            availableBalance = FinAccountHelper.ZERO;
        }
        if (balance == null) {
            balance = FinAccountHelper.ZERO;
        }

        Boolean isFrozen = Boolean.valueOf("Y".equals(finAccount.getString("isFrozen")));
        Debug.log("FinAccount Balance [" + balance + "] Available [" + availableBalance + "] - Frozen: " + isFrozen, module);

        Map result = ServiceUtil.returnSuccess();
        result.put("availableBalance", new Double(availableBalance.doubleValue()));
        result.put("balance", new Double(balance.doubleValue()));
        result.put("isFrozen", isFrozen);
        return result;
    }

    public static Map checkFinAccountStatus(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String finAccountId = (String) context.get("finAccountId");

        if (finAccountId == null) {
            return ServiceUtil.returnError("Financial account ID is required for this service!");
        }

        GenericValue finAccount;
        try {
            finAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", finAccountId));
        } catch (GenericEntityException ex) {
            return ServiceUtil.returnError(ex.getMessage());
        }

        if (finAccount != null) {            
            String frozen = finAccount.getString("isFrozen");
            if (frozen == null) frozen = "N";

            BigDecimal balance = finAccount.getBigDecimal("actualBalance");
            if (balance == null) {
                balance = FinAccountHelper.ZERO;
            }

            Debug.log("Account #" + finAccountId + " Balance: " + balance + " Frozen: " + frozen, module);

            if ("N".equals(frozen) && balance.compareTo(FinAccountHelper.ZERO) < 1) {
                finAccount.set("isFrozen", "Y");
                Debug.logInfo("Financial account [" + finAccountId + "] has passed its threshold [" + balance + "] (Frozen)", module);
            } else if ("Y".equals(frozen) && balance.compareTo(FinAccountHelper.ZERO) > 0) {
                finAccount.set("isFrozen", "N");
                Debug.logInfo("Financial account [" + finAccountId + "] has been made current [" + balance + "] (Un-Frozen)", module);
            }
            try {
                finAccount.store();
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map refundFinAccount(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String finAccountId = (String) context.get("finAccountId");
        Map result = null;

        GenericValue finAccount;
        try {
            finAccount = delegator.findByPrimaryKey("FinAccount", UtilMisc.toMap("finAccountId", finAccountId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        if (finAccount != null) {
            // get the actual and available balance
            BigDecimal availableBalance = finAccount.getBigDecimal("availableBalance");
            BigDecimal actualBalance = finAccount.getBigDecimal("actualBalance");

            // if they do not match, then there are outstanding authorizations which need to be settled first
            if (!actualBalance.equals(availableBalance)) {
                return ServiceUtil.returnError("Available balance does not match the actual balance; pending authorizations; cannot refund FinAccount at this time.");
            }

            // now we make sure there is something to refund
            if (actualBalance.doubleValue() > 0) {
                BigDecimal remainingBalance = new BigDecimal(actualBalance.toString());
                BigDecimal refundAmount = new BigDecimal(0);

                EntityCondition condition = new EntityExpr("finAccountTransTypeId", EntityOperator.EQUALS, "DEPOSIT");
                EntityListIterator eli = null;
                try {
                    eli = delegator.findListIteratorByCondition("FinAccountTrans", condition, null, UtilMisc.toList("-transactionDate"));

                    GenericValue trans;
                    while (remainingBalance.compareTo(FinAccountHelper.ZERO) == 1 && (trans = (GenericValue) eli.next()) != null) {
                        String orderId = trans.getString("orderId");
                        String orderItemSeqId = trans.getString("orderItemSeqId");

                        // make sure there is an order available to refund
                        if (orderId != null && orderItemSeqId != null) {
                            GenericValue orderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                            if (!"ITEM_CANCELLED".equals(orderItem.getString("statusId"))) {
                                
                                // make sure the item hasn't already been returned
                                List returnItems = orderItem.getRelated("ReturnItem");
                                if (returnItems == null || returnItems.size() == 0) {
                                    BigDecimal txAmt = trans.getBigDecimal("amount");
                                    BigDecimal refAmt = txAmt;
                                    if (remainingBalance.compareTo(txAmt) == -1) {
                                        refAmt = remainingBalance;
                                    }
                                    remainingBalance = remainingBalance.subtract(refAmt);
                                    refundAmount = refundAmount.add(refAmt);

                                    // create the return header
                                    Map rhCtx = UtilMisc.toMap("returnHeaderTypeId", "CUSTOMER_RETURN", "userLogin", userLogin);
                                    Map rhResp = dispatcher.runSync("createReturnHeader", rhCtx);
                                    if (ServiceUtil.isError(rhResp)) {
                                        throw new GeneralException(ServiceUtil.getErrorMessage(rhResp));
                                    }
                                    String returnId = (String) rhResp.get("returnId");

                                    // create the return item
                                    Map returnItemCtx = FastMap.newInstance();
                                    returnItemCtx.put("returnId", returnId);
                                    returnItemCtx.put("orderId", orderId);
                                    returnItemCtx.put("description", orderItem.getString("itemDescription"));
                                    returnItemCtx.put("orderItemSeqId", orderItemSeqId);
                                    returnItemCtx.put("returnQuantity", new Double(1));
                                    returnItemCtx.put("receivedQuantity", new Double(1));
                                    returnItemCtx.put("returnPrice", new Double(refAmt.doubleValue()));
                                    returnItemCtx.put("returnReasonId", "RTN_NOT_WANT");
                                    returnItemCtx.put("returnTypeId", "RTN_REFUND"); // refund return
                                    returnItemCtx.put("returnItemTypeId", "RET_NPROD_ITEM");
                                    returnItemCtx.put("userLogin", userLogin);

                                    Map retItResp = dispatcher.runSync("createReturnItem", returnItemCtx);
                                    if (ServiceUtil.isError(retItResp)) {
                                        throw new GeneralException(ServiceUtil.getErrorMessage(retItResp));
                                    }
                                    String returnItemSeqId = (String) retItResp.get("returnItemSeqId");

                                    // approve the return
                                    Map appRet = UtilMisc.toMap("statusId", "RETURN_ACCEPTED", "returnId", returnId, "userLogin", userLogin);
                                    Map appResp = dispatcher.runSync("updateReturnHeader", appRet);
                                    if (ServiceUtil.isError(appResp)) {
                                        throw new GeneralException(ServiceUtil.getErrorMessage(appResp));
                                    }

                                    // "receive" the return - should trigger the refund
                                    Map recRet = UtilMisc.toMap("statusId", "RETURN_RECEIVED", "returnId", returnId, "userLogin", userLogin);
                                    Map recResp = dispatcher.runSync("updateReturnHeader", recRet);
                                    if (ServiceUtil.isError(recResp)) {
                                        throw new GeneralException(ServiceUtil.getErrorMessage(recResp));
                                    }

                                    // get the return item
                                    GenericValue returnItem = delegator.findByPrimaryKey("ReturnItem",
                                            UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
                                    GenericValue response = returnItem.getRelatedOne("ReturnItemResponse");
                                    if (response == null) {
                                        throw new GeneralException("No return response found for: " + returnItem.getPrimaryKey());
                                    }
                                    String paymentId = response.getString("paymentId");

                                    // create the adjustment transaction
                                    Map txCtx = FastMap.newInstance();
                                    txCtx.put("finAccountTransTypeId", "ADJUSTMENT");
                                    txCtx.put("finAccountId", finAccountId);
                                    txCtx.put("orderId", orderId);
                                    txCtx.put("orderItemSeqId", orderItemSeqId);
                                    txCtx.put("paymentId", paymentId);
                                    txCtx.put("amount", new Double(refAmt.doubleValue() * -1));
                                    txCtx.put("partyId", finAccount.getString("ownerPartyId"));
                                    txCtx.put("userLogin", userLogin);

                                    Map txResp = dispatcher.runSync("createFinAccountTrans", txCtx);
                                    if (ServiceUtil.isError(txResp)) {
                                        throw new GeneralException(ServiceUtil.getErrorMessage(txResp));
                                    }
                                }
                            }
                        }
                    }
                } catch (GeneralException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                } finally {
                    if (eli != null) {
                        try {
                            eli.close();
                        } catch (GenericEntityException e) {
                            Debug.logWarning(e, module);
                        }
                    }
                }

                // check to make sure we balanced out
                if (remainingBalance.compareTo(FinAccountHelper.ZERO) == 1) {
                    result = ServiceUtil.returnSuccess("FinAccount partially refunded; not enough replenish deposits to refund!");
                }
            }
        }

        if (result == null) {
            result = ServiceUtil.returnSuccess();
        }

        return result;
    }
}
