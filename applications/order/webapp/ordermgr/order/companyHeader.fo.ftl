        <fo:table font-size="8pt"  font-family="sans-serif">
           <fo:table-column column-width="4.5in"/>
           <fo:table-column column-width="2in"/>
            <fo:table-body>
              <fo:table-row >
                <fo:table-cell>
					<fo:block>
					<#if logoImageUrl?has_content><fo:external-graphic src="${logoImageUrl}" overflow="hidden" height="80px"/></#if>
					</fo:block>
                </fo:table-cell>
                <fo:table-cell>
                  <fo:block>${companyName?if_exists}</fo:block>
                  <fo:block>${postalAddress.address1?if_exists}</fo:block>
                  <fo:block>${postalAddress.postalCode?if_exists} ${postalAddress.city?if_exists} , ${countryName?if_exists} </fo:block>
                  <fo:table>
                    <fo:table-column column-width="15mm"/>
                    <fo:table-column column-width="25mm"/>
                    <fo:table-body>
                                  
                    <#if phone?exists>
                    <fo:table-row>
                      <fo:table-cell><fo:block>Tel:</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block>${phone.countryCode?if_exists}-${phone.areaCode?if_exists}-${phone.contactNumber?if_exists}</fo:block></fo:table-cell>
                    </fo:table-row>
                    </#if>

                    <#if email?exists>
                    <fo:table-row>
                      <fo:table-cell><fo:block>Email:</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block>${email.infoString}</fo:block></fo:table-cell>
                    </fo:table-row>
                    </#if>

                    <#if website?exists>
                    <fo:table-row>
                      <fo:table-cell><fo:block>Website:</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block>${website.infoString}</fo:block></fo:table-cell>
                    </fo:table-row>
                    </#if>

                    <#if eftAccount?exists>
                    <fo:table-row>
                      <fo:table-cell><fo:block>Bank:</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block>${eftAccount.bankName}</fo:block></fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                      <fo:table-cell><fo:block>Routing:</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block>${eftAccount.routingNumber}</fo:block></fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                      <fo:table-cell><fo:block>AccntNr:</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block>${eftAccount.accountNumber}</fo:block></fo:table-cell>
                    </fo:table-row>
                    </#if>

                  </fo:table-body>
                </fo:table>             
                </fo:table-cell>
            </fo:table-row>
          </fo:table-body>
        </fo:table>
 