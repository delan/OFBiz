/*
 * $Id: MrpServices.java,v 1.1 2004/05/09 21:29:06 holivier Exp $
 *
 * Copyright (c) 2003, 2004 Thierry GRAUSS - tgrauss@free.fr
 * Copyright (c) 2003, 2004 Nereide - www.nereide.biz
 * 
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 */
 
package org.ofbiz.manufacturing.mrp;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;
import java.util.Date;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.manufacturing.mrp.ProposedOrder;
import org.ofbiz.manufacturing.techdata.ProductHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;


/**
 * Services for running MRP
 *
 * @author     <a href=mailto:thierry.grauss@etu.univ-tours.fr">Thierry GRAUSS</a>
 * @version    $Revision: 1.1 $
 * @since      0.1
 */
public class MrpServices {

    public static final String module = MrpServices.class.getName();
    public static final String resource = "ManufacturingUiLabels";
    
    

    /**
     * Initialize the InventoryEventPlanned table.
     * <li>PreConditions : none</li>
	 * <li>Result : The table InventoryEventPlannedForMRP is initialized</li> 
	 * <li>INPUT : Parameter to get from the context :</li><ul>
	 * <li>Boolean reInitialize<br/>
	 * if true : we must reinitialize the table, else we synchronize the table (not for the moment)</li></ul>
	 * 
	 * <li>OUTPUT : Result to put in the map :</li><ul>
	 * <li>none</li></ul>
	 * 
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map initInventoryEventPlanned(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();        
		Security security = ctx.getSecurity();
		Timestamp now = UtilDateTime.nowTimestamp();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");		
        
        //Erases the old table 	for the moment and initializes it with the new orders,
        //Does not modify the old one now.
		Debug.logInfo("initInventoryEventPlanned called", module);
        
        Map parameters = null;
        List listResult = null;
		try{
			listResult = delegator.findAll("InventoryEventPlanned");
		} catch(GenericEntityException e) {
			Debug.logError(e,"Error : delegator.findAll(\"InventoryEventPlanned\")", module);
			return ServiceUtil.returnError("Problem, we can not find all the items of InventoryEventPlanned, for more detail look at the log");
		}
		if(listResult != null){
			try{
				delegator.removeAll(listResult);
			} catch(GenericEntityException e) {
				Debug.logError(e,"Error : delegator.removeAll(listResult), listResult ="+listResult, module);
				return ServiceUtil.returnError("Problem, we can not remove the InventoryEventPlanned items, for more detail look at the log");
			}
		}    

		GenericValue genericResult = null;  
		List resultList = null;
		parameters = UtilMisc.toMap("statusId", "ITEM_APPROVED");
		try {
			resultList = delegator.findByAnd("OrderItem", parameters);
		} catch(GenericEntityException e) {
			Debug.logError(e, "Error : delegator.findByAnd(\"OrderItem\", parameters\")", module);
			Debug.logError(e, "Error : parameters = "+parameters,module);
			return ServiceUtil.returnError("Problem, we can not find the order items, for more detail look at the log");
		}
		Iterator iteratorResult = resultList.iterator();
		while(iteratorResult.hasNext()){
			genericResult = (GenericValue) iteratorResult.next();
            String productId =  genericResult.getString("productId");
            Double eventQuantityTmp = new Double(-1.0 * genericResult.getDouble("quantity").doubleValue());
			Timestamp estimatedShipDate = genericResult.getTimestamp("estimatedShipDate");
            if (estimatedShipDate == null) estimatedShipDate =now;
			
			
			GenericValue tempInventoryEventPlanned = delegator.makeValue("InventoryEventPlanned", UtilMisc.toMap("productId", productId, "eventDate", estimatedShipDate, "inventoryEventPlanTypeId", "SALE_ORDER_SHIP"));
//			tempInventoryEventPlanned.setNonPKFields(context);
			
			genericResult = null;
			parameters = UtilMisc.toMap("productId",productId,"eventDate",estimatedShipDate,"inventoryEventPlanTypeId","SALE_ORDER_SHIP");
			try {
				genericResult = delegator.findByPrimaryKey("InventoryEventPlanned", parameters);
			} catch (GenericEntityException e) {
				Debug.logError(e,"Error : delegator.findByPrimaryKey(\"InventoryEventPlanned\", parameters)", module);
				Debug.logError(e,"Error : parameters = "+parameters,module);
				return ServiceUtil.returnError("Problem, we can not find the products, for more detail look at the log");
			}
			if(genericResult==null){
				tempInventoryEventPlanned.put("eventQuantity", eventQuantityTmp);
				try {
					tempInventoryEventPlanned.create();
				} catch (GenericEntityException e) {
					Debug.logError(e,"Error : InventoryEventPlanned.create()", module);
					Debug.logError(e,"Error : parameters = "+tempInventoryEventPlanned, module);
					return ServiceUtil.returnError("Problem, we can not create the product in InventoryEventPlanned, for more detail look at the log");
				}
			}
			else{
				double qties = eventQuantityTmp.doubleValue() - ((Double)genericResult.get("eventQuantity")).doubleValue();
				tempInventoryEventPlanned.put("eventQuantity", new Double(qties));
				try {
					tempInventoryEventPlanned.store();
				} catch (GenericEntityException e) {
					Debug.logError(e,"Error : InventoryEventPlanned.store()", module);
					Debug.logError(e,"Error : parameters = "+tempInventoryEventPlanned, module);
					return ServiceUtil.returnError("Problem, we can not update the product in InventoryEventPlanned, for more detail look at the log");
				}					
			}
		}      
        
		Map result = new HashMap();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		Debug.logInfo("return from initInventoryEventPlanned", module);
		return result;
    }
    
	    
	/**
	* Create a List  with all the event of InventotyEventPlanned for one billOfMaterialLevel, sorted by productId and eventDate.
	* 
	* <li>INPUT : Parameter to get from the context : </li><ul>
	* <li>Integer billOfMaterialLevel : 0 for root for more detail see BomHelper.getMaxDepth</li></ul>
	* 
	* <li>OUTPUT : Result to put in the map :</li><ul>
	* <li>List listInventoryEventForMRP : all the event of InventotyEventPlanned for one billOfMaterialLevel, sorted by productId and eventDate<br/>
	* @param ctx The DispatchContext that this service is operating in.
	* @param context Map containing the input parameters.
	* @return Map with the result of the service, the output parameters.
	*/
	public static Map listProductForMrp(DispatchContext ctx, Map context) {
		Debug.logInfo("listProductForMrp called", module);
        // read parameters from context
		GenericDelegator delegator = ctx.getDelegator();
		Security security = ctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
        Long billOfMaterialLevel = (Long) context.get("billOfMaterialLevel");
		
/*      TODO: security management  
		if (!security.hasEntityPermission(secEntity, secOperation, userLogin)) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, "You do not have permission to perform this operation for this party");
			return partyId;
		} */
		// Find all products in MrpInventoryEventPlanned, ordered by bom and eventDate
        List listResult = null;
		Map parameters = UtilMisc.toMap("billOfMaterialLevel", billOfMaterialLevel);
        // TODO : If billOfMaterialLevel == 0 the search must be done with (billOfMaterialLevel == 0 || billOfMaterialLevel == null)
        List orderBy = UtilMisc.toList("productId","eventDate");
		try{
			listResult = delegator.findByAnd("MrpInventoryEventPlanned", parameters, orderBy);
		} catch(GenericEntityException e) {
			Debug.logError(e, "Error : delegator.findByAnd(\"MrpInventoryEventPlanned\", parameters)", module);
			Debug.logError(e, "Error : parameters = "+parameters,module);
			Debug.logError(e, "Error : orderBy = "+orderBy,module);
			return ServiceUtil.returnError("Problem, we can not find the products, for more detail look at the log");
		}
		Map result = new HashMap();
		result.put("listInventoryEventForMrp",listResult);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		Debug.logInfo("return from listProductForMrp "+billOfMaterialLevel, module);
		return result;
	}
	
