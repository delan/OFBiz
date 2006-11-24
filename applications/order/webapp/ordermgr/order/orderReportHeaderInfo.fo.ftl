<#--

Copyright 2001-2006 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
-->
<#escape x as x?xml>
                  <fo:table>
                    <fo:table-column column-width="1.5in"/>
                    <fo:table-column column-width="1.5in"/>
                    <fo:table-body>
                    <fo:table-row>
                      <fo:table-cell>
                         <fo:block number-columns-spanned="2" font-weight="bold">${orderHeader.getRelatedOne("OrderType").get("description",locale)} ${uiLabelMap.OrderOrder}</fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                    
                    <fo:table-row>
                      <fo:table-cell><fo:block>${uiLabelMap.OrderDateOrdered}</fo:block></fo:table-cell>
                      <#assign dateFormat = Static["java.text.DateFormat"].LONG>
                      <#assign orderDate = Static["java.text.DateFormat"].getDateInstance(dateFormat).format(orderHeader.get("orderDate"))>
                      <fo:table-cell><fo:block>${orderDate}</fo:block></fo:table-cell>
                    </fo:table-row>
                                  
                    <fo:table-row>
                      <fo:table-cell><fo:block>${uiLabelMap.OrderOrder} #</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block>${orderId}</fo:block></fo:table-cell>
                    </fo:table-row>

                    <fo:table-row>
                      <fo:table-cell><fo:block>${uiLabelMap.OrderCurrentStatus}</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block font-weight="bold">${currentStatus.get("description",locale)}</fo:block></fo:table-cell>
                    </fo:table-row>
                  </fo:table-body>
                </fo:table>
