/*
 * Copyright (c) 2001, 2002, 2003 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.jxtest;



/**
 * JXConstants contains common Strings for OFBiz JXUnit tests. 
 *
 * @author     <a href="mailto:bgpalmer@computer.org">Brett G. Palmer</a>
 * @version    $Revision: 1.1 $
 * @since      1.0
 */
public interface JXConstants
{
	
	public static String TEST_SUCCESS = "success";
	public static String TEST_FAIL = "fail";
	public static String TEST_RESULTS = "results";
	public static String ERROR_MESSAGE = "errMsg";
	
	public static String JX_ROOT_DIRECTORY = ".";
	public static String JX_TEST_DIRECTORY = "testDirectory";
	public static String JX_SCHEMA = "jxuSchemaName";
	public static String JX_ATTRIBUTE_NAME = "name";
	
	public static String SERVICE_MODE_SYNC = "sync";
	public static String SERVICE_MODE_ASYNC = "async";
	public static String SERVICE_MAP_ELEMENT = "map";
	
	public static String MAP_INPUT_NAME = "mapNameIn";
	public static String MAP_OUTPUT_NAME = "mapNameOut";

}