
package org.ofbiz.core.minilang;

import java.net.*;
import java.text.*;
import java.util.*;

import org.w3c.dom.*;
import org.ofbiz.core.util.*;

import org.ofbiz.core.minilang.operation.*;

/**
 * <p><b>Title:</b> SimpleMapProcessor Mini Language
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 29, 2001
 *@version    1.0
 */
public class SimpleMapProcessor {

    protected static UtilCache simpleMapProcessorsCache = new UtilCache("SimpleMapProcessors", 0, 0);

    public static void runSimpleMapProcessor(String xmlResource, String name, Map inMap, Map results, List messages, Locale locale) throws MiniLangException {
        runSimpleMapProcessor(xmlResource, name, inMap, results, messages, locale, null);
    }

    public static void runSimpleMapProcessor(String xmlResource, String name, Map inMap, Map results, List messages, Locale locale, ClassLoader loader) throws MiniLangException {
        URL xmlURL = UtilURL.fromResource(xmlResource, loader);
        if (xmlURL == null) {
            throw new MiniLangException("Could not find SimpleMapProcessor XML document in resource: " + xmlResource);
        }

        runSimpleMapProcessor(xmlURL, name, inMap, results, messages, locale, loader);
    }

    public static void runSimpleMapProcessor(URL xmlURL, String name, Map inMap, Map results, List messages, Locale locale, ClassLoader loader) throws MiniLangException {
        if (loader == null)
            loader = Thread.currentThread().getContextClassLoader();

        MapProcessor processor = getProcessor(xmlURL, name);
        if (processor != null)
            processor.exec(inMap, results, messages, locale, loader);
    }

    protected static MapProcessor getProcessor(URL xmlURL, String name) throws MiniLangException {
        Map simpleMapProcessors = (Map) simpleMapProcessorsCache.get(xmlURL);
        if (simpleMapProcessors == null) {
            synchronized (SimpleMapProcessor.class) {
                simpleMapProcessors = (Map) simpleMapProcessorsCache.get(xmlURL);
                if (simpleMapProcessors == null) {
                    simpleMapProcessors = new HashMap();

                    //read in the file
                    Document document = null;
                    try {
                        document = UtilXml.readXmlDocument(xmlURL, true);
                    } catch (java.io.IOException e) {
                        throw new MiniLangException("Could not read XML file", e);
                    } catch (org.xml.sax.SAXException e) {
                        throw new MiniLangException("Could not parse XML file", e);
                    } catch (javax.xml.parsers.ParserConfigurationException e) {
                        throw new MiniLangException("XML parser not setup correctly", e);
                    }

                    if (document == null) {
                        throw new MiniLangException("Could not find SimpleMapProcessor XML document: " + xmlURL.toString());
                    }

                    Element rootElement = document.getDocumentElement();
                    List simpleMapProcessorElements = UtilXml.childElementList(rootElement, "simple-map-processor");
                    Iterator strProcorIter = simpleMapProcessorElements.iterator();
                    while (strProcorIter.hasNext()) {
                        Element simpleMapProcessorElement = (Element) strProcorIter.next();
                        MapProcessor processor = new MapProcessor(simpleMapProcessorElement);
                        simpleMapProcessors.put(simpleMapProcessorElement.getAttribute("name"), processor);
                    }

                    //put it in the cache
                    simpleMapProcessorsCache.put(xmlURL, simpleMapProcessors);
                }
            }
        }

        MapProcessor proc = (MapProcessor) simpleMapProcessors.get(name);
        if (proc == null) {
            throw new MiniLangException("Could not find SimpleMapProcessor named " + name + " in XML document: " + xmlURL.toString());
        }

        return proc;
    }
}
