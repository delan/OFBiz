/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.core.entity.config;

import java.util.*;
import java.net.*;
import java.io.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;

/**
 * Loads resources from the file system
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    March 1, 2002
 *@version    1.0
 */
public class FileLoader extends ResourceLoader {
    public InputStream loadResource(String location) throws GenericEntityConfException {
        String fullLocation = fullLocation(location);
        URL fileUrl = null;
        fileUrl = UtilURL.fromFilename(fullLocation);
        if (fileUrl == null) {
            throw new GenericEntityConfException("File Resource not found: " + fullLocation);
        }
        try {
            return fileUrl.openStream();
        } catch (java.io.IOException e) {
            throw new GenericEntityConfException("Error opening file at location [" + fileUrl.toExternalForm() + "]", e);
        }
    }
}
