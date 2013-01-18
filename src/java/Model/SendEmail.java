package Model;

import dataObjects.Message;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import peopleObjects.Member;

/*
 * User Authentication Part:
 *
 *   If it is required to provide user ID and Password to
 * the email server for authentication purpose then you can
 * set these properties as follows:
 *
 * props.setProperty("mail.user", "myuser");
 * props.setProperty("mail.password", "mypwd");
 *
 * */
public abstract class SendEmail
{
   // Recipient's email ID needs to be mentioned.
   public static void sendEmail(String to, Message m)
   {
      //Can all be set in constructor later if needed
      // Sender's email ID needs to be mentioned
      String from = "noreply@peso.org";
      // Assuming you are sending email from localhost
      String host = "localhost";

      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty("mail.smtp.host", host);

      // Get the default Session object.
      Session session = Session.getDefaultInstance(properties);

      try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(MimeMessage.RecipientType.TO,
                                  new InternetAddress(to));

         // Set Subject: header field
         message.setSubject(m.getTitle());

         // Now set the actual message

         message.setText("You received that message: \n"+ m.getTitle() + "\n You should read it");

         // Send message
         Transport.send(message);
         System.out.println("Sent message successfully....");
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
   
   // Envoie un message de bienvenue à un nouvel utilisateur.
   public static void welcomeEmail(Member user)
   {
      //Can all be set in constructor later if needed
      // Sender's email ID needs to be mentioned
      String from = "noreply@peso.org";
      // Assuming you are sending email from localhost
      String host = "localhost";

      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty("mail.smtp.host", host);

      // Get the default Session object.
      Session session = Session.getDefaultInstance(properties);

      try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(MimeMessage.RecipientType.TO,
                                  new InternetAddress(user.getEmail()));

         // Set Subject: header field
         message.setSubject("Votre compte a été créé");

         // Now set the actual message

         message.setText("Bonjour "+user.getFirst_name()+",\n\n"
                 + "Votre compte utilisateur sur le système PESO a bien été créé.\n"
                 + "Votre ID : \n"+user.getId_member() + "\n\n"
                 + "Ce message a été envoyé automatiquement, merci de ne pas répondre.");

         // Send message
         Transport.send(message);
         System.out.println("Message sent successfully....");
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
}
