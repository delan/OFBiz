/*
 * $Id: Input.java,v 1.3 2004/08/07 01:23:07 ajzeneski Exp $
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
import java.util.EmptyStackException;
import java.util.Iterator;

import net.xoetrope.swing.XEdit;
import net.xoetrope.xui.XPage;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.collections.LifoSet;
import org.ofbiz.pos.screen.PosScreen;
import org.ofbiz.pos.adaptor.KeyboardAdaptor;
import org.ofbiz.pos.adaptor.KeyboardReceiver;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.3 $
 * @since      3.1
 */
public class Input implements KeyboardReceiver {

    public static final String module = Input.class.getName();
    private static final String[] validFunc = { "LOGIN", "UNLOCK", "MGRLOGIN", "PLU", "TOTAL", "CREDIT", "CREDITINFO", "CHECK", "CHECKINFO", "QTY" };

    protected LifoSet functionStack = new LifoSet();
    protected Component[] pageComs = null;
    protected PosScreen pos = null;
    protected Color lastColor = null;
    protected XEdit input = null;
    protected boolean isLocked = false;

    public Input(XPage page) {
        this.pageComs = page.getComponents();
        this.pos = (PosScreen) page;
        this.input = (XEdit) page.findComponent("pos_input");
        this.input.setFocusable(false);

        // initialize the KeyboardAdaptor
        KeyboardAdaptor.setInput(this);
        KeyboardAdaptor.getInstance(this, KeyboardAdaptor.KEYBOARD_DATA);
    }

    public Component[] getComponents() {
        return pageComs;
    }

    public void setLock(boolean lock) {
        // hide the input text
        if (lock) {
            lastColor = this.input.getForeground();
            input.setForeground(this.input.getBackground());
        } else {
            input.setForeground(this.lastColor);
        }
        isLocked = lock;
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

    public void receiveData(int[] codes, char[] chars) {
        this.appendString(new String(chars));
    }

}
