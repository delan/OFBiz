/*
 * $Id: PosScreen.java,v 1.1 2004/07/27 18:37:39 ajzeneski Exp $
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

import org.ofbiz.content.xui.XuiContainer;
import org.ofbiz.content.xui.XuiSession;
import org.ofbiz.pos.component.Input;
import org.ofbiz.pos.component.Journal;
import org.ofbiz.pos.component.Output;
import org.ofbiz.pos.component.PosButton;
import org.ofbiz.base.util.Debug;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class PosScreen extends NavigationHelper implements Runnable {

    public static final String module = PosScreen.class.getName();
    public static final long maxInactivity = 1800000;

    protected Output output = null;
    protected Input input = null;
    protected Journal journal = null;
    protected PosButton buttons = null;
    protected XuiSession session = null;
    protected boolean isLocked = false;
    protected long lastActivity = 0;
    protected Thread activityMonitor = null;
    protected boolean monitorRunning = false;


    public void pageCreated() {
        this.session = XuiContainer.getSession("pos-1");
        this.output = new Output(this);
        this.input = new Input(this);
        this.journal = new Journal(this);
        this.buttons = new PosButton(this);
        this.lastActivity = System.currentTimeMillis();

        // create the monitor thread
        this.activityMonitor = new Thread(this);
        this.activityMonitor.setDaemon(false);
    }

    public void pageActivated() {
        this.setLastActivity(System.currentTimeMillis());
        if (session.getUserLogin() == null) {
            this.setLock(true);
        } else {            
            if (!monitorRunning) {
                this.monitorRunning = true;
                this.activityMonitor.start();
            }
        }
        this.repaint();
        this.input.focus();
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLock(boolean lock) {
        this.buttons.setLock(lock);
        this.input.setLock(lock);
        this.output.setLock(lock);
        this.journal.setLock(lock);
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
        this.buttons.buttonPressed(this);
        this.input.focus();
    }

    // run method for auto-locking POS on inactivity
    public void run() {
        while (monitorRunning) {
            if (!isLocked && (System.currentTimeMillis() - lastActivity) > maxInactivity) {
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
