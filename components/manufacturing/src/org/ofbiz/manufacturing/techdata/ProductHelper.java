/*
 * $Id$
 *
 * Copyright (c)  2003, 2004 The Open For Business Project - www.ofbiz.org
 * Copyright (c)  2003, 2004 �cole Polytechnique de l'Universit� de Tours, D�partement Informatique - www.univ-tours.fr
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

package org.ofbiz.manufacturing.techdata;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.lang.*;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.manufacturing.mrp.MrpServices;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;


/**
 * Method to retrieve some manufacturing Product Information
 *
 * @author     <a href="mailto:olivier.heintz@nereide.biz">Olivier Heintz</a>
 * @author     <a href="mailto:nicolas@librenberry.net">Nicolas MALIN</a>
 * @author     <a href=mailto:tgrauss@free.fr">Thierry GRAUSS</a>
 * @version    $Rev$
 * @since      3.0
 */
public class ProductHelper {
    
    public static final String module = ProductHelper.class.getName();
    public static final String resource = "ManufacturingUiLabels";
    
    
    /**
     * Get the routing object for a product.
     * @param product : the product for which the method return the routing
     * @param quantity : the quantity to build
     * @param evenDate : the date used to filter the active routing
     * @return GenericValue routing : the routing object
     **/
    public static GenericValue getRouting(GenericValue product, double quantity, Timestamp eventDate, LocalDispatcher dispatcher){
        Debug.logInfo("getRouting called", module);
        
        //Looks for the routing associated with the product
        List listRouting = null;
        try{
            listRouting = product.getRelated("WorkEffortGoodStandard",UtilMisc.toMap("statusId", "ROU_PROD_TEMPLATE"),UtilMisc.toList("estimatedQuantity DESC"));
            if (listRouting.size()>0) listRouting = EntityUtil.filterByDate(listRouting,eventDate);
        } catch (GenericEntityException e) {
            Debug.logError(e,"Error : product.getRelated routing... productId="+product.getString("productId"), module);
            return null;
        }
        if (listRouting == null || listRouting.size()==0) {
            // if the product is a configuration, probably the routing is linked to the virtual product
            Map serviceResponse = null;
            try {
                serviceResponse = dispatcher.runSync("getManufacturingComponents", UtilMisc.toMap("productId", product.getString("productId"), "quantity", new Double(quantity)));
            } catch (GenericServiceException e) {
                Debug.logError("Error : getManufacturingComponents for productId ="+product.getString("productId")+"--"+e.getMessage(), module);
                return null;
            }
            String routingId = (String)serviceResponse.get("workEffortId");
            try{
                return product.getDelegator().findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", routingId));
            } catch (GenericEntityException e) {
                return null;
            }
        }
        Iterator listRoutingIter = listRouting.iterator();
        boolean found = false;
        GenericValue routingProduct = null;
        //Looks to determine which routing has a valid quantity
        while (listRoutingIter.hasNext() && !found) {
            routingProduct = (GenericValue) listRoutingIter.next();
            if (routingProduct.getDouble("estimatedQuantity")==null ||
            routingProduct.getDouble("estimatedQuantity").doubleValue() < quantity) found = true;
        }
        try{
            return routingProduct.getRelatedOneCache("WorkEffort");
        } catch (GenericEntityException e) {
            Debug.logError(e,"Error : routingProduct.getRelated routing... workEffortId="+routingProduct.getString("workEffortId")+" productId="+routingProduct.getString("productId"), module);
            return null;
        }
    }
    
    
    /**
     * test to know if the product is build or bought
     * @param GenericValue product to test.
     * @return <ul>
     * <li>return true if the product is build</li>
     * <li>return false if the product is bought</li></ul>
     */
    public static boolean isBuild(GenericValue product) {
        Debug.logInfo("isBuild called", module);
                /*
                 * TODO : we should ameliorate this. For the moment a component which has no subcomponent
                 * is always bought. In the real life, this is not always true (for example coal, gold, wood, and raw materials
                 * in general).
                 */
        //look for the list of subcomponents of the product
        try{
            List listBom = product.getRelatedByAndCache("MainProductAssoc",UtilMisc.toMap("productAssocTypeId","MANUF_COMPONENT"));
            if (listBom.size()>0) listBom = EntityUtil.filterByDate(listBom);
            if (listBom.size()>0) return true;
            else return false;
        } catch (GenericEntityException e) {
            Debug.logError("Error : listBomComponent", module);
            return false;
        }
    }
    
    /**
     * Calcul the ATP Date of a list d'objet inventoryEventPlan
     * @param List The list of inventoryEventPlan that transmit to ftl
     * @return List of Double represant ATPDate
     */
    public static List getVariationProduct(List inventoryList, LocalDispatcher dispatcher){
        
        Debug.logInfo("coucou de la methode", module);
        ArrayList inventoryProductList;
        Map inventoryProductMap = new TreeMap();
        Map cumulativeAtpByEventMap = new TreeMap();
        GenericValue inventoryTmp;
        boolean firstOfList = true;
        
        //regroupement des inventorys en fonction de l'article
        Iterator iter = inventoryList.iterator();
        while( iter.hasNext() ){
            inventoryTmp = (GenericValue) iter.next();
            inventoryProductList = (ArrayList)inventoryProductMap.get( inventoryTmp.getString("productId") );
            if( inventoryProductList == null ){
                inventoryProductList = new ArrayList();
                inventoryProductMap.put( inventoryTmp.getString("productId"), inventoryProductList);
            }
            inventoryProductList.add( inventoryTmp );
        }
        
        // iteration on the product found
        ArrayList keys = new ArrayList( inventoryProductMap.keySet() );
        for (Iterator iterMap = keys.iterator(); iterMap.hasNext(); ){
            String productId = (String) iterMap.next();
            inventoryProductList = (ArrayList)  inventoryProductMap.get(productId);
            double productAtp = 0;
            for (iter = inventoryProductList.iterator();iter.hasNext();){
                //Acumulate all the InventoryEventPlanned.quantity
                inventoryTmp = (GenericValue)iter.next();
                if (firstOfList){
                    //Intinial ATP equal to the current product QOH
                    try {
                        GenericValue product = inventoryTmp.getRelatedOneCache("Product");
                        productAtp =  MrpServices.findProductMrpQoh(product, dispatcher);
                    } catch (Exception e) {
                        Debug.logError("Error : getRelatedOneCache Produc with productId="+inventoryTmp.getString("productId")+"--"+e.getMessage(), module);
                        return null;
                    }
                    firstOfList = false;
                }
                Double doubleTmp = (Double)inventoryTmp.getDouble("eventQuantity");
                productAtp += doubleTmp.doubleValue();
                cumulativeAtpByEventMap.put( inventoryTmp, new Double(productAtp) );
            }
            firstOfList = true;
        }
        
        //construct the return list
        List eventPlannedAndCumulativeAtp = new LinkedList();
        iter = inventoryList.iterator();
        while( iter.hasNext() ){
            inventoryTmp = (GenericValue)iter.next();
            Double productAtp = (Double) cumulativeAtpByEventMap.get(inventoryTmp);
            eventPlannedAndCumulativeAtp.add( productAtp );
        }
        return eventPlannedAndCumulativeAtp;
        
    }
}
