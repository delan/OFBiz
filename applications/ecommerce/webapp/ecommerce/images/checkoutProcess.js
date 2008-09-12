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
var isCartStepValidate = false;
var isShipStepValidate = false;
var isShipOptionStepValidate = false;
var isBillStepValidate = false;

Event.observe(window, 'load', function() {
    // Cart
    var validateCart = new Validation('cartForm', {immediate: true, onSubmit: false});
    var validateShip = new Validation('shippingForm', {immediate: true, onSubmit: false});
    var validateShipOption = new Validation('shippingOptionForm', {immediate: true, onSubmit: false});
    var validateBill = new Validation('billingForm', {immediate: true, onSubmit: false});    
    Event.observe($('editShipping'), 'click', function() {
        if (validateCart.validate()) {
            Effect.Fade('editShipping', {duration: 0.0});
            // Effect.Appear('processingShipping', {duration: 0.0});
            displayShippingPanel();
            isCartStepValidate = true;
        }
    });

    Event.observe($('openCartPanel'), 'click', function() {
        displayEditCartPanel();
    });

    // Shipping
    Event.observe($('editShippingOptions'), 'click', function() {
        if (isCartStepValidate) {
            if (validateShip.validate()) {
                Effect.Fade('editShippingOptions', {duration: 0.0});
                Effect.Appear('processingShippingOptions', {duration: 0.0});
                processShippingAddress();
                displayShippingOptionPanel();
                isShipStepValidate = true;
            }
        }
    });

    Event.observe($('openShippingPanel'), 'click', function() {
        if (isCartStepValidate) {
            if (isShipStepValidate) {
                displayEditShippingPanel();
            }
        }
    });

    // Shipping Options
    Event.observe($('editBilling'), 'click', function() {
        if (isCartStepValidate && isShipStepValidate) {
            if (validateShipOption.validate()) {
                Effect.Fade('editBilling', {duration: 0.0});
                Effect.Appear('processingBilling', {duration: 0.0});
                setShippingOption();
                displayBillingPanel();
                isShipOptionStepValidate = true;
            }
        }
    });

    Event.observe($('openShippingOptionPanel'), 'click', function() {
        if (isCartStepValidate && isShipStepValidate && isShipOptionStepValidate) {
            displayEditShippingOptionPanel();
        }
    });

    // Billing
    Event.observe($('openBillingPanel'), 'click', function() {
        if (isBillStepValidate) {
            displayEditBillingPanel();
        }  
    });

    Event.observe($('openOrderSubmitPanel'), 'click', function() {
        if (isCartStepValidate && isShipStepValidate && isShipOptionStepValidate) {
            if (validateBill.validate()) {
                Effect.Fade('openOrderSubmitPanel', {duration: 0.0});
                Effect.Appear('processingOrderSubmitPanel', {duration: 0.0});
                processBillingAndPayment();
                displayOrderSubmitPanel();
                isBillStepValidate = true;
            }
        }
    });
    
    // For Billing Address Same As Shipping
    Event.observe('useShippingAddressForBilling', 'click', function() {
        useShippingAddressForBillingToggle();
        validateBill.validate();
    });
    
    // Initiate Observing Edit Cart Events
    initCartProcessObservers();

    Event.observe('processOrderButton', 'click', processOrder);

    // Get associate states for billing panel
    Event.observe($('billToStateProvinceGeoId'), 'focus', function(){
      getAssociatedBillingStateList('billingForm', 'billToStateProvinceGeoId');
    });
});

// Check server side error
function getServerError(data) {
    var serverErrorHash = [];
    var serverError = "";
    if (data._ERROR_MESSAGE_LIST_ != undefined) {
        serverErrorHash = data._ERROR_MESSAGE_LIST_;
        serverErrorHash.each(function(error) {
            serverError += error.message;
        });
    }
    if (data._ERROR_MESSAGE_ != undefined) {
        serverError = data._ERROR_MESSAGE_; 
    }
    return serverError;    
}

// Cart
function displayShippingPanel() {

    Effect.BlindUp('editCartPanel', {duration: 0.0});
    Effect.BlindDown('cartSummaryPanel');
    if($('shippingSummaryPanel').visible()) {
        Effect.BlindUp('shippingSummaryPanel', {duration: 0.0});
    }
    Effect.BlindDown('editShippingPanel', {duration: 0.0});
    Effect.Appear('editShippingOptions');
}

