package org.ofbiz.designer.generic;

import org.ofbiz.designer.pattern.IGuiModel;
import javax.swing.*;

public interface IComboBoxModel extends MutableComboBoxModel, IGuiModel{
	public int getIndexOf(Object obj);
	public void removeAllElements();
}
