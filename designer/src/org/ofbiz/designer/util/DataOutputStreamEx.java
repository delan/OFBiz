package org.ofbiz.designer.util;

import java.io.DataOutputStream;

/**
	This class is an extension of DataOutputStream which takes a pre-allocated buffer in the constructor
 */

public class DataOutputStreamEx extends DataOutputStream {
	public DataOutputStreamEx(byte[] buffer, int offset){
		super(new ByteArrayOutputStreamEx(buffer, offset));
	}
}

class ByteArrayOutputStreamEx extends java.io.ByteArrayOutputStream {
	ByteArrayOutputStreamEx(byte[] buffer, int offset){
		buf = buffer;
		count = offset;
	}
}
