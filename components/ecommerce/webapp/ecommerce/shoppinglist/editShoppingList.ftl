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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Revision: 1.4 $
 *@since      2.1.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.EcommerceShoppingLists}</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/createEmptyShoppingList</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a>
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
                <a href="javascript:document.selectShoppingList.submit();" class="buttontext">[${uiLabelMap.CommonEdit}]</a>
              </form>
            <#else>
              <div class="tabletext">${uiLabelMap.EcommerceNoShoppingListsCreate}.</div>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<br/>

<#if shoppingList?has_content>
    <#if canView>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.EcommerceShoppingListDetail} - ${shoppingList.listName}</div>
          </td>  
          <td valign="middle" align="right">
            <a href="javascript:document.updateList.submit();" class="submenutextright">${uiLabelMap.CommonSave}</a>            
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
                    <td><div class="tableheadtext">${uiLabelMap.EcommerceListName}</div></td>
                    <td><input type="text" class="inputBox" size="25" name="listName" value="${shoppingList.listName}">
                  </tr>
                  <tr>
                    <td><div class="tableheadtext">${uiLabelMap.CommonDescription}</div></td>
                    <td><input type="text" class="inputBox" size="70" name="description" value="${shoppingList.description?if_exists}">
                  </tr>
                  <tr>
                    <td><div class="tableheadtext">${uiLabelMap.EcommerceListType}</div></td>
                    <td>
                      <select name="shoppingListTypeId" class="selectBox">
                      	<#if shoppingListType?exists>
                          <option value="${shoppingListType.shoppingListTypeId}">${shoppingListType.description?default(shoppingListType.shoppingListTypeId)}</option>
                          <option value="${shoppingListType.shoppingListTypeId}">--</option>
                        </#if>
                        <#list shoppingListTypes as shoppingListType>
                          <option value="${shoppingListType.shoppingListTypeId}">${shoppingListType.description?default(shoppingListType.shoppingListTypeId)}</option>
                        </#list>
                      </select>
                    </td>
                  </tr>                           
                  <tr>
                    <td><div class="tableheadtext">${uiLabelMap.EcommercePublic}?</div></td>
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
                    <td><div class="tableheadtext">${uiLabelMap.EcommerceParentList}</div></td>
                    <td>
                      <select name="parentShoppingListId" class="selectBox">
                      	<#if parentShoppingList?exists>
                          <option value="${parentShoppingList.shoppingListId}">${parentShoppingList.listName?default(parentShoppingList.shoppingListId)}</option>
                        </#if>
                        <option value="">${uiLabelMap.EcommerceNoParent}</option>
                        <#list allShoppingLists as newParShoppingList>
                          <option value="${newParShoppingList.shoppingListId}">${newParShoppingList.listName?default(newParShoppingList.shoppingListId)}</option>
                        </#list>
                      </select>
                      <#if parentShoppingList?exists>
                        <a href="<@ofbizUrl>/editShoppingList?shoppingListId=${parentShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonGotoParent} (${parentShoppingList.listName?default(parentShoppingList.shoppingListId)})</a>
                      </#if>
                    </td>
                  </tr>                           
                  <tr>
                    <td><div class="tableheadtext">&nbsp;</div></td>
                    <td align="left">
                      <a href="javascript:document.updateList.submit();" class="buttontext">[${uiLabelMap.CommonSave}]</a>         
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

<#if childShoppingListDatas?has_content>
<br/>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.EcommerceChildShoppingList} - ${shoppingList.listName}</div>
          </td>  
          <td valign="middle" align="right">
              <a href="<@ofbizUrl>/addListToCart?shoppingListId=${shoppingList.shoppingListId}&includeChild=yes</@ofbizUrl>" class="submenutextright">${uiLabelMap.EcommerceAddChildListsToCart}</a>
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
				<TD NOWRAP><div class='tabletext'><b>${uiLabelMap.EcommerceListName}</b></div></TD>
				<TD NOWRAP align="right"><div class='tabletext'><b>${uiLabelMap.EcommerceTotalPrice}</b></div></TD>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			  </TR>
			  <#list childShoppingListDatas as childShoppingListData>
			      <#assign childShoppingList = childShoppingListData.childShoppingList>
			      <#assign totalPrice = childShoppingListData.totalPrice>
				  <tr>
					<td nowrap align="left">
                      <a href="<@ofbizUrl>/editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${childShoppingList.listName?default(childShoppingList.shoppingListId)}</a>
					</td>                      
					<td nowrap align="right">
					  <div class="tabletext">${totalPrice?string.currency}</div>
					</td>                      
					<td align="right">
                      <a href="<@ofbizUrl>/editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.EcommerceGoToList}]</a>
                      <a href="<@ofbizUrl>/addListToCart?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.EcommerceAddListToCart}]</a>
					</td>                      
				  </tr>
				</form>
			  </#list>
			  <tr><td colspan="6"><hr class='sepbar'></td></tr>
			  <tr>
				<td><div class="tabletext">&nbsp;</div></td>
				<td nowrap align="right">
				  <div class="tableheadtext">${shoppingListChildTotal?string.currency}</div>
				</td>                      
				<td><div class="tabletext">&nbsp;</div></td>
			  </tr>
			</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
