/*
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
 */

package org.ofbiz.product.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;


/**
 * Product Config Wrapper: gets product config to display
 *
 * @author  <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
 *
 */

public class ProductConfigWrapper {
    
    public static final String module = ProductConfigWrapper.class.getName();
    
    protected GenericValue product = null; // the aggregated product
    protected List questions = null; // ProductConfigs
    protected List options = null; // lists of ProductConfigOptions
    
    /** Creates a new instance of ProductConfigWrapper */
    public ProductConfigWrapper() {
    }
    
    public ProductConfigWrapper(GenericDelegator delegator, LocalDispatcher dispatcher, String productId, String catalogId, String webSiteId, String currencyUomId, GenericValue autoUserLogin) throws Exception {
        init(delegator, dispatcher, productId, catalogId, webSiteId, currencyUomId, autoUserLogin);
    }
    
    private void init(GenericDelegator delegator, LocalDispatcher dispatcher, String productId, String catalogId, String webSiteId, String currencyUomId, GenericValue autoUserLogin) throws Exception {
        product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        questions = new ArrayList();
        options = new ArrayList();
        List questionsValues = new ArrayList();
        if (product.getString("productTypeId") != null && product.getString("productTypeId").equals("AGGREGATED")) {
            questionsValues = delegator.findByAnd("ProductConfig", UtilMisc.toMap("productId", productId), UtilMisc.toList("sequenceNum"));
            questionsValues = EntityUtil.filterByDate(questionsValues);
            Iterator questionsValuesIt = questionsValues.iterator();
            while (questionsValuesIt.hasNext()) {
                ConfigItem oneQuestion = new ConfigItem((GenericValue)questionsValuesIt.next());
                questions.add(oneQuestion);
                List configOptions = delegator.findByAnd("ProductConfigOption", UtilMisc.toMap("configItemId", oneQuestion.getConfigItemAssoc().getString("configItemId")), UtilMisc.toList("sequenceNum"));
                Iterator configOptionsIt = configOptions.iterator();
                List availableOptions = new ArrayList();
                while (configOptionsIt.hasNext()) {
                    ConfigOption option = new ConfigOption(delegator, dispatcher, (GenericValue)configOptionsIt.next(), catalogId, webSiteId, currencyUomId, autoUserLogin);
                    availableOptions.add(option);
                }
                options.add(availableOptions);
            }
        }
    }
    
    public List getQuestions() {
        return questions;
    }
    
    public List getOptions() {
        return options;
    }

    public GenericValue getProduct() {
        return product;
    }
    
    public void setSelected(int question, int option) throws Exception {
        ConfigItem ci = (ConfigItem)questions.get(question);
        List avalOptions = (List)options.get(question);
        if (ci.isSingleChoice()) {
            for (int j = 0; j < avalOptions.size(); j++) {
                ConfigOption oneOption = (ConfigOption)avalOptions.get(j);
                oneOption.setSelected(false);
            }
        }
        ConfigOption theOption = null;
        if (option >= 0 && option < avalOptions.size()) {
            theOption = (ConfigOption)avalOptions.get(option);
        }
        if (theOption != null) {
            theOption.setSelected(true);
        }
    }
    
    public List getSelectedOptions() {
        List selectedOptions = new ArrayList();
        for (int i = 0; i < questions.size(); i++) {
            ConfigItem ci = (ConfigItem)questions.get(i);
            List availOptions = (List)options.get(i);
            if (ci.isStandard()) {
                selectedOptions.addAll(availOptions);
            } else {
                for (int j = 0; j < availOptions.size(); j++) {
                    ConfigOption oneOption = (ConfigOption)availOptions.get(j);
                    if (oneOption.isSelected()) {
                        selectedOptions.add(oneOption);
                    }
                }
            }
        }
        return selectedOptions;
    }
    
