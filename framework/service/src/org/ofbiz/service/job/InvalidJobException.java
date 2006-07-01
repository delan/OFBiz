/*
 * $Id: InvalidJobException.java 5462 2005-08-05 18:35:48Z jonesde $
 *
 * Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.service.job;
/**
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      Jul 12, 2005
 */
public class InvalidJobException extends JobManagerException {

    /**
     * Creates new <code>InvalidJobException</code> without detail message.
     */
    public InvalidJobException() {
        super();
    }

    /**
     * Constructs an <code>InvalidJobException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidJobException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>InvalidJobException</code> with the specified detail message and nested Exception.
     * @param nested the nested exception.
     */
    public InvalidJobException(Throwable nested) {
        super(nested);
    }

    /**
     * Constructs an <code>InvalidJobException</code> with the specified detail message and nested Exception.
     * @param msg the detail message.
     * @param nested the nested exception.
     */
    public InvalidJobException(String msg, Throwable nested) {
        super(msg, nested);
    }
}
