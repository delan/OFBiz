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
package org.ofbiz.base.conversion;

import java.util.Locale;
import java.util.TimeZone;

import org.ofbiz.base.util.ObjectType;

/** Abstract LocalizedConverter class. This class handles converter registration
 * and it implements the <code>canConvert</code>, <code>getSourceClass</code>,
 * and <code>getTargetClass</code> methods.
 */
public abstract class AbstractLocalizedConverter<S, T> implements LocalizedConverter<S, T> {
    private final Class<S> sourceClass;
    private final Class<T> targetClass;

    protected AbstractLocalizedConverter(Class<S> sourceClass, Class<T> targetClass) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        Converters.registerConverter(this);
    }

    public T convert(Class<?> targetClass, S obj) throws ConversionException {
        return convert(obj);
    }

    public T convert(Class<?> targetClass, S obj, Locale locale, TimeZone timeZone) throws ConversionException {
        return convert(obj, locale, timeZone);
    }

    public T convert(Class<?> targetClass, S obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
        return convert(obj, locale, timeZone, formatString);
    }

    public boolean canConvert(Class<?> sourceClass, Class<?> targetClass) {
        return ObjectType.instanceOf(sourceClass, this.getSourceClass()) && ObjectType.instanceOf(targetClass, this.getTargetClass());
    }

    public final Class<S> getSourceClass() {
        return sourceClass;
    }

    public final Class<T> getTargetClass() {
        return targetClass;
    }
}
