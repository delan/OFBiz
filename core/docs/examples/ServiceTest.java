/* 
 * $Id$
 */

//package org.ofbiz.core.service;

import java.util.*;

public class ServiceTest {
    
    public static Map testOne(Map context) {
        Map response = new HashMap();
        if ( !context.containsKey("message") ) {
            response.put("resp","no message found");            
        }
        else {
            System.out.println("-----SERVICE TEST----- : " +(String)context.get("message"));
            response.put("resp","service done");
        }
        
        return response;
    }
       
}
        
