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
package org.ofbiz.pos.adaptor;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;

import org.apache.commons.collections.map.LinkedMap;


/**
 * KeyboardAdaptor - Handles reading keyboard input
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class KeyboardAdaptor {

    public static final String module = KeyboardAdaptor.class.getName();

    public static final int EVENT_RELEASED = 2;
    public static final int EVENT_PRESSED = 1;
    public static final int EVENT_TYPED = 3;

    public static final int KEYBOARD_DATA = 100;
    public static final int SCANNER_DATA = 101;
    public static final int MSR_DATA = 102;
    public static final int ALL_DATA = 999;

    protected static List loadedComponents = new LinkedList();
    protected static Map receivers = new LinkedMap();
    protected static KeyboardAdaptor adaptor = null;
    protected static boolean running = true;

    protected KeyboardListener listener = null;

    public static KeyboardAdaptor getInstance(KeyboardReceiver receiver, int dataType) {
        if (adaptor == null) {
            synchronized(KeyboardAdaptor.class) {
                if (adaptor == null) {
                    adaptor = new KeyboardAdaptor();
                }
            }
        }

        receivers.put(receiver, new Integer(dataType));
        return adaptor;
    }

    public static void attachComponents(Component[] coms) {
        // add the new components to listen on
        if (adaptor != null && coms != null) {
            adaptor.addComponents(coms);
        }
    }

    public static void stop() {
        running = false;
    }

    private KeyboardAdaptor() {
        this.listener = new KeyboardListener();
        this.listener.setDaemon(false);
        this.listener.setName(listener.toString());
        this.listener.start();
        KeyboardAdaptor.adaptor = this;
    }

    private void addComponents(Component[] coms) {
        listener.reader.configureComponents(coms);
    }

    private class KeyboardListener extends Thread {

        private static final long MAX_WAIT = 100;
        private List keyCodeData = new LinkedList();
        private List keyCharData = new LinkedList();
        private long lastKey = -1;
        private KeyReader reader = null;

        public KeyboardListener() {
            this.reader = new KeyReader(this);
        }

        private int checkDataType(char[] chars) {
            if (chars.length == 0) {
                // non-character data from keyboard interface (i.e. FN keys, enter, esc, etc)
                return KEYBOARD_DATA;
            } else if (((int) chars[0]) == 2 && ((int) chars[chars.length - 1]) == 10) {
                // test for scanner data
                return SCANNER_DATA;
            } else if (((int) chars[0]) == 37 && ((int) chars[chars.length - 1]) == 10) {
                // test for MSR data
                return MSR_DATA;
            } else {
                // otherwise it's keyboard data
                return KEYBOARD_DATA;
            }
        }

        protected synchronized void receiveCode(int keycode) {
            keyCodeData.add(new Integer(keycode));
        }

        protected synchronized void receiveChar(char keychar) {
            keyCharData.add(new Character(keychar));
        }

        protected synchronized void sendData() {
            if (KeyboardAdaptor.receivers.size() > 0) {
                if (keyCharData.size() > 0 || keyCodeData.size() > 0) {
                    char[] chars = new char[keyCharData.size()];
                    int[] codes = new int[keyCodeData.size()];

                    for (int i = 0; i < codes.length; i++) {
                        Integer itg = (Integer) keyCodeData.get(i);
                        codes[i] = itg.intValue();
                    }

                    for (int i = 0; i < chars.length; i++) {
                        Character ch = (Character) keyCharData.get(i);
                        chars[i] = ch.charValue();
                    }

                    Iterator ri = KeyboardAdaptor.receivers.keySet().iterator();
                    while (ri.hasNext()) {
                        KeyboardReceiver receiver = (KeyboardReceiver) ri.next();
                        int receiverType = ((Integer) receivers.get(receiver)).intValue();
                        int thisDataType = this.checkDataType(chars);
                        if (receiverType == ALL_DATA || receiverType == thisDataType) {
                            receiver.receiveData(codes, chars);
                        }
                    }

                    keyCharData = new LinkedList();
                    keyCodeData = new LinkedList();
                    lastKey = -1;
                }
            } else {
                Debug.logWarning("No receivers configured for key input", module);
            }
        }

        protected synchronized void sendEvent(int eventType, KeyEvent event) {
            lastKey = System.currentTimeMillis();
            if (KeyboardAdaptor.receivers.size() > 0) {
                Iterator ri = KeyboardAdaptor.receivers.keySet().iterator();
                while (ri.hasNext()) {
                    KeyboardReceiver receiver = (KeyboardReceiver) ri.next();
                    if (receiver instanceof KeyListener) {
                        switch (eventType) {
                            case 1:
                                ((KeyListener) receiver).keyPressed(event);
                                break;
                            case 2:
                                ((KeyListener) receiver).keyTyped(event);
                                break;
                            case 3:
                                ((KeyListener) receiver).keyReleased(event);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

        public void run() {
            while (running) {
                long now = System.currentTimeMillis();
                if ((lastKey > -1) && (now - lastKey) >= MAX_WAIT) {
                    this.sendData();
                }

                if (!running) {
                    break;
                } else {
                    try {
                        Thread.sleep(MAX_WAIT);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    class KeyReader implements KeyListener {

        private KeyboardListener k;

        public KeyReader(KeyboardListener k) {
            this.k = k;
        }

        private void configureComponents(Component[] coms) {
            for (int i = 0; i < coms.length; i++) {
                if (!loadedComponents.contains(coms[i])) {
                    coms[i].addKeyListener(this);
                }
                if (coms[i] instanceof Container) {
                    Component[] nextComs = ((Container) coms[i]).getComponents();
                    configureComponents(nextComs);
                }
            }
        }

        public void keyTyped(KeyEvent e) {
            k.receiveChar(e.getKeyChar());
            k.sendEvent(EVENT_TYPED, e);
        }

        public void keyPressed(KeyEvent e) {
            k.receiveCode(e.getKeyCode());
            k.sendEvent(EVENT_PRESSED, e);
        }

        public void keyReleased(KeyEvent e) {
            k.sendEvent(EVENT_RELEASED, e);
        }
    }
}
