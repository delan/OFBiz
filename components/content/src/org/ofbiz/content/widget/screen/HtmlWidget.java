/*
 * $Id: HtmlWidget.java,v 1.1 2004/07/15 22:25:00 jonesde Exp $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.w3c.dom.Element;

/**
 * Widget Library - Screen model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class HtmlWidget extends ModelScreenWidget {
    public static final String module = HtmlWidget.class.getName();
    
    protected ModelScreenWidget childWidget;
    
    public HtmlWidget(ModelScreen modelScreen, Element htmlElement) {
        super(modelScreen, htmlElement);
        List childElementList = UtilXml.childElementList(htmlElement);
        Iterator childElementIter = childElementList.iterator();
        while (childElementIter.hasNext()) {
            Element childElement = UtilXml.firstChildElement(htmlElement);
            if ("html-template".equals(childElement.getNodeName())) {
                this.childWidget = new HtmlTemplate(modelScreen, childElement);
            } else if ("html-template-decorator".equals(childElement.getNodeName())) {
                this.childWidget = new HtmlTemplateDecorator(modelScreen, childElement);
            } else {
                throw new IllegalArgumentException("Tag not supported under the platform-specific -> html tag with name: " + childElement.getNodeName());
            }
        }
    }

    public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
        childWidget.renderWidgetString(writer, context, screenStringRenderer);
    }
    
    public static void renderHtmlTemplate(Writer writer, Map context) {
        //TODO: implement this
    }

    public static class HtmlTemplate extends ModelScreenWidget {
        protected FlexibleStringExpander locationExdr;
        
        public HtmlTemplate(ModelScreen modelScreen, Element htmlTemplateElement) {
            super(modelScreen, htmlTemplateElement);
            this.locationExdr = new FlexibleStringExpander(htmlTemplateElement.getAttribute("location"));
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            //TODO: implement this
        }
    }

    public static class HtmlTemplateDecorator extends ModelScreenWidget {
        protected FlexibleStringExpander locationExdr;
        protected Map sectionMap = new HashMap();
        
        public HtmlTemplateDecorator(ModelScreen modelScreen, Element htmlTemplateDecoratorElement) {
            super(modelScreen, htmlTemplateDecoratorElement);
            this.locationExdr = new FlexibleStringExpander(htmlTemplateDecoratorElement.getAttribute("location"));
            
            List htmlTemplateDecoratorSectionElementList = UtilXml.childElementList(htmlTemplateDecoratorElement, "html-template-decorator-section");
            Iterator htmlTemplateDecoratorSectionElementIter = htmlTemplateDecoratorSectionElementList.iterator();
            while (htmlTemplateDecoratorSectionElementIter.hasNext()) {
                Element htmlTemplateDecoratorSectionElement = (Element) htmlTemplateDecoratorSectionElementIter.next();
                String name = htmlTemplateDecoratorSectionElement.getAttribute("name");
                this.sectionMap.put(name, null);
            }
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            // isolate the scope
            if (!(context instanceof MapStack)) {
                context = new MapStack(context);
            }
            ((MapStack) context).push();
            
            // put the sectionMap in the context, make sure it is in the sub-scope, ie after calling push on the MapStack
            context.put("sectionMap", sectionMap);
            
            //TODO: implement this
        }
    }

    public static class HtmlTemplateDecoratorSection extends ModelScreenWidget {
        protected String name;
        protected List subWidgets;
        
        public HtmlTemplateDecoratorSection(ModelScreen modelScreen, Element htmlTemplateDecoratorSectionElement) {
            super(modelScreen, htmlTemplateDecoratorSectionElement);
            this.name = htmlTemplateDecoratorSectionElement.getAttribute("name");
            // read sub-widgets
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, htmlTemplateDecoratorSectionElement);
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            // render sub-widgets
            renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);
        }
    }
}

