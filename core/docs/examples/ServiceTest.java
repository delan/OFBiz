/*
 * $Id$
 */

//package org.ofbiz.core.service;

import java.util.*;

import org.ofbiz.core.service.*;

public class ServiceTest {

    public static Map testOne(DispatchContext dctx, Map context) {
        Map response = new HashMap();
        if (!context.containsKey("message")) {
            response.put("resp", "no message found");
        } else {
            System.out.println("-----SERVICE TEST----- : " + (String) context.get("message"));
            response.put("resp", "service done");
        }

        System.out.println("----- SVC: " + dctx.getRootPath() + " -----");
        return response;
    }

}

