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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.ofbiz.base.util.StringUtil;

import javolution.util.FastList;
import javolution.util.FastSet;

/** Number Converter classes. */
public class NumberConverters implements ConverterLoader {

    protected static Number fromString(String str, Locale locale) throws ConversionException {
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        try {
            return nf.parse(str);
        } catch (ParseException e) {
            throw new ConversionException(e);
        }
    }

    public static abstract class AbstractToNumberConverter<S, T> extends AbstractLocalizedConverter<S, T> {
        protected AbstractToNumberConverter(Class<S> sourceClass, Class<T> targetClass) {
            super(sourceClass, targetClass);
        }

        public T convert(S obj, Locale locale, TimeZone timeZone, String formatString) throws ConversionException {
            return convert(obj, locale, null);
        }
    }

    public static class BigDecimalToDouble extends AbstractConverter<BigDecimal, Double> {
        public BigDecimalToDouble() {
            super(BigDecimal.class, Double.class);
        }

        public Double convert(BigDecimal obj) throws ConversionException {
            return obj.doubleValue();
        }
    }

    public static class BigDecimalToFloat extends AbstractConverter<BigDecimal, Float> {
        public BigDecimalToFloat() {
            super(BigDecimal.class, Float.class);
        }

        public Float convert(BigDecimal obj) throws ConversionException {
            return obj.floatValue();
        }
    }

    public static class BigDecimalToInteger extends AbstractConverter<BigDecimal, Integer> {
        public BigDecimalToInteger() {
            super(BigDecimal.class, Integer.class);
        }

        public Integer convert(BigDecimal obj) throws ConversionException {
            return obj.intValue();
        }
    }

    public static class BigDecimalToList extends GenericSingletonToList<BigDecimal> {
        public BigDecimalToList() {
            super(BigDecimal.class);
        }
    }

    public static class BigDecimalToLong extends AbstractConverter<BigDecimal, Long> {
        public BigDecimalToLong() {
            super(BigDecimal.class, Long.class);
        }

        public Long convert(BigDecimal obj) throws ConversionException {
            return obj.longValue();
        }
    }

    public static class BigDecimalToSet extends GenericSingletonToSet<BigDecimal> {
        public BigDecimalToSet() {
            super(BigDecimal.class);
        }
    }

    public static class BigDecimalToString extends AbstractToNumberConverter<BigDecimal, String> {
        public BigDecimalToString() {
            super(BigDecimal.class, String.class);
        }

        public String convert(BigDecimal obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(BigDecimal obj, Locale locale, TimeZone timeZone) throws ConversionException {
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            return nf.format(obj.doubleValue());
        }
    }

    public static class DoubleToBigDecimal extends AbstractConverter<Double, BigDecimal> {
        public DoubleToBigDecimal() {
            super(Double.class, BigDecimal.class);
        }

        public BigDecimal convert(Double obj) throws ConversionException {
            return BigDecimal.valueOf(obj.doubleValue());
        }
    }

    public static class BigIntegerToDouble extends AbstractConverter<BigInteger, Double> {
        public BigIntegerToDouble() {
            super(BigInteger.class, Double.class);
        }

        public Double convert(BigInteger obj) throws ConversionException {
            return obj.doubleValue();
        }
    }

    public static class BigIntegerToFloat extends AbstractConverter<BigInteger, Float> {
        public BigIntegerToFloat() {
            super(BigInteger.class, Float.class);
        }

        public Float convert(BigInteger obj) throws ConversionException {
            return obj.floatValue();
        }
    }

    public static class BigIntegerToInteger extends AbstractConverter<BigInteger, Integer> {
        public BigIntegerToInteger() {
            super(BigInteger.class, Integer.class);
        }

        public Integer convert(BigInteger obj) throws ConversionException {
            return obj.intValue();
        }
    }

    public static class BigIntegerToList extends GenericSingletonToList<BigInteger> {
        public BigIntegerToList() {
            super(BigInteger.class);
        }
    }

    public static class BigIntegerToLong extends AbstractConverter<BigInteger, Long> {
        public BigIntegerToLong() {
            super(BigInteger.class, Long.class);
        }

        public Long convert(BigInteger obj) throws ConversionException {
            return obj.longValue();
        }
    }

    public static class BigIntegerToSet extends GenericSingletonToSet<BigInteger> {
        public BigIntegerToSet() {
            super(BigInteger.class);
        }
    }

    public static class BigIntegerToString extends AbstractToNumberConverter<BigInteger, String> {
        public BigIntegerToString() {
            super(BigInteger.class, String.class);
        }

        public String convert(BigInteger obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(BigInteger obj, Locale locale, TimeZone timeZone) throws ConversionException {
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            return nf.format(obj.doubleValue());
        }
    }

