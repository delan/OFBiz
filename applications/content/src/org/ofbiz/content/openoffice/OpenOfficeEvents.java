package org.ofbiz.content.openoffice;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;

public class OpenOfficeEvents {

    public static final String module = OpenOfficeServices.class.getName();

    public static String genCompDocPdf(HttpServletRequest request, HttpServletResponse response) {
        String responseStr = "success";
        ByteWrapper byteWrapper = null;
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        ServletContext servletContext = session.getServletContext();
        LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
        Map paramMap = UtilHttp.getParameterMap(request);
        String contentId = (String)paramMap.get("contentId");
        Locale locale = UtilHttp.getLocale(request);
        String rootDir = null;
        String webSiteId = null;
        String https = null;
        
        if (UtilValidate.isEmpty(rootDir)) {
            rootDir = servletContext.getRealPath("/");
        }
        if (UtilValidate.isEmpty(webSiteId)) {
            webSiteId = (String) servletContext.getAttribute("webSiteId");
        }
        if (UtilValidate.isEmpty(https)) {
            https = (String) servletContext.getAttribute("https");
        }
        
        Map mapIn = new HashMap();
        mapIn.put("contentId", contentId);
        mapIn.put("locale", locale);
        mapIn.put("rootDir", rootDir);
        mapIn.put("webSiteId", webSiteId);
        mapIn.put("https", https);
        mapIn.put("userLogin", userLogin);
        
        Map results = null;
        try {
            results = dispatcher.runSync("renderCompDocPdf", mapIn);
        } catch(ServiceAuthException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        } catch(GenericServiceException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        } catch(Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        if (ServiceUtil.isError(results)) {
            request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(results));
            return "error";
        }
        
        ByteWrapper pdfByteWrapper = (ByteWrapper)results.get("pdfByteWrapper");

        // setup content type
        String contentType = "application/pdf; charset=ISO-8859-1";

        ByteArrayInputStream bais = new ByteArrayInputStream(pdfByteWrapper.getBytes());
        try {
            FileOutputStream fos = new FileOutputStream("/home/byersa/pdftest.pdf");
            fos.write(pdfByteWrapper.getBytes());
        } catch(FileNotFoundException e) {
        } catch(IOException e) {
            
        }
        try {
            UtilHttp.streamContentToBrowser(response, bais, pdfByteWrapper.getLength(), contentType);
        } catch(IOException e) {
            
        }
        return responseStr;
    }
    public static String genContentPdf(HttpServletRequest request, HttpServletResponse response) {
        String responseStr = "success";
        ByteWrapper byteWrapper = null;
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        ServletContext servletContext = session.getServletContext();
        LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
        Map paramMap = UtilHttp.getParameterMap(request);
        String contentId = (String)paramMap.get("contentId");
        Locale locale = UtilHttp.getLocale(request);
        String rootDir = null;
        String webSiteId = null;
        String https = null;
        
        if (UtilValidate.isEmpty(rootDir)) {
            rootDir = servletContext.getRealPath("/");
        }
        if (UtilValidate.isEmpty(webSiteId)) {
            webSiteId = (String) servletContext.getAttribute("webSiteId");
        }
        if (UtilValidate.isEmpty(https)) {
            https = (String) servletContext.getAttribute("https");
        }
        
        Map mapIn = new HashMap();
        mapIn.put("contentId", contentId);
        mapIn.put("locale", locale);
        mapIn.put("rootDir", rootDir);
        mapIn.put("webSiteId", webSiteId);
        mapIn.put("https", https);
        mapIn.put("userLogin", userLogin);
        
        Map results = null;
        try {
            results = dispatcher.runSync("renderContentPdf", mapIn);
        } catch(ServiceAuthException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        } catch(GenericServiceException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        } catch(Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        if (ServiceUtil.isError(results)) {
            request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(results));
            return "error";
        }
        
        ByteWrapper pdfByteWrapper = (ByteWrapper)results.get("pdfByteWrapper");

        // setup content type
        String contentType = "application/pdf; charset=ISO-8859-1";

        ByteArrayInputStream bais = new ByteArrayInputStream(pdfByteWrapper.getBytes());
        try {
            FileOutputStream fos = new FileOutputStream("/home/byersa/pdftest.pdf");
            fos.write(pdfByteWrapper.getBytes());
        } catch(FileNotFoundException e) {
        } catch(IOException e) {
            
        }
        try {
            UtilHttp.streamContentToBrowser(response, bais, pdfByteWrapper.getLength(), contentType);
        } catch(IOException e) {
            
        }
        return responseStr;
    }
}
