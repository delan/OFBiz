/*
 * $Id: KeywordSearch.java,v 1.11 2004/01/22 12:53:28 jonesde Exp $
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
package org.ofbiz.product.product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

/**
 *  Does a product search by keyword using the PRODUCT_KEYWORD table.
 *  <br>Special thanks to Glen Thorne and the Weblogic Commerce Server for ideas.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.11 $
 * @since      2.1
 */
public class KeywordSearch {

    public static final String module = KeywordSearch.class.getName();

    public static Set thesaurusRelsToInclude = new HashSet();
    public static Set thesaurusRelsForReplace = new HashSet();

    static {
        thesaurusRelsToInclude.add("KWTR_UF");
        thesaurusRelsToInclude.add("KWTR_USE");
        thesaurusRelsToInclude.add("KWTR_CS");
        thesaurusRelsToInclude.add("KWTR_NT");
        thesaurusRelsToInclude.add("KWTR_BT");
        thesaurusRelsToInclude.add("KWTR_RT");

        thesaurusRelsForReplace.add("KWTR_USE");
        thesaurusRelsForReplace.add("KWTR_CS");
    }

    public static List makeKeywordList(String keywordsString) {
        StringTokenizer tokenizer = new StringTokenizer(keywordsString);

        List keywords = new ArrayList(10);
        String curToken;

        while (tokenizer.hasMoreTokens()) {
            curToken = tokenizer.nextToken();
            curToken = curToken.toLowerCase();
            keywords.add(curToken);
        }
        return keywords;
    }

