package org.ofbiz.core.datafile;

import java.util.*;
import java.text.*;
import java.net.*;
import java.io.*;

/**
 * <p><b>Title:</b> 
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author <a href='mailto:jonesde@ofbiz.org'>David E. Jones</a>
 *@created Nov 14, 2001
 *@version 1.0
 */

public class Record implements Serializable {
  /** Contains a map with field data by name */
  protected Map fields;
  /** Contains the name of the record definition */
  protected String recordName;
  /** Contains the definition for the record */
  protected transient ModelRecord modelRecord;
  
  protected Record parentRecord = null;
  protected List childRecords = new Vector();
  
  /** Creates new Record */
  public Record() { this.recordName = null; this.modelRecord = null; this.fields = new HashMap(); }
  /** Creates new Record */
  public Record(ModelRecord modelRecord) {
    if(modelRecord == null) throw new IllegalArgumentException("Cannont create a Record with a null modelRecord parameter");
    this.recordName = modelRecord.name;
    this.modelRecord = modelRecord;
    this.fields = new HashMap();
  }
  /** Creates new Record from existing Map */
  public Record(ModelRecord modelRecord, Map fields) {
    if(modelRecord == null) throw new IllegalArgumentException("Cannont create a Record with a null modelRecord parameter");
    this.recordName = modelRecord.name;
    this.modelRecord = modelRecord;
    this.fields = (fields==null?new HashMap():new HashMap(fields));
  }

  public String getRecordName() { return recordName; }
  public ModelRecord getModelRecord() {
    if(modelRecord == null) {
      throw new IllegalStateException("[Record.getModelRecord] could not find modelRecord for recordName " + recordName);
    }
    return modelRecord; 
  }
  
  public Object get(String name) {
    if(getModelRecord().getModelField(name) == null) {
      throw new IllegalArgumentException("[Record.get] \"" + name + "\" is not a field of " + recordName);
      //Debug.logWarning("[GenericRecord.get] \"" + name + "\" is not a field of " + recordName + ", but getting anyway...");
    }
    return fields.get(name);
  }
  
  /** Sets the named field to the passed value, even if the value is null
   * @param name The field name to set
   * @param value The value to set
   */
  public void set(String name, Object value) {
    set(name, value, true);
  }
  /** Sets the named field to the passed value. If value is null, it is only
   *  set if the setIfNull parameter is true.
   * @param name The field name to set
   * @param value The value to set
   * @param setIfNull Specifies whether or not to set the value if it is null
   */
  public synchronized void set(String name, Object value, boolean setIfNull) {
    if(getModelRecord().getModelField(name) == null) {
      throw new IllegalArgumentException("[Record.set] \"" + name + "\" is not a field of " + recordName);
      //Debug.logWarning("[GenericRecord.set] \"" + name + "\" is not a field of " + recordName + ", but setting anyway...");
    }
    if(value != null || setIfNull) {
      if (value instanceof Boolean) {
        value = ((Boolean) value).booleanValue() ? "Y" : "N";
      }
      fields.put(name, value);
    }
  }
  
  /** Sets the named field to the passed value, converting the value from a String to the corrent type using <code>Type.valueOf()</code>
   * @param name The field name to set
   * @param value The String value to convert and set
   */
  public void setString(String name, String value) throws ParseException {
    if(name == null || value == null) return;
    ModelField field = getModelRecord().getModelField(name);
    if(field == null) set(name, value); //this will get an error in the set() method...
    
    String fieldType = field.type;
    //first the custom types that need to be parsed
    if(fieldType.equals("CustomTimestamp")) {
      //this custom type will take a string a parse according to date formatting 
      // string then put the result in a java.sql.Timestamp
      //a common timestamp format for flat files is with no separators: yyyyMMddHHmmss
      SimpleDateFormat sdf = new SimpleDateFormat(field.format);
      java.util.Date tempDate = sdf.parse(value);
      java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());
      set(name, timestamp);
    }
    else if(fieldType.equals("CustomDate")) {
      //a common date only format for flat files is with no separators: yyyyMMdd or MMddyyyy
      SimpleDateFormat sdf = new SimpleDateFormat(field.format);
      java.util.Date tempDate = sdf.parse(value);
      java.sql.Date date = new java.sql.Date(tempDate.getTime());
      set(name, date);
    }
    else if(fieldType.equals("CustomTime")) {
      //a common time only format for flat files is with no separators: HHmmss
      SimpleDateFormat sdf = new SimpleDateFormat(field.format);
      java.util.Date tempDate = sdf.parse(value);
      java.sql.Date date = new java.sql.Date(tempDate.getTime());
      set(name, date);
    }
    else if(fieldType.equals("FixedPointDouble")) {
      //this custom type will parse a fixed point number according to the number 
      // of decimal places in the formatting string then place it in a Double
      NumberFormat nf = NumberFormat.getNumberInstance();
      Number tempNum = nf.parse(value);
      double number = tempNum.doubleValue();
      double decimalPlaces = Double.parseDouble(field.format);
      double divisor = Math.pow(10.0, decimalPlaces);
      number = number/divisor;
      set(name, new Double(number));
    }
    //standard types
    else if(fieldType.equals("java.lang.String") || fieldType.equals("String"))
      set(name, value);
    else if(fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp"))
      set(name, java.sql.Timestamp.valueOf(value));
    else if(fieldType.equals("java.sql.Time") || fieldType.equals("Time"))
      set(name, java.sql.Time.valueOf(value));
    else if(fieldType.equals("java.sql.Date") || fieldType.equals("Date"))
      set(name, java.sql.Date.valueOf(value));
    else if(fieldType.equals("java.lang.Integer") || fieldType.equals("Integer"))
      set(name, Integer.valueOf(value));
    else if(fieldType.equals("java.lang.Long") || fieldType.equals("Long"))
      set(name, Long.valueOf(value));
    else if(fieldType.equals("java.lang.Float") || fieldType.equals("Float"))
      set(name, Float.valueOf(value));
    else if(fieldType.equals("java.lang.Double") || fieldType.equals("Double"))
      set(name, Double.valueOf(value));
    else {
      throw new IllegalArgumentException("Java type " + fieldType + " not currently supported. Sorry.");
    }
  }
  
  public Record getParentRecord() { return parentRecord; }
  public List getChildRecords() { return childRecords; }
  public void addChildRecord(Record record) { childRecords.add(record); }
}
