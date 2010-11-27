package org.ofbiz.accounting.thirdparty.authorizedotnet;

import java.util.Map;

import javolution.util.FastMap;

public class AIMRespPositions extends AuthorizeResponse.RespPositions {

    // AIM v3.1 response positions
    private static Map<String, Integer> positions = FastMap.newInstance();
    static {
        positions.put(AuthorizeResponse.RESPONSE_CODE, 1);
        positions.put(AuthorizeResponse.REASON_CODE, 3);
        positions.put(AuthorizeResponse.REASON_TEXT, 4);
        positions.put(AuthorizeResponse.AUTHORIZATION_CODE, 5);
        positions.put(AuthorizeResponse.AVS_RESULT_CODE, 6);
        positions.put(AuthorizeResponse.CVV_RESULT_CODE, 39);
        positions.put(AuthorizeResponse.TRANSACTION_ID, 7);
        positions.put(AuthorizeResponse.AMOUNT, 10);               
    }
    
    @Override
    public int getPosition(String name) {
        if (positions.containsKey(name)) {
            return positions.get(name);
        } else {
            return -1;
        }              
    }
    
    @Override
    public String getApprovalString() {
        return "1";
    }
}
