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
package org.ofbiz.core.widgetimpl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.core.control.RequestHandler;
import org.ofbiz.core.taglib.ContentUrlTag;
import org.ofbiz.core.util.SiteDefs;
import org.ofbiz.core.util.UtilValidate;
import org.ofbiz.core.widget.form.FormStringRenderer;
import org.ofbiz.core.widget.form.ModelForm;
import org.ofbiz.core.widget.form.ModelFormField;
import org.ofbiz.core.widget.form.ModelFormField.CheckField;
import org.ofbiz.core.widget.form.ModelFormField.DateTimeField;
import org.ofbiz.core.widget.form.ModelFormField.DisplayField;
import org.ofbiz.core.widget.form.ModelFormField.DropDownField;
import org.ofbiz.core.widget.form.ModelFormField.HiddenField;
import org.ofbiz.core.widget.form.ModelFormField.HyperlinkField;
import org.ofbiz.core.widget.form.ModelFormField.IgnoredField;
import org.ofbiz.core.widget.form.ModelFormField.RadioField;
import org.ofbiz.core.widget.form.ModelFormField.ResetField;
import org.ofbiz.core.widget.form.ModelFormField.SubmitField;
import org.ofbiz.core.widget.form.ModelFormField.TextField;
import org.ofbiz.core.widget.form.ModelFormField.TextareaField;

/**
 * Widget Library - HTML Form Renderer implementation
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.2
 */
public class HtmlFormRenderer implements FormStringRenderer {

    HttpServletRequest request;
    HttpServletResponse response;

    protected HtmlFormRenderer() {}

    public HtmlFormRenderer(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }
    
