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
    public void renderDisplayField(StringBuffer buffer, Map context, ModelFormField.DisplayField displayField, GenericDelegator delegator, LocalDispatcher dispatcher);
    public void renderHyperlinkField(StringBuffer buffer, Map context, ModelFormField.HyperlinkField hyperlinkField, GenericDelegator delegator, LocalDispatcher dispatcher);

    public void renderTextField(StringBuffer buffer, Map context, ModelFormField.TextField textField, GenericDelegator delegator, LocalDispatcher dispatcher);
    public void renderTextareaField(StringBuffer buffer, Map context, ModelFormField.TextareaField textareaField, GenericDelegator delegator, LocalDispatcher dispatcher);
    public void renderDateTimeField(StringBuffer buffer, Map context, ModelFormField.DateTimeField dateTimeField, GenericDelegator delegator, LocalDispatcher dispatcher);

    public void renderDropDownField(StringBuffer buffer, Map context, ModelFormField.DropDownField dropDownField, GenericDelegator delegator, LocalDispatcher dispatcher);
    public void renderCheckField(StringBuffer buffer, Map context, ModelFormField.CheckField checkField, GenericDelegator delegator, LocalDispatcher dispatcher);
    public void renderRadioField(StringBuffer buffer, Map context, ModelFormField.RadioField radioField, GenericDelegator delegator, LocalDispatcher dispatcher);

    public void renderSubmitField(StringBuffer buffer, Map context, ModelFormField.SubmitField submitField, GenericDelegator delegator, LocalDispatcher dispatcher);
    public void renderResetField(StringBuffer buffer, Map context, ModelFormField.ResetField resetField, GenericDelegator delegator, LocalDispatcher dispatcher);

    public void renderHiddenField(StringBuffer buffer, Map context, ModelFormField.HiddenField hiddenField, GenericDelegator delegator, LocalDispatcher dispatcher);
    public void renderIgnoredField(StringBuffer buffer, Map context, ModelFormField.IgnoredField ignoredField, GenericDelegator delegator, LocalDispatcher dispatcher);
}
