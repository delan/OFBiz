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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.ofbiz.base.conversion.Converter;
import org.ofbiz.base.conversion.Converters;
import org.ofbiz.base.conversion.LocalizedConverter;
import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ScriptUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.collections.FlexibleMapAccessor;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.minilang.method.MethodContext;
import org.ofbiz.minilang.method.MethodObject;
import org.ofbiz.minilang.method.MethodOperation;
import org.ofbiz.minilang.method.callops.CallService;
import org.ofbiz.minilang.method.callops.CallServiceAsynch;
import org.ofbiz.minilang.method.callops.CallSimpleMethod;
import org.ofbiz.minilang.method.callops.SetServiceFields;
import org.ofbiz.minilang.method.conditional.MasterIf;
import org.ofbiz.minilang.method.entityops.EntityAnd;
import org.ofbiz.minilang.method.entityops.EntityCondition;
import org.ofbiz.minilang.method.entityops.EntityCount;
import org.ofbiz.minilang.method.entityops.EntityOne;
import org.ofbiz.minilang.method.entityops.FindByAnd;
import org.ofbiz.minilang.method.entityops.FindByPrimaryKey;
import org.ofbiz.minilang.method.entityops.GetRelated;
import org.ofbiz.minilang.method.entityops.GetRelatedOne;
import org.ofbiz.minilang.method.entityops.MakeValue;
import org.ofbiz.minilang.method.envops.Iterate;
import org.ofbiz.minilang.method.envops.IterateMap;
import org.ofbiz.minilang.method.envops.Loop;
import org.ofbiz.minilang.method.envops.While;
import org.ofbiz.minilang.method.ifops.IfCompare;
import org.ofbiz.minilang.method.ifops.IfCompareField;
import org.ofbiz.minilang.method.ifops.IfEmpty;
import org.ofbiz.minilang.method.ifops.IfHasPermission;
import org.ofbiz.minilang.method.ifops.IfInstanceOf;
import org.ofbiz.minilang.method.ifops.IfNotEmpty;
import org.ofbiz.minilang.method.ifops.IfRegexp;
import org.ofbiz.minilang.method.ifops.IfValidateMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Mini-language utilities.
 */
public final class MiniLangUtil {

    public static final String module = MiniLangUtil.class.getName();

    public static final Set<String> SCRIPT_PREFIXES;

    static {
        Set<String> scriptPrefixes = new HashSet<String>();
        for (String scriptName : ScriptUtil.SCRIPT_NAMES) {
            scriptPrefixes.add(scriptName.concat(":"));
        }
        SCRIPT_PREFIXES = Collections.unmodifiableSet(scriptPrefixes);
    }

