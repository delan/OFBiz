/*
 * $Id: ModelScreen.java,v 1.3 2004/07/11 05:00:03 jonesde Exp $
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
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.LocalDispatcher;
import org.w3c.dom.Element;

/**
 * Widget Library - Screen model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.3 $
 * @since      3.1
 */
public class ModelScreen {

    public static final String module = ModelScreen.class.getName();

    protected GenericDelegator delegator;
    protected LocalDispatcher dispatcher;

    protected String name;
    
    protected List actions;
    protected Section section;

    // ===== CONSTRUCTORS =====
    /** Default Constructor */
    public ModelScreen() {}

    /** XML Constructor */
    public ModelScreen(Element screenElement, GenericDelegator delegator, LocalDispatcher dispatcher) {
        this.delegator = delegator;
        this.dispatcher = dispatcher;

        this.name = screenElement.getAttribute("name");

        // read all actions under the "actions" element
        Element actionsElement = UtilXml.firstChildElement(screenElement, "actions");
        if (actionsElement != null) {
            this.actions = ScreenAction.readSubActions(this, actionsElement);
        }
        
        // read in the section, which will read all sub-widgets too
        Element sectionElement = UtilXml.firstChildElement(screenElement, "section");
        if (sectionElement == null) {
            throw new IllegalArgumentException("No section found for the screen definition with name: " + this.name);
        }
        this.section = new Section(this, sectionElement);
    }

    /**
     * Renders this screen to a String, i.e. in a text format, as defined with the
     * ScreenStringRenderer implementation.
     *
     * @param writer The Writer that the screen text will be written to
     * @param context Map containing the screen context; the following are
     *   reserved words in this context: parameters (Map), isError (Boolean),
     *   itemIndex (Integer, for lists only, otherwise null), bshInterpreter,
     *   screenName (String, optional alternate name for screen, defaults to the
     *   value of the name attribute)
     * @param screenStringRenderer An implementation of the ScreenStringRenderer
     *   interface that is responsible for the actual text generation for
     *   different screen elements; implementing your own makes it possible to
     *   use the same screen definitions for many types of screen UIs
     */
    public void renderScreenString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
        // run the actions
        ScreenAction.runSubActions(this.actions, context);
        
        // render the screen, starting with the top-level section
        this.section.renderWidgetString(writer, context, screenStringRenderer);
    }

    public LocalDispatcher getDispacher() {
        return this.dispatcher;
    }

    public GenericDelegator getDelegator() {
        return this.delegator;
    }
    
    public String getName() {
        return name;
    }
    
    public static abstract class ScreenAction {
        protected ModelScreen modelScreen;

        public ScreenAction(ModelScreen modelScreen, Element actionElement) {
            this.modelScreen = modelScreen;
        }
        
        public abstract void runAction(Map context);
        
        public static List readSubActions(ModelScreen modelScreen, Element parentElement) {
            List actions = new LinkedList();
            
            List actionElementList = UtilXml.childElementList(parentElement);
            Iterator actionElementIter = actionElementList.iterator();
            while (actionElementIter.hasNext()) {
                Element actionElement = (Element) actionElementIter.next();
                // script | service | entity-one | entity-and | entity-condition
                if ("script".equals(actionElement.getNodeName())) {
                    // TODO: implement this
                    // actions.add(new Script(modelScreen, actionElement));
                } else if ("service".equals(actionElement.getNodeName())) {
                    // TODO: implement this
                } else if ("entity-one".equals(actionElement.getNodeName())) {
                    // TODO: implement this
                } else if ("entity-and".equals(actionElement.getNodeName())) {
                    // TODO: implement this
                } else if ("entity-condition".equals(actionElement.getNodeName())) {
                    // TODO: implement this
                }
            }
            
            return actions;
        }
        
        public static void runSubActions(List actions, Map context) {
            Iterator actionIter = actions.iterator();
            while (actionIter.hasNext()) {
                ScreenAction action = (ScreenAction) actionIter.next();
                action.runAction(context);
            }
        }
    }

    public static abstract class ScreenWidget {
        protected ModelScreen modelScreen;
        
        public ScreenWidget(ModelScreen modelScreen, Element widgetElement) {
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
                ScreenWidget subWidget = (ScreenWidget) subWidgetIter.next();
                subWidget.renderWidgetString(writer, context, screenStringRenderer);
            }
        }
    }
    public static class Section extends ScreenWidget {
        protected String name;
        protected List subWidgets;
        
        public Section(ModelScreen modelScreen, Element sectionElement) {
            super(modelScreen, sectionElement);
            this.name = sectionElement.getAttribute("name");
            
            // read sub-widgets
            this.subWidgets = ScreenWidget.readSubWidgets(this.modelScreen, sectionElement);
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

