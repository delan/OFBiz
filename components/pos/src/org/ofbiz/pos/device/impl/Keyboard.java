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
package org.ofbiz.pos.device.impl;

import java.util.List;

import jpos.JposException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.pos.adaptor.DataEventAdaptor;
import org.ofbiz.pos.config.ButtonEventConfig;
import org.ofbiz.pos.device.GenericDevice;
import org.ofbiz.pos.screen.PosScreen;

/**
 * Keyboard Key -> Button Mapping Tool
 *
 * This class will invoke button events based on a key press.
 * The key -> code mapping is handled in the jpos.xml file.
 * The code -> button mapping is handled in the buttonconfig.xml file.
 * It is advised to map to key codes > 200.
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class Keyboard extends GenericDevice {

    public static final String module = CashDrawer.class.getName();

    public Keyboard(String deviceName, int timeout) {
        super(deviceName, timeout);
        this.control = new jpos.POSKeyboard();
    }

    protected void initialize() throws JposException {
        Debug.logInfo("Keyboard [" + control.getPhysicalDeviceName() + "] Claimed : " + control.getClaimed(), module);
        final jpos.POSKeyboard keyboard = (jpos.POSKeyboard) control;

        keyboard.addDataListener(new DataEventAdaptor() {
            public void dataOccurred(jpos.events.DataEvent event) {
                Debug.log("POSKeyboard DataEvent - " + event.getWhen(), module);
                try {
                    int keyCode = keyboard.getPOSKeyData();
                    Debug.log("Received KeyCode From POSKeyboard DataEvent : " + keyCode, module);

                    // -1 is not valid
                    if (keyCode == -1) {
                        return;
                    }

                    // check for button mapping
                    List buttonEvents = ButtonEventConfig.findButtonKeyAssign(keyCode);
                    if (buttonEvents != null && buttonEvents.size() > 0) {
                        Debug.log("Key -> Button Mapping(s) Found [" + keyCode + "]", module);
                        try {
                            ButtonEventConfig.invokeButtonEvents(buttonEvents, PosScreen.currentScreen);
                        } catch (ButtonEventConfig.ButtonEventNotFound e) {
                            Debug.logError(e, module);
                        } catch (ButtonEventConfig.ButtonEventException e) {
                            Debug.logError(e, module);
                        }
                    } else {
                        Debug.logWarning("No key-code button mappings found for key-code [" + keyCode + "]", module);
                    }
                } catch (JposException e) {
                    Debug.logError(e, module);
                }
            }
        });
    }
}
