/*******************************************************************************
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
 *******************************************************************************/
package org.ofbiz.entity;

/**
 * Thrown when there is a problem with the entity engine configuration.
 *
 * @see <code>entity-config.xsd</code>
 */
@SuppressWarnings("serial")
public class GenericEntityConfException extends GenericEntityException {

    public GenericEntityConfException() {
        super();
    }

    public GenericEntityConfException(String str) {
        super(str);
    }

    public GenericEntityConfException(String str, Throwable nested) {
        super(str, nested);
    }
}
