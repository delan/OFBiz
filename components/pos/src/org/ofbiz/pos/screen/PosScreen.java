/*
 * $Id: PosScreen.java,v 1.3 2004/08/10 18:58:57 ajzeneski Exp $
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

import net.xoetrope.builder.NavigationHelper;
import net.xoetrope.xui.XPage;
import net.xoetrope.xui.XProjectManager;

import org.ofbiz.base.util.Debug;
import org.ofbiz.content.xui.XuiContainer;
import org.ofbiz.content.xui.XuiSession;
import org.ofbiz.pos.component.Input;
import org.ofbiz.pos.component.Journal;
import org.ofbiz.pos.component.Output;
import org.ofbiz.pos.component.PosButton;
import org.ofbiz.pos.device.DeviceLoader;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.3 $
 * @since      3.1
 */
public class PosScreen extends NavigationHelper implements Runnable {

    public static final String module = PosScreen.class.getName();
    public static final String BUTTON_ACTION_METHOD = "buttonPressed";
    public static final long MAX_INACTIVITY = 1800000;

    protected static boolean monitorRunning = false;
    protected static boolean isLocked = false;
    protected static boolean initialized = false;

    protected static Thread activityMonitor = null;
    protected static long lastActivity = 0;

    protected static XuiSession session = null;
    protected static Output output = null;
    protected static Input input = null;
    protected static Journal journal = null;
    protected PosButton buttons = null;

    public void pageCreated() {
        super.pageCreated();

        // initial settings
        this.setEnabled(false);
        this.setVisible(false);

        if (!initialized) {
            initialized = true;

            // setup the shared components
            session = XuiContainer.getSession("pos-1");
            output = new Output(this);
            input = new Input(this);
            journal = new Journal(this);
            lastActivity = System.currentTimeMillis();

            // create the monitor thread
            activityMonitor = new Thread(this);
            activityMonitor.setDaemon(false);

            // load the shared devices
            try {
                DeviceLoader.load(this);
            } catch (Exception e) {
                Debug.logError(e, module);
            }

            // pre-load a few screens
            XProjectManager.getPageManager().loadPage("main/mgrpanel");
            XProjectManager.getPageManager().loadPage("main/promopanel");
        }

        // buttons are different per screen
        this.buttons = new PosButton(this);
    }

    public void pageActivated() {
        super.pageActivated();

        this.setEnabled(true);
        this.setVisible(true);
        this.setLastActivity(System.currentTimeMillis());
        if (session.getUserLogin() == null) {
            this.setLock(true);
        } else {
            if (!monitorRunning) {
                monitorRunning = true;
                activityMonitor.start();
            }
        }
        this.repaint();
        journal.focus();
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLock(boolean lock) {
        this.buttons.setLock(lock);
        input.setLock(lock);
        output.setLock(lock);
        journal.setLock(lock);
        isLocked = lock;
        input.setFunction("LOGIN");
    }

    public XuiSession getSession() {
        return session;
    }

    public Input getInput() {
        return input;
    }

    public Output getOutput() {
        return output;
    }

    public Journal getJournal() {
        return journal;
    }

    public PosButton getButtons() {
        return buttons;
    }

    public void setLastActivity(long l) {
        lastActivity = l;
    }

    // generic button XUI event calls into PosButton to lookup the external reference
    public void buttonPressed() {
        this.setLastActivity(System.currentTimeMillis());
        buttons.buttonPressed(this);
        journal.focus();
    }

    // generic page display methods - extends those in XPage
    public void showPage(String pageName) {
        XProjectManager.getPageManager().showPage(pageName);
    }

    public void showDialog(String pageName) {
        XPage dialogPage = XProjectManager.getPageManager().loadPage(pageName);        
        PosDialog dialog = PosDialog.getInstance(dialogPage);
        dialog.showDialog(this, "dialogCb");
    }

    // PosDialog Callback method
    public void dialogCb(PosDialog dialog) {
        Debug.log("PosDialog Completed CB - " + dialog.getName(), module);
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
