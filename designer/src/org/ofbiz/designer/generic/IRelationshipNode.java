package org.ofbiz.designer.generic;

public interface IRelationshipNode {
    public void setDataElement(String elementName, Object value);
    public Object getDataElement(String elementName);
    public void addRelationship(String relationshipName, IRelationshipNode relationship);
    public void removeRelationship(String relationshipName, IRelationshipNode relationship);
    public IRelationshipNode getRelationshipAt(String relationshipName, int index);
    public int getRelationshipCount(String relationshipName);
    public int getIndexOf(String relationshipName, IRelationshipNode node);
    public boolean containsRelationship(String relationshipName, IRelationshipNode relationship);
    public void removeAllRelationshipElements(String relationshipName);

    // for single order relationships
    public IRelationshipNode getRelationship(String relationshipName);
    public void setRelationship(String relationshipName, IRelationshipNode relationship);

    public void die();  
    public void beginTransaction();
    public void commitTransaction();

    public void fireSynchronizeNode();
    public void neighborChanged(IRelationshipNode changeSource);
    public void fireDying();
    public void neighborDying(IRelationshipNode changeSource);
    public void handleChanged();
}
