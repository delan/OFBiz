/*
 * $Id$
 * $Log$
 */
package org.ofbiz.ecommerce.catalog;

import java.util.*;
import javax.naming.InitialContext;
import java.sql.*;
import javax.sql.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> KeywordSearch.java
 * <p><b>Description:</b> Does a product search by keyword using the PRODUCT_KEYWORD table.
 *  Special thanks to Glen Thorne and the Weblogic Commerce Server for ideas.
 * <p>Copyright (c) 2001 The Open For Business Project (www.ofbiz.org) and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version 1.0
 *@created Sep 4, 2001
 */
public class KeywordSearch {  
  /** Does a product search by keyword using the PRODUCT_KEYWORD table.
   *@param keywordsString A space separated list of keywords with '%' or '*' as wildcards for 0..many characters and '_' for wildcard for 1 character.
   *@param serverName The name of the server to get a connection to
   *@return Collection of productId Strings
   */
  public static Collection productsByKeywords(String keywordsString, String serverName) {
    if(serverName == null) return null;
    Collection pbkCollection = new LinkedList();
    
    String keywords[] = makeKeywordList(keywordsString);
    List keywordList = fixKeywords(keywords);
    List params = new LinkedList();
    String sql = getSearchSQL(keywordList, params);
    if(sql == null) return null;
    
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      connection = ConnectionFactory.getConnection(serverName);
      statement = connection.prepareStatement(sql);
      
      for(int i=0; i<params.size(); i++) {
        statement.setString(i+1,(String)params.get(i));
        Debug.logInfo("[KeywordSearch] Params: " + (String)params.get(i));
      }
      resultSet = statement.executeQuery();
      
      if(resultSet.next()) {
        do {
          pbkCollection.add(resultSet.getString("PRODUCT_ID"));
        }
        while (resultSet.next());
        return pbkCollection;
      }
      else {
        Debug.logInfo("[KeywordSearch] no results found for search string:" + keywordsString);
        return null;
      }
    }
    catch (java.sql.SQLException sqle) {
      sqle.printStackTrace();
    }
    finally {
      try { if (resultSet != null) resultSet.close(); } catch (SQLException sqle) { }
      try { if (statement != null) statement.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    
    return null;
  }
  
  protected static String[] makeKeywordList(String keywordsString) {
    StringTokenizer tokenizer = new StringTokenizer(keywordsString);
    
    LinkedList list = new LinkedList();
    String curToken;
    while (tokenizer.hasMoreTokens()) {
      curToken = tokenizer.nextToken().toLowerCase();
      curToken = curToken.replace('*', '%');
      list.add(curToken);
    }
    String[] keywords = new String[list.size()];
    keywords = (String[])list.toArray(keywords);
    return keywords;
  }
  
  protected static List fixKeywords(String keywords[]) {
    if(keywords == null)
      return null;
    List list = new LinkedList();
    String str = null;
    for(int i = 0; i < keywords.length; i++)
      if((str = keywords[i]) != null && !list.contains(str))
        list.add(str);
    
    return list;
  }
  
  protected static String getSearchSQL(List keywords, List params) {
    if(keywords == null || keywords.size() <= 0) return null;
    
    StringBuffer buf = new StringBuffer();
    Iterator keywordIter = keywords.iterator();
    
    StringBuffer selectBuf = new StringBuffer();
    StringBuffer joinBuf = new StringBuffer();
    StringBuffer whereBuf = new StringBuffer();
    
    //EXAMPLE:
    //  SELECT DISTINCT P1.PRODUCT_ID FROM PRODUCT_KEYWORD P1, PRODUCT_KEYWORD P2, PRODUCT_KEYWORD P3
    //  WHERE (P1.PRODUCT_ID=P2.PRODUCT_ID AND P1.PRODUCT_ID=P3.PRODUCT_ID AND P1.KEYWORD LIKE 'TI%' AND P2.KEYWORD LIKE 'HOUS%' AND P3.KEYWORD LIKE '1003027%')
    
    selectBuf.append("SELECT DISTINCT P1.PRODUCT_ID FROM ");
    joinBuf.append(" WHERE (");
    whereBuf.append(" ");
    
    int i = 1;
    while(keywordIter.hasNext()) {
      String keyword = (String)keywordIter.next();
      String comparator = "=";
      if(keyword.indexOf('%') >= 0) comparator = " LIKE ";
      params.add(keyword);
      if(i == 1) {
        selectBuf.append("PRODUCT_KEYWORD P"); selectBuf.append(i);
        whereBuf.append(" P"); whereBuf.append(i); whereBuf.append(".KEYWORD"); whereBuf.append(comparator); whereBuf.append("? ");
      } else {
        selectBuf.append(", PRODUCT_KEYWORD P"); selectBuf.append(i); selectBuf.append(" ");
        joinBuf.append("P"); joinBuf.append((i-1)); joinBuf.append(".PRODUCT_ID=P"); joinBuf.append(i); joinBuf.append(".PRODUCT_ID AND ");
        whereBuf.append("AND P"); whereBuf.append(i); whereBuf.append(".KEYWORD"); whereBuf.append(comparator); whereBuf.append("? ");
      }
      i++;
    }
    buf.append(selectBuf.toString());
    buf.append(joinBuf.toString());
    buf.append(whereBuf.toString());
    buf.append(")");
    
    Debug.logInfo("[KeywordSearch] sql=" + buf);
    return buf.toString();
  }
}
