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
                  <#-- it is actually somewhat confusing to show the current list here, without that it is much cleaner, especially with heirarchical lists
                    <#if shoppingList?has_content>
                      <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
                      <option value="${shoppingList.shoppingListId}">--</option>
                    </#if>
                  -->
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
    <#if canView>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Shopping List Detail - ${shoppingList.listName}</div>
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
              <form name="updateList" method="post" action="<@ofbizUrl>/updateShoppingList</@ofbizUrl>">
                <input type="hidden" class="inputBox" name="shoppingListId" value="${shoppingList.shoppingListId}">
                <input type="hidden" class="inputBox" name="partyId" value="${shoppingList.partyId?if_exists}">
                <table border='0' width='100%' cellspacing='0' cellpadding='0'>
                  <tr>
                    <td><div class="tableheadtext">List Name</div></td>
                    <td><input type="text" class="inputBox" size="25" name="listName" value="${shoppingList.listName}">
                  </tr>
                  <tr>
                    <td><div class="tableheadtext">Description</div></td>
                    <td><input type="text" class="inputBox" size="70" name="description" value="${shoppingList.description?if_exists}">
                  </tr>
                  <tr>
                    <td><div class="tableheadtext">List Type</div></td>
                    <td>
                      <select name="shoppingListTypeId" class="selectBox">
                      	<#if shoppingListType?exists>
                          <option value="${shoppingListType.shoppingListTypeId}">${shoppingListType.description?default(shoppingListType.shoppingListTypeId)}</option>
                          <option value="${shoppingListType.shoppingListTypeId}">--</option>
                        </#if>
                        <#list shoppingListTypes as newShoppingListType>
                          <option value="${newShoppingListType.shoppingListTypeId}">${newShoppingListType.description?default(newShoppingListType.shoppingListTypeId)}</option>
                        </#list>
                      </select>
                    </td>
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
                  <tr>
                    <td><div class="tableheadtext">Parent List</div></td>
                    <td>
                      <select name="parentShoppingListId" class="selectBox">
                      	<#if parentShoppingList?exists>
                          <option value="${parentShoppingList.shoppingListId}">${parentShoppingList.listName?default(parentShoppingList.shoppingListId)}</option>
                        </#if>
                        <option value="">No Parent</option>
                        <#list allShoppingLists as newParShoppingList>
                          <option value="${newParShoppingList.shoppingListId}">${newParShoppingList.listName?default(newParShoppingList.shoppingListId)}</option>
                        </#list>
                      </select>
                      <#if parentShoppingList?exists>
                        <a href="<@ofbizUrl>/editShoppingList?shoppingListId=${parentShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">Go To Parent (${parentShoppingList.listName?default(parentShoppingList.shoppingListId)})</a>
                      </#if>
                    </td>
                  </tr>                           
                  <tr>
                    <td><div class="tableheadtext">&nbsp;</div></td>
                    <td align="left">
                      <a href="javascript:document.updateList.submit();" class="buttontext">[Save]</a>         
                    </td>
                  </tr>
                </table>
              </form>           
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<#if childShoppingLists?has_content>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Child Shopping Lists - ${shoppingList.listName}</div>
          </td>  
          <td valign="middle" align="right">
              <a href="<@ofbizUrl>/addListToCart?shoppingListId=${shoppingList.shoppingListId}&includeChild=yes</@ofbizUrl>" class="lightbuttontext">[Add Parent And Child List(s) To Cart]</a>
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
			<table width='100%' cellspacing="0" cellpadding="1" border="0">
			  <TR> 
				<TD NOWRAP><div class='tabletext'><b>List Name</b></div></TD>
				<TD NOWRAP align=center><div class='tabletext'><b>Total Price</b></div></TD>
				<td>&nbsp;</td>
			  </TR>
			  <#list childShoppingLists as childShoppingList>  
				  <tr>
					<td nowrap align="left">
                      <a href="<@ofbizUrl>/editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${childShoppingList.listName?default(childShoppingList.shoppingListId)}</a>
					</td>                      
					<td nowrap align="right">
					  <div class="tabltext"></div>
					</td>                      
					<td nowrap align="right">
                      <a href="<@ofbizUrl>/editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">[Go To List]</a>&nbsp;
                      <a href="<@ofbizUrl>/addListToCart?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">[Add List To Cart]</a>
					</td>                      
				  </tr>
				</form>
			  </#list>
			</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
</#if>

<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Shopping List Items - ${shoppingList.listName}</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/addListToCart?shoppingListId=${shoppingList.shoppingListId}</@ofbizUrl>" class="lightbuttontext">[Add List To Cart]</a>            
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
                        <td nowrap align="center"><div class='tabletext'><a href="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if>?shoppingListId=${listItem.shoppingListId}&quantity=${listItem.quantity}&add_product_id=${listItem.productId}</@ofbizUrl>" class="buttontext">[Add ${listItem.quantity?string} To Cart]</a></div></td>
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

	<#else>
		<#-- shoppingList was found, but belongs to a different party -->
		<div class="head2">ERROR: The specified shopping list (with ID ${shoppingList.shoppingListId}) does not belong to you, please try again.</div>
	</#if>
</#if>
