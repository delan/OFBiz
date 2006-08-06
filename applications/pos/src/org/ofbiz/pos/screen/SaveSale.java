/*
 * Copyright 2001-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.pos.screen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.xoetrope.swing.XButton;
import net.xoetrope.swing.XDialog;
import net.xoetrope.swing.XEdit;
import net.xoetrope.xui.XPage;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.pos.PosTransaction;

/**
 * 
 * @author     <a href="mailto:jleroux@apache.org">Jacques Le Roux</a>
 * @version    $Rev$
 * @since      3.2
 */

public class SaveSale extends XPage {

	/**
	 * To save a sale. 2 modes : save and keep the current sale or save and clear the current sale.  
	 */
	public static final String module = SaveSale.class.getName();
	protected static PosScreen m_pos = null;
	protected XDialog m_dialog = null;	
	protected XEdit m_saleName = null;
	protected XButton m_cancel = null;	
	protected XButton m_save = null;
	protected XButton m_saveAndClear = null;
	protected static PosTransaction m_trans = null;	
	public static SimpleDateFormat sdf = new SimpleDateFormat(UtilProperties.getMessage("pos","DateTimeFormat",Locale.getDefault()));

	//TODO : make getter and setter for members (ie m_*) if needed (extern calls)
	
	public SaveSale(PosTransaction trans, PosScreen page) {
		m_trans = trans;
		m_pos = page;
	}

    public void openDlg() {
    	XDialog dlg = (XDialog) pageMgr.loadPage(m_pos.getScreenLocation() + "/dialog/savesale");
    	m_dialog = dlg;
    	dlg.setModal(true);
    	dlg.setCaption(UtilProperties.getMessage("pos", "SaveASale", Locale.getDefault()));
    	m_saleName = (XEdit) dlg.findComponent("saleName");
    	m_saleName.setText(m_pos.session.getUserId() + " " + sdf.format(new Date()));

    	m_cancel = (XButton) dlg.findComponent("BtnCancel");
    	m_save = (XButton) dlg.findComponent("BtnSave");    	    	
    	m_saveAndClear = (XButton) dlg.findComponent("BtnSaveAndClear");

    	addMouseHandler(m_cancel, "cancel");
    	addMouseHandler(m_save, "save");
    	addMouseHandler(m_saveAndClear, "saveAndClear");
    	
    	dlg.pack();
    	dlg.showDialog(this);
    }

    public synchronized void cancel()
    {
    	if (wasMouseClicked()) {
    		this.m_dialog.closeDlg();
    	}
    }

    public synchronized void save() {
    	if (wasMouseClicked()) {
    		String sale = m_saleName.getText();
    		if (null != sale) {
    			saveSale(sale);
    		}
    	}
    }

    public synchronized void saveAndClear() {
    	if (wasMouseClicked()) {
    		String sale = m_saleName.getText();
    		if (null != sale) {
    			saveSale(sale);
    			m_trans.voidSale();
    			m_pos.refresh();
    		}
    	}
    }
      
    private void saveSale(String sale) {
        final ClassLoader cl = this.getClassLoader(m_pos);
        Thread.currentThread().setContextClassLoader(cl);    	
		m_trans.saveSale(sale, m_pos);
    	this.m_dialog.closeDlg();    	
    }
    
    private ClassLoader getClassLoader(PosScreen pos) {
        ClassLoader cl = pos.getClassLoader();
        if (cl == null) {
            try {
                cl = Thread.currentThread().getContextClassLoader();
            } catch (Throwable t) {
            }
            if (cl == null) {
                Debug.log("No context classloader available; using class classloader", module);
                try {
                    cl = this.getClass().getClassLoader();
                } catch (Throwable t) {
                    Debug.logError(t, module);
                }
            }
        }
        return cl;
    }    
}