
package org.ofbiz.commonapp.common.uom;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Unit Of Measure Type Entity
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
public class UomTypeValue implements UomType
{
  /** The variable of the UOM_TYPE_ID column of the UOM_TYPE table. */
  private String uomTypeId;
  /** The variable of the PARENT_TYPE_ID column of the UOM_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the UOM_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the UOM_TYPE table. */
  private String description;

  private UomType uomType;

  public UomTypeValue()
  {
    this.uomTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.uomType = null;
  }

  public UomTypeValue(UomType uomType) throws RemoteException
  {
    if(uomType == null) return;
  
    this.uomTypeId = uomType.getUomTypeId();
    this.parentTypeId = uomType.getParentTypeId();
    this.hasTable = uomType.getHasTable();
    this.description = uomType.getDescription();

    this.uomType = uomType;
  }

  public UomTypeValue(UomType uomType, String uomTypeId, String parentTypeId, String hasTable, String description)
  {
    if(uomType == null) return;
  
    this.uomTypeId = uomTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.uomType = uomType;
  }


  /** Get the primary key of the UOM_TYPE_ID column of the UOM_TYPE table. */
  public String getUomTypeId()  throws RemoteException { return uomTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the UOM_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the UOM_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(uomType!=null) uomType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the UOM_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the UOM_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(uomType!=null) uomType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the UOM_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the UOM_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(uomType!=null) uomType.setDescription(description);
  }

  /** Get the value object of the UomType class. */
  public UomType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the UomType class. */
  public void setValueObject(UomType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(uomType!=null) uomType.setValueObject(valueObject);

    if(uomTypeId == null) uomTypeId = valueObject.getUomTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent UomType entity corresponding to this entity. */
  public UomType getParentUomType() { return UomTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent UomType entity corresponding to this entity. */
  public void removeParentUomType() { UomTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child UomType related entities. */
  public Collection getChildUomTypes() { return UomTypeHelper.findByParentTypeId(uomTypeId); }
  /** Get the Child UomType keyed by member(s) of this class, and other passed parameters. */
  public UomType getChildUomType(String uomTypeId) { return UomTypeHelper.findByPrimaryKey(uomTypeId); }
  /** Remove Child UomType related entities. */
  public void removeChildUomTypes() { UomTypeHelper.removeByParentTypeId(uomTypeId); }
  /** Remove the Child UomType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildUomType(String uomTypeId) { UomTypeHelper.removeByPrimaryKey(uomTypeId); }

  /** Get a collection of  Uom related entities. */
  public Collection getUoms() { return UomHelper.findByUomTypeId(uomTypeId); }
  /** Get the  Uom keyed by member(s) of this class, and other passed parameters. */
  public Uom getUom(String uomId) { return UomHelper.findByPrimaryKey(uomId); }
  /** Remove  Uom related entities. */
  public void removeUoms() { UomHelper.removeByUomTypeId(uomTypeId); }
  /** Remove the  Uom keyed by member(s) of this class, and other passed parameters. */
  public void removeUom(String uomId) { UomHelper.removeByPrimaryKey(uomId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(uomType!=null) return uomType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(uomType!=null) return uomType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(uomType!=null) return uomType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(uomType!=null) return uomType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(uomType!=null) uomType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
