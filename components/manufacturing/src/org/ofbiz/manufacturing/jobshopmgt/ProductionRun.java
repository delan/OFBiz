/*
 * $Id$
 *
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 * Copyright (c) 2004 Nereide - www.nereide.biz
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

package org.ofbiz.manufacturing.jobshopmgt;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.manufacturing.techdata.TechDataServices;
import org.ofbiz.service.GenericServiceException;


/**
 * ProductionRun Object used by the Jobshop management OFBiz comonents,
 * 	this object is used to find or updated an existing ProductionRun.
 *
 * @author     <a href="mailto:olivier.heintz@nereide.biz">Olivier Heintz</a>
 * @version    $Rev$
 * @since      3.0
 */
public class ProductionRun {
    
    public static final String module = ProductionRun.class.getName();
    public static final String resource = "ManufacturingUiLabels";
    
    protected GenericValue productionRun; // WorkEffort (PROD_ORDER_HEADER)
    protected GenericValue productionRunProduct; // WorkEffortGoodStandard (WIP_OUTGOING_FULFIL)
    protected GenericValue productProduced; // Product (from WorkEffortGoodStandard WIP_OUTGOING_FILFIL)
    protected Double quantity; // the estimatedQuantity
    
    protected Timestamp estimatedStartDate;
    protected Timestamp estimatedCompletionDate;
    protected String productionRunName;
    protected String description;
    protected GenericValue currentStatus;
    protected List productionRunComponents;
    protected List productionRunRoutingTasks;
    
