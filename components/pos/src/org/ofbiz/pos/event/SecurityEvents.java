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
import org.ofbiz.entity.GenericValue;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.2
 */
public class SecurityEvents {

    public static final String module = SecurityEvents.class.getName();

    public static void login(PosScreen pos) {
        String[] func = pos.getInput().getFunction("LOGIN");
        if (func == null) {
            pos.getInput().setFunction("LOGIN", "");
        }
        baseLogin(pos, false);
    }

    public static void logout(PosScreen pos) {
        PosTransaction trans = PosTransaction.getCurrentTx(pos.getSession());
        XuiSession session = pos.getSession();
        trans.voidSale();
        session.logout();
        pos.showPage("main/pospanel");
        PosScreen.currentScreen.setLock(true);
    }

    public static void mgrLogin(PosScreen pos) {
        XuiSession session = pos.getSession();
        if (session.hasRole(session.getUserLogin(), "MANAGER")) {
            ManagerEvents.mgrLoggedIn = true;
            pos.showPage("main/mgrpanel");
            PosScreen.currentScreen.getInput().clear();
        } else {
            String[] func = pos.getInput().getFunction("MGRLOGIN");
            if (func == null) {
                pos.getInput().setFunction("MGRLOGIN", "");
            }
            baseLogin(pos, true);
        }
    }

    public static void lock(PosScreen pos) {
        pos.setLock(true);
    }

    private static void baseLogin(PosScreen pos, boolean mgr) {
        XuiSession session = pos.getSession();
        Output output = pos.getOutput();
        Input input = pos.getInput();

        String loginFunc = mgr ? "MGRLOGIN" : "LOGIN";
        String[] func = input.getLastFunction();
        String text = input.value();
        if (func != null && func[0].equals(loginFunc)) {
            if (UtilValidate.isEmpty(func[1]) && UtilValidate.isEmpty(text)) {
                output.print(Output.ULOGIN);
            } else if (UtilValidate.isEmpty(func[1])) {
                output.print(Output.UPASSW);
            } else {
                String username = func[1];
                String password = text;
                if (!mgr) {
                    boolean passed = false;
                    try {
                        session.login(username, password);
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
                } else {
                    GenericValue mgrUl = null;
                    try {
                        mgrUl = session.checkLogin(username, password);
                    } catch (XuiSession.UserLoginFailure e) {
                        output.print(e.getMessage());
                        input.clear();
                    }
                    if (mgrUl != null) {
                        boolean isMgr = session.hasRole(mgrUl, "MANAGER");
                        if (!isMgr) {
                            output.print("User is not a valid manager!");
                            input.clear();
                        } else {
                            ManagerEvents.mgrLoggedIn = true;
                            pos.showPage("main/mgrpanel");
                            PosScreen.currentScreen.getInput().clear();                            
                        }
                    }
                }
            }
            input.setFunction(loginFunc);

        } else {
            Debug.log("Login function called but not prepared as a function!", module);
        }
    }
}
