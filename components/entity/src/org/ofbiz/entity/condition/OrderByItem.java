/*
 * $Id: OrderByItem.java,v 1.2 2004/07/20 15:05:45 doogie Exp $
 *
 * <p>Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.entity.condition;

public class OrderByItem {
    public static final int DEFAULT = 0;
    public static final int UPPER   = 1;
    public static final int LOWER   = 2;

    public boolean descending;
    public int caseSensitivity;
    public String field;

    public OrderByItem() {
    }

    public OrderByItem(String text) {
        parse(text);
    }

    public final void parse(String text) {
        text = text.trim();
        int startIndex = 0, endIndex = text.length();
        if (text.endsWith(" DESC")) {
            descending = true;
            endIndex -= 5;
        } else if (text.endsWith(" ASC")) {
            descending = false;
            endIndex -= 4;
        } else if (text.startsWith("-")) {
            descending = true;
            startIndex++;
        } else if (text.startsWith("+")) {
            descending = false;
            startIndex++;
        } else {
            descending = false;
        }

        if (startIndex != 0 || endIndex != text.length()) {
            text = text.substring(startIndex, endIndex);
            startIndex = 0;
            endIndex = text.length();
        }

        if (text.endsWith(")")) {
            String upperText = text.toUpperCase();
            endIndex--;
            if (upperText.startsWith("UPPER(")) {
                caseSensitivity = UPPER;
                startIndex = 6;
            } else if (upperText.startsWith("LOWER(")) {
                caseSensitivity = LOWER;
                startIndex = 6;
            } else {
                caseSensitivity = DEFAULT;
            }
        } else {
            caseSensitivity = DEFAULT;
        }

        if (startIndex != 0 || endIndex != text.length()) {
            text = text.substring(startIndex, endIndex);
            startIndex = 0;
            endIndex = text.length();
        }

        field = text;
    }

    public String toString() {
        return toString(null);
    }

    public String toString(String fieldPrefix) {
        StringBuffer sb = new StringBuffer();
        switch (caseSensitivity) {
            case UPPER:     sb.append("UPPER("); break;
            case LOWER:     sb.append("LOWER("); break;
        }
        if (fieldPrefix != null) sb.append(fieldPrefix);
        sb.append(field);
        switch (caseSensitivity) {
            case UPPER:     
            case LOWER:
                sb.append(')');
        }
        sb.append(descending ? " DESC" : " ASC");
        return sb.toString();
    }
}
