package org.ofbiz.core.util;


import java.util.Date;
import java.net.*;
import java.io.*;

/**
 * <p><b>Title:</b> SendMailSMTP.java
 * <p><b>Description:</b> Sends Email via SMTP..
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
 * <p>Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author David Jones
 * @version 1.0
 * Created on August 23, 2001, 7:58 PM
 */
public class SendMailSMTP
{
  private String destinationSMTPServer = null;
  private String localMachine = null;
  private String senderName = null;
  private String recipientTO = null;
  private String recipientCC = null;
  private String recipientBCC = null;
  private String subject = null;
  private String message = null;
  private String extraHeader = null;

  private BufferedReader in;
  private BufferedWriter out;

  // class constants
  public static final int SMTP_PORT = 25;

  public SendMailSMTP()
  {
  }

  public SendMailSMTP(String sender)
  {
    setSender(sender);
  }

  public SendMailSMTP(String sender, String recipientTO, String message)
  {
    setSender(sender);
    setRecipientTO(recipientTO);
    setMessage(message);
  }

  public SendMailSMTP(String destinationSMTPServer, String sender, String recipientTO, String message)
  {
    setSender(sender);
    setRecipientTO(recipientTO);
    setMessage(message);
    setDestinationSMTPServer(destinationSMTPServer);
  }

  public SendMailSMTP(String destinationSMTPServer, String localMachine, String sender, String recipientTO, String recipientCC, String recipientBCC, String subject, String message)
  {
    setSender(sender);
    setRecipientTO(recipientTO);
    setRecipientCC(recipientCC);
    setRecipientBCC(recipientBCC);
    setSubject(subject);
    setMessage(message);
    setLocalMachine(localMachine);
    setDestinationSMTPServer(destinationSMTPServer);
  }

  public void setSender(String sender)
  {
      int indexOfAtSign = sender.indexOf('@');
      if(indexOfAtSign < 0)
      {
        throw new RuntimeException("Malformed sender address. Need full user@host format");
      }
      this.senderName = sender; //.substring(0, indexOfAtSign);
      this.localMachine = sender.substring(indexOfAtSign + 1);
  }

  public void setLocalMachine(String localMachine)
  {
      this.localMachine = localMachine;
  }

  public void setRecipientTO(String recipientTO)
  {
      this.recipientTO = recipientTO;
  }

  public void setRecipientCC(String recipientCC)
  {
      this.recipientCC = recipientCC;
  }

  public void setRecipientBCC(String recipientBCC)
  {
      this.recipientBCC = recipientBCC;
  }

  public void setDestinationSMTPServer(String destinationSMTPServer)
  {
      this.destinationSMTPServer = destinationSMTPServer;
  }

  public void setSubject(String subject)
  {
    this.subject = subject;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public void setExtraHeader(String extraHeader)
  {
    this.extraHeader = extraHeader;
  }

  public void send() throws IOException
  {
    if(destinationSMTPServer == null) throw new RuntimeException("No destinationSMTPServer specified!");
    if(localMachine == null) throw new RuntimeException("No localMachine specified!");
    if(senderName == null) throw new RuntimeException("No senderName specified!");
    if(message == null) throw new RuntimeException("No message specified!");

    // attempt to make the connection, this might throw an exception
    Socket s = new Socket(destinationSMTPServer, SMTP_PORT);
    in = new BufferedReader(new InputStreamReader(s.getInputStream(),"8859_1"));
    out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(),"8859_1"));
    String response;
    String header = new String();

    if(subject != null)
    {
        header = "Subject: " + subject + "\n";
    }

    // discard signon message, introduce ourselves, and discard reply
    response = hear(true);
    say("HELO " + localMachine + "\n");
    response = hear(true);

    header = header + "From: " + senderName + "\n";
    say("MAIL FROM: " + senderName + "\n");
    response = hear(true);

    if(recipientTO != null)
    {
        header = header + "To: " + recipientTO + "\n";

        int indexofcomma = recipientTO.indexOf(',');
        while(indexofcomma > 0)
        {
            //send, then remove, one email address at a time...
            say("RCPT TO: " + recipientTO.substring(0, indexofcomma) + "\n");
            response = hear(false);

            recipientTO = recipientTO.substring(indexofcomma + 1);
            indexofcomma = recipientTO.indexOf(',');
        }

        //no more commas, send the last email address
        say("RCPT TO: " + recipientTO + "\n");
        response = hear(false);
    }
    if(recipientCC != null)
    {
        header = header + "Cc: " + recipientCC + "\n";

        int indexofcomma = recipientCC.indexOf(',');
        while(indexofcomma > 0)
        {
            //send, then remove, one email address at a time...
            say("RCPT TO: " + recipientCC.substring(0, indexofcomma) + "\n");
            response = hear(false);

            recipientCC = recipientCC.substring(indexofcomma + 1);
            indexofcomma = recipientCC.indexOf(',');
        }

        //no more commas, send the last email address
        say("RCPT TO: " + recipientCC + "\n");
        response = hear(false);
    }
    if(recipientBCC != null)
    {
        int indexofcomma = recipientBCC.indexOf(',');
        while(indexofcomma > 0)
        {
            //send, then remove, one email address at a time...
            say("RCPT TO: " + recipientBCC.substring(0, indexofcomma) + "\n");
            response = hear(false);

            recipientBCC = recipientBCC.substring(indexofcomma + 1);
            indexofcomma = recipientBCC.indexOf(',');
        }

        //no more commas, send the last email address
        say("RCPT TO: " + recipientBCC + "\n");
        response = hear(false);
    }

    //add the date to the header...
    Date today = new Date();
    header = header + "Date: " + today.toString() + "\n";

    //add in the extra header string...
    if(extraHeader != null) header = header + extraHeader;

    say("DATA\n");
    response = hear(true);

    say(header + "\n\n" + message + "\n.\n");
    response = hear(true);
    say("QUIT\n");

    // now close down the connection..
    s.close();
  }

  private void say(String toSay) throws IOException
  {
    out.write(toSay);
    out.flush();
    //Debug.log("SendMailSMTP: " + toSay);
  }

  private String hear(boolean stop) throws IOException
  {
    String inString = in.readLine();
    if ("23".indexOf(inString.charAt(0)) < 0)
    {
      if(stop) throw new IOException("SMTP problem: " + inString);
      else System.out.println("SMTP problem: " + inString);
    }
    Debug.log("SendMailSMTP: " + inString);
    return inString;
  }

  //public static void main(String args[]) throws Exception {
  //  BufferedReader in = new BufferedReader(
  //                             new InputStreamReader(System.in));
  //  System.out.print("Your address: ");
  //  System.out.flush();
  //  String sender = in.readLine();
  //  System.out.print("Recipient address: ");
  //  System.out.flush();
  //  String recipient = in.readLine();
  //  String message = "";
  //  String part;
  //  System.out.println("Message, end with '.' by itself:");
  //  for (;;) {
  //    part = in.readLine();
  //    if ((part == null) || part.equals(".")) {
  //      break;
  //    }
  //    message += part + "\n";
  //  }
  //  SMTP mailer = new SMTP(sender, recipient, message);
  //  mailer.send();
  //}
}
