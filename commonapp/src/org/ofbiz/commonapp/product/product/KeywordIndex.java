/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 *
 */
package org.ofbiz.commonapp.product.product;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 *  Does indexing in preparation for a keyword search.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class KeywordIndex {

    public static void indexKeywords(GenericValue product) throws GenericEntityException {
        if (product == null) return;
        GenericDelegator delegator = product.getDelegator();

        if (delegator == null) return;
        String productId = product.getString("productId");

        // String separators = ";: ,.!?\t\"\'\r\n\\/()[]{}*%<>-_";
        String separators = UtilProperties.getPropertyValue("prodsearch", "index.keyword.separators", ";: ,.!?\t\"\'\r\n\\/()[]{}*%<>-_");
        String stopWordBagOr = UtilProperties.getPropertyValue("prodsearch", "stop.word.bag.or");
        String stopWordBagAnd = UtilProperties.getPropertyValue("prodsearch", "stop.word.bag.and");

        Map keywords = new TreeMap();
        List strings = new ArrayList(50);

        int pidWeight = 1;

        try {
            pidWeight = Integer.parseInt(UtilProperties.getPropertyValue("prodsearch", "index.weight.Product.productId", "1"));
        } catch (Exception e) {}
        keywords.put(product.getString("productId").toLowerCase(), new Long(pidWeight));

        addWeightedKeywordSourceString(product, "productName", strings);
        addWeightedKeywordSourceString(product, "brandName", strings);
        addWeightedKeywordSourceString(product, "description", strings);
        addWeightedKeywordSourceString(product, "longDescription", strings);

        if (!"0".equals(UtilProperties.getPropertyValue("prodsearch", "index.weight.ProductFeatureAndAppl.description", "1")) ||
            !"0".equals(UtilProperties.getPropertyValue("prodsearch", "index.weight.ProductFeatureAndAppl.abbrev", "1")) ||
            !"0".equals(UtilProperties.getPropertyValue("prodsearch", "index.weight.ProductFeatureAndAppl.idCode", "1"))) {
            // get strings from attributes and features
            Iterator productFeatureAndAppls = UtilMisc.toIterator(delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId)));

            while (productFeatureAndAppls != null && productFeatureAndAppls.hasNext()) {
                GenericValue productFeatureAndAppl = (GenericValue) productFeatureAndAppls.next();

                addWeightedKeywordSourceString(productFeatureAndAppl, "description", strings);
                addWeightedKeywordSourceString(productFeatureAndAppl, "abbrev", strings);
                addWeightedKeywordSourceString(productFeatureAndAppl, "idCode", strings);
            }
        }

        if (!"0".equals(UtilProperties.getPropertyValue("prodsearch", "index.weight.ProductAttribute.attrName", "1")) ||
            !"0".equals(UtilProperties.getPropertyValue("prodsearch", "index.weight.ProductAttribute.attrValue", "1"))) {
            Iterator productAttributes = UtilMisc.toIterator(delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId", productId)));

            while (productAttributes != null && productAttributes.hasNext()) {
                GenericValue productAttribute = (GenericValue) productAttributes.next();

                addWeightedKeywordSourceString(productAttribute, "attrName", strings);
                addWeightedKeywordSourceString(productAttribute, "attrValue", strings);
            }
        }
        
        Iterator strIter = strings.iterator();

        while (strIter.hasNext()) {
            String str = (String) strIter.next();

            if (str.length() > 0) {
                StringTokenizer tokener = new StringTokenizer(str, separators, false);

                while (tokener.hasMoreTokens()) {
                    String token = tokener.nextToken().toLowerCase();
                    String colonToken = ":" + token + ":";

                    if (stopWordBagOr.indexOf(colonToken) >= 0 && stopWordBagAnd.indexOf(colonToken) >= 0) {
                        continue;
                    }
                    Long curWeight = (Long) keywords.get(token);

                    if (curWeight == null) {
                        keywords.put(token, new Long(1));
                    } else {
                        keywords.put(token, new Long(curWeight.longValue() + 1));
                    }
                }
            }
        }

        List toBeStored = new LinkedList();
        Iterator kiter = keywords.entrySet().iterator();

        while (kiter.hasNext()) {
            Map.Entry entry = (Map.Entry) kiter.next();
            GenericValue productKeyword = delegator.makeValue("ProductKeyword", UtilMisc.toMap("productId", product.getString("productId"), "keyword", ((String) entry.getKey()).toLowerCase(), "relevancyWeight", entry.getValue()));
            toBeStored.add(productKeyword);
        }
        if (toBeStored.size() > 0) {
            if (Debug.infoOn()) Debug.logInfo("[KeywordSearch.induceKeywords] Storing " + toBeStored.size() + " keywords for productId " + product.getString("productId"));
            delegator.storeAll(toBeStored);
        }
    }

    public static void addWeightedKeywordSourceString(GenericValue value, String fieldName, List strings) {
        if (value.getString(fieldName) != null) {
            int weight = 1;

            try {
                weight = Integer.parseInt(UtilProperties.getPropertyValue("prodsearch", "index.weight." + value.getEntityName() + "." + fieldName, "1"));
            } catch (Exception e) {}

            for (int i = 0; i < weight; i++) {
                strings.add(value.getString(fieldName));
            }
        }
    }
}
