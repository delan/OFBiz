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

import org.apache.ofbiz.product.store.*

processResult = parameters.processResult
if (processResult != null) {
   if (!processResult) {
       request.setAttribute("_ERROR_MESSAGE_", "<li>There was a problem linking your cards. Please check the numbers and try again.</li>")
       request.removeAttribute("_EVENT_MESSAGE_")
   } else {
       request.setAttribute("_EVENT_MESSAGE_", "<li>Thank-you. Your gift card account is now linked.</li>")
   }
}

context.userLogin = userLogin
context.paymentProperties = ProductStoreWorker.getProductStorePaymentProperties(request, "GIFT_CARD", null, true)
