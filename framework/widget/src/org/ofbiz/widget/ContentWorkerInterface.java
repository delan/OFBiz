/*
 * $Id$
 *
 *  Copyright (c) 2004-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.widget;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 * ContentWorkerInterface
 * 
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version $Rev$
 */
public interface ContentWorkerInterface {

    public GenericValue getCurrentContentExt(GenericDelegator delegator, List trail, GenericValue userLogin, Map ctx, Boolean nullThruDatesOnly, String contentAssocPredicateId)  throws GeneralException;

    public Map renderSubContentAsTextExt(GenericDelegator delegator, String contentId, Writer out, String mapKey, String subContentId, GenericValue subContentDataResourceView, 
            Map templateContext, Locale locale, String mimeTypeId, GenericValue userLogin, Timestamp fromDate) throws GeneralException, IOException;

    public String renderSubContentAsTextCacheExt(GenericDelegator delegator, String contentId,  String mapKey,  GenericValue subContentDataResourceView, 
            Map templateRoot, Locale locale, String mimeTypeId, GenericValue userLogin, Timestamp fromDate) throws GeneralException, IOException;

    public Map renderSubContentAsTextCacheExt(GenericDelegator delegator, String contentId, Writer out, String mapKey,  GenericValue subContentDataResourceView, 
            Map templateRoot, Locale locale, String mimeTypeId, GenericValue userLogin, Timestamp fromDate) throws GeneralException, IOException;

    public Map renderSubContentAsTextCacheExt(GenericDelegator delegator, String contentId, Writer out, String mapKey,  GenericValue subContentDataResourceView, 
            Map templateRoot, Locale locale, String mimeTypeId, GenericValue userLogin, Timestamp fromDate, Boolean nullThruDatesOnly) throws GeneralException, IOException;

    public Map renderContentAsTextExt(GenericDelegator delegator, String contentId, Writer out, Map templateContext, GenericValue view, Locale locale, String mimeTypeId) throws GeneralException, IOException;

    public String renderContentAsTextCacheExt(GenericDelegator delegator, String contentId,  Map templateContext, GenericValue view, Locale locale, String mimeTypeId) throws GeneralException, IOException;

    public Map renderContentAsTextCacheExt(GenericDelegator delegator, String contentId, Writer out, Map templateContext, GenericValue view, Locale locale, String mimeTypeId) throws GeneralException, IOException;

    public String getMimeTypeIdExt(GenericDelegator delegator, GenericValue view, Map ctx);

    public GenericValue getWebSitePublishPointExt(GenericDelegator delegator, String contentId, boolean ignoreCache) throws GenericEntityException;
}
