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
package org.ofbiz.pos.component;

import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.xoetrope.swing.XButton;
import net.xoetrope.xui.helper.SwingWorker;

import org.ofbiz.base.config.GenericConfigException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.pos.config.ButtonEventConfig;
import org.ofbiz.pos.screen.PosScreen;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public class PosButton {

    public static final String module = PosButton.class.getName();

    protected Map loadedXButtons = new HashMap();
    protected PosScreen pos = null;

    public PosButton(PosScreen pos) {
        this.pos = pos;
        this.loadButtons(pos.getComponents());

        try {
            ButtonEventConfig.loadButtonConfig();
        } catch (GenericConfigException e) {
            Debug.logError(e, module);
        }
    }

    private void loadButtons(Component[] component) {
        for (int i = 0; i < component.length; i++) {
            if (component[i] instanceof XButton) {
                XButton button = (XButton) component[i];
                PosButtonWrapper wrapper = new PosButtonWrapper(button);
                if (UtilValidate.isEmpty(button.getName())) {
                    wrapper.setEnabled(false);
                } else {
                    pos.addActionHandler(button, PosScreen.BUTTON_ACTION_METHOD);
                    loadedXButtons.put(button.getName(), wrapper);
                }
            }
            if (component[i] instanceof Container) {
                Component[] subComponents = ((Container) component[i]).getComponents();
                loadButtons(subComponents);
            }
        }
    }

    public boolean isLockable(String name) {
        if (!loadedXButtons.containsKey(name)) {
            return false;
        }

        return ButtonEventConfig.isLockable(name);
    }

    public void setLock(boolean lock) {
        Iterator i = loadedXButtons.keySet().iterator();
        while (i.hasNext()) {
            String buttonName = (String) i.next();
            if (this.isLockable(buttonName)) {
                PosButtonWrapper button = (PosButtonWrapper) loadedXButtons.get(buttonName);
                button.setEnabled(!lock);
            }
        }
    }

    public void buttonPressed(final PosScreen pos) {
        final String buttonName = ButtonEventConfig.getButtonName(pos);
        if (buttonName != null) {
            final SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    try {
                        ButtonEventConfig.invokeButtonEvent(buttonName, pos);
                    } catch (ButtonEventConfig.ButtonEventNotFound e) {
                        Debug.logWarning(e.getMessage(), module);
                    } catch (ButtonEventConfig.ButtonEventException e) {
                        Debug.logError(e, module);
                    }
                    return null;
                }
            };
            worker.start();
        }
    }
}
