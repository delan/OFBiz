/*
 * $Id: ModelScreen.java,v 1.6 2004/07/18 10:09:35 jonesde Exp $
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
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.LocalDispatcher;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Widget Library - Screen model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.6 $
 * @since      3.1
 */
public class ModelScreen {

    public static final String module = ModelScreen.class.getName();

    protected String name;
    protected Map modelScreenMap;
    
    protected List actions;
    protected ModelScreenWidget.Section section;

    // ===== CONSTRUCTORS =====
    /** Default Constructor */
    public ModelScreen() {}

    /** XML Constructor */
    public ModelScreen(Element screenElement, Map modelScreenMap) {
        this.name = screenElement.getAttribute("name");
        this.modelScreenMap = modelScreenMap;

        // read all actions under the "actions" element
        Element actionsElement = UtilXml.firstChildElement(screenElement, "actions");
        if (actionsElement != null) {
            this.actions = ModelScreenAction.readSubActions(this, actionsElement);
        }
        
        // read in the section, which will read all sub-widgets too
        Element sectionElement = UtilXml.firstChildElement(screenElement, "section");
        if (sectionElement == null) {
            throw new IllegalArgumentException("No section found for the screen definition with name: " + this.name);
        }
        this.section = new ModelScreenWidget.Section(this, sectionElement);
    }

    /**
     * Renders this screen to a String, i.e. in a text format, as defined with the
     * ScreenStringRenderer implementation.
     *
     * @param writer The Writer that the screen text will be written to
     * @param context Map containing the screen context; the following are
     *   reserved words in this context:
     *    - parameters (contains any special initial parameters coming in)
     *    - userLogin (if a user is logged in)
     *    - autoUserLogin (if a user is automatically logged in, ie no password has been entered)
     *    - formStringRenderer
     *    - request, response, session, application (special case, only in HTML contexts, etc) 
     *    - delegator, dispatcher, security
     *    - sections (used for decorators to reference the sections to be decorated and render them) 
     * @param screenStringRenderer An implementation of the ScreenStringRenderer
     *   interface that is responsible for the actual text generation for
     *   different screen elements; implementing your own makes it possible to
     *   use the same screen definitions for many types of screen UIs
     */
    public void renderScreenString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {

        // run the actions
        ModelScreenAction.runSubActions(this.actions, context);
        
        // render the screen, starting with the top-level section
        this.section.renderWidgetString(writer, context, screenStringRenderer);
    }

    public LocalDispatcher getDispatcher(Map context) {
        LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
        return dispatcher;
    }

    public GenericDelegator getDelegator(Map context) {
        GenericDelegator delegator = (GenericDelegator) context.get("delegator");
        return delegator;
    }
    
    public String getName() {
        return name;
    }
    
    public static class ScreenRenderer {
        protected Writer writer;
        protected Map context;
        protected ScreenStringRenderer screenStringRenderer;
        
        public ScreenRenderer(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            this.writer = writer;
            this.context = context;
            this.screenStringRenderer = screenStringRenderer;
        }
        
        public void render(String combinedName) throws IOException, SAXException, ParserConfigurationException {
            String resourceName = ScreenFactory.getResourceNameFromCombined(combinedName);
            String screenName = ScreenFactory.getScreenNameFromCombined(combinedName);
            render(resourceName, screenName);
        }
        public void render(String resourceName, String screenName) throws IOException, SAXException, ParserConfigurationException {
            ModelScreen modelScreen = ScreenFactory.getScreenFromLocation(resourceName, screenName);
            modelScreen.renderScreenString(writer, context, screenStringRenderer);
        }
    }
}

