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

public class ModelField {
  /** The name of the Field */
  public String name = "";
  /** The position of the field in the record - byte number for fixed-length, or field number for delimited */
  public int position = -1;
  /** The length of the Field in bytes, if applicable (mostly for fixed-length) */
  public int length = -1;
  /** The type of the Field */
  public String type = "";
  /** The format of the Field */
  public String format = "";
  /** The valid-exp of the Field */
  public String validExp = "";
  /** Free form description of the Field */
  public String description = "";
  /** boolean which specifies whether or not the Field is a Primary Key */
  public boolean isPk = false;
}
