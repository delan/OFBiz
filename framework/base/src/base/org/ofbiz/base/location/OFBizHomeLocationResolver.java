/*
 * Copyright 2001-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.base.location;

import java.net.MalformedURLException;
import java.net.URL;

import org.ofbiz.base.util.UtilURL;

/**
 * A special location resolver that uses Strings like URLs, but with more options 
 *
 */

public class OFBizHomeLocationResolver implements LocationResolver {
    
    public static final String envName = "ofbiz.home";
    
    public URL resolveLocation(String location) throws MalformedURLException {
        String propValue = System.getProperty(envName);
        if (propValue == null) {
            String errMsg = "The Java environment (-Dxxx=yyy) variable with name " + envName + " is not set, cannot resolve location.";
            throw new MalformedURLException(errMsg);
        }

        StringBuffer baseLocation = new StringBuffer(FlexibleLocation.stripLocationType(location));
        
        // if there is not a forward slash between the two, add it
        if (baseLocation.charAt(0) != '/' && propValue.charAt(propValue.length() - 1) != '/') {
            baseLocation.insert(0, '/');
        }
        
        baseLocation.insert(0, propValue);
        
        return UtilURL.fromFilename(baseLocation.toString());
    }
}
