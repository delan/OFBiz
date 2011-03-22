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
package org.ofbiz.base.container;

/**
 * An OFBiz container. A container can be thought of as a background process.
 * 
 * <p>
 * When OFBiz starts, the main thread will create the <code>Container</code> instance and
 * then call the container's <code>init</code> method. If the method returns without
 * throwing an exception the container will be added to a list of initialized containers.
 * After all instances have been created and initialized, the main thread will call the
 * <code>start</code> method of each container in the list. When OFBiz shuts down, a
 * separate shutdown thread will call the <code>stop</code> method of each container.
 * Implementations should anticipate asynchronous calls to the methods by different
 * threads.
 * </p>
 * 
 */
public interface Container {

    /** Initialize the container.
     *
     * @param args Command-line arguments.
     * @param configFile Location of the configuration file used to load this container.
     * @throws ContainerException If an error was encountered. Throwing this exception
     * will halt container loading, so it should be thrown only when other containers
     * might depend on this one.
     */
    public void init(String[] args, String configFile) throws ContainerException;

    /**
     * Start the container process.
     *
     * @return <code>true</code> if the process started.
     * @throws ContainerException If an error was encountered.
     */
    public boolean start() throws ContainerException;

    /**
     * Stop the container process.
     *
     * @throws ContainerException If an error was encountered.
     */
    public void stop() throws ContainerException;
}
