/*
 * $Id: ModelScreen.java,v 1.2 2004/07/10 17:08:48 jonesde Exp $
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

import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.LocalDispatcher;
import org.w3c.dom.Element;

/**
 * Widget Library - Screen model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.2 $
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

    }

    /**
     * Renders this screen to a String, i.e. in a text format, as defined with the
     * ScreenStringRenderer implementation.
     *
     * @param buffer The StringBuffer that the screen text will be written to
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
    public void renderScreenString(StringBuffer buffer, Map context, ScreenStringRenderer screenStringRenderer) {
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
    public void setName(String name) {
        this.name = name;
    }
    
    public static interface ScreenAction {
        public void runAction(Map context);
    }

    public static interface ScreenWidget {
        public void renderWidgetString(StringBuffer buffer, Map context, ScreenStringRenderer screenStringRenderer);
    }
    public static class Section implements ScreenWidget {
        public Section(Element sectionElement) {
            // TODO: implement this
        }

        public void renderWidgetString(StringBuffer buffer, Map context, ScreenStringRenderer screenStringRenderer) {
            // TODO: implement this
        }
    }
}
