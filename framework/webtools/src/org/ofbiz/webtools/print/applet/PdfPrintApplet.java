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
package org.ofbiz.webtools.print.applet;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterJob;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PageRanges;
import javax.swing.*;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

import org.ofbiz.webtools.print.rmi.FopPrintRemote;

/**
 * PdfPrintApplet
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.5
 */
public class PdfPrintApplet extends Applet {

    public static final String module = PdfPrintApplet.class.getName();

    protected DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
    protected FopPrintRemote remote = null;

    protected float scale = (float) 0.5770202;
    protected boolean ready = true;

    protected Exception exception = null;
    protected String serverUrl = null;
    protected String rmiHost = null;
    protected String rmiName = null;
    protected int rmiPort = 1099;

    protected List printing = null;
    protected Map urlMap = null;
    protected Map ctx = null;


    public void init() {
        printing = new ArrayList();
        ctx = new HashMap();

        this.serverUrl = this.getParameter("server-url");
        this.rmiName = this.getParameter("rmi-name");
        this.rmiHost = this.getParameter("rmi-host");
        this.rmiPort = Integer.parseInt(this.getParameter("rmi-port"));
        this.loadRemote();
        this.loadScreens();
        this.initPrint();
    }

    public void paint(Graphics g) {
        if (ready && exception == null) {
            System.out.println("Calling paint()");
            this.removeAll();
            this.displayPrinting(g);
        } else if (exception != null) {
            g.setFont(new Font("Arial", Font.PLAIN, 11));
            g.drawString("Error: " + exception.getMessage(), 10, 12);
        }
    }

    protected void loadScreens() {
        urlMap = new HashMap();
        for (int x = 1; x < 11; x++) {
            String printer = this.getParameter("printer." + x);
            String screen = this.getParameter("screen." + x);
            if (screen != null && screen.length() > 0) {
                this.setToPrint(screen, printer);
            }
        }
    }

