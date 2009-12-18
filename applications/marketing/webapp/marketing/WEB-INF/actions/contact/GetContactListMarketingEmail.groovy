/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// figure out the MARKETING_EMAIL of the ContactList owner, for setting in the send email link
if (!contactList && contactListId) {
    contactList = delegator.findOne("ContactList", [contactListId : "contactListId"], true);
}
if (contactList) {
    ownerParty = contactList.getRelatedOne("OwnerParty");
    if (ownerParty) {
        contactMechs = ownerParty.getRelatedByAnd("PartyContactMechPurpose", [contactMechPurposeTypeId : "MARKETING_EMAIL"]);
        if (!contactMechs) {
            contactMechs = ownerParty.getRelatedByAnd("PartyContactMechPurpose", [contactMechPurposeTypeId : "PRIMARY_EMAIL"]);
        }
        
        if (contactMechs) {
            context.marketingEmail = contactMechs.get(0);
            //context.contactMechIdFrom = context.marketingEmail.contactMechId;
        }
    }
}
