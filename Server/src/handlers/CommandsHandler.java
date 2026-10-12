package handlers;

import data.*;
import files.FileParser;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import messages.Message;
import server.Server;
import utils.User;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * This class contains methods used to communicate with the client and that are invoking when client sends messages.
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/io/ObjectOutputStream.html">ObjectOutputStream</a>
 */
public class CommandsHandler {

    private ObjectOutputStream out;
    private AtomicReference<String> clientIpAndPort;
    
    /**
     * This constructor is assigning appropriate output stream with the handler and assign reference to the client ip and port string. <br />
     * Reference will be used to print client's data (making logs).
     * @param out otput stream to communicate with the client
     * @param clientIpAndPort reference to the client's ip and port.
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicReference.html">AtomicReference</a> - passing values of any types as references.
     */
    public CommandsHandler(ObjectOutputStream out, AtomicReference<String> clientIpAndPort) {
        this.out = out;
        this.clientIpAndPort = clientIpAndPort;
    }
    /**
     * This method is getting the object of the message's content and checking if there is such an account.<br />
     * It also sends a response to a login attempt.
     * @param obj it gets an object of the message from the client - it should be instance of "User" class.
     * @throws IOException handle possibility of error during sending message (incorrect stream)
     */
    public void loggin(Object obj) {
        if (obj instanceof User) {
            User user = (User) obj;

            User returnal = DataAdministration.getUser(user.getLogin(), user.getPassword());
            try {
                if (returnal != null) {
                    out.writeObject(new Message(ActionsEnum.Loggin, "TRUE", returnal));
                    clientIpAndPort.set(clientIpAndPort.get().split("]")[0] + " - as: " + user.getLogin() + "]");
                    Server.getServerFrame().updateTerminal("Client logged in! <- " + clientIpAndPort);
                } else {
                    Server.getServerFrame().updateTerminal("Invalid attempt to log in! <- " + clientIpAndPort);
                    out.writeObject(new Message(ActionsEnum.Loggin, "FALSE"));
                }
            } catch (IOException ex) {
                System.err.println("Cannot send user!");
                Server.getServerFrame().updateTerminal("Cannot send user!");
            }
        } else {
            System.err.println("There was sent not an USER !!!");
            Server.getServerFrame().updateTerminal("There was sent not an USER !!!");
        }
    }
    
    /**
     * This method is getting the structure of given directory and sends it as list of names.
     * @param path the path that the user wants to get the structure of.
     * @see files.FileParser#getStructure(java.lang.String) <b>FileParser.getStructure(String path)</b> - getting the directory content as a List of strings.
     * @throws IOException handle possibility of error during sending message (incorrect stream)
     */
    public void sendStructure(String path) {
        try {
            List<String> structure = FileParser.getStructure(path);
            out.writeObject(new Message(ActionsEnum.GetStructure, path, structure));
        } catch (IOException ex) {
            System.err.println("Cannot send structure!");
            Server.getServerFrame().updateTerminal("Cannot send structure!");
        }
    }
    
    /**
     * This method is sending the file to the user.<br />
     * There is used <b>buffor</b> to optimalize processing data between input(FileInputStream) and output stream (ObjectOutputStream).
     * @param path the path to the file that user wants to get
     * @param action decides if user wants the file to open or download
     * @throws IOException handle possibility of error during sending message (incorrect stream)
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/io/FileInputStream.html">FileInputStream</a>
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/io/ObjectOutputStream.html">ObjectOutputStream</a>
     */
    public void sendFile(String path, ActionsEnum action) {
        try {
            String finalLocation = FileParser.getRESOURCES_LOCATION() + File.separator + path;
            
            File file = new File(finalLocation);
            
            if (file.exists()) {
                Server.getServerFrame().updateTerminal("[Sending file] Path: " + finalLocation);

                out.writeObject(new Message(action, file.getName()));

                FileInputStream fileInputStream = new FileInputStream(file);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                // Sending additional mesege, because the stream wasn't ending (Only after doing other operation)
                out.writeObject(new Message(action, "EOF"));
                fileInputStream.close();

            } else {
                System.err.println("File doesn't exist!");
                Server.getServerFrame().updateTerminal("File doesn't exist!");
            }
        } catch (IOException ex) {
            System.err.println("Cannot send File to download!");
            Server.getServerFrame().updateTerminal("Cannot send File to download!");
        }
    }
    
