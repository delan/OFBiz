/*
 * $Id: PdfSurveyServices.java 5462 2005-08-05 18:35:48Z byersa $
 *
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.content.survey;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericServiceException;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLister;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.DocumentException;


/**
 * PdfSurveyServices Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version $Rev: 5462 $
 * @since 3.2
 * 
 *  
 */

public class PdfSurveyServices {
	
    public static final String module = PdfSurveyServices.class.getName();

    /**
     * 
     */
    public static Map buildSurveyFromPdf(DispatchContext dctx, Map context) {

    	String surveyId = null;
    	try {
			
        	GenericDelegator delegator = dctx.getDelegator();
        	String pdfFileNameIn = (String)context.get("pdfFileNameIn");
        	String surveyName = (String)context.get("surveyName");
        	ByteArrayOutputStream os = new ByteArrayOutputStream();
			PdfReader r = new PdfReader(pdfFileNameIn);
			PdfStamper s = new PdfStamper(r,os);
			AcroFields fs = s.getAcroFields();
			HashMap hm = fs.getFields();
			
			GenericValue survey = delegator.makeValue("Survey", UtilMisc.toMap("surveyName", surveyName));
			surveyId = delegator.getNextSeqId("Survey");
			survey.set("surveyId", surveyId);
			delegator.create(survey);
			s.setFormFlattening(true);
			for (Iterator i = hm.keySet().iterator();i.hasNext();)
			{
				String fieldName = (String)i.next();
				AcroFields.Item item = fs.getFieldItem(fieldName);
				int type = fs.getFieldType(fieldName);
				String value = fs.getField(fieldName);
				System.out.println("item:" + item + " value:" +value);
				GenericValue surveyQuestion = delegator.makeValue("SurveyQuestion", UtilMisc.toMap("question", fieldName));
				String surveyQuestionId = delegator.getNextSeqId("SurveyQuestion");
				surveyQuestion.set("surveyQuestionId", surveyQuestionId);
				surveyQuestion.set("description", fieldName);

				if (type == AcroFields.FIELD_TYPE_TEXT) {
					
					surveyQuestion.set("surveyQuestionTypeId", "TEXT_SHORT");
					
				} else if (type == AcroFields.FIELD_TYPE_RADIOBUTTON) {
					
					surveyQuestion.set("surveyQuestionTypeId", "OPTION");
					
				} else {
				}
				
				delegator.create(surveyQuestion);

				GenericValue surveyQuestionAppl = delegator.makeValue("SurveyQuestionAppl", UtilMisc.toMap("surveyId", surveyId, "surveyQuestionId", surveyQuestionId));
				surveyQuestionAppl.set("fromDate", UtilDateTime.nowTimestamp());
				delegator.create(surveyQuestionAppl);
				
			}			
			s.close();
        } catch (GenericEntityException e) {
            String errMsg = "Error generating PDF: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        } catch (Exception e) {
            String errMsg = "Error generating PDF: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        Map results = ServiceUtil.returnSuccess();
        results.put("surveyId", surveyId);
        return results;
    }
    
    /**
     * 
     */
    public static Map buildSurveyResponseFromPdf(DispatchContext dctx, Map context) {

    	String surveyResponseId = null;
    	try {
			
        	GenericDelegator delegator = dctx.getDelegator();
        	String pdfFileNameIn = (String)context.get("pdfFileNameIn");
        	String partyId = (String)context.get("partyId");
        	String surveyId = (String)context.get("surveyId");
        	ByteArrayOutputStream os = new ByteArrayOutputStream();
			PdfReader r = new PdfReader(pdfFileNameIn);
			PdfStamper s = new PdfStamper(r,os);
			AcroFields fs = s.getAcroFields();
			HashMap hm = fs.getFields();
			
			surveyResponseId = delegator.getNextSeqId("SurveyResponse");
			GenericValue surveyResponse = delegator.makeValue("SurveyResponse", UtilMisc.toMap("surveyResponseId", surveyResponseId, "surveyId", surveyId, "partyId", partyId));
			surveyResponse.set("responseDate", UtilDateTime.nowTimestamp());
			surveyResponse.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			delegator.create(surveyResponse);
			
			s.setFormFlattening(true);
			for (Iterator i = hm.keySet().iterator();i.hasNext();)
			{
				String fieldName = (String)i.next();
				//AcroFields.Item item = fs.getFieldItem(fieldName);
				//int type = fs.getFieldType(fieldName);
				String value = fs.getField(fieldName);
				
				List questions = delegator.findByAnd("SurveyQuestionAndAppl", UtilMisc.toMap("surveyId", surveyId, "description", fieldName));
				if (questions.size() == 0 ) {
					Debug.logInfo("No question found for surveyId:" + surveyId + " and description:" + fieldName, module);
					continue;
				}
				
				GenericValue surveyQuestionAndAppl = (GenericValue)questions.get(0);
				String surveyQuestionId = (String)surveyQuestionAndAppl.get("surveyQuestionId");
				String surveyQuestionTypeId = (String)surveyQuestionAndAppl.get("surveyQuestionTypeId");
				GenericValue surveyResponseAnswer = delegator.makeValue("SurveyResponseAnswer", UtilMisc.toMap("surveyResponseId", surveyResponseId, "surveyQuestionId", surveyQuestionId));
				if (surveyQuestionTypeId ==null || surveyQuestionTypeId.equals("TEXT_SHORT")) {
					surveyResponseAnswer.set("textResponse", value);
				}

				delegator.create(surveyResponseAnswer);

				
			}			
			s.close();
        } catch (GenericEntityException e) {
            String errMsg = "Error generating PDF: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        } catch (Exception e) {
            String errMsg = "Error generating PDF: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        Map results = ServiceUtil.returnSuccess();
        results.put("surveyResponseId", surveyResponseId);
        return results;
    }
    
    /*
     * $Id: ListFields.java,v 1.3 2005/05/09 11:52:44 blowagie Exp $
     * $Name:  $
     *
     * This code is part of the 'iText Tutorial'.
     * You can find the complete tutorial at the following address:
     * http://itextdocs.lowagie.com/tutorial/
     *
     * This code is distributed in the hope that it will be useful,
     * but WITHOUT ANY WARRANTY; without even the implied warranty of
     * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
     *
     * itext-questions@lists.sourceforge.net
     */

    /**
     */
    public static Map getAcroFieldsFromPdf(DispatchContext dctx, Map context) {
        
		Map acroFieldMap = new HashMap();
		try {
	    	String pdfFileNameIn = (String)context.get("pdfFileNameIn");
	    	ByteArrayOutputStream os = new ByteArrayOutputStream();
			PdfReader r = new PdfReader(pdfFileNameIn);
			PdfStamper s = new PdfStamper(r,os);
			AcroFields fs = s.getAcroFields();
			HashMap map = fs.getFields();
			
			s.setFormFlattening(true);
			
			// Debug code to get the values for setting TDP
	//		String[] sa = fs.getAppearanceStates("TDP");
	//		for (int i=0;i<sa.length;i++)
	//			Debug.log("Appearance="+sa[i]);
			
			Iterator iter = map.keySet().iterator();
			while (iter.hasNext()) {
				String fieldName=(String)iter.next();
				String parmValue = fs.getField(fieldName);
				acroFieldMap.put(fieldName, parmValue);
			}			
                 
        } catch(DocumentException e) {
            System.err.println(e.getMessage());
            ServiceUtil.returnError(e.getMessage());
        } catch(IOException ioe) {
            System.err.println(ioe.getMessage());
            ServiceUtil.returnError(ioe.getMessage());
        }
        
	Map results = ServiceUtil.returnSuccess();
	results.put("acroFieldMap", acroFieldMap);
	return results;
	}
    
    /**
     */
    public static Map setAcroFields(DispatchContext dctx, Map context) {
        
    	Map results = ServiceUtil.returnSuccess();
		try {
	    	String pdfFileNameIn = (String)context.get("pdfFileNameIn");
			Map acroFieldMap = (HashMap)context.get("acroFieldMap");
	    	String pdfFileNameOut = (String)context.get("pdfFileNameOut");
	    	if (pdfFileNameOut == null) {
	    		pdfFileNameOut = pdfFileNameIn;
	    	}
	    	FileOutputStream os = new FileOutputStream(pdfFileNameOut);
			PdfReader r = new PdfReader(pdfFileNameIn);
			PdfStamper s = new PdfStamper(r,os);
			AcroFields fs = s.getAcroFields();
			HashMap map = fs.getFields();
			
			s.setFormFlattening(true);
			
			// Debug code to get the values for setting TDP
	//		String[] sa = fs.getAppearanceStates("TDP");
	//		for (int i=0;i<sa.length;i++)
	//			Debug.log("Appearance="+sa[i]);
			Iterator iter = map.keySet().iterator();
			while (iter.hasNext()) {
				String fieldName=(String)iter.next();
				String fieldValue = fs.getField(fieldName);
				Object obj = acroFieldMap.get(fieldName);
				if (obj instanceof Date) {
					Date d=(Date)obj;
					fieldValue=UtilDateTime.toDateString(d);
				} else if (obj instanceof Long) {
					Long lg=(Long)obj;
					fieldValue=lg.toString();
				} else if (obj instanceof Integer) {
					Integer ii=(Integer)obj;
					fieldValue=ii.toString();
				}	else {
					fieldValue=(String)obj;
				}
			
				if (fieldValue != null)
					fs.setField(fieldName, fieldValue);
			}			
            os.close();
        } catch(DocumentException e) {
            System.err.println(e.getMessage());
            ServiceUtil.returnError(e.getMessage());
        } catch(FileNotFoundException e) {
            System.err.println(e.getMessage());
            ServiceUtil.returnError(e.getMessage());
        } catch(IOException ioe) {
            System.err.println(ioe.getMessage());
            ServiceUtil.returnError(ioe.getMessage());
        }
        
	return results;
	}
    
    /**
     */
    public static Map setAcroFieldsFromSurvey(DispatchContext dctx, Map context) {
        
    	GenericDelegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Map results = ServiceUtil.returnSuccess();
    		String pdfFileNameIn = (String)context.get("pdfFileNameIn");
        	String surveyResponseId = (String)context.get("surveyResponseId");
			Map acroFieldMap = new HashMap();
	    	String pdfFileNameOut = (String)context.get("pdfFileNameOut");
	    	if (pdfFileNameOut == null) {
	    		pdfFileNameOut = pdfFileNameIn;
	    	}
        
	    	try {
	    	    List responses = delegator.findByAnd("SurveyResponseAnswer", UtilMisc.toMap("surveyResponseId", surveyResponseId));
	    	    Iterator iter = responses.iterator();
	    	    while (iter.hasNext()) {
		    	    String value = null;
	    	    	GenericValue surveyResponseAnswer = (GenericValue)iter.next();
	            	String surveyQuestionId = (String)surveyResponseAnswer.get("surveyQuestionId");
		    		GenericValue surveyQuestion = delegator.findByPrimaryKey("SurveyQuestion", UtilMisc.toMap("surveyQuestionId", surveyQuestionId));
		    		String questionType = surveyQuestion.getString("surveyQuestionTypeId");
		    		String fieldName = surveyQuestion.getString("description");
		            if ("OPTION".equals(questionType)) {
	                    value = surveyResponseAnswer.getString("surveyOptionSeqId");
		            } else if ("BOOLEAN".equals(questionType)) {
	                    value = surveyResponseAnswer.getString("booleanResponse");
		            } else if ("NUMBER_LONG".equals(questionType)
		            		|| "NUMBER_CURRENCY".equals(questionType)
		            		|| "NUMBER_FLOAT".equals(questionType)
		            		) {
		                Double num = surveyResponseAnswer.getDouble("numericResponse");
		                if (num != null) {
		                	value = num.toString();
		                }
		            } else if ("SEPERATOR_LINE".equals(questionType) || "SEPERATOR_TEXT".equals(questionType)) {
		                // not really a question; ingore completely
		            } else {
		            	value = surveyResponseAnswer.getString("textResponse");
		            }
		            acroFieldMap.put(fieldName, value);
	    	    }
	    	} catch (GenericEntityException e) {
	            System.err.println(e.getMessage());
	            ServiceUtil.returnError(e.getMessage());
	    	}
	    	
	    	try {
	    		Map map = dispatcher.runSync("setAcroFields", UtilMisc.toMap("pdfFileNameIn", pdfFileNameIn, "pdfFileNameOut", pdfFileNameOut, "acroFieldMap", acroFieldMap));
	    		if (ServiceUtil.isError(map)) {
	    			String errMsg = ServiceUtil.makeErrorMessage(map, null, null, null, null);
		            System.err.println(errMsg);
		            ServiceUtil.returnError(errMsg);
	    		}
	    	} catch (GenericServiceException e) {
	            System.err.println(e.getMessage());
	            ServiceUtil.returnError(e.getMessage());
	    	}
	    	
	return results;
	}
}
