/*
 * $Id$
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.content.widget.form.FormFactory;
import org.ofbiz.content.widget.form.FormStringRenderer;
import org.ofbiz.content.widget.form.ModelForm;
import org.ofbiz.content.widget.html.HtmlFormRenderer;
import org.ofbiz.content.widget.html.HtmlMenuRenderer;
import org.ofbiz.content.widget.html.HtmlTreeExpandCollapseRenderer;
import org.ofbiz.content.widget.html.HtmlTreeRenderer;
import org.ofbiz.content.widget.menu.MenuFactory;
import org.ofbiz.content.widget.menu.MenuStringRenderer;
import org.ofbiz.content.widget.menu.ModelMenu;
import org.ofbiz.content.widget.screen.ModelScreen.ScreenRenderer;
import org.ofbiz.content.widget.tree.ModelTree;
import org.ofbiz.content.widget.tree.TreeFactory;
import org.ofbiz.content.widget.tree.TreeStringRenderer;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Widget Library - Screen model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
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
    
    public static List readSubWidgets(ModelScreen modelScreen, List subElementList) {
        List subWidgets = new LinkedList();
        
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
            } else if ("include-form".equals(subElement.getNodeName())) {
                subWidgets.add(new Form(modelScreen, subElement));
            } else if ("include-menu".equals(subElement.getNodeName())) {
                subWidgets.add(new Menu(modelScreen, subElement));
            } else if ("include-tree".equals(subElement.getNodeName())) {
                subWidgets.add(new Tree(modelScreen, subElement));
            } else if ("content".equals(subElement.getNodeName())) {
                subWidgets.add(new Content(modelScreen, subElement));
            } else if ("sub-content".equals(subElement.getNodeName())) {
                subWidgets.add(new SubContent(modelScreen, subElement));
            } else if ("platform-specific".equals(subElement.getNodeName())) {
                subWidgets.add(new PlatformSpecific(modelScreen, subElement));
            } else if ("link".equals(subElement.getNodeName())) {
                subWidgets.add(new Link(modelScreen, subElement));
            } else if ("image".equals(subElement.getNodeName())) {
                subWidgets.add(new Image(modelScreen, subElement));
            } else {
                throw new IllegalArgumentException("Found invalid screen widget element with name: " + subElement.getNodeName());
            }
        }
        
        return subWidgets;
    }
    
    public static void renderSubWidgetsString(List subWidgets, Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
        if (subWidgets == null)
            return;
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
        protected ModelScreenCondition condition;
        protected List actions;
        protected List subWidgets;
        protected List failWidgets;
        
        public Section(ModelScreen modelScreen, Element sectionElement) {
            super(modelScreen, sectionElement);
            this.name = sectionElement.getAttribute("name");

            // read condition under the "condition" element
            Element conditionElement = UtilXml.firstChildElement(sectionElement, "condition");
            if (conditionElement != null) {
                this.condition = new ModelScreenCondition(modelScreen, conditionElement);
            }

            // read all actions under the "actions" element
            Element actionsElement = UtilXml.firstChildElement(sectionElement, "actions");
            if (actionsElement != null) {
                this.actions = ModelScreenAction.readSubActions(modelScreen, actionsElement);
            }
            
            // read sub-widgets
            Element widgetsElement = UtilXml.firstChildElement(sectionElement, "widgets");
            List subElementList = UtilXml.childElementList(widgetsElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);

            // read fail-widgets
            Element failWidgetsElement = UtilXml.firstChildElement(sectionElement, "fail-widgets");
            if (failWidgetsElement != null) {
                List failElementList = UtilXml.childElementList(failWidgetsElement);
                this.failWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, failElementList);
            }
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            // check the condition, if there is one
            boolean condTrue = true;
            if (this.condition != null) {
                if (!this.condition.eval(context)) {
                    condTrue = false;
                }
            }
            
            // if condition does not exist or evals to true run actions and render widgets, otherwise render fail-widgets
            if (condTrue) {
                // run the actions only if true
                ModelScreenAction.runSubActions(this.actions, context);
                
                try {
                    // section by definition do not themselves do anything, so this method will generally do nothing, but we'll call it anyway
                    screenStringRenderer.renderSectionBegin(writer, context, this);
                    
                    // render sub-widgets
                    renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);

                    screenStringRenderer.renderSectionEnd(writer, context, this);
                } catch (IOException e) {
                    String errMsg = "Error rendering widgets section [" + this.getName() + "] in screen named [" + this.modelScreen.getName() + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new RuntimeException(errMsg);
                }
            } else {
                try {
                    // section by definition do not themselves do anything, so this method will generally do nothing, but we'll call it anyway
                    screenStringRenderer.renderSectionBegin(writer, context, this);
                    
                    // render sub-widgets
                    renderSubWidgetsString(this.failWidgets, writer, context, screenStringRenderer);

                    screenStringRenderer.renderSectionEnd(writer, context, this);
                } catch (IOException e) {
                    String errMsg = "Error rendering fail-widgets section [" + this.getName() + "] in screen named [" + this.modelScreen.getName() + "]: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    throw new RuntimeException(errMsg);
                }
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
        
        public Container(ModelScreen modelScreen, Element containerElement) {
            super(modelScreen, containerElement);
            this.idExdr = new FlexibleStringExpander(containerElement.getAttribute("id"));
            this.styleExdr = new FlexibleStringExpander(containerElement.getAttribute("style"));
            
            // read sub-widgets
            List subElementList = UtilXml.childElementList(containerElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
            return;
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            try {
                screenStringRenderer.renderContainerBegin(writer, context, this);
                
                // render sub-widgets
                renderSubWidgetsString(this.subWidgets, writer, context, screenStringRenderer);

                screenStringRenderer.renderContainerEnd(writer, context, this);
            } catch (IOException e) {
                String errMsg = "Error rendering container in screen named [" + this.modelScreen.getName() + "]: " + e.toString();
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
            
            if (UtilValidate.isEmpty(name)) {
                Debug.logInfo("In the include-screen tag the screen name was empty, ignoring include; in screen [" + this.modelScreen.getName() + "]", module);
                return;
            }
            
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
            List subElementList = UtilXml.childElementList(decoratorSectionElement);
            this.subWidgets = ModelScreenWidget.readSubWidgets(this.modelScreen, subElementList);
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
            
                //Debug.logInfo("before renderFormString, context:" + context, module);
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

    public static class Tree extends ModelScreenWidget {
        protected FlexibleStringExpander nameExdr;
        protected FlexibleStringExpander locationExdr;
        
        public Tree(ModelScreen modelScreen, Element treeElement) {
            super(modelScreen, treeElement);

            this.nameExdr = new FlexibleStringExpander(treeElement.getAttribute("name"));
            this.locationExdr = new FlexibleStringExpander(treeElement.getAttribute("location"));
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            String name = this.getName(context);
            String location = this.getLocation(context);
            ModelTree modelTree = null;
            try {
                modelTree = TreeFactory.getTreeFromLocation(this.getLocation(context), this.getName(context), this.modelScreen.getDelegator(context), this.modelScreen.getDispatcher(context));
            } catch (IOException e) {
                String errMsg = "Error rendering included tree named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch (SAXException e) {
                String errMsg = "Error rendering included tree named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            } catch (ParserConfigurationException e) {
                String errMsg = "Error rendering included tree named [" + name + "] at location [" + location + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }
            
            // try finding the treeStringRenderer by name in the context in case one was prepared and put there
            TreeStringRenderer treeStringRenderer = (TreeStringRenderer) context.get("treeStringRenderer");
            // if there was no treeStringRenderer put in place, now try finding the request/response in the context and creating a new one
            if (treeStringRenderer == null) {
                String renderClassStyle = modelTree.getRenderStyle();
                if (UtilValidate.isNotEmpty(renderClassStyle) && renderClassStyle.equals("simple")) 
                    treeStringRenderer = new HtmlTreeRenderer();
                else
                    treeStringRenderer = new HtmlTreeExpandCollapseRenderer();
        
            }
            // still null, throw an error
            if (treeStringRenderer == null) {
                throw new IllegalArgumentException("Could not find a treeStringRenderer in the context, and could not find HTTP request/response objects need to create one.");
            }
            
            StringBuffer renderBuffer = new StringBuffer();
            modelTree.renderTreeString(renderBuffer, context, treeStringRenderer);
            try {
                writer.write(renderBuffer.toString());
            } catch (IOException e) {
                String errMsg = "Error rendering included tree named [" + name + "] at location [" + location + "]: " + e.toString();
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
        protected FlexibleStringExpander editRequest;
        
        public Content(ModelScreen modelScreen, Element subContentElement) {
            super(modelScreen, subContentElement);

            // put the text attribute first, then the pcdata under the element, if both are there of course
            this.contentId = new FlexibleStringExpander(UtilFormatOut.checkNull(subContentElement.getAttribute("content-id")));
            this.editRequest = new FlexibleStringExpander(UtilFormatOut.checkNull(subContentElement.getAttribute("edit-request")));
            return;
        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            try {
                screenStringRenderer.renderContentBegin(writer, context, this);
                screenStringRenderer.renderContentBody(writer, context, this);
                screenStringRenderer.renderContentEnd(writer, context, this);
            } catch (IOException e) {
                String errMsg = "Error rendering content with contentId [" + getContentId(context) + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }

        }
        
        public String getContentId(Map context) {
            return this.contentId.expandString(context);
        }
        
        public String getEditRequest(Map context) {
            return this.editRequest.expandString(context);
        }
        
    }

    public static class SubContent extends ModelScreenWidget {
        
        protected FlexibleStringExpander contentId;
        protected FlexibleStringExpander assocName;
        protected FlexibleStringExpander editRequest;
        
        public SubContent(ModelScreen modelScreen, Element subContentElement) {
            super(modelScreen, subContentElement);

            // put the text attribute first, then the pcdata under the element, if both are there of course
            this.contentId = new FlexibleStringExpander(UtilFormatOut.checkNull(subContentElement.getAttribute("content-id")));
            this.assocName = new FlexibleStringExpander(UtilFormatOut.checkNull(subContentElement.getAttribute("assoc-name")));
            this.editRequest = new FlexibleStringExpander(UtilFormatOut.checkNull(subContentElement.getAttribute("edit-request")));

        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            try {
                screenStringRenderer.renderSubContentBegin(writer, context, this);
                screenStringRenderer.renderSubContentBody(writer, context, this);
                screenStringRenderer.renderSubContentEnd(writer, context, this);
            } catch (IOException e) {
                String errMsg = "Error rendering subContent with contentId [" + getContentId(context) + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }
        }
        
        public String getContentId(Map context) {
            return this.contentId.expandString(context);
        }
        
        public String getAssocName(Map context) {
            return this.assocName.expandString(context);
        }
        
        public String getEditRequest(Map context) {
            return this.editRequest.expandString(context);
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
    
    public static class Link extends ModelScreenWidget {
        protected FlexibleStringExpander textExdr;
        protected FlexibleStringExpander idExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander targetExdr;
        protected FlexibleStringExpander targetWindowExdr;
        protected FlexibleStringExpander prefixExdr;
        protected FlexibleStringExpander nameExdr;
        protected Image image;
        protected String urlMode = "intra-app";
        protected boolean fullPath = false;
        protected boolean secure = false;
        protected boolean encode = false;
        

        public Link(ModelScreen modelScreen, Element linkElement) {
            super(modelScreen, linkElement);

            setText(linkElement.getAttribute("text"));
            setId(linkElement.getAttribute("id"));
            setStyle(linkElement.getAttribute("style"));
            setName(linkElement.getAttribute("name"));
            setTarget(linkElement.getAttribute("target"));
            setTargetWindow(linkElement.getAttribute("target-window"));
            setPrefix(linkElement.getAttribute("prefix"));
            setUrlMode(linkElement.getAttribute("url-mode"));
            setFullPath(linkElement.getAttribute("full-path"));
            setSecure(linkElement.getAttribute("secure"));
            setEncode(linkElement.getAttribute("encode"));
            Element imageElement = UtilXml.firstChildElement(linkElement, "image");
            if (imageElement != null) {
                this.image = new Image(modelScreen, imageElement);
            }

        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            try {
                screenStringRenderer.renderLink(writer, context, this);
            } catch (IOException e) {
                String errMsg = "Error rendering link with id [" + getId(context) + "]: " + e.toString();
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
        
        public String getTarget(Map context) {
            return this.targetExdr.expandString(context);
        }
        
        public String getName(Map context) {
            return this.nameExdr.expandString(context);
        }
        
        public String getTargetWindow(Map context) {
            return this.targetWindowExdr.expandString(context);
        }
        
        public String getUrlMode() {
            return this.urlMode;
        }
        
        public String getPrefix(Map context) {
            return this.prefixExdr.expandString(context);
        }
        
        public boolean getFullPath() {
            return this.fullPath;
        }
        
        public boolean getSecure() {
            return this.secure;
        }
        
        public boolean getEncode() {
            return this.encode;
        }
        
        public Image getImage() {
            return this.image;
        }

        public void setText( String val ) {
            String textAttr = UtilFormatOut.checkNull(val);
            this.textExdr = new FlexibleStringExpander(textAttr);
        }
        public void setId( String val ) {
            this.idExdr = new FlexibleStringExpander(val);
        }
        public void setStyle( String val ) {
            this.styleExdr = new FlexibleStringExpander(val);
        }
        public void setTarget( String val ) {
            this.targetExdr = new FlexibleStringExpander(val);
        }
        public void setName( String val ) {
            this.nameExdr = new FlexibleStringExpander(val);
        }
        public void setTargetWindow( String val ) {
            this.targetWindowExdr = new FlexibleStringExpander(val);
        }
        public void setPrefix( String val ) {
            this.prefixExdr = new FlexibleStringExpander(val);
        }
        public void setUrlMode( String val ) {
            if (UtilValidate.isNotEmpty(val))
                this.urlMode = val;
        }
        public void setFullPath( String val ) {
            String sFullPath = val;
            if (sFullPath != null && sFullPath.equalsIgnoreCase("true"))
                this.fullPath = true;
            else
                this.fullPath = false;
        }

        public void setSecure( String val ) {
            String sSecure = val;
            if (sSecure != null && sSecure.equalsIgnoreCase("true"))
                this.secure = true;
            else
                this.secure = false;
        }

        public void setEncode( String val ) {
            String sEncode = val;
            if (sEncode != null && sEncode.equalsIgnoreCase("true"))
                this.encode = true;
            else
                this.encode = false;
        }
        public void setImage( Image img ) {
            this.image = img;
        }
            
    }

    public static class Image extends ModelScreenWidget {

        protected FlexibleStringExpander srcExdr;
        protected FlexibleStringExpander idExdr;
        protected FlexibleStringExpander styleExdr;
        protected FlexibleStringExpander widthExdr;
        protected FlexibleStringExpander heightExdr;
        protected FlexibleStringExpander borderExdr;
        protected String urlMode = "content";
        

        public Image( ModelScreen modelScreen, Element imageElement) {
            super(modelScreen, imageElement);

            setSrc(imageElement.getAttribute("src"));
            setId(imageElement.getAttribute("id"));
            setStyle(imageElement.getAttribute("style"));
            setWidth(imageElement.getAttribute("width"));
            setHeight(imageElement.getAttribute("height"));
            setBorder(UtilFormatOut.checkEmpty(imageElement.getAttribute("border"), "0"));
            setUrlMode(UtilFormatOut.checkEmpty(imageElement.getAttribute("url-mode"), "content"));

        }

        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            try {
                screenStringRenderer.renderImage(writer, context, this);
            } catch (IOException e) {
                String errMsg = "Error rendering image with id [" + getId(context) + "]: " + e.toString();
                Debug.logError(e, errMsg, module);
                throw new RuntimeException(errMsg);
            }
        }
        
        public String getSrc(Map context) {
            return this.srcExdr.expandString(context);
        }
        
        public String getId(Map context) {
            return this.idExdr.expandString(context);
        }
        
        public String getStyle(Map context) {
            return this.styleExdr.expandString(context);
        }

        public String getWidth(Map context) {
            return this.widthExdr.expandString(context);
        }

        public String getHeight(Map context) {
            return this.heightExdr.expandString(context);
        }

        public String getBorder(Map context) {
            return this.borderExdr.expandString(context);
        }
        
        public String getUrlMode() {
            return this.urlMode;
        }
        
        public void setSrc( String val ) {
            String textAttr = UtilFormatOut.checkNull(val);
            this.srcExdr = new FlexibleStringExpander(textAttr);
        }
        public void setId( String val ) {
            this.idExdr = new FlexibleStringExpander(val);
        }
        public void setStyle( String val ) {
            this.styleExdr = new FlexibleStringExpander(val);
        }
        public void setWidth( String val ) {
            this.widthExdr = new FlexibleStringExpander(val);
        }
        public void setHeight( String val ) {
            this.heightExdr = new FlexibleStringExpander(val);
        }
        public void setBorder( String val ) {
            this.borderExdr = new FlexibleStringExpander(val);
        }
        public void setUrlMode( String val ) {
            if (UtilValidate.isEmpty(val))
                this.urlMode = "content";
            else
                this.urlMode = val;
        }
            
    }
}

