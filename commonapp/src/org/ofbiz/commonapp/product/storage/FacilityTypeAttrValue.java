
package org.ofbiz.commonapp.product.storage;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Facility Type Attribute Entity
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
public class FacilityTypeAttrValue implements FacilityTypeAttr
{
  /** The variable of the FACILITY_TYPE_ID column of the FACILITY_TYPE_ATTR table. */
  private String facilityTypeId;
  /** The variable of the NAME column of the FACILITY_TYPE_ATTR table. */
  private String name;

  private FacilityTypeAttr facilityTypeAttr;

  public FacilityTypeAttrValue()
  {
    this.facilityTypeId = null;
    this.name = null;

    this.facilityTypeAttr = null;
  }

  public FacilityTypeAttrValue(FacilityTypeAttr facilityTypeAttr) throws RemoteException
  {
    if(facilityTypeAttr == null) return;
  
    this.facilityTypeId = facilityTypeAttr.getFacilityTypeId();
    this.name = facilityTypeAttr.getName();

    this.facilityTypeAttr = facilityTypeAttr;
  }

  public FacilityTypeAttrValue(FacilityTypeAttr facilityTypeAttr, String facilityTypeId, String name)
  {
    if(facilityTypeAttr == null) return;
  
    this.facilityTypeId = facilityTypeId;
    this.name = name;

    this.facilityTypeAttr = facilityTypeAttr;
  }


  /** Get the primary key of the FACILITY_TYPE_ID column of the FACILITY_TYPE_ATTR table. */
  public String getFacilityTypeId()  throws RemoteException { return facilityTypeId; }

  /** Get the primary key of the NAME column of the FACILITY_TYPE_ATTR table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value object of the FacilityTypeAttr class. */
  public FacilityTypeAttr getValueObject() throws RemoteException { return this; }
  /** Set the value object of the FacilityTypeAttr class. */
  public void setValueObject(FacilityTypeAttr valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(facilityTypeAttr!=null) facilityTypeAttr.setValueObject(valueObject);

    if(facilityTypeId == null) facilityTypeId = valueObject.getFacilityTypeId();
    if(name == null) name = valueObject.getName();
  }


  /** Get the  FacilityType entity corresponding to this entity. */
  public FacilityType getFacilityType() { return FacilityTypeHelper.findByPrimaryKey(facilityTypeId); }
  /** Remove the  FacilityType entity corresponding to this entity. */
  public void removeFacilityType() { FacilityTypeHelper.removeByPrimaryKey(facilityTypeId); }

  /** Get a collection of  FacilityAttribute related entities. */
  public Collection getFacilityAttributes() { return FacilityAttributeHelper.findByName(name); }
  /** Get the  FacilityAttribute keyed by member(s) of this class, and other passed parameters. */
  public FacilityAttribute getFacilityAttribute(String facilityId) { return FacilityAttributeHelper.findByPrimaryKey(facilityId, name); }
  /** Remove  FacilityAttribute related entities. */
  public void removeFacilityAttributes() { FacilityAttributeHelper.removeByName(name); }
  /** Remove the  FacilityAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removeFacilityAttribute(String facilityId) { FacilityAttributeHelper.removeByPrimaryKey(facilityId, name); }

  /** Get a collection of  Facility related entities. */
  public Collection getFacilitys() { return FacilityHelper.findByFacilityTypeId(facilityTypeId); }
  /** Get the  Facility keyed by member(s) of this class, and other passed parameters. */
  public Facility getFacility(String facilityId) { return FacilityHelper.findByPrimaryKey(facilityId); }
  /** Remove  Facility related entities. */
  public void removeFacilitys() { FacilityHelper.removeByFacilityTypeId(facilityTypeId); }
  /** Remove the  Facility keyed by member(s) of this class, and other passed parameters. */
  public void removeFacility(String facilityId) { FacilityHelper.removeByPrimaryKey(facilityId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(facilityTypeAttr!=null) return facilityTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(facilityTypeAttr!=null) return facilityTypeAttr.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(facilityTypeAttr!=null) return facilityTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(facilityTypeAttr!=null) return facilityTypeAttr.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(facilityTypeAttr!=null) facilityTypeAttr.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
