/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.control;

import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.http.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.stats.*;

/**
 * HttpSessionListener that gathers and tracks various information and statistics
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ControlActivationEventListener implements HttpSessionActivationListener {
    // Debug module name
    public static final String module = ControlActivationEventListener.class.getName();

    public ControlActivationEventListener() {}

    public void sessionWillPassivate(HttpSessionEvent event) {
        ControlEventListener.countPassivateSession();
        Debug.logInfo("Passivating session: " + event.getSession().toString(), module);
    }

    public void sessionDidActivate(HttpSessionEvent event) {
        ControlEventListener.countActivateSession();
        Debug.logInfo("Activating session: " + event.getSession().toString(), module);
    }
}