</#if>

<br/>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.EcommerceListItems} - ${shoppingList.listName}</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/addListToCart?shoppingListId=${shoppingList.shoppingListId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.EcommerceAddListToCart}</a>
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
            <#if shoppingListItemDatas?has_content>
                <table width='100%' cellspacing="0" cellpadding="1" border="0">
                  <TR>
                    <TD NOWRAP><div class='tabletext'><b>${uiLabelMap.EcommerceProduct}</b></div></TD>
                    <TD NOWRAP align="center"><div class='tabletext'><b>${uiLabelMap.CommonQuantity}</b></div></TD>
                    <#-- <TD NOWRAP align="center"><div class='tabletext'><b>Purchased</b></div></TD> -->
                    <TD NOWRAP align="right"><div class='tabletext'><b>${uiLabelMap.EcommercePrice}</b></div></TD>
                    <TD NOWRAP align="right"><div class='tabletext'><b>${uiLabelMap.EcommerceTotal}</b></div></TD>
                    <td>&nbsp;</td>
                  </TR>

                  <#list shoppingListItemDatas as shoppingListItemData>
                    <#assign shoppingListItem = shoppingListItemData.shoppingListItem>
                    <#assign product = shoppingListItemData.product>
                    <#assign unitPrice = shoppingListItemData.unitPrice>
                    <#assign totalPrice = shoppingListItemData.totalPrice>
                    <#assign productVariantAssocs = shoppingListItemData.productVariantAssocs?if_exists>
                    <#assign isVirtual = product.isVirtual?exists && product.isVirtual.equals("Y")>
                    
                      <tr>
                        <td>
                          <div class='tabletext'>
                             <a href="<@ofbizUrl>/product?product_id=${shoppingListItem.productId}</@ofbizUrl>" class='buttontext'>${shoppingListItem.productId} - 
                             ${product.productName?if_exists}</a> : ${product.description?if_exists}
                          </div>
                        </td>
                        <td nowrap align="center">
						  <form method="POST" action="<@ofbizUrl>/updateShoppingListItem</@ofbizUrl>" name='listform_${shoppingListItem.shoppingListItemSeqId}' style='margin: 0;'>
						    <input type="hidden" name="shoppingListId" value="${shoppingListItem.shoppingListId}">
						    <input type="hidden" name="shoppingListItemSeqId" value="${shoppingListItem.shoppingListItemSeqId}">
                            <div class='tabletext'>
                              <input size="6" class='inputBox' type="text" name="quantity" value="${shoppingListItem.quantity?string.number}">
                            </div>
		                  </form>
                        </td>
                        <#--
                        <td nowrap align="center">
                          <div class="tabletext">${shoppingListItem.quantityPurchased?default(0)?string.number}</div>
                        </td>
                        -->
                        <td nowrap align="right">
                          <div class="tabletext">${unitPrice?string.currency}</div>
                        </td>
                        <td nowrap align="right">
                          <div class="tabletext">${totalPrice?string.currency}</div>
                        </td>
                        <td align="right">
                        	<a href="javascript:document.listform_${shoppingListItem.shoppingListItemSeqId}.submit();" class="buttontext">[${uiLabelMap.CommonUpdate}]</a>
                        	<a href="<@ofbizUrl>/removeFromShoppingList?shoppingListId=${shoppingListItem.shoppingListId}&shoppingListItemSeqId=${shoppingListItem.shoppingListItemSeqId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonRemove}]</a>
                          <#if isVirtual && productVariantAssocs?has_content>
                            <#assign replaceItemAction = "/replaceShoppingListItem/" + requestAttributes._CURRENT_VIEW_?if_exists>
                            <#assign addToCartAction = "/additem/" + requestAttributes._CURRENT_VIEW_?if_exists>
                            <br/>
                            <form method="POST" action="<@ofbizUrl>${addToCartAction}</@ofbizUrl>" name='listreplform_${shoppingListItem.shoppingListItemSeqId}' style='margin: 0;'>
                              <input type="hidden" name="shoppingListId" value="${shoppingListItem.shoppingListId}">
                              <input type="hidden" name="shoppingListItemSeqId" value="${shoppingListItem.shoppingListItemSeqId}">
                              <input type="hidden" name="quantity" value="${shoppingListItem.quantity}">
                              <select name="add_product_id" class="selectBox">
                              	<#list productVariantAssocs as productVariantAssoc>
                              	  <#assign variantProduct = productVariantAssoc.getRelatedOneCache("AssocProduct")>
                              	  <#if variantProduct?exists>
                              	    <option value="${variantProduct.productId}">${variantProduct.productName} [${variantProduct.productId}]</option>
                              	  </#if>
                              	</#list>
                              </select>
                              <br/>
                              <a href="javascript:document.listreplform_${shoppingListItem.shoppingListItemSeqId}.action='<@ofbizUrl>${replaceItemAction}</@ofbizUrl>';document.listreplform_${shoppingListItem.shoppingListItemSeqId}.submit();" class="buttontext">[${uiLabelMap.EcommerceReplaceWithVariation}]</a>
                              <br/>
                              <a href="javascript:document.listreplform_${shoppingListItem.shoppingListItemSeqId}.action='<@ofbizUrl>${addToCartAction}</@ofbizUrl>';document.listreplform_${shoppingListItem.shoppingListItemSeqId}.submit();" class="buttontext">[${uiLabelMap.CommonAdd}&nbsp;${shoppingListItem.quantity?string}&nbsp;${uiLabelMap.EcommerceVariationToCart}]</a>
                            </form>
                          <#else>
                            <a href="<@ofbizUrl>/additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if>?shoppingListId=${shoppingListItem.shoppingListId}&shoppingListItemSeqId=${shoppingListItem.shoppingListItemSeqId}&quantity=${shoppingListItem.quantity}&add_product_id=${shoppingListItem.productId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonAdd}&nbsp;${shoppingListItem.quantity?string}&nbsp;${uiLabelMap.EcommerceToCart}]</a>
                          </#if>
                        </td>
                      </tr>
                  </#list>
                  <tr><td colspan="6"><hr class='sepbar'></td></tr>
				  <tr>
					<td><div class="tabletext">&nbsp;</div></td>
					<td><div class="tabletext">&nbsp;</div></td>
					<#--<td><div class="tabletext">&nbsp;</div></td>-->
					<td><div class="tabletext">&nbsp;</div></td>
					<td nowrap align="right">
					  <div class="tableheadtext">${shoppingListItemTotal?string.currency}</div>
					</td>                      
					<td><div class="tabletext">&nbsp;</div></td>
				  </tr>
                </table>
            <#else>
                <div class='head2'>${uiLabelMap.EcommerceShoppingListEmpty}.</div>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br/>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.EcommerceShoppingListPriceTotals} - ${shoppingList.listName}</div>
          </td>  
          <td valign="middle" align="right">
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td align="left" width="5%">
          	<div class="tabletext">${uiLabelMap.EcommerceChildListTotalPrice}</div>
          </td>
          <td align="right" width="5%">
          	<div class="tabletext">${shoppingListChildTotal?string.currency}</div>
          </td>
          <td width="90%"><div class="tabletext">&nbsp;</div></td>
        </tr>
        <tr>
          <td align="left">
          	<div class="tabletext">${uiLabelMap.EcommerceListItemsTotalPrice}&nbsp;</div>
          </td>
          <td align="right">
          	<div class="tabletext">${shoppingListItemTotal?string.currency}</div>
          </td>
          <td><div class="tabletext">&nbsp;</div></td>
        </tr>
        <tr>
          <td align="left">
          	<div class="tableheadtext">${uiLabelMap.OrderGrandTotal}</div>
          </td>
          <td align="right">
          	<div class="tableheadtext">${shoppingListTotalPrice?string.currency}</div>
          </td>
          <td><div class="tabletext">&nbsp;</div></td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

	<#else>
		<#-- shoppingList was found, but belongs to a different party -->
		<div class="head2">${uiLabelMap.EcommerceShoppingListError} ID ${shoppingList.shoppingListId}) ${uiLabelMap.EcommerceListDoesNotBelong}.</div>
	</#if>
</#if>
