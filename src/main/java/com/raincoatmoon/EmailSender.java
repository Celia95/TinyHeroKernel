package com.raincoatmoon;

import com.raincoatmoon.Core.ExtendedFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class EmailSender {

    private static void sendEmailWithAttachments(String host, String port,
                                                final String userName, final String password, String from, String toAddress,
                                                String subject, String message, ExtendedFile attach) throws MessagingException {
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(from));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        if (attach != null) {
            MimeBodyPart attachPart = new MimeBodyPart();
            try {
                attachPart.attachFile(attach.getInternalPath());
                attachPart.setFileName(attach.getFileName());
                multipart.addBodyPart(attachPart);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // sets the multi-part as e-mail's content
        msg.setContent(multipart);

        // sends the e-mail
        Transport.send(msg);

    }

    public static void sendMail(String from, String to, String subject, String text, ExtendedFile attach) {
        // SMTP info
        String host = EnvVariables.SMTP_HOST;
        String port = EnvVariables.SMTP_PORT;
        String sender = EnvVariables.EMAIL_SENDER;
        String password = EnvVariables.EMAIL_PASS;

        try {
            sendEmailWithAttachments(host, port, sender, password, from, to,
                    subject, text, attach);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}