    public static class ByteToDouble extends AbstractConverter<Byte, Double> {
        public ByteToDouble() {
            super(Byte.class, Double.class);
        }

        public Double convert(Byte obj) throws ConversionException {
            return obj.doubleValue();
        }
    }

    public static class ByteToFloat extends AbstractConverter<Byte, Float> {
        public ByteToFloat() {
            super(Byte.class, Float.class);
        }

        public Float convert(Byte obj) throws ConversionException {
            return obj.floatValue();
        }
    }

    public static class ByteToInteger extends AbstractConverter<Byte, Integer> {
        public ByteToInteger() {
            super(Byte.class, Integer.class);
        }

        public Integer convert(Byte obj) throws ConversionException {
            return obj.intValue();
        }
    }

    public static class ByteToList extends GenericSingletonToList<Byte> {
        public ByteToList() {
            super(Byte.class);
        }
    }

    public static class ByteToLong extends AbstractConverter<Byte, Long> {
        public ByteToLong() {
            super(Byte.class, Long.class);
        }

        public Long convert(Byte obj) throws ConversionException {
            return obj.longValue();
        }
    }

    public static class ByteToSet extends GenericSingletonToSet<Byte> {
        public ByteToSet() {
            super(Byte.class);
        }
    }

    public static class ByteToString extends AbstractToNumberConverter<Byte, String> {
        public ByteToString() {
            super(Byte.class, String.class);
        }

        public String convert(Byte obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(Byte obj, Locale locale, TimeZone timeZone) throws ConversionException {
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            return nf.format(obj.floatValue());
        }
    }

    public static class StringToBigInteger extends AbstractToNumberConverter<String, BigInteger> {
        public StringToBigInteger() {
            super(String.class, BigInteger.class);
        }

        public BigInteger convert(String obj) throws ConversionException {
            return new BigInteger(obj);
        }

        public BigInteger convert(String obj, Locale locale, TimeZone timeZone) throws ConversionException {
            String trimStr = StringUtil.removeSpaces(obj);
            if (trimStr.length() == 0) {
                return null;
            }
            return BigInteger.valueOf(fromString(trimStr, locale).longValue());
        }
    }

    public static class DoubleToFloat extends AbstractConverter<Double, Float> {
        public DoubleToFloat() {
            super(Double.class, Float.class);
        }

        public Float convert(Double obj) throws ConversionException {
            return obj.floatValue();
        }
    }

    public static class DoubleToInteger extends AbstractConverter<Double, Integer> {
        public DoubleToInteger() {
            super(Double.class, Integer.class);
        }

        public Integer convert(Double obj) throws ConversionException {
            return obj.intValue();
        }
    }

    public static class DoubleToList extends GenericSingletonToList<Double> {
        public DoubleToList() {
            super(Double.class);
        }
    }

    public static class DoubleToLong extends AbstractConverter<Double, Long> {
        public DoubleToLong() {
            super(Double.class, Long.class);
        }

        public Long convert(Double obj) throws ConversionException {
            return obj.longValue();
        }
    }

    public static class DoubleToSet extends GenericSingletonToSet<Double> {
        public DoubleToSet() {
            super(Double.class);
        }
    }

    public static class DoubleToString extends AbstractToNumberConverter<Double, String> {
        public DoubleToString() {
            super(Double.class, String.class);
        }

        public String convert(Double obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(Double obj, Locale locale, TimeZone timeZone) throws ConversionException {
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            return nf.format(obj.doubleValue());
        }
    }

    public static class FloatToBigDecimal extends AbstractConverter<Float, BigDecimal> {
        public FloatToBigDecimal() {
            super(Float.class, BigDecimal.class);
        }

        public BigDecimal convert(Float obj) throws ConversionException {
            return BigDecimal.valueOf(obj.doubleValue());
        }
    }

    public static class FloatToDouble extends AbstractConverter<Float, Double> {
        public FloatToDouble() {
            super(Float.class, Double.class);
        }

        public Double convert(Float obj) throws ConversionException {
            return obj.doubleValue();
        }
    }

    public static class FloatToInteger extends AbstractConverter<Float, Integer> {
        public FloatToInteger() {
            super(Float.class, Integer.class);
        }

        public Integer convert(Float obj) throws ConversionException {
            return obj.intValue();
        }
    }

    public static class FloatToList extends GenericSingletonToList<Float> {
        public FloatToList() {
            super(Float.class);
        }
    }

    public static class FloatToLong extends AbstractConverter<Float, Long> {
        public FloatToLong() {
            super(Float.class, Long.class);
        }

        public Long convert(Float obj) throws ConversionException {
            return obj.longValue();
        }
    }

    public static class FloatToSet extends GenericSingletonToSet<Float> {
        public FloatToSet() {
            super(Float.class);
        }
    }

