package org.ofbiz.core.entity;

import org.ofbiz.core.entity.model.ModelEntity;
import org.ofbiz.core.entity.model.ModelReader;

/**
 * <p><b>Title:</b> Generic Entity Reference
 * <p><b>Description:</b> Used to connect two unrelated entities.
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     <a href='mailto:chris_maurer@altavista.com'>Chris Maurer</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    Mon Nov 5, 2001
 *@version    1.0
 */
public class EntityClause {
  private String firstEntity = "";
  private String secondEntity = "";
  private String firstField = "";
  private String secondField = "";
  private ModelEntity firstModelEntity = null;
  private ModelEntity secondModelEntity = null;
  private EntityOperator interFieldOperation = null;
  private EntityOperator intraFieldOperation = null;
  
  public EntityClause(){
  }
  
  public EntityClause(String firstEntity, String secondEntity, String firstField, String secondField, EntityOperator interFieldOperation, EntityOperator intraFieldOperation) {
    this.firstEntity = firstEntity;
    this.secondEntity = secondEntity;
    this.firstField = firstField;
    this.secondField = secondField;
    this.interFieldOperation = interFieldOperation;
    this.intraFieldOperation = intraFieldOperation;
  }
  
  public String getFirstEntity(){
    return firstEntity;
  }
  
  public String getSecondEntity(){
    return secondEntity;
  }
  
  public String getFirstField(){
    return firstField;
  }
  
  public String getSecondField(){
    return secondField;
  }
  
  public EntityOperator getInterFieldOperation(){
    return interFieldOperation;
  }
  
  public EntityOperator getIntraFieldOperation(){
    return intraFieldOperation;
  }
  
  public void setFirstEntity(String firstEntity){
    this.firstEntity = firstEntity;
  }
  
  public void setSecondEntity(String secondEntity){
    this.secondEntity = secondEntity;
  }
  
  public void setFirstField(String firstField){
    this.firstField = firstField;
  }
  
  public void setSecondField(String secondField){
    this.secondField = secondField;
  }
  
  public void setInterFieldOperation(EntityOperator interFieldOperation){
    this.interFieldOperation = interFieldOperation;
  }
  
  public void setIntraFieldOperation(EntityOperator intraFieldOperation){
    this.intraFieldOperation = intraFieldOperation;
  }
  
  //--  Protected Methods  - for internal use only --//
  protected void setModelEntities(ModelReader modelReader){
    firstModelEntity = (ModelEntity)modelReader.getModelEntity(firstEntity);
    secondModelEntity = (ModelEntity)modelReader.getModelEntity(secondEntity);
  }
  
  protected ModelEntity getFirstModelEntity(){
    return firstModelEntity;
  }
  
  protected ModelEntity getSecondModelEntity(){
    return secondModelEntity;
  }
  
}
