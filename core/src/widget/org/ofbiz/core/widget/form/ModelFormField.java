/*
 * $Id$
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.widget.form;

import java.util.*;
import org.w3c.dom.*;
import org.ofbiz.core.util.*;

/**
 * Widget Library - Form model class
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.2
 */
public class ModelFormField {
    
    public static final String module = ModelFormField.class.getName();

    protected String name;
    protected String mapName;
    protected String entityName;
    protected String serviceName;
    protected String entryName;
    protected String parameterName;
    protected String fieldName;
    protected String attributeName;
    protected String title;
    protected String tooltip;
    protected String titleStyle;
    protected String widgetStyle;
    protected int position;
    protected String redWhen;
    protected String useWhen;

    // ===== CONSTRUCTORS =====
    /** Default Constructor */
    public ModelFormField() {}

    /** XML Constructor */
    public ModelFormField(Element formElement) {
        this.name = formElement.getAttribute("name");
        this.mapName = formElement.getAttribute("map-name");
        this.entityName = formElement.getAttribute("entity-name");
        this.serviceName = formElement.getAttribute("service-name");
        this.entryName = UtilXml.checkEmpty(formElement.getAttribute("entry-name"), this.name);
        this.parameterName = UtilXml.checkEmpty(formElement.getAttribute("parameter-name"), this.name);
        this.fieldName = UtilXml.checkEmpty(formElement.getAttribute("field-name"), this.name);
        this.attributeName = UtilXml.checkEmpty(formElement.getAttribute("attribute-name"), this.name);
        this.title = formElement.getAttribute("title");
        this.tooltip = formElement.getAttribute("tooltip");
        this.titleStyle = formElement.getAttribute("title-style");
        this.widgetStyle = formElement.getAttribute("widget-style");
        this.redWhen = formElement.getAttribute("red-when");
        this.useWhen = formElement.getAttribute("use-when");
        
        String positionStr = formElement.getAttribute("position");
        try {
            position = Integer.parseInt(positionStr);
        } catch (Exception e) {
            position = 1;
            if (positionStr != null && positionStr.length() > 0) {
                Debug.logError(e, "Could not convert position attribute of the field element to an integer: [" + positionStr + "], using the default of 1");
            }
        }
        
        
    }
    
    /**
     * @return
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @return
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return
     */
    public String getEntryName() {
        return entryName;
    }

    /**
     * @return
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return
     */
    public String getRedWhen() {
        return redWhen;
    }

    /**
     * @return
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return
     */
    public String getTitleStyle() {
        return titleStyle;
    }

    /**
     * @return
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * @return
     */
    public String getUseWhen() {
        return useWhen;
    }

    /**
     * @return
     */
    public String getWidgetStyle() {
        return widgetStyle;
    }

    /**
     * @param string
     */
    public void setAttributeName(String string) {
        attributeName = string;
    }

    /**
     * @param string
     */
    public void setEntityName(String string) {
        entityName = string;
    }

    /**
     * @param string
     */
    public void setEntryName(String string) {
        entryName = string;
    }

    /**
     * @param string
     */
    public void setFieldName(String string) {
        fieldName = string;
    }

    /**
     * @param string
     */
    public void setMapName(String string) {
        mapName = string;
    }

    /**
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

    /**
     * @param string
     */
    public void setParameterName(String string) {
        parameterName = string;
    }

    /**
     * @param i
     */
    public void setPosition(int i) {
        position = i;
    }

    /**
     * @param string
     */
    public void setRedWhen(String string) {
        redWhen = string;
    }

    /**
     * @param string
     */
    public void setServiceName(String string) {
        serviceName = string;
    }

    /**
     * @param string
     */
    public void setTitle(String string) {
        title = string;
    }

    /**
     * @param string
     */
    public void setTitleStyle(String string) {
        titleStyle = string;
    }

    /**
     * @param string
     */
    public void setTooltip(String string) {
        tooltip = string;
    }

    /**
     * @param string
     */
    public void setUseWhen(String string) {
        useWhen = string;
    }

    /**
     * @param string
     */
    public void setWidgetStyle(String string) {
        widgetStyle = string;
    }
    
    public static abstract class FieldInfo {
        protected String fieldTypeName;
        protected ModelFormField modelFormField;
        
        /** Don't allow the Default Constructor */
        protected FieldInfo() {}

        /** Value Constructor */        
        public FieldInfo(String fieldTypeName, ModelFormField modelFormField) {
            this.fieldTypeName = fieldTypeName;
            this.modelFormField = modelFormField;
        }
        
        /** XML Constructor */        
        public FieldInfo(Element element, ModelFormField modelFormField) {
            this.fieldTypeName = element.getTagName();
            this.modelFormField = modelFormField;
        }
        
        /**
         * @return
         */
        public String getFieldTypeName() {
            return fieldTypeName;
        }

        /**
         * @return
         */
        public ModelFormField getModelFormField() {
            return modelFormField;
        }

        /**
         * @param string
         */
        public void setFieldTypeName(String string) {
            fieldTypeName = string;
        }

        public abstract void renderFieldString(StringBuffer buffer, FormStringRenderer formStringRenderer);
    }
    
    public static class DisplayField extends FieldInfo {
        protected boolean alsoHidden = true;
        protected String description;
        
