/*
 * $Id: ModelScreenAction.java,v 1.1 2004/07/11 07:24:52 jonesde Exp $
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
public abstract class ModelScreenAction {
    protected ModelScreen modelScreen;

    public ModelScreenAction(ModelScreen modelScreen, Element actionElement) {
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
            ModelScreenAction action = (ModelScreenAction) actionIter.next();
            action.runAction(context);
        }
    }
}

