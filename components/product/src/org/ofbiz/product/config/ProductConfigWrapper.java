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
import java.util.Locale;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
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
    protected double basePrice = 0.0;
    protected List questions = null; // ProductConfigs
    
    /** Creates a new instance of ProductConfigWrapper */
    public ProductConfigWrapper() {
    }
    
    public ProductConfigWrapper(GenericDelegator delegator, LocalDispatcher dispatcher, String productId, String catalogId, String webSiteId, String currencyUomId, Locale locale, GenericValue autoUserLogin) throws Exception {
        init(delegator, dispatcher, productId, catalogId, webSiteId, currencyUomId, locale, autoUserLogin);
    }
    
    private void init(GenericDelegator delegator, LocalDispatcher dispatcher, String productId, String catalogId, String webSiteId, String currencyUomId, Locale locale, GenericValue autoUserLogin) throws Exception {
        product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        if (product == null || !product.getString("productTypeId").equals("AGGREGATED")) {
            throw new ProductConfigWrapperException("Product " + productId + " is not an AGGREGATED product.");
        }
        // get the base price
        Map fieldMap = UtilMisc.toMap("product", product, "prodCatalogId", catalogId, "webSiteId", webSiteId,
                                      "currencyUomId", currencyUomId, "autoUserLogin", autoUserLogin);
        Map priceMap = dispatcher.runSync("calculateProductPrice", fieldMap);
        Double price = (Double)priceMap.get("price");
        if (price != null) {
            basePrice = price.doubleValue();
        }
        questions = new ArrayList();
        List questionsValues = new ArrayList();
        if (product.getString("productTypeId") != null && product.getString("productTypeId").equals("AGGREGATED")) {
            questionsValues = delegator.findByAnd("ProductConfig", UtilMisc.toMap("productId", productId), UtilMisc.toList("sequenceNum"));
            questionsValues = EntityUtil.filterByDate(questionsValues);
            Iterator questionsValuesIt = questionsValues.iterator();
            while (questionsValuesIt.hasNext()) {
                ConfigItem oneQuestion = new ConfigItem((GenericValue)questionsValuesIt.next());
                oneQuestion.setContent(locale, "text/html"); // TODO: mime-type shouldn't be hardcoded
                questions.add(oneQuestion);
                List configOptions = delegator.findByAnd("ProductConfigOption", UtilMisc.toMap("configItemId", oneQuestion.getConfigItemAssoc().getString("configItemId")), UtilMisc.toList("sequenceNum"));
                Iterator configOptionsIt = configOptions.iterator();
                //List availableOptions = new ArrayList();
                while (configOptionsIt.hasNext()) {
                    ConfigOption option = new ConfigOption(delegator, dispatcher, (GenericValue)configOptionsIt.next(), catalogId, webSiteId, currencyUomId, autoUserLogin);
                    oneQuestion.addOption(option);
                    //availableOptions.add(option);
                }
                //options.add(availableOptions);
            }
        }
    }
    
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ProductConfigWrapper)) {
            return false;
        }
        ProductConfigWrapper cw = (ProductConfigWrapper)obj;
        if (!product.getString("productId").equals(cw.getProduct().getString("productId"))) {
            return false;
        }
        List cwq = cw.getQuestions();
        if (questions.size() != cwq.size()) {
            return false;
        }
        for (int i = 0; i < questions.size(); i++) {
            ConfigItem ci = (ConfigItem)questions.get(i);
            if (ci.equals(cwq.get(i))) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return "" + questions;
    }

    public List getQuestions() {
        return questions;
    }
    
    public GenericValue getProduct() {
        return product;
    }
    
    public void setSelected(int question, int option) throws Exception {
        ConfigItem ci = (ConfigItem)questions.get(question);
        List avalOptions = ci.getOptions();
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
            if (ci.isStandard()) {
                selectedOptions.addAll(ci.getOptions());
            } else {
                Iterator availOptions = ci.getOptions().iterator();
                while (availOptions.hasNext()) {
                    ConfigOption oneOption = (ConfigOption)availOptions.next();
                    if (oneOption.isSelected()) {
                        selectedOptions.add(oneOption);
                    }
                }
            }
        }
        return selectedOptions;
    }
    
    public double getTotalPrice() {
        double totalPrice = basePrice;
        List options = getSelectedOptions();
        for (int i = 0; i < options.size(); i++) {
            ConfigOption oneOption = (ConfigOption)options.get(i);
            totalPrice += oneOption.getPrice();
        }
        return totalPrice;
    }
    
    public boolean isCompleted() {
        boolean completed = true;
        for (int i = 0; i < questions.size(); i++) {
            ConfigItem ci = (ConfigItem)questions.get(i);
            if (!ci.isStandard() && ci.isMandatory()) {
                Iterator availOptions = ci.getOptions().iterator();
                while (availOptions.hasNext()) {
                    ConfigOption oneOption = (ConfigOption)availOptions.next();
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
    
    public class ConfigItem {
        GenericValue configItem = null;
        GenericValue configItemAssoc = null;
        ProductConfigItemContentWrapper content = null;
        List options = null;
        
        public ConfigItem(GenericValue questionAssoc) throws Exception {
            configItemAssoc = questionAssoc;
            configItem = configItemAssoc.getRelatedOne("ConfigItemProductConfigItem");
            options = new ArrayList();
        }
        
        public void setContent(Locale locale, String mimeTypeId) {
            content = new ProductConfigItemContentWrapper(configItem, locale, mimeTypeId);
        }
        
        public ProductConfigItemContentWrapper getContent() {
            return content;
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
        
        public void addOption(ConfigOption option) {
            options.add(option);
        }
        
        public List getOptions() {
            return options;
        }
        
        public String getQuestion() {
            String question = "";
            if (UtilValidate.isNotEmpty(configItemAssoc.getString("description"))) {
                question = configItemAssoc.getString("description");
            } else {
                if (content != null) {
                    question = content.get("DESCRIPTION");
                } else {
                    question = (configItem.getString("description") != null? configItem.getString("description"): "");
                }
            }
            return question;
        }
        
        public boolean isSelected() {
            Iterator availOptions = getOptions().iterator();
            while (availOptions.hasNext()) {
                ConfigOption oneOption = (ConfigOption)availOptions.next();
                if (oneOption.isSelected()) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof ConfigItem)) {
                return false;
            }
            ConfigItem ci = (ConfigItem)obj;
            if (!configItem.getString("configItemId").equals(ci.getConfigItem().getString("configItemId"))) {
                return false;
            }
            List opts = ci.getOptions();
            if (options.size() != opts.size()) {
                return false;
            }
            for (int i = 0; i < options.size(); i++) {
                ConfigOption co = (ConfigOption)options.get(i);
                if (co.equals(opts.get(i))) {
                    return false;
                }
            }
            return true;
        }

        public String toString() {
            return configItem.getString("configItemId");
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
        
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof ConfigOption)) {
                return false;
            }
            ConfigOption co = (ConfigOption)obj;
            // TODO: we should compare also the GenericValues
            
            return isSelected() == co.isSelected();
        }
        
        public String toString() {
            return configOption.getString("configItemId") + "/" + configOption.getString("configOptionId") + (isSelected()? "*": "");
        }

    }
    
}
