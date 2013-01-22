package Model;

import peopleObjects.Member;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.naming.InitialContext;
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

public abstract class SendEmail {

    public static void sendEmail(Member user, String subject, String messBody) throws Exception {
        InitialContext ctx = new InitialContext();
        Session session =
                (Session) ctx.lookup("mail/no-reply");
        // Or by injection.  
        //@Resource(name = "mail/<name>")  
        //private Session session;  

        Message msg = new MimeMessage(session);
        msg.setSubject(subject);

        msg.setRecipient(RecipientType.TO,
                new InternetAddress(user.getEmail(), user.getFirst_name()));
        msg.setFrom(new InternetAddress(
                "noreply@peso.org",
                "no-reply"));

        // Body text.  
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(messBody);

        // Multipart message.  
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Add multipart message to email.  
        msg.setContent(multipart);

        // Send email.  
        Transport.send(msg);
    }
}