function displayEditShippingPanel() {

    if (!$('editShippingPanel').visible()) {
    	if ($('shippingSummaryPanel').visible()) {
            Effect.BlindUp('shippingSummaryPanel', {duration: 0.0});
    	}
            Effect.BlindDown('cartSummaryPanel', {duration: 0.0});
            Effect.BlindDown('editShippingPanel');
            Effect.Fade('processingShipping', {duration: 0.0});
            Effect.Appear('editShippingOptions');
    }
    if($('editCartPanel').visible()) {
        Effect.BlindUp('editCartPanel', {duration: 0.0});
        Effect.Fade('editShipping');
    }
    if ($('editShippingOptionPanel').visible()) {
        Effect.BlindUp('editShippingOptionPanel', {duration: 0.0});
        Effect.BlindDown('shippingOptionSummaryPanel', {duration: 0.0});
        Effect.Fade('editBilling')
    }
    if ($('editBillingPanel').visible()) {
        Effect.BlindUp('editBillingPanel', {duration: 0.0});
        Effect.BlindDown('billingSummaryPanel', {duration: 0.0});
        Effect.Fade('openOrderSubmitPanel');
    }
    if ($('orderSubmitPanel').visible()) {
        Effect.BlindUp('orderSubmitPanel', {duration: 0.0});
    }
}

function displayEditCartPanel() {

    if ($('orderSubmitPanel').visible()) {
        Effect.BlindUp('orderSubmitPanel', {duration: 0.0});
    }
    if ($('editBillingPanel')) {
        Effect.BlindUp('editBillingPanel', {duration: 0.0});
        Effect.BlindDown('billingSummaryPanel', {duration: 0.0});
        Effect.Fade('openOrderSubmitPanel');
    }
    if ($('editShippingPanel').visible()) {
        Effect.BlindUp('editShippingPanel', {duration: 0.0});
        Effect.BlindDown('shippingSummaryPanel', {duration: 0.0});
        Effect.Fade('editShippingOptions', {duration: 0.0});
    }
    if ($('editShippingOptionPanel').visible()) {  
        Effect.BlindUp('editShippingOptionPanel', {duration: 0.0});
        Effect.BlindDown('shippingOptionSummaryPanel', {duration: 0.0});
        Effect.Fade('editBilling', {duration: 0.0});
    }
    if (!$('editCartPanel').visible() ) {
        Effect.BlindUp('cartSummaryPanel', {duration: 0.0});
        Effect.BlindDown('editCartPanel');
        Effect.Appear('editShipping');
    }
}

// Shipping
function displayShippingOptionPanel() {
    if ($('shippingOptionSummaryPanel').visible()) {
        Effect.BlindUp('shippingOptionSummaryPanel', {duration: 0.0});
    }
    Effect.BlindUp('editShippingPanel', {duration: 0.0});
    Effect.BlindDown('shippingSummaryPanel', {duration: 0.0});
    setDataInShippingCompleted();
    Effect.BlindDown('shippingCompleted', {duration: 0.0});
    Effect.BlindDown('editShippingOptionPanel', {duration: 0.0});
    Effect.Fade('processingShippingOptions', {duration: 0.0});
    Effect.Appear('editBilling', {duration: 0.0});
}

function displayEditShippingOptionPanel() {

    if ($('editCartPanel').visible()) {
        Effect.BlindUp('editCartPanel', {duration: 0.0});
        Effect.BlindDown('cartSummaryPanel', {duration: 0.0});
        Effect.Fade('editShipping', {duration: 0.0});
    }
    if ($('editShippingPanel').visible()) {
        Effect.BlindUp('editShippingPanel', {duration: 0.0});
        setDataInShippingCompleted();
        Effect.BlindDown('shippingCompleted', {duration: 0.0});
        Effect.BlindDown('shippingSummaryPanel', {duration: 0.0});
        Effect.Fade('editShippingOptions', {duration: 0.0});
    }
    if ($('editBillingPanel')) {
        Effect.BlindDown('billingSummaryPanel');
        Effect.BlindUp('editBillingPanel', {duration: 0.0});
        Effect.Fade('openOrderSubmitPanel', {duration: 0.0});
    }
    if ($('orderSubmitPanel').visible()) {
        Effect.BlindUp('orderSubmitPanel');
    }
    if (!$('editShippingOptionPanel').visible() && isShipStepValidate) {
        if ($('shippingOptionSummaryPanel').visible()) {
            Effect.BlindUp('shippingOptionSummaryPanel', {duration: 0.0});
        }
        Effect.BlindDown('editShippingOptionPanel');
        Effect.Appear('editBilling');
        Effect.Fade('processingShippingOptions');
    }
}