    protected void initPrint() {
        this.ready = true;

        Iterator a = urlMap.keySet().iterator();
        while (a.hasNext()) {
            String urlStr = (String) a.next();
            String printer = (String) urlMap.get(urlStr);

            if (printer == null || printer.length() == 0) {
                // show setup info for this URL
                ready = false;
                System.out.println("Creating printer selection UI");

                Font textFont = new Font("Arial", Font.PLAIN, 11);

                // title label
                Label preLabel = new Label("Print - ");
                preLabel.setFont(textFont);
                this.add(preLabel);

                final Label urlLabel = new Label(urlStr);
                urlLabel.setFont(textFont);
                this.add(urlLabel);

                Label postLabel = new Label(" to:");
                postLabel.setFont(textFont);
                this.add(postLabel);

                // printer selection
                PrintService[] svcs = this.getPrintServices();
                final Choice svc = new Choice();
                for (int v = 0; v < svcs.length; v++) {
                    svc.add(svcs[v].getName());
                    System.out.println("added - " + svcs[v].getName());
                }
                svc.add("No Printer");
                this.add(svc);

                // set button
                Button set = new Button("Set");
                set.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String printer = svc.getSelectedItem();
                        String screen = urlLabel.getText();

                        System.out.println(e.getActionCommand());
                        System.out.println(screen);
                        System.out.println(printer);

                        if ("No Printer".equals(svc.getSelectedItem())) {
                            printer = "_NA_";
                        }
                        setToPrint(screen, printer);
                        initPrint();
                    }
                });
                this.add(set);
            }
        }

        if (ready) {
            Iterator i = urlMap.keySet().iterator();
            while (i.hasNext()) {
                String screenStr = (String) i.next();
                String printer = (String) urlMap.get(screenStr);
                try {
                    this.print(printer, screenStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    exception = e;
                    repaint();
                }
            }
        }
    }

    public void print(final String printerName, final String screen) throws Exception {
        printing.add(screen);

        if (!"_NA_".equals(printerName)) {
            Thread worker = new Thread() {
                public void run() {
                    try {
                        PdfDecoder decoder = openPdf(getScreenUri(screen), getParameters(screen));
                        if (decoder != null) {
                            DocPrintJob job = createPrinterJob(printerName, decoder);
                            Doc printDoc = createPrintDoc(decoder);
                            job.print(printDoc, new HashPrintRequestAttributeSet());
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        exception = e;
                        repaint();
                    } catch (Exception e) {
                        e.printStackTrace();
                        exception = e;
                        repaint();
                    }
                    printing.remove(screen);
                    repaint();
                }
            };
            worker.start();
        } else {
            printing.remove(screen);
        }
    }

    protected void sendComplete() throws Exception {
        StringBuffer buf = new StringBuffer();
        Iterator i = urlMap.keySet().iterator();
        int count = 1;
        while (i.hasNext()) {
            String screen = (String) i.next();
            String printer = (String) urlMap.get(screen);
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
            buf.append(URLEncoder.encode(printer, "UTF-8"));
            count++;
        }

        String path = "/webtools/control/printComplete?" + buf.toString();
        URL url = new URL(serverUrl + path);
        System.out.println("Returning complete: " + url.toExternalForm());
        this.getAppletContext().showDocument(url);
    }

    protected void displayPrinting(Graphics g) {
        System.out.println("URL Map contents: " + urlMap);
        if (printing.size() > 0) {
            int count = 1;
            int y = 0;
            Iterator i = printing.iterator();
            while (i.hasNext()) {
                String screen = (String) i.next();
                System.out.println("Displaying settings for screen: " + screen);
                g.setFont(new Font("Arial", Font.PLAIN, 11));
                g.drawString("Spooling [" + count + "] : ", 10, (y+=12));
                g.drawString(screen, 20, (y+=10));
                g.drawString("To printer : " + urlMap.get(screen), 30, (y+=12));
                y+=5;
            }
        } else {
            g.setFont(new Font("Sans", Font.BOLD, 11));
            g.drawString("Print spooling done.", 10, 12);
            try {
                this.sendComplete();
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
                repaint();
            }
        }
    }

    protected void setToPrint(String screen, String printer) {
        System.out.println("Document to print: " + screen + " @ " + printer);
        urlMap.put(screen, printer);
    }

    protected PdfDecoder openPdf(String fileName) throws Exception {
        PdfDecoder decoder = new PdfDecoder();
        try {
            decoder.openPdfFile(fileName);
            this.initPdf(decoder);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return decoder;
    }

    protected PdfDecoder openPdf(String screen, Map parameters) throws Exception {
        PdfDecoder decoder = new PdfDecoder();
        if (remote == null) {
            throw new Exception("No RMI connection available");
        }
        try {
            // read the PDF from the RMI server
            byte[] pdfBytes = remote.getFopPdf(screen, parameters);

            // open the PDF
            decoder.openPdfArray(pdfBytes);
            this.initPdf(decoder);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        } catch (PdfException e) {
            e.printStackTrace();
            throw e;
        }

        return decoder;
    }

    protected void initPdf(PdfDecoder decoder) throws Exception {
        // set the render display
        decoder.setRenderMode(PdfDecoder.RENDERIMAGES + PdfDecoder.RENDERTEXT);
        decoder.useHiResScreenDisplay(true);

        // values extraction mode, dpi of images, dpi of page as a factor of 72
        decoder.setExtractionMode(0, 72, scale);

        // resize (ensure at least certain size)
        decoder.setPageParameters(scale, 1, 0);

        // add a border
        decoder.setPDFBorder(BorderFactory.createLineBorder(Color.black, 1));
        decoder.disableBorderForPrinting();
    }

    protected DocPrintJob createPrinterJob(String printerName, PdfDecoder decoder) throws Exception {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(flavor, null);
        PrintService service = null;

        for (int i = 0; i < services.length; i++) {
            if (printerName.equals(services[i].getName())) {
                service = services[i];
            }
        }

        if (service != null) {
            // setup print job
            DocPrintJob printJob = service.createPrintJob();

            // work around to get the decoder the page format object
            decoder.setPageFormat(PrinterJob.getPrinterJob().defaultPage());

            // page range setting
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(new PageRanges(1, decoder.getPageCount()));

            //set page range
            PageRanges r = (PageRanges) aset.get(PageRanges.class);
            if (r != null) {
                decoder.setPagePrintRange(r);
            }

            return printJob;

        } else {
            throw new Exception("No available print service.");
        }
    }

    protected void loadRemote() {
        String location = "rmi://" + rmiHost + ":" + rmiPort + "/" + rmiName;
        try {
            this.remote = (FopPrintRemote) Naming.lookup(location);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            exception = e;
            repaint();
        } catch (NotBoundException e) {
            e.printStackTrace();
            exception = e;
            repaint();
        } catch (RemoteException e) {
            e.printStackTrace();
            exception = e;
            repaint();
        }
    }

    protected PrintService[] getPrintServices() {
        return PrintServiceLookup.lookupPrintServices(flavor, null);
    }

    protected Doc createPrintDoc(PdfDecoder decoder) {
        return new SimpleDoc(decoder, flavor, null);
    }

    protected String getScreenUri(String screen) {
        return screen.indexOf("?") != -1 ? screen.substring(0, screen.indexOf("?")) : screen;
    }

    protected Map getParameters(String screen) {
        Map params = parseQueryString(screen.substring(screen.indexOf("?") + 1));
        System.out.println("Parsed parameters: " + params);
        return params;
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
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3),
                                16));
                        i += 2;
                    } catch (NumberFormatException e) {
                        // XXX
                        // need to be more specific about illegal arg
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

    public static Map parseQueryString(String s) {
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
}
