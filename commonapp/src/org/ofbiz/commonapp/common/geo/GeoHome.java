
package org.ofbiz.commonapp.common.geo;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import java.math.*;

/**
 * <p><b>Title:</b> Geographic Boundary Entity
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
 *@created    Fri Jul 27 01:18:21 MDT 2001
 *@version    1.0
 */

public interface GeoHome extends EJBHome
{

  public Geo create(String geoId, String geoTypeId, String name, String geoCode, String abbreviation) throws RemoteException, CreateException;
  public Geo create(String geoId) throws RemoteException, CreateException;
  public Geo findByPrimaryKey(java.lang.String primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;


  /**
   *  Finds Geos by the following fields:
   *

   *@param  geoTypeId                  Field for the GEO_TYPE_ID column.
   *@return      Collection containing the found Geos
   */
  public Collection findByGeoTypeId(String geoTypeId) throws RemoteException, FinderException;

  /**
   *  Finds Geos by the following fields:
   *

   *@param  name                  Field for the NAME column.
   *@return      Collection containing the found Geos
   */
  public Collection findByName(String name) throws RemoteException, FinderException;

  /**
   *  Finds Geos by the following fields:
   *

   *@param  geoCode                  Field for the GEO_CODE column.
   *@return      Collection containing the found Geos
   */
  public Collection findByGeoCode(String geoCode) throws RemoteException, FinderException;

  /**
   *  Finds Geos by the following fields:
   *

   *@param  abbreviation                  Field for the ABBREVIATION column.
   *@return      Collection containing the found Geos
   */
  public Collection findByAbbreviation(String abbreviation) throws RemoteException, FinderException;

}
