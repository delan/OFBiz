package org.ofbiz.core.datafile;

import java.util.*;

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

public class ModelRecord {
  public static final String LIMIT_ONE = "one";
  public static final String LIMIT_MANY = "many";
  
  /** The name of the Record */
  public String name = "";
  /** The type-code of the Record */
  public String typeCode = "";
  /** The position of the type-code of the Record */
  public int tcPosition = -1;
  /** The length of the type-code of the Record - optional */
  public int tcLength = -1;
  /** A free form description of the Record */
  public String description = "";
  /** The name of the parent record for this record, if any */
  public String parentName = "";
  /** The number limit of records to go under the parent, may be one or many */
  public String limit = "";

  public ModelRecord parentRecord = null;
  public List childRecords = new Vector();
  
  /** List of the fields that compose this record */
  public List fields = new Vector();

  ModelField getModelField(String fieldName) {
    for(int i=0; i<fields.size(); i++) {
      ModelField curField = (ModelField)fields.get(i);
      if(curField.name.equals(fieldName)) {
        return curField;
      }
    }
    return null;
  }
}
