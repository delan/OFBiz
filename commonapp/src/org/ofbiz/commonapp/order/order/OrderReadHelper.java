/*
 * $Id$
 * $Log$
 * Revision 1.10  2001/09/27 15:56:13  epabst
 * refactored code to use getRelatedByAnd, filterByDate
 *
 * Revision 1.9  2001/09/26 17:18:14  epabst
 * added getDistributorId() method
 *
 * Revision 1.8  2001/09/26 15:17:42  epabst
 * moved getFirst() method into new EntityUtil class
 *
 * Revision 1.7  2001/09/20 22:47:21  jonesde
 * Fixed illegal use of getRelatedOne
 *
 * Revision 1.6  2001/09/19 08:35:19  jonesde
 * Initial checkin of refactored entity engine.
 *
 * Revision 1.5  2001/09/18 20:42:25  jonesde
 * Fixed null pointer problems...
 *
 * Revision 1.4  2001/09/14 21:15:13  epabst
 * cleaned up
 *
 */

package org.ofbiz.commonapp.order.order;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Order Reading Helper
 * <p><b>Description:</b> Utility class for easily extracting important information from orders
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
 *@author     Eric Pabst
 *@created    Sept 7, 2001
 *@version    1.0
 */
public class OrderReadHelper {
  private GenericValue orderHeader;
  private Double totalPrice;
  
  public OrderReadHelper(GenericValue orderHeader) {
    this.orderHeader = orderHeader;
  }
  
  public String getShippingMethod() {
    try {
      GenericValue shipmentPreference = null;
      Iterator tempIter = UtilMisc.toIterator(orderHeader.getRelated("OrderShipmentPreference"));
      if(tempIter != null && tempIter.hasNext()) { shipmentPreference = (GenericValue)tempIter.next(); }
      if(shipmentPreference != null) {
        GenericValue carrierShipmentMethod = shipmentPreference.getRelatedOne("CarrierShipmentMethod");
        if(carrierShipmentMethod != null) {
          GenericValue shipmentMethodType = carrierShipmentMethod.getRelatedOne("ShipmentMethodType");
          if(shipmentMethodType != null) {
            return UtilFormatOut.checkNull(shipmentPreference.getString("carrierPartyId")) + " " + UtilFormatOut.checkNull(shipmentMethodType.getString("description"));
          }
        }
        return UtilFormatOut.checkNull(shipmentPreference.getString("carrierPartyId"));
      }
      return "";
    }
    catch(GenericEntityException e) { Debug.logWarning(e); }    
    return "";
  }
  
