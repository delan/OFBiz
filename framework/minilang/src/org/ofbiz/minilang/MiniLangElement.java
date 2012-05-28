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
package org.ofbiz.minilang;

import org.ofbiz.minilang.artifact.ArtifactInfoContext;
import org.ofbiz.base.util.Debug;
import org.ofbiz.minilang.method.MethodContext;
import org.w3c.dom.Element;

/**
 * A single Mini-language element. This class is the superclass for all XML element models.
 */
public class MiniLangElement {

    // This must be private so subclasses cannot reference it.
    private static final String module = MiniLangElement.class.getName();

    private final Object lineNumber;
    protected final SimpleMethod simpleMethod;
    private final String tagName;

    public MiniLangElement(Element element, SimpleMethod simpleMethod) {
        this.lineNumber = element.getUserData("startLine");
        this.simpleMethod = simpleMethod;
        this.tagName = element.getTagName().intern();
    }

    public void gatherArtifactInfo(ArtifactInfoContext aic) {
    }

    public String getLineNumber() {
        return this.lineNumber == null ? "unknown" : this.lineNumber.toString();
    }

    public SimpleMethod getSimpleMethod() {
        return this.simpleMethod;
    }

    public String getTagName() {
        return this.tagName;
    }

    public void outputTraceMessage(MethodContext methodContext, String... messages) {
        String lineSep = System.getProperty("line.separator");
        StringBuilder buf = new StringBuilder(getSimpleMethod().getFileName());
        buf.append(", Line ").append(getLineNumber()).append(" <").append(getTagName()).append("> element: ");
        for (int i = 0; i < messages.length; i++) {
            buf.append(messages[i]);
            if (i < messages.length - 1 && messages.length > 1) {
                buf.append(lineSep);
            }
        }
        Debug.log(methodContext.getTraceLogLevel(), null, buf.toString(), module);
    }

    @Override
    public String toString() {
        return "<".concat(this.tagName).concat(">");
    }
}
