
package org.ofbiz.commonapp.common.uom;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Unit Of Measure Entity
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
 *@created    Fri Jul 27 01:18:22 MDT 2001
 *@version    1.0
 */
public class UomValue implements Uom
{
  /** The variable of the UOM_ID column of the UOM table. */
  private String uomId;
  /** The variable of the UOM_TYPE_ID column of the UOM table. */
  private String uomTypeId;
  /** The variable of the ABBREVIATION column of the UOM table. */
  private String abbreviation;
  /** The variable of the DESCRIPTION column of the UOM table. */
  private String description;

  private Uom uom;

  public UomValue()
  {
    this.uomId = null;
    this.uomTypeId = null;
    this.abbreviation = null;
    this.description = null;

    this.uom = null;
  }

  public UomValue(Uom uom) throws RemoteException
  {
    if(uom == null) return;
  
    this.uomId = uom.getUomId();
    this.uomTypeId = uom.getUomTypeId();
    this.abbreviation = uom.getAbbreviation();
    this.description = uom.getDescription();

    this.uom = uom;
  }

  public UomValue(Uom uom, String uomId, String uomTypeId, String abbreviation, String description)
  {
    if(uom == null) return;
  
    this.uomId = uomId;
    this.uomTypeId = uomTypeId;
    this.abbreviation = abbreviation;
    this.description = description;

    this.uom = uom;
  }


  /** Get the primary key of the UOM_ID column of the UOM table. */
  public String getUomId()  throws RemoteException { return uomId; }

  /** Get the value of the UOM_TYPE_ID column of the UOM table. */
  public String getUomTypeId() throws RemoteException { return uomTypeId; }
  /** Set the value of the UOM_TYPE_ID column of the UOM table. */
  public void setUomTypeId(String uomTypeId) throws RemoteException
  {
    this.uomTypeId = uomTypeId;
    if(uom!=null) uom.setUomTypeId(uomTypeId);
  }

  /** Get the value of the ABBREVIATION column of the UOM table. */
  public String getAbbreviation() throws RemoteException { return abbreviation; }
  /** Set the value of the ABBREVIATION column of the UOM table. */
  public void setAbbreviation(String abbreviation) throws RemoteException
  {
    this.abbreviation = abbreviation;
    if(uom!=null) uom.setAbbreviation(abbreviation);
  }

  /** Get the value of the DESCRIPTION column of the UOM table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the UOM table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(uom!=null) uom.setDescription(description);
  }

  /** Get the value object of the Uom class. */
  public Uom getValueObject() throws RemoteException { return this; }
  /** Set the value object of the Uom class. */
  public void setValueObject(Uom valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(uom!=null) uom.setValueObject(valueObject);

    if(uomId == null) uomId = valueObject.getUomId();
    uomTypeId = valueObject.getUomTypeId();
    abbreviation = valueObject.getAbbreviation();
    description = valueObject.getDescription();
  }


  /** Get the  UomType entity corresponding to this entity. */
  public UomType getUomType() { return UomTypeHelper.findByPrimaryKey(uomTypeId); }
  /** Remove the  UomType entity corresponding to this entity. */
  public void removeUomType() { UomTypeHelper.removeByPrimaryKey(uomTypeId); }

  /** Get a collection of Main UomConversion related entities. */
  public Collection getMainUomConversions() { return UomConversionHelper.findByUomId(uomId); }
  /** Get the Main UomConversion keyed by member(s) of this class, and other passed parameters. */
  public UomConversion getMainUomConversion(String uomIdTo) { return UomConversionHelper.findByPrimaryKey(uomId, uomIdTo); }
  /** Remove Main UomConversion related entities. */
  public void removeMainUomConversions() { UomConversionHelper.removeByUomId(uomId); }
  /** Remove the Main UomConversion keyed by member(s) of this class, and other passed parameters. */
  public void removeMainUomConversion(String uomIdTo) { UomConversionHelper.removeByPrimaryKey(uomId, uomIdTo); }

  /** Get a collection of ConvTo UomConversion related entities. */
  public Collection getConvToUomConversions() { return UomConversionHelper.findByUomIdTo(uomId); }
  /** Get the ConvTo UomConversion keyed by member(s) of this class, and other passed parameters. */
  public UomConversion getConvToUomConversion(String uomId) { return UomConversionHelper.findByPrimaryKey(uomId, uomId); }
  /** Remove ConvTo UomConversion related entities. */
  public void removeConvToUomConversions() { UomConversionHelper.removeByUomIdTo(uomId); }
  /** Remove the ConvTo UomConversion keyed by member(s) of this class, and other passed parameters. */
  public void removeConvToUomConversion(String uomId) { UomConversionHelper.removeByPrimaryKey(uomId, uomId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(uom!=null) return uom.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(uom!=null) return uom.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(uom!=null) return uom.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(uom!=null) return uom.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(uom!=null) uom.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
