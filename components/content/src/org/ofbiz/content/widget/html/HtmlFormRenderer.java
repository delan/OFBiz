/*
 * $Id: HtmlFormRenderer.java,v 1.11 2004/06/02 17:50:11 byersa Exp $
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
package org.ofbiz.content.widget.html;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.webapp.control.RequestHandler;
import org.ofbiz.content.webapp.taglib.ContentUrlTag;
import org.ofbiz.content.widget.WidgetWorker;
import org.ofbiz.content.widget.form.FormStringRenderer;
import org.ofbiz.content.widget.form.ModelForm;
import org.ofbiz.content.widget.form.ModelFormField;
import org.ofbiz.content.widget.form.ModelFormField.CheckField;
import org.ofbiz.content.widget.form.ModelFormField.DateFindField;
import org.ofbiz.content.widget.form.ModelFormField.DateTimeField;
import org.ofbiz.content.widget.form.ModelFormField.DisplayField;
import org.ofbiz.content.widget.form.ModelFormField.DropDownField;
import org.ofbiz.content.widget.form.ModelFormField.FileField;
import org.ofbiz.content.widget.form.ModelFormField.HiddenField;
import org.ofbiz.content.widget.form.ModelFormField.HyperlinkField;
import org.ofbiz.content.widget.form.ModelFormField.IgnoredField;
import org.ofbiz.content.widget.form.ModelFormField.ImageField;
import org.ofbiz.content.widget.form.ModelFormField.LookupField;
import org.ofbiz.content.widget.form.ModelFormField.PasswordField;
import org.ofbiz.content.widget.form.ModelFormField.RadioField;
import org.ofbiz.content.widget.form.ModelFormField.RangeFindField;
import org.ofbiz.content.widget.form.ModelFormField.ResetField;
import org.ofbiz.content.widget.form.ModelFormField.SubmitField;
import org.ofbiz.content.widget.form.ModelFormField.TextField;
import org.ofbiz.content.widget.form.ModelFormField.TextFindField;
import org.ofbiz.content.widget.form.ModelFormField.TextareaField;

/**
 * Widget Library - HTML Form Renderer implementation
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.11 $
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

    public void appendWhitespace(StringBuffer buffer) {
        // appending line ends for now, but this could be replaced with a simple space or something
        buffer.append("\r\n");
        //buffer.append(' ');
    }

    public void appendOfbizUrl(StringBuffer buffer, String location) {
        ServletContext ctx = (ServletContext) this.request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");
        // make and append the link
        buffer.append(rh.makeLink(this.request, this.response, location));
    }

    public void appendContentUrl(StringBuffer buffer, String location) {
        ContentUrlTag.appendContentPrefix(this.request, buffer);
        buffer.append(location);
    }

    public void appendTooltip(StringBuffer buffer, Map context, ModelFormField modelFormField) {
        // render the tooltip, in other methods too
        String tooltip = modelFormField.getTooltip(context);
        if (UtilValidate.isNotEmpty(tooltip)) {
            buffer.append("<span");
            String tooltipStyle = modelFormField.getTooltipStyle();
            if (UtilValidate.isNotEmpty(tooltipStyle)) {
                buffer.append(" class=\"");
                buffer.append(tooltipStyle);
                buffer.append("\"");
            }
            buffer.append("> -[");
            buffer.append(tooltip);
            buffer.append("]- </span>");
        }
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderDisplayField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.DisplayField)
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

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderHyperlinkField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.HyperlinkField)
     */
    public void renderHyperlinkField(StringBuffer buffer, Map context, HyperlinkField hyperlinkField) {
        ModelFormField modelFormField = hyperlinkField.getModelFormField();
        this.makeHyperlinkString(
            buffer,
            modelFormField.getWidgetStyle(),
            hyperlinkField.getTargetType(),
            hyperlinkField.getTarget(context),
            hyperlinkField.getDescription(context));
        this.appendTooltip(buffer, context, modelFormField);
        this.appendWhitespace(buffer);
    }

    public void makeHyperlinkString(StringBuffer buffer, ModelFormField.SubHyperlink subHyperlink, Map context) {
        if (subHyperlink == null) {
            return;
        }
        if (subHyperlink.shouldUse(context)) {
            buffer.append(' ');
            this.makeHyperlinkString(
                buffer,
                subHyperlink.getLinkStyle(),
                subHyperlink.getTargetType(),
                subHyperlink.getTarget(context),
                subHyperlink.getDescription(context));
        }
    }

    public void makeHyperlinkString(StringBuffer buffer, String linkStyle, String targetType, String target, String description) {

        Map context = null;
        List paramList = null;
        WidgetWorker.makeHyperlinkString(buffer, linkStyle, targetType, target, description, this.request, this.response, context, paramList);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderTextField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.TextField)
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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append('"');

        String value = modelFormField.getEntry(context, textField.getDefaultValue(context));
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

        String idName = modelFormField.getIdName();
        if (UtilValidate.isNotEmpty(idName)) {
            buffer.append(" id=\"");
            buffer.append(idName);
            buffer.append('"');
        }

        buffer.append("/>");

        this.makeHyperlinkString(buffer, textField.getSubHyperlink(), context);

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderTextareaField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.TextareaField)
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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append('"');

        buffer.append(" cols=\"");
        buffer.append(textareaField.getCols());
        buffer.append('"');

        buffer.append(" rows=\"");
        buffer.append(textareaField.getRows());
        buffer.append('"');

        String idName = modelFormField.getIdName();
        if (UtilValidate.isNotEmpty(idName)) {
            buffer.append(" id=\"");
            buffer.append(idName);
            buffer.append('"');
        }


        buffer.append('>');

        String value = modelFormField.getEntry(context, textareaField.getDefaultValue(context));
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(value);
        }

        buffer.append("</textarea>");

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderDateTimeField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.DateTimeField)
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
        buffer.append(modelFormField.getParameterName(context));
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

        String idName = modelFormField.getIdName();
        if (UtilValidate.isNotEmpty(idName)) {
            buffer.append(" id=\"");
            buffer.append(idName);
            buffer.append('"');
        }


        buffer.append("/>");

        // add calendar pop-up button and seed data IF this is not a "time" type date-time
        if (!"time".equals(dateTimeField.getType())) {
            buffer.append("<a href=\"javascript:call_cal(document.");
            buffer.append(modelFormField.getModelForm().getCurrentFormName(context));
            buffer.append('.');
            buffer.append(modelFormField.getParameterName(context));
            buffer.append(", '");
            buffer.append(modelFormField.getEntry(context, dateTimeField.getDefaultDateTimeString(context)));
            buffer.append("');\">");
            buffer.append("<img src=\"");
            this.appendContentUrl(buffer, "/content/images/cal.gif");
            buffer.append("\" width=\"16\" height=\"16\" border=\"0\" alt=\"Calendar\"></a>");
        }

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderDropDownField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.DropDownField)
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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append('"');

        String idName = modelFormField.getIdName();
        if (UtilValidate.isNotEmpty(idName)) {
            buffer.append(" id=\"");
            buffer.append(idName);
            buffer.append('"');
        }


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
            String explicitDescription = dropDownField.getCurrentDescription(context);
            if (UtilValidate.isNotEmpty(explicitDescription)) {
                buffer.append(explicitDescription);
            } else {
                buffer.append(ModelFormField.FieldInfoWithOptions.getDescriptionForOptionKey(currentValue, allOptionValues));
            }
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
            } else if (
                UtilValidate.isEmpty(currentValue)
                    && dropDownField.getNoCurrentSelectedKey() != null
                    && dropDownField.getNoCurrentSelectedKey().equals(optionValue.getKey())) {
                buffer.append(" selected");
            }
            buffer.append(" value=\"");
            buffer.append(optionValue.getKey());
            buffer.append("\">");
            buffer.append(optionValue.getDescription());
            buffer.append("</option>");
        }

        buffer.append("</select>");

        this.makeHyperlinkString(buffer, dropDownField.getSubHyperlink(), context);

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderCheckField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.CheckField)
     */
    public void renderCheckField(StringBuffer buffer, Map context, CheckField checkField) {
        // well, I don't know if this will be very useful... but here it is

        ModelFormField modelFormField = checkField.getModelFormField();
        // never used: ModelForm modelForm = modelFormField.getModelForm();
        String currentValue = modelFormField.getEntry(context);

        buffer.append("<span");
        String className = modelFormField.getWidgetStyle();
        if (UtilValidate.isNotEmpty(className)) {
            buffer.append(" class=\"");
            buffer.append(className);
            buffer.append('"');
        }
        buffer.append(">");

        buffer.append("<input type=\"");
        buffer.append("checkbox");
        buffer.append('"');

        // if current value should be selected in the list, select it
        if ("Y".equals(currentValue) || "T".equals(currentValue)) {
            buffer.append(" checked");
        }
        buffer.append(" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append('"');
        buffer.append(" value=\"Y\"/>");
        // any description by it?
        buffer.append("</span>");

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderRadioField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.RadioField)
     */
    public void renderRadioField(StringBuffer buffer, Map context, RadioField radioField) {
        ModelFormField modelFormField = radioField.getModelFormField();
        ModelForm modelForm = modelFormField.getModelForm();
        List allOptionValues = radioField.getAllOptionValues(context, modelForm.getDelegator());
        String currentValue = modelFormField.getEntry(context);

        // list out all options according to the option list
        Iterator optionValueIter = allOptionValues.iterator();
        while (optionValueIter.hasNext()) {
            ModelFormField.OptionValue optionValue = (ModelFormField.OptionValue) optionValueIter.next();
            String className = modelFormField.getWidgetStyle();
            buffer.append("<div");
            if (UtilValidate.isNotEmpty(className)) {
                buffer.append(" class=\"");
                buffer.append(className);
                buffer.append('"');
            }
            buffer.append(">");

            buffer.append("<input type=\"");
            buffer.append("radio");
            buffer.append('"');

            // if current value should be selected in the list, select it
            if (UtilValidate.isNotEmpty(currentValue) && currentValue.equals(optionValue.getKey())) {
                buffer.append(" checked");
            }
            buffer.append(" name=\"");
            buffer.append(modelFormField.getParameterName(context));
            buffer.append('"');
            buffer.append(" value=\"");
            buffer.append(optionValue.getKey());
            buffer.append("\"/>");

            buffer.append(optionValue.getDescription());
            buffer.append("</div>");
        }

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderSubmitField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.SubmitField)
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
            buffer.append(modelFormField.getParameterName(context));
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
            buffer.append(modelFormField.getParameterName(context));
            buffer.append('"');

            String title = modelFormField.getTitle(context);
            if (UtilValidate.isNotEmpty(title)) {
                buffer.append(" value=\"");
                buffer.append(title);
                buffer.append('"');
            }

            buffer.append("/>");
        }

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderResetField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.ResetField)
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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append('"');

        String title = modelFormField.getTitle(context);
        if (UtilValidate.isNotEmpty(title)) {
            buffer.append(" value=\"");
            buffer.append(title);
            buffer.append('"');
        }

        buffer.append("/>");

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderHiddenField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.HiddenField)
     */
    public void renderHiddenField(StringBuffer buffer, Map context, HiddenField hiddenField) {
        ModelFormField modelFormField = hiddenField.getModelFormField();
        String value = hiddenField.getValue(context);
        this.renderHiddenField(buffer, context, modelFormField, value);
    }

    public void renderHiddenField(StringBuffer buffer, Map context, ModelFormField modelFormField, String value) {
        buffer.append("<input type=\"hidden\"");

        buffer.append(" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append('"');

        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }

        buffer.append("/>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderIgnoredField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.IgnoredField)
     */
    public void renderIgnoredField(StringBuffer buffer, Map context, IgnoredField ignoredField) {
        // do nothing, it's an ignored field; could add a comment or something if we wanted to
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFieldTitle(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField)
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

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<form method=\"POST\" ");
        String targ = modelForm.getTarget(context);
        if (targ != null && targ.length() > 0) {
            buffer.append(" action=\"");
            this.appendOfbizUrl(buffer, "/" + targ);
            buffer.append("\" ");
        }

        String formType = modelForm.getType();
        if (formType.equals("upload") ) {
            buffer.append(" enctype=\"multipart/form-data\"");
        }

        buffer.append(" name=\"");
        buffer.append(modelForm.getCurrentFormName(context));
        buffer.append("\" style=\"margin: 0;\">");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</form>");

        this.appendWhitespace(buffer);
    }

    public void renderFormatListWrapperOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<table border=\"1\" cellpadding=\"2\" cellspacing=\"0\" width=\"100%\">");

        this.appendWhitespace(buffer);
    }

    public void renderFormatListWrapperClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</table>");

        this.appendWhitespace(buffer);

        this.renderNextPrev(buffer, context, modelForm);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatHeaderRowOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormatHeaderRowOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<tr>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatHeaderRowClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormatHeaderRowClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</tr>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatHeaderRowCellOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm, org.ofbiz.content.widget.form.ModelFormField)
     */
    public void renderFormatHeaderRowCellOpen(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField) {
        buffer.append("<td>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatHeaderRowCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm, org.ofbiz.content.widget.form.ModelFormField)
     */
    public void renderFormatHeaderRowCellClose(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField) {
        buffer.append("</td>");

        this.appendWhitespace(buffer);
    }

    public void renderFormatHeaderRowFormCellOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<td align=\"center\">");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatHeaderRowFormCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormatHeaderRowFormCellClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</td>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatHeaderRowFormCellTitleSeparator(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm, boolean)
     */
    public void renderFormatHeaderRowFormCellTitleSeparator(
        StringBuffer buffer,
        Map context,
        ModelForm modelForm,
        ModelFormField modelFormField,
        boolean isLast) {
        buffer.append("<span");
        if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
            buffer.append(" class=\"");
            buffer.append(modelFormField.getTitleStyle());
            buffer.append("\"");
        }
        buffer.append(">");
        if (isLast) {
            buffer.append(" - ");
        } else {
            buffer.append(" - ");
        }
        buffer.append("</span>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatItemRowOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormatItemRowOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<tr>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatItemRowClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormatItemRowClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</tr>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatItemRowCellOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm, org.ofbiz.content.widget.form.ModelFormField)
     */
    public void renderFormatItemRowCellOpen(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField) {
        buffer.append("<td>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatItemRowCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm, org.ofbiz.content.widget.form.ModelFormField)
     */
    public void renderFormatItemRowCellClose(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField) {
        buffer.append("</td>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatItemRowFormCellOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormatItemRowFormCellOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<td align=\"center\">");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatItemRowFormCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormatItemRowFormCellClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</td>");

        this.appendWhitespace(buffer);
    }

    public void renderFormatSingleWrapperOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<table border=\"0\" cellpadding=\"2\" cellspacing=\"0\">");

        this.appendWhitespace(buffer);
    }

    public void renderFormatSingleWrapperClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</table>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatFieldRowOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormatFieldRowOpen(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("<tr>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatFieldRowClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelForm)
     */
    public void renderFormatFieldRowClose(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("</tr>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatFieldRowTitleCellOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField)
     */
    public void renderFormatFieldRowTitleCellOpen(StringBuffer buffer, Map context, ModelFormField modelFormField) {
        buffer.append("<td width=\"20%\" align=\"right\">");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatFieldRowTitleCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField)
     */
    public void renderFormatFieldRowTitleCellClose(StringBuffer buffer, Map context, ModelFormField modelFormField) {
        buffer.append("</td>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatFieldRowSpacerCell(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField)
     */
    public void renderFormatFieldRowSpacerCell(StringBuffer buffer, Map context, ModelFormField modelFormField) {
        buffer.append("<td>&nbsp;</td>");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatFieldRowWidgetCellOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField, int)
     */
    public void renderFormatFieldRowWidgetCellOpen(
        StringBuffer buffer,
        Map context,
        ModelFormField modelFormField,
        int positions,
        int positionSpan,
        Integer nextPositionInRow) {
        buffer.append("<td width=\"");
        if (nextPositionInRow != null || modelFormField.getPosition() > 1) {
            buffer.append("30");
        } else {
            buffer.append("80");
        }
        buffer.append("%\" align=\"left\"");
        if (positionSpan > 0) {
            buffer.append(" colspan=\"");
            // do a span of 1 for this column, plus 3 columns for each spanned 
            //position or each blank position that this will be filling in 
            buffer.append(1 + (positionSpan * 3));
            buffer.append("\"");
        }
        buffer.append(">");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFormatFieldRowWidgetCellClose(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField, int)
     */
    public void renderFormatFieldRowWidgetCellClose(
        StringBuffer buffer,
        Map context,
        ModelFormField modelFormField,
        int positions,
        int positionSpan,
        Integer nextPositionInRow) {
        buffer.append("</td>");

        this.appendWhitespace(buffer);
    }

    public void renderFormatEmptySpace(StringBuffer buffer, Map context, ModelForm modelForm) {
        buffer.append("&nbsp;");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderTextFindField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.TextFindField)
     */
    public void renderTextFindField(StringBuffer buffer, Map context, TextFindField textFindField) {

        ModelFormField modelFormField = textFindField.getModelFormField();
        Locale locale = (Locale)context.get("locale");
        String opEquals = UtilProperties.getMessage("conditional", "equals", locale);
        String opBeginsWith = UtilProperties.getMessage("conditional", "begins_with", locale);
        String opContains = UtilProperties.getMessage("conditional", "contains", locale);
        String opIsEmpty = UtilProperties.getMessage("conditional", "is_empty", locale);

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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append('"');

        String value = modelFormField.getEntry(context);
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }

        buffer.append(" size=\"");
        buffer.append(textFindField.getSize());
        buffer.append('"');

        Integer maxlength = textFindField.getMaxlength();
        if (maxlength != null) {
            buffer.append(" maxlength=\"");
            buffer.append(maxlength.intValue());
            buffer.append('"');
        }

        buffer.append("/>");

        buffer.append(" <span");
        if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
            buffer.append(" class=\"");
            buffer.append(modelFormField.getTitleStyle());
            buffer.append('"');
        }
        buffer.append('>');
        buffer.append(" " + opEquals + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_op\" value=\"equals\" checked/>");

        buffer.append(" " + opBeginsWith + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_op\" value=\"like\"/>");

        buffer.append(" " + opContains + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_op\" value=\"contains\"/>");

        buffer.append(" " + opIsEmpty + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_op\" value=\"empty\"/>");
        buffer.append("</span>");

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderRangeFindField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.RangeFindField)
     */
    public void renderRangeFindField(StringBuffer buffer, Map context, RangeFindField rangeFindField) {

        ModelFormField modelFormField = rangeFindField.getModelFormField();
        Locale locale = (Locale)context.get("locale");
        String opEquals = UtilProperties.getMessage("conditional", "equals", locale);
        String opGreaterThan = UtilProperties.getMessage("conditional", "greater_than", locale);
        String opGreaterThanEquals = UtilProperties.getMessage("conditional", "greater_than_equals", locale);
        String opLessThan = UtilProperties.getMessage("conditional", "less_than", locale);
        String opLessThanEquals = UtilProperties.getMessage("conditional", "less_than_equals", locale);
        String opIsEmpty = UtilProperties.getMessage("conditional", "is_empty", locale);

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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld0_value\"");

        String value = modelFormField.getEntry(context);
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }

        buffer.append(" size=\"");
        buffer.append(rangeFindField.getSize());
        buffer.append('"');

        Integer maxlength = rangeFindField.getMaxlength();
        if (maxlength != null) {
            buffer.append(" maxlength=\"");
            buffer.append(maxlength.intValue());
            buffer.append('"');
        }

        buffer.append("/>");

        buffer.append(" <span");
        if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
            buffer.append(" class=\"");
            buffer.append(modelFormField.getTitleStyle());
            buffer.append('"');
        }
        buffer.append('>');

        buffer.append(" " + opEquals + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld0_op\" value=\"equals\" checked/>");

        buffer.append(" " + opGreaterThan + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld0_op\" value=\"greaterThan\"/>");

        buffer.append(" " + opGreaterThanEquals + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld0_op\" value=\"greaterThanEqualTo\"/>");

        buffer.append("</span>");

        buffer.append(" <br/> ");

        buffer.append("<input type=\"text\"");

        className = modelFormField.getWidgetStyle();
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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld1_value\"");

        value = modelFormField.getEntry(context);
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }

        buffer.append(" size=\"");
        buffer.append(rangeFindField.getSize());
        buffer.append('"');

        if (maxlength != null) {
            buffer.append(" maxlength=\"");
            buffer.append(maxlength.intValue());
            buffer.append('"');
        }

        buffer.append("/>");

        buffer.append(" <span");
        if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
            buffer.append(" class=\"");
            buffer.append(modelFormField.getTitleStyle());
            buffer.append('"');
        }
        buffer.append('>');

        buffer.append(" " + opLessThan+ " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld1_op\" value=\"lessThan\"/>");

        buffer.append(" " + opLessThanEquals + " <input type=\"radio\" name=\"");
        buffer.append(" Less than equals<input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld1_op\" value=\"lessThanEqualTo\"/>");

        buffer.append(" " + opIsEmpty+ " <input type=\"radio\" name=\"");
        buffer.append(" Is Empty<input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_op\" value=\"empty\"/>");

        buffer.append("</span>");

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderDateFindField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.DateFindField)
     */
    public void renderDateFindField(StringBuffer buffer, Map context, DateFindField dateFindField) {
        ModelFormField modelFormField = dateFindField.getModelFormField();

        Locale locale = (Locale)context.get("locale");
        String opEquals = UtilProperties.getMessage("conditional", "equals", locale);
        String opGreaterThan = UtilProperties.getMessage("conditional", "greater_than", locale);
        String opSameDay = UtilProperties.getMessage("conditional", "same_day", locale);
        String opGreaterThanFromDayStart = UtilProperties.getMessage("conditional", 
                                                "greater_than_from_day_start", locale);
        String opLessThan = UtilProperties.getMessage("conditional", "less_than", locale);
        String opUpToDay = UtilProperties.getMessage("conditional", "up_to_day", locale);
        String opUpThruDay = UtilProperties.getMessage("conditional", "up_thru_day", locale);
        String opIsEmpty = UtilProperties.getMessage("conditional", "is_empty", locale);

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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld0_value\"");

        String value = modelFormField.getEntry(context);
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }

        // the default values for a timestamp
        int size = 25;
        int maxlength = 30;

        buffer.append(" size=\"");
        buffer.append(size);
        buffer.append('"');

        buffer.append(" maxlength=\"");
        buffer.append(maxlength);
        buffer.append('"');

        buffer.append("/>");

        // add calendar pop-up button and seed data 
        buffer.append("<a href=\"javascript:call_cal(document.");
        buffer.append(modelFormField.getModelForm().getCurrentFormName(context));
        buffer.append('.');
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld0_value, '");
        buffer.append(modelFormField.getEntry(context, dateFindField.getDefaultDateTimeString(context)));
        buffer.append("');\">");
        buffer.append("<img src=\"");
        this.appendContentUrl(buffer, "/content/images/cal.gif");
        buffer.append("\" width=\"16\" height=\"16\" border=\"0\" alt=\"Calendar\"></a>");

        buffer.append(" <span");
        if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
            buffer.append(" class=\"");
            buffer.append(modelFormField.getTitleStyle());
            buffer.append('"');
        }
        buffer.append('>');

        buffer.append(" " + opEquals + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld0_op\" value=\"equals\" checked/>");

        buffer.append(" " + opSameDay +  " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld0_op\" value=\"sameDay\" checked/>");

        buffer.append(" " + opGreaterThanFromDayStart + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld0_op\" value=\"greaterThanFromDayStart\"/>");

        buffer.append(" " + opGreaterThan + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld0_op\" value=\"greaterThan\"/>");

        buffer.append(" <span");

        buffer.append(" <br/> ");

        buffer.append("<input type=\"text\"");
        className = modelFormField.getWidgetStyle();
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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld1_value\"");

        value = modelFormField.getEntry(context);
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }

        buffer.append(" size=\"");
        buffer.append(size);
        buffer.append('"');

        buffer.append(" maxlength=\"");
        buffer.append(maxlength);
        buffer.append('"');

        buffer.append("/>");

        // add calendar pop-up button and seed data 
        buffer.append("<a href=\"javascript:call_cal(document.");
        buffer.append(modelFormField.getModelForm().getCurrentFormName(context));
        buffer.append('.');
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld1_value, '");
        buffer.append(modelFormField.getEntry(context, dateFindField.getDefaultDateTimeString(context)));
        buffer.append("');\">");
        buffer.append("<img src=\"");
        this.appendContentUrl(buffer, "/content/images/cal.gif");
        buffer.append("\" width=\"16\" height=\"16\" border=\"0\" alt=\"Calendar\"></a>");

        buffer.append(" <span");
        if (UtilValidate.isNotEmpty(modelFormField.getTitleStyle())) {
            buffer.append(" class=\"");
            buffer.append(modelFormField.getTitleStyle());
            buffer.append('"');
        }
        buffer.append('>');

        buffer.append(" " + opLessThan + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld1_op\" value=\"lessThan\"/>");

        buffer.append(" " + opUpToDay + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld1_op\" value=\"upToDay\"/>");

        buffer.append(" " + opUpThruDay + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_fld1_op\" value=\"upThruDay\"/>");

        buffer.append(" " + opIsEmpty + " <input type=\"radio\" name=\"");
        buffer.append(modelFormField.getParameterName(context));
        buffer.append("_op\" value=\"empty\"/>");

        buffer.append("</span>");

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderLookupField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.LookupField)
     */
    public void renderLookupField(StringBuffer buffer, Map context, LookupField lookupField) {
        ModelFormField modelFormField = lookupField.getModelFormField();

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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append('"');

        String value = modelFormField.getEntry(context);
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }

        buffer.append(" size=\"");
        buffer.append(lookupField.getSize());
        buffer.append('"');

        Integer maxlength = lookupField.getMaxlength();
        if (maxlength != null) {
            buffer.append(" maxlength=\"");
            buffer.append(maxlength.intValue());
            buffer.append('"');
        }

        buffer.append("/>");

        // add lookup pop-up button 
        buffer.append("<a href=\"javascript:call_fieldlookup2(document.");
        buffer.append(modelFormField.getModelForm().getCurrentFormName(context));
        buffer.append('.');
        buffer.append(modelFormField.getParameterName(context));
        buffer.append(", '");
        buffer.append(lookupField.getFormName());
        buffer.append("');\">");
        buffer.append("<img src=\"");
        this.appendContentUrl(buffer, "/content/images/fieldlookup.gif");
        buffer.append("\" width=\"16\" height=\"16\" border=\"0\" alt=\"Lookup\"></a>");

        this.makeHyperlinkString(buffer, lookupField.getSubHyperlink(), context);
        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    public void renderNextPrev(StringBuffer buffer, Map context, ModelForm modelForm) {
        String targetService = modelForm.getPaginateTarget();
        if (targetService == null) {
            targetService = "${targetService}";
        }

        int viewIndex = -1;
        try {
            viewIndex = ((Integer) context.get("viewIndex")).intValue();
        } catch (Exception e) {
            viewIndex = 0;
        }

        int viewSize = -1;
        try {
            viewSize = ((Integer) context.get("viewSize")).intValue();
        } catch (Exception e) {
            viewSize = 0;
        }

        int listSize = -1;
        try {
            listSize = ((Integer) context.get("listSize")).intValue();
        } catch (Exception e) {
            listSize = 0;
        }

        int highIndex = -1;
        try {
            highIndex = ((Integer) context.get("highIndex")).intValue();
        } catch (Exception e) {
            highIndex = 0;
        }

        int lowIndex = -1;
        try {
            lowIndex = ((Integer) context.get("lowIndex")).intValue();
        } catch (Exception e) {
            lowIndex = 0;
        }

        String queryString = (String) context.get("queryString");
        ServletContext ctx = (ServletContext) request.getAttribute("servletContext");
        RequestHandler rh = (RequestHandler) ctx.getAttribute("_REQUEST_HANDLER_");

        buffer.append("<table border=\"0\" width=\"100%\" cellpadding=\"2\">\n");
        buffer.append("  <tr>\n");
        buffer.append("    <td align=right>\n");
        buffer.append("      <b>\n");
        if (viewIndex > 0) {
            buffer.append(" <a href=\"");
            String linkText = targetService + "?";
            if (queryString != null && !queryString.equals("null"))
                linkText += queryString + "&";
            linkText += "VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex - 1) + "\"";

            // make the link
            buffer.append(rh.makeLink(request, response, linkText, false, false, false));
            buffer.append(" class=\"buttontext\">[Previous]</a>\n");

        }
        if (listSize > 0) {
            buffer.append("          <span class=\"tabletext\">" + lowIndex + " - " + highIndex + " of " + listSize + "</span> \n");
        }
        if (highIndex < listSize) {
            buffer.append(" <a href=\"");
            String linkText = "" + targetService + "?" + queryString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex + 1) + "\"";

            // make the link
            buffer.append(rh.makeLink(request, response, linkText, false, false, false));
            buffer.append(" class=\"buttontext\">[Next]</a>\n");

        }
        buffer.append("      </b>\n");
        buffer.append("    </td>\n");
        buffer.append("  </tr>\n");
        buffer.append("</table>\n");

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderFileField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.FileField)
     */
    public void renderFileField(StringBuffer buffer, Map context, FileField textField) {
        ModelFormField modelFormField = textField.getModelFormField();

        buffer.append("<input type=\"file\"");

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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append('"');

        String value = modelFormField.getEntry(context, textField.getDefaultValue(context));
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

        this.makeHyperlinkString(buffer, textField.getSubHyperlink(), context);

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderPasswordField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.PasswordField)
     */
    public void renderPasswordField(StringBuffer buffer, Map context, PasswordField passwordField) {
        ModelFormField modelFormField = passwordField.getModelFormField();

        buffer.append("<input type=\"password\"");

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
        buffer.append(modelFormField.getParameterName(context));
        buffer.append('"');

        String value = modelFormField.getEntry(context, passwordField.getDefaultValue(context));
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" value=\"");
            buffer.append(value);
            buffer.append('"');
        }

        buffer.append(" size=\"");
        buffer.append(passwordField.getSize());
        buffer.append('"');

        Integer maxlength = passwordField.getMaxlength();
        if (maxlength != null) {
            buffer.append(" maxlength=\"");
            buffer.append(maxlength.intValue());
            buffer.append('"');
        }

        String idName = modelFormField.getIdName();
        if (UtilValidate.isNotEmpty(idName)) {
            buffer.append(" id=\"");
            buffer.append(idName);
            buffer.append('"');
        }

        buffer.append("/>");

        this.makeHyperlinkString(buffer, passwordField.getSubHyperlink(), context);

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

    /* (non-Javadoc)
     * @see org.ofbiz.content.widget.form.FormStringRenderer#renderImageField(java.lang.StringBuffer, java.util.Map, org.ofbiz.content.widget.form.ModelFormField.ImageField)
     */
    public void renderImageField(StringBuffer buffer, Map context, ImageField imageField) {
        ModelFormField modelFormField = imageField.getModelFormField();

        buffer.append("<img ");


        String value = modelFormField.getEntry(context, imageField.getValue(context));
        if (UtilValidate.isNotEmpty(value)) {
            buffer.append(" src=\"");
            ContentUrlTag.appendContentPrefix(request, buffer);
            buffer.append(value);
            buffer.append('"');
        }

        buffer.append(" border=\"");
        buffer.append(imageField.getBorder());
        buffer.append('"');

        Integer width = imageField.getWidth();
        if (width != null) {
            buffer.append(" width=\"");
            buffer.append(width.intValue());
            buffer.append('"');
        }

        Integer height = imageField.getHeight();
        if (height != null) {
            buffer.append(" height=\"");
            buffer.append(height.intValue());
            buffer.append('"');
        }

        buffer.append("/>");

        this.makeHyperlinkString(buffer, imageField.getSubHyperlink(), context);

        this.appendTooltip(buffer, context, modelFormField);

        this.appendWhitespace(buffer);
    }

}
