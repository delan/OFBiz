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
package org.ofbiz.pos.event;

import org.ofbiz.pos.screen.PosScreen;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public class NumericEvents {

    public static final String module = NumericEvents.class.getName();

    // standard number events
    public static void triggerOne(PosScreen pos) {
        pos.getInput().appendString("1");
    }

    public static void triggerTwo(PosScreen pos) {
        pos.getInput().appendString("2");
    }

    public static void triggerThree(PosScreen pos) {
        pos.getInput().appendString("3");
    }

    public static void triggerFour(PosScreen pos) {
        pos.getInput().appendString("4");
    }

    public static void triggerFive(PosScreen pos) {
        pos.getInput().appendString("5");
    }

    public static void triggerSix(PosScreen pos) {
        pos.getInput().appendString("6");
    }

    public static void triggerSeven(PosScreen pos) {
        pos.getInput().appendString("7");
    }

    public static void triggerEight(PosScreen pos) {
        pos.getInput().appendString("8");
    }

    public static void triggerNine(PosScreen pos) {
        pos.getInput().appendString("9");
    }

    public static void triggerZero(PosScreen pos) {
        pos.getInput().appendString("0");
    }

    public static void triggerDZero(PosScreen pos) {
        pos.getInput().appendString("00");
    }

    public static void triggerPercent(PosScreen pos) {
        pos.getInput().appendString("%");
    }
}


