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
//package print;
package org.ofbiz.webtools.print.applet;

import java.awt.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.swing.*;

import org.apache.fop.apps.Driver;
import org.apache.fop.layout.Page;
import org.apache.fop.render.awt.AWTRenderer;
import org.apache.commons.codec.binary.Base64;
import org.xml.sax.InputSource;

import org.ofbiz.webtools.print.rmi.FopPrintRemote;

/**
 * FopPrinter
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      Jan 23, 2006
 */
public class FopPrintApplet extends JApplet {

    public static final String module = FopPrintApplet.class.getName();

    protected DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
    protected String requestPath = "/webtools/control/getXslFo";
    protected String serverUrl = null;
    protected String rmiHost = null;
    protected String rmiName = null;
    protected int rmiPort = 1099;
    protected boolean resetCookies = false;
    protected boolean started = false;

    protected List printing = new ArrayList();
    protected Map jobQueue = new HashMap();
    protected Map toPrint = new HashMap();

    public void init(Map toPrint, String serverUrl, String rmiName, String rmiHost, int rmiPort, boolean reset) {
        this.serverUrl = serverUrl;
        this.rmiName = rmiName;
        this.rmiHost = rmiHost;
        this.rmiPort = rmiPort;
        this.toPrint = toPrint;
        this.resetCookies = reset;
    }

    public void init() {
        int port = 1099;
        if (this.getParameter("rmi-port") != null) {
            try {
                port = Integer.parseInt(this.getParameter("rmi-port"));
            } catch (NumberFormatException e) {
            }
        }

        String serverUrl = this.getParameter("server-url");
        String rmiName = this.getParameter("rmi-name");
        String rmiHost = this.getParameter("rmi-host");
        String reset = this.getParameter("reset-cookies");
        boolean resetCookies = reset != null && "true".equalsIgnoreCase(reset);

        Map toPrint = new HashMap();
        boolean look = true;
        int count = 1;
        while (look) {
            String printer = this.getParameter("printer." + count);
            String screen = this.getParameter("screen." + count);
            if (screen != null && screen.length() > 0) {
                toPrint.put(screen, printer);
                count++;
            } else {
                look = false;
            }
        }
        this.init(toPrint, serverUrl, rmiName, rmiHost, port, resetCookies);
    }

    public void paint(Graphics g) {
        if (!started) {
            started = true;
            Thread worker = new Thread() {
                public void run() {
                    processJobs();
                }
            };
            worker.start();                    
        } else {
            this.removeAll();
        }

        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Print spooler started sith [" + jobQueue.size() + "] jobs queued...", 70, 70);
    }

