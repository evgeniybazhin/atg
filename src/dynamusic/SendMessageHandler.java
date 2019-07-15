/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2003 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package dynamusic;

/* Developing ATG Applications */
/* Sample Form Handler: SendMessageHandler */

import atg.droplet.*;
import atg.service.email.*;
import atg.repository.*;
import atg.servlet.*;

import java.io.Serializable;


public class SendMessageHandler extends GenericFormHandler implements Serializable {

   String mFromUserid;
   String mToUserid;
   Repository mUserRepository;
   EmailSender mEmailSender;
   String mMessage;
   String mSubject;
   String mSuccessURL;
   String mErrorURL;
   private static final String SEND_MESSAGE_HANDLER_CALLED_FROM = "send message handler called with from= ";
   private static final String NO_EMAIL_SENDER_SET = "no email sender set";
   private static final String ERROR_RETRIEVING_USER_EMAIL_ADDRESS = "error retrieving user's email address";
   private static final String USER_HAS_NOT_SUPPLIED_AN_EMAIL_ADDRESS = "User has not supplied an email address";
   private static final String INVALID_USER_ID = "invalid user id";
   private static final String SUBJECT_AND_MESSAGE_BOTH_NULL = "subject and message both null";
   private static final String SUBJECT_AND_MESSAGE_CANT_BOTH = "Subject and message can't both be empty";
   
   
   public String getToUserid() { return mToUserid; }
   public void setToUserid(String pUserid) { mToUserid = pUserid; }

   public String getFromUserid() { return mFromUserid; }
   public void setFromUserid(String pUserid) { mFromUserid = pUserid; }
   
   public Repository getUserRepository() { return mUserRepository; }
   public void setUserRepository(Repository pRepository) { mUserRepository = pRepository; }
   
   public EmailSender getEmailSender() { return mEmailSender; }
   public void setEmailSender(EmailSender pEmailSender) { mEmailSender = pEmailSender; }

   public String getMessage() { return mMessage; }
   public void setMessage(String pMessage) { mMessage = pMessage; }

   public String getSubject() { return mSubject; }
   public void setSubject(String pSubject) { mSubject = pSubject; }

   public String getSuccessURL() { return mSuccessURL; }
   public void setSuccessURL(String pSuccessURL) { mSuccessURL = pSuccessURL; }

   public String getErrorURL() { return mErrorURL; }
   public void setErrorURL(String pErrorURL) { mErrorURL = pErrorURL; }
   
   /* handler for the send property */
   public boolean handleSend(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) 
                              throws java.io.IOException, javax.servlet.ServletException {
                              
       if (isLoggingDebug()) 
           logDebug( SEND_MESSAGE_HANDLER_CALLED_FROM + getFromUserid() + ", to=" + getToUserid());
           
       RepositoryItem touser=null;
       EmailSender es = getEmailSender();
       
       if (es == null) 
           throw new DropletException(NO_EMAIL_SENDER_SET);
       else {       
            try {
                touser = getUserRepository().getItem(getToUserid(),"user");
            }
            catch (RepositoryException e) {
                if (isLoggingError()) logError(ERROR_RETRIEVING_USER_EMAIL_ADDRESS,e);
                addFormException(new DropletException(USER_HAS_NOT_SUPPLIED_AN_EMAIL_ADDRESS));
 	        if (getErrorURL() != null) {
 	              pResponse.sendLocalRedirect(getErrorURL(), pRequest);
	              return false;
                }
            }       
            
            if (touser == null) {
               throw new DropletException(INVALID_USER_ID);
            }


            String toEmailAddr = (String)touser.getPropertyValue("email");
            String fromEmailAddr = "user" + getFromUserid() + "@dynamusic.com";
     
            String subject = getSubject();
            String message = getMessage();


            if (subject.equals("") && message.equals("")) {
                if (isLoggingDebug())
                    logDebug(SUBJECT_AND_MESSAGE_BOTH_NULL);
                addFormException(new DropletException(SUBJECT_AND_MESSAGE_CANT_BOTH));
      	        if (getErrorURL() != null) {
 	              pResponse.sendLocalRedirect(getErrorURL(), pRequest);
	              return false;
                }
                return true;
            }
            
            if (isLoggingDebug()) 
                logDebug("sending message from " + fromEmailAddr + " to " + toEmailAddr +
                	"with subject " + subject + " and body " + message);
                	
            try {
                es.sendEmailMessage(fromEmailAddr, toEmailAddr, subject, message);
            }
            catch (EmailException e) {
               if (isLoggingError())
                   logError(e);
               if (getErrorURL() != null) {
      	          pResponse.sendLocalRedirect(getErrorURL(), pRequest);
	              return false;
	         }
              throw new DropletException ("Email to " + toEmailAddr + " failed",e);
            }   
         }
         if (getSuccessURL() != null) {
       	     pResponse.sendLocalRedirect(getSuccessURL(), pRequest);
	     return false;
	 }
         return true;
   }
}