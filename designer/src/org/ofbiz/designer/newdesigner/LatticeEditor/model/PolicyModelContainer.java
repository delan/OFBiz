package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import org.ofbiz.designer.newdesigner.model.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.generic.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.*;
import org.ofbiz.designer.util.*;

public class PolicyModelContainer extends AbstractRelationshipNode implements IDocumentWrapper {

    public PolicyModelContainer(IDocumentWrapper theModel) {
        setModel(theModel);
    }

    public void setModel(IDocumentWrapper newModel) {
        setDataElement("model",newModel);
    }

    public Object[][] getRelationships() {
        Object[][] returnObj = {
            {"parent","policies",SINGLE},
            {"fromDomain","fromPolicies",SINGLE},
            {"toDomain","toPolicies",SINGLE}
        };
        return returnObj; 
    }

    public Object[][] getDataElements() {
        Object[][] returnObj = {
            {"id","java.lang.String"},
            {"model","org.ofbiz.designer.generic.IDocumentWrapper"},
            {"type","java.lang.String"}
        };
        return returnObj;
    }

    public String getId() {
        return(String)getDataElement("id");
    }

    public void setId(String newId) {
        setDataElement("id",newId);
    }

    public String getType() {
        return(String)getDataElement("type");
    }

    public void setType(String newType) {
        setDataElement("type",newType);
    }

    public IDomainEnvModel getParent() {
        return(IDomainEnvModel)getRelationshipAt("parent",0);
    }

    public void setParent(IDomainEnvModel aParent) {
        removeRelationship("parent",getParent());
        addRelationship("parent",aParent);
    }

    public IDomainModel getFromDomain() {
        return(IDomainModel)getRelationshipAt("fromDomain",0);
    }

    public void setFromDomain(IDomainModel aDomain) {
        removeRelationship("fromDomain",getFromDomain());
        addRelationship("fromDomain",aDomain);
    }

    public IDomainModel getToDomain() {
        return(IDomainModel)getRelationshipAt("toDomain",0);
    }

    public void setToDomain(IDomainModel aDomain) {
        removeRelationship("toDomain",getToDomain());
        addRelationship("toDomain",aDomain);
    }

    public void remove(int offs, int len) {
        try {
            ((IDocumentWrapper) getDataElement("model")).remove(offs,len);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void insertString(int offset, String str, AttributeSet a) {
        try {
            ((IDocumentWrapper) getDataElement("model")).insertString(offset,str,a);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Element[] getRootElements() {
        return((IDocumentWrapper) getDataElement("model")).getRootElements();
    }

    public void addDocumentListener(DocumentListener docList) {
        ((IDocumentWrapper) getDataElement("model")).addDocumentListener(docList);
    }

    public int getLength() {
        return((IDocumentWrapper) getDataElement("model")).getLength();
    }

    public void removeDocumentListener(DocumentListener docList) {
        ((IDocumentWrapper) getDataElement("model")).removeDocumentListener(docList);
    }

    public Object getProperty(Object prop) {
        return((IDocumentWrapper) getDataElement("model")).getProperty(prop);
    }

    public void removeUndoableEditListener(UndoableEditListener und) {
        ((IDocumentWrapper) getDataElement("model")).removeUndoableEditListener(und);
    }

    public void putProperty(Object propKey,Object propVal) {
        ((IDocumentWrapper) getDataElement("model")).putProperty(propKey,propVal);
    }

    public void render(Runnable run) {
        ((IDocumentWrapper) getDataElement("model")).render(run);
    }

    public Element getDefaultRootElement() {
        return((IDocumentWrapper) getDataElement("model")).getDefaultRootElement();
    }

    public Position getEndPosition() {
        return((IDocumentWrapper) getDataElement("model")).getEndPosition();
    }

    public Position getStartPosition() {
        return((IDocumentWrapper) getDataElement("model")).getStartPosition();
    }

    public Position createPosition(int pos) {
        Position returnObj = null;
        try {
            return((IDocumentWrapper) getDataElement("model")).createPosition(pos);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return returnObj;
    }

    public void getText(int intone, int inttwo, Segment seg) {
        try {
            ((IDocumentWrapper) getDataElement("model")).getText(intone,inttwo,seg);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getText(int intone,int inttwo) {
        String returnObj = null;
        try {
            return((IDocumentWrapper) getDataElement("model")).getText(intone,inttwo);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return returnObj;
    }

    public void addUndoableEditListener(UndoableEditListener listen) {
        ((IDocumentWrapper) getDataElement("model")).addUndoableEditListener(listen);
    }

    public void die() {
        //purposefully not implemented.
    }

    public void handleChanged() {
        //purposefully not implemented.
    }

    public HashSet getModifyMethods() {
        return((IDocumentWrapper) getDataElement("model")).getModifyMethods();
    }

    public Object getRawModel() {
        return((IDocumentWrapper) getDataElement("model")).getRawModel();
    }

    public ITranslator getTranslator() {
        return((IDocumentWrapper) getDataElement("model")).getTranslator();
    }

    public void setTranslator(ITranslator newTrans) {
        ((IDocumentWrapper) getDataElement("model")).setTranslator(newTrans);
    }

    public void neighborChanged(IRelationshipNode neighbor) {
        //purposefully not implemented.
    }

    public void neighborDying(IRelationshipNode neighbor) {
        die();
    }

    public void dataGone() {
        WARNING.println("DATA HAS BEEN REMOVED !!");
    }
}