// Billing
function displayBillingPanel() {
    if ($('billingSummaryPanel').visible()) {
        Effect.BlindUp('billingSummaryPanel', {duration: 0.0});
    }
    Effect.BlindUp('editShippingOptionPanel', {duration: 0.0});
    Effect.BlindDown('shippingOptionSummaryPanel', {duration: 0.0});
    setDataInShippingOptionCompleted();
    Effect.BlindDown('shippingOptionCompleted', {duration: 0.0});
    Effect.BlindDown('editBillingPanel', {duration: 0.0});
    
    Effect.Appear('openOrderSubmitPanel', {duration: 0.0});
    Effect.Fade('processingBilling', {duration: 0.0});
}

function displayEditBillingPanel() {

    if (!$('editBillingPanel').visible() && isShipOptionStepValidate) {
        Effect.BlindUp('billingSummaryPanel', {duration: 0.0});
        Effect.BlindDown('editBillingPanel');
        Effect.Appear('openOrderSubmitPanel');
        Effect.Fade('processingOrderSubmitPanel');
    }
    if ($('editCartPanel').visible()) {
        Effect.BlindUp('editCartPanel', {duration: 0.0});
        Effect.BlindDown('cartSummaryPanel', {duration: 0.0});
        Effect.Fade('editShipping');
    }
    if ($('editShippingPanel').visible()) {
        Effect.BlindUp('editShippingPanel', {duration: 0.0});
        Effect.BlindDown('shippingCompleted');
        Effect.BlindDown('shippingSummaryPanel', {duration: 0.0});
        Effect.Fade('editShippingOptions');
    }
    setDataInShippingOptionCompleted();
    if ($('editShippingOptionPanel').visible()) {
        Effect.BlindUp('editShippingOptionPanel', {duration: 0.0});
        Effect.BlindUp('orderSubmitPanel', {duration: 0.0});
        Effect.BlindDown('shippingOptionCompleted');
        Effect.BlindDown('shippingOptionSummaryPanel', {duration: 0.0});
        Effect.Fade('editBilling');
    }
    if ($('orderSubmitPanel')) {
        Effect.BlindUp('orderSubmitPanel', {duration: 0.0});
    }
}

// Order Submit
function displayOrderSubmitPanel() {

    if (!$('orderSubmitPanel').visible() && isBillStepValidate) {
        Effect.BlindUp('editBillingPanel', {duration: 0.0});
        Effect.BlindDown('billingSummaryPanel', {duration: 0.0});
        setDataInBillingCompleted();
        Effect.BlindDown('billingCompleted', {duration: 0.0});
        Effect.BlindDown('orderSubmitPanel', {duration: 0.0});
        Effect.Fade('processingBilling', {duration: 0.0});
        Effect.Fade('processingOrderButton', {duration: 0.0});
        Effect.Appear('processOrderButton', {duration: 0.0});
        Effect.Fade('processingOrderSubmitPanel', {duration: 0.0});
    }
}

function processShippingAddress() {
    createUpdateCustomerAndShippingAddress();
    if (isShipStepValidate) {
        getShipOptions();
    }
}

function createUpdateCustomerAndShippingAddress() {
    new Ajax.Request('createUpdateShippingAddress', {
        asynchronous: false, 
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            var serverError = getServerError(data);
            if (serverError != "") {
                Effect.Appear('shippingFormServerError');
                $('shippingFormServerError').update(serverError);
                isShipStepValidate = false;
                Effect.Appear('editShippingOptions', {duration: 0.0});
                Effect.Fade('processingShippingOptions', {duration: 0.0});
            } else {
                Effect.Fade('shippingFormServerError');
                isShipStepValidate = true;
                // Process Shipping data response.
                $('shipToPartyId').value = data.partyId;
                $('billToPartyId').value = data.partyId;
                $('shipToContactMechId').value = data.shipToContactMechId;
                $('shipToPhoneContactMechId').value = data.shipToPhoneContactMechId;
                $('emailContactMechId').value = data.emailContactMechId;
                $('completedShippingMethod').update(data.shippingDescription);
            }
        }, parameters: $('shippingForm').serialize(), requestHeaders: {Accept: 'application/json'}
    });
}