	/**
	* Find the quantity on hand of products for MRP.
	* <li>PreConditions :	none</li>
	* <li>Result : We get the quantity of product available in the stocks.</li>
	* 
	* @param product the product for which the Quantity Available is required
	* @return the sum of all the totalAvailableToPromise of the inventoryItem related to the product, if the related facility is Mrp available (not yet implemented!!)
	*/
    public static double findProductMrpQoh(GenericValue product) {
        Debug.logInfo("findProductMrpQoh called", module);
         
        /*
         * TODO : verify if the facility is really MRP available !!!!!!!!!!!!!!! 
         */
        List productInventoryItems = null;
        /*
         * Lists all stocks where the product is defined
         */
        List orderBy = UtilMisc.toList("facilityId", "-receivedDate", "-inventoryItemId");
        try{
            productInventoryItems = product.getRelated("InventoryItem", null, orderBy);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error : product.getRelated(\"InventoryItem\")", module);
            Debug.logError(e, "Error : orderBy = "+orderBy,module);
            return 0;
        }
        //loop through the inventory items and get totals of available to promise per facility  
        Iterator productInventoryItemIter = productInventoryItems.iterator();
        double totalAvailableToPromise = 0.0;
        while (productInventoryItemIter.hasNext()) {
            GenericValue productInventoryItem = (GenericValue) productInventoryItemIter.next();
            GenericValue facility = null;
            try{
                facility = productInventoryItem.getRelatedOneCache("Facility");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error : productInventoryItem.getRelatedCache(\"Facility\")", module);
                return 0;
            }
            if(facility!=null){
                if ("SERIALIZED_INV_ITEM".equals(productInventoryItem.getString("inventoryItemTypeId"))) {
                    if ("INV_PROMISED".equals(productInventoryItem.getString("statusId"))) {
                        totalAvailableToPromise += 1;
                    }
                } else if ("NON_SERIAL_INV_ITEM".equals(productInventoryItem.getString("inventoryItemTypeId"))) {
                    if (productInventoryItem.get("availableToPromise") != null) {
                        totalAvailableToPromise += productInventoryItem.getDouble("availableToPromise").doubleValue();
                    }
                }
            }
        }
        Debug.logInfo("return from findProductMrpQoh "+product.getString("productId"), module);
        return totalAvailableToPromise;
    }   