    public static class FloatToString extends AbstractToNumberConverter<Float, String> {
        public FloatToString() {
            super(Float.class, String.class);
        }

        public String convert(Float obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(Float obj, Locale locale, TimeZone timeZone) throws ConversionException {
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            return nf.format(obj.floatValue());
        }
    }

    public static class IntegerToBigDecimal extends AbstractConverter<Integer, BigDecimal> {
        public IntegerToBigDecimal() {
            super(Integer.class, BigDecimal.class);
        }

        public BigDecimal convert(Integer obj) throws ConversionException {
            return BigDecimal.valueOf(obj.intValue());
        }
    }

    public static class IntegerToByte extends AbstractConverter<Integer, Byte> {
        public IntegerToByte() {
            super(Integer.class, Byte.class);
        }

        public Byte convert(Integer obj) throws ConversionException {
            return obj.byteValue();
        }
    }

    public static class IntegerToDouble extends AbstractConverter<Integer, Double> {
        public IntegerToDouble() {
            super(Integer.class, Double.class);
        }

        public Double convert(Integer obj) throws ConversionException {
            return obj.doubleValue();
        }
    }

    public static class IntegerToFloat extends AbstractConverter<Integer, Float> {
        public IntegerToFloat() {
            super(Integer.class, Float.class);
        }

        public Float convert(Integer obj) throws ConversionException {
            return obj.floatValue();
        }
    }

    public static class IntegerToList extends GenericSingletonToList<Integer> {
        public IntegerToList() {
            super(Integer.class);
        }
    }

    public static class IntegerToLong extends AbstractConverter<Integer, Long> {
        public IntegerToLong() {
            super(Integer.class, Long.class);
        }

        public Long convert(Integer obj) throws ConversionException {
            return obj.longValue();
        }
    }

    public static class IntegerToSet extends GenericSingletonToSet<Integer> {
        public IntegerToSet() {
            super(Integer.class);
        }
    }

    public static class IntegerToShort extends AbstractConverter<Integer, Short> {
        public IntegerToShort() {
            super(Integer.class, Short.class);
        }

        public Short convert(Integer obj) throws ConversionException {
            return obj.shortValue();
        }
    }

    public static class IntegerToString extends AbstractToNumberConverter<Integer, String> {
        public IntegerToString() {
            super(Integer.class, String.class);
        }

        public String convert(Integer obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(Integer obj, Locale locale, TimeZone timeZone) throws ConversionException {
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            return nf.format(obj.intValue());
        }
    }

    public static class LongToBigDecimal extends AbstractConverter<Long, BigDecimal> {
        public LongToBigDecimal() {
            super(Long.class, BigDecimal.class);
        }

        public BigDecimal convert(Long obj) throws ConversionException {
            return BigDecimal.valueOf(obj.longValue());
        }
    }

    public static class LongToByte extends AbstractConverter<Long, Byte> {
        public LongToByte() {
            super(Long.class, Byte.class);
        }

        public Byte convert(Long obj) throws ConversionException {
            return obj.byteValue();
        }
    }

    public static class LongToDouble extends AbstractConverter<Long, Double> {
        public LongToDouble() {
            super(Long.class, Double.class);
        }

        public Double convert(Long obj) throws ConversionException {
            return obj.doubleValue();
        }
    }

    public static class LongToFloat extends AbstractConverter<Long, Float> {
        public LongToFloat() {
            super(Long.class, Float.class);
        }

        public Float convert(Long obj) throws ConversionException {
            return obj.floatValue();
        }
    }

    public static class LongToInteger extends AbstractConverter<Long, Integer> {
        public LongToInteger() {
            super(Long.class, Integer.class);
        }

        public Integer convert(Long obj) throws ConversionException {
            return obj.intValue();
        }
    }

    public static class LongToList extends GenericSingletonToList<Long> {
        public LongToList() {
            super(Long.class);
        }
    }

    public static class LongToSet extends GenericSingletonToSet<Long> {
        public LongToSet() {
            super(Long.class);
        }
    }

    public static class LongToShort extends AbstractConverter<Long, Short> {
        public LongToShort() {
            super(Long.class, Short.class);
        }

        public Short convert(Long obj) throws ConversionException {
            return obj.shortValue();
        }
    }

    public static class LongToString extends AbstractToNumberConverter<Long, String> {
        public LongToString() {
            super(Long.class, String.class);
        }

        public String convert(Long obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(Long obj, Locale locale, TimeZone timeZone) throws ConversionException {
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            return nf.format(obj.longValue());
        }
    }

    public static class ShortToDouble extends AbstractConverter<Short, Double> {
        public ShortToDouble() {
            super(Short.class, Double.class);
        }

