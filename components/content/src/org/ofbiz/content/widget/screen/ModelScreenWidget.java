/*
 * $Id: ModelScreenWidget.java,v 1.4 2004/07/18 10:09:35 jonesde Exp $
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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.content.widget.form.FormFactory;
import org.ofbiz.content.widget.form.FormStringRenderer;
import org.ofbiz.content.widget.form.ModelForm;
import org.ofbiz.content.widget.html.HtmlFormRenderer;
import org.ofbiz.content.widget.html.HtmlMenuRenderer;
import org.ofbiz.content.widget.menu.MenuFactory;
import org.ofbiz.content.widget.menu.MenuStringRenderer;
import org.ofbiz.content.widget.menu.ModelMenu;
import org.ofbiz.content.widget.screen.ModelScreen.ScreenRenderer;
import org.ofbiz.entity.GenericValue;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Widget Library - Screen model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.4 $
 * @since      3.1
 */
public abstract class ModelScreenWidget {
    public static final String module = ModelScreenWidget.class.getName();

    protected ModelScreen modelScreen;
    
    public ModelScreenWidget(ModelScreen modelScreen, Element widgetElement) {
        this.modelScreen = modelScreen;
        if (Debug.verboseOn()) Debug.logVerbose("Reading Screen sub-widget with name: " + widgetElement.getNodeName(), module);
    }
    
