/*
 * Created on Feb 15, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.ofbiz.core.ftl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.core.util.*;

import freemarker.ext.beans.BeanModel;
import freemarker.template.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

/**
 * @author jaz
 */
public class SetRequestAttributeMethod implements TemplateMethodModelEx {
        
    public static final String module = SetRequestAttributeMethod.class.getName();        

    /* 
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    public TemplateModel exec(List args) throws TemplateModelException {      
        if (args == null || args.size() != 2)
            throw new TemplateModelException("Invalid number of arguements");  
        if (!(args.get(0) instanceof TemplateScalarModel))    
            throw new TemplateModelException("First argument not an instance of TemplateScalarModel");
        if (!(args.get(1) instanceof BeanModel) && !(args.get(1) instanceof TemplateNumberModel) && !(args.get(1) instanceof TemplateScalarModel)) 
            throw new TemplateModelException("Second argument not an instance of BeanModel nor TemplateNumberModel nor TemplateScalarModel");
                          
        Environment env = Environment.getCurrentEnvironment();
        BeanModel req = (BeanModel)env.getVariable("request");
        HttpServletRequest request = (HttpServletRequest) req.getWrappedObject();
        
        String name = ((TemplateScalarModel) args.get(0)).getAsString();
        Object value = null;
        if (args.get(1) instanceof TemplateScalarModel)
            value = ((TemplateScalarModel) args.get(1)).getAsString();
        else if (args.get(1) instanceof TemplateNumberModel)
            value = ((TemplateNumberModel) args.get(1)).getAsNumber();
        else
            value = ((BeanModel) args.get(1)).getWrappedObject();        
                       
        request.setAttribute(name, value);               
        return new SimpleScalar("");
    }

}