function getShipOptions() {
    var shipOptions = null;
    var optionList = [];
    if ($F('shipMethod') == "" || $F('shipMethod') == null) {
	    new Ajax.Request('getShipOptions', {
            asynchronous: false,
            onSuccess: function(transport) {
                var data = transport.responseText.evalJSON(true);
                var serverError = getServerError(data);
                if (serverError != "") {
                    Effect.Appear('shippingFormServerError');
                    $('shippingFormServerError').update(serverError);
                    isShipStepValidate = false;
                } else {
                    Effect.Fade('shippingFormServerError');
                    isShipStepValidate = true;
                    shipOptions = data.shippingOptions;
                    shipOptions.each( function(shipOption) {
                        optionList.push("<option value = " + shipOption.shippingMethod + " > " + shipOption.shippingDesc + " </option>");
                    });
                    $('shipMethod').update(optionList);
                }
            }, requestHeaders: {Accept: 'application/json'}
        });
    }
}

function setDataInShippingCompleted() {
    var fullName = $('firstName').value + " " +$('lastName').value;
    var extension = "";
    if ($F('shipToExtension')) {
        extension = "-" + $F('shipToExtension');
    }
    var shippingContactPhoneNumber = $F('shipToCountryCode')+ "-" + $F('shipToAreaCode') 
            + "-" + $F('shipToContactNumber') + extension;
    $('completedShipToAttn').update("Attn: " + fullName);
    $('completedShippingContactNumber').update(shippingContactPhoneNumber);
    $('completedEmailAddress').update($('emailAddress').value);
    $('completedShipToAddress1').update($F('shipToAddress1'));
    $('completedShipToAddress2').update($('shipToAddress2').value);
    var shipToGeo = $('shipToCity').value+","+$('shipToStateProvinceGeoId').value +" "+$('shipToCountryGeoId').value+" "+$('shipToPostalCode').value;
    $('completedShipToGeo').update(shipToGeo);
    // set shipToContactMechId in Billing form.
    $('shipToContactMechIdInBillingForm').value = $F('shipToContactMechId');
}

// Shipping option
var shipTotal = null;
var shipMethod = null;
function setShippingOption() {
    new Ajax.Request('setShippingOption', {
        asynchronous: false,
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            shipMethod = data.shippingDescription;
            shipTotal = data.shippingTotal;
            var serverError = getServerError(data);
            if(serverError != "") {
                Effect.Appear('shippingOptionFormServerError');
                $('shippingOptionFormServerError').update(serverError);
                isShipOptionStepValidate = false;
                Effect.Appear('editBilling', {duration: 0.0});
                Effect.Fade('processingBilling', {duration: 0.0});
            } else {
                Effect.Fade('shippingOptionFormServerError');
                isShipOptionStepValidate = true;
                $('shippingDescription').value = data.shippingDescription;
                $('shippingTotal').value = data.shippingTotal;
                $('cartGrandTotal').value = data.cartGrandTotal;
                $('totalSalesTax').value = data.totalSalesTax; 
            }
        }, parameters: $('shippingOptionForm').serialize(), requestHeaders: {Accept: 'application/json'}
    });
    updateCartData();
}

function setDataInShippingOptionCompleted() {
    var shipMethodSelected = [];
    var shipOptions = $A($('shipMethod').options);
    shipOptions.each( function(shipOption) {
        if (shipOption.selected) {
            shipMethodSelected = shipOption.text.split('-');
        }
    });
    $('selectedShipmentOption').update(shipMethodSelected[0]);
}

// Billing
function useShippingAddressForBillingToggle() {
    if ($('useShippingAddressForBilling').checked) {
        $('billToAddress1').value = $F('shipToAddress1');
        $('billToAddress2').value = $F('shipToAddress2');
        $('billToCity').value = $F('shipToCity');
        $('billToPostalCode').value = $F('shipToPostalCode');
        $('billToCountryGeoId').value = $F('shipToCountryGeoId');
        $('billToStateProvinceGeoId').update("<option value = " + $F('shipToStateProvinceGeoId') + " > " + $('shipToStateProvinceGeo').value + " </option>");
        Effect.BlindUp($('billingAddress'), {duration: 0.3});
    } else {
        Effect.BlindDown($('billingAddress'), {duration: 0.3});
    }
}

