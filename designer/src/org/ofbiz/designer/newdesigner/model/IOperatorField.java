package org.ofbiz.designer.newdesigner.model;

import java.util.*;
import org.ofbiz.designer.util.*;

public interface IOperatorField extends IBaseModel{
	public String getID();
	public String getCondition();
	public void setCondition(String condition);
	public IOperatorModel getOperator();
	public void setOperator(IOperatorModel model);
	public IOperatorModel getParentOperator();
	public void setParentOperator(IOperatorModel model);
	public IOperatorModel createOperator(String operatorType, String operatorID, boolean terminal);
}

