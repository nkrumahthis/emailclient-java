import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchProviderException;
import java.util.Properties;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

/**
 * FetchingMails
 */
public class FetchingMails {

    private FetchingMails() {
    }

    public static void main(String[] args) {
        String host = "pop.gmail.com"; // change accordingly
        String mailStoreType = "pop3";
        String username = "nkrumah@thescienceset.com"; // change accordingly
        String password = "Nkrumah@D3xt"; // change accordingly
        fetch(host, mailStoreType, username, password);
    }

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
                String line = reader.readLine();
                if ("YES".equals(line)) {
                    message.writeTo(System.out);
                } else if ("QUIT".equals(line)) {
                    break;
                }

                emailFolder.close(false);
                store.close();
            }

        } catch (NoSuchProviderException | MessagingException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * This method checks for content-type based on which, it processes and fetches
     * the content of the messge
     */

    public static void writePart(Part part) throws Exception {
        if (part instanceof Message) {
            writeEnvelope((Message) part);
        }

        System.out.println("------------------------");
        System.out.println("CONTENT-TYPE: " + part.getContentType());

        // Check if the content is plain text
        if (part.isMimeType("text/plain")) {
            handlePlainText(part);
        }

        // Check if the content has attachment
        else if (part.isMimeType("multipart/*")) {
            handleMultipart(part);
        }

        // Check if the content is a nested message
        else if (part.isMimeType("message/rfc822")) {
            handleNestedMessage(part);
        }

        // check if the content is an inline image
        else if (part.isMimeType("image/jpeg")) {
            handleJpeg(part);
        }

        else if (part.getContentType().contains("image/")) {
            handleContainsImage(part);
        }

        else {
            // TODO continue
            Object object = part.getContent();
            if (object instanceof String) {
                System.out.println("This is a string");
                System.out.println("-------------------------");
                System.out.println((String) object);
            } else if (object instanceof InputStream) {
                System.out.println("This is just an input stream");
                System.out.println("-------------------------------");
                InputStream inputStream = (InputStream) object;
                inputStream = (InputStream) object;
                int c;
                while ((c = inputStream.read()) != -1) {
                    System.out.write(c);
                }
            } else {
                System.out.println("This is an unknown type");
                System.out.println("-----------------------");
                System.out.println(object.toString());
            }
        }
    }

    private static void handlePlainText(Part part) throws Exception {
        System.out.println("This is plain text");
        System.out.println("---------------------");
        System.out.println((String) part.getContentType());
    }

    private static void handleMultipart(Part part) throws Exception {
        System.out.println("This is a Multipart");
        System.out.println("--------------------");
        Multipart multipart = (Multipart) part.getContent();
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            writePart(multipart.getBodyPart(i));
        }
    }

    private static void handleNestedMessage(Part part) throws Exception {
        System.out.println("This is a Nested Message");
        System.out.println("-------------------------");
        writePart((Part) part.getContent());
    }

    private static void handleJpeg(Part part) throws Exception {
        // image jpeg
        System.out.println("-------------> image/jpeg");
        Object object = part.getContent();

        InputStream inputStream = (InputStream) object;
        System.out.println("inputstream.length = " + inputStream.available());

        // construct the required byte array

        int i = 0;
        byte[] byteArray = new byte[inputStream.available()];
        while ((i = (int) ((InputStream) inputStream).available()) > 0) {
            int result = (int) (((InputStream) inputStream).read(byteArray));
            if (result == -1) {
                break;
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream("/tmp/image.jpg");
        fileOutputStream.write(byteArray);
        fileOutputStream.close();
    }

    private static void handleContainsImage(Part part) throws Exception {
        System.out.println("content type" + part.getContentType());
        File file = new File("image " + new Date().getTime() + ".jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
        com.sun.mail.util.BASE64DecoderStream test = (com.sun.mail.util.BASE64DecoderStream) part.getContent();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = test.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }

        dataOutputStream.close();
        bufferedOutputStream.close();
        fileOutputStream.close();

    }

    // This method would print FROM, TO and SUBJECT of the message

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