/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project (www.ofbiz.org)
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
package org.ofbiz.commonapp.product.product;

import java.util.*;
import java.sql.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 *  Does a product search by keyword using the PRODUCT_KEYWORD table.
 *  <br>Special thanks to Glen Thorne and the Weblogic Commerce Server for ideas.
 *
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version 1.0
 *@created Sep 4, 2001
 */
public class KeywordSearch {
    
    /** Does a product search by keyword using the PRODUCT_KEYWORD table.
     *@param keywordsString A space separated list of keywords with '%' or '*' as wildcards for 0..many characters and '_' or '?' for wildcard for 1 character.
     *@param delegator The delegator to look up the name of the helper/server to get a connection to
     *@param categoryId If not null the list of products will be restricted to those in this category
     *@return Collection of productId Strings
     */
    public static Collection productsByKeywords(String keywordsString, GenericDelegator delegator, String categoryId, String visitId) {
        return productsByKeywords(keywordsString, delegator, categoryId, visitId, false, false, "OR");
    }
    
    /** Does a product search by keyword using the PRODUCT_KEYWORD table.
     *@param keywordsString A space separated list of keywords with '%' or '*' as wildcards for 0..many characters and '_' or '?' for wildcard for 1 character.
     *@param delegator The delegator to look up the name of the helper/server to get a connection to
     *@param categoryId If not null the list of products will be restricted to those in this category
     *@param anyPrefix If true use a wildcard to allow any prefix to each keyword
     *@param anySuffix If true use a wildcard to allow any suffix to each keyword
     *@param intraKeywordOperator The operator to use inbetween the keywords, usually "AND" or "OR"
     *@return Collection of productId Strings
     */
    public static ArrayList productsByKeywords(String keywordsString, GenericDelegator delegator, String categoryId, String visitId, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
        if (delegator == null) {
            return null;
        }
        String helperName = null;
        helperName = delegator.getEntityHelperName("ProductKeyword");
        boolean useCategory = (categoryId != null && categoryId.length() > 0) ? true : false;
        intraKeywordOperator = intraKeywordOperator.toUpperCase();
        if (intraKeywordOperator == null || (!"AND".equals(intraKeywordOperator) && !"OR".equals(intraKeywordOperator))) {
            Debug.logWarning("intraKeywordOperator [" + intraKeywordOperator + "] was not valid, defaulting to OR");
            intraKeywordOperator = "OR";
        }
        
        boolean removeStems = UtilProperties.propertyValueEquals("general", "remove.stems", "true");

        ArrayList pbkList = new ArrayList(100);

        List keywordFirstPass = makeKeywordList(keywordsString);
        List keywordList = fixKeywords(keywordFirstPass, anyPrefix, anySuffix, removeStems, intraKeywordOperator);
        if (keywordList.size() == 0) {
            return null;
        }
        
        List params = new ArrayList();
        String sql = getSearchSQL(keywordList, params, useCategory, intraKeywordOperator);
        if (sql == null) {
            return null;
        }
        if (useCategory) {
            params.add(categoryId);
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection(helperName);
            statement = connection.prepareStatement(sql);

            for (int i = 0; i < params.size(); i++) {
                statement.setString(i + 1, (String) params.get(i));
                if (Debug.verboseOn()) Debug.logVerbose("[KeywordSearch] Params: " + (String) params.get(i));
            }
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                pbkList.add(resultSet.getString("PRODUCT_ID"));
                //Debug.logInfo("PRODUCT_ID=" + resultSet.getString("PRODUCT_ID") + " TOTAL_WEIGHT=" + resultSet.getInt("TOTAL_WEIGHT"));
            }
            if (Debug.infoOn()) Debug.logInfo("[KeywordSearch] got " + pbkList.size() + " results found for search string: [" + keywordsString + "], keyword combine operator is " + intraKeywordOperator + ", categoryId=" + categoryId + ", anyPrefix=" + anyPrefix + ", anySuffix=" + anySuffix + ", removeStems=" + removeStems);
            
            try {
                GenericValue productKeywordResult = delegator.makeValue("ProductKeywordResult", null);
                Long nextPkrSeqId = delegator.getNextSeqId("ProductKeywordResult");
                productKeywordResult.set("productKeywordResultId", nextPkrSeqId.toString());
                productKeywordResult.set("visitId", visitId);
                if (useCategory) productKeywordResult.set("productCategoryId", categoryId);
                productKeywordResult.set("searchString", keywordsString);
                productKeywordResult.set("intraKeywordOperator", intraKeywordOperator);
                productKeywordResult.set("anyPrefix", new Boolean(anyPrefix));
                productKeywordResult.set("anySuffix", new Boolean(anySuffix));
                productKeywordResult.set("removeStems", new Boolean(removeStems));
                productKeywordResult.set("numResults", new Long(pbkList.size()));
                productKeywordResult.create();
            } catch (Exception e) {
                Debug.logError(e, "Error saving keyword result stats");
                Debug.logError("[KeywordSearch] Stats are: got " + pbkList.size() + " results found for search string: [" + keywordsString + "], keyword combine operator is " + intraKeywordOperator + ", categoryId=" + categoryId + ", anyPrefix=" + anyPrefix + ", anySuffix=" + anySuffix + ", removeStems=" + removeStems);
            }
            
            if (pbkList.size() == 0) {
                return null;
            } else {
                return pbkList;
            }
        } catch (java.sql.SQLException sqle) {
            Debug.logError(sqle);
        } catch (GenericEntityException e) {
            Debug.logError(e);
        } finally { 
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException sqle) { }
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException sqle) { }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) { }
        }
        return null;
    }

    protected static List makeKeywordList(String keywordsString) {
        StringTokenizer tokenizer = new StringTokenizer(keywordsString);

        List keywords = new ArrayList(10);
        String curToken;
        while (tokenizer.hasMoreTokens()) {
            curToken = tokenizer.nextToken();
            keywords.add(curToken);
        }
        return keywords;
    }

    protected static List fixKeywords(List keywords, boolean anyPrefix, boolean anySuffix, boolean removeStems, String intraKeywordOperator) {
        if (keywords == null) {
            return null;
        }
        
        String stopWordBag = null;
        if (intraKeywordOperator.equals("AND")) {
            stopWordBag = UtilProperties.getPropertyValue("general", "stop.word.bag.and");
        } else {
            stopWordBag = UtilProperties.getPropertyValue("general", "stop.word.bag.or");
        }
        
        String stemBag = UtilProperties.getPropertyValue("general", "stem.bag");
        List stemList = new ArrayList(10);
        if (UtilValidate.isNotEmpty(stemBag)) {
            String curToken;
            StringTokenizer tokenizer = new StringTokenizer(stemBag, ": ");
            while (tokenizer.hasMoreTokens()) {
                curToken = tokenizer.nextToken();
                stemList.add(curToken);
            }
        }
        
        List fixedKeywords = new ArrayList(keywords.size());
        String str = null;
        Iterator kwiter = keywords.iterator();
        while (kwiter.hasNext()) {
            str = (String) kwiter.next();
            
            //do some cleanup, and replace wildcards
            str = str.replace('*', '%');
            str = str.replace('?', '_');
            str = str.toLowerCase();
            if (stopWordBag.indexOf(":" + str + ":") >= 0) continue;

            //if enabled, remove stems in stem.bag
            if (anySuffix && removeStems) {
                Iterator stemIter = stemList.iterator();
                while (stemIter.hasNext()) {
                    String stem = (String) stemIter.next();
                    if (str.endsWith(stem)) {
                        str = str.substring(0, str.length() - stem.length());
                    }
                }
            }

            StringBuffer strSb = new StringBuffer();
            if (anyPrefix) strSb.append('%');
            strSb.append(str);
            if (anySuffix) strSb.append('%');
            str = strSb.toString();

            if(!fixedKeywords.contains(str)) {
                fixedKeywords.add(str);
            }
        }

        return fixedKeywords;
    }

    protected static String getSearchSQL(List keywords, List params, boolean useCategory, String intraKeywordOperator) {
        if (keywords == null || keywords.size() <= 0)
            return null;
        StringBuffer sql = new StringBuffer();
        Iterator keywordIter = keywords.iterator();

        boolean isAnd = intraKeywordOperator.equals("AND");
        
        //AND EXAMPLE:
        //  SELECT DISTINCT P1.PRODUCT_ID, (P1.RELEVANCY_WEIGHT + P2.RELEVANCY_WEIGHT + P3.RELEVANCY_WEIGHT) AS TOTAL_WEIGHT FROM PRODUCT_KEYWORD P1, PRODUCT_KEYWORD P2, PRODUCT_KEYWORD P3
        //  WHERE P1.PRODUCT_ID=P2.PRODUCT_ID AND P1.PRODUCT_ID=P3.PRODUCT_ID AND P1.KEYWORD LIKE 'TI%' AND P2.KEYWORD LIKE 'HOUS%' AND P3.KEYWORD = '1003027' ORDER BY TOTAL_WEIGHT DESC
        //AND EXAMPLE WITH CATEGORY CONSTRAINT:
        //  SELECT DISTINCT P1.PRODUCT_ID, PCM.SEQUENCE_NUM AS CAT_SEQ_NUM, TOTAL_WEIGHT = P1.RELEVANCY_WEIGHT + P2.RELEVANCY_WEIGHT + P3.RELEVANCY_WEIGHT FROM PRODUCT_KEYWORD P1, PRODUCT_KEYWORD P2, PRODUCT_KEYWORD P3, PRODUCT_CATEGORY_MEMBER PCM
        //  WHERE P1.PRODUCT_ID=P2.PRODUCT_ID AND P1.PRODUCT_ID=P3.PRODUCT_ID AND P1.KEYWORD LIKE 'TI%' AND P2.KEYWORD LIKE 'HOUS%' AND P3.KEYWORD = '1003027' AND P1.PRODUCT_ID=PCM.PRODUCT_ID AND PCM.PRODUCT_CATEGORY_ID='foo' ORDER BY CAT_SEQ_NUM, TOTAL_WEIGHT DESC

        //ORs are a little more complicated, so get individual results group them by PRODUCT_ID and sum the RELEVANCY_WEIGHT
        //OR EXAMPLE:
        //  SELECT DISTINCT P1.PRODUCT_ID, SUM(P1.RELEVANCY_WEIGHT) AS TOTAL_WEIGHT FROM PRODUCT_KEYWORD P1
        //  WHERE (P1.KEYWORD LIKE 'TI%' OR P1.KEYWORD LIKE 'HOUS%' OR P1.KEYWORD = '1003027') GROUP BY P1.PRODUCT_ID ORDER BY TOTAL_WEIGHT DESC
        //OR EXAMPLE WITH CATEGORY CONSTRAINT:
        //  SELECT DISTINCT P1.PRODUCT_ID, MIN(PCM.SEQUENCE_NUM) AS CAT_SEQ_NUM, TOTAL_WEIGHT = SUM(P1.RELEVANCY_WEIGHT) FROM PRODUCT_KEYWORD P1, PRODUCT_CATEGORY_MEMBER PCM
        //  WHERE (P1.KEYWORD LIKE 'TI%' OR P1.KEYWORD LIKE 'HOUS%' OR P1.KEYWORD = '1003027') AND P1.PRODUCT_ID=PCM.PRODUCT_ID AND PCM.PRODUCT_CATEGORY_ID='foo' GROUP BY P1.PRODUCT_ID ORDER BY CAT_SEQ_NUM, TOTAL_WEIGHT DESC

        StringBuffer from = new StringBuffer(" FROM ");
        StringBuffer join = new StringBuffer(" WHERE ");
        StringBuffer where = new StringBuffer(" (");
        StringBuffer selectWeightTotal = new StringBuffer();
        StringBuffer groupBy = new StringBuffer();
        
        if (isAnd) {
            selectWeightTotal.append(", (P1.RELEVANCY_WEIGHT");
            int i = 1;
            while (keywordIter.hasNext()) {
                String keyword = (String) keywordIter.next();
                String comparator = "=";
                if (keyword.indexOf('%') >= 0 || keyword.indexOf('_') >= 0) {
                    comparator = " LIKE ";
                }
                params.add(keyword);
                if (i == 1) {
                    from.append("PRODUCT_KEYWORD P");
                    from.append(i);

                    where.append(" P");
                    where.append(i);
                    where.append(".KEYWORD");
                    where.append(comparator);
                    where.append("? ");
                } else {
                    from.append(", PRODUCT_KEYWORD P");
                    from.append(i);
                    from.append(" ");

                    selectWeightTotal.append(" + P");
                    selectWeightTotal.append(i);
                    selectWeightTotal.append(".RELEVANCY_WEIGHT");

                    join.append("P");
                    join.append(i - 1);
                    join.append(".PRODUCT_ID=P");
                    join.append(i);
                    join.append(".PRODUCT_ID AND ");

                    where.append("AND P");
                    where.append(i);
                    where.append(".KEYWORD");
                    where.append(comparator);
                    where.append("? ");
                }
                i++;
            }
            selectWeightTotal.append(") AS TOTAL_WEIGHT");
            where.append(") ");
        } else {
            selectWeightTotal.append(", SUM(P1.RELEVANCY_WEIGHT) AS TOTAL_WEIGHT");
            from.append("PRODUCT_KEYWORD P1");
            groupBy.append(" GROUP BY P1.PRODUCT_ID ");
            int i = 1;
            while (keywordIter.hasNext()) {
                String keyword = (String) keywordIter.next();
                String comparator = "=";
                if (keyword.indexOf('%') >= 0 || keyword.indexOf('_') >= 0) {
                    comparator = " LIKE ";
                }
                params.add(keyword);
                if (i == 1) {
                    where.append(" P1.KEYWORD");
                    where.append(comparator);
                    where.append("? ");
                } else {
                    where.append("OR P1.KEYWORD");
                    where.append(comparator);
                    where.append("? ");
                }
                i++;
            }
            where.append(") ");
        }
        
        if (useCategory) {
            from.append(", PRODUCT_CATEGORY_MEMBER PCM");
            where.append(" AND P1.PRODUCT_ID=PCM.PRODUCT_ID AND PCM.PRODUCT_CATEGORY_ID=?");
        }

        StringBuffer select = null;
        if (useCategory) {
            if (isAnd) {
                select = new StringBuffer("SELECT DISTINCT P1.PRODUCT_ID, PCM.SEQUENCE_NUM AS CAT_SEQ_NUM");
            } else {
                select = new StringBuffer("SELECT DISTINCT P1.PRODUCT_ID, MIN(PCM.SEQUENCE_NUM) AS CAT_SEQ_NUM");
            }
        } else {
            select = new StringBuffer("SELECT DISTINCT P1.PRODUCT_ID");
        }
        sql.append(select.toString());
        sql.append(selectWeightTotal.toString());
        sql.append(from.toString());
        sql.append(join.toString());
        sql.append(where.toString());
        sql.append(groupBy.toString());
        //for order by: do by SEQUENCE_NUM first then by RELEVANCY_WEIGHT
        // this basicly allows a default ordering with the RELEVANCY_WEIGHT and a manual override with SEQUENCE_NUM
        sql.append(" ORDER BY ");
        if (useCategory) {
            sql.append("CAT_SEQ_NUM, ");
        }
        sql.append("TOTAL_WEIGHT DESC");

        if (Debug.infoOn()) Debug.logInfo("[KeywordSearch] sql=" + sql.toString());
        return sql.toString();
    }

    public static String separators = ";: ,.!?\t\"\'\r\n\\/()[]{}*%<>-_";
    public static void induceKeywords(GenericValue product) throws GenericEntityException {
        if (product == null) return;
        GenericDelegator delegator = product.getDelegator();
        if (delegator == null) return;
        String productId = product.getString("productId");

        String stopWordBagOr = UtilProperties.getPropertyValue("general", "stop.word.bag.or");
        String stopWordBagAnd = UtilProperties.getPropertyValue("general", "stop.word.bag.and");
        
        Map keywords = new TreeMap();
        keywords.put(product.getString("productId").toLowerCase(), new Long(1));

        Collection strings = new ArrayList(40);
        if (product.getString("productName") != null) strings.add(product.getString("productName"));
        if (product.getString("description") != null) strings.add(product.getString("description"));
        if (product.getString("longDescription") != null) strings.add(product.getString("longDescription"));
        
        //get strings from attributes and features
        Iterator productFeatureAndAppls = UtilMisc.toIterator(delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId)));
        while (productFeatureAndAppls != null && productFeatureAndAppls.hasNext()) {
            GenericValue productFeatureAndAppl = (GenericValue) productFeatureAndAppls.next();
            if (productFeatureAndAppl.get("description") != null) strings.add(productFeatureAndAppl.get("description"));
            if (productFeatureAndAppl.get("abbrev") != null) strings.add(productFeatureAndAppl.get("abbrev"));
            if (productFeatureAndAppl.get("idCode") != null) strings.add(productFeatureAndAppl.get("idCode"));
        }

        Iterator productAttributes = UtilMisc.toIterator(delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId", productId)));
        while (productAttributes != null && productAttributes.hasNext()) {
            GenericValue productAttribute = (GenericValue) productAttributes.next();
            if (productAttribute.get("name") != null) strings.add(productAttribute.get("name"));
            if (productAttribute.get("value") != null) strings.add(productAttribute.get("value"));
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
            GenericValue productKeyword = delegator.makeValue("ProductKeyword", UtilMisc.toMap("productId", product.getString("productId"), "keyword", entry.getKey(), "relevancyWeight", entry.getValue()));
            toBeStored.add(productKeyword);
        }
        if (toBeStored.size() > 0) {
            if (Debug.infoOn()) Debug.logInfo("[KeywordSearch.induceKeywords] Storing " + toBeStored.size() + " keywords for productId " + product.getString("productId"));
            delegator.storeAll(toBeStored);
        }
    }
}
