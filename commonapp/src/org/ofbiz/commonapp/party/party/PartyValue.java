
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.security.login.*;
import org.ofbiz.commonapp.product.cost.*;
import org.ofbiz.commonapp.product.price.*;
import org.ofbiz.commonapp.product.inventory.*;
import org.ofbiz.commonapp.product.storage.*;
import org.ofbiz.commonapp.product.supplier.*;

/**
 * <p><b>Title:</b> Party Entity
 * <p><b>Description:</b> None
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
 *@author     David E. Jones
 *@created    Fri Jul 27 01:18:23 MDT 2001
 *@version    1.0
 */
public class PartyValue implements Party
{
  /** The variable of the PARTY_ID column of the PARTY table. */
  private String partyId;

  private Party party;

  public PartyValue()
  {
    this.partyId = null;

    this.party = null;
  }

  public PartyValue(Party party) throws RemoteException
  {
    if(party == null) return;
  
    this.partyId = party.getPartyId();

    this.party = party;
  }

  public PartyValue(Party party, String partyId)
  {
    if(party == null) return;
  
    this.partyId = partyId;

    this.party = party;
  }


  /** Get the primary key of the PARTY_ID column of the PARTY table. */
  public String getPartyId()  throws RemoteException { return partyId; }

  /** Get the value object of the Party class. */
  public Party getValueObject() throws RemoteException { return this; }
  /** Set the value object of the Party class. */
  public void setValueObject(Party valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(party!=null) party.setValueObject(valueObject);

    if(partyId == null) partyId = valueObject.getPartyId();
  }


  /** Get a collection of  PartyClassification related entities. */
  public Collection getPartyClassifications() { return PartyClassificationHelper.findByPartyId(partyId); }
  /** Get the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public PartyClassification getPartyClassification(String partyTypeId) { return PartyClassificationHelper.findByPrimaryKey(partyId, partyTypeId); }
  /** Remove  PartyClassification related entities. */
  public void removePartyClassifications() { PartyClassificationHelper.removeByPartyId(partyId); }
  /** Remove the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public void removePartyClassification(String partyTypeId) { PartyClassificationHelper.removeByPrimaryKey(partyId, partyTypeId); }

  /** Get a collection of  PartyAttribute related entities. */
  public Collection getPartyAttributes() { return PartyAttributeHelper.findByPartyId(partyId); }
  /** Get the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public PartyAttribute getPartyAttribute(String name) { return PartyAttributeHelper.findByPrimaryKey(partyId, name); }
  /** Remove  PartyAttribute related entities. */
  public void removePartyAttributes() { PartyAttributeHelper.removeByPartyId(partyId); }
  /** Remove the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePartyAttribute(String name) { PartyAttributeHelper.removeByPrimaryKey(partyId, name); }

  /** Get a collection of  UserLogin related entities. */
  public Collection getUserLogins() { return UserLoginHelper.findByPartyId(partyId); }
  /** Get the  UserLogin keyed by member(s) of this class, and other passed parameters. */
  public UserLogin getUserLogin(String userLoginId) { return UserLoginHelper.findByPrimaryKey(userLoginId); }
  /** Remove  UserLogin related entities. */
  public void removeUserLogins() { UserLoginHelper.removeByPartyId(partyId); }
  /** Remove the  UserLogin keyed by member(s) of this class, and other passed parameters. */
  public void removeUserLogin(String userLoginId) { UserLoginHelper.removeByPrimaryKey(userLoginId); }

  /** Get a collection of  CostComponent related entities. */
  public Collection getCostComponents() { return CostComponentHelper.findByPartyId(partyId); }
  /** Get the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public CostComponent getCostComponent(String costComponentId) { return CostComponentHelper.findByPrimaryKey(costComponentId); }
  /** Remove  CostComponent related entities. */
  public void removeCostComponents() { CostComponentHelper.removeByPartyId(partyId); }
  /** Remove the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponent(String costComponentId) { CostComponentHelper.removeByPrimaryKey(costComponentId); }

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() { return PriceComponentHelper.findByPartyId(partyId); }
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() { PriceComponentHelper.removeByPartyId(partyId); }
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() { return InventoryItemHelper.findByPartyId(partyId); }
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) { return InventoryItemHelper.findByPrimaryKey(inventoryItemId); }
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() { InventoryItemHelper.removeByPartyId(partyId); }
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) { InventoryItemHelper.removeByPrimaryKey(inventoryItemId); }

  /** Get a collection of  PartyFacility related entities. */
  public Collection getPartyFacilitys() { return PartyFacilityHelper.findByPartyId(partyId); }
  /** Get the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public PartyFacility getPartyFacility(String facilityId) { return PartyFacilityHelper.findByPrimaryKey(partyId, facilityId); }
  /** Remove  PartyFacility related entities. */
  public void removePartyFacilitys() { PartyFacilityHelper.removeByPartyId(partyId); }
  /** Remove the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public void removePartyFacility(String facilityId) { PartyFacilityHelper.removeByPrimaryKey(partyId, facilityId); }

  /** Get a collection of  ReorderGuideline related entities. */
  public Collection getReorderGuidelines() { return ReorderGuidelineHelper.findByPartyId(partyId); }
  /** Get the  ReorderGuideline keyed by member(s) of this class, and other passed parameters. */
  public ReorderGuideline getReorderGuideline(String reorderGuidelineId) { return ReorderGuidelineHelper.findByPrimaryKey(reorderGuidelineId); }
  /** Remove  ReorderGuideline related entities. */
  public void removeReorderGuidelines() { ReorderGuidelineHelper.removeByPartyId(partyId); }
  /** Remove the  ReorderGuideline keyed by member(s) of this class, and other passed parameters. */
  public void removeReorderGuideline(String reorderGuidelineId) { ReorderGuidelineHelper.removeByPrimaryKey(reorderGuidelineId); }

  /** Get a collection of  SupplierProduct related entities. */
  public Collection getSupplierProducts() { return SupplierProductHelper.findByPartyId(partyId); }
  /** Get the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public SupplierProduct getSupplierProduct(String productId) { return SupplierProductHelper.findByPrimaryKey(productId, partyId); }
  /** Remove  SupplierProduct related entities. */
  public void removeSupplierProducts() { SupplierProductHelper.removeByPartyId(partyId); }
  /** Remove the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public void removeSupplierProduct(String productId) { SupplierProductHelper.removeByPrimaryKey(productId, partyId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(party!=null) return party.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(party!=null) return party.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(party!=null) return party.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(party!=null) return party.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(party!=null) party.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