        public Double convert(Short obj) throws ConversionException {
            return obj.doubleValue();
        }
    }

    public static class ShortToFloat extends AbstractConverter<Short, Float> {
        public ShortToFloat() {
            super(Short.class, Float.class);
        }

        public Float convert(Short obj) throws ConversionException {
            return obj.floatValue();
        }
    }

    public static class ShortToInteger extends AbstractConverter<Short, Integer> {
        public ShortToInteger() {
            super(Short.class, Integer.class);
        }

        public Integer convert(Short obj) throws ConversionException {
            return obj.intValue();
        }
    }

    public static class ShortToList extends GenericSingletonToList<Short> {
        public ShortToList() {
            super(Short.class);
        }
    }

    public static class ShortToLong extends AbstractConverter<Short, Long> {
        public ShortToLong() {
            super(Short.class, Long.class);
        }

        public Long convert(Short obj) throws ConversionException {
            return obj.longValue();
        }
    }

    public static class ShortToSet extends GenericSingletonToSet<Short> {
        public ShortToSet() {
            super(Short.class);
        }
    }

    public static class ShortToString extends AbstractToNumberConverter<Short, String> {
        public ShortToString() {
            super(Short.class, String.class);
        }

        public String convert(Short obj) throws ConversionException {
            return obj.toString();
        }

        public String convert(Short obj, Locale locale, TimeZone timeZone) throws ConversionException {
            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            return nf.format(obj.floatValue());
        }
    }

    public static class StringToBigDecimal extends AbstractToNumberConverter<String, BigDecimal> {
        public StringToBigDecimal() {
            super(String.class, BigDecimal.class);
        }

        public BigDecimal convert(String obj) throws ConversionException {
            return BigDecimal.valueOf(Double.valueOf(obj));
        }

        public BigDecimal convert(String obj, Locale locale, TimeZone timeZone) throws ConversionException {
            String trimStr = StringUtil.removeSpaces(obj);
            if (trimStr.length() == 0) {
                return null;
            }
            return BigDecimal.valueOf(fromString(trimStr, locale).doubleValue());
        }
    }

    public static class StringToByte extends AbstractConverter<String, Byte> {
        public StringToByte() {
            super(String.class, Byte.class);
        }

        public Byte convert(String obj) throws ConversionException {
            return Byte.valueOf(obj);
        }
    }

    public static class StringToDouble extends AbstractToNumberConverter<String, Double> {
        public StringToDouble() {
            super(String.class, Double.class);
        }

        public Double convert(String obj) throws ConversionException {
            return Double.valueOf(obj);
        }

        public Double convert(String obj, Locale locale, TimeZone timeZone) throws ConversionException {
            String trimStr = StringUtil.removeSpaces(obj);
            if (trimStr.length() == 0) {
                return null;
            }
            return fromString(trimStr, locale).doubleValue();
        }
    }

    public static class StringToFloat extends AbstractToNumberConverter<String, Float> {
        public StringToFloat() {
            super(String.class, Float.class);
        }

        public Float convert(String obj) throws ConversionException {
            return Float.valueOf(obj);
        }

        public Float convert(String obj, Locale locale, TimeZone timeZone) throws ConversionException {
            String trimStr = StringUtil.removeSpaces(obj);
            if (trimStr.length() == 0) {
                return null;
            }
            return fromString(trimStr, locale).floatValue();
        }
    }

    public static class StringToInteger extends AbstractToNumberConverter<String, Integer> {
        public StringToInteger() {
            super(String.class, Integer.class);
        }

        public Integer convert(String obj) throws ConversionException {
            return Integer.valueOf(obj);
        }

        public Integer convert(String obj, Locale locale, TimeZone timeZone) throws ConversionException {
            String trimStr = StringUtil.removeSpaces(obj);
            if (trimStr.length() == 0) {
                return null;
            }
            return fromString(trimStr, locale).intValue();
        }
    }

    public static class StringToLong extends AbstractToNumberConverter<String, Long> {
        public StringToLong() {
            super(String.class, Long.class);
        }

        public Long convert(String obj) throws ConversionException {
            return Long.valueOf(obj);
        }

        public Long convert(String obj, Locale locale, TimeZone timeZone) throws ConversionException {
            String trimStr = StringUtil.removeSpaces(obj);
            if (trimStr.length() == 0) {
                return null;
            }
            return fromString(trimStr, locale).longValue();
        }
    }

    public static class StringToShort extends AbstractConverter<String, Short> {
        public StringToShort() {
            super(String.class, Short.class);
        }

        public Short convert(String obj) throws ConversionException {
            return Short.valueOf(obj);
        }
    }

    public void loadConverters() {
        Converters.loadContainedConverters(NumberConverters.class);
    }
}
