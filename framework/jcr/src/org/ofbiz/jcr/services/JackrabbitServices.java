package org.ofbiz.jcr.services;

import java.util.Date;
import java.util.Map;

import javax.jcr.Session;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.jcr.access.jackrabbit.RepositoryAccessJackrabbit;
import org.ofbiz.jcr.loader.JCRFactoryUtil;
import org.ofbiz.jcr.orm.jackrabbit.OfbizRepositoryMappingJackrabbitNews;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class JackrabbitServices {

    private static String module = JackrabbitServices.class.getName();

    public static Map<String, Object> determineJackrabbitRepositorySpeed(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Integer maxNodes = (Integer) context.get("maxNodes");

        Long start = 0l;
        Long diff = 0l;

        Session session = JCRFactoryUtil.getSession();
        start = new Date().getTime();
        for (int i = 0; i <= maxNodes; i++) {
            try {
                // add a node
                session.getRootNode().addNode("__Speedtest_Node-" + i);
                session.save();
                // remove the node
                session.removeItem("/__Speedtest_Node-" + i);
                session.save();
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }

        session.logout();
        diff = (new Date().getTime() - start);
        result.put("repositoryDirectAccessTime", diff.toString());

        RepositoryAccessJackrabbit access = new RepositoryAccessJackrabbit(userLogin);
        start = new Date().getTime();
        for (int i = 0; i <= maxNodes; i++) {
            try {
                OfbizRepositoryMappingJackrabbitNews news = new OfbizRepositoryMappingJackrabbitNews("/__Speedtest_Node-" + i, "de", "", null, "");
                access.storeContentObject(news);
                access.removeContentObject("/__Speedtest_Node-" + i);
            } catch (Exception e) {
                Debug.logError(e, module);
            }

        }

        access.closeAccess();
        diff = (new Date().getTime() - start);
        result.put("repositoryOcmAccessTime", diff.toString());

        return result;
    }
}