    protected void processJobs() {
        Iterator i = toPrint.keySet().iterator();
        while (i.hasNext()) {
            String screen = (String) i.next();
            String printer = (String) toPrint.get(screen);

            String screenUri = this.getScreenUri(screen);
            PrintService service = this.getPrintService(screenUri, printer);
            if (service != null) {
                jobQueue.put(screenUri, service);
                this.repaint();

                try {
                    this.printFo((rmiHost == null ? serverUrl : rmiHost), screen);
                } catch (Exception e) {
                    e.printStackTrace();
                    this.getAppletContext().showStatus("Unable to render print job.");
                }
            } else {
                System.out.println("No printer for job: " + screenUri);
                this.getAppletContext().showStatus("No printer(s) available.");
            }
        }

        if (jobQueue.size() == 0) {
            try {
                this.sendComplete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.repaint();
    }

    protected void printFo(String url, String screenUrl) throws Exception {
        if (url.startsWith("http")) {
            this.printFoFromHttp(url, screenUrl);
        } else if (url.startsWith("rmi")) {
            this.printFoFromRmi(url, screenUrl);
        } else {
            this.getAppletContext().showStatus("Unsupported protocol used.");
        }
    }

    protected void printFoFromRmi(String lookup, String screenUrl) throws Exception {
        InputSource source = this.getScreenFromRmi(lookup, screenUrl);
        this.renderFO(this.getScreenUri(screenUrl), source);
    }

    protected void printFoFromHttp(String server, String screenUrl) throws Exception {
        InputSource source = this.getScreenFromHttp(server, screenUrl);
        this.renderFO(this.getScreenUri(screenUrl), source);
    }

    protected InputSource getScreenFromHttp(String server, String screenUrl) throws Exception {
        String screen = this.getScreenUri(screenUrl);
        Map params = this.getParameters(screenUrl);
        Iterator i = params.keySet().iterator();
        String paramString = "?screenUri=" + URLEncoder.encode(screen, "UTF-8");
        while (i.hasNext()) {
            String key = (String) i.next();
            Object val = params.get(key);
            paramString = paramString + "&" + key + "=" + URLEncoder.encode(val.toString(), "UTF-8");
        }

        URL url = new URL(server + requestPath + paramString);
        URLConnection con = url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

        PrintWriter toServlet = new PrintWriter(con.getOutputStream());
        toServlet.flush();
        toServlet.close();

        InputStream in = con.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        StringBuffer buf = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buf.append(line);
            buf.append(System.getProperty("line.separator"));
        }
        reader.close();

        // convert the base64 string to bytes
        byte[] bytes = Base64.decodeBase64(buf.toString().getBytes());
        System.out.println(new String(bytes));
        return new InputSource(new ByteArrayInputStream(bytes));
    }

    protected InputSource getScreenFromRmi(String lookup, String screenUrl) throws Exception {
        FopPrintRemote remote = (FopPrintRemote) Naming.lookup(lookup);
        if (remote != null) {
            String screen = this.getScreenUri(screenUrl);
            Map params = this.getParameters(screenUrl);
            byte[] fo = remote.getXslFo(screen, params);
            return new InputSource(new ByteArrayInputStream(fo));
        }
        return null;
    }

    protected String getScreenUri(String screen) {
        return screen.indexOf("?") != -1 ? screen.substring(0, screen.indexOf("?")) : screen;
    }

    protected Map getParameters(String screen) {
        Map params = parseQueryString(screen.substring(screen.indexOf("?") + 1));
        params.put("locale", Locale.getDefault());
        System.out.println("Parsed parameters: " + params);
        return params;
    }

    protected void setJobComplete(String jobName) throws Exception {
        jobQueue.remove(jobName);
        if (jobQueue.size() == 0) {
            this.sendComplete();
        }
    }

    protected void sendComplete() throws Exception {
        this.getAppletContext().showStatus("Updating settings on server...");
        StringBuffer buf = new StringBuffer();
        Iterator i = toPrint.keySet().iterator();
        int count = 1;
        while (i.hasNext()) {
            String screen = (String) i.next();
            String printerName = (String) toPrint.get(screen);

            if (buf.length() > 0) {
                buf.append("&");
            }

            // first the screen
            buf.append("screen.");
            buf.append(count);
            buf.append("=");
            buf.append(URLEncoder.encode(this.getScreenUri(screen), "UTF-8"));

            // then the printer
            buf.append("&printer.");
            buf.append(count);
            buf.append("=");
            buf.append(URLEncoder.encode(printerName, "UTF-8"));
            count++;
        }

        String path = "/webtools/control/printComplete?" + buf.toString();
        URL url = new URL(serverUrl + path);
        System.out.println("Returning complete: " + url.toExternalForm());
        this.getAppletContext().showStatus("All jobs queued.");
        this.getAppletContext().showDocument(url);
    }

    protected PrintService getPrintService(String jobName, String printerName) {
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, aset);
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();

        PrintService service = null;
        if (printService.length > 0) {
            if (!resetCookies && printerName != null && printerName.length() > 0) {
                System.out.println("Looking for available printer: " + printerName);
                for (int i = 0; i < printService.length; i++) {
                    PrintService svc = printService[i];
                    if (svc.getName().equals(printerName)) {
                        service = svc;
                    }
                }
            }

            if (service == null) {
                aset.add(new JobName(jobName.substring(jobName.indexOf("#") + 1), Locale.getDefault()));
                this.getAppletContext().showStatus("Requesting printer settings... [" + jobName.substring(jobName.indexOf("#") + 1) + "]");
                try {
                    service = ServiceUI.printDialog(null, 200, 200, printService, defaultService, flavor, aset);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                if (service == null) {
                    System.out.println("Null print service returned.");
                } else {
                    System.out.println("Print service received: " + service.getName());
                }
            }
        }

        return service;
    }

    /**
     * Renders an FO inputsource to the selected printer.
     */
    protected void renderFO(final String name, final InputSource foFile) throws Exception {
        final PrintService service = (PrintService) jobQueue.get(name);
        if (service != null) {
            this.getAppletContext().showStatus("Rendering PDF...");
            Thread worker = new Thread() {
                public void run() {
                    try {
                        PrintRenderer renderer = new PrintRenderer(service, name);
                        Driver driver = new Driver(foFile, null);
                        driver.setRenderer(renderer);
                        driver.run();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

            worker.start();
        }
    }

    private static String parseName(String s, StringBuffer sb) {
        sb.setLength(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException e) {
                        String rest = s.substring(i);
                        sb.append(rest);
                        if (rest.length() == 2)
                            i++;
                    }
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    private static Map parseQueryString(String s) {
        System.out.println("Parsing string: " + s);

        if (s == null) {
            throw new IllegalArgumentException();
        }
        HashMap ht = new HashMap();
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            System.out.println("Next token: " + pair);
            int pos = pair.indexOf('=');
            if (pos == -1) {
                throw new IllegalArgumentException();
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1, pair.length()), sb);
            System.out.println("Key: " + key + " / Val: " + val);

            ht.put(key, val);
        }
        return ht;
    }

    // This is stolen from FOP PrintStarter
    class PrintRenderer extends AWTRenderer {

        public static final int EVEN_AND_ALL = 0;
        public static final int EVEN = 1;
        public static final int ODD = 2;

        private int startNumber;
        private int endNumber;
        private int mode = EVEN_AND_ALL;
        private int copies = 0;
        private PrintService service;
        private String jobName;

        PrintRenderer(PrintService service, String jobName) {
            this(service, jobName, EVEN_AND_ALL, 0);
        }

        PrintRenderer(PrintService service, String jobName, int mode, int copies) {
            super(null);
            this.service = service;
            this.jobName = jobName;
            this.copies = copies;
            this.mode = mode;
            this.startNumber = 0 ;
            this.endNumber = -1;
            this.setScaleFactor(1);
        }

        public void stopRenderer(OutputStream outputStream) throws IOException {
            super.stopRenderer(outputStream);

            if (endNumber == -1) {
                endNumber = getPageCount();
            }

            Vector numbers = getInvalidPageNumbers();
            for (int i = numbers.size() - 1; i > -1; i--) {
                removePage(Integer.parseInt((String) numbers.elementAt(i)));
            }

            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(new JobName(jobName.substring(jobName.indexOf("#") + 1), Locale.getDefault()));
            if (copies > 0) {
                aset.add(new Copies(copies));
            }

            if (service != null) {
                log.info("Printing job [" + jobName + "] to printer - " + service.getName());
                DocPrintJob job = service.createPrintJob();
                DocAttributeSet das = new HashDocAttributeSet();
                Doc doc = new SimpleDoc(this, flavor, das);
                try {
                    int copiesToPrint = 1;
                    if (!service.isAttributeCategorySupported(Copies.class)) {
                        copiesToPrint = ((Copies) aset.get(Copies.class)).getValue();
                        aset.add(new Copies(1));
                    }

                    getAppletContext().showStatus("Printing...");
                    for (int i = 0; i < copiesToPrint; i++) {
                        job.print(doc, aset);
                    }

                    try {
                        setJobComplete(jobName);
                    } catch (Exception e) {
                        throw new IOException(e.getMessage());
                    }
                } catch (PrintException e) {
                    log.error("Unable to print", e);
                    throw new IOException("Unable to print: " + e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }

        public void renderPage(Page page) {
            pageWidth = (int)((float) page.getWidth() / 1000f);
            pageHeight = (int)((float) page.getHeight() / 1000f);
            super.renderPage(page);
        }

        private Vector getInvalidPageNumbers() {
            Vector vec = new Vector();
            int max = getPageCount();
            boolean isValid;
            for (int i = 0; i < max; i++) {
                isValid = true;
                if (i < startNumber || i > endNumber) {
                    isValid = false;
                } else if (mode != EVEN_AND_ALL) {
                    if (mode == EVEN && ((i + 1) % 2 != 0)) {
                        isValid = false;
                    } else if (mode == ODD && ((i + 1) % 2 != 1)) {
                        isValid = false;
                    }
                }

                if (!isValid) {
                    vec.add(i + "");
                }
            }
            return vec;
        }
    }
}

