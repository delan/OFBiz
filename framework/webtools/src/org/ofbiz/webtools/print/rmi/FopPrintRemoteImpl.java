/*
 * $Id$
 *
 * Copyright (c) 2001-2006 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.webtools.print.rmi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Locale;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.webapp.view.FopRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;
import org.ofbiz.widget.screen.ScreenRenderer;

/**
 * FopPrintRemoteImpl
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.5
 */
public class FopPrintRemoteImpl extends UnicastRemoteObject implements FopPrintRemote {

    public static final String module = FopPrintRemoteImpl.class.getName();
    protected HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();
    protected DispatchContext dctx = null;
    protected Locale locale = null;

    public FopPrintRemoteImpl(DispatchContext dctx, Locale locale, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(0, csf, ssf);
        this.locale = locale;
        this.dctx = dctx;
    }

    public byte[] getFopPdf(String screenUri, Map parameters) throws RemoteException {
        // make sure the locale object is in the parameters
        if (parameters.get("locale") == null) {
            if (this.locale == null) {
                this.locale = Locale.getDefault();
            }
            parameters.put("locale", locale);
        }

        Debug.log("Attempt to render screen [" + screenUri + "] using parameters: " + parameters, module);

        // render and obtain the XSL-FO
        Writer writer = new StringWriter();
        try {
            ScreenRenderer screens = new ScreenRenderer(writer, null, htmlScreenRenderer);
            screens.populateContextForService(dctx, parameters);
            screens.render(screenUri);
        } catch (Throwable t) {
            throw new RemoteException("Problems with the response writer/output stream", t);
        }

        // render the byte array
        ByteArrayOutputStream out = null;
        try {
            out = FopRenderer.render(writer);
        } catch (GeneralException e) {
            throw new RemoteException(e.getMessage(), e.getNested());
        }

        byte[] bytes = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            throw new RemoteException("Problem closing output stream", e);
        }

        return bytes;
    }
}
