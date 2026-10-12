package handlers;

import java.io.*;
import java.net.Socket;
import messages.Message;
import data.ActionsEnum;
import files.FileParser;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicReference;
import server.Server;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * This class makes a thread that listens all incoming messages from the client, does desired method and sends appropriate message back.
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html">Thread</a>
 */
public class ClientHandler extends Thread {

    private Socket socket;
    private AtomicReference<String> clientIpAndPort;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private CommandsHandler commands;
    
    /**
     * This constructor is setting the socked and streams for communication with client.
     * @param s socket on which client and server are connected
     * @throws IOException handle possibility of error during sending message (incorrect stream)
     */
    public ClientHandler(Socket s) {
        socket = s;
        clientIpAndPort = new AtomicReference<>("[" + socket.getInetAddress() + ":" + socket.getPort() + "]");
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            commands = new CommandsHandler(out, clientIpAndPort);
        } catch (IOException e) {
            System.err.println("Error on streams operations: " + e);
            Server.getServerFrame().updateTerminal("Error on streams operations: " + e);
        }
    }
    
    /**
     * This method is starting the thread - <b>listening the server socket</b>.
     * It also does appropriate methods depending on message and sends message back
     * @throws IOException handle possibility of error occuring when the client sends the file.
     * @throws SocketException it is invoking when client disconnect.
     * @throws Exception handle possibility of any other error.
     */
    @Override
    public void run() {
        try {
            while (true) {
                Message message = (Message) in.readObject();

                if (message.getAction() == ActionsEnum.End) {
                    break;
                }

                switch (message.getAction()) {
                    case Loggin -> {
                        if (message.getContent() != null) {
                            commands.loggin(message.getContent());
                            commands.sendStructure("");
                        }
                    }
                    case GetStructure -> {
                        commands.sendStructure(message.getMessage());
                        Server.getServerFrame().updateTerminal("Structure has been send to -> " + clientIpAndPort);
                    }
                    case OpenFile -> {
                        Server.getServerFrame().updateTerminal("File has been send to Open to -> " + clientIpAndPort);
                        commands.sendFile(message.getMessage(), ActionsEnum.OpenFile);
                    }
                    case DownloadFile -> {
                        Server.getServerFrame().updateTerminal("File has been send to Download to -> " + clientIpAndPort);
                        commands.sendFile(message.getMessage(), ActionsEnum.DownloadFile);
                    }
                    case UploadFile -> {
                        if (!message.getMessage().equals("EOF")) {
                            String location = FileParser.getRESOURCES_LOCATION() + File.separator + message.getMessage();
                            
                            Server.getServerFrame().updateTerminal("Getting file from the User " + clientIpAndPort + " to location: " + location);
                            
                            try (FileOutputStream fileOutputStream = new FileOutputStream(location)) {
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = in.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, bytesRead);
                                }
                            } catch (IOException ex) {
                                System.err.println("File saving error!: " + ex);
                                Server.getServerFrame().updateTerminal("File saving error!: " + ex);
                            }
                        } else {
                            // Receiving additional mesege ("EOF") to stop, because the stream wasn't ending (Only after doing other operation)
                            break;
                        }
                    }
                    case CreateFile -> {
                        Server.getServerFrame().updateTerminal("Creating new File by <- " + clientIpAndPort);
                        commands.createFileOrFolder(message.getMessage(), true);
                    }
                    case CreateFolder -> {
                        Server.getServerFrame().updateTerminal("Creating new Folder by <- " + clientIpAndPort);
                        commands.createFileOrFolder(message.getMessage(), false);
                    }
                    case RemoveFile -> {
                        commands.removeFile(message.getMessage());
                    }
                    case RemoveFolder -> {
                        commands.removeFolder(message.getMessage());
                    }
                    case ModifyFile -> {
                        commands.modifyFile(message);
                    }
                    case Info -> {
                        Server.getServerFrame().updateTerminal("Message from client <- " + clientIpAndPort +": " + message.getMessage());
                        out.writeObject(new Message(ActionsEnum.Info, message.getMessage()));
                    }

                    default -> {
                        System.err.println("There isn't such operation! Invoke by <- " + clientIpAndPort);
                        Server.getServerFrame().updateTerminal("There isn't such operation! Invoke by <- " + clientIpAndPort);
                    }
                }

                out.flush();
            }

            Server.getServerFrame().updateTerminal("Client " + clientIpAndPort + " disconnected");
            in.close();
            out.close();
            socket.close();

        } catch (SocketException ex) {
            Server.getServerFrame().updateTerminal("User " + clientIpAndPort + " disconnected!");
            
        } catch (Exception ex) {
            System.err.println("Error at communication loop: " + ex);
            System.out.println("----------------------------------------");
            System.err.println(ex.getMessage());

            Server.getServerFrame().updateTerminal("Error at communication loop: " + ex);
            Server.getServerFrame().updateTerminal("----------------------------------------");
            Server.getServerFrame().updateTerminal(ex.getMessage());
        }
    }
}
