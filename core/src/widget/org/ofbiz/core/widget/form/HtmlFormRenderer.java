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

import java.util.Map;

import org.ofbiz.core.entity.GenericDelegator;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.util.UtilValidate;
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
        buffer.append(">");
        buffer.append(displayField.getDescription(context));
        buffer.append("</span>");
    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderHyperlinkField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.HyperlinkField)
     */
    public void renderHyperlinkField(StringBuffer buffer, Map context, HyperlinkField hyperlinkField) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderTextField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.TextField)
     */
    public void renderTextField(StringBuffer buffer, Map context, TextField textField) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderTextareaField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.TextareaField)
     */
    public void renderTextareaField(StringBuffer buffer, Map context, TextareaField textareaField) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderDateTimeField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.DateTimeField)
     */
    public void renderDateTimeField(StringBuffer buffer, Map context, DateTimeField dateTimeField) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderDropDownField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.DropDownField)
     */
    public void renderDropDownField(StringBuffer buffer, Map context, DropDownField dropDownField) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderCheckField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.CheckField)
     */
    public void renderCheckField(StringBuffer buffer, Map context, CheckField checkField) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderRadioField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.RadioField)
     */
    public void renderRadioField(StringBuffer buffer, Map context, RadioField radioField) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderSubmitField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.SubmitField)
     */
    public void renderSubmitField(StringBuffer buffer, Map context, SubmitField submitField) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderResetField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.ResetField)
     */
    public void renderResetField(StringBuffer buffer, Map context, ResetField resetField) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderHiddenField(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelFormField.HiddenField)
     */
    public void renderHiddenField(StringBuffer buffer, Map context, HiddenField hiddenField) {
        // TODO Auto-generated method stub

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
     * @see org.ofbiz.core.widget.form.FormStringRenderer#renderFormOpen(java.lang.StringBuffer, java.util.Map, org.ofbiz.core.widget.form.ModelForm, java.lang.Integer)
     */
    public void renderFormOpen(StringBuffer buffer, Map context, ModelForm modelForm, Integer itemNumber) {
        buffer.append("<form method=\"POST\" action=\"");
        // TODO: call ofbiz URL for target
        buffer.append("/" + modelForm.getTarget(context));
        buffer.append("\" name=\"");
        buffer.append(modelForm.getName());
        if (itemNumber != null) {
            buffer.append(itemNumber.intValue());
        }
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
