/*
 * $Id: ModelTree.java,v 1.4 2004/08/09 23:52:21 jonesde Exp $
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.w3c.dom.Element;

/**
 * Widget Library - Tree model class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.4 $
 * @since      3.1
 */
public class ModelTree extends ModelScreenWidget {

    public static final String module = ModelTree.class.getName();

    protected String name;
    protected String rootNodeName;
    protected List nodeList = new ArrayList();
    protected Map nodeMap = new HashMap();
    
// ===== CONSTRUCTORS =====
    /** Default Constructor */

    /** XML Constructor */
    public ModelTree(ModelScreen modelScreen, Element treeElement) {

        super(modelScreen, treeElement);

        this.name = treeElement.getAttribute("name");
        this.rootNodeName = treeElement.getAttribute("root-node-name");

        List nodeElements = UtilXml.childElementList(treeElement, "node");
        Iterator nodeElementIter = nodeElements.iterator();
        while (nodeElementIter.hasNext()) {
            Element nodeElementEntry = (Element) nodeElementIter.next();
            ModelNode node = new ModelNode(nodeElementEntry, this);
            String nodeName = node.getName();
            nodeList.add(node);
            nodeMap.put(nodeName,node);
        }
    
        if (nodeList.size() == 0) {
            throw new IllegalArgumentException("No node elements found for the tree definition with name: " + this.name);
        }

    }
    
    public String getName() {
        return name;
    }

    public String getRootNodeName() {
        return rootNodeName;
    }


    /**
     * Renders this tree to a String, i.e. in a text format, as defined with the
     * TreeStringRenderer implementation.
     *
     * @param writer The Writer that the tree text will be written to
     * @param context Map containing the tree context; the following are
     *   reserved words in this context: parameters (Map), isError (Boolean),
     *   itemIndex (Integer, for lists only, otherwise null), bshInterpreter,
     *   treeName (String, optional alternate name for tree, defaults to the
     *   value of the name attribute)
     * @param treeStringRenderer An implementation of the TreeStringRenderer
     *   interface that is responsible for the actual text generation for
     *   different tree elements; implementing your own makes it possible to
     *   use the same tree definitions for many types of tree UIs
     */
    public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            ModelNode node = (ModelNode)nodeMap.get(rootNodeName);
            node.renderWidgetString(writer, context, screenStringRenderer);
    }

    public static class ModelNode {

        protected ModelScreen screen;
        protected ModelScreenWidget.Label label;
        protected List subNodeList = new ArrayList();
        protected List actions = new ArrayList();
        protected String name;
        protected ModelTree modelTree;

        public ModelNode() {}

        public ModelNode(Element nodeElement, ModelTree modelTree) {

            this.modelTree = modelTree;
            this.name = nodeElement.getAttribute("name");
    
            Element actionsElement = UtilXml.firstChildElement(nodeElement, "entity-one");
            List lst = null;
            if (actionsElement != null) {
                lst = ModelScreenAction.readSubActions(modelTree.modelScreen, actionsElement);
            }
            this.actions.addAll(lst);
        
            actionsElement = UtilXml.firstChildElement(nodeElement, "service");
            if (actionsElement != null) {
                lst = ModelScreenAction.readSubActions(modelTree.modelScreen, actionsElement);
            }
            this.actions.addAll(lst);
        
            Element screenElement = UtilXml.firstChildElement(nodeElement, "screen");
            if (screenElement != null) {
                //this isn't how it works at all, can't create a full screen def from here, just references a screen so we need an object that does that: this.screen = new ModelScreen(screenElement);
            }
            
            Element labelElement = UtilXml.firstChildElement(nodeElement, "label");
            if (labelElement != null) {
                this.label = new ModelScreenWidget.Label(modelTree.modelScreen, labelElement);
            }
    
            if (screenElement == null && labelElement == null) {
                throw new IllegalArgumentException("Neither 'screen' nor 'label' found for the node definition with name: " + this.name);
            }

            List subNodeElements = UtilXml.childElementList(nodeElement, "sub-node");
            Iterator subNodeElementIter = subNodeElements.iterator();
            while (subNodeElementIter.hasNext()) {
                Element subNodeElementEntry = (Element) subNodeElementIter.next();
                ModelSubNode subNode = new ModelSubNode(subNodeElementEntry, this);
                subNodeList.add(subNode);
            }
    
            
        }
    
        public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
            
            ModelScreenAction.runSubActions(this.actions, context);
         
            // TODO: the item below needs to be moved to a TreeStringRenderer class, shouldn't be in the ScreenStringRenderer
            // screenStringRenderer.renderNodeOpen(writer, context,  this);
            
            if ( screen != null)
                screen.renderScreenString(writer, context, screenStringRenderer);
            if ( label != null)
                label.renderWidgetString(writer, context, screenStringRenderer);
            List tempNodeList = new ArrayList();
            Iterator nodeIter = subNodeList.iterator();
            while (nodeIter.hasNext()) {
                ModelSubNode subNode = (ModelSubNode)nodeIter.next();
                String nodeName = subNode.getNodeName();
                ModelNode node = (ModelNode)modelTree.nodeMap.get(nodeName);
                List subNodeActions = subNode.getActions();
                ModelScreenAction.runSubActions(subNodeActions, context);
                List dataFound = (List)context.get("dataFound");
                Iterator dataIter = dataFound.iterator();
                while (dataIter.hasNext()) {
                    GenericValue val = (GenericValue)dataIter.next();
                    GenericPK pk = val.getPrimaryKey();
                    Map newContext = ((MapStack) context).standAloneChildStack();
                    newContext.putAll(pk);
                    node.renderWidgetString(writer, newContext, screenStringRenderer);
                }
            }

            // TODO: the item below needs to be moved to a TreeStringRenderer class, shouldn't be in the ScreenStringRenderer
            //screenStringRenderer.renderNodeClose(writer, context,  this);
        }

        public List sortNodes(List nodeList) {

            List returnList = new ArrayList(nodeList);
            return returnList;
        }

        public String getName() {
            return name;
        }
    
        public static class ModelSubNode {
    
            protected ModelNode rootNode;
            protected ModelNode subNode;
            protected String nodeName;
            protected List actions = new ArrayList();
            protected List outFieldMaps;
    
            public ModelSubNode() {}
    
            public ModelSubNode(Element nodeElement, ModelNode modelNode) {
    
                this.rootNode = modelNode;
                this.nodeName = nodeElement.getAttribute("node-name");
        
                Element actionsElement = UtilXml.firstChildElement(nodeElement, "entity-one");
                List lst = null;
                if (actionsElement != null) {
                    lst = ModelScreenAction.readSubActions(rootNode.modelTree.modelScreen, actionsElement);
                }
                this.actions.addAll(lst);
            
                actionsElement = UtilXml.firstChildElement(nodeElement, "service");
                if (actionsElement != null) {
                    lst = ModelScreenAction.readSubActions(rootNode.modelTree.modelScreen, actionsElement);
                }
                this.actions.addAll(lst);
                
            }
        
            public String getNodeName() {
                return nodeName;
            }
    
            public List getActions() {
                return actions;
            }
    
        }
    }
}

