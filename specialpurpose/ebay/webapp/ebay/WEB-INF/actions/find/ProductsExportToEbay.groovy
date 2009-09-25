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
import org.ofbiz.webapp.website.WebSiteWorker;

webSite = WebSiteWorker.getWebSite(request);
productStoreId = null; 
if (webSite) {
    productStoreId = webSite.productStoreId;
    context.productStoreId = productStoreId;
    eBayConfig = delegator.findOne("EbayConfig", [productStoreId : productStoreId], false);
    context.customXml = eBayConfig.customXml;
    context.webSiteUrl = webSite.getString("standardContentPrefix");
    
    categoryCode = parameters.categoryCode;
    context.categoryCode = categoryCode; 
    userLogin = parameters.userLogin;
    
    if (productStoreId) {
        results = dispatcher.runSync("getEbayCategories", [categoryCode : categoryCode, userLogin : userLogin, productStoreId : productStoreId]);
    }
    
    if (results.categories) {
        context.categories = results.categories;
    }
    
    if (categoryCode) {
        if (!"Y".equals(categoryCode.substring(0, 1)) && !"".equals(categoryCode)) {
            context.hideExportOptions = "Y";
        } else {
            context.hideExportOptions = "N";
        }
    } else {
        context.hideExportOptions = "N";
    }    
}
