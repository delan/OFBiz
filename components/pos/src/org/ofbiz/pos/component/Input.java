/*
 * $Id: Input.java,v 1.1 2004/07/27 18:37:36 ajzeneski Exp $
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
package org.ofbiz.pos.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EmptyStackException;
import java.util.Iterator;

import net.xoetrope.swing.XEdit;
import net.xoetrope.xui.XPage;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.collections.LifoSet;
import org.ofbiz.pos.screen.PosScreen;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class Input implements KeyListener {

    public static final String module = Input.class.getName();
    private static final String[] validFunc = { "LOGIN", "UNLOCK", "MGRLOGIN", "PLU", "TOTAL", "CREDIT", "CREDITINFO", "CHECK", "CHECKINFO", "QTY" };

    protected LifoSet functionStack = new LifoSet();
    protected PosScreen pos = null;
    protected Color lastColor = null;
    protected XEdit input = null;
    protected boolean isLocked = false;

    public Input(XPage page) {
        this.pos = (PosScreen) page;
        this.input = (XEdit) page.findComponent("pos_input");
        this.focus();

        // attache this to all components as a key listener
        Component[] pageComs = page.getComponents();
        configureComponentListener(pageComs);
    }

    private void configureComponentListener(Component[] coms) {
        for (int i = 0; i < coms.length; i++) {
            coms[i].addKeyListener(this);
            if (coms[i] instanceof Container) {
                Component[] nextComs = ((Container) coms[i]).getComponents();
                configureComponentListener(nextComs);
            }
        }
    }

    public void setLock(boolean lock) {
        // hide the input text
        if (lock) {
            lastColor = this.input.getForeground();
            input.setForeground(this.input.getBackground());
        } else {
            input.setForeground(this.lastColor);
        }
        input.setFocusable(!lock);
        isLocked = lock;
    }

    public void focus() {
        if (!this.isLocked) {
            input.requestFocus();
        }
    }

    public void setFunction(String function, String value) throws IllegalArgumentException {
        if (isValidFunction(function)) {
            this.functionStack.push( new String[] { function, value });
            input.setText("");
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setFunction(String function) throws IllegalArgumentException {
        setFunction(function, input.getText());    
    }

    private boolean isValidFunction(String function) {
        for (int i = 0; i < validFunc.length; i++) {
            if (validFunc[i].equals(function)) {
                return true;
            }
        }
        return false;
    }

    public String[] getLastFunction() {
        String[] f = null;
        try {
            f = (String[]) this.functionStack.peek();
        } catch (EmptyStackException e) {            
        }
        return f;
    }

    public String[] clearLastFunction() {
        String[] f = null;
        try {
            f = (String[]) this.functionStack.pop();
        } catch (EmptyStackException e) {
        }
        return f;
    }

    public String[] getFunction(String function) {
        Iterator i = functionStack.iterator();
        while (i.hasNext()) {
            String[] func = (String[]) i.next();
            if (func[0].equals(function)) {
                return func;
            }
        }
        return null;
    }

    public String[] clearFunction(String function) {
        Iterator i = functionStack.iterator();
        while (i.hasNext()) {
            String[] func = (String[]) i.next();
            if (func[0].equals(function)) {
                i.remove();
                return func;
            }
        }
        return null;
    }

    public boolean isFunctionSet(String function) {
        Iterator i = functionStack.iterator();
        while (i.hasNext()) {
            String func[] = (String[]) i.next();
            if (func[0].equals(function)) {
                return true;
            }
        }
        return false;
    }

    public void clearInput() {
        input.setText("");
    }

    public void clear() {
        input.setText("");
        functionStack.clear();
        Debug.log("Stack Size : " + functionStack.size(), module);
    }

    public String value() {
        return input.getText();
    }

    public void appendChar(char c) {
        input.setText(this.input.getText() + c);
    }

    public void appendString(String str) {
        input.setText(this.input.getText() + str);
    }

    public void keyPressed(KeyEvent event) {
        pos.setLastActivity(System.currentTimeMillis());
        if (!this.input.hasFocus()) {
            this.focus();
            this.appendChar(event.getKeyChar());
        }
    }

    public void keyTyped(KeyEvent event) {
    }

    public void keyReleased(KeyEvent event) {
    }
}
