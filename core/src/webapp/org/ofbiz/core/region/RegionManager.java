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

package org.ofbiz.core.region;


import java.util.*;
import java.net.*;

import org.w3c.dom.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.config.*;


/**
 * A class to manage the region cache and read a region XML file
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    February 26, 2002
 *@version    1.0
 */
public class RegionManager {

    public static Region getRegion(URL regionFile, String regionName) {
        if (regionFile == null) return null;

        Map regions = getRegions(regionFile);

        return (Region) regions.get(regionName);
    }

    public static void putRegion(URL regionFile, Region region) {
        if (regionFile == null) throw new IllegalArgumentException("regionFile cannot be null");

        Map regions = getRegions(regionFile);

        regions.put(region.getId(), region);
    }

    public static Map getRegions(URL regionFile) {
        Map regions = RegionCache.getRegions(regionFile);

        if (regions == null) {
            if (Debug.verboseOn()) Debug.logVerbose("Regions not yet loaded for " + regionFile + ", loading now");
            regions = readRegionXml(regionFile);
            RegionCache.putRegions(regionFile, regions);
        }
        return regions;
    }

    public static Map readRegionXml(URL regionXmlLocation) {
        Map regions = new HashMap();

        Document document = null;

        try {
            document = UtilXml.readXmlDocument(regionXmlLocation, true);
        } catch (java.io.IOException e) {
            Debug.logError(e);
        } catch (org.xml.sax.SAXException e) {
            Debug.logError(e);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            Debug.logError(e);
        }

        if (document == null) return regions;

        Element rootElement = document.getDocumentElement();

        List defineElements = UtilXml.childElementList(rootElement, "define");
        Iterator defineIter = defineElements.iterator();

        while (defineIter.hasNext()) {
            Element defineElement = (Element) defineIter.next();

            addRegion(regionXmlLocation, defineElement, regions);
        }

        return regions;
    }

    protected static void addRegion(URL regionFile, Element defineElement, Map regions) {
        Region newRegion = null;

        String idAttr = defineElement.getAttribute("id");
        String templateAttr = defineElement.getAttribute("template");
        String regionAttr = defineElement.getAttribute("region");

        if (UtilValidate.isNotEmpty(templateAttr) && UtilValidate.isNotEmpty(regionAttr)) {
            throw new IllegalArgumentException("Cannot use both template and region attributes");
        }

        if (UtilValidate.isNotEmpty(templateAttr)) {
            newRegion = new Region(idAttr, templateAttr, null);
        } else {
            if (UtilValidate.isNotEmpty(regionAttr)) {
                Region parentRegion = (Region) regions.get(regionAttr);

                if (parentRegion == null) {
                    throw new IllegalArgumentException("can't find page definition attribute with this key: " + regionAttr);
                }
                newRegion = new Region(idAttr, parentRegion.getContent(), parentRegion.getSections());
            } else {
                throw new IllegalArgumentException("Must specify either the template or the region attribute");
            }
        }

        regions.put(idAttr, newRegion);

        List putElements = UtilXml.childElementList(defineElement, "put");
        Iterator putIter = putElements.iterator();

        while (putIter.hasNext()) {
            Element putElement = (Element) putIter.next();

            newRegion.put(makeSection(putElement, regionFile));
        }
    }

    protected static Section makeSection(Element putElement, URL readerFile) {
        String bodyContent = UtilXml.elementValue(putElement);
        String section = putElement.getAttribute("section");
        String info = putElement.getAttribute("info");
        String content = putElement.getAttribute("content");
        String type = putElement.getAttribute("type");

        if (UtilValidate.isEmpty(type)) type = "default";

        if (UtilValidate.isNotEmpty(bodyContent) && UtilValidate.isNotEmpty(content)) {
            throw new IllegalArgumentException("Cannot use both content attribute and tag body text");
        }

        if (UtilValidate.isNotEmpty(bodyContent)) {
            content = bodyContent;
            type = "direct";
        }

        return new Section(section, info, content, type, readerFile);
    }
}
