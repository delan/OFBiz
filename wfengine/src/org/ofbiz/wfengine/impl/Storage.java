/* $Id$
 * @(#)Storage.java   Sun Aug 12 13:22:40 GMT+02:00 2001
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Oliver Wieland (wieland.oliver@t-online.de)
 * @created Sun Aug 12 13:22:40 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */
package org.ofbiz.wfengine.impl;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.wfengine.WFException;



/**
 * Base class for all storage types.
 * @author Oliver Wieland
 * @version 1.0
 */

public class Storage implements Serializable  {

	
	// Attribute instance 'user'
	private String user;
	
	// Attribute instance 'password'
	private String password;
	
	// Attribute instance 'driver'
	private String driver;
	
	
		
	/**
	 * Empty constructor
	 */
	Storage() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pUser Initial value for attribute 'user'
	 * @param pPassword Initial value for attribute 'password'
	 * @param pDriver Initial value for attribute 'driver'
	 */
	Storage(
		String pUser,
		String pPassword,
		String pDriver) {		
				
		user = pUser;		
		password = pPassword;		
		driver = pDriver;
	}
		
	/**
	 * Getter for attribute 'user'
	 * 
	 * @return Value of attribute user
	 */
	public String getUser()  {
		return user;
	}
	
	/**
	 * Setter for attribute 'user'
	 * 
	 * @param pUser Neuer Wert des Attributes user
	 */
	public void setUser(String pUser)  {
		if (user == pUser) return;		
		if ( !notifyAttributeChangeUser( pUser ) ) return;
		user = pUser;
	}
	
	/**
	 * This method is called, before the attribute 'User' is set to a new
	 * value.
	 * @param pUser New Value for attribute 'User'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeUser(String pUser) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'password'
	 * 
	 * @return Value of attribute password
	 */
	public String getPassword()  {
		return password;
	}
	
	/**
	 * Setter for attribute 'password'
	 * 
	 * @param pPassword Neuer Wert des Attributes password
	 */
	public void setPassword(String pPassword)  {
		if (password == pPassword) return;		
		if ( !notifyAttributeChangePassword( pPassword ) ) return;
		password = pPassword;
	}
	
	/**
	 * This method is called, before the attribute 'Password' is set to a new
	 * value.
	 * @param pPassword New Value for attribute 'Password'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangePassword(String pPassword) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'driver'
	 * 
	 * @return Value of attribute driver
	 */
	public String getDriver()  {
		return driver;
	}
	
	/**
	 * Setter for attribute 'driver'
	 * 
	 * @param pDriver Neuer Wert des Attributes driver
	 */
	public void setDriver(String pDriver)  {
		if (driver == pDriver) return;		
		if ( !notifyAttributeChangeDriver( pDriver ) ) return;
		driver = pDriver;
	}
	
	/**
	 * This method is called, before the attribute 'Driver' is set to a new
	 * value.
	 * @param pDriver New Value for attribute 'Driver'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeDriver(String pDriver) {		
		return true;
	}
	
	/**
	 * Method load
	 * @param pUrl Value for parameter Url
	 */
	public void load(String pUrl)  throws Throwable {				
		// !!NO_CODE!!
		/* NULL */;
	}
	
	/**
	 * Method save
	 * @param pUrl Value for parameter Url
	 */
	public void save(String pUrl) {				
		// !!NO_CODE!!
		/* NULL */;
	}
	

	// Link attribute of association 'Engine '
			
	private WFEngine engine;

	/**
	 * Getter of association 'Engine'
	 * @return Current value of association 'Engine'.
	 * @throws RuntimeException, if value is null
	 */
	public WFEngine getEngine() {
		if (engine == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: Storage#Engine is null!");
		}
		return engine;
	}

	/**
	 * Setter of association 'Engine'.
	 * @param pEngine New value for association 'Engine'
	 */
	public void setEngine (WFEngine pEngine) {
		if (pEngine == null && engine != null) {
			engine.unlinkStorage( this );
		}
		engine = pEngine;		 
		engine.linkStorage(this);
	}
		
	/**
	 * Checks, if aggregate 'Engine' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isEngineNull() {		
		return engine == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkEngine(WFEngine pEngine) {		
		
		if (engine != null) {
			engine.unlinkStorage(this); // Alte Beziehung löschen
		}
		engine = pEngine;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkEngine(WFEngine pEngine) {
		engine = null;
		
	}	

	/**
	 * String representation of Storage
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("Storage");	
		return lRet.toString();
	}
}








