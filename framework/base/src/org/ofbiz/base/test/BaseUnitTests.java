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
package org.ofbiz.base.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.Converter;
import org.ofbiz.base.conversion.DateTimeConverters;
import org.ofbiz.base.util.ComparableRange;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;

public class BaseUnitTests extends TestCase {

    public BaseUnitTests(String name) {
        super(name);
    }

    public void testDebug() {
        Debug.set(Debug.VERBOSE, true);
        assertTrue(Debug.verboseOn());

        Debug.set(Debug.VERBOSE, false);
        assertFalse(Debug.verboseOn());

        Debug.set(Debug.INFO, true);
        assertTrue(Debug.infoOn());
    }

    public void testFormatPrintableCreditCard_1() {
        assertEquals("test 4111111111111111 to ************111",
                "************1111",
                UtilFormatOut.formatPrintableCreditCard("4111111111111111"));
    }

    public void testFormatPrintableCreditCard_2() {
        assertEquals("test 4111 to 4111",
                "4111",
                UtilFormatOut.formatPrintableCreditCard("4111"));
    }

    public void testFormatPrintableCreditCard_3() {
        assertEquals("test null to null",
                null,
                UtilFormatOut.formatPrintableCreditCard(null));
    }
    public void testIsDouble_1() {
        assertFalse(UtilValidate.isDouble("10.0", true, true, 2, 2));
    }
    public void testIsFloat_1() {
        assertFalse(UtilValidate.isFloat("10.0", true, true, 2, 2));
    }
    public void testIsDouble_2() {
        assertTrue(UtilValidate.isDouble("10.000", true, true, 3, 3));
    }
    public void testIsFloat_2() {
        assertTrue(UtilValidate.isFloat("10.000", true, true, 3, 3));
    }

    public void testComparableRange() {
        ComparableRange<Integer> pointTest = new ComparableRange<Integer>(1, 1);
        assertTrue("isPoint", pointTest.isPoint());
        assertTrue("equality", pointTest.equals(new ComparableRange<Integer>(1, 1)));
        ComparableRange<Integer> range1 = new ComparableRange<Integer>(3, 1);
        ComparableRange<Integer> range2 = new ComparableRange<Integer>(4, 6);
        assertTrue("after range", range2.after(range1));
        assertTrue("before range", range1.before(range2));
        assertFalse("excludes value", range1.includes(0));
        assertTrue("includes value", range1.includes(1));
        assertTrue("includes value", range1.includes(2));
        assertTrue("includes value", range1.includes(3));
        assertFalse("excludes value", range1.includes(4));
        assertTrue("includes range", range1.includes(pointTest));
        assertFalse("excludes range", range1.includes(range2));
        ComparableRange<Integer> overlapTest = new ComparableRange<Integer>(2, 5);
        assertTrue("overlaps range", range1.overlaps(overlapTest));
        assertTrue("overlaps range", range2.overlaps(overlapTest));
        assertFalse("does not overlap range", range1.overlaps(range2));
        IllegalArgumentException caught = null;
        try {
            @SuppressWarnings("unused")
            ComparableRange<java.util.Date> range3 = new ComparableRange<java.util.Date>(new java.util.Date(), new java.sql.Timestamp(System.currentTimeMillis()));
        } catch (IllegalArgumentException e) {
            caught = e;
        } finally {
            assertNotNull("expected exception", caught);
        }
    }

