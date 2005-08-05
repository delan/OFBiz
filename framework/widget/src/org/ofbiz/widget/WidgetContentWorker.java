/*
 * $Id$
 *
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.widget;

import org.ofbiz.base.util.Debug;

/**
 * WidgetContentWorker Class
 * 
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version $Rev$
 */
public class WidgetContentWorker {
    public static final String module = WidgetContentWorker.class.getName();
    public static ContentWorkerInterface contentWorker = null;
    static {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            // note: loadClass is necessary for these since this class doesn't know anything about them at compile time
            contentWorker = (ContentWorkerInterface) loader.loadClass("org.ofbiz.content.content.ContentWorker").newInstance();
        } catch (ClassNotFoundException e) {
            Debug.logError(e, "Could not pre-initialize dynamically loaded class: ", module);
        } catch (IllegalAccessException e) {
            Debug.logError(e, "Could not pre-initialize dynamically loaded class: ", module);
        } catch (InstantiationException e) {
            Debug.logError(e, "Could not pre-initialize dynamically loaded class: ", module);
        }
    }
}
