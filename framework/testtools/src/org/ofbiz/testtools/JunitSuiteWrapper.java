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

import java.util.Iterator;
import java.util.List;

import javolution.util.FastList;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.config.GenericConfigException;
import org.ofbiz.base.config.ResourceHandler;
import org.ofbiz.base.util.Debug;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Use this class in a JUnit test runner to prepare the TestSuite.
 */
public class JunitSuiteWrapper {

    public static final String module = JunitSuiteWrapper.class.getName();
 
    protected List<ModelTestSuite> modelTestSuiteList = FastList.newInstance();
 
    public JunitSuiteWrapper(String componentName, String suiteName, String testCase) {
        for (ComponentConfig.TestSuiteInfo testSuiteInfo: ComponentConfig.getAllTestSuiteInfos(componentName)) {
            ResourceHandler testSuiteResource = testSuiteInfo.createResourceHandler();

            try {
                Document testSuiteDocument = testSuiteResource.getDocument();
                // TODO create TestSuite object based on this that will contain its TestCase objects

                Element documentElement = testSuiteDocument.getDocumentElement();
                ModelTestSuite modelTestSuite = new ModelTestSuite(documentElement, testCase);

                // make sure there are test-cases configured for the suite
                if (suiteName != null && !modelTestSuite.getSuiteName().equals(suiteName)) {
                    continue;
                }
                if (modelTestSuite.getTestList().size() > 0) {
                    this.modelTestSuiteList.add(modelTestSuite);
                }
            } catch (GenericConfigException e) {
                String errMsg = "Error reading XML document from ResourceHandler for loader [" + testSuiteResource.getLoaderName() + "] and location [" + testSuiteResource.getLocation() + "]";
                Debug.logError(e, errMsg, module);
                throw new IllegalArgumentException(errMsg);
            }
        }
    }

    @Deprecated
    public void populateTestSuite(TestSuite suite) {
        for (ModelTestSuite modelTestSuite: this.modelTestSuiteList) {
            for (Test tst: modelTestSuite.getTestList()) {
                suite.addTest(tst);
            }
        }
    }

    public List<TestSuite> makeTestSuites() {
        List<TestSuite> testSuites = FastList.newInstance();

        for (ModelTestSuite modelTestSuite: this.modelTestSuiteList) {
            TestSuite suite = new TestSuite();
            suite.setName(modelTestSuite.getSuiteName());
            for (Test tst: modelTestSuite.getTestList()) {
                suite.addTest(tst);
            }
            testSuites.add(suite);
        }

        return testSuites;
    }
 
    public List<Test> getAllTestList() {
        List<Test> allTestList = FastList.newInstance();

        for (ModelTestSuite modelTestSuite: this.modelTestSuiteList) {
            for (Test tst: modelTestSuite.getTestList()) {
                allTestList.add(tst);
            }
        }
 
        return allTestList;
    }
}
