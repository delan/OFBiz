/*
 * Created by IntelliJ IDEA.
 * User: Oliver Wieland
 * Date: Jul 27, 2001
 * Time: 12:58:33 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.ofbiz.wrappers.xml;

import java.util.HashMap;

public class IIDRefBinding {
    private HashMap refs = new HashMap();

    public void setIdRef(String pKey, Object pObject) {
        refs.put( pKey, pObject);
    }

    public Object getIdRef(String pRef) {
        return refs.get( pRef );
    }

    public void removeIdRef(String pKey) {
        if (refs.containsKey( pKey )) {
            refs.remove( refs.get( pKey ));
        }
    }
}
