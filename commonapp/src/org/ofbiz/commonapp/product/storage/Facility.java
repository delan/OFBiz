
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.product.inventory.*;

/**
 * <p><b>Title:</b> Facility Entity
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
 *@created    Fri Jul 27 01:18:32 MDT 2001
 *@version    1.0
 */

public interface Facility extends EJBObject
{
  /** Get the primary key of the FACILITY_ID column of the FACILITY table. */
  public String getFacilityId() throws RemoteException;
  
  /** Get the value of the FACILITY_TYPE_ID column of the FACILITY table. */
  public String getFacilityTypeId() throws RemoteException;
  /** Set the value of the FACILITY_TYPE_ID column of the FACILITY table. */
  public void setFacilityTypeId(String facilityTypeId) throws RemoteException;
  
  /** Get the value of the FACILITY_NAME column of the FACILITY table. */
  public String getFacilityName() throws RemoteException;
  /** Set the value of the FACILITY_NAME column of the FACILITY table. */
  public void setFacilityName(String facilityName) throws RemoteException;
  
  /** Get the value of the SQUARE_FOOTAGE column of the FACILITY table. */
  public Long getSquareFootage() throws RemoteException;
  /** Set the value of the SQUARE_FOOTAGE column of the FACILITY table. */
  public void setSquareFootage(Long squareFootage) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the FACILITY table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the FACILITY table. */
  public void setDescription(String description) throws RemoteException;
  

  /** Get the value object of this Facility class. */
  public Facility getValueObject() throws RemoteException;
  /** Set the values in the value object of this Facility class. */
  public void setValueObject(Facility facilityValue) throws RemoteException;


  /** Get the  FacilityType entity corresponding to this entity. */
  public FacilityType getFacilityType() throws RemoteException;
  /** Remove the  FacilityType entity corresponding to this entity. */
  public void removeFacilityType() throws RemoteException;  

  /** Get a collection of  FacilityTypeAttr related entities. */
  public Collection getFacilityTypeAttrs() throws RemoteException;
  /** Get the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public FacilityTypeAttr getFacilityTypeAttr(String name) throws RemoteException;
  /** Remove  FacilityTypeAttr related entities. */
  public void removeFacilityTypeAttrs() throws RemoteException;
  /** Remove the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityTypeAttr(String name) throws RemoteException;

  /** Get a collection of  FacilityAttribute related entities. */
  public Collection getFacilityAttributes() throws RemoteException;
  /** Get the  FacilityAttribute keyed by member(s) of this class, and other passed parameters. */
  public FacilityAttribute getFacilityAttribute(String name) throws RemoteException;
  /** Remove  FacilityAttribute related entities. */
  public void removeFacilityAttributes() throws RemoteException;
  /** Remove the  FacilityAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityAttribute(String name) throws RemoteException;

  /** Get a collection of  InventoryItem related entities. */
  public Collection getInventoryItems() throws RemoteException;
  /** Get the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public InventoryItem getInventoryItem(String inventoryItemId) throws RemoteException;
  /** Remove  InventoryItem related entities. */
  public void removeInventoryItems() throws RemoteException;
  /** Remove the  InventoryItem keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItem(String inventoryItemId) throws RemoteException;

  /** Get a collection of  Container related entities. */
  public Collection getContainers() throws RemoteException;
  /** Get the  Container keyed by member(s) of this class, and other passed parameters. */
  public Container getContainer(String containerId) throws RemoteException;
  /** Remove  Container related entities. */
  public void removeContainers() throws RemoteException;
  /** Remove the  Container keyed by member(s) of this class, and other passed parameters. */
  public void removeContainer(String containerId) throws RemoteException;

  /** Get a collection of  FacilityContactMechanism related entities. */
  public Collection getFacilityContactMechanisms() throws RemoteException;
  /** Get the  FacilityContactMechanism keyed by member(s) of this class, and other passed parameters. */
  public FacilityContactMechanism getFacilityContactMechanism(String contactMechanismId) throws RemoteException;
  /** Remove  FacilityContactMechanism related entities. */
  public void removeFacilityContactMechanisms() throws RemoteException;
  /** Remove the  FacilityContactMechanism keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityContactMechanism(String contactMechanismId) throws RemoteException;

  /** Get a collection of  PartyFacility related entities. */
  public Collection getPartyFacilitys() throws RemoteException;
  /** Get the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public PartyFacility getPartyFacility(String partyId) throws RemoteException;
  /** Remove  PartyFacility related entities. */
  public void removePartyFacilitys() throws RemoteException;
  /** Remove the  PartyFacility keyed by member(s) of this class, and other passed parameters. */
  public void removePartyFacility(String partyId) throws RemoteException;

}
