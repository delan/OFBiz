package org.ofbiz.designer.generic;

import java.util.*;
import org.ofbiz.designer.util.*;

public abstract class AbstractRelationshipNode implements IRelationshipNode {
    // format - [name, type]
    protected abstract Object[][] getDataElements();

    // format - [name, complement, SINGLE/MULTIPLE]
    protected abstract Object[][] getRelationships();

    // synchronize data elements with GUI
    //public abstract void synchronizeGui();	

    protected Hashtable fields = new Hashtable();
    protected Hashtable fieldTypes = new Hashtable();
    protected Hashtable relationships = new Hashtable();
    protected Hashtable complements = new Hashtable();
    protected Hashtable relationshipOrder = new Hashtable();

    protected static final String SINGLE = "SINGLE";
    protected static final String MULTIPLE = "MULTIPLE";

    public AbstractRelationshipNode() {
        try {
            Object[][] dataElements = getDataElements();
            for(int i=0; i<dataElements.length; i++) {
                String name = (String)dataElements[i][0];
                String type = (String)dataElements[i][1];
                Class.forName(type); // verify class exists
                fieldTypes.put(name, type);
            }

            Object[][] relationshipElements = getRelationships();
            for(int i=0; i<relationshipElements.length; i++) {
                String name = (String)relationshipElements[i][0];
                String complement = (String)relationshipElements[i][1];
                String order = (String)relationshipElements[i][2];
                if(!order.equals(SINGLE) && !order.equals(MULTIPLE))
                    throw new RuntimeException("INVALID ORDER TYPE " + order);

                relationships.put(name, new Vector());
                relationshipOrder.put(name, order);
                complements.put(name, complement);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Object getDataElement(String elementName) {
        if(!fieldTypes.containsKey(elementName))
            throw new RuntimeException("NON-EXISTENT FIELD NAME " + elementName);
        return fields.get(elementName);
    }

    public final void setDataElement(String elementName, Object value) {
        if(value == null && fields.get(elementName) == null)
            return; // return if newvalue == oldvalue == null

        try {
            if(value.equals(fields.get(elementName)))
                return;
        } catch(NullPointerException e) {
        }
        Object invokerMarker = new Object();
        if(originalInvoker == null)
            originalInvoker = invokerMarker;
        try {
            if(!fieldTypes.containsKey(elementName))
                throw new RuntimeException("NON-EXISTENT FIELD NAME " + elementName);
            if(value != null && !Class.forName((String)fieldTypes.get(elementName)).isInstance(value)) {
                LOG.println("value is " + value.getClass().getName());
                LOG.println("Class.forName((String)fieldTypes.get(elementName)) is " + Class.forName((String)fieldTypes.get(elementName)));
                throw new RuntimeException("BAD DATATYPE IN SET " + elementName);
            }
            if(value == null) {
                fields.remove(elementName);
            } else {
                fields.put(elementName, value);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        //if (!transactionInProgress)
        fireSynchronizeNode();

        modifiedSet.add(this);
        if(originalInvoker == invokerMarker)
            if(!transactionInProgress())
                syncAll();
    }

    private boolean transactionInProgress() {
        return(numberOfPendingTransactions>0);
    }

    private  static int numberOfPendingTransactions = 0;
    public void beginTransaction() {
        numberOfPendingTransactions++;
    }
    public void commitTransaction() {
        if(numberOfPendingTransactions == 0) throw new RuntimeException("Too many commits");
        numberOfPendingTransactions--;
        syncAll();
    }

    public static Object originalInvoker = null;
    protected static HashSet modifiedSet = new HashSet();

    protected static void printModifiedSet() {
        System.err.println("********* MODIFIED SET CONTENTS ** START *********");
        Iterator it = modifiedSet.iterator();
        while(it.hasNext())
            System.err.println((IRelationshipNode)it.next());
        System.err.println("********* MODIFIED SET CONTENTS ** END *********");
    }

    private boolean isSafeToAccess(String relationshipName, IRelationshipNode relationship) {
        if(relationship == null) return false; 
        else if(!relationships.containsKey(relationshipName)) {
            WARNING.println("NON-EXISTENT RELATIONSHIP TYPE");
            return false;
        } else return true;
    }

    private boolean isSafeToAdd(String relationshipName, IRelationshipNode relationship) {
        if(!isSafeToAccess(relationshipName, relationship))
            return false;
        else if(((Vector)relationships.get(relationshipName)).contains(relationship)) {
            //LOG.println("ATTEMPTING TO ADD AN EXISTING RELATIONSHIP " + relationship);
            return false; 
        } else return true;
    }

    private boolean isSafeToRemove(String relationshipName, IRelationshipNode relationship) {
        if(!isSafeToAccess(relationshipName, relationship))
            return false;
        else if(!((Vector)relationships.get(relationshipName)).contains(relationship)) {
            //LOG.println("ATTEMPTING TO REMOVE NON-EXISTING RELATIONSHIP " + relationship);
            return false; 
        } else return true;
    }

    public void addRelationship(String relationshipName, IRelationshipNode relationship) {
        if(!isSafeToAdd(relationshipName, relationship)) return;
        int position = ((Vector)relationships.get(relationshipName)).size();
        insertRelationshipAt(relationshipName, relationship, position);
    }

    public IRelationshipNode getRelationship(String relationshipName) {
        if(relationshipOrder.get(relationshipName).equals(MULTIPLE))
            throw new RuntimeException("Cannot invoke this method in relationship of order MULTIPLE");
        return getRelationshipAt(relationshipName, 0);
    }

    public void setRelationship(String relationshipName, IRelationshipNode relationship) {
        if(!isSafeToAdd(relationshipName, relationship))
            return;
        if(relationshipOrder.get(relationshipName).equals(MULTIPLE))
            throw new RuntimeException("Cannot invoke this method in relationship of order MULTIPLE");
        removeAllRelationshipElements(relationshipName);
        addRelationship(relationshipName, relationship);
    }

    public void insertRelationshipAt(String relationshipName, IRelationshipNode relationship, int position) {
        if(!isSafeToAdd(relationshipName, relationship))
            return;
        Object invokerMarker = new Object();
        if(originalInvoker == null)
            originalInvoker = invokerMarker;

        if(relationshipOrder.get(relationshipName).equals(SINGLE)) {
            Vector vec = (Vector)relationships.get(relationshipName);
            if(vec.size() > 1)
                throw new RuntimeException("Single Order relationship has multiple elements !!");
            for(int i=0; i<vec.size(); i++) {
                IRelationshipNode model = (IRelationshipNode)vec.elementAt(i);
                removeRelationship(relationshipName, model);
            }
            position = 0;
        }
        ((Vector)relationships.get(relationshipName)).insertElementAt(relationship, position);

        relationship.addRelationship((String)complements.get(relationshipName), this);
        modifiedSet.add(this);
        if(originalInvoker == invokerMarker)
            if(!transactionInProgress())
                syncAll();
    }

    public void removeRelationship(String relationshipName, IRelationshipNode relationship) {
        if(!isSafeToRemove(relationshipName, relationship))
            return;
        Object invokerMarker = new Object();
        if(originalInvoker == null)
            originalInvoker = invokerMarker;
        ((Vector)relationships.get(relationshipName)).remove(relationship);
        relationship.removeRelationship((String)complements.get(relationshipName), this);
        modifiedSet.add(this);
        if(originalInvoker == invokerMarker)
            if(!transactionInProgress())
                syncAll();
    }

    public IRelationshipNode getRelationshipAt(String relationshipName, int index) {
        if(!relationships.containsKey(relationshipName))
            throw new RuntimeException("NON-EXISTENT RELATIONSHIP");
        else if(((Vector)relationships.get(relationshipName)).size() <= index)
            return null;
        else
            return(IRelationshipNode)((Vector)relationships.get(relationshipName)).elementAt(index);
    }

    public int getRelationshipCount(String relationshipName) {
        if(!relationships.containsKey(relationshipName))
            throw new RuntimeException("NON-EXISTENT RELATIONSHIP");
        else
            return((Vector)relationships.get(relationshipName)).size();
    }

    public boolean containsRelationship(String relationshipName, IRelationshipNode relationship) {
        if(!relationships.containsKey(relationshipName))
            throw new RuntimeException("NON-EXISTENT RELATIONSHIP");
        else
            return((Vector)relationships.get(relationshipName)).contains(relationship);
    }

    public int getIndexOf(String relationshipName, IRelationshipNode node) {
        int count = getRelationshipCount(relationshipName); 
        for(int i=0; i<count; i++)
            if(getRelationshipAt(relationshipName, i) == node)
                return i;
        return -1;
    }

    public void removeAllRelationshipElements(String relationshipName) {
        if(!relationships.containsKey(relationshipName))
            throw new RuntimeException("NON-EXISTENT RELATIONSHIP");
        else {
            Vector vec = ((Vector)relationships.get(relationshipName));
            for(int i=0; i<vec.size(); i++) {
                IRelationshipNode node = (IRelationshipNode)vec.elementAt(i);
                removeRelationship(relationshipName, node);
            }
        }
    }

    public static void syncAll() {
        originalInvoker = null;
        Iterator it = modifiedSet.iterator();
        while(it.hasNext()) {
            IRelationshipNode next = (IRelationshipNode)it.next();
            next.handleChanged();               
        }
        modifiedSet.clear();
    }

    public void fireSynchronizeNode() {
        Enumeration keys = relationships.keys();
        while(keys.hasMoreElements()) {
            String relationship = (String)keys.nextElement();
            Vector vec = (Vector)relationships.get(relationship);
            for(int i=0; i<vec.size(); i++) {
                IRelationshipNode model = (IRelationshipNode)vec.elementAt(i);
                model.neighborChanged(this);
            }
        }
    }

    public void fireDying() {
        Enumeration keys = relationships.keys();
        while(keys.hasMoreElements()) {
            String relationship = (String)keys.nextElement();
            Vector vec = (Vector)relationships.get(relationship);
            for(int i=0; i<vec.size(); i++) {
                IRelationshipNode model = (IRelationshipNode)vec.elementAt(i);
                model.neighborDying(this);
            }
        }
    }
}
