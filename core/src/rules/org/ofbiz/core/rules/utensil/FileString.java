package org.ofbiz.core.rules.utensil;


import java.io.*;


/**
 * <p><b>Title:</b> File String
 * <p><b>Description:</b> None
 * <p>Copyright (c) 1999 Steven J. Metsker.
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
 * <br>
 * <p>This class has a static method that returns a file's characters
 * as a single String.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class FileString {

    /**
     * Returns a string that represents the contents of a file.
     *
     * @param    fileName    the name of the file to read
     * @return   string    the contents of a file as a String
     * @exception   IOException   if the file is not found, or if there is
     *                            any problem reading the file
     */
    public static String stringFromFileNamed(String fileName)
        throws java.io.IOException {

        final int BUFLEN = 1024;
        char buf[] = new char[BUFLEN];

        FileReader in = new FileReader(fileName);
        StringWriter out = new StringWriter();

        try {
            while (true) {
                int len = in.read(buf, 0, BUFLEN);

                if (len == -1) {
                    break;
                }
                out.write(buf, 0, len);
            }
        } finally {
            out.close();
            in.close();
        }
        return out.toString();
    }
}
