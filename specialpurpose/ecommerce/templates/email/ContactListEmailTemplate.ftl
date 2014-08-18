<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<html xmlns="http://www.w3.org/1999/xhtml">
    <body>
        <div>
            ${content}
        </div>
        <#assign verifyUrl = baseEcommerceSecureUrl + "updateContactListPartyNoUserLogin" />
        <form method="post" action="${verifyUrl}">
            <fieldset>
                <label>E-mail: ${emailAddress}</label>
                <input type="hidden" name="contactListId" value="${contactListId}" />
                <input type="hidden" name="partyId" value="${partyId}" />
                <input type="hidden" name="preferredContactMechId" value="${preferredContactMechId!}" />
                <input type="hidden" name="fromDate" value="${fromDate}" />
                <input type="hidden" name="statusId" value="CLPT_UNSUBS_PENDING" />
                <input type="hidden" name="optInVerifyCode" value="${optInVerifyCode!}" />
                <input type="submit" name="submitButton" value="Click here to unsubscribe your newsletter subscription." />
            </fieldset>
        </form>
    </body>
</html>
