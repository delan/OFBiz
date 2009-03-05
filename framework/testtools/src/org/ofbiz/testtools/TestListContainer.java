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
package org.ofbiz.testtools;

import org.ofbiz.base.component.AlreadyLoadedException;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.config.GenericConfigException;
import org.ofbiz.base.config.ResourceHandler;
import org.ofbiz.base.container.ComponentContainer;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilXml;

import javolution.util.FastList;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A Container implementation to run the tests configured through this testtools stuff.
 */
public class TestListContainer implements Container {

    public static final String module = TestListContainer.class.getName();

    private String outputLocation;
    private String mode = "text";

    public static final class FoundTest {
        public final String componentName;
        public final String caseName;

        public FoundTest(String componentName, String caseName) {
            this.componentName = componentName;
            this.caseName = caseName;
        }
    }

    /**
     * @see org.ofbiz.base.container.Container#init(java.lang.String[], java.lang.String)
     */
    public void init(String[] args, String configFile) {
        this.outputLocation = args[0];
        for (int i = 1; i < args.length; i++) {
            if ("-ant".equals(args[i])) {
                mode = "ant";
            } else if ("-text".equals(args[i])) {
                mode = "text";
            }
        }
    }

    public boolean start() throws ContainerException {
        List<FoundTest> foundTests = FastList.newInstance();
        for (ComponentConfig.TestSuiteInfo testSuiteInfo: ComponentConfig.getAllTestSuiteInfos(null)) {
            String componentName = testSuiteInfo.componentConfig.getComponentName();
            ResourceHandler testSuiteResource = testSuiteInfo.createResourceHandler();

            try {
                Document testSuiteDocument = testSuiteResource.getDocument();
                Element documentElement = testSuiteDocument.getDocumentElement();
                for (Element testCaseElement : UtilXml.childElementList(documentElement, UtilMisc.toSet("test-case", "test-group"))) {
                    String caseName = testCaseElement.getAttribute("case-name");
                    foundTests.add(new FoundTest(componentName, caseName));
                }
            } catch (GenericConfigException e) {
                String errMsg = "Error reading XML document from ResourceHandler for loader [" + testSuiteResource.getLoaderName() + "] and location [" + testSuiteResource.getLocation() + "]";
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
        try {
            FileOutputStream fout = new FileOutputStream(outputLocation + ".tmp");
            PrintStream pout = new PrintStream(fout);
            if ("text".equals(mode)) {
                for (FoundTest foundTest: foundTests) {
                    pout.format("%s:%s\n", foundTest.componentName, foundTest.caseName);
                }
            } else if ("ant".equals(mode)) {
                pout.println("<project default=\"all-tests\">");
                pout.print(" <target name=\"all-tests\" depends=\"");
                for (int i = 0; i < foundTests.size(); i++) {
                    if (i != 0) pout.print(',');
                    FoundTest foundTest = foundTests.get(i);
                    pout.format("%s:%s", foundTest.componentName, foundTest.caseName.replace(' ', '_'));
                }
                pout.println("\"/>\n");
                for (int i = 0; i < foundTests.size(); i++) {
                    FoundTest foundTest = foundTests.get(i);
                    pout.format(" <target name=\"%1$s:%2$s\">\n  <ant antfile=\"build.xml\" target=\"run-single-test\">\n   <property name=\"test.component\" value=\"%1$s\"/>\n   <property name=\"test.case\" value=\"%3$s\"/>\n  </ant>\n </target>\n", foundTest.componentName, foundTest.caseName.replace(' ', '_'), foundTest.caseName);
                }
                pout.println("</project>");
            }
            pout.close();
            fout.close();
            new File(outputLocation + ".tmp").renameTo(new File(outputLocation));
        } catch (IOException e) {
            Debug.logError(e, module);
            throw (IllegalArgumentException) new IllegalArgumentException(e.getMessage()).initCause(e);
        }

        return true;
    }

    public void stop() throws ContainerException {
    }
}
