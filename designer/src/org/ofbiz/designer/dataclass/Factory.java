/**
 * Factory.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
 *
 * Copyright 1999 by ObjectSpace, Inc.,
 * 14850 Quorum Dr., Dallas, TX, 75240 U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of ObjectSpace, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with ObjectSpace.
 */

package org.ofbiz.designer.dataclass;

public class Factory
  {
  public static IType newType()
    {
    return new org.ofbiz.designer.dataclass.Type();
    }

  public static IParent newParent()
    {
    return new org.ofbiz.designer.dataclass.Parent();
    }

  public static IUrl newUrl()
    {
    return new org.ofbiz.designer.dataclass.Url();
    }

  public static IMethodList newMethodList()
    {
    return new org.ofbiz.designer.dataclass.MethodList();
    }

  public static IMethod newMethod()
    {
    return new org.ofbiz.designer.dataclass.Method();
    }

  public static IField newField()
    {
    return new org.ofbiz.designer.dataclass.Field();
    }

  public static IFieldList newFieldList()
    {
    return new org.ofbiz.designer.dataclass.FieldList();
    }

  public static IParametersList newParametersList()
    {
    return new org.ofbiz.designer.dataclass.ParametersList();
    }

  public static IDataClass newDataClass()
    {
    return new org.ofbiz.designer.dataclass.DataClass();
    }

  public static ISimpleTypeOrUrl newSimpleTypeOrUrl()
    {
    return new org.ofbiz.designer.dataclass.SimpleTypeOrUrl();
    }

  public static IParameter newParameter()
    {
    return new org.ofbiz.designer.dataclass.Parameter();
    }

  }