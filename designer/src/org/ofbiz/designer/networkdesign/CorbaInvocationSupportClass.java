package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;


//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class CorbaInvocationSupportClass extends AbstractDataSupportClass implements ICorbaInvocationSupportClass {
    public Vector getParametersNames() {
        int count = data().getParameterCount();
        Vector returnVec = new Vector();
        for(int i=0; i<count; i++) {
            IParameter parameter = data().getParameterAt(i);
            String paramStr = parameter.getDatatypeurlAttribute() + " " + parameter.getVariablenameAttribute();
            returnVec.addElement(paramStr);
        }
        return returnVec;
    }

    public void addParameterByName(String name) {
        IParameter parameter = new Parameter();
        //String ID = "Parameter" + Math.random();
        String ID = getXml().generateUniqueName("Parameter");
        parameter.setIdAttribute(ID);
        StringTokenizer stk = new StringTokenizer(name);
        String type = stk.nextToken();
        name = stk.nextToken();
        parameter.setDatatypeurlAttribute(type);
        parameter.setVariablenameAttribute(name);
        data().addParameter(parameter);
        //getXml().setIdRef(ID, parameter);
        notifyElementAdded(parameter, data());
    }

    public void removeParameterByName(String name) {
        StringTokenizer stk = new StringTokenizer(name);
        String type = stk.nextToken();
        name = stk.nextToken();

        int count = data().getParameterCount();
        for(int i=0; i<count; i++) {
            IParameter param = data().getParameterAt(i);
            if(param.getVariablenameAttribute().equals(name) && param.getDatatypeurlAttribute().equals(type)) {
                Object obj = data().getParameterAt(i);
                data().removeParameterAt(i);
                notifyElementRemoved(obj, data());
                return;
            }
        }
    }

    public Vector getForwardMappings() {
        IForwardMappingList ifl = data().getForwardMappingList();
        int count = ifl.getCorbaMappingCount();
        Vector returnVec = new Vector();
        for(int i=0; i<count; i++) {
            ICorbaMapping cm = ifl.getCorbaMappingAt(i);
            String paramStr = cm.getFirstElementAttribute() + " --> " + cm.getSecondElementAttribute();
            returnVec.addElement(paramStr);
        }
        return returnVec;
    }

    public String getReturnValueMapping() {
        IReverseMappingList irl = data().getReverseMappingList();
        int count = irl.getCorbaMappingCount();
        ICorbaMapping cm = null;
        if(count == 0) {
            cm = new CorbaMapping();
            irl.addCorbaMapping(cm);
        } else
            cm = irl.getCorbaMappingAt(0);
        return cm.getFirstElementAttribute() + " <-- " + cm.getSecondElementAttribute();
    }

    public void setReturnValueMapping(String mappingStr) {
        IReverseMappingList irl = data().getReverseMappingList();
        if(mappingStr.length() == 0) {
            irl.removeAllCorbaMappings();
            return;
        }
        int count = irl.getCorbaMappingCount();
        ICorbaMapping cm = null;
        if(count == 0) {
            cm = new CorbaMapping();
            irl.addCorbaMapping(cm);
        } else
            cm = irl.getCorbaMappingAt(0);


        int index = mappingStr.indexOf("<--");
        if(index == -1)
            throw new RuntimeException("invalid mappingstr " + mappingStr);

        String first = mappingStr.substring(0, index).trim();
        String second = mappingStr.substring(index+3, mappingStr.length()).trim();

        cm.setFirstElementAttribute(first);
        cm.setSecondElementAttribute(second);
        notifyElementAdded(cm, irl);
    }

    public void addForwardMapping(String mappingStr) {
        IForwardMappingList fl = data().getForwardMappingList();
        ICorbaMapping mapping = new CorbaMapping();

        int index = mappingStr.indexOf("-->");
        if(index == -1)
            throw new RuntimeException("invalid mappingstr " + mappingStr);

        String first = mappingStr.substring(0, index).trim();
        String second = mappingStr.substring(index+3, mappingStr.length()).trim();

        mapping.setFirstElementAttribute(first);
        mapping.setSecondElementAttribute(second);
        fl.addCorbaMapping(mapping);
        notifyElementAdded(mapping, fl);
    }

    public void addReverseMapping(String mappingStr) {
        IReverseMappingList fl = data().getReverseMappingList();
        ICorbaMapping mapping = new CorbaMapping();

        int index = mappingStr.indexOf("<--");
        if(index == -1)
            throw new RuntimeException("invalid mappingstr " + mappingStr);

        String first = mappingStr.substring(0, index).trim();
        String second = mappingStr.substring(index+3, mappingStr.length()).trim();

        mapping.setFirstElementAttribute(first);
        mapping.setSecondElementAttribute(second);
        fl.addCorbaMapping(mapping);
        notifyElementAdded(mapping, fl);
    }

    public void removeForwardMapping(String mappingStr) {
        IForwardMappingList fl = data().getForwardMappingList();

        int index = mappingStr.indexOf("-->");
        if(index == -1)
            throw new RuntimeException("invalid mappingstr " + mappingStr);

        String first = mappingStr.substring(0, index).trim();
        String second = mappingStr.substring(index+3, mappingStr.length()).trim();

        int count = fl.getCorbaMappingCount();
        for(int i=0; i<count; i++) {
            ICorbaMapping mapping = fl.getCorbaMappingAt(i);
            if(mapping.getFirstElementAttribute().equals(first) && mapping.getSecondElementAttribute().equals(second)) {
                Object obj = fl.getCorbaMappingAt(i);
                fl.removeCorbaMappingAt(i);
                notifyElementRemoved(obj, fl);
                return;
            }
        }
        WARNING.println(mappingStr + " does not match any mapping in data");
    }

    public void removeReverseMapping(String mappingStr) {
        IReverseMappingList fl = data().getReverseMappingList();

        int index = mappingStr.indexOf("<--");
        if(index == -1)
            throw new RuntimeException("invalid mappingstr " + mappingStr);

        String first = mappingStr.substring(0, index).trim();
        String second = mappingStr.substring(index+3, mappingStr.length()).trim();

        int count = fl.getCorbaMappingCount();
        for(int i=0; i<count; i++) {
            ICorbaMapping mapping = fl.getCorbaMappingAt(i);
            if(mapping.getFirstElementAttribute().equals(first) && mapping.getSecondElementAttribute().equals(second)) {
                Object obj = fl.getCorbaMappingAt(i);
                fl.removeCorbaMappingAt(i);
                notifyElementRemoved(obj, fl);
                return;
            }
        }
        WARNING.println(mappingStr + " does not match any mapping in data");
    }

    private ICorbaInvocation data() {
        return(ICorbaInvocation)getDtdObject();
    }
}