    /**
    * Process the bill of material (bom) of the product  to insert components in the InventoryEventPlanned table.
    *   Before inserting in the entity, test if there is the record already existing to add quantity rather to create a new one.
    * 
    * @param product 
    * @param eventQuantity the product quantity needed
    *  @param startDate the startDate of the productionRun which will used to produce the product 
    *  @param routingTaskStartDate Map with all the routingTask as keys and startDate of each of them
    * @return None
    */
    public static void processBomComponent(GenericValue product, double eventQuantity, Timestamp startDate, Map routingTaskStartDate) {
       // TODO : change the return type to boolean to be able to test if all is ok or if it have had a exception
        Debug.logInfo("processBomComponent called", module);
        GenericDelegator delegator = product.getDelegator();
        // read the Product Manufacturing Components list
        List listComponent = null;
        try {
            listComponent = product.getRelatedByAndCache("MainProductAssoc",UtilMisc.toMap("productAssocTypeId", "MANUF_COMPONENT"));
        } catch (GenericEntityException e) {
            Debug.logError("Error : product.getRelatedByAndCache components productId ="+product.getString("productId")+"--"+e.getMessage(), module);
        }
        if (listComponent != null && listComponent.size() >0) {
            Iterator listComponentIter = listComponent.iterator();
            while (listComponentIter.hasNext()) {
                GenericValue productComponent = (GenericValue) listComponentIter.next();
                // read the startDate for the component
                String routingTask = productComponent.getString("routingWorkEffortId");
                Timestamp eventDate = (routingTask == null || !routingTaskStartDate.containsKey(routingTask)) ? startDate : (Timestamp) routingTaskStartDate.get(routingTask);
                // if the components is valid at the event Date create the Mrp requirement in the InventoryEventPlanned entity
                if (EntityUtil.isValueActive(productComponent, eventDate)) {
                    Map parameters = UtilMisc.toMap("productId", productComponent.getString("productIdTo"));
                    parameters.put("eventDate",eventDate);
                    parameters.put("inventoryEventPlanTypeId", "MRP_REQUIREMENT");
                    double scrapFactor = (productComponent.get("scrapFactor") != null)? productComponent.getDouble("scrapFactor").doubleValue() : 0;
                    double componentEventQuantity = Math.floor((productComponent.getDouble("quantity").doubleValue() * eventQuantity / (1-(scrapFactor / 100))) + 0.5);
                    //look if the product is already in the table or not
                    GenericValue mrpInventoryEventPlanned = null;
                    try {
                        mrpInventoryEventPlanned = delegator.findByPrimaryKey("InventoryEventPlanned", parameters);
                    } catch (GenericEntityException e) {
                        Debug.logError("Error : delegator.findByPrimaryKey(\"InventoryEventPlanned\", parameters) ="+parameters+"--"+e.getMessage(), module);
                    }
                    if(mrpInventoryEventPlanned == null){
                        //the product is not in the table for this date so we insert it
                        mrpInventoryEventPlanned = delegator.makeValue("InventoryEventPlanned", parameters);
                        mrpInventoryEventPlanned.put("eventQuantity", new Double(-1.0*componentEventQuantity));
                        try {
                            Debug.logInfo("MIEP creation : productId = "+productComponent.getString("productIdTo")+" startDate = "+startDate.toString()+" eventQuantity="+Double.toString(componentEventQuantity),module);
                            mrpInventoryEventPlanned.create();
                        } catch (GenericEntityException e) {
                            Debug.logError("Error : InventoryEventPlanned.create() parameters = "+mrpInventoryEventPlanned+"--"+e.getMessage(), module);
                        }
                    }
                    else{
                        // the product is already in the table at this date, so we update it, adding the componentEventQuantity
                        Double eventQuantityTmp = new Double(componentEventQuantity - (mrpInventoryEventPlanned.getDouble("eventQuantity")).doubleValue());
                        mrpInventoryEventPlanned.put("eventQuantity", eventQuantityTmp);
                        try {
                            Debug.logInfo("MIEP update : productId = "+product.getString("productId")+" startDate = "+startDate.toString()+" eventQuantity="+Double.toString(componentEventQuantity),module);
                            mrpInventoryEventPlanned.store();
                        } catch (GenericEntityException e) {
                            Debug.logError("Error : InventoryEventPlanned.store() parameters = "+mrpInventoryEventPlanned+"--"+e.getMessage(), module);
                        }                   
                    }
                }
            }
        }
        return;
    }       

	

