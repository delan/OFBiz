
//			javax.swing.JComboBox has a bug.
//			it resets the selectedItem index on the ComboBoxModel when setModel is called.
//			This class is a workaround for that problem

package org.ofbiz.designer.util;

import javax.swing.*;
import java.util.*;

public class ModifiedJComboBox extends JComboBox {
    public ModifiedJComboBox(Vector vec) {
        super(vec);
    }

    public void setModel(ComboBoxModel model) {
        Object selected = model.getSelectedItem();
        super.setModel(model);
        model.setSelectedItem(selected);
    }
}
