package org.ofbiz.core.rules.parse;


import java.util.*;


/**
 * <p><b>Title:</b> Assembler
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
 * <p>Parsers that have an Assembler ask it to work on an
 * assembly after a successful match.
 * <p>
 * By default, terminals push their matches on a assembly's
 * stack after a successful match.
 * <p>
 * Parsers recognize text, and assemblers provide any
 * sort of work that should occur after this recognition.
 * This work usually has to do with the state of the assembly,
 * which is why assemblies have a stack and a target.
 * Essentially, parsers trade advancement on a assembly
 * for work on the assembly's stack or target.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */
public abstract class Assembler {

    /**
     * Returns a vector of the elements on an assembly's stack
     * that appear before a specified fence.
     * <p>
     * Sometimes a parser will recognize a list from within
     * a pair of parentheses or brackets. The parser can mark
     * the beginning of the list with a fence, and then retrieve
     * all the items that come after the fence with this method.
     *
     * @param   assembly   a assembly whose stack should contain
     * some number of items above a fence marker
     *
     * @param   object   the fence, a marker of where to stop
     *                   popping the stack
     *
     * @return   List   the elements above the specified fence
     *
     */
    public static List elementsAbove(Assembly a, Object fence) {
        List items = new ArrayList();

        while (!a.stackIsEmpty()) {
            Object top = a.pop();

            if (top.equals(fence)) {
                break;
            }
            items.add(top);
        }
        return items;
    }

    /**
     * This is the one method all subclasses must implement. It
     * specifies what to do when a parser successfully
     * matches against a assembly.
     *
     * @param   Assembly   the assembly to work on
     */
    public abstract void workOn(Assembly a);
}