    public void testFlexibleStringExpander() {
        FlexibleStringExpander fse = FlexibleStringExpander.getInstance(null);
        assertTrue("null FlexibleStringExpander", fse.isEmpty());
        String compare = "Hello World!";
        fse = FlexibleStringExpander.getInstance(compare);
        assertEquals("null context", compare, fse.expandString(null));
        Map<String, Object> testMap = new HashMap<String, Object>();
        testMap.put("var", "World");
        fse = FlexibleStringExpander.getInstance("Hello ${var}!");
        assertTrue("simple replacement", compare.equals(fse.expandString(testMap)));
        testMap.put("nested", "Hello ${var}");
        fse = FlexibleStringExpander.getInstance("${nested}!");
        assertTrue("hidden (runtime) nested replacement", compare.equals(fse.expandString(testMap)));
        fse = FlexibleStringExpander.getInstance("${'Hello ${var}'}!");
        assertTrue("visible nested replacement", compare.equals(fse.expandString(testMap)));
        fse = FlexibleStringExpander.getInstance("${bsh:return \"Hello \" + var + \"!\";}");
        assertTrue("bsh: script", compare.equals(fse.expandString(testMap)));
        fse = FlexibleStringExpander.getInstance("${groovy:return \"Hello \" + var + \"!\";}");
        assertTrue("groovy: script", compare.equals(fse.expandString(testMap)));
        testMap.put("testMap", testMap);
        fse = FlexibleStringExpander.getInstance("Hello ${testMap.var}!");
        assertTrue("UEL integration: Map", compare.equals(fse.expandString(testMap)));
        List<String> testList = new ArrayList<String>();
        testList.add("World");
        testMap.put("testList", testList);
        fse = FlexibleStringExpander.getInstance("Hello ${testList[0]}!");
        assertTrue("UEL integration: List", compare.equals(fse.expandString(testMap)));
        fse = FlexibleStringExpander.getInstance("This is an \\${escaped} expression");
        assertTrue("Escaped expression", "This is an ${escaped} expression".equals(fse.expandString(testMap)));
    }

    // These tests rely upon FlexibleStringExpander, so they
    // should follow the FlexibleStringExpander tests.
    public void testFlexibleMapAccessor() {
        String compare = "Hello World!";
        Map<String, Object> testMap = new HashMap<String, Object>();
        FlexibleMapAccessor<String> fma = FlexibleMapAccessor.getInstance("parameters.var");
        fma.put(testMap, "World");
        FlexibleStringExpander fse = FlexibleStringExpander.getInstance("Hello ${parameters.var}!");
        assertTrue("UEL auto-vivify Map", compare.equals(fse.expandString(testMap)));
        fma = FlexibleMapAccessor.getInstance("parameters.someList[+0]");
        fma.put(testMap, "World");
        fse = FlexibleStringExpander.getInstance("Hello ${parameters.someList[0]}!");
        assertTrue("UEL auto-vivify List " + fse.expandString(testMap), compare.equals(fse.expandString(testMap)));
    }

    public void testDateTimeConverters() throws Exception {
        // Source class = java.util.Date
        java.util.Date utilDate = new java.util.Date();
        long dateMillis = utilDate.getTime();
        Converter<java.util.Date, Long> dateToLong = new DateTimeConverters.DateToLong();
        {
            Long target = dateToLong.convert(utilDate);
            assertEquals("DateToLong", dateMillis, target.longValue());
        }
        Converter<java.util.Date, java.sql.Date> dateToSqlDate = new DateTimeConverters.DateToSqlDate();
        {
            java.sql.Date target = dateToSqlDate.convert(utilDate);
            assertEquals("DateToSqlDate", dateMillis, target.getTime());
        }
        Converter<java.util.Date, String> dateToString = new DateTimeConverters.DateToString();
        {
            String target = dateToString.convert(utilDate);
            assertEquals("DateToString", utilDate.toString(), target);
        }
        Converter<java.util.Date, java.sql.Timestamp> dateToTimestamp = new DateTimeConverters.DateToTimestamp();
        {
            java.sql.Timestamp timestamp = dateToTimestamp.convert(utilDate);
            assertEquals("DateToTimestamp", dateMillis, timestamp.getTime());
        }
        // Source class = java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
        Converter<java.sql.Date, java.util.Date> sqlDateToDate = new DateTimeConverters.SqlDateToDate();
        {
            java.util.Date target = sqlDateToDate.convert(sqlDate);
            assertEquals("SqlDateToDate", sqlDate.getTime(), target.getTime());
        }
        Converter<java.sql.Date, String> sqlDateToString = new DateTimeConverters.SqlDateToString();
        {
            String target = sqlDateToString.convert(sqlDate);
            assertEquals("SqlDateToString", sqlDate.toString(), target);
        }
        Converter<java.sql.Date, java.sql.Timestamp> sqlDateToTimestamp = new DateTimeConverters.SqlDateToTimestamp();
        {
            java.sql.Timestamp target = sqlDateToTimestamp.convert(sqlDate);
            assertEquals("SqlDateToTimestamp", sqlDate.getTime(), target.getTime());
        }
    }

    public void testStringUtil() {
        byte[] testArray = {-1};
        byte[] result = StringUtil.fromHexString(StringUtil.toHexString(testArray));
        assertEquals("Hex conversions", testArray[0], result[0]);
    }

}
