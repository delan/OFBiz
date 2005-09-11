<#--
 *      Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *      Copyright (c) 2005 Undersun Consulting, LLC
 *      Copyright (c) 2005 Open Source Strategies, Inc. 
 *  
 *      PLEASE READ THIS LICENSE CAREFULLY.  THE TERMS ARE SUBSTANTIALLY 
 *      DIFFERENT THAN THOSE OF THE MIT PUBLIC LICENSE OR ANY OTHER 
 *      OSI-APROVED OPEN SOURCE LICENSE.
 *  
 *      By using, copying, distributing, or modifying this software, you 
 *  indicate your acceptance of this license as the “licensee” and all its 
 *  terms and conditions for using, copying, and distributing, or modifying 
 *  the program.  Nothing other than this license grants you permission to 
 *  modify or distribute the program.  If you do not accept these terms and 
 *  conditions, do not use, copy, distribute, or modify this program, as 
 *  such acts are prohibited by law unless this license is accepted.
 *  
 *      Permission is hereby granted to the licensee to use this software 
 *  for any purpose and to modify this software for such within the original 
 *  organization for which it was purchased.  Permission is not granted to 
 *  the licensee to copy, modify, merge, publish, distribute, sublicense, or 
 *  sell copies of this program.  Modifications you have made to the software, 
 *  as long as they are kept for internal use only, do not have to be 
 *  published or otherwise made publicly available.
 *  
 *      It is the intent of the copyrightholders to release, at a future 
 *  date, this software under an open source license identical to that of 
 *  the Open For Business Project, when the initial funding requirements 
 *  for the software have been met.  When this software is released under 
 *  such an open source license, the terms of this license will no longer 
 *  apply.  By accepting this license, you also accept the open source 
 *  license which replaces it.
 *  
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY   
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *  
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Rev: 159 $
 *@since      2.2
-->

<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<#if fixedAsset?has_content>
    <div class='tabContainer'>
        <a href="<@ofbizUrl>/EditFixedAsset?fixedAssetId=${fixedAsset.fixedAssetId}</@ofbizUrl>" class="${selectedClassMap.EditFixedAsset?default(unselectedClassName)}">${uiLabelMap.AccountingFixedAsset}</a>
<!--   <a href="<@ofbizUrl>/ListFixedAssetRollUp?fixedAssetId=${fixedAsset.fixedAssetId}</@ofbizUrl>" class="${selectedClassMap.ListFixedAssetRollUp?default(unselectedClassName)}">${uiLabelMap.AccountingFixedAssetRollUp}</a>
        <a href="<@ofbizUrl>/ListFixedAssetParties?fixedAssetId=${fixedAsset.fixedAssetId}</@ofbizUrl>" class="${selectedClassMap.ListFixedAssetParties?default(unselectedClassName)}">${uiLabelMap.AccountingFixedAssetParties}</a> -->
        <a href="<@ofbizUrl>/ListFixedAssetProducts?fixedAssetId=${fixedAsset.fixedAssetId}</@ofbizUrl>" class="${selectedClassMap.ListFixedAssetProducts?default(unselectedClassName)}">${uiLabelMap.AccountingFixedAssetProducts}</a>
        <a href="<@ofbizUrl>/ListFixedAssetCalendar?fixedAssetId=${fixedAsset.fixedAssetId}</@ofbizUrl>" class="${selectedClassMap.ListFixedAssetCalendar?default(unselectedClassName)}">${uiLabelMap.AccountingFixedAssetCalendar}</a>
    </div>
</#if>
