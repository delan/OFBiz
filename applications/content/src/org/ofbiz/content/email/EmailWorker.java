/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.content.email;

import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import javolution.util.FastMap;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.GenericServiceException;

public class EmailWorker {

    public final static String module = EmailWorker.class.getName();

    public String getForwardedField(MimeMessage message) {
        String fieldValue = null;
        return fieldValue;
    }
    
    public static int addAttachmentsToCommEvent(Multipart messageContent, String subject, String communicationEventId, LocalDispatcher dispatcher, GenericValue userLogin) 
        throws MessagingException, IOException, GenericServiceException {
        Map commEventMap = FastMap.newInstance();
        commEventMap.put("communicationEventId", communicationEventId);
        commEventMap.put("contentTypeId", "DOCUMENT");
        commEventMap.put("mimeTypeId", "text/html");
        commEventMap.put("userLogin", userLogin);
        if (subject != null && subject.length() > 80) { 
            subject = subject.substring(0,80); // make sure not too big for database field. (20 characters for filename)
        }
        currentIndex = "";
        attachmentCount = 0;
        return addMultipartAttachementToComm(messageContent, commEventMap, subject, dispatcher, userLogin);
    }
    private static String currentIndex = "";
    private static int attachmentCount = 0;
    private static int addMultipartAttachementToComm(Multipart multipart, Map commEventMap, String subject, LocalDispatcher dispatcher, GenericValue userLogin)
    throws MessagingException, IOException, GenericServiceException {
        try {
            int multipartCount = multipart.getCount();
            // Debug.logInfo(currentIndex + "====number of attachments: " + multipartCount, module);
            for (int i=0; i < multipartCount; i++) {
            	// Debug.logInfo(currentIndex + "====processing attachment: " + i, module);
                Part part = multipart.getBodyPart(i);
                String thisContentTypeRaw = part.getContentType();
                // Debug.logInfo("====thisContentTypeRaw: " + thisContentTypeRaw, module);
                int idx2 = thisContentTypeRaw.indexOf(";");
                if (idx2 == -1) idx2 = thisContentTypeRaw.length();
                String thisContentType = thisContentTypeRaw.substring(0, idx2);
                String disposition = part.getDisposition();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (part instanceof Multipart) {
                    currentIndex = currentIndex.concat("." + i);
                	// Debug.logInfo("=====attachment contain attachment, index:" + currentIndex, module);
                    return addMultipartAttachementToComm((Multipart) part.getContent(), commEventMap, subject, dispatcher, userLogin);
                }
            	// Debug.logInfo("=====attachment not contains attachment, index:" + currentIndex, module);
            	// Debug.logInfo("=====check for currentIndex(" + currentIndex  + ") against master contentIndex(" + EmailServices.contentIndex + ")", module);
                if(currentIndex.concat("." + i).equals(EmailServices.contentIndex)) continue;

                // The first test should not pass, because if it exists, it should be the bodyContentIndex part
                // Debug.logInfo("====check for disposition: " + disposition + " contentType: '" + thisContentType + "' variable i:" + i, module);
                if ((disposition == null && thisContentType.startsWith("text")) 
                        || ((disposition != null)
                                && (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE))
                                ) )
                {
                    String attFileName = part.getFileName();
                    Debug.logInfo("===processing attachment: " + attFileName, module);
                    if (!UtilValidate.isEmpty(attFileName)) { 
                           commEventMap.put("contentName", attFileName); 
                           commEventMap.put("description", subject + "-" + attachmentCount);
                    } else {
                        commEventMap.put("contentName", subject + "-" + attachmentCount);
                    }
                    commEventMap.put("drMimeTypeId", thisContentType);
                    if (thisContentType.startsWith("text")) {
                        String content = (String)part.getContent();
                        commEventMap.put("drDataResourceTypeId", "ELECTRONIC_TEXT");
                        commEventMap.put("textData", content);
                    } else {
                        InputStream is = part.getInputStream();
                        int c;
                        while ((c = is.read()) > -1) {
                            baos.write(c);
                        }
                        ByteBuffer imageData = ByteBuffer.wrap(baos.toByteArray());
                        int len = imageData.limit();
                        if (Debug.infoOn()) Debug.logInfo("imageData length: " + len, module);
                        commEventMap.put("drDataResourceName", part.getFileName());
                        commEventMap.put("imageData", imageData);
                        commEventMap.put("drDataResourceTypeId", "IMAGE_OBJECT");
                        commEventMap.put("_imageData_contentType", thisContentType);
                    }
                    dispatcher.runSync("createCommContentDataResource", commEventMap);
                    attachmentCount++;
                }
            }
        } catch (MessagingException e) {
            Debug.logError(e, module);
        } catch (IOException e) {
            Debug.logError(e, module);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }
        return attachmentCount;
    }
}
