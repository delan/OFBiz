
package org.ofbiz.commonapp.product.supplier;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.product.category.*;
import org.ofbiz.commonapp.party.party.*;

/**
 * <p><b>Title:</b> Market Interest Entity
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
 *@created    Fri Jul 27 01:18:34 MDT 2001
 *@version    1.0
 */
public class MarketInterestValue implements MarketInterest
{
  /** The variable of the PRODUCT_CATEGORY_ID column of the MARKET_INTEREST table. */
  private String productCategoryId;
  /** The variable of the PARTY_TYPE_ID column of the MARKET_INTEREST table. */
  private String partyTypeId;
  /** The variable of the FROM_DATE column of the MARKET_INTEREST table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the MARKET_INTEREST table. */
  private java.util.Date thruDate;

  private MarketInterest marketInterest;

  public MarketInterestValue()
  {
    this.productCategoryId = null;
    this.partyTypeId = null;
    this.fromDate = null;
    this.thruDate = null;

    this.marketInterest = null;
  }

  public MarketInterestValue(MarketInterest marketInterest) throws RemoteException
  {
    if(marketInterest == null) return;
  
    this.productCategoryId = marketInterest.getProductCategoryId();
    this.partyTypeId = marketInterest.getPartyTypeId();
    this.fromDate = marketInterest.getFromDate();
    this.thruDate = marketInterest.getThruDate();

    this.marketInterest = marketInterest;
  }

  public MarketInterestValue(MarketInterest marketInterest, String productCategoryId, String partyTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    if(marketInterest == null) return;
  
    this.productCategoryId = productCategoryId;
    this.partyTypeId = partyTypeId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;

    this.marketInterest = marketInterest;
  }


  /** Get the primary key of the PRODUCT_CATEGORY_ID column of the MARKET_INTEREST table. */
  public String getProductCategoryId()  throws RemoteException { return productCategoryId; }

  /** Get the primary key of the PARTY_TYPE_ID column of the MARKET_INTEREST table. */
  public String getPartyTypeId()  throws RemoteException { return partyTypeId; }

  /** Get the value of the FROM_DATE column of the MARKET_INTEREST table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the MARKET_INTEREST table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(marketInterest!=null) marketInterest.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the MARKET_INTEREST table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the MARKET_INTEREST table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(marketInterest!=null) marketInterest.setThruDate(thruDate);
  }

  /** Get the value object of the MarketInterest class. */
  public MarketInterest getValueObject() throws RemoteException { return this; }
  /** Set the value object of the MarketInterest class. */
  public void setValueObject(MarketInterest valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(marketInterest!=null) marketInterest.setValueObject(valueObject);

    if(productCategoryId == null) productCategoryId = valueObject.getProductCategoryId();
    if(partyTypeId == null) partyTypeId = valueObject.getPartyTypeId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
  }


  /** Get the  ProductCategory entity corresponding to this entity. */
  public ProductCategory getProductCategory() { return ProductCategoryHelper.findByPrimaryKey(productCategoryId); }
  /** Remove the  ProductCategory entity corresponding to this entity. */
  public void removeProductCategory() { ProductCategoryHelper.removeByPrimaryKey(productCategoryId); }

  /** Get the  PartyType entity corresponding to this entity. */
  public PartyType getPartyType() { return PartyTypeHelper.findByPrimaryKey(partyTypeId); }
  /** Remove the  PartyType entity corresponding to this entity. */
  public void removePartyType() { PartyTypeHelper.removeByPrimaryKey(partyTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(marketInterest!=null) return marketInterest.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(marketInterest!=null) return marketInterest.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(marketInterest!=null) return marketInterest.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(marketInterest!=null) return marketInterest.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(marketInterest!=null) marketInterest.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