    public double getTotalPrice() {
        double totalPrice = 0.0;
        for (int i = 0; i < options.size(); i++) {
            ConfigItem ci = (ConfigItem)questions.get(i);
            List availOptions = (List)options.get(i);
            for (int j = 0; j < availOptions.size(); j++) {
                ConfigOption oneOption = (ConfigOption)availOptions.get(j);
                if (oneOption.isSelected() || ci.isStandard()) {
                    totalPrice += oneOption.getPrice();
                }
            }
        }
        return totalPrice;
    }
    
    public boolean isCompleted() {
        boolean completed = true;
        for (int i = 0; i < questions.size(); i++) {
            ConfigItem ci = (ConfigItem)questions.get(i);
            if (!ci.isStandard() && ci.isMandatory()) {
                List availOptions = (List)options.get(i);
                for (int j = 0; j < availOptions.size(); j++) {
                    ConfigOption oneOption = (ConfigOption)availOptions.get(j);
                    if (oneOption.isSelected()) {
                        completed = true;
                        break;
                    } else {
                        completed = false;
                    }
                }
                if (!completed) {
                    break;
                }
            }
        }
        return completed;
    }
    
    class ConfigItem {
        GenericValue configItem = null;
        GenericValue configItemAssoc = null;
        
        public ConfigItem(GenericValue questionAssoc) throws Exception {
            configItemAssoc = questionAssoc;
            configItem = configItemAssoc.getRelatedOne("ConfigItemProductConfigItem");
        }
        
        public GenericValue getConfigItem() {
            return configItem;
        }
        
        public GenericValue getConfigItemAssoc() {
            return configItemAssoc;
        }

        public boolean isStandard() {
            return configItemAssoc.getString("configTypeId").equals("STANDARD");
        }
        
        public boolean isSingleChoice() {
            return configItem.getString("configItemTypeId").equals("SINGLE");
        }
        
        public boolean isMandatory() {
            return configItemAssoc.getString("isMandatory") != null && configItemAssoc.getString("isMandatory").equals("Y");
        }
        
    }
    
    public class ConfigOption {
        double optionPrice = 0;
        Date availabilityDate = null;
        List components = null; // lists of ProductConfigProduct
        GenericValue configOption = null;
        boolean selected = false;
        boolean available = true;
        
        public ConfigOption(GenericDelegator delegator, LocalDispatcher dispatcher, GenericValue option, String catalogId, String webSiteId, String currencyUomId, GenericValue autoUserLogin) throws Exception {
            configOption = option;
            components = option.getRelated("ConfigOptionProductConfigProduct");
            Iterator componentsIt = components.iterator();
            while (componentsIt.hasNext()) {
                double price = 0;
                GenericValue oneComponent = (GenericValue)componentsIt.next();
                // Get the component's price
                Map fieldMap = UtilMisc.toMap("product", oneComponent.getRelatedOne("ProductProduct"), "prodCatalogId", catalogId, "webSiteId", webSiteId,
                                              "currencyUomId", currencyUomId, "autoUserLogin", autoUserLogin);
                Map priceMap = dispatcher.runSync("calculateProductPrice", fieldMap);
                Double componentPrice = (Double)priceMap.get("componentPrice");
                double mult = 1;
                if (oneComponent.getDouble("quantity") != null) {
                    mult = oneComponent.getDouble("quantity").doubleValue();
                }
                if (mult == 0) {
                    mult = 1;
                }
                if (componentPrice != null) {
                    price = componentPrice.doubleValue();
                } else {
                    price = ((Double)priceMap.get("price")).doubleValue();
                }
                optionPrice += (price * mult);
                // TODO: get the component's availability date
            }
        }
        
        public String getDescription() {
            return (configOption.getString("description") != null? configOption.getString("description"): "no description");
        }
        
        public double getPrice() {
            return optionPrice;
        }
        
        public boolean isSelected() {
            return selected;
        }
        
        public void setSelected(boolean newValue) {
            selected = newValue;
        }
        
        public boolean isAvailable() {
            return available;
        }
        
        public void setAvailable(boolean newValue) {
            available = newValue;
        }

        public List getComponents() {
            return components;
        }
    }
    
}
