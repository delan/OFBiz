/*
 * $Id: EntityComparisonOperator.java,v 1.1 2003/11/05 12:08:00 jonesde Exp $
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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

package org.ofbiz.entity.condition;

import java.util.List;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Encapsulates operations between entities and entity fields. This is a immutable class.
 *
 * @author     <a href="mailto:adam@doogie.org">Adam Heath</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 */
public class EntityComparisonOperator extends EntityOperator {

    protected static PatternMatcher matcher = new Perl5Matcher();
    protected static Perl5Util perl5Util = new Perl5Util();
    protected static PatternCompiler compiler = new Perl5Compiler();

    public static Pattern makeOroPattern(String sqlLike) {
        sqlLike = perl5Util.substitute("s/([$^.+*?])/\\\\$1/g", sqlLike);
        sqlLike = perl5Util.substitute("s/%/.*/g", sqlLike);
        sqlLike = perl5Util.substitute("s/_/./g", sqlLike);
        try {
            return compiler.compile(sqlLike);
        } catch (MalformedPatternException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean compare(Object lhs, Object rhs) {
        throw new UnsupportedOperationException(codeString);
    }

    public EntityComparisonOperator(int id, String code) {
        super(id, code);
    }

    public static final boolean compareEqual(Object lhs, Object rhs) {
        if (lhs == null) {
            if (rhs != null) {
                return false;
            }
        } else if (!lhs.equals(rhs)) {
            return false;
        }
        return true;
    }

    public static final boolean compareNotEqual(Object lhs, Object rhs) {
        if (lhs == null) {
            if (rhs == null) {
                return false;
            }
        } else if (lhs.equals(rhs)) {
            return false;
        }
        return true;
    }

    public static final boolean compareGreaterThan(Object lhs, Object rhs) {
        if (lhs == null) {
            if (rhs != null) {
                return false;
            }
        } else if (((Comparable) lhs).compareTo(rhs) <= 0) {
            return false;
        }
        return true;
    }

    public static final boolean compareGreaterThanEqualTo(Object lhs, Object rhs) {
        if (lhs == null) {
            if (rhs != null) {
                return false;
            }
        } else if (((Comparable) lhs).compareTo(rhs) < 0) {
            return false;
        }
        return true;
    }

    public static final boolean compareLessThan(Object lhs, Object rhs) {
        if (lhs == null) {
            if (rhs != null) {
                return false;
            }
        } else if (((Comparable) lhs).compareTo(rhs) >= 0) {
            return false;
        }
        return true;
    }

    public static final boolean compareLessThanEqualTo(Object lhs, Object rhs) {
        if (lhs == null) {
            if (rhs != null) {
                return false;
            }
        } else if (((Comparable) lhs).compareTo(rhs) > 0) {
            return false;
        }
        return true;
    }

    public static final boolean compareIn(Object lhs, Object rhs) {
        if (lhs == null) {
            if (rhs != null) {
                return false;
            }
            return true;
        } else if (((List) rhs).contains(lhs)) {
            return true;
        }
        return false;
    }

    public static final boolean compareLike(Object lhs, Object rhs) {
        if (lhs == null) {
            if (rhs != null) {
                return false;
            }
        } else if (lhs instanceof String && rhs instanceof String) {
            //see if the lhs value is like the rhs value, rhs will have the pattern characters in it...
            return matcher.matches((String) lhs, makeOroPattern((String) rhs));
        }
        return true;
    }
}
