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
 *@version    $Revision: 1.3 $
 *@since      3.0
-->

<#if hasPermission>
  <div class="head1">Alternate Key Word Thesaurus</div>
  <form method="POST" action="<@ofbizUrl>/createKeywordThesaurus</@ofbizUrl>">
        <div class="tabletext">
        Keyword:<input type="text" name="enteredKeyword" size="10" class="inputBox"/>
        Alternate:<input type="text" name="alternateKeyword" size="10" class="inputBox"/>
        Relationship:<select name="relationshipEnumId" class="selectBox"><#list relationshipEnums as relationshipEnum><option value="${relationshipEnum.enumId}">${relationshipEnum.description}</option></#list></select>
        <input type="submit" value="Add" class="smallButton">
    </div>
  </form>

  <div class="tabletext">
    <#list letterList as letter>
      <#if letter == firstLetter><#assign highlight=true><#else><#assign highlight=false></#if>
      <a href="<@ofbizUrl>/editKeywordThesaurus?firstLetter=${letter}</@ofbizUrl>" class="buttontext"><#if highlight>[</#if>[${letter}]<#if highlight>]</#if></a>
    </#list>
  </div>
  <br/>

  <#assign lastkeyword = "">
  <table border="1" cellpadding="2" cellspacing="0">
    <#list keywordThesauruses as keyword>
      <#assign relationship = keyword.getRelatedOneCache("RelationshipEnumeration")>
      <#if keyword.enteredKeyword == lastkeyword><#assign sameRow=true><#else><#assign lastkeyword=keyword.enteredKeyword><#assign sameRow=false></#if>
      <#if sameRow == false>
        <#if (keyword_index > 0)>
          </td>
        </tr>
        </#if>
        <tr>
          <td>
            <form method="POST" action="<@ofbizUrl>/createKeywordThesaurus</@ofbizUrl>">
              <div class="tabletext">
                <b>${keyword.enteredKeyword}</b>
                <a href="<@ofbizUrl>/deleteKeywordThesaurus?enteredKeyword=${keyword.enteredKeyword}</@ofbizUrl>" class="buttontext">[Delete All]</a>
              </div>
              <div class="tabletext">
                <input type="hidden" name="enteredKeyword" value=${keyword.enteredKeyword}>
                Alternate: <input type="text" name="alternateKeyword" size="10">
                Relationship:<select name="relationshipEnumId" class="selectBox"><#list relationshipEnums as relationshipEnum><option value="${relationshipEnum.enumId}">${relationshipEnum.description}</option></#list></select>
                <input type="submit" value="Add">
              </div>
            </form>
          </td>
          <td align="left">
      </#if>
      <div class="tabletext">
        <a href="<@ofbizUrl>/deleteKeywordThesaurus?enteredKeyword=${keyword.enteredKeyword}&alternateKeyword=${keyword.alternateKeyword}</@ofbizUrl>" class="buttontext">[X]</a>
        <b>${keyword.alternateKeyword}</b>&nbsp;(Rel:${(relationship.description)?default(keyword.relationshipEnumId?if_exists)})
      </div>
    </#list>
      </td>
    </tr>
  </table>
<#else>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
</#if>