function processBillingAndPayment() {
    new Ajax.Request('createUpdateBillingAndPayment', {
        asynchronous: false, 
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            var serverError = getServerError(data);
            if(serverError != "") {
                Effect.Appear('billingFormServerError');
                $('billingFormServerError').update(serverError);
                isBillStepValidate = false;
                Effect.Appear('openOrderSubmitPanel', {duration: 0.0});
                Effect.Fade('processingOrderSubmitPanel', {duration: 0.0});                
            } else {
                Effect.Fade('billingFormServerError');
                isBillStepValidate = true;
                $('billToContactMechId').value = data.billToContactMechId;
                $('paymentMethodId').value = data.paymentMethodId;
                $('billToPhoneContactMechId').value = data.billToPhoneContactMechId;
            }
        }, parameters: $('billingForm').serialize(), requestHeaders: {Accept: 'application/json'}
    });
}

function setDataInBillingCompleted() {
    var fullName = $F('firstNameOnCard') + " " +$F('lastNameOnCard');
    $('completedBillToAttn').update("Attn: " + fullName);
    var extension = "";
    if ($F('billToExtension')) {
        extension = "-" + $F('billToExtension');
    }
    var billToPhoneNumber = $F('billToCountryCode') + "-" + $F('billToAreaCode') + "-" + $F('billToContactNumber') + extension;
    $('completedBillToPhoneNumber').update(billToPhoneNumber);
    var cardNumber = "CC#:XXXXXXXXXXXX"+$F('cardNumber').gsub('-','').slice(12,16);
    $('completedCCNumber').update(cardNumber);
    var expiryDate = "Expires:"+$F('expMonth')+"/"+$F('expYear');
    $('completedExpiryDate').update(expiryDate);
    $('completedBillToAddress1').update($F('billToAddress1'));
    $('completedBillToAddress2').update($F('billToAddress2'));
    var billToGeo = $F('billToCity')+","+$F('billToStateProvinceGeoId') +" "+$F('billToCountryGeoId')+" "+$F('billToPostalCode');
    $('completedBillToGeo').update(billToGeo);
    $('paymentMethod').update($F('paymentMethodTypeId'));
    $('billToContactMechIdInShipingForm').value = $F('billToContactMechId');
}

function initCartProcessObservers() {
    var cartForm = $('cartForm');
    Event.observe($('productPromoCode'), 'change', addPromoCode);
    var inputs = cartForm.getInputs('text');
    inputs.each(function(e) {
        if(e.id != 'productPromoCode') {
            Event.observe(e, 'change', cartItemQtyChanged);
        }
    });
    var removeLinks = cartForm.getElementsByTagName('a');
    var links = $A(removeLinks);
    links.each( function(e) {
        Event.observe(e, 'click', removeItem);
    });
    if ($('initializedCompletedCartDiscount') != undefined && $('initializedCompletedCartDiscount').value == 0) {
        $('completedCartDiscountRow').hide();
    }    
}

function addPromoCode() {
    new Ajax.Request('silentAddPromoCode', {
        asynchronous: false, 
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            var serverError = getServerError(data);
            if(serverError != "") {
                Effect.Appear('cartFormServerError');
                $('cartFormServerError').update(serverError);
            } else {
                Effect.Fade('cartFormServerError');
                updateCartData();
            }
        },
        parameters: {productPromoCodeId:$F('productPromoCode')}
    });
}

function getProductLineItemIndex(event, productId) {
    var itemIndex = null;
    var productIdParam = "productId=" + productId;
    var formValues = $('cartForm').serialize() + "&" + productIdParam;
    new Ajax.Request('getShoppingCartItemIndex', {
        asynchronous: false, 
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            itemIndex = data.itemIndex;
        },
        parameters: formValues
    });
    return itemIndex;
}

function removeItem(event) {
    var removeElement = Event.element(event);
    var elementId = removeElement.id;
    var qtyId = elementId.sub('remove_', 'qty_');
    var productIdElementId =  elementId.sub('remove_', 'cartLineProductId_');
    var productId = $(productIdElementId).value;
    var itemIndex = getProductLineItemIndex(event,productId);
    var formValues = "update_" + itemIndex + "= 0";
    updateCartData(qtyId, formValues, 0, itemIndex); 
}