  public GenericValue getShippingAddress() {
    GenericDelegator delegator = orderHeader.getDelegator();
    try {
      GenericValue orderContactMech = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderContactMech", UtilMisc.toMap(
              "contactMechPurposeTypeId", "SHIPPING_LOCATION")));
      if (orderContactMech != null) {
        GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");
        if (contactMech != null) {
          return contactMech.getRelatedOne("PostalAddress");
        }
      }
    }
    catch(GenericEntityException e) { Debug.logWarning(e); }    
    return null;
  }

  public GenericValue getBillingAddress() {
    GenericDelegator delegator = orderHeader.getDelegator();
    try {
      GenericValue orderContactMech = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderContactMech", UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION")));
      if (orderContactMech != null) {
        GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");
        if (contactMech != null) {
          return contactMech.getRelatedOne("PostalAddress");
        }
      }
    }
    catch(GenericEntityException e) { Debug.logWarning(e); }    
    return null;
  }
  
  public double getTotalPrice() {
    if (totalPrice == null) {
      double total = getOrderItemsTotal();
      Iterator iter = getAdjustmentIterator();
      while (iter.hasNext()) {
        Adjustment adjustment = (Adjustment) iter.next();
        total += adjustment.getAmount();
      }
      totalPrice = new Double(total);
    }//else already set
    return totalPrice.doubleValue();
  }
  
  public String getStatusString() {
    Collection orderStatusList = null;
    try { orderStatusList = orderHeader.getRelated("OrderStatus"); }
    catch(GenericEntityException e) { Debug.logWarning(e); return ""; }
    
    Set orderStatusIdSet = new HashSet();
    Iterator orderStatusIter = orderStatusList.iterator();
    while (orderStatusIter.hasNext()) {
      orderStatusIdSet.add(((GenericValue) orderStatusIter.next()).getString("statusId"));
    }
    Iterator orderStatusIdIter = orderStatusIdSet.iterator();
    String orderStatusIds;
    if (orderStatusIdIter.hasNext()) {
      orderStatusIds = orderStatusIdIter.next().toString();
      while (orderStatusIdIter.hasNext()) {
        orderStatusIds += "/" + orderStatusIdIter.next().toString();
      }
    } else {
      orderStatusIds = "(unspecified)";
    }
    return orderStatusIds;
  }
  
  public GenericValue getBillToPerson() {
    GenericDelegator delegator = orderHeader.getDelegator();
    try {
      GenericEntity billToRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "BILL_TO_CUSTOMER")));
      if (billToRole != null) {
        return delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", billToRole.getString("partyId")));
      } 
      else {
        return null;
      }
    }
    catch(GenericEntityException e) { Debug.logWarning(e); }
    return null;
  }
  
  public String getDistributorId() {
    GenericDelegator delegator = orderHeader.getDelegator();
    try {
      GenericEntity distributorRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "DISTRIBUTOR")));
      return distributorRole == null ? null : distributorRole.getString("partyId");
    }
    catch(GenericEntityException e) { Debug.logWarning(e); }
    return null;
  }
  
  public double getOrderItemsTotal() {
    double result = 0.0;
    Iterator itemIter = null;
    try { itemIter = UtilMisc.toIterator(orderHeader.getRelated("OrderItem")); }
    catch(GenericEntityException e) { Debug.logWarning(e); itemIter = null; }

    while(itemIter != null && itemIter.hasNext()) {
      result += getOrderItemTotal((GenericValue) itemIter.next());
    }
    return result;
  }
  
  
  public double getOrderItemTotal(GenericValue orderItem) {
    double result = orderItem.getDouble("unitPrice").doubleValue() * orderItem.getDouble("quantity").doubleValue();
    //FIXME should include adjustments as well
    return result;
  }
  
  /** Iterator of OrderReadHelper.Adjustment */
  public Iterator getAdjustmentIterator() {
    try {
      return new AdjustmentIterator(orderHeader.getRelated("OrderAdjustment"), getOrderItemsTotal());
    }
    catch(GenericEntityException e) { Debug.logWarning(e); return null; }
  }
  
  private class AdjustmentIterator implements Iterator {
    private Iterator orderAdjustmentIter;
    private double basePrice;
    
    private AdjustmentIterator(Collection orderAdjustments, double basePrice) {
      this.basePrice = basePrice;
      this.orderAdjustmentIter = orderAdjustments.iterator();
    }
    
    public boolean hasNext() {
      return orderAdjustmentIter.hasNext();
    }
    
    public Object next() {
      GenericValue orderAdjustment = (GenericValue) orderAdjustmentIter.next();
      GenericValue orderAdjustmentType = null;
      try { orderAdjustmentType = orderAdjustment.getRelatedOne("OrderAdjustmentType"); }
      catch(GenericEntityException e) { Debug.logWarning(e); orderAdjustmentType = null; }
      String description;
      if (orderAdjustmentType != null) {
        description = orderAdjustmentType.getString("description");
      } else {
        //if not linked to an adjustment type, leave the description generic
        description = "Adjustment";
      }
      Adjustment result = new Adjustment(description, orderAdjustment.getDouble("amount"), orderAdjustment.getDouble("percentage"), basePrice);
      if ("SHIPPING_CHARGES".equals(orderAdjustmentType.getString("orderAdjustmentTypeId"))
      && (getShippingMethod() != null)) {
        //put the shipping method in the adjustment description
        result.prependDescription(getShippingMethod() + " ");
      }//else keep as is
      return result;
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
}
