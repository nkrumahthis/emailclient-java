import java.io.FileInputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

/**
 * SendMail
 */

public class SendMail {

    public static void main(String[] args) {

        FileInputStream stream = null;
        Properties prop = new Properties();
        String filename = ".config";
        try {
            stream = new FileInputStream(filename);
            prop.load(stream);
            stream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        String host = prop.getProperty("host");
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");

        String to = JOptionPane.showInputDialog(null, "to:");
        String from = "nkrumah@thescienceset.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "25");

        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        Session session = Session.getInstance(props, authenticator);

        try {
            // create a default mimemessage object
            Message message = new MimeMessage(session);

            // set from: header field of the header
            message.setFrom(new InternetAddress(from));

            // Set to: header field of the header
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            // set subject: header field
            String subject = JOptionPane.showInputDialog(null, "subject");
            message.setSubject(subject);

            // now set the actual message
            String text = JOptionPane.showInputDialog(null, "email");
            message.setText(text);

            // send message
            Transport.send(message);

            System.out.println("Sent message successfully ...");
            JOptionPane.showMessageDialog(null, "message sent successfully");
        } catch (MessagingException e) {
            throw new RuntimeException(e);

        }

    }
}