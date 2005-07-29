<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     Leon Torres (leon@opensourcestrategies.com)
 *@author     Si Chen (sichen@opensourcestrategies.com)
-->
<?xml version="1.0" encoding="iso-8859-1"?>

<#-- Generates PDF of return invoice -->
<#-- A great XSL:FO tutorial is at http://www.xml.com/pub/a/2001/01/17/xsl-fo/ -->

<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <fo:layout-master-set>

    <fo:simple-page-master master-name="return-summary"
        margin-top="1in" margin-bottom="1in"
        margin-left="1in" margin-right="1in">
      <fo:region-body margin-top="3in" margin-bottom="0.5in"/>  <#-- main body with parties, list of returned items and total -->
      <fo:region-before extent="3in"/>  <#-- header with logo and date/returnId -->
      <fo:region-after extent="0.5in"/>  <#-- footer with page number and caption -->
    </fo:simple-page-master>

  </fo:layout-master-set>

  <fo:page-sequence master-reference="return-summary">


    <#-- header with logo and date/returnId -->


    <fo:static-content flow-name="xsl-region-before">
      <fo:block font-size="10pt">
      <fo:table>
        <fo:table-column column-width="2in"/>
        <fo:table-column column-width="1in"/>
        <fo:table-column column-width="3in"/>
        <fo:table-body>
        <fo:table-row>

        <fo:table-cell>
          <#-- TODO: find a way to share logo in /includes/ directory. -->
             ${screens.render("component://order/widget/ordermgr/OrderPrintForms.xml#CompanyLogo")}
        </fo:table-cell>

        <fo:table-cell/>

        <fo:table-cell>
          <fo:table><fo:table-column column-width="0.3in"/><fo:table-body><fo:table-row><fo:table-cell>
            <fo:table font-size="10pt">
            <fo:table-column column-width="1in"/>
            <fo:table-column column-width="1in"/>
            <fo:table-column column-width="1in"/>
            <fo:table-body>

            <fo:table-row>
              <fo:table-cell number-columns-spanned="3">
                <fo:block space-after="2mm" font-size="14pt" font-weight="bold" text-align="right">Return Summary</fo:block>
              </fo:table-cell>
            </fo:table-row>

            <fo:table-row>
              <fo:table-cell text-align="center" border-style="solid" border-width="0.2pt">
                <fo:block padding="1mm" font-weight="bold">Date</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center" border-style="solid" border-width="0.2pt">
                <fo:block padding="1mm" font-weight="bold">Number</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center" border-style="solid" border-width="0.2pt">
                <fo:block padding="1mm" font-weight="bold">Status</fo:block>
              </fo:table-cell>
            </fo:table-row>
                                  
            <fo:table-row>
              <fo:table-cell text-align="center" border-style="solid" border-width="0.2pt">
                <fo:block padding="1mm">${entryDate?string("yyyy-MM-dd")}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center" border-style="solid" border-width="0.2pt">
                <fo:block padding="1mm">${returnId}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center" border-style="solid" border-width="0.2pt">
                <fo:block padding="1mm">${currentStatus.description}</fo:block>
              </fo:table-cell>
            </fo:table-row>

          </fo:table-body>
          </fo:table>
        </fo:table-cell></fo:table-row></fo:table-body></fo:table>
      </fo:table-cell>

      </fo:table-row>
      </fo:table-body>
      </fo:table>
      </fo:block>

      <#-- return from and to -->

      <fo:block font-size="10pt" space-before="5mm">
        <fo:table>
          <fo:table-column column-width="2.75in"/>
          <fo:table-column column-width="0.5in"/>
          <fo:table-column column-width="2.75in"/>
          <fo:table-body>
          <fo:table-row>

            <fo:table-cell>
            <fo:table border-style="solid" border-width="0.2pt" height="1in">
              <fo:table-column column-width="2.75in"/>
              <fo:table-body>
                <fo:table-row><fo:table-cell border-style="solid" border-width="0.2pt" padding="1mm"><fo:block font-weight="bold">Return From</fo:block></fo:table-cell></fo:table-row>
                <fo:table-row><fo:table-cell padding="1mm">
                 <fo:block white-space-collapse="false"><#if postalAddressFrom?exists><#if postalAddressFrom.toName?has_content>${postalAddressFrom.toName}</#if><#if postalAddressFrom.attnName?has_content>
${postalAddressFrom.attnName}</#if>
${postalAddressFrom.address1}<#if postalAddressFrom.address2?has_content>
${postalAddressFrom.address2}<br/></#if>
${postalAddressFrom.city}<#if postalAddressFrom.stateProvinceGeoId?has_content>, ${postalAddressFrom.stateProvinceGeoId} </#if></#if></fo:block>

                </fo:table-cell></fo:table-row>
              </fo:table-body>
            </fo:table>
            </fo:table-cell>

            <fo:table-cell/>

            <fo:table-cell>
            <#if postalAddressTo?exists>
            <fo:table border-style="solid" border-width="0.2pt" height="1in">
              <fo:table-column column-width="2.75in"/>
              <fo:table-body>
                <fo:table-row><fo:table-cell padding="1mm" border-style="solid" border-width="0.2pt"><fo:block font-weight="bold">Return To</fo:block></fo:table-cell></fo:table-row>
                <fo:table-row><fo:table-cell padding="1mm">
                 <fo:block white-space-collapse="false"><#if postalAddressTo?exists><#if postalAddressTo.toName?has_content>${postalAddressTo.toName}</#if><#if postalAddressTo.attnName?has_content>
