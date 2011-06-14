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
package org.ofbiz.jcr;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.w3c.dom.Element;

public interface JCRFactory {

    /**
     *
     * @param configRootElement
     * @throws RepositoryException
     */
    public void initialize(Element configRootElement) throws RepositoryException;

    /**
     *
     * @throws RepositoryException
     */
    public void start() throws RepositoryException;

    /**
     *
     * @param removeRepositoryOnShutdown
     * @throws RepositoryException
     */
    public void stop(boolean removeRepositoryOnShutdown) throws RepositoryException;

    /**
     *
     * @param workspaceName
     * @return
     * @throws RepositoryException
     */
    public Session createSession() throws RepositoryException;

    // public static final String WORKSPACE_NAME =
    // PropsUtil.get(PropsKeys.JCR_WORKSPACE_NAME);
    //
    // public static final String NODE_DOCUMENTLIBRARY =
    // PropsUtil.get(PropsKeys.JCR_NODE_DOCUMENTLIBRARY);

}
