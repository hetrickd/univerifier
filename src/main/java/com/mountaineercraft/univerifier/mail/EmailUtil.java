package com.mountaineercraft.univerifier.mail;

import com.mountaineercraft.univerifier.Univerifier;
import org.bukkit.entity.Player;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    public static void sendVerificationEmail(String token, String to, Player player) {
        // Set email properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", Univerifier.smtpHost);
        properties.put("mail.smtp.port", Univerifier.smtpPort);

        Session session = Session.getInstance(properties);

        // Send email
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(Univerifier.fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Verify your email address");
            message.setText("Click the link below to verify your email address:\n\n" +
                    "http://" + Univerifier.domain + ":25500" + "/verify?token=" + token + "&player=" + player.getName());

            Transport transport = session.getTransport();

            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
