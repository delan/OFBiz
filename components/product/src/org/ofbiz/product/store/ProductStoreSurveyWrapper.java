/*
 * $Id: ProductStoreSurveyWrapper.java,v 1.3 2003/12/06 00:04:50 ajzeneski Exp $
 *
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.product.store;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.content.survey.SurveyWrapper;

import java.util.Map;

/**
 * Product Store Survey Wrapper
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.3 $
 * @since      3.0
 */
public class ProductStoreSurveyWrapper extends SurveyWrapper {

    public static final String module = ProductStoreSurveyWrapper.class.getName();

    protected GenericValue productStoreSurveyAppl = null;

    protected ProductStoreSurveyWrapper() {}

    public ProductStoreSurveyWrapper(GenericValue productStoreSurveyAppl, String partyId, Map passThru) {
        this.productStoreSurveyAppl = productStoreSurveyAppl;

        this.passThru = passThru;
        if (this.productStoreSurveyAppl != null) {
            this.partyId = partyId;
            this.delegator = productStoreSurveyAppl.getDelegator();
            this.surveyId = productStoreSurveyAppl.getString("surveyId");
            this.templatePath = productStoreSurveyAppl.getString("templatePath");
        } else {
            throw new IllegalArgumentException("Required parameter productStoreSurveyAppl missing");
        }
        this.checkParameters();
    }
}
