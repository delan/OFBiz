package net.matuschek.util;

/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/

import java.io.Writer;
                                         
/**
 * This class implements a simple Writer that ignores everything
 * it is like a /dev/null for Java
 * 
 * @author Daniel Matuschek 
 * @version $Id $
 */
public class NullWriter extends Writer {

  public NullWriter() {}

  public void close() {}

  public void flush() {}

  public void write(char[] cbuf, int off, int len) {}
}
