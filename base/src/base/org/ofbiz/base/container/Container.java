/*
 * $Id: Container.java,v 1.3 2004/06/22 19:00:42 ajzeneski Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.base.container;

/**
 * Container - Interface for containers
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
  *@version    $Revision: 1.3 $
 * @since      3.0
 */
public interface Container {

    /** Initialize the container
     *
     * @param args Arguements from calling class
     * @throws ContainerException
     */
    public void init(String[] args) throws ContainerException;
    
    /**
     * Start the container
     * 
     * @param configFileLocation Location of master OFBiz configuration file
     * @return true if server started
     * @throws ContainerException
     */
    public boolean start(String configFileLocation) throws ContainerException;

    /**
     * Stop the container
     *     
     * @throws ContainerException
     */
    public void stop() throws ContainerException;
}
