/*
 * $Id$
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.pos.screen;

import java.awt.Frame;

import net.xoetrope.builder.NavigationHelper;
import net.xoetrope.xui.XPage;
import net.xoetrope.xui.XProjectManager;
import net.xoetrope.xui.XResourceManager;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.content.xui.XuiContainer;
import org.ofbiz.content.xui.XuiSession;
import org.ofbiz.pos.component.Input;
import org.ofbiz.pos.component.Journal;
import org.ofbiz.pos.component.Output;
import org.ofbiz.pos.component.PosButton;
import org.ofbiz.pos.component.Operator;
import org.ofbiz.pos.device.DeviceLoader;
import org.ofbiz.pos.PosTransaction;
import org.ofbiz.pos.adaptor.KeyboardAdaptor;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public class PosScreen extends NavigationHelper implements Runnable, DialogCallback {

    public static final String module = PosScreen.class.getName();
    public static final Frame appFrame = XResourceManager.getAppFrame();
    public static final String BUTTON_ACTION_METHOD = "buttonPressed";
    public static final long MAX_INACTIVITY = 1800000;
    public static PosScreen currentScreen;

    protected static boolean deviceInit = false;
    protected boolean monitorRunning = false;
    protected boolean isLocked = false;

    protected Thread activityMonitor = null;
    protected long lastActivity = 0;

    protected XuiSession session = null;
    protected Output output = null;
    protected Input input = null;
    protected Journal journal = null;
    protected Operator operator = null;
    protected PosButton buttons = null;

    public void pageCreated() {
        super.pageCreated();

        // initial settings                
        this.setEnabled(false);
        this.setVisible(false);

        // setup the shared components
        this.session = XuiContainer.getSession("pos-1");
        this.output = new Output(this);
        this.input = new Input(this);
        this.journal = new Journal(this);
        this.operator = new Operator(this);
        this.lastActivity = System.currentTimeMillis();

        // create the monitor thread
        this.activityMonitor = new Thread(this);
        this.activityMonitor.setDaemon(false);

        if (!deviceInit) {
            deviceInit = true;

            // load the shared devices
            try {
                DeviceLoader.load(session);
            } catch (Exception e) {
                Debug.logError(e, module);
            }

            // pre-load a few screens
            XProjectManager.getPageManager().loadPage("main/paypanel");
            XProjectManager.getPageManager().loadPage("main/mgrpanel");
            XProjectManager.getPageManager().loadPage("main/promopanel");
        }

        // buttons are different per screen
        this.buttons = new PosButton(this);

        // make sure all components have the keyboard set
        KeyboardAdaptor.attachComponents(this.getComponents());
    }

    public void pageActivated() {
        super.pageActivated();

        this.setLastActivity(System.currentTimeMillis());
        if (session.getUserLogin() == null) {
            this.setLock(true);
        } else {
            this.setLock(isLocked);
            if (!monitorRunning) {
                monitorRunning = true;
                activityMonitor.start();
            }
        }

        currentScreen = this;
        this.refresh();      
    }

    public void pageDeactivated() {
        super.pageDeactivated();

        Debug.log("App Frame :", module);
        Debug.log("name    - " + appFrame.getName(), module);
        Debug.log("title   - " + appFrame.getTitle(), module);
        Debug.log("active  - " + appFrame.isActive(), module);
        Debug.log("enabled - " + appFrame.isEnabled(), module);
        Debug.log("visible - " + appFrame.isVisible(), module);
        Debug.log("opaque  - " + appFrame.isOpaque(), module);
    }

    public void refresh() {
        this.requestFocus();
        if (!isLocked) {
            this.setEnabled(true);
            this.setVisible(true);
            input.clearInput();
            operator.refresh();
            if (input.isFunctionSet("PAID")) {
                output.print(Output.CHANGE + UtilFormatOut.formatPrice((PosTransaction.getCurrentTx(this.getSession()).getTotalDue() * -1)));
            } else if (input.isFunctionSet("TOTAL")) {
                journal.refresh(this);
                output.print(Output.TOTALD + UtilFormatOut.formatPrice(PosTransaction.getCurrentTx(this.getSession()).getTotalDue()));
            } else {
                journal.refresh(this);
                output.print(Output.ISOPEN);
            }
        } else {
            output.print(Output.ULOGIN);
        }

        journal.focus();
        this.repaint();
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLock(boolean lock) {
        this.buttons.setLock(lock);
        this.input.setLock(lock);
        this.output.setLock(lock);
        this.journal.setLock(lock);
        this.operator.setLock(lock);
        this.isLocked = lock;
        this.input.setFunction("LOGIN");
    }

    public XuiSession getSession() {
        return this.session;
    }

    public Input getInput() {
        return this.input;
    }

    public Output getOutput() {
        return this.output;
    }

    public Journal getJournal() {
        return this.journal;
    }

    public PosButton getButtons() {
        return this.buttons;
    }

    public void setLastActivity(long l) {
        this.lastActivity = l;
    }

    // generic button XUI event calls into PosButton to lookup the external reference
    public void buttonPressed() {
        this.setLastActivity(System.currentTimeMillis());
        buttons.buttonPressed(this);
        journal.focus();
    }

    // generic page display methods - extends those in XPage
    public PosScreen showPage(String pageName) {
        XPage newPage = XProjectManager.getPageManager().showPage(pageName);
        if (newPage instanceof PosScreen) {
            ((PosScreen) newPage).refresh();
            return (PosScreen) newPage;
        }
        return null;
    }

    public PosDialog showDialog(String pageName) {
        return showDialog(pageName, this);
    }

    public PosDialog showDialog(String pageName, DialogCallback cb) {
        XPage dialogPage = XProjectManager.getPageManager().loadPage(pageName);        
        PosDialog dialog = PosDialog.getInstance(dialogPage);
        dialog.showDialog(this, cb);
        return dialog;
    }

    // PosDialog Callback method
    public void receiveDialogCb(PosDialog dialog) {
        this.refresh();
    }

    // run method for auto-locking POS on inactivity
    public void run() {
        while (monitorRunning) {
            if (!isLocked && (System.currentTimeMillis() - lastActivity) > MAX_INACTIVITY) {
                this.setLock(true);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Debug.logError(e, module);
            }
        }
    }
}