    /**
     * Launch the MRP.
     * <li>PreConditions : none</li>
     * <li>Result : The date when we must order or begin to build the products and subproducts we need are calclated</li>
     * 
     * <li>INPUT : parameters to get from the context :</li><ul> 
     * <li>String timePeriod</li></ul>
     * 
     * <li>OUTPUT : Result to put in the map :</li><ul> 
     * <li>none</li></ul>
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters, productId routingId, quantity, startDate.
     * @return Map with the result of the service, the output parameters.
     */    
    public static Map runningMrp(DispatchContext ctx, Map context) {
        Debug.logInfo("runningMrp called", module);
        //Context
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();        
        Security security = ctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");   
        String timePeriod = (String) context.get("timePeriod");
        /* TODO: security management  
        if (!security.hasEntityPermission(secEntity, secOperation, userLogin)) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "You do not have permission to perform this operation for this party");
            return partyId;
        } */
        // Variable declaration
        int bomLevelWithNoEvent = 0;
        double stockTmp = 0;
        String oldProductId = null;
        String productId = null;
        GenericValue product = null;
        double eventQuantity = 0;
        Timestamp eventDate = null;
        boolean isNegative = false;
        double quantityNeeded = 0;
        
        Map result = null;
        Map parameters = null;
        List listInventoryEventForMRP = null;
        ListIterator iteratorListInventoryEventForMRP = null;
        GenericValue inventoryEventForMRP = null;

         // Initialisation of the InventoryEventPlanned table, This table will contain the products we want to buy or build. 
        parameters = UtilMisc.toMap("reInitialize",new Boolean(true),"userLogin", userLogin);
        try {
            result = dispatcher.runSync("initInventoryEventPlanned", parameters);
        } catch (GenericServiceException e) {
            Debug.logError("Error : initInventoryEventPlanned", module);
            Debug.logError("Error : parameters = "+parameters,module);
            return ServiceUtil.returnError("Problem, can not initialise the table InventoryEventPlanned, for more detail look at the log");
        }
//      TODO : modifier le jeux d'essai de TGR pour mettre 0 au niveau pf
        long bomLevel = 0;
        // iteration for the bomLevel for which there are some events
        do  {
            //get the products from the InventoryEventPlanned table for the current billOfMaterialLevel (ie. BOM)
            parameters = UtilMisc.toMap("billOfMaterialLevel",new Long(bomLevel),"userLogin", userLogin);
            try {
                result = dispatcher.runSync("listProductForMrp", parameters);
            } catch (GenericServiceException e) {
                Debug.logError("Error : listProductForMrp, parameters ="+parameters, module);
                return ServiceUtil.returnError("Problem, can not list the products for the MRP, for more detail look at the log");
            }
            listInventoryEventForMRP = (List) result.get("listInventoryEventForMrp");
            if (listInventoryEventForMRP != null && listInventoryEventForMRP.size()>0) {
                iteratorListInventoryEventForMRP = listInventoryEventForMRP.listIterator();
                inventoryEventForMRP = (GenericValue) iteratorListInventoryEventForMRP.next();
                oldProductId = (String) inventoryEventForMRP.getString("productId");
                try {
                    product = inventoryEventForMRP.getRelatedOneCache("Product");
                } catch (GenericEntityException e) {
                    Debug.logError("Error : getRelatedOneCache Produc with productId="+productId+"--"+e.getMessage(), module);
                    return ServiceUtil.returnError("Problem, can not find the product for a event, for more detail look at the log");
                }
                // read the MrpQoh : (Quantity available in stock for the MRP ) for the first item
                stockTmp = findProductMrpQoh(product);
                // iterate for all the event in the listInventoryEventForMRP
                do  {
                    productId = (String) inventoryEventForMRP.getString("productId");
                    if (! productId.equals(oldProductId)) {
                        // It's a new product, so it's necessary to  read the MrpQoh
                        try {
                            product = inventoryEventForMRP.getRelatedOneCache("Product");
                        } catch (GenericEntityException e) {
                            Debug.logError("Error : getRelatedOneCache Produc with productId="+productId+"--"+e.getMessage(), module);
                            return ServiceUtil.returnError("Problem, can not find the product for a event, for more detail look at the log");
                        }
                        stockTmp = findProductMrpQoh(product);
                        oldProductId = productId;
                    }
                    eventQuantity = inventoryEventForMRP.getDouble("eventQuantity").doubleValue();
                    // test if the event is a in or out stock movement, "-" is an out. 
                    if (eventQuantity < 0) {
                        quantityNeeded = (eventQuantity * -1.0) - stockTmp ;
                        Debug.logInfo("quantityNeeded = "+Double.toString(quantityNeeded)+" eventQuantity = "+Double.toString(eventQuantity)+" stockTmp = "+ Double.toString(stockTmp)
                                                    + " productId = "+productId, module);
                       
                        if(quantityNeeded > 0){    //need to buy or build the product as we have not enough stock
                            eventDate = inventoryEventForMRP.getTimestamp("eventDate");
                            // to be just before the requirement
                            eventDate.setTime(eventDate.getTime()-1);
                            boolean isbuild = ProductHelper.isBuild(product);
                            Debug.logInfo("retour isBuild =" + ((isbuild)?"true":"false"),module);
                            ProposedOrder proposedOrder = new ProposedOrder(product, isbuild, eventDate ,quantityNeeded);
                            // calculate the ProposedOrder quantity and update the quantity object property.
                            proposedOrder.calculateQuantityToSupply(iteratorListInventoryEventForMRP);
                            // calculate the ProposedOrder requirementStartDate and update the requirementStartDate object property.
                            Map routingTaskStartDate = proposedOrder.calculateStartDate();
                            if (isbuild) {
                                // process the product components
                                processBomComponent(product, proposedOrder.getQuantity(), proposedOrder.getRequirementStartDate(), routingTaskStartDate);
                            }
                            // create the  ProposedOrder and the InventoryEventPlanned associated (see ECA)
                            String proposedOrderId = proposedOrder.create(ctx, userLogin);
                            stockTmp = stockTmp + eventQuantity + proposedOrder.getQuantity();
                        }  // there is enought stock 
                        else stockTmp = stockTmp + eventQuantity;
                    }
                     //  it's an in stock movement
                     else stockTmp = stockTmp + eventQuantity;
                    // go to the next event
                    if (iteratorListInventoryEventForMRP.hasNext()) {
                        inventoryEventForMRP = (GenericValue) iteratorListInventoryEventForMRP.next();
                    } else break;
                } while (true);
                bomLevel +=1;
            }
            // test if it's the end of loop of all the bomLevel
            if (listInventoryEventForMRP == null || listInventoryEventForMRP.size()==0) {
                // if there is 3 level with no inventoryEvenPanned the loop is stop 
                bomLevelWithNoEvent +=1;
                if (bomLevelWithNoEvent == 3) break;
            } else  bomLevelWithNoEvent = 0;
        } while (true);    
        result =  new HashMap();
        List msgResult = new LinkedList();
        result.put("msgResult",msgResult);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        Debug.logInfo("return from runningMrp", module);
        // coucou
        return result;
    }
}
