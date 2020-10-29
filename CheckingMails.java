import java.io.FileInputStream;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Folder;
import javax.mail.Store;
import javax.mail.Message;

/**
 * CheckingMails
 */
public class CheckingMails {

    private static void check(String host, String storeType, String user, String password) {
        try {
            // create properties field and create a session

            Properties properties = new Properties();
            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);

            // create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");
            store.connect(host, user, password);

            // Create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length ---- " + messages.length);

            // print out all messages
            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                System.out.println("-----------------------------------");
                System.out.println("Email Number: " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());
            }

            // close the store and folder objects
            emailFolder.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // read config file
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
        String mailStoreType = prop.getProperty("mailStoreType");
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        check(host, mailStoreType, username, password);
    }

}
