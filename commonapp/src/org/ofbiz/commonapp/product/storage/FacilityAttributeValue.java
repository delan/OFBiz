
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Facility Attribute Entity
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
public class FacilityAttributeValue implements FacilityAttribute
{
  /** The variable of the FACILITY_ID column of the FACILITY_ATTRIBUTE table. */
  private String facilityId;
  /** The variable of the NAME column of the FACILITY_ATTRIBUTE table. */
  private String name;
  /** The variable of the VALUE column of the FACILITY_ATTRIBUTE table. */
  private String value;

  private FacilityAttribute facilityAttribute;

  public FacilityAttributeValue()
  {
    this.facilityId = null;
    this.name = null;
    this.value = null;

    this.facilityAttribute = null;
  }

  public FacilityAttributeValue(FacilityAttribute facilityAttribute) throws RemoteException
  {
    if(facilityAttribute == null) return;
  
    this.facilityId = facilityAttribute.getFacilityId();
    this.name = facilityAttribute.getName();
    this.value = facilityAttribute.getValue();

    this.facilityAttribute = facilityAttribute;
  }

  public FacilityAttributeValue(FacilityAttribute facilityAttribute, String facilityId, String name, String value)
  {
    if(facilityAttribute == null) return;
  
    this.facilityId = facilityId;
    this.name = name;
    this.value = value;

    this.facilityAttribute = facilityAttribute;
  }


  /** Get the primary key of the FACILITY_ID column of the FACILITY_ATTRIBUTE table. */
  public String getFacilityId()  throws RemoteException { return facilityId; }

  /** Get the primary key of the NAME column of the FACILITY_ATTRIBUTE table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value of the VALUE column of the FACILITY_ATTRIBUTE table. */
  public String getValue() throws RemoteException { return value; }
  /** Set the value of the VALUE column of the FACILITY_ATTRIBUTE table. */
  public void setValue(String value) throws RemoteException
  {
    this.value = value;
    if(facilityAttribute!=null) facilityAttribute.setValue(value);
  }

  /** Get the value object of the FacilityAttribute class. */
  public FacilityAttribute getValueObject() throws RemoteException { return this; }
  /** Set the value object of the FacilityAttribute class. */
  public void setValueObject(FacilityAttribute valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(facilityAttribute!=null) facilityAttribute.setValueObject(valueObject);

    if(facilityId == null) facilityId = valueObject.getFacilityId();
    if(name == null) name = valueObject.getName();
    value = valueObject.getValue();
  }


  /** Get the  Facility entity corresponding to this entity. */
  public Facility getFacility() { return FacilityHelper.findByPrimaryKey(facilityId); }
  /** Remove the  Facility entity corresponding to this entity. */
  public void removeFacility() { FacilityHelper.removeByPrimaryKey(facilityId); }

  /** Get a collection of  FacilityTypeAttr related entities. */
  public Collection getFacilityTypeAttrs() { return FacilityTypeAttrHelper.findByName(name); }
  /** Get the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public FacilityTypeAttr getFacilityTypeAttr(String facilityTypeId) { return FacilityTypeAttrHelper.findByPrimaryKey(facilityTypeId, name); }
  /** Remove  FacilityTypeAttr related entities. */
  public void removeFacilityTypeAttrs() { FacilityTypeAttrHelper.removeByName(name); }
  /** Remove the  FacilityTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityTypeAttr(String facilityTypeId) { FacilityTypeAttrHelper.removeByPrimaryKey(facilityTypeId, name); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(facilityAttribute!=null) return facilityAttribute.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(facilityAttribute!=null) return facilityAttribute.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(facilityAttribute!=null) return facilityAttribute.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(facilityAttribute!=null) return facilityAttribute.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(facilityAttribute!=null) facilityAttribute.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
