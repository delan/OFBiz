/*
 * $Id$
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.pos.event;

import org.ofbiz.pos.screen.PosScreen;
import org.ofbiz.pos.component.Input;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.2
 */
public class CharacterEvents {

    public static boolean capsLockSet = false;

    public static void triggerShift(PosScreen pos) {
        pos.getInput().setFunction("SHIFT");
        // TODO refresh the button display
    }

    public static void triggerCaps(PosScreen pos) {
        capsLockSet = !capsLockSet;
        // TODO refresh the button display
    }

    public static void triggerA(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('A');
        } else {
            input.appendChar('a');
        }
    }

    public static void triggerB(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('B');
        } else {
            input.appendChar('b');
        }
    }

    public static void triggerC(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('C');
        } else {
            input.appendChar('c');
        }
    }

    public static void triggerD(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('D');
        } else {
            input.appendChar('d');
        }
    }

    public static void triggerE(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('E');
        } else {
            input.appendChar('e');
        }
    }

    public static void triggerF(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('F');
        } else {
            input.appendChar('f');
        }
    }

    public static void triggerG(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('G');
        } else {
            input.appendChar('g');
        }
    }

    public static void triggerH(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('H');
        } else {
            input.appendChar('h');
        }
    }

    public static void triggerI(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('I');
        } else {
            input.appendChar('i');
        }
    }

    public static void triggerJ(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('J');
        } else {
            input.appendChar('j');
        }
    }

    public static void triggerK(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('K');
        } else {
            input.appendChar('k');
        }
    }

    public static void triggerL(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('L');
        } else {
            input.appendChar('l');
        }
    }

    public static void triggerM(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('M');
        } else {
            input.appendChar('m');
        }
    }

    public static void triggerN(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('N');
        } else {
            input.appendChar('n');
        }
    }

    public static void triggerO(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('O');
        } else {
            input.appendChar('o');
        }
    }

    public static void triggerP(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('P');
        } else {
            input.appendChar('p');
        }
    }

    public static void triggerQ(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('Q');
        } else {
            input.appendChar('q');
        }
    }

    public static void triggerR(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('R');
        } else {
            input.appendChar('r');
        }
    }

    public static void triggerS(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('S');
        } else {
            input.appendChar('s');
        }
    }

    public static void triggerT(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('T');
        } else {
            input.appendChar('t');
        }
    }

    public static void triggerU(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('U');
        } else {
            input.appendChar('u');
        }
    }

    public static void triggerV(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('V');
        } else {
            input.appendChar('v');
        }
    }

    public static void triggerW(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('W');
        } else {
            input.appendChar('w');
        }
    }

    public static void triggerX(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('X');
        } else {
            input.appendChar('x');
        }
    }

    public static void triggerY(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('Y');
        } else {
            input.appendChar('y');
        }
    }

    public static void triggerZ(PosScreen pos) {
        Input input = pos.getInput();
        if (capsLockSet || input.isFunctionSet("SHIFT")) {
            input.appendChar('Z');
        } else {
            input.appendChar('z');
        }
    }
}
