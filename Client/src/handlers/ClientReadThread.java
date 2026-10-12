package handlers;

import client.Client;
import data.ActionsEnum;
import data.ClientData;
import frames.SaveAsForm;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import messages.Message;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * This class makes a thread that listens all incoming messages from the server and is doing appropriate methods depending on type of message
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html">Thread</a>
 */
public class ClientReadThread extends Thread {

    private ObjectInputStream in;
    private Socket socket;
    
    private static boolean doModify = false;
    
    /**
     * This constructor is setting the socked and input stream to listen the server messages
     * @param s socket on which client and server are connected
     */
    public ClientReadThread(Socket s) {
        socket = s;
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.err.println("Error initializing input stream: " + ex.getMessage());
        }
    }
    
    /**
     * This method is starting the thread - <b>listening the server socket</b>.
     * It also does appropriate methods depending on received message
     * @throws EOFException it is invoking during disconnecting
     * @throws IOException handle possibility of error during sending message (incorrect stream)
     * @throws ClassNotFoundException handle possibility of error occurring when the class to which the object has to be converted is not found.
     * @throws Exception handle possibility of any other error
     */
    @Override
    public void run() {
        try {

            while (true) {
                Object obj = in.readObject();
                if (obj == null) {
                    break;
                }
                Message receivedMessage = (Message) obj;
                if (receivedMessage.getAction() == ActionsEnum.End) {
                    break;
                }

                switch (receivedMessage.getAction()) {
                    case Loggin -> {
                        CommandsHandlers.recieveLogin(receivedMessage);
                    }
                    case GetStructure -> {
                        if(!Client.isLogged()) {
                            break;
                        }
                        CommandsHandlers.recieveStructure(receivedMessage);
                        Client.getMainFrame().updateSrcLabel();
                        
                    }
                    case OpenFile -> {
                        if (!receivedMessage.getMessage().equals("EOF")) {
                            try {
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = in.read(buffer)) != -1) {
                                    String data = new String(buffer, 0, bytesRead);
                                    if(doModify) {
                                        Client.getMainFrame().appendModifyDialogConentArea(data);
                                    }
                                    else {
                                        Client.getMainFrame().appendReadDialogConentArea(data);
                                    }
                                }
                            } catch (IOException ex) {
                                System.err.println("File saving error!: " + ex);
                            }
                        } else {
                            // Receiving additional mesege ("EOF") to stop, because the stream wasn't ending (Only after doing other operation)
                            break;
                        }
                    }
                    case DownloadFile -> {
                        if (!receivedMessage.getMessage().equals("EOF")) {

                            new SaveAsForm(receivedMessage.getMessage());

                            String location = receivedMessage.getMessage();
                            if (!ClientData.getDownloadLocation().isBlank()) {
                                if (ClientData.getDownloadLocation().contains(".") && receivedMessage.getMessage().contains(".")) {
                                    location = ClientData.getDownloadLocation();
                                } else if (receivedMessage.getMessage().contains(".") && !ClientData.getDownloadLocation().contains(".")) {
                                    location = pathWithoutLastElement(ClientData.getDownloadLocation()) + receivedMessage.getMessage();
                                }
                            }

                            try (FileOutputStream fileOutputStream = new FileOutputStream(location)) {
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = in.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, bytesRead);
                                }
                            } catch (IOException ex) {
                                System.err.println("File saving error!: " + ex);
                            }
                        } else {
                            // Receiving additional mesege ("EOF") to stop, because the stream wasn't ending (Only after doing other operation)
                            break;
                        }
                    }
                    case RemoveFile, RemoveFolder -> {
                        CommandsHandlers.askForStructure(ClientData.getSrc());
                    }
                    case ModifyFile -> {
                        
                    }
                    case Info -> {
                        System.out.println("[INFO] " + receivedMessage.getMessage());
                    }

                    default ->
                        System.err.println("There isn't such operation!");
                }
            }

            if (in != null) { // Close input stream if it's not null
                in.close();
            }

        } catch (EOFException ex) {
            System.out.println("Disconnected");
        } catch (IOException ex) {
            System.err.println("IOExeption | in ClientReadThread: " + ex);
            Client.showsThatConnectionBroken();
        } catch (ClassNotFoundException ex) {
            System.err.println("ClassNotFound | in ClientReadThread: " + ex);
        } catch (Exception ex) {
            System.err.println("ERROR | in ClientReadThread: " + ex);
        }
    }

    private String pathWithoutLastElement(String s) {
        String arr[] = s.split(Pattern.quote(File.separator));
        String temp = "";
        for (int i = 0; i < arr.length - 2; i++) {
            temp += arr[i];
            temp += File.separator;
        }
        return temp;
    }
    
    
    public static void setDoModify(boolean value) {
        doModify = value;
    }
}
