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

import jpos.JposException;

import org.ofbiz.base.util.Debug;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class NullPosPrinter extends BaseService implements jpos.services.POSPrinterService12 {

    public int getDeviceServiceVersion() throws JposException {
        return 1002000;
    }
    
    public int getCapCharacterSet() throws JposException {
        return 0;
    }

    public boolean getCapConcurrentJrnRec() throws JposException {
        return false;
    }

    public boolean getCapConcurrentJrnSlp() throws JposException {
        return false;
    }

    public boolean getCapConcurrentRecSlp() throws JposException {
        return false;
    }

    public boolean getCapCoverSensor() throws JposException {
        return false;
    }

    public boolean getCapJrn2Color() throws JposException {
        return false;
    }

    public boolean getCapJrnBold() throws JposException {
        return false;
    }

    public boolean getCapJrnDhigh() throws JposException {
        return false;
    }

    public boolean getCapJrnDwide() throws JposException {
        return false;
    }

    public boolean getCapJrnDwideDhigh() throws JposException {
        return false;
    }

    public boolean getCapJrnEmptySensor() throws JposException {
        return false;
    }

    public boolean getCapJrnItalic() throws JposException {
        return false;
    }

    public boolean getCapJrnNearEndSensor() throws JposException {
        return false;
    }

    public boolean getCapJrnPresent() throws JposException {
        return false;
    }

    public boolean getCapJrnUnderline() throws JposException {
        return false;
    }

    public boolean getCapRec2Color() throws JposException {
        return false;
    }

    public boolean getCapRecBarCode() throws JposException {
        return false;
    }

    public boolean getCapRecBitmap() throws JposException {
        return false;
    }

    public boolean getCapRecBold() throws JposException {
        return false;
    }

    public boolean getCapRecDhigh() throws JposException {
        return false;
    }

    public boolean getCapRecDwide() throws JposException {
        return false;
    }

    public boolean getCapRecDwideDhigh() throws JposException {
        return false;
    }

    public boolean getCapRecEmptySensor() throws JposException {
        return false;
    }

    public boolean getCapRecItalic() throws JposException {
        return false;
    }

    public boolean getCapRecLeft90() throws JposException {
        return false;
    }

    public boolean getCapRecNearEndSensor() throws JposException {
        return false;
    }

    public boolean getCapRecPapercut() throws JposException {
        return false;
    }

    public boolean getCapRecPresent() throws JposException {
        return false;
    }

    public boolean getCapRecRight90() throws JposException {
        return false;
    }

    public boolean getCapRecRotate180() throws JposException {
        return false;
    }

    public boolean getCapRecStamp() throws JposException {
        return false;
    }

    public boolean getCapRecUnderline() throws JposException {
        return false;
    }

    public boolean getCapSlp2Color() throws JposException {
        return false;
    }

    public boolean getCapSlpBarCode() throws JposException {
        return false;
    }

    public boolean getCapSlpBitmap() throws JposException {
        return false;
    }

    public boolean getCapSlpBold() throws JposException {
        return false;
    }

    public boolean getCapSlpDhigh() throws JposException {
        return false;
    }

    public boolean getCapSlpDwide() throws JposException {
        return false;
    }

    public boolean getCapSlpDwideDhigh() throws JposException {
        return false;
    }

    public boolean getCapSlpEmptySensor() throws JposException {
        return false;
    }

    public boolean getCapSlpFullslip() throws JposException {
        return false;
    }

    public boolean getCapSlpItalic() throws JposException {
        return false;
    }

    public boolean getCapSlpLeft90() throws JposException {
        return false;
    }

    public boolean getCapSlpNearEndSensor() throws JposException {
        return false;
    }

    public boolean getCapSlpPresent() throws JposException {
        return false;
    }

    public boolean getCapSlpRight90() throws JposException {
        return false;
    }

    public boolean getCapSlpRotate180() throws JposException {
        return false;
    }

    public boolean getCapSlpUnderline() throws JposException {
        return false;
    }

    public boolean getCapTransaction() throws JposException {
        return false;
    }

    public boolean getAsyncMode() throws JposException {
        return false;
    }

    public void setAsyncMode(boolean b) throws JposException {
    }

    public int getCharacterSet() throws JposException {
        return 0;
    }

    public void setCharacterSet(int i) throws JposException {
    }

    public String getCharacterSetList() throws JposException {
        return null;
    }

    public boolean getCoverOpen() throws JposException {
        return false;
    }

    public int getErrorLevel() throws JposException {
        return 0;
    }

    public int getErrorStation() throws JposException {
        return 0;
    }

    public String getErrorString() throws JposException {
        return null;
    }

    public boolean getFlagWhenIdle() throws JposException {
        return false;
    }

    public void setFlagWhenIdle(boolean b) throws JposException {
    }

    public String getFontTypefaceList() throws JposException {
        return null;
    }

    public boolean getJrnEmpty() throws JposException {
        return false;
    }

    public boolean getJrnLetterQuality() throws JposException {
        return false;
    }

    public void setJrnLetterQuality(boolean b) throws JposException {
    }

    public int getJrnLineChars() throws JposException {
        return 0;
    }

    public void setJrnLineChars(int i) throws JposException {
    }

    public String getJrnLineCharsList() throws JposException {
        return null;
    }

    public int getJrnLineHeight() throws JposException {
        return 0;
    }

    public void setJrnLineHeight(int i) throws JposException {
    }

    public int getJrnLineSpacing() throws JposException {
        return 0;
    }

    public void setJrnLineSpacing(int i) throws JposException {
    }

    public int getJrnLineWidth() throws JposException {
        return 0;
    }

    public boolean getJrnNearEnd() throws JposException {
        return false;
    }

    public int getMapMode() throws JposException {
        return 0;
    }

    public void setMapMode(int i) throws JposException {
    }

    public int getOutputID() throws JposException {
        return 0;
    }

    public String getRecBarCodeRotationList() throws JposException {
        return null;
    }

    public boolean getRecEmpty() throws JposException {
        return false;
    }

    public boolean getRecLetterQuality() throws JposException {
        return false;
    }

    public void setRecLetterQuality(boolean b) throws JposException {
    }

    public int getRecLineChars() throws JposException {
        return 0;
    }

    public void setRecLineChars(int i) throws JposException {
    }

    public String getRecLineCharsList() throws JposException {
        return null;
    }

    public int getRecLineHeight() throws JposException {
        return 0;
    }

    public void setRecLineHeight(int i) throws JposException {
    }

    public int getRecLineSpacing() throws JposException {
        return 0;
    }

    public void setRecLineSpacing(int i) throws JposException {
    }

    public int getRecLinesToPaperCut() throws JposException {
        return 0;
    }

    public int getRecLineWidth() throws JposException {
        return 0;
    }

    public boolean getRecNearEnd() throws JposException {
        return false;
    }

    public int getRecSidewaysMaxChars() throws JposException {
        return 0;
    }

    public int getRecSidewaysMaxLines() throws JposException {
        return 0;
    }

    public int getRotateSpecial() throws JposException {
        return 0;
    }

    public void setRotateSpecial(int i) throws JposException {
    }

    public String getSlpBarCodeRotationList() throws JposException {
        return null;
    }

    public boolean getSlpEmpty() throws JposException {
        return false;
    }

    public boolean getSlpLetterQuality() throws JposException {
        return false;
    }

    public void setSlpLetterQuality(boolean b) throws JposException {
    }

    public int getSlpLineChars() throws JposException {
        return 0;
    }

    public void setSlpLineChars(int i) throws JposException {
    }

    public String getSlpLineCharsList() throws JposException {
        return null;
    }

    public int getSlpLineHeight() throws JposException {
        return 0;
    }

    public void setSlpLineHeight(int i) throws JposException {
    }

    public int getSlpLinesNearEndToEnd() throws JposException {
        return 0;
    }

    public int getSlpLineSpacing() throws JposException {
        return 0;
    }

    public void setSlpLineSpacing(int i) throws JposException {
    }

    public int getSlpLineWidth() throws JposException {
        return 0;
    }

    public int getSlpMaxLines() throws JposException {
        return 0;
    }

    public boolean getSlpNearEnd() throws JposException {
        return false;
    }

    public int getSlpSidewaysMaxChars() throws JposException {
        return 0;
    }

    public int getSlpSidewaysMaxLines() throws JposException {
        return 0;
    }

    public void beginInsertion(int i) throws JposException {
    }

    public void beginRemoval(int i) throws JposException {
    }

    public void clearOutput() throws JposException {
    }

    public void cutPaper(int i) throws JposException {
    }

    public void endInsertion() throws JposException {
    }

    public void endRemoval() throws JposException {
    }

    public void printBarCode(int i, String s, int i1, int i2, int i3, int i4, int i5) throws JposException {
        Debug.log("Barcode:\n" + s + "\n", module);
    }

    public void printBitmap(int i, String s, int i1, int i2) throws JposException {
        Debug.log("Bitmap:\n" + s + "\n", module);
    }

    public void printImmediate(int i, String s) throws JposException {
        Debug.log("Immediate:\n" + s + "\n", module);
    }

    public void printNormal(int i, String s) throws JposException {
        Debug.log("Normal:\n" + s + "\n", module);
    }

    public void printTwoNormal(int i, String s, String s1) throws JposException {
        Debug.log("2Normal:\n" + s + "\n", module);
    }

    public void rotatePrint(int i, int i1) throws JposException {
    }

    public void setBitmap(int i, int i1, String s, int i2, int i3) throws JposException {
    }

    public void setLogo(int i, String s) throws JposException {
    }

    public void transactionPrint(int i, int i1) throws JposException {
    }

    public void validateData(int i, String s) throws JposException {
    }
}