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
    public static Collection productsByKeywords(String keywordsString, GenericDelegator delegator, String categoryId) {
        return productsByKeywords(keywordsString, delegator, categoryId, false, false, "OR");
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
    public static Collection productsByKeywords(String keywordsString, GenericDelegator delegator, String categoryId, boolean anyPrefix, boolean anySuffix, String intraKeywordOperator) {
        if (delegator == null) {
            return null;
        }
        String helperName = null;
        helperName = delegator.getEntityHelperName("ProductKeyword");
        boolean useCategory = (categoryId != null && categoryId.length() > 0) ? true : false;

        Collection pbkCollection = new LinkedList();

        String keywords[] = makeKeywordList(keywordsString);
        List keywordList = fixKeywords(keywords, anyPrefix, anySuffix);
        if (keywordList.size() == 0) {
            return null;
        }
        
        List params = new LinkedList();
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
                Debug.logInfo("[KeywordSearch] Params: " + (String) params.get(i));
            }
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                do {
                    pbkCollection.add(resultSet.getString("PRODUCT_ID"));
                } while (resultSet.next())
                    ;
                return pbkCollection;
            } else {
                Debug.logInfo("[KeywordSearch] no results found for search string:" + keywordsString);
                return null;
            }
        } catch (java.sql.SQLException sqle) {
            Debug.logError(sqle);
        }
        catch (GenericEntityException e) {
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

    protected static String[] makeKeywordList(String keywordsString) {
        StringTokenizer tokenizer = new StringTokenizer(keywordsString);

        LinkedList list = new LinkedList();
        String curToken;
        while (tokenizer.hasMoreTokens()) {
            curToken = tokenizer.nextToken();
            list.add(curToken);
        }
        String[] keywords = new String[list.size()];
        keywords = (String[]) list.toArray(keywords);
        return keywords;
    }

    protected static List fixKeywords(String keywords[], boolean anyPrefix, boolean anySuffix) {
        if (keywords == null) {
            return null;
        }
        List list = new LinkedList();
        String str = null;
        for (int i = 0; i < keywords.length; i++) {
            if ((str = keywords[i]) != null) {
                //do some cleanup, and replace wildcards
                str = str.replace('*', '%');
                str = str.replace('?', '_');
                str = str.toLowerCase();

                StringBuffer strSb = new StringBuffer();
                if (anyPrefix) strSb.append('%');
                strSb.append(str);
                if (anySuffix) strSb.append('%');
                str = strSb.toString();
                
                if(!list.contains(str)) {
                    list.add(str);
                }
            }
        }

        return list;
    }

    protected static String getSearchSQL(List keywords, List params, boolean useCategory, String intraKeywordOperator) {
        if (keywords == null || keywords.size() <= 0)
            return null;
        StringBuffer sql = new StringBuffer();
        Iterator keywordIter = keywords.iterator();

        if (intraKeywordOperator == null || (!"AND".equalsIgnoreCase(intraKeywordOperator) && !"OR".equalsIgnoreCase(intraKeywordOperator))) {
            Debug.logWarning("intraKeywordOperator [" + intraKeywordOperator + "] was not valid, defaulting to OR");
            intraKeywordOperator = "OR";
        }
        
        //EXAMPLE:
        //  SELECT DISTINCT P1.PRODUCT_ID FROM PRODUCT_KEYWORD P1, PRODUCT_KEYWORD P2, PRODUCT_KEYWORD P3
        //  WHERE P1.PRODUCT_ID=P2.PRODUCT_ID AND P1.PRODUCT_ID=P3.PRODUCT_ID AND P1.KEYWORD LIKE 'TI%' AND P2.KEYWORD LIKE 'HOUS%' AND P3.KEYWORD LIKE '1003027%'
        //EXAMPLE WITH CATEGORY CONSTRAINT:
        //  SELECT DISTINCT P1.PRODUCT_ID FROM PRODUCT_KEYWORD P1, PRODUCT_KEYWORD P2, PRODUCT_KEYWORD P3, PRODUCT_CATEGORY_MEMBER PCM
        //  WHERE P1.PRODUCT_ID=P2.PRODUCT_ID AND P1.PRODUCT_ID=P3.PRODUCT_ID AND P1.KEYWORD LIKE 'TI%' AND P2.KEYWORD LIKE 'HOUS%' AND P3.KEYWORD LIKE '1003027%' AND P1.PRODUCT_ID=PCM.PRODUCT_ID AND PCM.PRODUCT_CATEGORY_ID='foo'

        StringBuffer select = null;
        if (useCategory) {
            select = new StringBuffer("SELECT DISTINCT P1.PRODUCT_ID, PCM.SEQUENCE_NUM FROM ");
        } else {
            select = new StringBuffer("SELECT DISTINCT P1.PRODUCT_ID FROM ");
        }
        StringBuffer join = new StringBuffer(" WHERE ");
        StringBuffer where = new StringBuffer(" (");

        int i = 1;
        while (keywordIter.hasNext()) {
            String keyword = (String) keywordIter.next();
            String comparator = "=";
            if (keyword.indexOf('%') >= 0 || keyword.indexOf('_') >= 0) {
                comparator = " LIKE ";
            }
            params.add(keyword);
            if (i == 1) {
                select.append("PRODUCT_KEYWORD P");
                select.append(i);

                where.append(" P");
                where.append(i);
                where.append(".KEYWORD");
                where.append(comparator);
                where.append("? ");
            } else {
                select.append(", PRODUCT_KEYWORD P");
                select.append(i);
                select.append(" ");
                
                join.append("P");
                join.append(i - 1);
                join.append(".PRODUCT_ID=P");
                join.append(i);
                join.append(".PRODUCT_ID AND ");
                
                where.append(intraKeywordOperator);
                where.append(" P");
                where.append(i);
                where.append(".KEYWORD");
                where.append(comparator);
                where.append("? ");
            }
            i++;
        }
        where.append(") ");
        
        if (useCategory) {
            select.append(", PRODUCT_CATEGORY_MEMBER PCM");
            where.append(" AND P1.PRODUCT_ID=PCM.PRODUCT_ID AND PCM.PRODUCT_CATEGORY_ID=?");
        }
        sql.append(select.toString());
        sql.append(join.toString());
        sql.append(where.toString());
        if (useCategory) {
            sql.append(" ORDER BY PCM.SEQUENCE_NUM");
        }

        Debug.logInfo("[KeywordSearch] sql=" + sql.toString());
        return sql.toString();
    }

    public static String tokens = ";: ,.!?\t\"\'\r\n()[]{}*%<>";
    public static void induceKeywords(GenericValue product) throws GenericEntityException {
        if (product == null)
            return;
        GenericDelegator delegator = product.getDelegator();
        if (delegator == null)
            return;

        Collection keywords = new TreeSet();
        keywords.add(product.getString("productId").toLowerCase());

        Collection strings = new ArrayList();
        if (product.getString("productName") != null)
            strings.add(product.getString("productName"));
        if (product.getString("comments") != null)
            strings.add(product.getString("comments"));
        if (product.getString("description") != null)
            strings.add(product.getString("description"));
        if (product.getString("longDescription") != null)
            strings.add(product.getString("longDescription"));

        Iterator strIter = strings.iterator();
        while (strIter.hasNext()) {
            String str = (String) strIter.next();
            if (str.length() > 0) {
                StringTokenizer tokener = new StringTokenizer(str, tokens, false);

                while (tokener.hasMoreTokens()) {
                    keywords.add(tokener.nextToken().toLowerCase());
                }
            }
        }

        List toBeStored = new LinkedList();
        Iterator kiter = keywords.iterator();
        while (kiter.hasNext()) {
            String keyword = (String) kiter.next();
            GenericValue productKeyword = delegator.makeValue("ProductKeyword", UtilMisc.toMap("productId", product.getString("productId"), "keyword", keyword));
            toBeStored.add(productKeyword);
        }
        if (toBeStored.size() > 0) {
            Debug.logInfo("[KeywordSearch.induceKeywords] Storing " + toBeStored.size() + " keywords for productId " + product.getString("productId"));
            delegator.storeAll(toBeStored);
        }
    }
}