        protected DisplayField() { super(); }

        public DisplayField(String fieldTypeName, ModelFormField modelFormField) {
            super(fieldTypeName, modelFormField);
        }

        public DisplayField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            
            String description = element.getAttribute("description");
            String alsoHiddenStr = element.getAttribute("also-hidden");
            try {
                alsoHidden = Boolean.getBoolean(alsoHiddenStr);
            } catch (Exception e) {
                if (alsoHiddenStr != null && alsoHiddenStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + alsoHiddenStr + "], setting to default of " + alsoHidden);
                }
            }
        }

        public void renderFieldString(StringBuffer buffer, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderDisplayField(buffer, this);
        }
        
        /**
         * @return
         */
        public boolean isAlsoHidden() {
            return alsoHidden;
        }

        /**
         * @return
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param b
         */
        public void setAlsoHidden(boolean b) {
            alsoHidden = b;
        }

        /**
         * @param string
         */
        public void setDescription(String string) {
            description = string;
        }
    }
    
    public static class HyperlinkField extends FieldInfo {
        protected boolean alsoHidden = true;
        protected String target;
        protected String description;
        
        protected HyperlinkField() { super(); }

        public HyperlinkField(String fieldTypeName, ModelFormField modelFormField) {
            super(fieldTypeName, modelFormField);
        }

        public HyperlinkField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            
            String target = element.getAttribute("target");
            String description = element.getAttribute("description");
            String alsoHiddenStr = element.getAttribute("also-hidden");
            try {
                alsoHidden = Boolean.getBoolean(alsoHiddenStr);
            } catch (Exception e) {
                if (alsoHiddenStr != null && alsoHiddenStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + alsoHiddenStr + "], setting to default of " + alsoHidden);
                }
            }
        }

        public void renderFieldString(StringBuffer buffer, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderHyperlinkField(buffer, this);
        }
        
        /**
         * @return
         */
        public boolean isAlsoHidden() {
            return alsoHidden;
        }

        /**
         * @return
         */
        public String getDescription() {
            return description;
        }

        /**
         * @return
         */
        public String getTarget() {
            return target;
        }

        /**
         * @param b
         */
        public void setAlsoHidden(boolean b) {
            alsoHidden = b;
        }

        /**
         * @param string
         */
        public void setDescription(String string) {
            description = string;
        }

        /**
         * @param string
         */
        public void setTarget(String string) {
            target = string;
        }
    }
    
    public static class TextField extends FieldInfo {
        protected int size = 25;
        protected Integer maxlength;
        
        protected TextField() { super(); }

        public TextField(String fieldTypeName, ModelFormField modelFormField) {
            super(fieldTypeName, modelFormField);
        }

        public TextField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            
            String sizeStr = element.getAttribute("size");
            try {
                size = Integer.parseInt(sizeStr);
            } catch (Exception e) {
                if (sizeStr != null && sizeStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + sizeStr + "], setting to the default of " + size);
                }
            }
            
            String maxlengthStr = element.getAttribute("maxlength");
            try {
                maxlength = Integer.valueOf(maxlengthStr);
            } catch (Exception e) {
                maxlength = null;
                if (maxlengthStr != null && maxlengthStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + sizeStr + "], setting to null; default of no maxlength will be used");
                }
            }
        }

        public void renderFieldString(StringBuffer buffer, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderTextField(buffer, this);
        }
        
        /**
         * @return
         */
        public Integer getMaxlength() {
            return maxlength;
        }

        /**
         * @return
         */
        public int getSize() {
            return size;
        }

        /**
         * @param integer
         */
        public void setMaxlength(Integer integer) {
            maxlength = integer;
        }

        /**
         * @param i
         */
        public void setSize(int i) {
            size = i;
        }
    }
    
    public static class TextareaField extends FieldInfo {
        protected int cols = 60;
        protected int rows = 2;
        
        protected TextareaField() { super(); }

        public TextareaField(String fieldTypeName, ModelFormField modelFormField) {
            super(fieldTypeName, modelFormField);
        }

        public TextareaField(Element element, ModelFormField modelFormField) {
            super(element, modelFormField);
            
            String colsStr = element.getAttribute("cols");
            try {
                cols = Integer.parseInt(colsStr);
            } catch (Exception e) {
                if (colsStr != null && colsStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + colsStr + "], setting to default of " + cols);
                }
            }
            
            String rowsStr = element.getAttribute("rows");
            try {
                rows = Integer.parseInt(rowsStr);
            } catch (Exception e) {
                if (rowsStr != null && rowsStr.length() > 0) {
                    Debug.logError("Could not parse the size value of the text element: [" + rowsStr + "], setting to default of " + rows);
                }
            }
        }

        public void renderFieldString(StringBuffer buffer, FormStringRenderer formStringRenderer) {
            formStringRenderer.renderTextareaField(buffer, this);
        }
        
        /**
         * @return
         */
        public int getCols() {
            return cols;
        }

        /**
         * @return
         */
        public int getRows() {
            return rows;
        }

        /**
         * @param i
         */
        public void setCols(int i) {
            cols = i;
        }

        /**
         * @param i
         */
        public void setRows(int i) {
            rows = i;
        }
    }

}
