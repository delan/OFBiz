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
    protected List choosenOptions = null; // lists of ProductConfigConfigs
    
    /** Creates a new instance of ProductConfigWrapper */
    public ProductConfigWrapper() {
    }
    
    public ProductConfigWrapper(GenericDelegator delegator, LocalDispatcher dispatcher, String productId, String catalogId, String webSiteId, String currencyUomId, GenericValue autoUserLogin) throws Exception {
        init(delegator, dispatcher, productId, catalogId, webSiteId, currencyUomId, autoUserLogin);
    }
    
    private void init(GenericDelegator delegator, LocalDispatcher dispatcher, String productId, String catalogId, String webSiteId, String currencyUomId, GenericValue autoUserLogin) throws Exception {
        product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        options = new ArrayList();
        choosenOptions = new ArrayList();
        if (product.getString("productTypeId") != null && product.getString("productTypeId").equals("AGGREGATED")) {
            questions = delegator.findByAnd("ProductConfig", UtilMisc.toMap("productId", productId), UtilMisc.toList("sequenceNum"));
            questions = EntityUtil.filterByDate(questions);
            Iterator questionsIt = questions.iterator();
            while (questionsIt.hasNext()) {
                GenericValue oneQuestion = (GenericValue)questionsIt.next();
                List configOptions = delegator.findByAnd("ProductConfigOption", UtilMisc.toMap("configItemId", oneQuestion.getString("configItemId")), UtilMisc.toList("sequenceNum"));
                Iterator configOptionsIt = configOptions.iterator();
                List availableOptions = new ArrayList();
                while (configOptionsIt.hasNext()) {
                    ConfigOption option = new ConfigOption(delegator, dispatcher, (GenericValue)configOptionsIt.next(), catalogId, webSiteId, currencyUomId, autoUserLogin);
                    availableOptions.add(option);
                }
                options.add(availableOptions);
                choosenOptions.add(null);
            }
        }
    }
    
    public String renderWebForm() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<form>");
        sb.append("<table>");
        for (int i = 0; i < questions.size(); i++) {
            GenericValue oneQuestionAssoc = (GenericValue)questions.get(i);
            GenericValue oneQuestion = oneQuestionAssoc.getRelatedOne("ConfigItemProductConfigItem");
            List avalOptions = (List)options.get(i);
            sb.append("<tr><th>");
            sb.append(oneQuestion.getString("description"));
            sb.append("</th></tr>");
            sb.append("<tr><td>");
            if (oneQuestionAssoc.getString("configTypeId").equals("STANDARD")) {
                for (int j = 0; j < avalOptions.size(); j++) {
                    if (j > 0) {
                        sb.append(", ");
                    }
                    ConfigOption oneOption = (ConfigOption)avalOptions.get(j);
                    sb.append(oneOption.getDescription() + " (" + oneOption.getPrice() + ")");
                }
            } else if (oneQuestion.getString("configItemTypeId").equals("SINGLE")) {
                sb.append("<tr><td>");
                sb.append("<select class='selectBox'>");
                for (int j = 0; j < avalOptions.size(); j++) {
                    ConfigOption oneOption = (ConfigOption)avalOptions.get(j);
                    sb.append("<option>");
                    sb.append(oneOption.getDescription() + " (" + oneOption.getPrice() + ")");
                    sb.append("</option>");
                }
                sb.append("</select>");
                sb.append("</td></tr>");
            } else {
                // MULTI-CHOICE  question
                for (int j = 0; j < avalOptions.size(); j++) {
                    sb.append("<tr><td>");
                    ConfigOption oneOption = (ConfigOption)avalOptions.get(j);
                    sb.append("<input type='RADIO' name='" + i + "' value=''>");
                    sb.append(oneOption.getDescription() + " (" + oneOption.getPrice() + ")");
                    sb.append("</td></tr>");
                }
            }
            sb.append("</td></tr>");

            
        }
        sb.append("</table>");
        sb.append("</form>");
        return sb.toString();
    }
    
    class ConfigOption {
        double optionPrice = 0;
        Date availabilityDate = null;
        List components = null; // lists of ProductConfigProduct
        GenericValue configOption = null;
        
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
        
    }
    
}
