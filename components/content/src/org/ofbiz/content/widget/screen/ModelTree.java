/*
 * $Id: ModelTree.java,v 1.1 2004/07/16 05:33:47 byersa Exp $
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
import java.util.*;

import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.LocalDispatcher;
import org.w3c.dom.Element;

/**
 * Widget Library - Tree model class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class ModelTree extends ModelScreenWidget {

    public static final String module = ModelTree.class.getName();

    protected String name;
    protected String rootNodeName;
    protected List nodeList = new ArrayList();
    
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
            nodeList.add(node);
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
        
        Iterator nodeIter = nodeList.iterator();
        while (nodeIter.hasNext()) {
            ModelNode node = (ModelNode)nodeIter.next();
            node.renderWidgetString(writer, context, screenStringRenderer);
        }
    }

    public class ModelNode {

        protected ModelTree rootTree;
        protected ModelScreen screen;
        //protected ScreenConditionOne entityOne;
        protected ModelScreenWidget.Label label;
        //protected ScreenService screenService;
        //protected GenericValue entityValue;
        protected List subNodeList = new ArrayList();
        protected String name;

        public ModelNode() {}

        public ModelNode(Element nodeElement, ModelTree modelTree) {

            this.rootTree = modelTree;
            this.name = nodeElement.getAttribute("name");
    
/*
            Element entityOneElement = UtilXml.firstChildElement(nodeElement, "entity-one");
            if (entityOneElement != null) {
                this.entityOne = new ScreenConditionOne(this, entityOneElement);
            }
            
            Element screenServiceElement = UtilXml.firstChildElement(nodeElement, "service");
            if (screenServiceElement != null) {
                this.screenService = new ScreenService(this, screenServiceElement);
            }
    
            if (entityOneElement == null && screenServiceElement == null) {
                throw new IllegalArgumentException("Neither 'entity-one' nor 'service' found for the tree definition with name: " + this.name);
            }
*/
            
            Element screenElement = UtilXml.firstChildElement(nodeElement, "screen");
            GenericDelegator delegator = modelScreen.getDelegator();
            LocalDispatcher dispatcher = modelScreen.getDispacher();
            if (screenElement != null) {
                this.screen = new ModelScreen(screenElement, delegator, dispatcher);
            }
            
            Element labelElement = UtilXml.firstChildElement(nodeElement, "label");
            if (labelElement != null) {
                this.label = new ModelScreenWidget.Label(modelScreen, labelElement);
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
            
/*
            if (entityOne != null) {
                entityValue = entityOne.retrieveValue(context);
            } else if (screenService != null) {
                entityValue = screenService.retrieveValue(context);
            } else {
                return;
            }
            context.put("entityValue", entityValue);
*/
         
            screenStringRenderer.renderNodeOpen(writer, context,  this);
            if ( screen != null)
                screen.renderScreenString(writer, context, screenStringRenderer);
            if ( label != null)
                label.renderWidgetString(writer, context, screenStringRenderer);
            List tempNodeList = new ArrayList();
            Iterator nodeIter = subNodeList.iterator();
            while (nodeIter.hasNext()) {
                ModelSubNode subNode = (ModelSubNode)nodeIter.next();
                List subNodes = subNode.retrieveSubNodes(context);
                tempNodeList.addAll(subNodes);
            }

            List subNodeValueList = sortNodes(tempNodeList);

            Iterator nodeValueIter = subNodeValueList.iterator();
            while (nodeValueIter.hasNext()) {
                ModelNode node = (ModelNode)nodeValueIter.next();
                node.renderWidgetString(writer, context, screenStringRenderer);
            }
            screenStringRenderer.renderNodeClose(writer, context,  this);
        }

        public List sortNodes(List nodeList) {

            List returnList = new ArrayList(nodeList);
            return returnList;
        }

        public String getName() {
            return name;
        }
    
        public class ModelSubNode {
    
            protected ModelNode rootNode;
            protected ModelNode subNode;
            protected String nodeName;
            //protected ScreenConditionAnd entityAnd;
            //protected ScreenService screenService;
            protected List outFieldMaps;
    
            public ModelSubNode() {}
    
            public ModelSubNode(Element nodeElement, ModelNode modelNode) {
    
                this.rootNode = modelNode;
                this.nodeName = nodeElement.getAttribute("node-name");
        
/*
                Element entityAndElement = UtilXml.firstChildElement(nodeElement, "entity-one");
                if (entityAndElement != null) {
                    this.entityAnd = new ScreenConditionAnd(this, entityAndElement);
                }
                
                Element screenServiceElement = UtilXml.firstChildElement(nodeElement, "service");
                if (screenServiceElement != null) {
                    this.screenService = new ScreenService(this, screenServiceElement);
                }
        
                if (entityAndElement == null && screenServiceElement == null) {
                    throw new IllegalArgumentException("Neither 'entity-one' nor 'service' found for the tree definition with name: " + this.name);
                }
*/
                
                
            }
        
            public List retrieveSubNodes(Map context) {
                List retrievedNodes = new ArrayList();
                // TODO: finish
                return retrievedNodes;
            }
            public String getNodeName() {
                return nodeName;
            }
    
        }


    }


}