    /**
     * This method is creating a file or a directory in desired path.
     * @param path path where the file/directory should be created.
     * @param isFile decides whether to create a file or a directory.
     * @throws IOException handle possibility of error during sending message (incorrect stream)
     */
    public void createFileOrFolder(String path, boolean isFile) {
        try {
            String finalLocation = FileParser.getRESOURCES_LOCATION() + File.separator + path;

            File file = new File(finalLocation);

            if (isFile) {
                if (file.createNewFile()) {
                    Server.getServerFrame().updateTerminal("File has been created");
                    out.writeObject(new Message(ActionsEnum.Info, "Created successfully"));
                } else {
                    Server.getServerFrame().updateTerminal("File with given name Exist!");
                }
            } else {
                if (file.mkdir()) {
                    Server.getServerFrame().updateTerminal("Folder has been created");
                    out.writeObject(new Message(ActionsEnum.Info, "Created successfully"));
                } else {
                    Server.getServerFrame().updateTerminal("Folder with given name Exist!");
                }
            }

        } catch (IOException ex) {
            System.err.println("Cannot create file!");
            Server.getServerFrame().updateTerminal("Cannot create file!");
        }
    }
    /**
     * This method is removing file in desired path.
     * @param path the path to the file to be remove.
     * @throws IOException handle possibility of error during sending message (incorrect stream)
     */
    public void removeFile(String path) {
        try {
            String finalLocation = FileParser.getRESOURCES_LOCATION() + File.separator + path;

            File file = new File(finalLocation);

            if (file.delete()) {
                Server.getServerFrame().updateTerminal("File has been removed");
                out.writeObject(new Message(ActionsEnum.RemoveFile, "File removed successfully"));
            } else {
                Server.getServerFrame().updateTerminal("Cannot remove file");
            }
        } catch (IOException ex) {
            System.err.println("Cannot remove file!");
            Server.getServerFrame().updateTerminal("Cannot remove file!");
        }
    }
    
    /**
     * This method is removing directory in desired path.<br />
     * It deletes the directory and its <b>entire structure</b>.
     * @param path the path to the directory to be remove.
     * @throws IOException handle possibility of error during sending message (incorrect stream)
     */
    public void removeFolder(String path) {
        try {
            String finalLocation = FileParser.getRESOURCES_LOCATION() + File.separator + path;

            File folder = new File(finalLocation);
            removeFolderAndStructure(folder);
            
            out.writeObject(new Message(ActionsEnum.RemoveFolder, "Folder removed successfully"));
        } catch (IOException ex) {
            System.err.println("Cannot remove Folder!");
            Server.getServerFrame().updateTerminal("Cannot remove Folder!");
        }
    }
    
    private void removeFolderAndStructure(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        removeFolderAndStructure(file);
                    } else {
                        file.delete();
                    }
                }
            }
            folder.delete();
            Server.getServerFrame().updateTerminal("Folder has been removed!");
        } else {
            Server.getServerFrame().updateTerminal("Cannot remove Folder! - it doesn't exist!");
        }
    }
    
    /**
     * This method is modifying the file with new content.
     * @param message the message that the client has sent - it contains the location and the content of file that has to be applied.
     * @see files.FileParser#parseDataToFile(java.lang.String, java.lang.String) <b>FileParser.parseDataToFile(String path, String contentData)</b> - it modifies the file with given content.
     * @throws IOException handle possibility of error during sending message (incorrect stream)
     */
    public void modifyFile(Message message) {
        try {
            String finalLocation = FileParser.getRESOURCES_LOCATION() + File.separator + message.getMessage();
            
            if(message.getStringContent() != null) {
                FileParser.parseDataToFile(finalLocation, message.getStringContent());
                out.writeObject(new Message(ActionsEnum.Info, "File has been modified!"));
                Server.getServerFrame().updateTerminal("[Modifying file] -- " + finalLocation + " -- has been modified by <- " + clientIpAndPort);
            }
        } catch (IOException ex) {
            System.err.println("Cannot modify Folder!");
            Server.getServerFrame().updateTerminal("Cannot modify Folder!");
        }
    }
}