    public abstract void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer);
    
    public static List readSubWidgets(ModelScreen modelScreen, Element widgetElement) {
        List subWidgets = new LinkedList();
        
        List subElementList = UtilXml.childElementList(widgetElement);
        Iterator subElementIter = subElementList.iterator();
        while (subElementIter.hasNext()) {
            Element subElement = (Element) subElementIter.next();

            if ("section".equals(subElement.getNodeName())) {
                subWidgets.add(new Section(modelScreen, subElement));
            } else if ("container".equals(subElement.getNodeName())) {
                subWidgets.add(new Container(modelScreen, subElement));
            } else if ("include-screen".equals(subElement.getNodeName())) {
                subWidgets.add(new IncludeScreen(modelScreen, subElement));
            } else if ("decorator-screen".equals(subElement.getNodeName())) {
                subWidgets.add(new DecoratorScreen(modelScreen, subElement));
            } else if ("decorator-section-include".equals(subElement.getNodeName())) {
                subWidgets.add(new DecoratorSectionInclude(modelScreen, subElement));
            } else if ("label".equals(subElement.getNodeName())) {
                subWidgets.add(new Label(modelScreen, subElement));
            } else if ("form".equals(subElement.getNodeName())) {
                subWidgets.add(new Form(modelScreen, subElement));
            } else if ("menu".equals(subElement.getNodeName())) {
                subWidgets.add(new Menu(modelScreen, widgetElement));
            } else if ("tree".equals(subElement.getNodeName())) {
                subWidgets.add(new ModelTree(modelScreen, widgetElement));
            } else if ("content".equals(subElement.getNodeName())) {
                subWidgets.add(new Content(modelScreen, widgetElement));
            } else if ("sub-content".equals(subElement.getNodeName())) {
                subWidgets.add(new SubContent(modelScreen, widgetElement));
            } else if ("platform-specific".equals(subElement.getNodeName())) {
                subWidgets.add(new PlatformSpecific(modelScreen, subElement));
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
            if (Debug.verboseOn()) Debug.logVerbose("Rendering screen widget " + subWidget.getClass().getName(), module);
            subWidget.renderWidgetString(writer, context, screenStringRenderer);
        }
    }

    public static class SectionsRenderer {
        protected Map sectionMap;
        protected ScreenStringRenderer screenStringRenderer;
        protected Map context;
        
        public SectionsRenderer(Map sectionMap, Map context, ScreenStringRenderer screenStringRenderer) {
            this.sectionMap = sectionMap;
            this.context = context;
            this.screenStringRenderer = screenStringRenderer;
        }

        public String render(String sectionName) {
            Writer tempWriter = new StringWriter();
            this.render(sectionName, tempWriter);
            return tempWriter.toString();
        }
        
        public void render(String sectionName, Writer writer) {
            ModelScreenWidget section = (ModelScreenWidget) this.sectionMap.get(sectionName);
            // if no section by that name, write nothing
            if (section != null) {
                section.renderWidgetString(writer, this.context, this.screenStringRenderer);
            }
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
            try {
                // section by definition do not themselves do anything, so this method will generally do nothing, but we'll call it anyway
                screenStringRenderer.renderSectionBegin(writer, context, this);
                
                // render sub-widgets
                renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);

                screenStringRenderer.renderSectionEnd(writer, context, this);
            } catch (IOException e) {
                String errMsg = "Error rendering label in screen named [" + this.modelScreen.getName() + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }
        }
        
        public String getName() {
            return name;
        }
    }

    public static class Container extends ModelScreenWidget {
        protected FlexibleStringExpander idExdr;
        protected FlexibleStringExpander styleExdr;
        protected List subWidgets;
        
        public Container(ModelScreen modelScreen, Element sectionElement) {
            super(modelScreen, sectionElement);
            this.idExdr = new FlexibleStringExpander(sectionElement.getAttribute("id"));
            this.styleExdr = new FlexibleStringExpander(sectionElement.getAttribute("style"));
            
            // read sub-widgets
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, sectionElement);
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            try {
                screenStringRenderer.renderContainerBegin(writer, context, this);
                
                // render sub-widgets
                renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);

                screenStringRenderer.renderContainerEnd(writer, context, this);
            } catch (IOException e) {
                String errMsg = "Error rendering label in screen named [" + this.modelScreen.getName() + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }
        }
        
        public String getId(Map context) {
            return this.idExdr.expandString(context);
        }
        
        public String getStyle(Map context) {
            return this.styleExdr.expandString(context);
        }
    }

    public static class IncludeScreen extends ModelScreenWidget {
        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander locationExdr;
        protected FlexibleStringExpander shareScopeExdr;
        
        public IncludeScreen(ModelScreen modelScreen, Element includeScreenElement) {
            super(modelScreen, includeScreenElement);
            this.nameExdr = new FlexibleStringExpander(includeScreenElement.getAttribute("name"));
            this.locationExdr = new FlexibleStringExpander(includeScreenElement.getAttribute("location"));
            this.shareScopeExdr = new FlexibleStringExpander(includeScreenElement.getAttribute("share-scope"));
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            // if we are not sharing the scope, protect it using the MapStack
            boolean protectScope = !shareScope(context);
            if (protectScope) {
                if (!(context instanceof MapStack)) {
                    context = new MapStack(context);
                }
                ((MapStack) context).push();
            }
            
            // dont need the renderer here, will just pass this on down to another screen call; screenStringRenderer.renderContainerBegin(writer, context, this);
            String name = this.getName(context);
            String location = this.getLocation(context);
            
            ModelScreen modelScreen = null;
            if (UtilValidate.isNotEmpty(location)) {
                try {
                    modelScreen = ScreenFactory.getScreenFromLocation(location, name);
                } catch (IOException e) {
                    String errMsg = "Error rendering included screen named [" + name + "] at location [" + location + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new RuntimeException(errMsg);
                } catch (SAXException e) {
                    String errMsg = "Error rendering included screen named [" + name + "] at location [" + location + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new RuntimeException(errMsg);
                } catch (ParserConfigurationException e) {
                    String errMsg = "Error rendering included screen named [" + name + "] at location [" + location + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new RuntimeException(errMsg);
                }
            } else {
                modelScreen = (ModelScreen) this.modelScreen.modelScreenMap.get(name);
                if (modelScreen == null) {
                    throw new IllegalArgumentException("Could not find screen with name [" + name + "] in the same file as the screen with name [" + this.modelScreen.getName() + "]");
                }
            }
            modelScreen.renderScreenString(writer, context, screenStringRenderer);
        }
        
        public String getName(Map context) {
            return this.nameExdr.expandString(context);
        }
        
        public String getLocation(Map context) {
            return this.locationExdr.expandString(context);
        }
        
        public boolean shareScope(Map context) {
            String shareScopeString = this.shareScopeExdr.expandString(context);
            // defaults to false, so anything but true is false
            return "true".equals(shareScopeString);
        }
    }

    public static class DecoratorScreen extends ModelScreenWidget {
        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander locationExdr;
        protected Map sectionMap = new HashMap();
        
        public DecoratorScreen(ModelScreen modelScreen, Element decoratorScreenElement) {
            super(modelScreen, decoratorScreenElement);
            this.nameExdr = new FlexibleStringExpander(decoratorScreenElement.getAttribute("name"));
            this.locationExdr = new FlexibleStringExpander(decoratorScreenElement.getAttribute("location"));
            
            List decoratorSectionElementList = UtilXml.childElementList(decoratorScreenElement, "decorator-section");
            Iterator decoratorSectionElementIter = decoratorSectionElementList.iterator();
            while (decoratorSectionElementIter.hasNext()) {
                Element decoratorSectionElement = (Element) decoratorSectionElementIter.next();
                String name = decoratorSectionElement.getAttribute("name");
                this.sectionMap.put(name, new DecoratorSection(modelScreen, decoratorSectionElement));
            }
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            // isolate the scope
            if (!(context instanceof MapStack)) {
                context = new MapStack(context);
            }
            
            MapStack contextMs = (MapStack) context;

            // create a standAloneStack, basically a "save point" for this SectionsRenderer, and make a new "screens" object just for it so it is isolated and doesn't follow the stack down
            MapStack standAloneStack = contextMs.standAloneChildStack();
            standAloneStack.put("screens", new ScreenRenderer(writer, context, screenStringRenderer));
            SectionsRenderer sections = new SectionsRenderer(this.sectionMap, standAloneStack, screenStringRenderer);
            
            // put the sectionMap in the context, make sure it is in the sub-scope, ie after calling push on the MapStack
            contextMs.push();
            context.put("sections", sections);
            
            String name = this.getName(context);
            String location = this.getLocation(context);
            
            ModelScreen modelScreen = null;
            if (UtilValidate.isNotEmpty(location)) {
                try {
                    modelScreen = ScreenFactory.getScreenFromLocation(location, name);
                } catch (IOException e) {
                    String errMsg = "Error rendering included screen named [" + name + "] at location [" + location + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new RuntimeException(errMsg);
                } catch (SAXException e) {
                    String errMsg = "Error rendering included screen named [" + name + "] at location [" + location + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new RuntimeException(errMsg);
                } catch (ParserConfigurationException e) {
                    String errMsg = "Error rendering included screen named [" + name + "] at location [" + location + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new RuntimeException(errMsg);
                }
            } else {
                modelScreen = (ModelScreen) this.modelScreen.modelScreenMap.get(name);
                if (modelScreen == null) {
                    throw new IllegalArgumentException("Could not find screen with name [" + name + "] in the same file as the screen with name [" + this.modelScreen.getName() + "]");
                }
            }
            modelScreen.renderScreenString(writer, context, screenStringRenderer);
        }

        public String getName(Map context) {
            return this.nameExdr.expandString(context);
        }
        
        public String getLocation(Map context) {
            return this.locationExdr.expandString(context);
        }
    }

    public static class DecoratorSection extends ModelScreenWidget {
        protected String name;
        protected List subWidgets;
        
        public DecoratorSection(ModelScreen modelScreen, Element decoratorSectionElement) {
            super(modelScreen, decoratorSectionElement);
            this.name = decoratorSectionElement.getAttribute("name");
            // read sub-widgets
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, decoratorSectionElement);
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            // render sub-widgets
            renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);
        }
    }
    
    public static class DecoratorSectionInclude extends ModelScreenWidget {
        protected String name;
        
        public DecoratorSectionInclude(ModelScreen modelScreen, Element decoratorSectionElement) {
            super(modelScreen, decoratorSectionElement);
            this.name = decoratorSectionElement.getAttribute("name");
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            SectionsRenderer sections = (SectionsRenderer) context.get("sections");
            // for now if sections is null, just log a warning; may be permissible to make the screen for flexible
            if (sections == null) {
                Debug.logWarning("In decorator-section-include could not find sections object in the context, not rendering section with name [" + this.name + "]", module);
            } else {
                sections.render(this.name, writer);
            }
        }
    }
    
    public static class Label extends ModelScreenWidget {
        protected FlexibleStringExpander textExdr;
        
        protected FlexibleStringExpander idExdr;
        protected FlexibleStringExpander styleExdr;
        
        public Label(ModelScreen modelScreen, Element labelElement) {
            super(modelScreen, labelElement);

            // put the text attribute first, then the pcdata under the element, if both are there of course
            String textAttr = UtilFormatOut.checkNull(labelElement.getAttribute("text"));
            String pcdata = UtilFormatOut.checkNull(UtilXml.elementValue(labelElement));
            this.textExdr = new FlexibleStringExpander(textAttr + pcdata);

            this.idExdr = new FlexibleStringExpander(labelElement.getAttribute("id"));
            this.styleExdr = new FlexibleStringExpander(labelElement.getAttribute("style"));
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            try {
                screenStringRenderer.renderLabel(writer, context, this);
            } catch (IOException e) {
                String errMsg = "Error rendering label in screen named [" + this.modelScreen.getName() + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }
        }
        
        public String getText(Map context) {
            return this.textExdr.expandString(context);
        }
        
        public String getId(Map context) {
            return this.idExdr.expandString(context);
        }
        
        public String getStyle(Map context) {
            return this.styleExdr.expandString(context);
        }
    }

    public static class Form extends ModelScreenWidget {
        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander locationExdr;
        
        public Form(ModelScreen modelScreen, Element formElement) {
            super(modelScreen, formElement);

            this.nameExdr = new FlexibleStringExpander(formElement.getAttribute("name"));
            this.locationExdr = new FlexibleStringExpander(formElement.getAttribute("location"));
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            String name = this.getName(context);
            String location = this.getLocation(context);
            ModelForm modelForm = null;
            try {
                modelForm = FormFactory.getFormFromLocation(this.getLocation(context), this.getName(context), this.modelScreen.getDelegator(context), this.modelScreen.getDispatcher(context));
            } catch (IOException e) {
                String errMsg = "Error rendering included form named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch (SAXException e) {
                String errMsg = "Error rendering included form named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch (ParserConfigurationException e) {
                String errMsg = "Error rendering included form named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }
            
            // try finding the formStringRenderer by name in the context in case one was prepared and put there
            FormStringRenderer formStringRenderer = (FormStringRenderer) context.get("formStringRenderer");
            // if there was no formStringRenderer put in place, now try finding the request/response in the context and creating a new one
            if (formStringRenderer == null) {
                HttpServletRequest request = (HttpServletRequest) context.get("request");
                HttpServletResponse response = (HttpServletResponse) context.get("response");
                if (request != null && response != null) {
                    formStringRenderer = new HtmlFormRenderer(request, response);
                }
            }
            // still null, throw an error
            if (formStringRenderer == null) {
                throw new IllegalArgumentException("Could not find a formStringRenderer in the context, and could not find HTTP request/response objects need to create one.");
            }
            
            StringBuffer renderBuffer = new StringBuffer();
            modelForm.renderFormString(renderBuffer, context, formStringRenderer);
            try {
                writer.write(renderBuffer.toString());
            } catch (IOException e) {
                String errMsg = "Error rendering included form named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }
        }
        
        public String getName(Map context) {
            return this.nameExdr.expandString(context);
        }
        
        public String getLocation(Map context) {
            return this.locationExdr.expandString(context);
        }
    }

    public static class PlatformSpecific extends ModelScreenWidget {
        protected ModelScreenWidget subWidget;
        
        public PlatformSpecific(ModelScreen modelScreen, Element platformSpecificElement) {
            super(modelScreen, platformSpecificElement);
            Element childElement = UtilXml.firstChildElement(platformSpecificElement);
            if ("html".equals(childElement.getNodeName())) {
                subWidget = new HtmlWidget(modelScreen, childElement);
            } else {
                throw new IllegalArgumentException("Tag not supported under the platform-specific tag with name: " + childElement.getNodeName());
            }
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            subWidget.renderWidgetString(writer, context, screenStringRenderer);
        }
    }

    public static class Content extends ModelScreenWidget {
        
        protected FlexibleStringExpander contentId;
        
        public Content(ModelScreen modelScreen, Element subContentElement) {
            super(modelScreen, subContentElement);

            // put the text attribute first, then the pcdata under the element, if both are there of course
            String contentId = UtilFormatOut.checkNull(subContentElement.getAttribute("content-id"));

        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            Locale locale = Locale.getDefault();
            Boolean nullThruDatesOnly = new Boolean(false);
            String mimeTypeId = "text/html";
            Map map = null;
            String expandedContentId = contentId.expandString(context);
            try {
                ContentWorker.renderContentAsTextCache(this.modelScreen.getDelegator(context), expandedContentId, writer, map, null, locale, mimeTypeId);

            } catch(GeneralException e) {
                String errMsg = "Error rendering included content with id [" + contentId + "] : " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch(IOException e2) {
                String errMsg = "Error rendering included content with id [" + contentId + "] : " + e2.toString();
                Debug.logError(e2, errMsg, module);
                throw new RuntimeException(errMsg);
            }


        }
        
        public String getContentId(Map context) {
            return this.contentId.expandString(context);
        }
        
    }


    public static class SubContent extends ModelScreenWidget {
        
        protected FlexibleStringExpander contentId;
        protected FlexibleStringExpander assocName;
        
        public SubContent(ModelScreen modelScreen, Element subContentElement) {
            super(modelScreen, subContentElement);

            // put the text attribute first, then the pcdata under the element, if both are there of course
            String contentId = UtilFormatOut.checkNull(subContentElement.getAttribute("content-id"));
            String assocName = UtilFormatOut.checkNull(subContentElement.getAttribute("assoc-name"));

        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            Locale locale = Locale.getDefault();
            Boolean nullThruDatesOnly = new Boolean(false);
            Timestamp fromDate = UtilDateTime.nowTimestamp();
            String mimeTypeId = "text/html";
            GenericValue userLogin = null;
            Map map = null;
            String expandedContentId = contentId.expandString(context);
            String expandedAssocName = assocName.expandString(context);
            HttpServletRequest request = (HttpServletRequest) context.get("request");
            if (request != null) {
                HttpSession session = request.getSession();
                userLogin = (GenericValue)session.getAttribute("userLogin");
            }
            try {
                ContentWorker.renderSubContentAsTextCache(this.modelScreen.getDelegator(context), expandedContentId, writer, expandedAssocName, null, map, locale, mimeTypeId, userLogin, fromDate, nullThruDatesOnly);
            } catch(GeneralException e) {
                String errMsg = "Error rendering included content with id [" + contentId + "] and assoc [" + assocName + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch(IOException e2) {
                String errMsg = "Error rendering included content with id [" + contentId + "] and assoc [" + assocName + "]: " + e2.toString();
                Debug.logError(e2, errMsg, module);
                throw new RuntimeException(errMsg);
            }
        }
        
        public String getContentId(Map context) {
            return this.contentId.expandString(context);
        }
        
        public String getAssocName(Map context) {
            return this.assocName.expandString(context);
        }
        
    }

    public static class Menu extends ModelScreenWidget {
        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander locationExdr;
        
        public Menu(ModelScreen modelScreen, Element menuElement) {
            super(modelScreen, menuElement);

            this.nameExdr = new FlexibleStringExpander(menuElement.getAttribute("name"));
            this.locationExdr = new FlexibleStringExpander(menuElement.getAttribute("location"));
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            String name = this.getName(context);
            String location = this.getLocation(context);
            ModelMenu modelMenu = null;
            try {
                modelMenu = MenuFactory.getMenuFromLocation(this.getLocation(context), this.getName(context), this.modelScreen.getDelegator(context), this.modelScreen.getDispatcher(context));
            } catch (IOException e) {
                String errMsg = "Error rendering included menu named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch (SAXException e) {
                String errMsg = "Error rendering included menu named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch (ParserConfigurationException e) {
                String errMsg = "Error rendering included menu named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }
            
            // try finding the menuStringRenderer by name in the context in case one was prepared and put there
            MenuStringRenderer menuStringRenderer = (MenuStringRenderer) context.get("menuStringRenderer");
            // if there was no menuStringRenderer put in place, now try finding the request/response in the context and creating a new one
            if (menuStringRenderer == null) {
                HttpServletRequest request = (HttpServletRequest) context.get("request");
                HttpServletResponse response = (HttpServletResponse) context.get("response");
                if (request != null && response != null) {
                    menuStringRenderer = new HtmlMenuRenderer(request, response);
                }
            }
            // still null, throw an error
            if (menuStringRenderer == null) {
                throw new IllegalArgumentException("Could not find a menuStringRenderer in the context, and could not find HTTP request/response objects need to create one.");
            }
            
            StringBuffer renderBuffer = new StringBuffer();
            modelMenu.renderMenuString(renderBuffer, context, menuStringRenderer);
            try {
                writer.write(renderBuffer.toString());
            } catch (IOException e) {
                String errMsg = "Error rendering included menu named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }
        }
        
        public String getName(Map context) {
            return this.nameExdr.expandString(context);
        }
        
        public String getLocation(Map context) {
            return this.locationExdr.expandString(context);
        }
    }
}

