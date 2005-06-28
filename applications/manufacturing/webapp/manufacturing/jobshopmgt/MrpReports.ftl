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
 *@author     Jacopo Cappellato (tiz@sastau.it)
 *
-->

<#if mrpName?exists>
  <div class="head1">${uiLabelMap.ManufacturingMrpName}: ${mrpName?if_exists}</div>
  <!--
  <div><a href="<@ofbizUrl>MRPPRunsProductsByFeature.pdf?mrpName=${mrpName}&taskNamePar=O-LAV_01b&productCategoryIdPar=</@ofbizUrl>" class="buttontext" target="_report">[${uiLabelMap.ManufacturingMRPPRunsProductsByFeature}]</a></div>
  <div><a href="<@ofbizUrl>PRunsComponentsByFeature.pdf?showLocation=Y&mrpName=${mrpName}&taskNamePar=O-PREL_L&productCategoryIdPar=PANNELLI</@ofbizUrl>" class="buttontext" target="_report">[${uiLabelMap.ManufacturingPRunsComponentsByFeature}]</a></div>
  <div><a href="<@ofbizUrl>PRunsComponentsByFeature.pdf?showLocation=N&mrpName=${mrpName}&taskNamePar=O-PREL_L&productCategoryIdPar=PEZZI</@ofbizUrl>" class="buttontext" target="_report">[${uiLabelMap.ManufacturingPRunsComponentsByFeature1}]</a></div>
  <div><a href="<@ofbizUrl>PRunsProductsStacks.pdf?mrpName=${mrpName}&taskNamePar=O-LAV_01b&productCategoryIdPar=</@ofbizUrl>" class="buttontext" target="_report">[${uiLabelMap.ManufacturingPRunsProductsStacks}]</a></div>
  -->
</#if>

