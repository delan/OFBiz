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
package org.ofbiz.shipment.packing;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class PackingSession implements java.io.Serializable {

    public static final String module = PackingSession.class.getName();

    protected GenericValue userLogin = null;
    protected String pickerPartyId = null;
    protected String primaryOrderId = null;
    protected String primaryShipGrp = null;
    protected String dispatcherName = null;
    protected String delegatorName = null;
    protected String picklistBinId = null;
    protected String facilityId = null;
    protected String shipmentId = null;
    protected String instructions = null;
    protected String dimensionUomId = null;
    protected String weightUomId = null;
    protected String invoiceId = null;
    protected BigDecimal additionalShippingCharge = null;
    protected Map<Integer, BigDecimal> packageWeights = null;
    protected List<PackingEvent> packEvents = null;
    protected List<PackingSessionLine> packLines = null;
    protected List<ItemDisplay> itemInfos = null;
    protected int packageSeq = -1;
    protected int status = 1;

    private transient GenericDelegator _delegator = null;
    private transient LocalDispatcher _dispatcher = null;
    private static BigDecimal ZERO = BigDecimal.ZERO;

    public PackingSession(LocalDispatcher dispatcher, GenericValue userLogin, String facilityId, String binId, String orderId, String shipGrp) {
        this._dispatcher = dispatcher;
        this.dispatcherName = dispatcher.getName();

        this._delegator = _dispatcher.getDelegator();
        this.delegatorName = _delegator.getDelegatorName();

        this.primaryOrderId = orderId;
        this.primaryShipGrp = shipGrp;
        this.picklistBinId = binId;
        this.userLogin = userLogin;
        this.facilityId = facilityId;
        this.packLines = FastList.newInstance();
        this.packEvents = FastList.newInstance();
        this.itemInfos = FastList.newInstance();
        this.packageSeq = 1;
        this.packageWeights = FastMap.newInstance();
    }

    public PackingSession(LocalDispatcher dispatcher, GenericValue userLogin, String facilityId) {
        this(dispatcher, userLogin, facilityId, null, null, null);
    }

    public PackingSession(LocalDispatcher dispatcher, GenericValue userLogin) {
        this(dispatcher, userLogin, null, null, null, null);
    }

    public void addOrIncreaseLine(String orderId, String orderItemSeqId, String shipGroupSeqId, String productId, BigDecimal quantity, int packageSeqId, BigDecimal weight, boolean update) throws GeneralException {
        // reset the session if we just completed
        if (status == 0) {
            throw new GeneralException("Packing session has been completed; be sure to CLEAR before packing a new order! [000]");
        }

        // do nothing if we are trying to add a quantity of 0
        if (!update && quantity.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        // find the actual product ID
        productId = ProductWorker.findProductId(this.getDelegator(), productId);

        // set the default null values - primary is the assumed first item
        if (orderId == null) {
            orderId = primaryOrderId;
        }
        if (shipGroupSeqId == null) {
            shipGroupSeqId = primaryShipGrp;
        }
        if (orderItemSeqId == null && productId != null) {
            orderItemSeqId = this.findOrderItemSeqId(productId, orderId, shipGroupSeqId, quantity);
        }

        // get the reservations for the item
        Map<String, Object> invLookup = FastMap.newInstance();
        invLookup.put("orderId", orderId);
        invLookup.put("orderItemSeqId", orderItemSeqId);
        invLookup.put("shipGroupSeqId", shipGroupSeqId);
        List<GenericValue> reservations = this.getDelegator().findByAnd("ItemIssuance", invLookup, UtilMisc.toList("quantity DESC"));

        // no reservations we cannot add this item
        if (UtilValidate.isEmpty(reservations)) {
            throw new GeneralException("No inventory reservations available; cannot pack this item! [101]");
        }

        // find the inventoryItemId to use
        if (reservations.size() == 1) {
            GenericValue res = EntityUtil.getFirst(reservations);
            int checkCode = this.checkLineForAdd(res, orderId, orderItemSeqId, shipGroupSeqId, productId, quantity, packageSeqId, update);
            this.createPackLineItem(checkCode, res, orderId, orderItemSeqId, shipGroupSeqId, productId, quantity, weight, packageSeqId);
        } else {
            // more than one reservation found
            Map<GenericValue, BigDecimal> toCreateMap = FastMap.newInstance();
            Iterator<GenericValue> i = reservations.iterator();
            BigDecimal qtyRemain = quantity;

            while (i.hasNext() && qtyRemain.compareTo(BigDecimal.ZERO) > 0) {
                GenericValue res = i.next();

                // Check that the inventory item product match with the current product to pack
                if (!productId.equals(res.getRelatedOne("InventoryItem").getString("productId"))) {
                    continue;
                }

                BigDecimal resQty = res.getBigDecimal("quantity");
                BigDecimal resPackedQty = this.getPackedQuantity(orderId, orderItemSeqId, shipGroupSeqId, productId, res.getString("inventoryItemId"), -1);
                if (resPackedQty.compareTo(resQty) >= 0) {
                    continue;
                } else if (!update) {
                    resQty = resQty.subtract(resPackedQty);
                }

                BigDecimal thisQty = resQty.compareTo(qtyRemain) > 0 ? qtyRemain : resQty;

                int thisCheck = this.checkLineForAdd(res, orderId, orderItemSeqId, shipGroupSeqId, productId, thisQty, packageSeqId, update);
                switch (thisCheck) {
                    case 2:
                        Debug.log("Packing check returned '2' - new pack line will be created!", module);
                        toCreateMap.put(res, thisQty);
                        qtyRemain = qtyRemain.subtract(thisQty);
                        break;
                    case 1:
                        Debug.log("Packing check returned '1' - existing pack line has been updated!", module);
                        qtyRemain = qtyRemain.subtract(thisQty);
                        break;
                    case 0:
                        Debug.log("Packing check returned '0' - doing nothing.", module);
                        break;
                }
            }

            if (qtyRemain.compareTo(BigDecimal.ZERO) == 0) {
                for (Map.Entry<GenericValue, BigDecimal> entry: toCreateMap.entrySet()) {
                    GenericValue res = entry.getKey();
                    BigDecimal qty = entry.getValue();
                    this.createPackLineItem(2, res, orderId, orderItemSeqId, shipGroupSeqId, productId, qty, weight, packageSeqId);
                }
            } else {
                throw new GeneralException("Not enough inventory reservation available; cannot pack the item! [103]");
            }
        }

        // run the add events
        this.runEvents(PackingEvent.EVENT_CODE_ADD);
    }

    public void addOrIncreaseLine(String orderId, String orderItemSeqId, String shipGroupSeqId, BigDecimal quantity, int packageSeqId) throws GeneralException {
        this.addOrIncreaseLine(orderId, orderItemSeqId, shipGroupSeqId, null, quantity, packageSeqId, BigDecimal.ZERO, false);
    }

    public void addOrIncreaseLine(String productId, BigDecimal quantity, int packageSeqId) throws GeneralException {
        this.addOrIncreaseLine(null, null, null, productId, quantity, packageSeqId, BigDecimal.ZERO, false);
    }

    public PackingSessionLine findLine(String orderId, String orderItemSeqId, String shipGroupSeqId, String productId, String inventoryItemId, int packageSeq) {
        for (PackingSessionLine line: this.getLines()) {
            if (orderId.equals(line.getOrderId()) &&
                    orderItemSeqId.equals(line.getOrderItemSeqId()) &&
                    shipGroupSeqId.equals(line.getShipGroupSeqId()) &&
                    productId.equals(line.getProductId()) &&
                    inventoryItemId.equals(line.getInventoryItemId()) &&
                    packageSeq == line.getPackageSeq()) {
                return line;
            }
        }
        return null;
    }

    protected void createPackLineItem(int checkCode, GenericValue res, String orderId, String orderItemSeqId, String shipGroupSeqId, String productId, BigDecimal quantity, BigDecimal weight, int packageSeqId) throws GeneralException {
        // process the result; add new item if necessary
        switch (checkCode) {
            case 0:
                // not enough reserved
                throw new GeneralException("Not enough inventory reservation available; cannot pack the item! [201]");
            case 1:
                // we're all good to go; quantity already updated
                break;
            case 2:
                // need to create a new item
                String invItemId = res.getString("inventoryItemId");
                packLines.add(new PackingSessionLine(orderId, orderItemSeqId, shipGroupSeqId, productId, invItemId, quantity, weight, packageSeqId));
                break;
        }

        // Add the line weight to the package weight
        if (weight.compareTo(BigDecimal.ZERO) > 0) this.addToPackageWeight(packageSeqId, weight);

        // update the package sequence
        if (packageSeqId > packageSeq) {
            this.packageSeq = packageSeqId;
        }
    }

    protected String findOrderItemSeqId(String productId, String orderId, String shipGroupSeqId, BigDecimal quantity) throws GeneralException {
        Map<String, Object> lookupMap = FastMap.newInstance();
        lookupMap.put("orderId", orderId);
        lookupMap.put("productId", productId);
        lookupMap.put("statusId", "ITEM_APPROVED");
        lookupMap.put("shipGroupSeqId", shipGroupSeqId);

        List<String> sort = UtilMisc.toList("-quantity");
        List<GenericValue> orderItems = this.getDelegator().findByAnd("OrderItemAndShipGroupAssoc", lookupMap, sort);

        String orderItemSeqId = null;
        if (orderItems != null) {
            for (GenericValue item: orderItems) {
                // get the reservations for the item
                Map<String, Object> invLookup = FastMap.newInstance();
                invLookup.put("orderId", orderId);
                invLookup.put("orderItemSeqId", item.getString("orderItemSeqId"));
                invLookup.put("shipGroupSeqId", shipGroupSeqId);
                List<GenericValue> reservations = this.getDelegator().findByAnd("OrderItemShipGrpInvRes", invLookup);
                for (GenericValue res: reservations) {
                    BigDecimal qty = res.getBigDecimal("quantity");
                    if (quantity.compareTo(qty) <= 0) {
                        orderItemSeqId = item.getString("orderItemSeqId");
                        break;
                    }
                }
            }
        }

        if (orderItemSeqId != null) {
            return orderItemSeqId;
        } else {
            throw new GeneralException("No valid order item found for product [" + productId + "] with quantity: " + quantity);
        }
    }

    protected int checkLineForAdd(GenericValue res, String orderId, String orderItemSeqId, String shipGroupSeqId, String productId, BigDecimal quantity, int packageSeqId, boolean update) {
        // check to see if the reservation can hold the requested quantity amount
        String invItemId = res.getString("inventoryItemId");
        BigDecimal resQty = res.getBigDecimal("quantity");

        PackingSessionLine line = this.findLine(orderId, orderItemSeqId, shipGroupSeqId, productId, invItemId, packageSeqId);
        BigDecimal packedQty = this.getPackedQuantity(orderId, orderItemSeqId, shipGroupSeqId, productId);

        Debug.log("Packed quantity [" + packedQty + "] + [" + quantity + "]", module);

        if (line == null) {
            Debug.log("No current line found testing [" + invItemId + "] R: " + resQty + " / Q: " + quantity, module);
            if (resQty.compareTo(quantity) < 0) {
                return 0;
            } else {
                return 2;
            }
        } else {
            BigDecimal newQty = update ? quantity : (line.getQuantity().add(quantity));
            Debug.log("Existing line found testing [" + invItemId + "] R: " + resQty + " / Q: " + newQty, module);
            if (resQty.compareTo(newQty) < 0) {
                return 0;
            } else {
                line.setQuantity(newQty);
                return 1;
            }
        }
    }

    public void addItemInfo(List<GenericValue> infos) {
        for (GenericValue v: infos) {
            ItemDisplay newItem = new ItemDisplay(v);
            int currentIdx = itemInfos.indexOf(newItem);
            if (currentIdx != -1) {
                ItemDisplay existingItem = itemInfos.get(currentIdx);
                existingItem.quantity = existingItem.quantity.add(newItem.quantity);
            } else {
                itemInfos.add(newItem);
            }
        }
    }

    public List<ItemDisplay> getItemInfos() {
        return itemInfos;
    }

    /**
     * <p>Delivers all the packing lines grouped by package.</p>
     * <p>Output map:
     * <ul>
     * <li>packageMap - a Map of type Map<Integer, List<PackingSessionLine>>
     * that maps package sequence ids to the lines that belong in
     * that package</li>
     * <li>sortedKeys - a List of type List<Integer> with the sorted package
     * sequence numbers to index the packageMap</li>
     * @return result Map with packageMap and sortedKeys
     */
    public Map<Object, Object> getPackingSessionLinesByPackage() {
        FastMap<Integer, List<PackingSessionLine>> packageMap = FastMap.newInstance();
        for (PackingSessionLine line : packLines) {
           int pSeq = line.getPackageSeq();
           List<PackingSessionLine> packageLineList = packageMap.get(pSeq);
           if (packageLineList == null) {
               packageLineList = FastList.newInstance();
               packageMap.put(pSeq, packageLineList);
           }
           packageLineList.add(line);
        }
        Object[] keys = packageMap.keySet().toArray();
        java.util.Arrays.sort(keys);
        List<Object> sortedKeys = FastList.newInstance();
        for (Object key : keys) {
            sortedKeys.add(key);
        }
        Map<Object, Object> result = FastMap.newInstance();
        result.put("packageMap", packageMap);
        result.put("sortedKeys", sortedKeys);
        return result;
    }

    public void clearItemInfos() {
        itemInfos.clear();
    }

    public String getShipmentId() {
        return this.shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public List<PackingSessionLine> getLines() {
        return this.packLines;
    }

    public PackingSessionLine getLine(int packageSeqId) {
        PackingSessionLine packLine = null;
        for (PackingSessionLine line : this.getLines()) {
            if ((line.getPackageSeq()) == packageSeqId) {
                packLine = line;
            }
        }
        return packLine;
    }

    public int nextPackageSeq() {
        return ++packageSeq;
    }

    public int getCurrentPackageSeq() {
        return packageSeq;
    }

    public BigDecimal getPackedQuantity(String orderId, String orderItemSeqId, String shipGroupSeqId, String productId) {
        return getPackedQuantity(orderId, orderItemSeqId, shipGroupSeqId,  productId, null, -1);
    }

    public BigDecimal getPackedQuantity(String orderId, String orderItemSeqId, String shipGroupSeqId, String productId, int packageSeq) {
        return getPackedQuantity(orderId, orderItemSeqId, shipGroupSeqId,  productId, null, packageSeq);
    }

    public BigDecimal getPackedQuantity(String orderId, String orderItemSeqId, String shipGroupSeqId, String productId, String inventoryItemId, int packageSeq) {
        BigDecimal total = BigDecimal.ZERO;
        for (PackingSessionLine line: this.getLines()) {
            if (orderId.equals(line.getOrderId()) && orderItemSeqId.equals(line.getOrderItemSeqId()) &&
                    shipGroupSeqId.equals(line.getShipGroupSeqId()) && productId.equals(line.getProductId())) {
                if (inventoryItemId == null || inventoryItemId.equals(line.getInventoryItemId())) {
                    if (packageSeq == -1 || packageSeq == line.getPackageSeq()) {
                        total = total.add(line.getQuantity());
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal getPackedQuantity(String productId, int packageSeq) {
        if (productId != null) {
            try {
                productId = ProductWorker.findProductId(this.getDelegator(), productId);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }

        BigDecimal total = BigDecimal.ZERO;
        if (productId != null ) {
            for (PackingSessionLine line: this.getLines()) {
                if (productId.equals(line.getProductId())) {
                    if (packageSeq == -1 || packageSeq == line.getPackageSeq()) {
                        total = total.add(line.getQuantity());
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal getPackedQuantity(int packageSeq) {
        BigDecimal total = BigDecimal.ZERO;
        for (PackingSessionLine line: this.getLines()) {
            if (packageSeq == -1 || packageSeq == line.getPackageSeq()) {
                total = total.add(line.getQuantity());
            }
        }
        return total;
    }

    public BigDecimal getPackedQuantity(String productId) {
        return getPackedQuantity(productId, -1);
    }

    public BigDecimal getCurrentReservedQuantity(String orderId, String orderItemSeqId, String shipGroupSeqId, String productId) {
        BigDecimal reserved = BigDecimal.ONE.negate();
        try {
            GenericValue res = EntityUtil.getFirst(this.getDelegator().findByAnd("OrderItemAndShipGrpInvResAndItemSum", UtilMisc.toMap("orderId", orderId,
                    "orderItemSeqId", orderItemSeqId, "shipGroupSeqId", shipGroupSeqId, "inventoryProductId", productId)));
            reserved = res.getBigDecimal("totQuantityAvailable");
            if (reserved == null) {
                reserved = BigDecimal.ONE.negate();
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return reserved;
    }

    public BigDecimal getCurrentShippedQuantity(String orderId, String orderItemSeqId, String shipGroupSeqId) {
        BigDecimal shipped = BigDecimal.ZERO;
        List<GenericValue> issues = this.getItemIssuances(orderId, orderItemSeqId, shipGroupSeqId);
        if (issues != null) {
            for (GenericValue v: issues) {
                BigDecimal qty = v.getBigDecimal("quantity");
                if (qty == null) qty = BigDecimal.ZERO;
                shipped = shipped.add(qty);
            }
        }

        return shipped;
    }

    public List<String> getCurrentShipmentIds(String orderId, String orderItemSeqId, String shipGroupSeqId) {
        Set<String> shipmentIds = FastSet.newInstance();
        List<GenericValue> issues = this.getItemIssuances(orderId, orderItemSeqId, shipGroupSeqId);

        if (issues != null) {
            for (GenericValue v: issues) {
                shipmentIds.add(v.getString("shipmentId"));
            }
        }

        List<String> retList = FastList.newInstance();
        retList.addAll(shipmentIds);
        return retList;
    }

    public List<String> getCurrentShipmentIds(String orderId, String shipGroupSeqId) {
        return this.getCurrentShipmentIds(orderId, null, shipGroupSeqId);
    }

    public void registerEvent(PackingEvent event) {
        this.packEvents.add(event);
        this.runEvents(PackingEvent.EVENT_CODE_EREG);
    }

    public LocalDispatcher getDispatcher() {
        if (_dispatcher == null) {
            _dispatcher = GenericDispatcher.getLocalDispatcher(dispatcherName, this.getDelegator());
        }
        return _dispatcher;
    }

    public GenericDelegator getDelegator() {
        if (_delegator == null) {
            _delegator = GenericDelegator.getGenericDelegator(delegatorName);
        }
        return _delegator;
    }

    public GenericValue getUserLogin() {
        return this.userLogin;
    }

    public int getStatus() {
        return this.status;
    }

    public String getFacilityId() {
        return this.facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getPrimaryOrderId() {
        return this.primaryOrderId;
    }

    public void setPrimaryOrderId(String orderId) {
        this.primaryOrderId = orderId;
    }

    public String getPrimaryShipGroupSeqId() {
        return this.primaryShipGrp;
    }

    public void setPrimaryShipGroupSeqId(String shipGroupSeqId) {
        this.primaryShipGrp = shipGroupSeqId;
    }

    public void setPicklistBinId(String binId) {
        this.picklistBinId = binId;
    }

    public String getPicklistBinId() {
        return this.picklistBinId;
    }

    public String getHandlingInstructions() {
        return this.instructions;
    }

    public void setHandlingInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setPickerPartyId(String partyId) {
        this.pickerPartyId = partyId;
    }

    public String getPickerPartyId() {
        return this.pickerPartyId;
    }

    public int clearLastPackage() {
        if (packageSeq == 1) {
            this.clear();
            return packageSeq;
        }

        List<PackingSessionLine> currentLines = UtilMisc.makeListWritable(this.packLines);
        for (PackingSessionLine line: currentLines) {
            if (line.getPackageSeq() == packageSeq) {
                this.clearLine(line);
            }
        }
        //return --packageSeq;
        return packageSeq;
    }

    public void clearLine(PackingSessionLine line) {
        this.packLines.remove(line);
        BigDecimal packageWeight = this.packageWeights.get(line.packageSeq);
        if (packageWeight != null) {
            packageWeight = packageWeight.subtract(line.weight);
            if (packageWeight.compareTo(BigDecimal.ZERO) < 0) {
                packageWeight = BigDecimal.ZERO;
            }
            this.packageWeights.put(line.packageSeq, packageWeight);
        }
        if (line.packageSeq == packageSeq) {
            packageSeq--;
        }
    }

    public void clearAllLines() {
        this.packLines.clear();
        this.packageWeights.clear();
        this.packageSeq = 1;
    }

    public void clear() {
        this.packLines.clear();
        this.instructions = null;
        this.pickerPartyId = null;
        this.picklistBinId = null;
        this.primaryOrderId = null;
        this.primaryShipGrp = null;
        this.additionalShippingCharge = null;
        if (this.packageWeights != null) this.packageWeights.clear();
        this.dimensionUomId = null;
        this.weightUomId = null;
        this.packageSeq = 1;
        this.status = 1;
        this.runEvents(PackingEvent.EVENT_CODE_CLEAR);
    }

    public String complete(boolean force, String orderId, Locale locale) throws GeneralException {
        // clear out empty lines
        // this.checkEmptyLines(); // removing, this seems to be causeing issues -  mja

        // check to see if there is anything to process
        if (this.getLines().size() == 0) {
            return "EMPTY";
        }

        this.checkPackedQty(orderId, locale);
        // set the status to 0
        this.status = 0;
        // create the shipment
        String shipmentId = this.getShipmentId();
        if (UtilValidate.isEmpty(shipmentId)) {
            this.createShipment();
        }
        // create the packages
        this.createPackages();
        // issue the items
        this.changeOrderItemStatus(orderId, shipmentId);
        // assign items to packages
        this.applyItemsToPackages();
        // update ShipmentRouteSegments with total weight and weightUomId
        this.updateShipmentRouteSegments();
        // set the shipment to packed
        this.setShipmentToPacked();
        // set role on picklist
        this.setPickerOnPicklist();
        // run the complete events
        this.runEvents(PackingEvent.EVENT_CODE_COMPLETE);

        return this.shipmentId;
    }

    protected void checkPackedQty(String orderId, Locale locale) throws GeneralException {

        BigDecimal packedQty = ZERO;
        BigDecimal orderedQty = ZERO;

        List<GenericValue> orderItems = this.getDelegator().findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
        for (GenericValue orderItem : orderItems) {
            orderedQty = orderedQty.add(orderItem.getBigDecimal("quantity"));
        }

        for (PackingSessionLine line : this.getLines()) {
            packedQty = packedQty.add(line.getQuantity());
        }

        if (orderedQty.compareTo(packedQty) != 0 ) {
            throw new GeneralException(UtilProperties.getMessage("ProductErrorUiLabels", "ProductErrorAllOrderItemsAreNotPacked", locale));
        }
    }

    protected void checkReservations(boolean ignore) throws GeneralException {
        List<String> errors = FastList.newInstance();
        for (PackingSessionLine line: this.getLines()) {
            BigDecimal reservedQty =  this.getCurrentReservedQuantity(line.getOrderId(), line.getOrderItemSeqId(), line.getShipGroupSeqId(), line.getProductId());
            BigDecimal packedQty = this.getPackedQuantity(line.getOrderId(), line.getOrderItemSeqId(), line.getShipGroupSeqId(), line.getProductId());

            if (packedQty.compareTo(reservedQty) != 0) {
                errors.add("Packed amount does not match reserved amount for item (" + line.getProductId() + ") [" + packedQty + " / " + reservedQty + "]");
            }
        }

        if (errors.size() > 0) {
            if (!ignore) {
                throw new GeneralException("Attempt to pack order failed.", errors);
            } else {
                Debug.logWarning("Packing warnings: " + errors, module);
            }
        }
    }

    protected void checkEmptyLines() throws GeneralException {
        List<PackingSessionLine> lines = FastList.newInstance();
        lines.addAll(this.getLines());
        for (PackingSessionLine l: lines) {
            if (l.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                this.packLines.remove(l);
            }
        }
    }

    protected void runEvents(int eventCode) {
        if (this.packEvents.size() > 0) {
            for (PackingEvent event: this.packEvents) {
                event.runEvent(this, eventCode);
            }
        }
    }

    protected List<GenericValue> getItemIssuances(String orderId, String orderItemSeqId, String shipGroupSeqId) {
        List<GenericValue> issues = null;
        if (orderId == null) {
            throw new IllegalArgumentException("Value for orderId is  null");
        }

        Map<String, Object> lookupMap = FastMap.newInstance();
        lookupMap.put("orderId", orderId);
        if (UtilValidate.isNotEmpty(orderItemSeqId)) {
            lookupMap.put("orderItemSeqId", orderItemSeqId);
        }
        if (UtilValidate.isNotEmpty(shipGroupSeqId)) {
            lookupMap.put("shipGroupSeqId", shipGroupSeqId);
        }
        try {
            issues = this.getDelegator().findByAnd("ItemIssuance",  lookupMap);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        return issues;
    }

    protected void createShipment() throws GeneralException {
        // first create the shipment
        Map<String, Object> newShipment = FastMap.newInstance();
        newShipment.put("originFacilityId", this.facilityId);
        newShipment.put("primaryShipGroupSeqId", primaryShipGrp);
        newShipment.put("primaryOrderId", primaryOrderId);
        newShipment.put("shipmentTypeId", "OUTGOING_SHIPMENT");
        newShipment.put("statusId", "SHIPMENT_INPUT");
        newShipment.put("handlingInstructions", instructions);
        newShipment.put("picklistBinId", picklistBinId);
        newShipment.put("additionalShippingCharge", additionalShippingCharge);
        newShipment.put("userLogin", userLogin);
        Debug.log("Creating new shipment with context: " + newShipment, module);
        Map<String, Object> newShipResp = this.getDispatcher().runSync("createShipment", newShipment);

        if (ServiceUtil.isError(newShipResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(newShipResp));
        }
        this.shipmentId = (String) newShipResp.get("shipmentId");
    }

    protected void issueItemsToShipment() throws GeneralException {
        List<PackingSessionLine> processedLines = FastList.newInstance();
        for (PackingSessionLine line: this.getLines()) {
            if (this.checkLine(processedLines, line)) {
                BigDecimal totalPacked = this.getPackedQuantity(line.getOrderId(),  line.getOrderItemSeqId(),
                        line.getShipGroupSeqId(), line.getProductId(), line.getInventoryItemId(), -1);

                line.issueItemToShipment(shipmentId, picklistBinId, userLogin, totalPacked, getDispatcher());
                processedLines.add(line);
            }
        }
    }

    protected void changeOrderItemStatus(String orderId, String shipmentId) throws GeneralException {
        List<GenericValue> shipmentItems = this.getDelegator().findByAnd("ShipmentItem", UtilMisc.toMap("shipmentId", shipmentId));
        for (GenericValue shipmentItem : shipmentItems) {
            for (PackingSessionLine line : this.getLines()) {
                if (orderId.equals(line.getOrderId()) && shipmentItem.getString("productId").equals(line.getProductId())) {
                    line.setShipmentItemSeqId(shipmentItem.getString("shipmentItemSeqId"));
                }
            }
        }
        List<GenericValue> orderItems = this.getDelegator().findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
        for (GenericValue orderItem : orderItems) {
            List orderItemShipGrpInvReserves = orderItem.getRelated("OrderItemShipGrpInvRes");
            if (UtilValidate.isEmpty(orderItemShipGrpInvReserves)) {
                Map<String, Object> orderItemStatusMap = FastMap.newInstance();
                orderItemStatusMap.put("orderId", orderId);
                orderItemStatusMap.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                orderItemStatusMap.put("userLogin", userLogin);
                orderItemStatusMap.put("statusId", "ITEM_COMPLETED");
                Map<String, Object> orderItemStatusResp = this.getDispatcher().runSync("changeOrderItemStatus", orderItemStatusMap);
                if (ServiceUtil.isError(orderItemStatusResp)) {
                    throw new GeneralException(ServiceUtil.getErrorMessage(orderItemStatusResp));
                }
            }
        }
    }

    protected boolean checkLine(List<PackingSessionLine> processedLines, PackingSessionLine line) {
        for (PackingSessionLine l: processedLines) {
            if (line.isSameItem(l)) {
                line.setShipmentItemSeqId(l.getShipmentItemSeqId());
                return false;
            }
        }

        return true;
    }

    protected void createPackages() throws GeneralException {
        for (int i = 0; i < packageSeq; i++) {
            PackingSessionLine line = this.getLine(i+1);
            String shipmentPackageSeqId = UtilFormatOut.formatPaddedNumber(i+1, 5);

            Map<String, Object> pkgCtx = FastMap.newInstance();
            pkgCtx.put("shipmentId", shipmentId);
            pkgCtx.put("shipmentPackageSeqId", shipmentPackageSeqId);
            pkgCtx.put("boxLength", line.getLength());
            pkgCtx.put("boxWidth", line.getWidth());
            pkgCtx.put("boxHeight", line.getHeight());
            pkgCtx.put("dimensionUomId", getDimensionUomId());
            pkgCtx.put("shipmentBoxTypeId", line.getShipmentBoxTypeId());
            pkgCtx.put("weight", getPackageWeight(i+1));
            pkgCtx.put("weightUomId", getWeightUomId());
            pkgCtx.put("userLogin", userLogin);
            Map<String, Object> newPkgResp = this.getDispatcher().runSync("createShipmentPackage", pkgCtx);

            if (ServiceUtil.isError(newPkgResp)) {
                throw new GeneralException(ServiceUtil.getErrorMessage(newPkgResp));
            }
        }
    }

    protected void applyItemsToPackages() throws GeneralException {
        for (PackingSessionLine line: this.getLines()) {
            line.applyLineToPackage(shipmentId, userLogin, getDispatcher());
        }
    }

    protected void updateShipmentRouteSegments() throws GeneralException {
        BigDecimal shipmentWeight = getTotalWeight();
        if (shipmentWeight.compareTo(BigDecimal.ZERO) <= 0) return;
        List<GenericValue> shipmentRouteSegments = getDelegator().findByAnd("ShipmentRouteSegment", UtilMisc.toMap("shipmentId", this.getShipmentId()));
        if (! UtilValidate.isEmpty(shipmentRouteSegments)) {
            for (GenericValue shipmentRouteSegment: shipmentRouteSegments) {
                shipmentRouteSegment.set("billingWeight", shipmentWeight);
                shipmentRouteSegment.set("billingWeightUomId", getWeightUomId());
            }
            getDelegator().storeAll(shipmentRouteSegments);
        }
    }

    protected void setShipmentToPacked() throws GeneralException {
        Map<String, Object> packedCtx = UtilMisc.toMap("shipmentId", shipmentId, "statusId", "SHIPMENT_PACKED", "userLogin", userLogin);
        Map<String, Object> packedResp = this.getDispatcher().runSync("updateShipment", packedCtx);
        if (packedResp != null && ServiceUtil.isError(packedResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(packedResp));
        }
    }

    protected void setPickerOnPicklist() throws GeneralException {
        if (picklistBinId != null) {
            // first find the picklist id
            GenericValue bin = this.getDelegator().findByPrimaryKey("PicklistBin", UtilMisc.toMap("picklistBinId", picklistBinId));
            if (bin != null) {
                Map<String, Object> ctx = FastMap.newInstance();
                ctx.put("picklistId", bin.getString("picklistId"));
                ctx.put("partyId", pickerPartyId);
                ctx.put("roleTypeId", "PICKER");

                // check if the role already exists and is valid
                List<GenericValue> currentRoles = this.getDelegator().findByAnd("PicklistRole", ctx);
                currentRoles = EntityUtil.filterByDate(currentRoles);

                // if not; create the role
                if (UtilValidate.isNotEmpty(currentRoles)) {
                    ctx.put("userLogin", userLogin);
                    Map<String, Object> addRole = this.getDispatcher().runSync("createPicklistRole", ctx);
                    if (ServiceUtil.isError(addRole)) {
                        throw new GeneralException(ServiceUtil.getErrorMessage(addRole));
                    }
                }
            }
        }
    }

    public BigDecimal getAdditionalShippingCharge() {
        return additionalShippingCharge;
    }

    public void setAdditionalShippingCharge(BigDecimal additionalShippingCharge) {
        this.additionalShippingCharge = additionalShippingCharge;
    }

    public BigDecimal getTotalWeight() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < packageSeq; i++) {
            BigDecimal packageWeight = getPackageWeight(i);
            if (! UtilValidate.isEmpty(packageWeight)) {
                total = total.add(packageWeight);
            }
        }
        return total;
    }

    public BigDecimal getShipmentCostEstimate(GenericValue orderItemShipGroup, String productStoreId, List<GenericValue> shippableItemInfo, BigDecimal shippableTotal, BigDecimal shippableWeight, BigDecimal shippableQuantity) {
        return getShipmentCostEstimate(orderItemShipGroup.getString("contactMechId"), orderItemShipGroup.getString("shipmentMethodTypeId"),
                                       orderItemShipGroup.getString("carrierPartyId"), orderItemShipGroup.getString("carrierRoleTypeId"),
                                       productStoreId, shippableItemInfo, shippableTotal, shippableWeight, shippableQuantity);
    }

    public BigDecimal getShipmentCostEstimate(GenericValue orderItemShipGroup, String productStoreId) {
        return getShipmentCostEstimate(orderItemShipGroup.getString("contactMechId"), orderItemShipGroup.getString("shipmentMethodTypeId"),
                                       orderItemShipGroup.getString("carrierPartyId"), orderItemShipGroup.getString("carrierRoleTypeId"),
                                       productStoreId, null, null, null, null);
    }

    public BigDecimal getShipmentCostEstimate(String shippingContactMechId, String shipmentMethodTypeId, String carrierPartyId, String carrierRoleTypeId, String productStoreId, List<GenericValue> shippableItemInfo, BigDecimal shippableTotal, BigDecimal shippableWeight, BigDecimal shippableQuantity) {

        BigDecimal shipmentCostEstimate = null;
        Map<String, Object> serviceResult = null;
        try {
            Map<String, Object> serviceContext = FastMap.newInstance();
            serviceContext.put("shippingContactMechId", shippingContactMechId);
            serviceContext.put("shipmentMethodTypeId", shipmentMethodTypeId);
            serviceContext.put("carrierPartyId", carrierPartyId);
            serviceContext.put("carrierRoleTypeId", carrierRoleTypeId);
            serviceContext.put("productStoreId", productStoreId);

            if (UtilValidate.isEmpty(shippableItemInfo)) {
                shippableItemInfo = FastList.newInstance();
                for (PackingSessionLine line: getLines()) {
                    List<GenericValue> oiasgas = getDelegator().findByAnd("OrderItemAndShipGroupAssoc", UtilMisc.toMap("orderId", line.getOrderId(), "orderItemSeqId", line.getOrderItemSeqId(), "shipGroupSeqId", line.getShipGroupSeqId()));
                    shippableItemInfo.addAll(oiasgas);
                }
            }
            serviceContext.put("shippableItemInfo", shippableItemInfo);

            if (UtilValidate.isEmpty(shippableWeight)) {
                shippableWeight = getTotalWeight();
            }
            serviceContext.put("shippableWeight", shippableWeight);

            if (UtilValidate.isEmpty(shippableQuantity)) {
                shippableQuantity = getPackedQuantity(-1);
            }
            serviceContext.put("shippableQuantity", shippableQuantity);

            if (UtilValidate.isEmpty(shippableTotal)) {
                shippableTotal = BigDecimal.ZERO;
            }
            serviceContext.put("shippableTotal", shippableTotal);

            serviceResult = getDispatcher().runSync("calcShipmentCostEstimate", serviceContext);
        } catch ( GenericEntityException e ) {
            Debug.logError(e, module);
        } catch ( GenericServiceException e ) {
            Debug.logError(e, module);
        }

        if (! UtilValidate.isEmpty(serviceResult.get("shippingEstimateAmount"))) {
            shipmentCostEstimate = (BigDecimal) serviceResult.get("shippingEstimateAmount");
        }

        return shipmentCostEstimate;

    }

    public String getWeightUomId() {
        return weightUomId;
    }

    public void setWeightUomId(String weightUomId) {
        this.weightUomId = weightUomId;
    }

    public String getDimensionUomId() {
        return dimensionUomId;
    }

    public void setDimensionUomId(String dimensionUomId) {
        this.dimensionUomId = dimensionUomId;
    }

    public List<Integer> getPackageSeqIds() {
        Set<Integer> packageSeqIds = new TreeSet<Integer>();
        if (! UtilValidate.isEmpty(this.getLines())) {
            for (PackingSessionLine line: this.getLines()) {
                packageSeqIds.add(Integer.valueOf(line.getPackageSeq()));
            }
        }
        return UtilMisc.makeListWritable(packageSeqIds);
    }

    public void setPackageWeight(int packageSeqId, BigDecimal packageWeight) {
        if (UtilValidate.isEmpty(packageWeight)) {
            packageWeights.remove(Integer.valueOf(packageSeqId));
        } else {
            packageWeights.put(Integer.valueOf(packageSeqId), packageWeight);
            PackingSessionLine packLine = this.getLine(packageSeqId);
            packLine.setWeight(packageWeight);
        }
    }

    public BigDecimal getPackageWeight(int packageSeqId) {
        if (this.packageWeights == null) return null;
        BigDecimal packageWeight = null;
        Object p = packageWeights.get(Integer.valueOf(packageSeqId));
        if (p != null) {
            packageWeight = (BigDecimal) p;
        }
        return packageWeight;
    }

    public void addToPackageWeight(int packageSeqId, BigDecimal weight) {
        if (UtilValidate.isEmpty(weight)) return;
        BigDecimal packageWeight = getPackageWeight(packageSeqId);
        BigDecimal newPackageWeight = UtilValidate.isEmpty(packageWeight) ? weight : weight.add(packageWeight);
        setPackageWeight(packageSeqId, newPackageWeight);
    }

    public void setPackageLength(String packageSeqId, String packageLength) {
        if (UtilValidate.isNotEmpty(packageSeqId)) {
            PackingSessionLine packLine = this.getLine(Integer.parseInt(packageSeqId));
            if (UtilValidate.isNotEmpty(packageLength)) {
                packLine.setLength(new BigDecimal(packageLength));
            }
        }
    }

    public BigDecimal getPackageLength(int packageSeqId) {
        BigDecimal packageLength = null;
        PackingSessionLine packLine = this.getLine(packageSeqId);
        if (UtilValidate.isNotEmpty(packLine)) {
            packageLength = packLine.getLength();
        }
        return packageLength;
    }

    public void setPackageWidth(String packageSeqId, String packageWidth) {
        if (UtilValidate.isNotEmpty(packageSeqId)) {
            PackingSessionLine packLine = this.getLine(Integer.parseInt(packageSeqId));
            if (UtilValidate.isNotEmpty(packageWidth)) {
                packLine.setWidth(new BigDecimal(packageWidth));
            }
        }
    }

    public BigDecimal getPackageWidth(int packageSeqId) {
        BigDecimal packageWidth = null;
        PackingSessionLine packLine = this.getLine(packageSeqId);
        if (UtilValidate.isNotEmpty(packLine)) {
            packageWidth = packLine.getWidth();
        }
        return packageWidth;
    }

    public void setPackageHeight(String packageSeqId, String packageHeight) {
        if (UtilValidate.isNotEmpty(packageSeqId)) {
            PackingSessionLine packLine = this.getLine(Integer.parseInt(packageSeqId));
            if (UtilValidate.isNotEmpty(packageHeight)) {
                packLine.setHeight(new BigDecimal(packageHeight));
            }
        }
    }

    public BigDecimal getPackageHeight(int packageSeqId) {
        BigDecimal packageHeight = null;
        PackingSessionLine packLine = this.getLine(packageSeqId);
        if (UtilValidate.isNotEmpty(packLine)) {
            packageHeight = packLine.getHeight();
        }
        return packageHeight;
    }

    public void setShipmentBoxTypeId(String packageSeqId, String shipmentBoxTypeId) {
        if (UtilValidate.isNotEmpty(packageSeqId)) {
            PackingSessionLine packLine = this.getLine(Integer.parseInt(packageSeqId));
            if (UtilValidate.isNotEmpty(shipmentBoxTypeId)) {
                packLine.setShipmentBoxTypeId(shipmentBoxTypeId);
            }
        }
    }

    public String getShipmentBoxTypeId(int packageSeqId) {
        String shipmentBoxTypeId = null;
        PackingSessionLine packLine = this.getLine(packageSeqId);
        if (UtilValidate.isNotEmpty(packLine)) {
            shipmentBoxTypeId = packLine.getShipmentBoxTypeId();
        }
        return shipmentBoxTypeId;
    }

    public void setWeightPackageSeqId(String packageSeqId, String weightPackageSeqId) {
        if (UtilValidate.isNotEmpty(packageSeqId)) {
            PackingSessionLine packLine = this.getLine(Integer.parseInt(packageSeqId));
            if (UtilValidate.isNotEmpty(weightPackageSeqId)) {
                packLine.setWeightPackageSeqId(weightPackageSeqId);
            }
        }
    }

    public int getWeightPackageSeqId(int packageSeqId) {
        int weightPackageSeqId = -1;
        if (UtilValidate.isNotEmpty(this.getLine(packageSeqId))) {
            if (UtilValidate.isNotEmpty(this.getLine(packageSeqId).getWeightPackageSeqId()))
                weightPackageSeqId = Integer.parseInt(this.getLine(packageSeqId).getWeightPackageSeqId());
        }
        return weightPackageSeqId;
    }

    protected void createPackages(String shipmentId) throws GeneralException {
        List<GenericValue> shipmentPackageRouteSegs = this.getDelegator().findByAnd("ShipmentPackageRouteSeg", UtilMisc.toMap("shipmentId", shipmentId));
        if (UtilValidate.isNotEmpty(shipmentPackageRouteSegs)) {
            for (GenericValue shipmentPackageRouteSeg : shipmentPackageRouteSegs) {
                shipmentPackageRouteSeg.remove();
            }
        }
        List<GenericValue> shipmentPackages = this.getDelegator().findByAnd("ShipmentPackage", UtilMisc.toMap("shipmentId", shipmentId));
        if (UtilValidate.isNotEmpty(shipmentPackages)) {
            for (GenericValue shipmentPackage : shipmentPackages) {
                shipmentPackage.remove();
            }
        }
        for (int i = 0; i < packageSeq; i++) {
            PackingSessionLine line = this.getLine(i+1);
            String shipmentPackageSeqId = UtilFormatOut.formatPaddedNumber(i+1, 5);
            Map<String, Object> shipmentPackageCtx = FastMap.newInstance();
            shipmentPackageCtx.put("shipmentId", shipmentId);
            shipmentPackageCtx.put("shipmentPackageSeqId", shipmentPackageSeqId);
            shipmentPackageCtx.put("boxLength", line.getLength());
            shipmentPackageCtx.put("boxWidth", line.getWidth());
            shipmentPackageCtx.put("boxHeight", line.getHeight());
            shipmentPackageCtx.put("dimensionUomId", getDimensionUomId());
            shipmentPackageCtx.put("shipmentBoxTypeId", line.getShipmentBoxTypeId());
            shipmentPackageCtx.put("weight", getPackageWeight(i+1));
            shipmentPackageCtx.put("weightUomId", getWeightUomId());
            shipmentPackageCtx.put("userLogin", userLogin);
            Map<String, Object> shipmentPackageResult = this.getDispatcher().runSync("createShipmentPackage", shipmentPackageCtx);
            if (ServiceUtil.isError(shipmentPackageResult)) {
                throw new GeneralException(ServiceUtil.getErrorMessage(shipmentPackageResult));
            }
        }
    }

    public void setDimensionAndShipmentBoxType(String packageSeqId) {
        if (UtilValidate.isNotEmpty(packageSeqId)) {
            PackingSessionLine packLine = this.getLine(Integer.parseInt(packageSeqId));
            packLine.setLength(null);
            packLine.setWidth(null);
            packLine.setHeight(null);
            packLine.setShipmentBoxTypeId(null);
        }
    }

    public List<Map<String, Object>> getPackageInfo() throws GenericEntityException {
        List<Map<String, Object>> packageInfoList = FastList.newInstance();
        if (UtilValidate.isNotEmpty(this.getLines())) {
            for (PackingSessionLine packedline : this.getLines()) {
                Map<String, Object> packageInfoMap = FastMap.newInstance();
                if (UtilValidate.isNotEmpty(packedline.getShipmentBoxTypeId())) {
                    GenericValue shipmentBoxType = this.getDelegator().findOne("ShipmentBoxType", UtilMisc.toMap("shipmentBoxTypeId", packedline.getShipmentBoxTypeId()), false);
                    packageInfoMap.put("shipmentBoxType", shipmentBoxType);
                } else {
                    packageInfoMap.put("shipmentBoxType", null);
                }
                if (UtilValidate.isNotEmpty(packedline.getLength()) && UtilValidate.isNotEmpty(packedline.getWidth()) && UtilValidate.isNotEmpty(packedline.getHeight())) {
                    packageInfoMap.put("packageLength", packedline.getLength());
                    packageInfoMap.put("packageWidth", packedline.getWidth());
                    packageInfoMap.put("packageHeight", packedline.getHeight());
                } else {
                    packageInfoMap.put("packageLength", null);
                    packageInfoMap.put("packageWidth", null);
                    packageInfoMap.put("packageHeight", null);
                }
                packageInfoMap.put("packageWeight", packedline.getWeight());
                packageInfoList.add(packageInfoMap);
            }
        }
        return packageInfoList;
    }

    class ItemDisplay extends AbstractMap {

        public GenericValue orderItem;
        public BigDecimal quantity;
        public String productId;

        public ItemDisplay(GenericValue v) {
            if ("PicklistItem".equals(v.getEntityName())) {
                quantity = v.getBigDecimal("quantity").setScale(2, BigDecimal.ROUND_HALF_UP);
                try {
                    orderItem = v.getRelatedOne("OrderItem");
                    productId = v.getRelatedOne("InventoryItem").getString("productId");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
            } else {
                // this is an OrderItemAndShipGrpInvResAndItemSum
                orderItem = v;
                productId = v.getString("inventoryProductId");
                quantity = v.getBigDecimal("totQuantityReserved").setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            Debug.log("created item display object quantity: " + quantity + " (" + productId + ")", module);
        }

        public GenericValue getOrderItem() {
            return orderItem;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public Set entrySet() {
            return null;
        }

        public Object get(Object name) {
            if ("orderItem".equals(name.toString())) {
                return orderItem;
            } else if ("quantity".equals(name.toString())) {
                return quantity;
            } else if ("productId".equals(name.toString())) {
                return productId;
            }
            return null;
        }

        public boolean equals(Object o) {
            if (o instanceof ItemDisplay) {
                ItemDisplay d = (ItemDisplay) o;
                boolean sameOrderItemProduct = true;
                if (d.getOrderItem().getString("productId") != null && orderItem.getString("productId") != null) {
                    sameOrderItemProduct = d.getOrderItem().getString("productId").equals(orderItem.getString("productId"));
                } else if (d.getOrderItem().getString("productId") != null || orderItem.getString("productId") != null) {
                    sameOrderItemProduct = false;
                }
                return (d.productId.equals(productId) &&
                        d.getOrderItem().getString("orderItemSeqId").equals(orderItem.getString("orderItemSeqId")) &&
                        sameOrderItemProduct);
            } else {
                return false;
            }
        }
    }
}
