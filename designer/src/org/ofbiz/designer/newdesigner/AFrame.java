/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 3:52:40 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import org.ofbiz.designer.util.WFFrame;

import java.awt.*;

class AFrame extends WFFrame {
    public AFrame(String title) {
        super(title);
    }
    public Rectangle getDefaultBounds() {
        return new Rectangle(130, 130, 647, 375);
    }
}
