/*
 * $Id: HtmlWidget.java 3547 2004-09-24 19:23:47Z jonesde $
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
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.content.webapp.ftl.FreeMarkerWorker;
import org.w3c.dom.Element;

import freemarker.template.TemplateException;

/**
 * Widget Library - Screen model HTML class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev: 3547 $
 * @since      3.1
 */
public class IterateSectionWidget extends ModelScreenWidget {
    public static final String module = IterateSectionWidget.class.getName();
    
    protected ModelScreenWidget childWidget;
    protected List sectionList;
    protected FlexibleStringExpander listNameExdr;
    protected FlexibleStringExpander entryNameExdr;
    protected FlexibleStringExpander keyNameExdr;
    protected String paginateTarget;
    protected boolean paginate = true;
    
    public static int DEFAULT_PAGE_SIZE = 100;
    protected int viewIndex = 0;
    protected int viewSize = DEFAULT_PAGE_SIZE;
    protected int lowIndex = -1;
    protected int highIndex = -1;
    protected int listSize = 0;
    protected int actualPageSize = 0;
    

    public IterateSectionWidget(ModelScreen modelScreen, Element iterateSectionElement) {
        super(modelScreen, iterateSectionElement);
        listNameExdr = new FlexibleStringExpander(iterateSectionElement.getAttribute("list-name"));
        entryNameExdr = new FlexibleStringExpander(iterateSectionElement.getAttribute("entry-name"));
        keyNameExdr = new FlexibleStringExpander(iterateSectionElement.getAttribute("key-name"));
        if (this.paginateTarget == null || iterateSectionElement.hasAttribute("paginate-target"))
            this.paginateTarget = iterateSectionElement.getAttribute("paginate-target");
         
        paginate = "true".equals(iterateSectionElement.getAttribute("true"));
        if (iterateSectionElement.hasAttribute("view-size"))
            setViewSize(iterateSectionElement.getAttribute("view-size"));
        sectionList = new ArrayList();
        List childElementList = UtilXml.childElementList(iterateSectionElement);
        Iterator childElementIter = childElementList.iterator();
        while (childElementIter.hasNext()) {
            Element sectionElement = (Element) childElementIter.next();
            ModelScreenWidget.Section section = new ModelScreenWidget.Section(modelScreen, sectionElement);
            sectionList.add(section);
        }
    }

    public void renderWidgetString(Writer writer, Map context, ScreenStringRenderer screenStringRenderer) {
    
        boolean isEntrySet = false;
            if (!(context instanceof MapStack)) {
                context = new MapStack(context);
            }
            
            MapStack contextMs = (MapStack) context;
            contextMs.push();

            // create a standAloneStack, basically a "save point" for this SectionsRenderer, and make a new "screens" object just for it so it is isolated and doesn't follow the stack down
        String listName = this.listNameExdr.expandString(context);
        String entryName = this.entryNameExdr.expandString(context);
        String keyName = this.keyNameExdr.expandString(context);
        Object obj = context.get(listName);
        if (obj == null) {
            Debug.logError("No object found for listName:" + listName, module);
            return;
        }
        List theList = null;
        if (obj instanceof Map ) {
            Set entrySet = ((Map)obj).entrySet();   
            Object [] a = entrySet.toArray();
            theList = Arrays.asList(a);
            isEntrySet = true;
        } else if (obj instanceof List ) {
            theList = (List)context.get(listName);
        } else {
            Debug.logError("Object not list or map type", module);
            return;
        }
        getListLimits(context, theList);
        int rowCount = 0;
        Iterator iter = theList.iterator();
        int itemIndex = -1;
        while (iter.hasNext()) {
            itemIndex++;
            if (itemIndex >= highIndex) {
                break;
            }
            Object item = iter.next();
            if (itemIndex < lowIndex) {
                continue;
            }
            if (isEntrySet) {
                contextMs.put(entryName, ((Map)item).get("value"));   
                contextMs.put(keyName, ((Map)item).get("key"));   
            } else {
                contextMs.put(entryName, item);
            }
            contextMs.put("itemIndex", new Integer(itemIndex));
            
            rowCount++;
            Iterator sectionIter = this.sectionList.iterator();
            while (sectionIter.hasNext()) {
                ModelScreenWidget.Section section = (ModelScreenWidget.Section)sectionIter.next();
                section.renderWidgetString(writer, contextMs, screenStringRenderer);
            }
        }
        if (itemIndex < highIndex) {
            setHighIndex(itemIndex);
        }
        setActualPageSize(highIndex - lowIndex);
        contextMs.pop();

    }
    /*
     * @return
     */
    public String getPaginateTarget() {
        return this.paginateTarget;
    }
    
    public boolean getPaginate() {
        return this.paginate;
    }
    
    public void setPaginate(boolean val) {
        paginate = val;
    }
    
    /**
     * @param string
     */
    public void setPaginateTarget(String string) {
        this.paginateTarget = string;
    }

    public void setViewIndex(int val) {
        viewIndex = val;
    }

    public void setViewSize(int val) {
        viewSize = val;
    }

    public void setViewSize(String val) {
        try {
            Integer sz = new Integer(val);
            viewSize = sz.intValue();
        } catch(NumberFormatException e) {
            viewSize = DEFAULT_PAGE_SIZE;   
        }
    }

    public void setListSize(int val) {
        listSize = val;
    }

    public void setLowIndex(int val) {
        lowIndex = val;
    }

    public void setHighIndex(int val) {
        highIndex = val;
    }
    public void setActualPageSize(int val) {
        actualPageSize = val;
    }

    public int getViewIndex() {
        return viewIndex;
    }

    public int getViewSize() {
        return viewSize;
    }

    public int getListSize() {
        return listSize;
    }

    public int getLowIndex() {
        return lowIndex;
    }

    public int getHighIndex() {
        return highIndex;
    }
    
    public int getActualPageSize() {
        return actualPageSize;
    }
    
    public void getListLimits(Map context, List items) {
        listSize = items.size();
        
       if (paginate) {
            try {
                viewIndex = ((Integer) context.get("viewIndex")).intValue();
            } catch (Exception e) {
                viewIndex = 0;
            }
    
            try {
                viewSize = ((Integer) context.get("viewSize")).intValue();
            } catch (Exception e) {
                //viewSize = DEFAULT_PAGE_SIZE;
            }
            lowIndex = viewIndex * viewSize;
            highIndex = (viewIndex + 1) * viewSize;
    
    
        } else {
            viewIndex = 0;
            viewSize = DEFAULT_PAGE_SIZE;
            lowIndex = 0;
            highIndex = DEFAULT_PAGE_SIZE;
        }
    }
    


}

