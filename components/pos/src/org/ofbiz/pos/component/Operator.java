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

import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import net.xoetrope.xui.style.XStyle;
import net.xoetrope.xui.XProjectManager;
import net.xoetrope.swing.XEdit;
import net.xoetrope.swing.XPanel;

import org.ofbiz.pos.screen.PosScreen;
import org.ofbiz.pos.PosTransaction;
import org.ofbiz.base.util.UtilFormatOut;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      Aug 24, 2004
 */
public class Operator {

    public static final String module = Operator.class.getName();
    public static final String style = "operTitle";

    public static final String[] OPER_TOTAL = { "oper_total", "TOTAL" };
    public static final String[] OPER_DATE = { "oper_date", "DATE" };
    public static final String[] OPER_EMPL = { "oper_empl", "EMPL" };
    public static final String[] OPER_TXID = { "oper_txid", "TXID" };
    public static final String[] OPER_DRWR = { "oper_drwr", "DRAWER" };

    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd");
    protected Component[] operatorField = null;
    protected XStyle titleStyle = null;
    protected XPanel operPanel = null;

    public Operator(PosScreen page) {
        this.titleStyle = XProjectManager.getStyleManager().getStyle(style);
        this.operPanel = (XPanel) page.findComponent("oper_panel");
        this.operatorField = operPanel.getComponents();
        this.operPanel.setVisible(false);
        this.refresh();
    }

    public void setLock(boolean lock) {
        operPanel.setVisible(!lock);        
    }

    public void refresh() {
        for (int i = 0; i < operatorField.length; i++) {
            if (operatorField[i] instanceof XEdit) {
                this.setupField((XEdit) operatorField[i]);
                this.setFieldValue((XEdit) operatorField[i]);
            }
        }
    }

    protected void setupField(XEdit field) {
        Color titleColor = titleStyle.getStyleAsColor(XStyle.COLOR_FORE);
        String fontName = titleStyle.getStyleAsString(XStyle.FONT_FACE);
        int fontStyle = titleStyle.getStyleAsInt(XStyle.FONT_WEIGHT);
        int fontSize = titleStyle.getStyleAsInt(XStyle.FONT_SIZE);
        Font titleFont = new Font(fontName, fontStyle, fontSize);

        Border base = BorderFactory.createEtchedBorder();
        TitledBorder border = BorderFactory.createTitledBorder(base, this.getFieldTitle(field.getName()),
                TitledBorder.LEFT, TitledBorder.TOP, titleFont, titleColor);
        field.setBorder(border);
        field.setOpaque(true);
        field.setEditable(false);
    }

    protected void setFieldValue(XEdit field) {
        PosTransaction trans = null;
        if (operPanel.isVisible()) {
            trans = PosTransaction.getCurrentTx(PosScreen.currentScreen.getSession());
        }

        String fieldName = field.getName();
        if (OPER_TOTAL[0].equals(fieldName)) {
            String total = "0.00";
            if (trans != null) {
                total = UtilFormatOut.formatPrice(trans.getTotalDue());
            }
            field.setText(total);
        } else if (OPER_DATE[0].equals(fieldName)) {
            field.setText(sdf.format(new Date()));
        } else if (OPER_EMPL[0].equals(fieldName)) {
            String userId = "NA";
            if (trans != null) {
                userId = PosScreen.currentScreen.getSession().getUserId();
            }
            field.setText(userId);
        } else if (OPER_TXID[0].equals(fieldName)) {
            String txId = "NA";
            if (trans != null) {
                txId = trans.getTransactionId();
            }
            field.setText(txId);
        } else if (OPER_DRWR[0].equals(fieldName)) {
            String drawer = "0";
            if (trans != null) {
                drawer = "" + trans.getDrawerNumber();
            }
            field.setText(drawer);
        }
    }

    protected String getFieldTitle(String fieldName) {
        if (OPER_TOTAL[0].equals(fieldName)) {
            return OPER_TOTAL[1];
        } else if (OPER_DATE[0].equals(fieldName)) {
            return OPER_DATE[1];
        } else if (OPER_EMPL[0].equals(fieldName)) {
            return OPER_EMPL[1];
        } else if (OPER_TXID[0].equals(fieldName)) {
            return OPER_TXID[1];
        } else if (OPER_DRWR[0].equals(fieldName)) {
            return OPER_DRWR[1];
        }
        return "";
    }

}
