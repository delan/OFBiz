/*
 * $Id: ModelScreenWidget.java,v 1.1 2004/07/11 07:24:52 jonesde Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 */
package org.ofbiz.content.widget.screen;

import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Element;

/**
 * Widget Library - Screen model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public abstract class ModelScreenWidget {
    protected ModelScreen modelScreen;
    
    public ModelScreenWidget(ModelScreen modelScreen, Element widgetElement) {
        this.modelScreen = modelScreen;
    }
    
    public abstract void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer);
    
    public static List readSubWidgets(ModelScreen modelScreen, Element widgetElement) {
        List subWidgets = new LinkedList();
        
        List subElementList = UtilXml.childElementList(widgetElement);
        Iterator subElementIter = subElementList.iterator();
        while (subElementIter.hasNext()) {
            Element subElement = (Element) subElementIter.next();

            if ("section".equals(subElement.getNodeName())) {
                subWidgets.add(new Section(modelScreen, widgetElement));
            } else if ("container".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("include-screen".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("decorator-screen".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("decorator-section".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("decorator-section-include".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("label".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("form".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("menu".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("tree".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("content".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("sub-content".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else if ("platform-specific".equals(subElement.getNodeName())) {
                // TODO: implement this
            } else {
                throw new IllegalArgumentException("Found invalid screen widget element with name: " + subElement.getNodeName());
            }
        }
        
        return subWidgets;
    }
    
    public static void renderSubWidgetsString(List subWidgets, Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
        Iterator subWidgetIter = subWidgets.iterator();
        while (subWidgetIter.hasNext()) {
            ModelScreenWidget subWidget = (ModelScreenWidget) subWidgetIter.next();
            subWidget.renderWidgetString(writer, context, screenStringRenderer);
        }
    }

    public static class Section extends ModelScreenWidget {
        protected String name;
        protected List subWidgets;
        
        public Section(ModelScreen modelScreen, Element sectionElement) {
            super(modelScreen, sectionElement);
            this.name = sectionElement.getAttribute("name");
            
            // read sub-widgets
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, sectionElement);
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            // section by definition do not themselves do anything, so this method will generally do nothing, but we'll call it anyway
            screenStringRenderer.renderSectionBegin(writer, context, this);
            
            // render sub-widgets
            renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);

            screenStringRenderer.renderSectionEnd(writer, context, this);
        }
    }
}

