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
package org.ofbiz.pos.config;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.awt.AWTEvent;

import net.xoetrope.swing.XButton;

import org.ofbiz.base.config.GenericConfigException;
import org.ofbiz.base.config.ResourceLoader;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.pos.screen.PosScreen;

import org.w3c.dom.Element;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public class ButtonEventConfig implements java.io.Serializable {

    public static final String module = ButtonEventConfig.class.getName();
    public static final String BUTTON_EVENT_CONFIG = "buttonevents.xml";   
    private static transient UtilCache buttonConfig = new UtilCache("pos.ButtonEvent", 0, 0, 0, false, true);

    protected String buttonName = null;
    protected String className = null;
    protected String method = null;
    protected int keyCode = -1;
    protected boolean disableLock = false;

    public static void loadButtonConfig() throws GenericConfigException {
        Element root = ResourceLoader.getXmlRootElement(ButtonEventConfig.BUTTON_EVENT_CONFIG);
        List buttonEvents = UtilXml.childElementList(root, "event");
        if (!UtilValidate.isEmpty(buttonEvents)) {
            Iterator i = buttonEvents.iterator();
            while (i.hasNext()) {
                Element e = (Element) i.next();
                ButtonEventConfig bef = new ButtonEventConfig(e);
                buttonConfig.put(bef.getName(), bef);
            }
        }
    }

    public static boolean isLockable(String buttonName) {
        ButtonEventConfig bef = (ButtonEventConfig) buttonConfig.get(buttonName);
        if (bef == null) {
            return true;
        }
        return bef.isLockable();
    }

    public static void invokeButtonEvents(List buttonNames, PosScreen pos) throws ButtonEventNotFound, ButtonEventException {
        if (buttonNames != null) {
            Iterator i = buttonNames.iterator();
            while (i.hasNext()) {
                invokeButtonEvent(((String) i.next()), pos, null);
            }
        }
    }

    public static void invokeButtonEvent(String buttonName, PosScreen pos, AWTEvent event) throws ButtonEventNotFound, ButtonEventException {
        // load / re-load the button configs
        if (buttonConfig.size() == 0) {
            synchronized(ButtonEventConfig.class) {
                try {
                    loadButtonConfig();
                } catch (GenericConfigException e) {
                    Debug.logError(e, module);
                }
            }
        }

        // check for sku mapping buttons
        if (buttonName.startsWith("SKU.")) {
            buttonName = "menuSku";
        }

        // invoke the button event
        ButtonEventConfig bef = (ButtonEventConfig) buttonConfig.get(buttonName);
        if (bef == null) {
            throw new ButtonEventNotFound("No button definition found for button - " + buttonName);
        }
        bef.invoke(pos, event);
    }

    public static List findButtonKeyAssign(int keyCode) {
        List buttonAssignments = new ArrayList();
        Iterator i = buttonConfig.values().iterator();
        while (i.hasNext()) {
            ButtonEventConfig bef = (ButtonEventConfig) i.next();
            if (bef.getKeyCode() == keyCode) {
                buttonAssignments.add(bef.getName());
            }
        }

        return buttonAssignments;
    }

    public static String getButtonName(AWTEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("AWTEvent parameter cannot be null");
        }
        Object source = event.getSource();
        if (source instanceof XButton) {
            XButton button = (XButton) source;
            return button.getName();
        } else {
            return null;
        }
    }

    protected ButtonEventConfig() {
    }

    protected ButtonEventConfig(Element element) {
        this.buttonName = element.getAttribute("button-name");
        this.className = element.getAttribute("class-name");
        this.method = element.getAttribute("method-name");
        String keyCodeStr = element.getAttribute("key-code");
        if (UtilValidate.isNotEmpty(keyCodeStr)) {
            try {
                this.keyCode = Integer.parseInt(keyCodeStr);
            } catch (Throwable t) {
                Debug.logWarning("Key code definition for button [" + buttonName + "] is not a valid Integer", module);
            }
        }
        this.disableLock = "true".equals(element.getAttribute("disable-lock"));
    }

    public String getName() {
        return this.buttonName;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public boolean isLockable() {
        return !disableLock;
    }

    public void invoke(PosScreen pos, AWTEvent event) throws ButtonEventNotFound, ButtonEventException {
        ClassLoader cl = this.getClass().getClassLoader();

        // two variations are available
        Class[] paramTypes1 = new Class[] { PosScreen.class, AWTEvent.class };
        Class[] paramTypes2 = new Class[] { PosScreen.class };
        Object[] params1 = new Object[] { pos, event };
        Object[] params2 = new Object[] { pos };

        // load the class
        Class buttonClass = null;
        try {
            buttonClass = cl.loadClass(this.className);
        } catch (ClassNotFoundException e) {
            throw new ButtonEventNotFound(e);
        }

        // load the method
        Method buttonMethod = null;
        int methodType = 0;
        try {
            buttonMethod = buttonClass.getMethod(this.method, paramTypes1);
            methodType = 1;
        } catch (NoSuchMethodException e) {
            try {
                buttonMethod = buttonClass.getMethod(this.method, paramTypes2);
                methodType = 2;
            } catch (NoSuchMethodException e2) {
                Debug.logError(e2, "No method found with matching signatures - " + this.buttonName, module);
                throw new ButtonEventNotFound(e);
            }
        }

        // invoke the method
        try {
            switch (methodType) {
                case 1:
                    buttonMethod.invoke(null, params1);
                    break;
                case 2:
                    buttonMethod.invoke(null, params2);
                    break;
                default:
                    throw new ButtonEventNotFound("No class method found for button - " + this.buttonName);
            }
        } catch (IllegalAccessException e) {
            throw new ButtonEventException(e);
        } catch (InvocationTargetException e) {
            throw new ButtonEventException(e);
        } catch (Throwable t) {
            throw new ButtonEventException(t);
        }
    }

    public static class ButtonEventNotFound extends GeneralException {
        public ButtonEventNotFound() {
            super();
        }

        public ButtonEventNotFound(String str) {
            super(str);
        }

        public ButtonEventNotFound(String str, Throwable nested) {
            super(str, nested);
        }

        public ButtonEventNotFound(Throwable nested) {
            super(nested);
        }
    }

    public static class ButtonEventException extends GeneralException {
        public ButtonEventException() {
            super();
        }

        public ButtonEventException(String str) {
            super(str);
        }

        public ButtonEventException(String str, Throwable nested) {
            super(str, nested);
        }

        public ButtonEventException(Throwable nested) {
            super(nested);
        }
    }
}
