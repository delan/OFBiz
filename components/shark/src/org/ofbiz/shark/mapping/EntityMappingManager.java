/*
 * $Id: EntityMappingManager.java,v 1.1 2004/04/22 15:41:05 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.shark.mapping;

import org.ofbiz.shark.transaction.JtaTransaction;

import org.enhydra.shark.api.internal.mappersistence.MappingManager;
import org.enhydra.shark.api.internal.mappersistence.ParticipantMappings;
import org.enhydra.shark.api.internal.mappersistence.ApplicationMappings;
import org.enhydra.shark.api.internal.mappersistence.ScriptsMappings;
import org.enhydra.shark.api.internal.working.CallbackUtilities;
import org.enhydra.shark.api.RootException;
import org.enhydra.shark.api.MappingTransaction;

/**
 * Shark Mapping Manager Implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class EntityMappingManager implements MappingManager {

    public static final String module = EntityMappingManager.class.getName();
    protected CallbackUtilities callBack = null;

    public void configure(CallbackUtilities callbackUtilities) throws RootException {
        this.callBack = callbackUtilities;
    }

    public ParticipantMappings getParticipantMappings() throws RootException {
        return new EntityParticipantMappings();
    }

    public ApplicationMappings getApplicationMappings() throws RootException {
        return new EntityApplicationMappings();
    }

    public ScriptsMappings getToolAgentsMappings() throws RootException {
        return new EntityScriptMappings();
    }

    public MappingTransaction getMappingTransaction() throws RootException {
        return new JtaTransaction();
    }
}
