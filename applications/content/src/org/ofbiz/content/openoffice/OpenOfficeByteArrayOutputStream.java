/*
 * $Id: PdfSurveyServices.java 5462 2005-08-05 18:35:48Z byersa $
 *
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.content.openoffice;

import java.io.ByteArrayOutputStream;

import com.sun.star.io.XSeekable;
import com.sun.star.io.XOutputStream;
import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.NotConnectedException;


/**
 * OpenOfficeByteArrayOutputStream Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Rev: 5462 $
 * @since 3.2
 * 
 *  
 */

public class OpenOfficeByteArrayOutputStream extends ByteArrayOutputStream implements XOutputStream {

    public static final String module = OpenOfficeByteArrayOutputStream.class.getName();
    
	public OpenOfficeByteArrayOutputStream() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OpenOfficeByteArrayOutputStream(int arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}


	  public void writeBytes(byte[] buf) throws BufferSizeExceededException, NotConnectedException, com.sun.star.io.IOException
	  {
		  try {
			  write(buf);
		  } catch ( java.io.IOException e ) {
			  throw(new com.sun.star.io.IOException(e.getMessage()));
		  }
	  }

	  public void closeOutput() throws BufferSizeExceededException, NotConnectedException, com.sun.star.io.IOException
	  {
		  try {
			  super.flush();
			  close();
		  } catch ( java.io.IOException e ) {
			  throw(new com.sun.star.io.IOException(e.getMessage()));
		  }
	  }
	  
	  public void flush()
	  {
		  try {
			  super.flush();
		  } catch ( java.io.IOException e ) {
		  }
	  }

}
