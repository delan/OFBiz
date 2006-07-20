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
package org.ofbiz.service;

import java.util.Map;
import java.io.Serializable;

/**
 * Generic Requester Interface
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> *
 * @version    $Rev$
 * @since      2.0
 */
public interface GenericRequester extends Serializable {

    /** 
     * Receive the result of an asynchronous service call
     * @param result Map of name, value pairs composing the result
     */
    public void receiveResult(Map result);
    
    /**
     * Receive an exception (Throwable) from an asynchronous service cell
     * @param t The Throwable which was received
     */
    public void receiveThrowable(Throwable t);
}

