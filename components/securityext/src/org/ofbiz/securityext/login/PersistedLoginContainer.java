/*
 * $Id: PersistedLoginContainer.java,v 1.3 2004/07/05 16:56:01 ajzeneski Exp $
 *
 */
package org.ofbiz.securityext.login;

import java.util.Map;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.Timer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilObject;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.transaction.GenericTransactionException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.3 $
 * @since      Jul 4, 2004
 */
public class PersistedLoginContainer implements Container {

    public static final String module = PersistedLoginContainer.class.getName();
    public static final String entityName = "PersistedLogin";
    private static PersistedLoginContainer plc = new PersistedLoginContainer();

    protected Map externalLoginKeys = new HashMap();
    protected Map loggedInSessions = new HashMap();
    protected GenericDelegator delegator = null;
    protected Timer mgr = null;

    public void init(String[] args) throws ContainerException {
        plc = this;
    }

    public boolean start(String configFileLocation) throws ContainerException {
        ContainerConfig.Container cc = ContainerConfig.getContainer("login-container", configFileLocation);
        String delegatorName = ContainerConfig.getPropertyValue(cc, "delegator-name", "default");
        long timerPeriod = ContainerConfig.getPropertyValue(cc, "timer-period", 5000);
        long timerDelay = ContainerConfig.getPropertyValue(cc, "timer-delay", 5000);
        this.delegator = GenericDelegator.getGenericDelegator(delegatorName);
        try {
            updateInfos();
        } catch (GeneralRuntimeException e) {
            throw new ContainerException(e);
        }

        mgr = new Timer();
        mgr.schedule(new PersistedLoginMonitor(this), timerDelay, timerPeriod);

        return true;
    }

    public void stop() throws ContainerException {
        if (mgr != null) {
            mgr.cancel();
        }

        try {
            updateInfos();
        } catch (GeneralRuntimeException e) {
            Debug.logError(e, module);
        }
    }


    // persistence methods

    protected synchronized void clearInfo() {
        try {
            delegator.removeByAnd(entityName, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
    }

    protected synchronized void updateInfos() {
        // start the tx
        try {
            TransactionUtil.begin();
        } catch (GenericTransactionException e) {
            throw new GeneralRuntimeException(e);
        }

        // re-load the session info
        List infosList = null;
        try {
            infosList = delegator.findAll(entityName);
        } catch (GenericEntityException e) {
            throw new GeneralRuntimeException(e);
        }
        GenericValue loginInfo = EntityUtil.getFirst(infosList);
        if (loginInfo == null) {
            loginInfo = delegator.makeValue(entityName, UtilMisc.toMap("persistedLoginId", delegator.getNextSeqId(entityName)));
            try {
                loginInfo.create();
            } catch (GenericEntityException e) {
                throw new GeneralRuntimeException(e);
            }
        }

        this.updateSessionInfo(loginInfo);
        this.updateExternalKeyInfo(loginInfo);

        // commit tx
        try {
            TransactionUtil.commit();
        } catch (GenericTransactionException e) {
            throw new GeneralRuntimeException(e);
        }
    }

    private void updateSessionInfo(GenericValue loginInfo) {
        if (loginInfo != null) {
            // deserialize the info into a Map
            byte[] bytes = loginInfo.getBytes("sessionLoginInfo");
            if (bytes != null) {
                Object persistedInfo = UtilObject.getObject(bytes);
                if (persistedInfo != null && persistedInfo instanceof Map) {
                    plc.loggedInSessions.putAll((Map) persistedInfo);
                }
            }
        }

        // store the our session info
        byte[] bytes = UtilObject.getBytes(plc.loggedInSessions);
        loginInfo.setBytes("sessionLoginInfo", bytes);
        try {
            delegator.store(loginInfo);
        } catch (GenericEntityException e) {
            throw new GeneralRuntimeException(e);
        }
    }

    private void updateExternalKeyInfo(GenericValue keyInfo) {        
        if (keyInfo != null) {
            // deserialize the info into a Map
            byte[] bytes = keyInfo.getBytes("externalKeyInfo");
            if (bytes != null) {
                Object persistedInfo = UtilObject.getObject(bytes);
                if (persistedInfo != null && persistedInfo instanceof Map) {
                    plc.externalLoginKeys.putAll((Map) persistedInfo);
                }
            }
        }

        // store the our session info
        byte[] bytes = UtilObject.getBytes(plc.externalLoginKeys);
        keyInfo.setBytes("externalKeyInfo", bytes);
        try {
            delegator.store(keyInfo);
        } catch (GenericEntityException e) {
            throw new GeneralRuntimeException(e);
        }
    }

    // session login methods

    public static boolean isLoggedInSession(GenericValue userLogin, HttpServletRequest request) {
        return isLoggedInSession(userLogin.getString("userLoginId"), request, true);
    }

    public static boolean isLoggedInSession(String userLoginId, HttpServletRequest request, boolean checkSessionId) {
        if (userLoginId != null) {
            Map webappMap = (Map) plc.loggedInSessions.get(userLoginId);
            if (webappMap == null) {
                return false;
            } else {
                String sessionId = (String) webappMap.get(UtilHttp.getApplicationName(request));
                if (!checkSessionId) {
                    if (sessionId == null) {
                        return false;
                    }
                } else {
                    if (sessionId == null || !sessionId.equals(request.getSession().getId())) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static void loginToSession(GenericValue userLogin, HttpServletRequest request) {
        if (userLogin != null) {
            Map webappMap = (Map) plc.loggedInSessions.get(userLogin.get("userLoginId"));
            if (webappMap == null) {
                webappMap = new HashMap();
                plc.loggedInSessions.put(userLogin.get("userLoginId"), webappMap);
            }

            String webappName = UtilHttp.getApplicationName(request);
            webappMap.put(webappName, request.getSession().getId());
        }
    }

    public static void logoutFromAllSessions(GenericValue userLogin) {
        if (userLogin != null) {
            plc.loggedInSessions.remove(userLogin.get("userLoginId"));
        }
    }

    // external login methods

    public static String createExternalLoginKey() {
        // no key made yet for this request, create one
        String externalKey = null;
        while (externalKey == null || plc.externalLoginKeys.containsKey(externalKey)) {
            externalKey = "EL" + Long.toString(Math.round(Math.random() * 1000000)) + Long.toString(Math.round(Math.random() * 1000000));
        }
        return externalKey;
    }

    public static GenericValue getExternalLoginKeyValue(String externalKey) {
        return (GenericValue) plc.externalLoginKeys.get(externalKey);
    }

    public static void setExternalLoginKey(String externalKey, GenericValue userLogin) {
        plc.externalLoginKeys.put(externalKey, userLogin);
    }

    public static void removeExternalLoginKey(String externalKey) {
        plc.externalLoginKeys.remove(externalKey);
    }

    public static void cleanupExternalLoginKey(HttpSession session) {
        String sesExtKey = (String) session.getAttribute(LoginEvents.EXTERNAL_LOGIN_KEY_ATTR);
        if (sesExtKey != null) {
            plc.externalLoginKeys.remove(sesExtKey);
        }
    }

    class PersistedLoginMonitor extends TimerTask {

        private PersistedLoginContainer plc = null;

        protected PersistedLoginMonitor(PersistedLoginContainer plc) {
            this.plc = plc;
        }

        public void run() {
            plc.updateInfos();
        }
    }
}
