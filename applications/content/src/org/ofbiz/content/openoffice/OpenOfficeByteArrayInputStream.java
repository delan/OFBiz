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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.sun.star.io.XSeekable;
import com.sun.star.io.XInputStream;
import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.NotConnectedException;

/**
 * OpenOfficeByteArrayInputStream Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Rev: 5462 $
 * @since 3.2
 * 
 *  
 */

public class OpenOfficeByteArrayInputStream extends ByteArrayInputStream implements XInputStream, XSeekable {
	
    public static final String module = OpenOfficeByteArrayInputStream.class.getName();
    
	public OpenOfficeByteArrayInputStream(byte [] bytes) {
		super(bytes);
	}
	
	
	public long getPosition() throws com.sun.star.io.IOException {
		return this.pos;
	}
	
	public long getLength() throws com.sun.star.io.IOException {
		return this.count;
	}
	
	public void seek(long pos1) throws com.sun.star.io.IOException, IllegalArgumentException {
		this.pos = (int)pos1;
	}

	public void skipBytes(int pos1) throws BufferSizeExceededException, 
                                             NotConnectedException, com.sun.star.io.IOException {
        skip(pos1);
	}

	public void closeInput() throws NotConnectedException, com.sun.star.io.IOException {
		
		try {
			close();
		} catch( IOException e) {
			String errMsg = e.getMessage();
			throw new com.sun.star.io.IOException( errMsg, this );
		}
		
    }
	
	public int readBytes(byte [][]buf, int pos2) throws BufferSizeExceededException, NotConnectedException, com.sun.star.io.IOException {
		
		int bytesRead = 0;
		byte [] buf2 = new byte[pos2];
		try {
			bytesRead = super.read(buf2);
		} catch( IOException e) {
			String errMsg = e.getMessage();
			throw new com.sun.star.io.IOException( errMsg, this );
		}
		
		if (bytesRead > 0) {
			if (bytesRead < pos2) {
				byte [] buf3 = new byte[bytesRead];
				System.arraycopy(buf2, 0, buf3, 0, bytesRead);
				buf[0] = buf3;
			} else {
				buf[0] = buf2;
			}
		} else {
			buf[0] = new byte[0];
		}
		return bytesRead;
	}

	public int readSomeBytes(byte [][]buf, int pos2) throws BufferSizeExceededException, NotConnectedException, com.sun.star.io.IOException {
		return readBytes(buf, pos2);
	}
}
