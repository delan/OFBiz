/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 4:03:36 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.designer.newdesigner;

import java.awt.*;

class DNDEngine {
    public static boolean resize() {
        if (xResize == 0 && yResize == 0) return false;
        else return true;
    }
    public static int xResize, yResize;
    public static Rectangle resizeBounds = null;
    public static ContainerView dragObject = null;
}