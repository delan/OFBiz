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
package org.ofbiz.pos.jpos.service;

import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

import jpos.JposException;
import jpos.POSKeyboardConst;
import jpos.JposConst;
import jpos.events.DataEvent;
import jpos.services.EventCallbacks;

import org.ofbiz.pos.adaptor.KeyboardReceiver;
import org.ofbiz.pos.adaptor.KeyboardAdaptor;
import org.ofbiz.base.util.Debug;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.2
 */
public class KeyboardService extends BaseKybService implements jpos.services.POSKeyboardService17, KeyboardReceiver, KeyListener {

    public static final String module = KeyboardService.class.getName();

    protected boolean autoDisable = false;
    protected boolean received = false;

    protected int eventTypes = POSKeyboardConst.KBD_ET_DOWN;
    protected int keyEvent = -1;
    protected int keyData = -1;

    protected Map keyMapping = null;

    public KeyboardService() {
        KeyboardAdaptor.getInstance(this, KeyboardAdaptor.KEYBOARD_DATA);
    }

    public void open(String deviceName, EventCallbacks ecb) throws JposException {
        super.open(deviceName, ecb);

        // setup the key mapping
        this.keyMapping = new HashMap();
        Enumeration props = entry.getPropertyNames();
        while (props.hasMoreElements()) {
            String propName = (String) props.nextElement();
            if (propName.startsWith("key.")) {
				String propValue = (String) entry.getPropertyValue(propName);
                propName = propName.substring(4);

                PosKey key = new PosKey(propName, propValue);
                keyMapping.put(key.getKeyName(), key);
            }
        }
    }

    // POSKeyboardService12
    public boolean getCapKeyUp() throws JposException {
        // we support both up/down events
        return true;
    }

    public boolean getAutoDisable() throws JposException {
        return this.autoDisable;
    }

    public void setAutoDisable(boolean b) throws JposException {
        this.autoDisable = b;
    }

    public int getEventTypes() throws JposException {
        return this.eventTypes;
    }

    public void setEventTypes(int i) throws JposException {
        this.eventTypes = i;
    }

    public int getPOSKeyData() throws JposException {
        if (!received) {
            throw new JposException(JposConst.JPOS_PS_UNKNOWN, "No data received");
        }
        return keyData;
    }

    public int getPOSKeyEventType() throws JposException {
        if (!received) {
            throw new JposException(JposConst.JPOS_PS_UNKNOWN, "No data received");
        }
        return this.keyEvent;
    }

    public void clearInput() throws JposException {
        this.keyEvent = -1;
        this.keyData = -1;
        this.received = false;
    }

    // POSKeyboardService13
    public int getCapPowerReporting() throws JposException {
        return 0;
    }

    public int getPowerNotify() throws JposException {
        return 0;
    }

    public void setPowerNotify(int i) throws JposException {
    }

    public int getPowerState() throws JposException {
        return 0;
    }

    // KeyboardReceiver
    public void receiveData(int[] codes, char[] chars) {
        this.received = true;

        // fire off the event notification
        DataEvent event = new DataEvent(this, 0);
        this.fireEvent(event);
    }

    // KeyListener
    public void keyPressed(KeyEvent event) {
        if (keyMapping != null) {
            PosKey thisKey = new PosKey(event);
            PosKey mappedKey = (PosKey) keyMapping.get(thisKey.getKeyName());
            if (mappedKey != null) {
                this.keyEvent = POSKeyboardConst.KBD_KET_KEYDOWN;
                this.keyData = mappedKey.getMappedCode();
            }
        }
    }

    public void keyTyped(KeyEvent event) {
    }

    public void keyReleased(KeyEvent event) {
        if (this.eventTypes == POSKeyboardConst.KBD_ET_DOWN_UP) {
            if (keyMapping != null) {
                PosKey thisKey = new PosKey(event);
                PosKey mappedKey = (PosKey) keyMapping.get(thisKey.getKeyName());
                if (mappedKey != null) {
                    this.keyEvent = POSKeyboardConst.KBD_KET_KEYDOWN;
                    this.keyData = mappedKey.getMappedCode();
                }
            }
        }
    }

    class PosKey {

        private int keyCode, mappedCode;
        private boolean alt, ctrl, shift;

        public PosKey(KeyEvent event) {
            this.keyCode = event.getKeyCode();
            this.mappedCode = -1;

            // the follow method of checking for button pressed was recommended by JavaDoc 1.4
            this.shift = (event.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) > 0 ? true : false;
            this.ctrl = (event.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0 ? true : false;
            this.alt = (event.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) > 0 ? true : false;
        }

        public PosKey(String keyName, String mappedValue) throws JposException {
            String keyDef = null;
            String keyMod = null;
            if (keyName.indexOf("+") != -1) {
                keyDef = keyName.substring (0, keyName.indexOf("+")).trim();
                keyMod = keyName.substring(keyName.indexOf("+") + 1);
            } else {
                keyDef = keyName;
            }

            // set the keycode
            if (keyDef.startsWith("0x")) {
                try {
                    this.keyCode = Integer.parseInt(keyDef.substring(2), 16);
                } catch (Throwable t) {
                    Debug.logError(t, module);
                    throw new JposException(JposConst.JPOS_E_ILLEGAL, "Illegal hex code key definition [" + keyName + "]");
                }
            } else if (keyDef.startsWith("VK_")) {
                try {
                    Field kef = KeyEvent.class.getField(keyDef);
                    this.keyCode = kef.getInt(kef);
                } catch (Throwable t) {
                    Debug.logError(t, module);
                    throw new JposException(JposConst.JPOS_E_ILLEGAL, "Illegal virtual key definition [" + keyName + "]");
                }
            } else {
                try {
                    this.keyCode = Integer.parseInt(keyDef);
                } catch (Throwable t) {
                    Debug.logError(t, module);
                    throw new JposException(JposConst.JPOS_E_ILLEGAL, "Illegal key code definition [" + keyName + "]");
                }
            }

            // set the key modifiers
            String[] modifiers = null;
            if (keyMod != null && keyMod.length() > 0) {
                if (keyMod.indexOf("+") != -1) {
                    modifiers = keyMod.split("\\+");
                } else {
                    modifiers = new String[1];
                    modifiers[0] = keyMod;
                }
                for (int i = 0; i < modifiers.length; i++) {
                    if ("SHIFT".equalsIgnoreCase(modifiers[i])) {
                        this.shift = true;
                    } else {
                        this.shift = false;
                    }
                    if ("CTRL".equalsIgnoreCase(modifiers[i])) {
                        this.ctrl = true;
                    } else {
                        this.ctrl = false;
                    }
                    if ("ALT".equalsIgnoreCase(modifiers[i])) {
                        this.alt = true;
                        this.alt = false;
                    }
                }
            }

            // set the mapped value
            try {
                this.mappedCode = Integer.parseInt(mappedValue);
            } catch (Throwable t) {
                Debug.logError(t, module);
                throw new JposException(JposConst.JPOS_E_ILLEGAL, "Illegal key code mapping [" + mappedValue + "]");
            }
        }

        public int getMappedCode() {
            return mappedCode;
        }

        public String getKeyName() {
            String name = new String();
            if (shift) name = name + "S";
            if (ctrl) name = name + "C";
            if (alt) name = name + "A";
            return name + "_" + keyCode;
        }
    }
}
