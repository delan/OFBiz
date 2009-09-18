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
Event.observe(window, 'load', function() {
    Event.observe($('costCentersSubmit'), 'click', processCostCenterData);
});
function processCostCenterData() {
    new Ajax.Request($('costCenters').action, {
        asynchronous: false,
        onSuccess: function(transport) {
            if (transport.responseText == "") {
                Effect.Appear('errorMessage', {duration: 0.0});
            } 
        }, parameters: $('costCenters').serialize(), requestHeaders: {Accept: 'application/json'}
    });
}