function cartItemQtyChanged(event) {
    var qtyElement = Event.element(event);
    var elementId = qtyElement.id;
    var productIdElementId = elementId.sub('qty_', 'cartLineProductId_');
    var productId = $(productIdElementId).value;
    if (qtyElement.value >= 0 && !isNaN(qtyElement.value)) {
        var itemIndex = getProductLineItemIndex(event, productId);
        var formValues = $('cartForm').serialize();
        updateCartData(elementId, formValues, qtyElement.value, itemIndex);
    } else {
        qtyElement.value = "";  
    }
}

function updateCartData(elementId, formValues, itemQty, itemIndex) {
    new Ajax.Request('cartItemQtyUpdate', {
        asynchronous: true, 
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            if (data.totalQuantity == 0) {
                $('emptyCartCheckoutPanel').show();
                $('checkoutPanel').hide();
            } else {
                // Used for edit cart
                $('microCartQuantity').update(data.totalQuantity);
                $('cartSubTotal').update(data.subTotalCurrencyFormatted);
                $('cartDiscountValue').update(data.displayOrderAdjustmentsTotalCurrencyFormatted);
                $('cartTotalShipping').update(data.totalShippingCurrencyFormatted);
                $('cartTotalSalesTax').update(data.totalSalesTaxCurrencyFormatted);
                $('microCartTotal').update(data.displayGrandTotalCurrencyFormatted);
                $('cartDisplayGrandTotal').update(data.displayGrandTotalCurrencyFormatted);
                // Used for summary 
                $('completedCartSubTotal').update(data.subTotalCurrencyFormatted);
                $('completedCartTotalShipping').update(data.totalShippingCurrencyFormatted);
                $('completedCartTotalSalesTax').update(data.totalSalesTaxCurrencyFormatted);
                $('completedCartDisplayGrandTotal').update(data.displayGrandTotalCurrencyFormatted);
                $('completedCartDiscount').update(data.displayOrderAdjustmentsTotalCurrencyFormatted);
                if (elementId != undefined && $(elementId).value != "") {
                    if (itemQty == 0) {
                        var cartItemRowId = elementId.sub('qty_','cartItemRow_');
                        $(cartItemRowId).remove();
                        var cartItemDisplayRowId = elementId.sub('qty_','cartItemDisplayRow_');
                        $(cartItemDisplayRowId).remove();
                    } else {
                        var itemsHash = $H(data.cartItemData);
                        var lineTotalId = elementId.sub('qty_','displayItem_');
                        var lineDiscountTotalId = elementId.sub('qty_','addPromoCode_');
                        var lineItemTotal = itemsHash.get("displayItemSubTotalCurrencyFormatted_"+itemIndex);
                        var lineItemAdjustment = itemsHash.get("displayItemAdjustment_"+itemIndex);
                        $(lineTotalId).update(lineItemTotal);
                        $(lineDiscountTotalId).update(lineItemAdjustment);
                        var completedLineItemQtyId =  elementId.sub('qty_','completedCartItemQty_');
                        $(completedLineItemQtyId).update($(elementId).value);
                        $('completedCartItemAdjustment_'+itemIndex).update(lineItemAdjustment);
                        var completedCartItemSubTotalId = elementId.sub('qty_','completedCartItemSubTotal_');
                        $(completedCartItemSubTotalId).update(lineItemTotal);
                    }
                }
            }
        },
        parameters: formValues
    });
}

function processOrder() {
    $('processOrderButton').disabled = true ;
    Effect.Fade('processOrderButton', {duration: 0.1});
    Effect.Appear('processingOrderButton', {duration: 0.1});
    $('orderSubmitForm').submit();
}

function getAssociatedBillingStateList(formName, divId) {
    var optionList = [];
    new Ajax.Request("getAssociatedStateList", {
        asynchronous: false,
        parameters: $(formName).serialize(),
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            stateList = data.stateList;
            stateList.each(function(state) {
                geoVolues = state.split(': ');
                optionList.push("<option value = "+geoVolues[1]+" >"+geoVolues[0]+"</option>");
            });
            $(divId).update(optionList);
        }
    });
}