<#--
$Id: $

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

@author     Brett G. Palmer (brettgplamer@gmail.com)

-->

<?xml version="1.0" encoding="iso-8859-1"?>

<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <fo:layout-master-set>
    <fo:simple-page-master master-name="all-checks">
      <fo:region-body margin="1in" padding="6pt"/>
    </fo:simple-page-master>
    <fo:page-sequence-master master-name="check-sequence">
      <fo:repeatable-page-master-reference master-reference="all-checks"/>
    </fo:page-sequence-master>
  </fo:layout-master-set>

<fo:page-sequence master-reference="all-checks">
<fo:flow flow-name="xsl-region-body">
	<fo:table>
	    <fo:table-column column-width="2in"/>
        <fo:table-column column-width="2in"/>
        <fo:table-column column-width="2in"/>
        <fo:table-column column-width="2in"/>
        <fo:table-body>
            <fo:table-row>
              <fo:table-cell>
                 <fo:block font-weigh="bold">Payor Address
               							   1234 S. 7880 E. 
               							   Somewhere, CA 84002</fo:block>
              </fo:table-cell>
              <fo:table-cell/>
              <fo:table-cell>
              	<fo:block>Date: 08/09/2005</fo:block>
              </fo:table-cell>
              <fo:table-cell/>
            </fo:table-row>
            <fo:table-row> <fo:table-cell number-rows-spanned="2"/> </fo:table-row>
            <fo:table-row> <fo:table-cell number-rows-spanned="2"/> </fo:table-row>
            <fo:table-row>
              <fo:table-cell>
                 <fo:block font-weigh="bold">Pay To:</fo:block>
              </fo:table-cell>
              <fo:table-cell number-rows-spanned="2">
              	<fo:block>
         			<fo:inline text-decoration="underline">OFBiz Consultants</fo:inline>
         		</fo:block>
              </fo:table-cell>
              <fo:table-cell>
              	<fo:block border-color="black" border-style="groove">
         			<fo:inline>$10,000.00</fo:inline>
         		</fo:block>
              </fo:table-cell>
              <fo:table-cell/>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell/>
              <fo:table-cell number-rows-spanned="2">
              	<fo:block>
         			<fo:inline text-decoration="underline">Ten Thousands Dollars</fo:inline>
         		</fo:block>
              </fo:table-cell>
              <fo:table-cell/>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell>
                 <fo:block font-weigh="bold">For:</fo:block>
              </fo:table-cell>
              <fo:table-cell number-rows-spanned="2">
              	<fo:block>
         			<fo:inline text-decoration="underline">Payment for services</fo:inline>
         		</fo:block>
              </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>

</fo:flow>
</fo:page-sequence>

</fo:root>
