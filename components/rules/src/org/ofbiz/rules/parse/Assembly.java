package org.ofbiz.rules.parse;


import java.util.*;
import org.ofbiz.rules.utensil.*;


/**
 * <p><b>Title:</b> Assembly
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
 * <p>An assembly maintains a stream of language elements along
 * with stack and target objects.
 *
 * Parsers use assemblers to record progress at
 * recognizing language elements from assembly's string.
 *
 * @author Steven J. Metsker
 * @version 1.0
 *
 */
public abstract class Assembly implements Enumeration, PubliclyCloneable {

    /**
     * a place to keep track of consumption progress
     */
    protected Stack stack = new Stack();

    /** Another place to record progress; this is just an object.
     * If a parser were recognizing an HTML page, for
     * example, it might create a Page object early, and store it
     * as an assembly's "target". As its recognition of the HTML
     * progresses, it could use the stack to build intermediate
     * results, like a heading, and then apply them to the target
     * object.
     */
    protected PubliclyCloneable target;

    /**
     * which element is next
     */
    protected int index = 0;

    /**
     * Return a copy of this object.
     *
     * @return a copy of this object
     */
    public Object clone() {
        try {
            Assembly a = (Assembly) super.clone();

            a.stack = (Stack) stack.clone();
            if (target != null) {
                a.target = (PubliclyCloneable) target.clone();
            }
            return a;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * Returns the elements of the assembly that have been
     * consumed, separated by the specified delimiter.
     *
     * @param   String   the mark to show between consumed
     *                   elements
     *
     * @return   the elements of the assembly that have been
     *           consumed
     */
    public abstract String consumed(String delimiter);

    /**
     * Returns the default string to show between elements.
     *
     * @return   the default string to show between elements
     */
    public abstract String defaultDelimiter();

    /**
     * Returns the number of elements that have been consumed.
     *
     * @return   the number of elements that have been consumed
     */
    public int elementsConsumed() {
        return index;
    }

    /**
     * Returns the number of elements that have not been consumed.
     *
     * @return   the number of elements that have not been
     *           consumed
     */
    public int elementsRemaining() {
        return length() - elementsConsumed();
    }

    /**
     * Removes this assembly's stack.
     *
     * @return   this assembly's stack
     */
    public Stack getStack() {
        return stack;
    }

    /**
     * Returns the object identified as this assembly's "target".
     * Clients can set and retrieve a target, which can be a
     * convenient supplement as a place to work, in addition to
     * the assembly's stack. For example, a parser for an
     * HTML file might use a web page object as its "target". As
     * the parser recognizes markup commands like <head>, it
     * could apply its findings to the target.
     *
     * @return   the target of this assembly
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Returns true if this assembly has unconsumed elements.
     *
     * @return   true, if this assembly has unconsumed elements
     */
    public boolean hasMoreElements() {
        return elementsConsumed() < length();
    }

    /**
     * Returns the number of elements in this assembly.
     *
     * @return   the number of elements in this assembly
     */
    public abstract int length();

    /**
     * Shows the next object in the assembly, without removing it
     *
     * @return   the next object
     *
     */
    public abstract Object peek();

    /**
     * Removes the object at the top of this assembly's stack and
     * returns it.
     *
     * @return   the object at the top of this assembly's stack
     *
     * @exception   EmptyStackException   if this stack is empty
     */
    public Object pop() {
        return stack.pop();
    }

    /**
     * Pushes an object onto the top of this assembly's stack.
     *
     * @param   object   the object to be pushed
     */
    public void push(Object o) {
        stack.push(o);
    }

    /**
     * Returns the elements of the assembly that remain to be
     * consumed, separated by the specified delimiter.
     *
     * @param   String   the mark to show between unconsumed
     *                   elements
     *
     * @return   the elements of the assembly that remain to be
     *           consumed
     */
    public abstract String remainder(String delimiter);

    /**
     * Sets the target for this assembly. Targets must implement
     * <code>clone()</code> as a public method.
     *
     * @param   target   a publicly cloneable object
     */
    public void setTarget(PubliclyCloneable target) {
        this.target = target;
    }

    /**
     * Returns true if this assembly's stack is empty.
     *
     * @return   true, if this assembly's stack is empty
     */
    public boolean stackIsEmpty() {
        return stack.isEmpty();
    }

    /**
     * Returns a textual description of this assembly.
     *
     * @return   a textual description of this assembly
     */
    public String toString() {
        String delimiter = defaultDelimiter();

        return stack +
            consumed(delimiter) + "^" + remainder(delimiter);
    }

    /**
     * Put back n objects
     *
     */
    public void unget(int n) {
        index -= n;
        if (index < 0) {
            index = 0;
        }
    }
}
