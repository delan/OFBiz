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
import org.ofbiz.pos.component.Output;
import org.ofbiz.pos.PosTransaction;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import org.ofbiz.content.xui.XuiSession;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class SecurityEvents {

    public static final String module = SecurityEvents.class.getName();

    public static void login(PosScreen pos) {
        XuiSession session = pos.getSession();
        Output output = pos.getOutput();
        Input input = pos.getInput();

        String[] func = input.getLastFunction();
        String text = input.value();
        if (func == null || func[0].equals("LOGIN")) {
            if (UtilValidate.isEmpty(func[1]) && UtilValidate.isEmpty(text)) {
                output.print(Output.ULOGIN);
            } else if (UtilValidate.isEmpty(func[1])) {
                output.print(Output.UPASSW);
            } else {
                String username = func[1];
                String password = text;
                boolean passed = false;
                try {
                    session.checkLogin(username, password);
                    passed = true;
                } catch (XuiSession.UserLoginFailure e) {
                    output.print(e.getMessage());
                    input.clear();
                }
                if (passed) {
                    pos.setLock(false);
                    pos.refresh();
                    input.clear();
                    return;
                }
            }
            input.setFunction("LOGIN");

        } else {
            Debug.log("Login function called but not prepared as a function!", module);
        }
    }

    public static void logout(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        XuiSession session = pos.getSession();
        trans.voidSale();
        session.logout();
        pos.setLock(true);
    }

    public static void mgrLogin(PosScreen pos) {
        pos.showPage("main/mgrpanel");
    }

    public static void lock(PosScreen pos) {
        pos.setLock(true);
    }    
}
