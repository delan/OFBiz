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
package org.ofbiz.content.widget.form;

import java.util.Map;

/**
 * Widget Library - Form String Renderer interface
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev:$
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

    public void renderHiddenField(StringBuffer buffer, Map context, ModelFormField modelFormField, String value);
    public void renderHiddenField(StringBuffer buffer, Map context, ModelFormField.HiddenField hiddenField);
    public void renderIgnoredField(StringBuffer buffer, Map context, ModelFormField.IgnoredField ignoredField);

    public void renderFieldTitle(StringBuffer buffer, Map context, ModelFormField modelFormField);
    
    public void renderFormOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormClose(StringBuffer buffer, Map context, ModelForm modelForm);
    
    public void renderFormatListWrapperOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatListWrapperClose(StringBuffer buffer, Map context, ModelForm modelForm);

    public void renderFormatHeaderRowOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatHeaderRowClose(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatHeaderRowCellOpen(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField);
    public void renderFormatHeaderRowCellClose(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField);

    public void renderFormatHeaderRowFormCellOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatHeaderRowFormCellClose(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatHeaderRowFormCellTitleSeparator(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField, boolean isLast);
    
    public void renderFormatItemRowOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatItemRowClose(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatItemRowCellOpen(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField);
    public void renderFormatItemRowCellClose(StringBuffer buffer, Map context, ModelForm modelForm, ModelFormField modelFormField);
    public void renderFormatItemRowFormCellOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatItemRowFormCellClose(StringBuffer buffer, Map context, ModelForm modelForm);

    public void renderFormatSingleWrapperOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatSingleWrapperClose(StringBuffer buffer, Map context, ModelForm modelForm);

    public void renderFormatFieldRowOpen(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatFieldRowClose(StringBuffer buffer, Map context, ModelForm modelForm);
    public void renderFormatFieldRowTitleCellOpen(StringBuffer buffer, Map context, ModelFormField modelFormField);
    public void renderFormatFieldRowTitleCellClose(StringBuffer buffer, Map context, ModelFormField modelFormField);
    public void renderFormatFieldRowSpacerCell(StringBuffer buffer, Map context, ModelFormField modelFormField);
    public void renderFormatFieldRowWidgetCellOpen(StringBuffer buffer, Map context, ModelFormField modelFormField, int positions, int positionSpan, Integer nextPositionInRow);
    public void renderFormatFieldRowWidgetCellClose(StringBuffer buffer, Map context, ModelFormField modelFormField, int positions, int positionSpan, Integer nextPositionInRow);

    public void renderFormatEmptySpace(StringBuffer buffer, Map context, ModelForm modelForm);

    public void renderTextFindField(StringBuffer buffer, Map context, ModelFormField.TextFindField textField);
    public void renderDateFindField(StringBuffer buffer, Map context, ModelFormField.DateFindField textField);
    public void renderRangeFindField(StringBuffer buffer, Map context, ModelFormField.RangeFindField textField);
    public void renderLookupField(StringBuffer buffer, Map context, ModelFormField.LookupField textField);
    public void renderFileField(StringBuffer buffer, Map context, ModelFormField.FileField textField);
    public void renderPasswordField(StringBuffer buffer, Map context, ModelFormField.PasswordField textField);
    public void renderImageField(StringBuffer buffer, Map context, ModelFormField.ImageField textField);
}
