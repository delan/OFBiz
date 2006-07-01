/*
 * $Id: EntityEcaException.java 7425 2006-04-26 23:04:59Z jonesde $
 *
 * Copyright 2003-2006 The Apache Software Foundation
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
package org.ofbiz.entityext.eca;

import org.ofbiz.entity.GenericEntityException;

/**
 * EntityEcaException
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @since      2.1
 */
public class EntityEcaException extends GenericEntityException {

    public EntityEcaException() {
        super();
    }

    public EntityEcaException(String str) {
        super(str);
    }

    public EntityEcaException(String str, Throwable nested) {
        super(str, nested);
    }
}
