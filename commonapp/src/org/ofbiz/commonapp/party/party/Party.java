
package org.ofbiz.commonapp.party.party;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

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

public interface Party extends EJBObject
{
  /** Get the primary key of the PARTY_ID column of the PARTY table. */
  public String getPartyId() throws RemoteException;
  

  /** Get the value object of this Party class. */
  public Party getValueObject() throws RemoteException;
  /** Set the values in the value object of this Party class. */
  public void setValueObject(Party partyValue) throws RemoteException;


  /** Get a collection of  PartyClassification related entities. */
  public Collection getPartyClassifications() throws RemoteException;
  /** Get the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public PartyClassification getPartyClassification(String partyTypeId) throws RemoteException;
  /** Remove  PartyClassification related entities. */
  public void removePartyClassifications() throws RemoteException;
  /** Remove the  PartyClassification keyed by member(s) of this class, and other passed parameters. */
  public void removePartyClassification(String partyTypeId) throws RemoteException;

  /** Get a collection of  PartyAttribute related entities. */
  public Collection getPartyAttributes() throws RemoteException;
  /** Get the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public PartyAttribute getPartyAttribute(String name) throws RemoteException;
  /** Remove  PartyAttribute related entities. */
  public void removePartyAttributes() throws RemoteException;
  /** Remove the  PartyAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePartyAttribute(String name) throws RemoteException;

  /** Get a collection of  UserLogin related entities. */
  public Collection getUserLogins() throws RemoteException;
  /** Get the  UserLogin keyed by member(s) of this class, and other passed parameters. */
  public UserLogin getUserLogin(String userLoginId) throws RemoteException;
  /** Remove  UserLogin related entities. */
  public void removeUserLogins() throws RemoteException;
  /** Remove the  UserLogin keyed by member(s) of this class, and other passed parameters. */
  public void removeUserLogin(String userLoginId) throws RemoteException;

  /** Get a collection of  CostComponent related entities. */
  public Collection getCostComponents() throws RemoteException;
  /** Get the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public CostComponent getCostComponent(String costComponentId) throws RemoteException;
  /** Remove  CostComponent related entities. */
  public void removeCostComponents() throws RemoteException;
  /** Remove the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponent(String costComponentId) throws RemoteException;

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() throws RemoteException;
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) throws RemoteException;
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() throws RemoteException;
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) throws RemoteException;

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() throws RemoteException;
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) throws RemoteException;
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() throws RemoteException;
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) throws RemoteException;

  /** Get a collection of  PartyFacility related entities. */
  public Collection getPartyFacilitys() throws RemoteException;
  /** Get the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public PartyFacility getPartyFacility(String facilityId) throws RemoteException;
  /** Remove  PartyFacility related entities. */
  public void removePartyFacilitys() throws RemoteException;
  /** Remove the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public void removePartyFacility(String facilityId) throws RemoteException;

  /** Get a collection of  ReorderGuideline related entities. */
  public Collection getReorderGuidelines() throws RemoteException;
  /** Get the  ReorderGuideline keyed by member(s) of this class, and other passed parameters. */
  public ReorderGuideline getReorderGuideline(String reorderGuidelineId) throws RemoteException;
  /** Remove  ReorderGuideline related entities. */
  public void removeReorderGuidelines() throws RemoteException;
  /** Remove the  ReorderGuideline keyed by member(s) of this class, and other passed parameters. */
  public void removeReorderGuideline(String reorderGuidelineId) throws RemoteException;

  /** Get a collection of  SupplierProduct related entities. */
  public Collection getSupplierProducts() throws RemoteException;
  /** Get the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public SupplierProduct getSupplierProduct(String productId) throws RemoteException;
  /** Remove  SupplierProduct related entities. */
  public void removeSupplierProducts() throws RemoteException;
  /** Remove the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public void removeSupplierProduct(String productId) throws RemoteException;

}