${postalAddressTo.attnName}</#if>
${postalAddressTo.address1}<#if postalAddressTo.address2?has_content>
${postalAddressTo.address2}<br/></#if>
${postalAddressTo.city}<#if postalAddressTo.stateProvinceGeoId?has_content>, ${postalAddressTo.stateProvinceGeoId} </#if></#if></fo:block>
                </fo:table-cell></fo:table-row>
              </fo:table-body>
            </fo:table>
            </#if>
            </fo:table-cell>
              
          </fo:table-row>
          </fo:table-body>
          </fo:table>
      </fo:block>

        <fo:table height="0.25in" space-before="5mm">
          <fo:table-column column-width="0.85in"/>
          <fo:table-column column-width="0.85in"/>
          <fo:table-column column-width="2in"/>
          <fo:table-column column-width="0.5in"/>
          <fo:table-column column-width="0.85in"/>
          <fo:table-column column-width="0.85in"/>
          <fo:table-body>
            <fo:table-row text-align="center" font-weight="bold">
              <fo:table-cell padding="1mm" border-style="solid" border-width="0.2pt" display-align="after"><fo:block>Order No.</fo:block></fo:table-cell>
              <fo:table-cell padding="1mm" border-style="solid" border-width="0.2pt" display-align="after"><fo:block>Product No.</fo:block></fo:table-cell>
              <fo:table-cell padding="1mm" border-style="solid" border-width="0.2pt" display-align="after"><fo:block>Description</fo:block></fo:table-cell>
              <fo:table-cell padding="1mm" border-style="solid" border-width="0.2pt" display-align="after"><fo:block>Qty</fo:block></fo:table-cell>
              <fo:table-cell padding="1mm" border-style="solid" border-width="0.2pt" display-align="after"><fo:block>Unit Price</fo:block></fo:table-cell>
              <fo:table-cell padding="1mm" border-style="solid" border-width="0.2pt" display-align="after"><fo:block>Amount</fo:block></fo:table-cell>
            </fo:table-row>
          </fo:table-body>
        </fo:table>

    </fo:static-content>


    <#-- footer.  Use it for standard boilerplate text. -->


    <fo:static-content flow-name="xsl-region-after">
      <#-- displays page number.  "theEnd" is an id of a fo:block at the very end -->    
      <fo:block space-before="5mm" font-size="10pt" text-align="center">Page <fo:page-number/> of <fo:page-number-citation ref-id="theEnd"/></fo:block>
    </fo:static-content>

  
    <#-- main body -->

    
    <fo:flow flow-name="xsl-region-body">

      <#-- Items returned -->
      
      <fo:block font-size="10pt">
        <fo:table border-style="solid" border-width="0.2pt" height="5in">
          <fo:table-column column-width="0.85in"/>
          <fo:table-column column-width="0.85in"/>
          <fo:table-column column-width="2in"/>
          <fo:table-column column-width="0.5in"/>
          <fo:table-column column-width="0.85in"/>
          <fo:table-column column-width="0.85in"/>
          <fo:table-body>

            <#-- each item -->
            <#assign total = 0.0/>
            <#list returnItems as returnItem>
              <fo:table-row>
                <fo:table-cell padding="1mm" font-size="8pt">
                  <fo:block>${returnItem.orderId}</fo:block>
                </fo:table-cell>
                <fo:table-cell padding="1mm" font-size="8pt">
                  <fo:block>
                    <#if returnItem.orderItemSeqId?exists>${returnItem.getRelatedOne("OrderItem").getString("productId")}</#if>
                  </fo:block>
                </fo:table-cell>
                <fo:table-cell padding="1mm"><fo:block wrap-option="wrap">${returnItem.description}</fo:block></fo:table-cell>
                <fo:table-cell padding="1mm" text-align="right"><fo:block>${returnItem.returnQuantity}</fo:block></fo:table-cell>
                <fo:table-cell padding="1mm" text-align="right"><fo:block>${returnItem.returnPrice?string.currency}</fo:block></fo:table-cell>
                <fo:table-cell padding="1mm" text-align="right"><fo:block>${(returnItem.returnPrice * returnItem.returnQuantity)?string.currency}</fo:block></fo:table-cell>
              </fo:table-row>
              <#assign total = total + returnItem.returnQuantity.doubleValue() * returnItem.returnPrice.doubleValue()/>
            </#list>

        </fo:table-body>
        </fo:table>
      </fo:block>

      <#-- total -->

        <fo:table space-before="5mm" font-size="10pt">
          <fo:table-column column-width="0.85in"/>
          <fo:table-column column-width="0.85in"/>
          <fo:table-column column-width="2in"/>
          <fo:table-column column-width="0.5in"/>
          <fo:table-column column-width="0.85in"/>
          <fo:table-column column-width="0.85in"/>
          <fo:table-body>
            <fo:table-row>
              <fo:table-cell/>
              <fo:table-cell/>
              <fo:table-cell/>
              <fo:table-cell/>
              <fo:table-cell padding="1mm" border-style="solid" border-width="0.2pt">
                <fo:block font-weight="bold" text-align="center">Total</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="right" padding="1mm" border-style="solid" border-width="0.2pt">
                <fo:block>${total?string.currency}</fo:block>
              </fo:table-cell>
            </fo:table-row>
          </fo:table-body>
        </fo:table>
      
      <fo:block id="theEnd"/>  <#-- marks the end of the pages and used to identify page-number at the end -->
    </fo:flow>

  </fo:page-sequence>
      
</fo:root>
