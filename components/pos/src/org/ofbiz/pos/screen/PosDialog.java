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
package org.ofbiz.pos.screen;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRootPane;

import net.xoetrope.swing.XButton;
import net.xoetrope.swing.XTextArea;
import net.xoetrope.xui.XPage;
import net.xoetrope.xui.XResourceManager;

import org.ofbiz.base.util.Debug;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class PosDialog {

    public static final String module = PosDialog.class.getName();
    protected static Map instances = new HashMap();

    protected final Frame clientFrame = XResourceManager.getAppFrame();
    protected final Window appWindow = XResourceManager.getAppWindow();

    protected DialogCallback cb = null;
    protected Component parent = null;

    protected JDialog dialog = null;
    protected XTextArea output = null;
    protected XPage page = null;
    protected boolean modal = true;
    protected int padding = 0;

    public static PosDialog getInstance(XPage page) {
        return getInstance(page, true, 0);
    }

    public static PosDialog getInstance(XPage page, boolean modal, int padding) {
        PosDialog dialog = (PosDialog) instances.get(page);
        if (dialog == null) {
            synchronized(PosDialog.class) {
                dialog = (PosDialog) instances.get(page);

                if (dialog == null) {
                    dialog = new PosDialog(page, modal, padding);
                    instances.put(page, dialog);
                }
            }
        }

        dialog.modal = modal;
        dialog.padding = padding;
        dialog.pack();
        return dialog;
    }

    protected PosDialog(XPage page, boolean modal, int padding) {
        this.page = page;
        this.modal = modal;
        this.padding = padding;
        this.configure();
    }

    protected void configure() {
        // create the new dialog box
        this.dialog = new JDialog(clientFrame, modal);
        dialog.setUndecorated(true);
        dialog.setSize(page.getSize());
        dialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

        // find the output edit object
        this.output = (XTextArea) page.findComponent("dialog_output");
        if (this.output != null) {
            this.output.setWrapStyleWord(true);
            this.output.setLineWrap(true);
            this.output.setEditable(false);
        }

        // set the components
        Component[] coms = page.getComponents();
        for (int i = 0; i < coms.length; i++) {
            dialog.getContentPane().add(coms[i]);
            coms[i].setVisible(true);
        }

        // set the close button
        this.setCloseBtn(dialog);

        // fix the layout and size
        this.pack();

        // adjust the dialog location
        Dimension wSize = dialog.getSize();
        dialog.setLocation(appWindow.getLocation().x + (appWindow.getSize().width / 2 - wSize.width / 2),
                appWindow.getLocation().y + (appWindow.getSize().height / 2 - wSize.height / 2));

        // set the window listener
        dialog.addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
                // always keep focus if we are enabled
                if (dialog.isEnabled()) {
                    dialog.requestFocus();
                }
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowOpened(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }
        });
    }

    public void showDialog(Container parent, DialogCallback cb, String text) {
        this.parent = parent;
        this.cb = cb;
        if (text != null) {
            this.setText(text);
        }

        // don't allow the main window to take focus
        appWindow.setFocusable(false);
        parent.setFocusable(false);

        dialog.setEnabled(true);
        dialog.requestFocus();
        dialog.repaint();
        dialog.setVisible(true);
    }

    public void setText(String text) {
        if (this.output != null) {
            Debug.log("Setting output text - " + text, module);
            this.output.setText(text);
        } else {
            Debug.log("PosDialog output edit box is NULL!", module);
        }
    }

    public String getName() {
        return page.getName();
    }

    protected void close() {
        // close down the dialog
        dialog.setVisible(false);
        dialog.setEnabled(false);

        // refocus the parent window
        appWindow.setFocusable(true);
        parent.setFocusable(true);
        parent.requestFocus();

        // callback the parent
        if (cb != null) {
            cb.receiveDialogCb(this);
        }
    }
    
    private void setCloseBtn(Container con) {
        Component[] coms = con.getComponents();
        for (int i = 0; i < coms.length; i++) {
            if (coms[i].getName() != null && "closeBtn".equals(coms[i].getName())) {
                if (coms[i] instanceof XButton) {
                    JButton b = (JButton) coms[i];
                    b.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            close();
                        }
                    });
                } else {
                    Debug.logWarning("Found component with name 'closeBtn' but was not an instance of JButton", module);
                }
            } else if (coms[i] instanceof Container) {
                setCloseBtn((Container) coms[i]);
            } else {
                coms[i].requestFocus();
            }
        }
    }

    private void pack() {
        dialog.pack();

        Dimension pageSize = page.getSize();
        if (pageSize.getHeight() > 0 || pageSize.getWidth() > 0) {
            dialog.setSize(page.getSize());
        } else {
            Container contentPane = dialog.getContentPane();
            Point size = this.getMaxCoordinates(contentPane);
            this.setSize(size.x + 2 * padding + 2, size.y + 2 * padding + 4);
        }
    }

    private void setSize(int width, int height) {
        dialog.getContentPane().setBounds(padding, padding, width - (padding * 2), height - (padding * 2));
        dialog.setSize(width, height);
    }

    private Point getMaxCoordinates(Container cont) {
        Point pt = cont.getLocation();

        int maxX = pt.x;
        int maxY = pt.y;
        int numChildren = cont.getComponentCount();

        for (int i = 0; i < numChildren; i++) {
            Component comp = cont.getComponent(i);
            Dimension size = comp.getSize();
            Point p = comp.getLocation();
            maxX = Math.max(pt.x + p.x + size.width, maxX);
            maxY = Math.max(pt.y + p.y + size.height, maxY);
            if (comp instanceof Container) {
                Point childDim = this.getMaxCoordinates((Container) comp);
                maxX = Math.max(childDim.x, maxX);
                maxY = Math.max(childDim.y, maxY);
            }
        }

        return new Point(maxX, maxY);
    }

}