    public static boolean containsScript(String str) {
        if (str.length() > 0) {
            for (String scriptPrefix : SCRIPT_PREFIXES) {
                if (str.contains(scriptPrefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean autoCorrectOn() {
        return "true".equals(UtilProperties.getPropertyValue("minilang.properties", "autocorrect"));
    }

    public static void callMethod(MethodOperation operation, MethodContext methodContext, List<MethodObject<?>> parameters, Class<?> methodClass, Object methodObject, String methodName, FlexibleMapAccessor<Object> retFieldFma) throws MiniLangRuntimeException {
        Object[] args = null;
        Class<?>[] parameterTypes = null;
        if (parameters != null) {
            args = new Object[parameters.size()];
            parameterTypes = new Class<?>[parameters.size()];
            int i = 0;
            for (MethodObject<?> methodObjectDef : parameters) {
                args[i] = methodObjectDef.getObject(methodContext);
                Class<?> typeClass = null;
                try {
                    typeClass = methodObjectDef.getTypeClass(methodContext);
                } catch (ClassNotFoundException e) {
                    throw new MiniLangRuntimeException(e, operation);
                }
                parameterTypes[i] = typeClass;
                i++;
            }
        }
        try {
            Method method = methodClass.getMethod(methodName, parameterTypes);
            Object retValue = method.invoke(methodObject, args);
            if (!retFieldFma.isEmpty()) {
                retFieldFma.put(methodContext.getEnvMap(), retValue);
            }
        } catch (Exception e) {
            throw new MiniLangRuntimeException(e, operation);
        }
    }

    @SuppressWarnings("unchecked")
    public static Object convertType(Object obj, Class<?> targetClass, Locale locale, TimeZone timeZone, String format) throws Exception {
        if (obj == null || obj == GenericEntity.NULL_FIELD) {
            return null;
        }
        if (obj instanceof Node) {
            Node node = (Node) obj;
            String nodeValue = node.getTextContent();
            if (targetClass == String.class) {
                return nodeValue;
            } else {
                return convertType(nodeValue, targetClass, locale, timeZone, format);
            }
        }
        if (targetClass == PlainString.class) {
            return obj.toString();
        }
        Class<?> sourceClass = obj.getClass();
        if (sourceClass == targetClass) {
            return obj;
        }
        Converter<Object, Object> converter = (Converter<Object, Object>) Converters.getConverter(sourceClass, targetClass);
        LocalizedConverter<Object, Object> localizedConverter = null;
        try {
            localizedConverter = (LocalizedConverter) converter;
            if (locale == null) {
                locale = Locale.getDefault();
            }
            if (timeZone == null) {
                timeZone = TimeZone.getDefault();
            }
            if (format != null && format.isEmpty()) {
                format = null;
            }
            return localizedConverter.convert(obj, locale, timeZone, format);
        } catch (ClassCastException e) {}
        return converter.convert(obj);
    }

    public static void findEntityNamesUsed(List<MethodOperation> methodOperations, Set<String> allEntityNames, Set<String> simpleMethodsVisited) throws MiniLangException {
        for (MethodOperation methodOperation : methodOperations) {
            if (methodOperation instanceof FindByPrimaryKey) {
                String entName = ((FindByPrimaryKey) methodOperation).getEntityName();
                if (UtilValidate.isNotEmpty(entName))
                    allEntityNames.add(entName);
            } else if (methodOperation instanceof FindByAnd) {
                String entName = ((FindByAnd) methodOperation).getEntityName();
                if (UtilValidate.isNotEmpty(entName))
                    allEntityNames.add(entName);
            } else if (methodOperation instanceof EntityOne) {
                String entName = ((EntityOne) methodOperation).getEntityName();
                if (UtilValidate.isNotEmpty(entName))
                    allEntityNames.add(entName);
            } else if (methodOperation instanceof EntityAnd) {
                String entName = ((EntityAnd) methodOperation).getEntityName();
                if (UtilValidate.isNotEmpty(entName))
                    allEntityNames.add(entName);
            } else if (methodOperation instanceof EntityCondition) {
                String entName = ((EntityCondition) methodOperation).getEntityName();
                if (UtilValidate.isNotEmpty(entName))
                    allEntityNames.add(entName);
            } else if (methodOperation instanceof EntityCount) {
                String entName = ((EntityCount) methodOperation).getEntityName();
                if (UtilValidate.isNotEmpty(entName))
                    allEntityNames.add(entName);
            } else if (methodOperation instanceof MakeValue) {
                String entName = ((MakeValue) methodOperation).getEntityName();
                if (UtilValidate.isNotEmpty(entName))
                    allEntityNames.add(entName);
            } else if (methodOperation instanceof GetRelated) {
                String relationName = ((GetRelated) methodOperation).getRelationName();
                if (UtilValidate.isNotEmpty(relationName))
                    allEntityNames.add(relationName);
            } else if (methodOperation instanceof GetRelatedOne) {
                String relationName = ((GetRelatedOne) methodOperation).getRelationName();
                if (UtilValidate.isNotEmpty(relationName))
                    allEntityNames.add(relationName);
            } else if (methodOperation instanceof CallSimpleMethod) {
                CallSimpleMethod csm = (CallSimpleMethod) methodOperation;
                try {
                    SimpleMethod calledMethod = csm.getSimpleMethodToCall(null);
                    if (calledMethod == null) {
                        Debug.logWarning("Could not find simple-method [" + csm.getMethodName() + "] in [" + csm.getXmlResource() + "] from the SimpleMethod [" + csm.getSimpleMethod().getMethodName() + "] in [" + csm.getSimpleMethod().getFromLocation() + "]", module);
                    } else {
                        if (!simpleMethodsVisited.contains(calledMethod.getLocationAndName())) {
                            simpleMethodsVisited.add(calledMethod.getLocationAndName());
                            findEntityNamesUsed(calledMethod.getMethodOperations(), allEntityNames, simpleMethodsVisited);
                        }
                    }
                } catch (MiniLangException e) {
                    Debug.logWarning("Error getting simple-method info in the [" + csm.getSimpleMethod().getMethodName() + "] in [" + csm.getSimpleMethod().getFromLocation() + "]: " + e.toString(), module);
                }
            } else if (methodOperation instanceof Iterate) {
                findEntityNamesUsed(((Iterate) methodOperation).getSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IterateMap) {
                findEntityNamesUsed(((IterateMap) methodOperation).getSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof Loop) {
                findEntityNamesUsed(((Loop) methodOperation).getSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof MasterIf) {
                findEntityNamesUsed(((MasterIf) methodOperation).getAllSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof While) {
                findEntityNamesUsed(((While) methodOperation).getThenSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfValidateMethod) {
                findEntityNamesUsed(((IfValidateMethod) methodOperation).getAllSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfInstanceOf) {
                findEntityNamesUsed(((IfInstanceOf) methodOperation).getAllSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfCompare) {
                findEntityNamesUsed(((IfCompare) methodOperation).getAllSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfCompareField) {
                findEntityNamesUsed(((IfCompareField) methodOperation).getAllSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfRegexp) {
                findEntityNamesUsed(((IfRegexp) methodOperation).getAllSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfEmpty) {
                findEntityNamesUsed(((IfEmpty) methodOperation).getAllSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfNotEmpty) {
                findEntityNamesUsed(((IfNotEmpty) methodOperation).getAllSubOps(), allEntityNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfHasPermission) {
                findEntityNamesUsed(((IfHasPermission) methodOperation).getAllSubOps(), allEntityNames, simpleMethodsVisited);
            }
        }
    }

    public static void findServiceNamesCalled(List<MethodOperation> methodOperations, Set<String> allServiceNames, Set<String> simpleMethodsVisited) throws MiniLangException {
        for (MethodOperation methodOperation : methodOperations) {
            if (methodOperation instanceof CallService) {
                String svcName = ((CallService) methodOperation).getServiceName();
                if (UtilValidate.isNotEmpty(svcName))
                    allServiceNames.add(svcName);
            } else if (methodOperation instanceof CallServiceAsynch) {
                String svcName = ((CallServiceAsynch) methodOperation).getServiceName();
                if (UtilValidate.isNotEmpty(svcName))
                    allServiceNames.add(svcName);
            } else if (methodOperation instanceof SetServiceFields) {
                String svcName = ((SetServiceFields) methodOperation).getServiceName();
                if (UtilValidate.isNotEmpty(svcName))
                    allServiceNames.add(svcName);
            } else if (methodOperation instanceof CallSimpleMethod) {
                CallSimpleMethod csm = (CallSimpleMethod) methodOperation;
                try {
                    SimpleMethod calledMethod = csm.getSimpleMethodToCall(methodOperations.getClass().getClassLoader());
                    if (calledMethod == null) {
                        Debug.logWarning("Could not find simple-method [" + csm.getMethodName() + "] in [" + csm.getXmlResource() + "] from the SimpleMethod [" + csm.getSimpleMethod().getMethodName() + "] in [" + csm.getSimpleMethod().getFromLocation() + "]", module);
                    } else {
                        if (!simpleMethodsVisited.contains(calledMethod.getLocationAndName())) {
                            simpleMethodsVisited.add(calledMethod.getLocationAndName());
                            findServiceNamesCalled(calledMethod.getMethodOperations(), allServiceNames, simpleMethodsVisited);
                        }
                    }
                } catch (MiniLangException e) {
                    Debug.logWarning("Error getting simple-method info in the [" + csm.getSimpleMethod().getMethodName() + "] in [" + csm.getSimpleMethod().getFromLocation() + "]: " + e.toString(), module);
                }
            } else if (methodOperation instanceof Iterate) {
                findServiceNamesCalled(((Iterate) methodOperation).getSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IterateMap) {
                findServiceNamesCalled(((IterateMap) methodOperation).getSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof Loop) {
                findServiceNamesCalled(((Loop) methodOperation).getSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof MasterIf) {
                findServiceNamesCalled(((MasterIf) methodOperation).getAllSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof While) {
                findServiceNamesCalled(((While) methodOperation).getThenSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfValidateMethod) {
                findServiceNamesCalled(((IfValidateMethod) methodOperation).getAllSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfInstanceOf) {
                findServiceNamesCalled(((IfInstanceOf) methodOperation).getAllSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfCompare) {
                findServiceNamesCalled(((IfCompare) methodOperation).getAllSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfCompareField) {
                findServiceNamesCalled(((IfCompareField) methodOperation).getAllSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfRegexp) {
                findServiceNamesCalled(((IfRegexp) methodOperation).getAllSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfEmpty) {
                findServiceNamesCalled(((IfEmpty) methodOperation).getAllSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfNotEmpty) {
                findServiceNamesCalled(((IfNotEmpty) methodOperation).getAllSubOps(), allServiceNames, simpleMethodsVisited);
            } else if (methodOperation instanceof IfHasPermission) {
                findServiceNamesCalled(((IfHasPermission) methodOperation).getAllSubOps(), allServiceNames, simpleMethodsVisited);
            }
        }
    }

    public static void flagDocumentAsCorrected(Element element) {
        Document doc = element.getOwnerDocument();
        if (doc != null) {
            doc.setUserData("autoCorrected", "true", null);
        }
    }

    public static Class<?> getObjectClassForConversion(Object object) {
        if (object == null || object instanceof String) {
            return PlainString.class;
        } else {
            if (object instanceof java.util.Map<?, ?>) {
                return java.util.Map.class;
            } else if (object instanceof java.util.List<?>) {
                return java.util.List.class;
            } else if (object instanceof java.util.Set<?>) {
                return java.util.Set.class;
            } else {
                return object.getClass();
            }
        }
    }

    public static boolean isConstantAttribute(String attributeValue) {
        if (attributeValue.length() > 0) {
            return !FlexibleStringExpander.containsExpression(FlexibleStringExpander.getInstance(attributeValue));
        }
        return true;
    }

    public static boolean isConstantPlusExpressionAttribute(String attributeValue) {
        if (attributeValue.length() > 0) {
            if (attributeValue.startsWith("${") && attributeValue.endsWith("}")) {
                // A lot of existing code uses concatenated expressions, and they can be difficult
                // to convert to a single expression, so we will allow them for now.
                String expression = attributeValue.substring(2, attributeValue.length() - 1);
                if (!expression.contains("${")) {
                    return true;
                }
            }
            FlexibleStringExpander fse = FlexibleStringExpander.getInstance(attributeValue);
            return FlexibleStringExpander.containsConstant(fse);
        }
        return true;
    }

    public static boolean isDocumentAutoCorrected(Document document) {
        return "true".equals(document.getUserData("autoCorrected"));
    }

    public static void removeInvalidAttributes(Element element, String... validAttributeNames) {
        Set<String> validNames = new HashSet<String>();
        for (String name : validAttributeNames) {
            validNames.add(name);
        }
        boolean elementModified = false;
        NamedNodeMap nnm = element.getAttributes();
        for (int i = 0; i < nnm.getLength(); i++) {
            String attributeName = nnm.item(i).getNodeName();
            if (!validNames.contains(attributeName)) {
                element.removeAttribute(attributeName);
                elementModified = true;
            }
        }
        if (elementModified) {
            flagDocumentAsCorrected(element);
        }
    }

    public static void writeMiniLangDocument(URL xmlURL, Document document) {
        URL styleSheetURL = null;
        InputStream styleSheetInStream = null;
        Transformer transformer = null;
        try {
            styleSheetURL = FlexibleLocation.resolveLocation("component://minilang/config/MiniLang.xslt");
            styleSheetInStream = styleSheetURL.openStream();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer(new StreamSource(styleSheetInStream));
        } catch (Exception e) {
            Debug.logWarning(e, "Error reading minilang/config/MiniLang.xslt: ", module);
            return;
        } finally {
            if (styleSheetInStream != null) {
                try {
                    styleSheetInStream.close();
                } catch (IOException e) {
                    Debug.logWarning(e, "Error closing minilang/config/MiniLang.xslt: ", module);
                }
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(xmlURL.getFile());
            UtilXml.transformDomDocument(transformer, document, fos);
            Debug.logInfo("Saved Mini-language file " + xmlURL, module);
        } catch (Exception e) {
            Debug.logWarning(e, "Error writing mini-language file " + xmlURL + ": ", module);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Debug.logWarning(e, "Error closing " + xmlURL + ": ", module);
                }
            }
        }
    }

    public static class PlainString {}

    private MiniLangUtil() {}
}
