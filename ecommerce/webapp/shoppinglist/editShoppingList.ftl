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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision$
 *@since      2.1.1
-->

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Shopping Lists</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/createEmptyShoppingList</@ofbizUrl>" class="lightbuttontext">[Create New]</a>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <#if shoppingLists?has_content>
              <form name="selectShoppingList" method="post" action="<@ofbizUrl>/editShoppingList</@ofbizUrl>">
                <select name="shoppingListId" class="selectBox">
                  <#if shoppingList?has_content>
                    <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
                    <option value="${shoppingList.shoppingListId}">--</option>
                  </#if>
                  <#list shoppingLists as list>
                    <option value="${list.shoppingListId}">${list.listName}</option>
                  </#list>
                </select>
                &nbsp;&nbsp;
                <a href="javascript:document.selectShoppingList.submit();" class="buttontext">[Edit]</a>
              </form>
            <#else>
              <div class="tabletext">No shopping lists to select, create a new one.</div>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<br>

<#if shoppingList?has_content>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Shopping List Detail</div>
          </td>  
          <td valign="middle" align="right">
            <a href="javascript:document.updateList.submit();" class="lightbuttontext">[Save]</a>        
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <#if shoppingLists?has_content>
              <form name="updateList" method="post" action="<@ofbizUrl>/updateShoppingList</@ofbizUrl>">
                <input type="hidden" class="inputBox" name="shoppingListId" value="${shoppingList.shoppingListId}">
                <input type="hidden" class="inputBox" name="partyId" value="${shoppingList.partyId?if_exists}">
                <table border='0' width='100%' cellspacing='0' cellpadding='0'>
                  <tr>
                    <td><div class="tableheadtext">List Name</div></td>
                    <td><input type="text" class="inputBox" name="listName" value="${shoppingList.listName}">
                  </tr>
                  <tr>
                    <td><div class="tableheadtext">Description</div></td>
                    <td><input type="text" class="inputBox" name="description" value="${shoppingList.description?if_exists}">
                  </tr>
                  <tr>
                    <td><div class="tableheadtext">Public?</div></td>
                    <td>
                      <select name="isPublic" class="selectBox">
                        <option>${shoppingList.isPublic}</option>
                        <option value="${shoppingList.isPublic}">--</option>
                        <option>Y</option>
                        <option>N</option>
                      </select>
                    </td>
                  </tr>                           
                </table>
              </form>           
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Shopping List - ${shoppingList.listName}</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <#if shoppingListItems?has_content>             
                <table width='100%' cellspacing="0" cellpadding="1" border="0">
                  <TR> 
                    <TD NOWRAP><div class='tabletext'><b>Product</b></div></TD>
                    <TD NOWRAP align=center><div class='tabletext'><b>Quantity Requested</b></div></TD>
                    <TD NOWRAP align=right><div class='tabletext'><b>Quantity Purchased</b></div></TD>                    
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                  </TR>

                  <#list shoppingListItems as listItem>  
                    <#assign product = listItem.getRelatedOneCache("Product")>      
                    <form method="POST" action="<@ofbizUrl>/updateShoppingListItem</@ofbizUrl>" name='listform_${listItem.shoppingListItemSeqId}' style='margin: 0;'>
                      <input type="hidden" name="shoppingListId" value="${listItem.shoppingListId}">
                      <input type="hidden" name="shoppingListItemSeqId" value="${listItem.shoppingListItemSeqId}">
                      
                      <tr><td colspan="6"><hr class='sepbar'></td></tr>
                      <tr>
                        <td>
                          <div class='tabletext'>                    
                             <a href="<@ofbizUrl>/product?product_id=${listItem.productId}</@ofbizUrl>" class='buttontext'>${listItem.productId} - 
                             ${product.productName?if_exists}</a> : ${product.description?if_exists}
                          </div>
                        </td>
                        <td nowrap align="center">
                          <div class='tabletext'>                         
                            <input size="6" class='inputBox' type="text" name="quantity" value="${listItem.quantity?string.number}">
                          </div>
                        </td> 
                        <td nowrap align="center">
                          <div class="tabltext">${listItem.quantityPurchased?default(0)?string.number}</div>
                        </td>                      
                        <td nowrap align="center"><div class='tabletext'><a href="javascript:document.listform_${listItem.shoppingListItemSeqId}.submit();" class="buttontext">[Update]</a></div></td>
                        <td nowrap align="center"><div class='tabletext'><a href="<@ofbizUrl>/removeFromShoppingList?shoppingListId=${listItem.shoppingListId}&shoppingListItemSeqId=${listItem.shoppingListItemSeqId}</@ofbizUrl>" class="buttontext">[Remove]</a></div></td>
                        <td nowrap align="center"><div class='tabletext'><a href="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if>?shoppingListId=${listItem.shoppingListId}&quantity=1&add_product_id=${listItem.productId}</@ofbizUrl>" class="buttontext">[Add 1 To Cart]</a></div></td>
                      </tr>
                    </form>
                  </#list>
                </table>
            <#else>
                <div class='head2'>Your shopping list is empty.</div>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
</#if>