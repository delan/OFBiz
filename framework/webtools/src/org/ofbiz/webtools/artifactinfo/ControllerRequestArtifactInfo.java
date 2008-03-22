/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.webtools.artifactinfo;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilObject;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.webapp.control.ConfigXMLReader;

/**
 *
 */
public class ControllerRequestArtifactInfo extends ArtifactInfoBase {
    public static final String module = ControllerRequestArtifactInfo.class.getName();

    protected URL controllerXmlUrl;
    protected String requestUri;
    
    protected Map<String, Object> requestInfoMap;
    
    protected ServiceArtifactInfo serviceCalledByRequestEvent = null;
    protected Set<ControllerRequestArtifactInfo> requestsThatAreResponsesToThisRequest = FastSet.newInstance();
    protected Set<ControllerViewArtifactInfo> viewsThatAreResponsesToThisRequest = FastSet.newInstance();
    
    public ControllerRequestArtifactInfo(URL controllerXmlUrl, String requestUri, ArtifactInfoFactory aif) throws GeneralException {
        super(aif);
        this.controllerXmlUrl = controllerXmlUrl;
        this.requestUri = requestUri;
        
        this.requestInfoMap = aif.getControllerRequestInfoMap(controllerXmlUrl, requestUri);
   
        if (this.requestInfoMap == null) {
            throw new GeneralException("Controller request with name [" + requestUri + "] is not defined in controller file [" + controllerXmlUrl + "].");
        }

        if (this.requestInfoMap == null) {
            throw new GeneralException("Could not find Controller Request [" + requestUri + "] at URL [" + controllerXmlUrl.toExternalForm() + "]");
        }
    }
    
    /** note this is mean to be called after the object is created and added to the ArtifactInfoFactory.allControllerRequestInfos in ArtifactInfoFactory.getControllerRequestArtifactInfo */
    public void populateAll() throws GeneralException {
        // populate serviceCalledByRequestEvent, requestsThatAreResponsesToThisRequest, viewsThatAreResponsesToThisRequest and related reverse maps
        
        if ("service".equals(this.requestInfoMap.get(ConfigXMLReader.EVENT_TYPE))) {
            String serviceName = (String) this.requestInfoMap.get(ConfigXMLReader.EVENT_METHOD);
            try {
                this.serviceCalledByRequestEvent = this.aif.getServiceArtifactInfo(serviceName);
                if (this.serviceCalledByRequestEvent != null) {
                    // add the reverse association
                    UtilMisc.addToSetInMap(this, aif.allRequestInfosReferringToServiceName, this.serviceCalledByRequestEvent.getUniqueId());
                }
            } catch (GeneralException e) {
                Debug.logWarning(e.toString(), module);
            }
        }
        
        Map<String, String> responseMap = (Map<String, String>) this.requestInfoMap.get(ConfigXMLReader.RESPONSE_MAP);
        for (String responseValue: responseMap.values()) {
            if (responseValue.startsWith("view:")) {
                String viewUri = responseValue.substring(5);
                if (viewUri.startsWith("/")) {
                    viewUri = viewUri.substring(1);
                }
                try {
                    ControllerViewArtifactInfo artInfo = this.aif.getControllerViewArtifactInfo(controllerXmlUrl, viewUri);
                    this.viewsThatAreResponsesToThisRequest.add(artInfo);
                    // add the reverse association
                    UtilMisc.addToSetInMap(this, this.aif.allRequestInfosReferringToView, artInfo.getUniqueId());
                } catch (GeneralException e) {
                    Debug.logWarning(e.toString(), module);
                }
            } else if (responseValue.startsWith("request:")) {
                String otherRequestUri = responseValue.substring(8);
                if (otherRequestUri.startsWith("/")) {
                    otherRequestUri = otherRequestUri.substring(1);
                }
                try {
                    ControllerRequestArtifactInfo artInfo = this.aif.getControllerRequestArtifactInfo(controllerXmlUrl, otherRequestUri);
                    this.requestsThatAreResponsesToThisRequest.add(artInfo);
                    UtilMisc.addToSetInMap(this, this.aif.allRequestInfosReferringToRequest, artInfo.getUniqueId());
                } catch (GeneralException e) {
                    Debug.logWarning(e.toString(), module);
                }
            } else if (responseValue.startsWith("request-redirect:")) {
                String otherRequestUri = responseValue.substring(17);
                ControllerRequestArtifactInfo artInfo = this.aif.getControllerRequestArtifactInfo(controllerXmlUrl, otherRequestUri);
                this.requestsThatAreResponsesToThisRequest.add(artInfo);
                UtilMisc.addToSetInMap(this, this.aif.allRequestInfosReferringToRequest, artInfo.getUniqueId());
            } else if (responseValue.startsWith("request-redirect-noparam:")) {
                String otherRequestUri = responseValue.substring(25);
                ControllerRequestArtifactInfo artInfo = this.aif.getControllerRequestArtifactInfo(controllerXmlUrl, otherRequestUri);
                this.requestsThatAreResponsesToThisRequest.add(artInfo);
                UtilMisc.addToSetInMap(this, this.aif.allRequestInfosReferringToRequest, artInfo.getUniqueId());
            }
        }
    }
    
    public URL getControllerXmlUrl() {
        return this.controllerXmlUrl;
    }
    
    public String getRequestUri() {
        return this.requestUri;
    }
    
    public String getDisplayName() {
        String location = UtilURL.getOfbizHomeRelativeLocation(this.controllerXmlUrl);
        if (location.endsWith("/WEB-INF/controller.xml")) {
            location = location.substring(0, location.length() - 23);
        }
        return this.requestUri + " (" + location + ")";
    }
    
    public String getDisplayType() {
        return "Controller Request";
    }
    
    public String getType() {
        return ArtifactInfoFactory.ControllerRequestInfoTypeId;
    }
    
    public String getUniqueId() {
        return this.controllerXmlUrl.toExternalForm() + "#" + this.requestUri;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof ControllerRequestArtifactInfo) {
            ControllerRequestArtifactInfo that = (ControllerRequestArtifactInfo) obj;
            return UtilObject.equalsHelper(this.controllerXmlUrl, that.controllerXmlUrl) && UtilObject.equalsHelper(this.requestUri, that.requestUri);
        } else {
            return false;
        }
    }
    
    /** Get the Services that are called by this Request */
    public ServiceArtifactInfo getServiceCalledByRequestEvent() {
        return serviceCalledByRequestEvent;
    }
    
    public Set<ControllerRequestArtifactInfo> getRequestsThatAreResponsesToThisRequest() {
        return this.requestsThatAreResponsesToThisRequest;
    }
    
    public Set<ControllerRequestArtifactInfo> getRequestsThatThisRequestIsResponsTo() {
        return this.aif.allRequestInfosReferringToRequest.get(this.getUniqueId());
    }
    
    public Set<ControllerViewArtifactInfo> getViewsThatAreResponsesToThisRequest() {
        return this.viewsThatAreResponsesToThisRequest;
    }
}