    /**
     * indicate if quantity or estimatedStartDate has been modified and
     *  estimatedCompletionDate not yet recalculated with recalculateEstimatedCompletionDate() methode.
     */
    private boolean updateCompletionDate = false;
    /**
     * indicate if quantity  has been modified, used for store() method to update appropriate entity.
     */
    private boolean quantityIsUpdated = false;
    
    
    public ProductionRun(GenericDelegator delegator, String productionRunId) {
        try {
            if (! UtilValidate.isEmpty(productionRunId)) {
                this.productionRun = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", productionRunId));
                if (exist()) {
                    this.estimatedStartDate = productionRun.getTimestamp("estimatedStartDate");
                    this.estimatedCompletionDate = productionRun.getTimestamp("estimatedCompletionDate");
                    this.productionRunName = productionRun.getString("workEffortName");
                    this.description = productionRun.getString("description");
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }
    }
    
    /**
     * test if the productionRun exist.
     * @return true if it exist false otherwise.
     **/
    public boolean exist(){
        return productionRun != null;
    }
    
    /**
     * get the ProductionRun GenericValue .
     * @return the ProductionRun GenericValue
     **/
    public GenericValue getGenericValue(){
        return productionRun;
    }
    /**
     * store  the modified ProductionRun object in the database.
     *     <li>store the the productionRun header
     *     <li> the productProduced related data
     *     <li> the listRoutingTask related data
     *     <li> the productComponent list related data
     * @return true if success false otherwise
     **/
    public boolean store(){
        if (exist()){
            if (updateCompletionDate){
                this.estimatedCompletionDate = recalculateEstimatedCompletionDate();
            }
            productionRun.set("estimatedStartDate",this.estimatedStartDate);
            productionRun.set("estimatedCompletionDate",this.estimatedCompletionDate);
            productionRun.set("workEffortName",this.productionRunName);
            productionRun.set("description",this.description);
            try {
                productionRun.store();
                if (quantityIsUpdated) {
                    productionRunProduct.set("estimatedQuantity",this.quantity);
                    productionRunProduct.store();
                    quantityIsUpdated = false;
                }
                if (productionRunRoutingTasks != null) {
                    for (Iterator iter = productionRunRoutingTasks.iterator(); iter.hasNext();){
                        GenericValue routingTask = (GenericValue) iter.next();
                        routingTask.store();
                    }
                }
                if (productionRunComponents != null) {
                    for (Iterator iter = productionRunComponents.iterator(); iter.hasNext();){
                        GenericValue component = (GenericValue) iter.next();
                        component.store();
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                return false;
            }
            return true;
        }
        return false;
    }
    
    /**
     * get the Product GenericValue corresponding to the productProduced.
     *     In the same time this method read the quantity property from SGBD
     * @return the productProduced related object
     **/
    public GenericValue getProductProduced(){
        if (exist()) {
            if (productProduced == null) {
                try {
                    List productionRunProducts = productionRun.getRelated("WorkEffortGoodStandard", UtilMisc.toMap("statusId", "WIP_OUTGOING_FULFIL"),null);
                    this.productionRunProduct = EntityUtil.getFirst(productionRunProducts);
                    quantity = productionRunProduct.getDouble("estimatedQuantity");
                    productProduced = productionRunProduct.getRelatedOneCache("Product");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e.getMessage(), module);
                }
            }
            return productProduced;
        }
        return null;
    }
    
    /**
     * get the quantity property.
     * @return the quantity property
     **/
    public Double getQuantity(){
        if (exist()) {
            if (quantity == null)  getProductProduced();
            return quantity;
        }
        else return null;
    }
    /**
     * set the quantity property and recalculated the productComponent quantity.
     * @return
     **/
    public void setQuantity(Double newQuantity) {
        if (quantity == null) getProductProduced();
        double previousQuantity = quantity.doubleValue(), componentQuantity;
        this.quantity = newQuantity;
        this.quantityIsUpdated = true;
        this.updateCompletionDate = true;
        if (productionRunComponents == null) getProductionRunComponents();
        for (Iterator iter = productionRunComponents.iterator(); iter.hasNext();){
            GenericValue component = (GenericValue) iter.next();
            componentQuantity = component.getDouble("estimatedQuantity").doubleValue();
            component.set("estimatedQuantity", new Double(Math.floor((componentQuantity / previousQuantity * newQuantity.doubleValue() ) + 0.5)));
        }
        return;
    }
    /**
     * get the estimatedStartDate property.
     * @return the estimatedStartDate property
     **/
    public Timestamp getEstimatedStartDate(){
        return (exist()? this.estimatedStartDate: null);
    }
    /**
     * set the estimatedStartDate property.
     * @return
     **/
    public void setEstimatedStartDate(Timestamp estimatedStartDate){
        this.estimatedStartDate = estimatedStartDate;
        this.updateCompletionDate = true;
    }
    /**
     * get the estimatedCompletionDate property.
     * @return the estimatedCompletionDate property
     **/
    public Timestamp getEstimatedCompletionDate(){
        if (exist()) {
            if (updateCompletionDate) {
                this.estimatedCompletionDate = recalculateEstimatedCompletionDate();
            }
            return this.estimatedCompletionDate;
        }
        else return null;
    }
    /**
     * set the estimatedCompletionDate property without any control or calculation.
     * usage productionRun.setEstimatedCompletionDate(productionRun.recalculateEstimatedCompletionDate(priority);
     * @return
     **/
    public void setEstimatedCompletionDate(Timestamp estimatedCompletionDate){
        this.estimatedCompletionDate = estimatedCompletionDate;
    }
    /**
     * recalculated  the estimatedCompletionDate property.
     *     Use the quantity and the estimatedStartDate properties as entries parameters.
     *     <br>read the listRoutingTask and for each recalculated and update the estimatedStart and endDate in the object.
     *     <br> no store in the database is done.
     * @param priority give the routingTask start point to recalculated
     * @return the estimatedCompletionDate calculated
     **/
    public Timestamp recalculateEstimatedCompletionDate(Long priority, Timestamp startDate){
        if (exist()) {
            getProductionRunRoutingTasks();
            if (quantity == null) getQuantity();
            Timestamp endDate=null;
            for (Iterator iter=productionRunRoutingTasks.iterator(); iter.hasNext();){
                GenericValue routingTask = (GenericValue) iter.next();
                if (priority.compareTo(routingTask.getLong("priority")) <= 0){
                    // Calculate the estimatedCompletionDate
                    long duringTime = (long)(routingTask.getDouble("estimatedSetupMillis").doubleValue() + (routingTask.getDouble("estimatedMilliSeconds").doubleValue() * quantity.doubleValue()));
                    endDate = TechDataServices.addForward(TechDataServices.getTechDataCalendar(routingTask),startDate, duringTime);
                    // update the routingTask
                    routingTask.set("estimatedStartDate",startDate);
                    routingTask.set("estimatedCompletionDate",endDate);
                    startDate = endDate;
                }
            }
            return endDate;
        } else {
            return null;
        }
    }
    /**
     * call recalculateEstimatedCompletionDate(0,estimatedStartDate), so recalculated for all the routingtask.
     */
    public Timestamp recalculateEstimatedCompletionDate(){
        this.updateCompletionDate = false;
        return recalculateEstimatedCompletionDate(new Long(0), estimatedStartDate);
    }
    /**
     * get the productionRunName property.
     * @return the productionRunName property
     **/
    public String getProductionRunName(){
        if (exist()) return this.productionRunName;
        else return null;
    }
    public  void setProductionRunName(String name){
        this.productionRunName = name;
    }
    /**
     * get the description property.
     * @return the description property
     **/
    public String getDescription(){
        if (exist()) return productionRun.getString("description");
        else return null;
    }
    public void setDescription(String description){
        this.description = description;
    }
    /**
     * get the GenericValue currentStatus.
     * @return the currentStatus related object
     **/
    public GenericValue getCurrentStatus(){
        if (exist()) {
            if (currentStatus == null) {
                try {
                    currentStatus = productionRun.getRelatedOneCache("StatusItem");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e.getMessage(), module);
                }
            }
            return currentStatus;
        }
        return null;
    }
    /**
     * get the list of all the productionRunComponents as a list of GenericValue.
     * @return the productionRunComponents related object
     **/
    public List getProductionRunComponents(){
        if (exist()) {
            if (productionRunComponents == null) {
                if (productionRunRoutingTasks == null)  this.getProductionRunRoutingTasks();
                if (productionRunRoutingTasks != null) {
                    try {
                        productionRunComponents = new LinkedList();
                        GenericValue routingTask;
                        for (Iterator iter=productionRunRoutingTasks.iterator(); iter.hasNext();) {
                            routingTask = (GenericValue)iter.next();
                            productionRunComponents.addAll(routingTask.getRelated("WorkEffortGoodStandard", UtilMisc.toMap("statusId", "WIP_INCOMING_FULFIL"),null));
                            productionRunComponents.addAll(routingTask.getRelated("WorkEffortGoodStandard", UtilMisc.toMap("statusId", "WIP_INCOMING_DONE"),null));
                        }
                    } catch (GenericEntityException e) {
                        Debug.logWarning(e.getMessage(), module);
                    }
                }
            }
            return productionRunComponents;
        }
        return null;
    }
    /**
     * get the list of all the productionRunRoutingTasks as a list of GenericValue.
     * @return the productionRunRoutingTasks related object
     **/
    public List getProductionRunRoutingTasks(){
        if (exist()) {
            if (productionRunRoutingTasks == null) {
                try {
                    productionRunRoutingTasks = productionRun.getRelated("ChildWorkEffort",UtilMisc.toMap("workEffortTypeId","PROD_ORDER_TASK"),UtilMisc.toList("priority"));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e.getMessage(), module);
                }
            }
            return productionRunRoutingTasks;
        }
        return null;
    }
    
    /**
     * get the list of all the productionRunRoutingTasks as a list of GenericValue.
     * @return the productionRunRoutingTasks related object
     **/
    public GenericValue getLastProductionRunRoutingTask(){
        if (exist()) {
            if (productionRunRoutingTasks == null) {
                try {
                    productionRunRoutingTasks = productionRun.getRelated("ChildWorkEffort",UtilMisc.toMap("workEffortTypeId","PROD_ORDER_TASK"),UtilMisc.toList("priority"));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e.getMessage(), module);
                }
            }
            return (GenericValue)(productionRunRoutingTasks != null && productionRunRoutingTasks.size() > 0? productionRunRoutingTasks.get(productionRunRoutingTasks.size() - 1): null);
        }
        return null;
    }

    /**
     * clear list of all the productionRunRoutingTasks to force re-reading at the next need.
     * This methode is used when the routingTasks ordering is changed.
     * @return
     **/
    public void clearRoutingTasksList(){
        this.productionRunRoutingTasks = null;
    }
    
}
