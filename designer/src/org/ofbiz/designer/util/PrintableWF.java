package org.ofbiz.designer.util;

import java.awt.print.Printable;

public interface PrintableWF extends Printable {
	public void prevPage();
	public void nextPage();
}