    public void appendOfbizUrl(StringBuffer buffer, String location) {
        ServletContext ctx = (ServletContext) this.request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) ctx.getAttribute(SiteDefs.REQUEST_HANDLER);
        // make and append the link
        buffer.append(rh.makeLink(this.request, this.response, location));
    }
    
    public void appendContentUrl(StringBuffer buffer, String location) {
        ContentUrlTag.appendContentPrefix(this.request, buffer);
        buffer.append(location);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderDisplayField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.DisplayField)
     */
    public void renderDisplayField(StringBuffer buffer, Map context, DisplayField displayField) {
        ModelFormField modelFormField = displayField.getModelFormField();

        buffer.append("<span");

        if (UtilValidate.isNotEmpty(modelFormField.getWidgetStyle())) {
            buffer.append(" class=\"");
            buffer.append(modelFormField.getWidgetStyle());
            buffer.append("\"");
        }

        // add a style of red if this is a date/time field and redWhen is true
        if (modelFormField.shouldBeRed(context)) {
            buffer.append(" style=\"color: red;\"");
        }
        
        buffer.append(">");
        buffer.append(displayField.getDescription(context));
        buffer.append("</span>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderHyperlinkField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.HyperlinkField)
     */
    public void renderHyperlinkField(StringBuffer buffer, Map context, HyperlinkField hyperlinkField) {
        ModelFormField modelFormField = hyperlinkField.getModelFormField();

        buffer.append("<a");

        if (UtilValidate.isNotEmpty(modelFormField.getWidgetStyle())) {
            buffer.append(" class=\"");
            buffer.append(modelFormField.getWidgetStyle());
            buffer.append("\"");
        }
        
        buffer.append(" href=\"");
        this.appendOfbizUrl(buffer, hyperlinkField.getTarget(context));
        buffer.append("\"");

        buffer.append('>');
        
        buffer.append(hyperlinkField.getDescription(context));
        buffer.append("</a>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderTextField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.TextField)
     */
    public void renderTextField(StringBuffer buffer, Map context, TextField textField) {
        ModelFormField modelFormField = textField.getModelFormField();
        
        buffer.append("<input type=\"text\"");
        
        String className = modelFormField.getWidgetStyle();
        if (UtilValidate.isNotEmpty(className)) {
            buffer.append(" class=\"");
            buffer.append(className);
            buffer.append('"');
        }
        
        // add a style of red if this is a date/time field and redWhen is true
        if (modelFormField.shouldBeRed(context)) {
            buffer.append(" style=\"color: red;\"");
        }
        
        buffer.append(" name=\"");
        buffer.append(modelFormField.getParameterName());
        buffer.append('"');
        
        String value = modelFormField.getEntry(context);
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }
        
        buffer.append(" size=\"");
        buffer.append(textField.getSize());
        buffer.append('"');
        
        Integer maxlength = textField.getMaxlength();
        if (maxlength != null) {
            buffer.append(" maxlength=\"");
            buffer.append(maxlength.intValue());
            buffer.append('"');
        }

        buffer.append("/>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderTextareaField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.TextareaField)
     */
    public void renderTextareaField(StringBuffer buffer, Map context, TextareaField textareaField) {
        ModelFormField modelFormField = textareaField.getModelFormField();
        
        buffer.append("<textarea class=\"textAreaBox\"");
        
        String className = modelFormField.getWidgetStyle();
        if (UtilValidate.isNotEmpty(className)) {
            buffer.append(" class=\"");
            buffer.append(className);
            buffer.append('"');
        }
        
        buffer.append(" name=\"");
        buffer.append(modelFormField.getParameterName());
        buffer.append('"');

        buffer.append(" cols=\"");
        buffer.append(textareaField.getCols());
        buffer.append('"');
        
        buffer.append(" rows=\"");
        buffer.append(textareaField.getRows());
        buffer.append('"');
        
        buffer.append('>');
        
        String value = modelFormField.getEntry(context);
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(value);
        }

        buffer.append("<textarea/>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderDateTimeField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.DateTimeField)
     */
    public void renderDateTimeField(StringBuffer buffer, Map context, DateTimeField dateTimeField) {
        ModelFormField modelFormField = dateTimeField.getModelFormField();
        
        buffer.append("<input type=\"text\"");
        
        String className = modelFormField.getWidgetStyle();
        if (UtilValidate.isNotEmpty(className)) {
            buffer.append(" class=\"");
            buffer.append(className);
            buffer.append('"');
        }
        
        // add a style of red if this is a date/time field and redWhen is true
        if (modelFormField.shouldBeRed(context)) {
            buffer.append(" style=\"color: red;\"");
        }
        
        buffer.append(" name=\"");
        buffer.append(modelFormField.getParameterName());
        buffer.append('"');
        
        String value = modelFormField.getEntry(context);
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }
        
        // the default values for a timestamp
        int size = 25;
        int maxlength = 30;
        
        if ("date".equals(dateTimeField.getType())) {
            size = 10;
            maxlength = 12;
        } else if ("time".equals(dateTimeField.getType())) {
            size = 12;
            maxlength = 15;
        }
        
        buffer.append(" size=\"");
        buffer.append(size);
        buffer.append('"');
        
        buffer.append(" maxlength=\"");
        buffer.append(maxlength);
        buffer.append('"');

        buffer.append("/>");
        
        // add calendar pop-up button and seed data IF this is not a "time" type date-time
        if (!"time".equals(dateTimeField.getType())) {
            buffer.append("<a href=\"javascript:call_cal(document.");
            buffer.append(modelFormField.getModelForm().getCurrentFormName(context));
            buffer.append('.');
            buffer.append(modelFormField.getParameterName());
            buffer.append(", '");
            buffer.append(modelFormField.getEntry(context, dateTimeField.getDefaultDateTimeString(context)));
            buffer.append("<img src=\"");
            this.appendContentUrl(buffer, "/images/cal.gif");
            buffer.append("\" width=\"16\" height=\"16\" border=\"0\" alt=\"Calendar\"></a>");
        }
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderDropDownField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.DropDownField)
     */
    public void renderDropDownField(StringBuffer buffer, Map context, DropDownField dropDownField) {
        ModelFormField modelFormField = dropDownField.getModelFormField();
        ModelForm modelForm = modelFormField.getModelForm();
        
        buffer.append("<select");
        
        String className = modelFormField.getWidgetStyle();
        if (UtilValidate.isNotEmpty(className)) {
            buffer.append(" class=\"");
            buffer.append(className);
            buffer.append('"');
        }

        buffer.append(" name=\"");
        buffer.append(modelFormField.getParameterName());
        buffer.append('"');

        buffer.append(" size=\"1\">");
        
        String currentValue = modelFormField.getEntry(context);
        List allOptionValues = dropDownField.getAllOptionValues(context, modelForm.getDelegator());

        // if the current value should go first, stick it in
        if (UtilValidate.isNotEmpty(currentValue) && "first-in-list".equals(dropDownField.getCurrent())) {
            buffer.append("<option");
            buffer.append(" selected");
            buffer.append(" value=\"");
            buffer.append(currentValue);
            buffer.append("\">");
            buffer.append(ModelFormField.FieldInfoWithOptions.getDescriptionForOptionKey(currentValue, allOptionValues));
            buffer.append("</option>");
            
            // add a "separator" option
            buffer.append("<option value=\"");
            buffer.append(currentValue);
            buffer.append("\">---</option>");
        }
        
        // if allow empty is true, add an empty option
        if (dropDownField.isAllowEmpty()) {
            buffer.append("<option value=\"\">&nbsp;</option>");
        }
        
        // list out all options according to the option list
        Iterator optionValueIter = allOptionValues.iterator();
        while (optionValueIter.hasNext()) {
            ModelFormField.OptionValue optionValue = (ModelFormField.OptionValue) optionValueIter.next();
            buffer.append("<option");
            // if current value should be selected in the list, select it
            if (UtilValidate.isNotEmpty(currentValue) && currentValue.equals(optionValue.getKey()) && "selected".equals(dropDownField.getCurrent())) {
                buffer.append(" selected");
            }
            buffer.append(" value=\"");
            buffer.append(optionValue.getKey());
            buffer.append("\">");
            buffer.append(optionValue.getDescription());
            buffer.append("</option>");
        }

        buffer.append("</select>");
    }
    
    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderCheckField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.CheckField)
     */
    public void renderCheckField(StringBuffer buffer, Map context, CheckField checkField) {
        // TODO Auto-generated method stub
        buffer.append("<!-- Sorry, check box fields have not been implemented yet -->");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderRadioField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.RadioField)
     */
    public void renderRadioField(StringBuffer buffer, Map context, RadioField radioField) {
        // TODO Auto-generated method stub
        buffer.append("<!-- Sorry, radio button fields have not been implemented yet -->");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderSubmitField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.SubmitField)
     */
    public void renderSubmitField(StringBuffer buffer, Map context, SubmitField submitField) {
        ModelFormField modelFormField = submitField.getModelFormField();
        ModelForm modelForm = modelFormField.getModelForm();
        
        if ("text-link".equals(submitField.getButtonType())) {
            buffer.append("<a");
        
            String className = modelFormField.getWidgetStyle();
            if (UtilValidate.isNotEmpty(className)) {
                buffer.append(" class=\"");
                buffer.append(className);
                buffer.append('"');
            }
            
            buffer.append(" href=\"javascript:document.");
            buffer.append(modelForm.getCurrentFormName(context));
            buffer.append(".submit()\">");

            buffer.append(modelFormField.getTitle(context));

            buffer.append("</a>");
        } else if ("image".equals(submitField.getButtonType())) {
            buffer.append("<input type=\"image\"");
        
            String className = modelFormField.getWidgetStyle();
            if (UtilValidate.isNotEmpty(className)) {
                buffer.append(" class=\"");
                buffer.append(className);
                buffer.append('"');
            }
        
            buffer.append(" name=\"");
            buffer.append(modelFormField.getParameterName());
            buffer.append('"');
        
            String title = modelFormField.getTitle(context);
            if (UtilValidate.isNotEmpty(title)) {
                buffer.append(" alt=\"");
                buffer.append(title);
                buffer.append('"');
            }
            
            buffer.append(" src=\"");
            this.appendContentUrl(buffer, submitField.getImageLocation());
            buffer.append('"');
        
            buffer.append("/>");
        } else {
            // default to "button"
            
            buffer.append("<input type=\"submit\"");
        
            String className = modelFormField.getWidgetStyle();
            if (UtilValidate.isNotEmpty(className)) {
                buffer.append(" class=\"");
                buffer.append(className);
                buffer.append('"');
            }
        
            buffer.append(" name=\"");
            buffer.append(modelFormField.getParameterName());
            buffer.append('"');
        
            String title = modelFormField.getTitle(context);
            if (UtilValidate.isNotEmpty(title)) {
                buffer.append(" value=\"");
                buffer.append(title);
                buffer.append('"');
            }
        
            buffer.append("/>");
        }
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderResetField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.ResetField)
     */
    public void renderResetField(StringBuffer buffer, Map context, ResetField resetField) {
        ModelFormField modelFormField = resetField.getModelFormField();

        buffer.append("<input type=\"reset\"");
        
        String className = modelFormField.getWidgetStyle();
        if (UtilValidate.isNotEmpty(className)) {
            buffer.append(" class=\"");
            buffer.append(className);
            buffer.append('"');
        }
        
        buffer.append(" name=\"");
        buffer.append(modelFormField.getParameterName());
        buffer.append('"');
        
        String title = modelFormField.getTitle(context);
        if (UtilValidate.isNotEmpty(title)) {
            buffer.append(" value=\"");
            buffer.append(title);
            buffer.append('"');
        }
        
        buffer.append("/>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderHiddenField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.HiddenField)
     */
    public void renderHiddenField(StringBuffer buffer, Map context, HiddenField hiddenField) {
        ModelFormField modelFormField = hiddenField.getModelFormField();
        
        buffer.append("<input type=\"hidden\"");
        
        buffer.append(" name=\"");
        buffer.append(modelFormField.getParameterName());
        buffer.append('"');
        
        String value = modelFormField.getEntry(context);
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }
        
        buffer.append("/>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderIgnoredField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.IgnoredField)
     */
    public void renderIgnoredField(StringBuffer buffer, Map context, IgnoredField ignoredField) {
        // do nothing, it's an ignored field; could add a comment or something if we wanted to
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFieldTitle(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField)
     */
    public void renderFieldTitle(StringBuffer buffer, Map context, ModelFormField modelFormField) {
        buffer.append("<span");
        if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
            buffer.append(" class=\"");
            buffer.append(modelFormField.getTitleStyle());
            buffer.append("\"");
        }
        buffer.append(">");
        buffer.append(modelFormField.getTitle(context));
        buffer.append("</span>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<form method=\"POST\" action=\"");
        this.appendOfbizUrl(buffer, "/" + modelForm.getTarget(context));
        buffer.append("\" name=\"");
        buffer.append(modelForm.getCurrentFormName(context));
        buffer.append("\">");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</form>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatWrapperOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormatWrapperOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        if ("single".equals(modelForm.getType())) {
            buffer.append("<table border=\"0\" cellpadding=\"2\" cellspacing=\"0\">");
        } else {
            buffer.append("<table border=\"1\" cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">");
        }
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatWrapperClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormatWrapperClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</table>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatHeaderRowOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormatHeaderRowOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<tr>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatHeaderRowClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormatHeaderRowClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</tr>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatHeaderRowCellOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm, org.ofbiz.core.widget.form.ModelFormField)
     */
    public void renderFormatHeaderRowCellOpen(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField) {
        buffer.append("<td>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatHeaderRowCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm, org.ofbiz.core.widget.form.ModelFormField)
     */
    public void renderFormatHeaderRowCellClose(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField) {
        buffer.append("</td>");
    }

    public void renderFormatHeaderRowFormCellOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<td align=\"center\">");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatHeaderRowFormCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormatHeaderRowFormCellClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</td>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatHeaderRowFormCellTitleSeparator(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm, boolean)
     */
    public void renderFormatHeaderRowFormCellTitleSeparator(StringBuffer buffer, Map context, ModelForm modelForm, boolean isLast) {
        if (isLast) {
            buffer.append(", &amp; ");
        } else {
            buffer.append(", ");
        }
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatItemRowOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormatItemRowOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<tr>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatItemRowClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormatItemRowClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</tr>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatItemRowCellOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm, org.ofbiz.core.widget.form.ModelFormField)
     */
    public void renderFormatItemRowCellOpen(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField) {
        buffer.append("<td>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatItemRowCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm, org.ofbiz.core.widget.form.ModelFormField)
     */
    public void renderFormatItemRowCellClose(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField) {
        buffer.append("</td>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatItemRowFormCellOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormatItemRowFormCellOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<td align=\"center\">");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatItemRowFormCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm)
     */
    public void renderFormatItemRowFormCellClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</td>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatFieldRowTitleCellOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField)
     */
    public void renderFormatFieldRowTitleCellOpen(StringBuffer buffer, Map context, ModelFormField modelFormField) {
        buffer.append("<td width=\"20%\">");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatFieldRowTitleCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField)
     */
    public void renderFormatFieldRowTitleCellClose(StringBuffer buffer, Map context, ModelFormField modelFormField) {
        buffer.append("</td>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatFieldRowSpacerCell(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField)
     */
    public void renderFormatFieldRowSpacerCell(StringBuffer buffer, Map context, ModelFormField modelFormField) {
        buffer.append("<td>&nbsp;</td>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatFieldRowWidgetCellOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField, int)
     */
    public void renderFormatFieldRowWidgetCellOpen(StringBuffer buffer, Map context, ModelFormField modelFormField, int positions) {
        if (positions == 1) {
            buffer.append("<td width=\"80%\">");
        } else {
            buffer.append("<td width=\"30%\">");
        }
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormatFieldRowWidgetCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField, int)
     */
    public void renderFormatFieldRowWidgetCellClose(StringBuffer buffer, Map context, ModelFormField modelFormField, int positions) {
        buffer.append("</td>");
    }
}
