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

/**
 * Widget Library - Form String Renderer interface
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.2
 */
public interface FormStringRenderer {
    public void renderDisplayField(StringBuffer buffer, Map context, ModelFormField.DisplayField displayField);
    public void renderHyperlinkField(StringBuffer buffer, Map context, ModelFormField.HyperlinkField hyperlinkField);

    public void renderTextField(StringBuffer buffer, Map context, ModelFormField.TextField textField);
    public void renderTextareaField(StringBuffer buffer, Map context, ModelFormField.TextareaField textareaField);
    public void renderDateTimeField(StringBuffer buffer, Map context, ModelFormField.DateTimeField dateTimeField);

    public void renderDropDownField(StringBuffer buffer, Map context, ModelFormField.DropDownField dropDownField);
    public void renderCheckField(StringBuffer buffer, Map context, ModelFormField.CheckField checkField);
    public void renderRadioField(StringBuffer buffer, Map context, ModelFormField.RadioField radioField);

    public void renderSubmitField(StringBuffer buffer, Map context, ModelFormField.SubmitField submitField);
    public void renderResetField(StringBuffer buffer, Map context, ModelFormField.ResetField resetField);

    public void renderHiddenField(StringBuffer buffer, Map context, ModelFormField.HiddenField hiddenField);
    public void renderIgnoredField(StringBuffer buffer, Map context, ModelFormField.IgnoredField ignoredField);

    public void renderFieldTitle(StringBuffer buffer, Map context, ModelFormField modelFormField);
    
    public void renderFormOpen(StringBuffer buffer, Map context, ModelForm modelForm, Integer itemNumber);
    public void renderFormClose(StringBuffer buffer, Map context, ModelForm modelForm);
    
    public void renderFormatWrapperOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatWrapperClose(StringBuffer buffer, Map context, ModelForm modelForm);

    public void renderFormatHeaderRowOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatHeaderRowClose(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatHeaderRowCellOpen(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField);
    public void renderFormatHeaderRowCellClose(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField);

    public void renderFormatHeaderRowFormCellOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatHeaderRowFormCellClose(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatHeaderRowFormCellTitleSeparator(StringBuffer buffer, Map context, ModelForm modelForm, boolean isLast);
    
    public void renderFormatItemRowOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatItemRowClose(StringBuffer buffer, Map context, ModelForm modelForm);

    public void renderFormatItemRowCellOpen(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField);
    public void renderFormatItemRowCellClose(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField);
    public void renderFormatItemRowFormCellOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatItemRowFormCellClose(StringBuffer buffer, Map context, ModelForm modelForm);

    public void renderFormatFieldRowTitleCellOpen(StringBuffer buffer, Map context, ModelFormField modelFormField);
    public void renderFormatFieldRowTitleCellClose(StringBuffer buffer, Map context, ModelFormField modelFormField);
    public void renderFormatFieldRowSpacerCell(StringBuffer buffer, Map context, ModelFormField modelFormField);
    public void renderFormatFieldRowWidgetCellOpen(StringBuffer buffer, Map context, ModelFormField modelFormField, int positions);
    public void renderFormatFieldRowWidgetCellClose(StringBuffer buffer, Map context, ModelFormField modelFormField, int positions);
}