    public static boolean expandKeyword(String enteredKeyword, List addToList, GenericDelegator delegator) {
        boolean replaceEnteredKeyword = false;

        try {
            List thesaurusList = delegator.findByAndCache("KeywordThesaurus", UtilMisc.toMap("enteredKeyword", enteredKeyword));
            Iterator thesaurusIter = thesaurusList.iterator();
            while (thesaurusIter.hasNext()) {
                GenericValue keywordThesaurus = (GenericValue) thesaurusIter.next();
                String relationshipEnumId = (String) keywordThesaurus.get("relationshipEnumId");
                if (thesaurusRelsToInclude.contains(relationshipEnumId)) {
                    addToList.addAll(makeKeywordList(keywordThesaurus.getString("alternateKeyword")));
                    if (thesaurusRelsForReplace.contains(relationshipEnumId)) {
                        replaceEnteredKeyword = true;
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error expanding entered keyword", module);
        }

        Debug.logInfo("Expanded keyword [" + enteredKeyword + "], got list: " + addToList, module);
        return replaceEnteredKeyword;
    }

    public static List fixKeywords(List keywords, boolean anyPrefix, boolean anySuffix, boolean removeStems, boolean isAnd) {
        if (keywords == null) {
            return null;
        }

        String stopWordBag = null;

        if (isAnd) {
            stopWordBag = UtilProperties.getPropertyValue("prodsearch", "stop.word.bag.and");
        } else {
            stopWordBag = UtilProperties.getPropertyValue("prodsearch", "stop.word.bag.or");
        }

        String stemBag = UtilProperties.getPropertyValue("prodsearch", "stem.bag");
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

            // do some cleanup, and replace wildcards
            str = str.replace('*', '%');
            str = str.replace('?', '_');
            str = str.toLowerCase();
            if (stopWordBag.indexOf(":" + str + ":") >= 0) continue;

            // if enabled, remove stems in stem.bag
            if (removeStems) {
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

            if (!fixedKeywords.contains(str)) {
                fixedKeywords.add(str);
            }
        }

        return fixedKeywords;
    }

    /* Does a product search by keyword using the PRODUCT_KEYWORD table.
     *@param keywordsString A space separated list of keywords with '%' or '*' as wildcards for 0..many characters and '_' or '?' for wildcard for 1 character.
     *@param delegator The delegator to look up the name of the helper/server to get a connection to
     *@param categoryId If not null the list of products will be restricted to those in this category
     *@return Collection of productId Strings
     */
    /* TODO: DEJ 20031025 delete this if not used in the near future
    public static Collection productsByKeywords(String keywordsString, GenericDelegator delegator, String categoryId, String visitId) {
        return productsByKeywords(keywordsString, delegator, categoryId, visitId, false, false, false);
    }
     */

    /* Does a product search by keyword using the PRODUCT_KEYWORD table.
     *@param keywordsString A space separated list of keywords with '%' or '*' as wildcards for 0..many characters and '_' or '?' for wildcard for 1 character.
     *@param delegator The delegator to look up the name of the helper/server to get a connection to
     *@param categoryId If not null the list of products will be restricted to those in this category
     *@param anyPrefix If true use a wildcard to allow any prefix to each keyword
     *@param anySuffix If true use a wildcard to allow any suffix to each keyword
     *@param isAnd The operator to use inbetween the keywords true for "AND", false for "OR"
     *@return ArrayList of productId Strings
     */
    /* TODO: DEJ 20031025 delete this if not used in the near future
    public static ArrayList productsByKeywords(String keywordsString, GenericDelegator delegator, String categoryId, String visitId, boolean anyPrefix, boolean anySuffix, boolean isAnd) {
        if (delegator == null) {
            return null;
        }
        String helperName = null;

        helperName = delegator.getEntityHelperName("ProductKeyword");
        boolean useCategory = (categoryId != null && categoryId.length() > 0) ? true : false;
        boolean removeStems = UtilProperties.propertyValueEquals("prodsearch", "remove.stems", "true");

        ArrayList pbkList = new ArrayList(100);

        List keywordFirstPass = makeKeywordList(keywordsString);
        List keywordList = fixKeywords(keywordFirstPass, anyPrefix, anySuffix, removeStems, isAnd);

        if (keywordList.size() == 0) {
            return null;
        }

        List params = new ArrayList();
        String sql = getSearchSQL(keywordList, params, useCategory, isAnd);

        if (sql == null) {
            return null;
        }
        if (useCategory) {
            params.add(categoryId);
            params.add(UtilDateTime.nowTimestamp());
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getConnection(helperName);
            statement = connection.prepareStatement(sql);

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    statement.setString(i + 1, (String) param);
                } else if (param instanceof Timestamp) {
                    statement.setTimestamp(i + 1, (Timestamp) param);
                } else {
                    //in this class we only put Strings and Timestamps in there, but warn anyway...
                    Debug.logWarning("Found a keyword search query parameter with an unknown type: " + param.getClass().getName(), module);
                }
                if (Debug.verboseOn()) Debug.logVerbose("[KeywordSearch] Params: " + (String) params.get(i), module);
            }
            resultSet = statement.executeQuery();
            Set idSet = new HashSet();
            while (resultSet.next()) {
                //since there is a chance of duplicate IDs, check to see if the ID is already in the list to eliminate all but the first
                String productId = resultSet.getString("PRODUCT_ID");
                if (productId != null && !idSet.contains(productId)) {
                    pbkList.add(productId);
                    idSet.add(productId);
                    // Debug.logInfo("PRODUCT_ID=" + productId + " TOTAL_WEIGHT=" + resultSet.getInt("TOTAL_WEIGHT"), module);
                }
            }
            if (Debug.infoOn()) Debug.logInfo("[KeywordSearch] got " + pbkList.size() + " results found for search string: [" + keywordsString + "], keyword combine operator is AND? [" + isAnd + "], categoryId=" + categoryId + ", anyPrefix=" + anyPrefix + ", anySuffix=" + anySuffix + ", removeStems=" + removeStems, module);
            //if (Debug.infoOn()) Debug.logInfo("pbkList=" + pbkList, module);

            try {
                GenericValue productKeywordResult = delegator.makeValue("ProductKeywordResult", null);
                Long nextPkrSeqId = delegator.getNextSeqId("ProductKeywordResult");

                productKeywordResult.set("productKeywordResultId", nextPkrSeqId.toString());
                productKeywordResult.set("visitId", visitId);
                if (useCategory) productKeywordResult.set("productCategoryId", categoryId);
                productKeywordResult.set("searchString", keywordsString);
                productKeywordResult.set("intraKeywordOperator", (isAnd ? "AND" : "OR"));
                productKeywordResult.set("anyPrefix", new Boolean(anyPrefix));
                productKeywordResult.set("anySuffix", new Boolean(anySuffix));
                productKeywordResult.set("removeStems", new Boolean(removeStems));
                productKeywordResult.set("numResults", new Long(pbkList.size()));
                productKeywordResult.create();
            } catch (Exception e) {
                Debug.logError(e, "Error saving keyword result stats", module);
                Debug.logError("[KeywordSearch] Stats are: got " + pbkList.size() + " results found for search string: [" + keywordsString + "], keyword combine operator is " + (isAnd ? "AND" : "OR") + ", categoryId=" + categoryId + ", anyPrefix=" + anyPrefix + ", anySuffix=" + anySuffix + ", removeStems=" + removeStems, module);
            }

            if (pbkList.size() == 0) {
                return null;
            } else {
                return pbkList;
            }
        } catch (java.sql.SQLException sqle) {
            Debug.logError(sqle, module);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException sqle) {}
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException sqle) {}
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {}
        }
        return null;
    }
     */

    /* TODO: DEJ 20031025 delete this if not used in the near future
    protected static String getSearchSQL(List keywords, List params, boolean useCategory, boolean isAnd) {
        if (keywords == null || keywords.size() <= 0)
            return null;
        StringBuffer sql = new StringBuffer();
        Iterator keywordIter = keywords.iterator();

        // AND EXAMPLE:
        // SELECT DISTINCT P1.PRODUCT_ID, (P1.RELEVANCY_WEIGHT + P2.RELEVANCY_WEIGHT + P3.RELEVANCY_WEIGHT) AS TOTAL_WEIGHT FROM PRODUCT_KEYWORD P1, PRODUCT_KEYWORD P2, PRODUCT_KEYWORD P3
        // WHERE P1.PRODUCT_ID=P2.PRODUCT_ID AND P1.PRODUCT_ID=P3.PRODUCT_ID AND P1.KEYWORD LIKE 'TI%' AND P2.KEYWORD LIKE 'HOUS%' AND P3.KEYWORD = '1003027' ORDER BY TOTAL_WEIGHT DESC
        // AND EXAMPLE WITH CATEGORY CONSTRAINT:
        // SELECT DISTINCT P1.PRODUCT_ID, PCM.SEQUENCE_NUM AS CAT_SEQ_NUM, TOTAL_WEIGHT = P1.RELEVANCY_WEIGHT + P2.RELEVANCY_WEIGHT + P3.RELEVANCY_WEIGHT FROM PRODUCT_KEYWORD P1, PRODUCT_KEYWORD P2, PRODUCT_KEYWORD P3, PRODUCT_CATEGORY_MEMBER PCM
        // WHERE P1.PRODUCT_ID=P2.PRODUCT_ID AND P1.PRODUCT_ID=P3.PRODUCT_ID AND P1.KEYWORD LIKE 'TI%' AND P2.KEYWORD LIKE 'HOUS%' AND P3.KEYWORD = '1003027' AND P1.PRODUCT_ID=PCM.PRODUCT_ID AND PCM.PRODUCT_CATEGORY_ID='foo' AND (PCM.THRU_DATE IS NULL OR PCM.THRU_DATE > ?) ORDER BY CAT_SEQ_NUM, TOTAL_WEIGHT DESC

        // ORs are a little more complicated, so get individual results group them by PRODUCT_ID and sum the RELEVANCY_WEIGHT
        // OR EXAMPLE:
        // SELECT DISTINCT P1.PRODUCT_ID, SUM(P1.RELEVANCY_WEIGHT) AS TOTAL_WEIGHT FROM PRODUCT_KEYWORD P1
        // WHERE (P1.KEYWORD LIKE 'TI%' OR P1.KEYWORD LIKE 'HOUS%' OR P1.KEYWORD = '1003027') GROUP BY P1.PRODUCT_ID ORDER BY TOTAL_WEIGHT DESC
        // OR EXAMPLE WITH CATEGORY CONSTRAINT:
        // SELECT DISTINCT P1.PRODUCT_ID, MIN(PCM.SEQUENCE_NUM) AS CAT_SEQ_NUM, TOTAL_WEIGHT = SUM(P1.RELEVANCY_WEIGHT) FROM PRODUCT_KEYWORD P1, PRODUCT_CATEGORY_MEMBER PCM
        // WHERE (P1.KEYWORD LIKE 'TI%' OR P1.KEYWORD LIKE 'HOUS%' OR P1.KEYWORD = '1003027') AND P1.PRODUCT_ID=PCM.PRODUCT_ID AND PCM.PRODUCT_CATEGORY_ID='foo' AND (PCM.THRU_DATE IS NULL OR PCM.THRU_DATE > ?) GROUP BY P1.PRODUCT_ID ORDER BY CAT_SEQ_NUM, TOTAL_WEIGHT DESC

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
            where.append(" AND P1.PRODUCT_ID=PCM.PRODUCT_ID AND PCM.PRODUCT_CATEGORY_ID=? AND (PCM.THRU_DATE IS NULL OR PCM.THRU_DATE > ?)");
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
        // for order by: do by SEQUENCE_NUM first then by RELEVANCY_WEIGHT
        // this basicly allows a default ordering with the RELEVANCY_WEIGHT and a manual override with SEQUENCE_NUM
        sql.append(" ORDER BY ");
        if (useCategory) {
            sql.append("CAT_SEQ_NUM, ");
        }
        sql.append("TOTAL_WEIGHT DESC");

        if (Debug.verboseOn()) Debug.logVerbose("[KeywordSearch] sql=" + sql.toString(), module);
        return sql.toString();
    }
     */

    public static void induceKeywords(GenericValue product) throws GenericEntityException {
        if (product == null) return;
        KeywordIndex.indexKeywords(product);
    }
}
