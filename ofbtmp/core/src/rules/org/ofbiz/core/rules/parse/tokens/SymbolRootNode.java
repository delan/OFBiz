package org.ofbiz.core.rules.parse.tokens;


import java.io.*;


/**
 * <p><b>Title:</b> Symbol Root Node
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2000 Steven J. Metsker.
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
 * This class is a special case of a <code>SymbolNode</code>. A
 * <code>SymbolRootNode</code> object has no symbol of its
 * own, but has children that represent all possible symbols.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public class SymbolRootNode extends SymbolNode {
    protected SymbolNode[] children = new SymbolNode[256];

    /**
     * Create and initialize a root node.
     */
    public SymbolRootNode() {
        super(null, (char) 0);
        init();
    }

    /**
     * Add the given string as a symbol.
     *
     * @param   String   the character sequence to add
     */
    public void add(String s) {
        char c = s.charAt(0);
        SymbolNode n = ensureChildWithChar(c);

        n.addDescendantLine(s.substring(1));
        findDescendant(s).setValid(true);
    }

    /**
     * A root node has no parent and no character of its own, so
     * its ancestry is "".
     *
     * @return an empty string
     */
    public String ancestry() {
        return "";
    }

    /**
     * A root node maintains its children in an array instead of
     * a ArrayList, to be faster.
     */
    protected SymbolNode findChildWithChar(char c) {
        return children[c];
    }

    /**
     * Set all possible symbols to be valid children. This means
     * that the decision of which characters are valid one-
     * character symbols lies outside this tree. If a tokenizer
     * asks this tree to produce a symbol, this tree assumes that
     * the first available character is a valid symbol.
     */
    protected void init() {
        int len = children.length;

        for (char i = 0; i < len; i++) {
            children[i] = new SymbolNode(this, i);
            children[i].setValid(true);
        }
    }

    /**
     * Return a symbol string from a reader.
     *
     * @param   PushbackReader   a reader to read from
     *
     * @param   int   the first character of this symbol, already
     *                read from the reader
     *
     * @return a symbol string from a reader
     */
    public String nextSymbol(PushbackReader r, int first)
        throws IOException {

        SymbolNode n1 = findChildWithChar((char) first);
        SymbolNode n2 = n1.deepestRead(r);
        SymbolNode n3 = n2.unreadToValid(r);

        return n3.ancestry();
    }
}
