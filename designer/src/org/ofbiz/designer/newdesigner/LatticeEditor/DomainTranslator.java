package org.ofbiz.designer.newdesigner.LatticeEditor;

//import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.pattern.*;
//import networktask.*;
import java.util.*;
import org.ofbiz.designer.util.*;

import org.ofbiz.designer.domainenv.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeNodeModel;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.IDomainModelWrapper;

import java.awt.*;




public class DomainTranslator extends BaseTranslator {


    DomainTranslator(IDomainModelWrapper modelIn, IDomainInfoWrapper wrapperIn, String direction) {
        super(modelIn, wrapperIn);
        synchronize(direction);
    }

    public void updateModelImpl(){
        IDomainInfoWrapper wrapper = (IDomainInfoWrapper)getDataObject();
        ILatticeNodeModel model = (ILatticeNodeModel)getGuiModel();

        model.beginTransaction();

        try{
            model.setLocation(new Point(Integer.parseInt(wrapper.getPosition().getNumber1()),
                                        Integer.parseInt(wrapper.getPosition().getNumber2())));
        } catch(NullPointerException e){
        }
        model.setName(wrapper.getName());
        model.setId(wrapper.getIdAttribute());
        model.setColor(new java.awt.Color(Integer.parseInt(wrapper.getColor().getNumber1()),
                                          Integer.parseInt(wrapper.getColor().getNumber2()),
                                          Integer.parseInt(wrapper.getColor().getNumber3())));
        model.setDescription(wrapper.getDescription());

        model.commitTransaction();

    }

    public void updateDataImpl(){
        LOG.println("<<<<<<<<<<<<<<<< UPDATE");
        IDomainInfoWrapper wrapper = (IDomainInfoWrapper)getDataObject();
        ILatticeNodeModel model = (ILatticeNodeModel)getGuiModel();

        wrapper.setName(model.getName());

        IPositionWrapper posWrap = (IPositionWrapper) wrapper.getPosition();
        if(posWrap!=null) {
            posWrap.setNumber1(Integer.toString(model.getLocation().x));
            posWrap.setNumber2(Integer.toString(model.getLocation().y));
        }



        IColorWrapper colorWrap = (IColorWrapper) wrapper.getColor();
        if(colorWrap!=null) {
            LOG.println("SETTING COLOR");
            colorWrap.setNumber1(Integer.toString(model.getColor().getRed()));
            colorWrap.setNumber2(Integer.toString(model.getColor().getGreen()));
            colorWrap.setNumber3(Integer.toString(model.getColor().getBlue()));
        }
        wrapper.setColor(wrapper.getColor());
        LOG.println("wrapper.getColor() is " + wrapper.getColor());

        wrapper.setDescription(model.getDescription());
    }
}

