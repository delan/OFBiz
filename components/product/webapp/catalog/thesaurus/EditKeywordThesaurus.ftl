<#--
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
 *
 *@author     Nathan De Graw
 *@version    $Revision: 1.1 $
 *@since      3.0
-->

<#if hasPermission>

  <div class="head1">Alternate Key Word Thesaurus</div>
  <form method="POST" action="<@ofbizUrl>/createKeywordThesaurus</@ofbizUrl>">
    <div>Keyword <input type="text" name="enteredKeyword" size="10"> Alternate <input type="text" name="alternateKeyword" size="10">
    <input type="submit" value="Add"></div>
  </form>

  <div>
    <#list letterList as letter>
      <#if letter == firstLetter><#assign highlight=true><#else><#assign highlight=false></#if>
      <a href="<@ofbizUrl>/editKeywordThesaurus?firstLetter=${letter}</@ofbizUrl>" class="buttontext"><#if highlight>[</#if>[${letter}]<#if highlight>]</#if></a>
    </#list>
  </div>
 
  <#assign lastkeyword = "">
  <table border=1> 
    <#list keywordThesauruses as keyword>
      <#if keyword.enteredKeyword == lastkeyword><#assign sameRow=true><#else><#assign lastkeyword=keyword.enteredKeyword><#assign sameRow=false></#if>
      <#if sameRow == false>
        <tr>
          <td>
            <form method="POST" action="<@ofbizUrl>/createKeywordThesaurus</@ofbizUrl>">
              <div><b>${keyword.enteredKeyword}</b>
              <div>Alternate <input type="text" name="alternateKeyword" size="10">
              <input type="hidden" name="enteredKeyword" value=${keyword.enteredKeyword}> 
              <input type="submit" value="Add"></div>
            </form>
            <form method="POST" action="<@ofbizUrl>/deleteKeywordThesaurus</@ofbizUrl>">
              <div>Delete All <input type="hidden" name="enteredKeyword" value=${keyword.enteredKeyword}>
              <input type="submit" value="Delete"></div>
            </form>
          <td align=right>
          <form method="POST" action="<@ofbizUrl>/deleteKeywordThesaurus</@ofbizUrl>">
            <div><b>${keyword.alternateKeyword}</b>
            <input type="hidden" name="enteredKeyword" value=${keyword.enteredKeyword}>
            <input type="hidden" name="alternateKeyword" value=${keyword.alternateKeyword}>
            <input type="submit" value="Delete"></div>
          </form>
      <#else>
        <form method="POST" action="<@ofbizUrl>/deleteKeywordThesaurus</@ofbizUrl>">
          <div><b>${keyword.alternateKeyword}</b>
          <input type="hidden" name="enteredKeyword" value=${keyword.enteredKeyword}>
          <input type="hidden" name="alternateKeyword" value=${keyword.alternateKeyword}>
          <input type="submit" value="Delete"></div>
        </form>
      </#if>
    </#list>
  </table>
  
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
