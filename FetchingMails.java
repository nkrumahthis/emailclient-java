import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

/**
 * FetchingMails
 */
public class FetchingMails {
    public static void fetch(String pop3Host, String storeType, String user, String password) {
        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.pop3.host", pop3Host);
            properties.put("mail.pop3.port", 995);
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            // emailSession.setDebut(true);

            // create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");
            store.connect(pop3Host, user, password);

            // create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                System.out.println("------------");
                writePart(message);

                // TODO continue
            }

            // create the folder object and open it
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void writePart(Part part) throws Exception {
        if (part instanceof Message) {
            writeEnvelope((Message) part);
        }

        System.out.println("------------------------");
        System.out.println("CONTENT-TYPE: " + part.getContentType());

        // Check if the content is plain text
        if (part.isMimeType("text/plain")) {
            System.out.println("This is plain text");
            System.out.println("---------------------");
            System.out.println((String) part.getContentType());
        }

        // Check if the content has attachment
        else if (part.isMimeType("multipart/*")) {
            System.out.println("This is a Multipart");
            System.out.println("--------------------");
            Multipart multipart = (Multipart) part.getcontent();
            int count = multipart.getCount();
            for (int i = 0; i < count; i++) {
                writePart(multipart.getBodyPart(i));
            }
        }

        // Check if the content is a nested message
        else if (part.isMimeType("message/rfc822")) {
            System.out.println("This is a Nested Message");
            System.out.println("-------------------------");
            writePart((Part) part.getContent());
        }

        // check if the content is an inline image
        else if (part.isMimeType("image/jpeg")) {
            // image jpeg

        }
    }

    public static void writeEnvelope(Message message) throws Exception {
        System.out.println("This is the message envelope");
        System.out.println("-----------------------------");
        Address[] addresses;

        // FROM
        if ((addresses = message.getFrom()) != null) {
            for (int j = 0; j < addresses.length; j++) {
                System.out.println("FROM: " + addresses[j].toString());
            }
        }

        // TO
        if ((addresses = message.getRecipients(Message.RecipientType.TO)) != null) {
            for (int j = 0; j < addresses.length; j++) {
                System.out.println("TO: " + addresses[j].toString());
            }
        }

        // SUBJECT
        if (message.getSubject() != null) {
            System.out.println("SUBJECT: " + message.getSubject());
        }
    }

}