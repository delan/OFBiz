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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.xoetrope.swing.XButton;

import org.ofbiz.base.config.GenericConfigException;
import org.ofbiz.base.config.ResourceLoader;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.pos.screen.PosScreen;

import org.w3c.dom.Element;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public class ButtonEventConfig {

    public static final String module = ButtonEventConfig.class.getName();
    public static final String BUTTON_EVENT_CONFIG = "buttonevents.xml";
    private static Map buttonConfig = new HashMap();

    protected String buttonName = null;
    protected String className = null;
    protected String method = null;
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

    public static void invokeButtonEvent(String buttonName, PosScreen pos) throws ButtonEventNotFound, ButtonEventException {
        ButtonEventConfig bef = (ButtonEventConfig) buttonConfig.get(buttonName);
        if (bef == null) {
            throw new ButtonEventNotFound("No button definition found for button - " + buttonName);
        }
        bef.invoke(pos);
    }

    public static String getButtonName(PosScreen pos) {
        Object source = pos.getCurrentEvent().getSource();
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
        this.disableLock = "true".equals(element.getAttribute("disable-lock"));
    }

    public String getName() {
        return this.buttonName;
    }

    public boolean isLockable() {
        return !disableLock;
    }

    public void invoke(PosScreen pos) throws ButtonEventNotFound, ButtonEventException {
        ClassLoader cl = this.getClass().getClassLoader();

        Class[] paramTypes = new Class[] { PosScreen.class };
        Object[] params = new Object[] { pos };
        try {
            Class c = cl.loadClass(this.className);
            Method m = c.getMethod(this.method, paramTypes);
            m.invoke(null, params);
        } catch (NoSuchMethodException e) {
            throw new ButtonEventNotFound(e);
        } catch (ClassNotFoundException e) {
            throw new ButtonEventNotFound(e);
        } catch (InvocationTargetException e) {
            throw new ButtonEventException(e);
        } catch (IllegalAccessException e) {
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
