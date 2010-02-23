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
package org.ofbiz.base.util.collections.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;

public class FlexibleMapAccessorTests extends TestCase {

    public FlexibleMapAccessorTests(String name) {
        super(name);
    }

    // These tests rely upon FlexibleStringExpander, so they
    // should follow the FlexibleStringExpander tests.
    public void testFlexibleMapAccessor() {
        String compare = "Hello World!";
        Map<String, Object> testMap = new HashMap<String, Object>();
        FlexibleMapAccessor<String> fma = FlexibleMapAccessor.getInstance("parameters.var");
        fma.put(testMap, "World");
        FlexibleStringExpander fse = FlexibleStringExpander.getInstance("Hello ${parameters.var}!");
        assertEquals("UEL auto-vivify Map", compare, fse.expandString(testMap));
        fma = FlexibleMapAccessor.getInstance("parameters.someList[+0]");
        fma.put(testMap, "World");
        fse = FlexibleStringExpander.getInstance("Hello ${parameters.someList[0]}!");
        assertEquals("UEL auto-vivify List", compare, fse.expandString(testMap));
    }
}
