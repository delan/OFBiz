package org.ofbiz.core.rules.parse;

import java.util.*;

/**
 * <p><b>Title:</b> Track
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
 * A Track is a sequence that throws a <code>
 * TrackException</code> if the sequence begins but
 * does not complete.
 *
 * @author Steven J. Metsker
 * @version 1.0
 */

public class Track extends Sequence {
  /**
   * Constructs a nameless Track.
   */
  public Track() {
  }
  /**
   * Constructs a Track with the given name.
   *
   * @param    name    a name to be known by
   */
  public Track(String name) {
    super(name);
  }
  /**
   * Given a collection of assemblies, this method matches
   * this track against all of them, and returns a new
   * collection of the assemblies that result from the
   * matches.
   *
   * If the match begins but does not complete, this method
   * throws a <code>TrackException</code>.
   *
   * @return   a List of assemblies that result from matching
   *           against a beginning set of assemblies
   *
   * @param in a vector of assemblies to match against
   *
   */
  public List match(List in) {
    boolean inTrack = false;
    List last = in;
    List out = in;
    Enumeration e = Collections.enumeration(subparsers);
    while (e.hasMoreElements()) {
      Parser p = (Parser) e.nextElement();
      out = p.matchAndAssemble(last);
      if (out.isEmpty()) {
        if (inTrack) {
          throwTrackException(last, p);
        }
        return out;
      }
      inTrack = true;
      last = out;
    }
    return out;
  }
  /**
   * Throw an exception showing how far the match had
   * progressed, what it found next, and what it was
   * expecting.
   */
  protected void throwTrackException(List previousState, Parser p) {
    
    Assembly best = best(previousState);
    String after = best.consumed(" ");
    if (after.equals("")) {
      after = "-nothing-";
    }
    
    String expected = p.toString();
    
    Object next = best.peek();
    String found =
    (next == null) ? "-nothing-" : next.toString();
    
    throw new TrackException(after, expected, found);
  }
}
