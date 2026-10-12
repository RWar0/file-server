package handlers;

import client.Client;
import data.*;
import java.io.*;
import java.util.List;
import messages.Message;
import utils.User;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * This class contains methods used to communicate with the server and that are invoking when server sends messages.
 * @see client.Client#getObjectOutputStream() <b>stream used for sending object to server</b>
 */
public class CommandsHandlers {

    // Loggin
    
    /**
     * The method sends message with login attempt to the server using stream.
     * @param login user's login
     * @param password user's password
     * @throws java.io.IOException handle possibility of error during sending message (incorrect stream)
     */
    public static void sendLoginAttempt(String login, String password) {
        try {
            Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.Loggin, "ATTEMPT", new User(login, password)));
        } catch (IOException ex) {
            System.err.println("Cannot send object - loggin: " + ex);
        }
    }
    
    /**
     * This method is getting message and checking the answer. If answer is true - it gives an access.
     * @param message received message from the server
     */
    public static void recieveLogin(Message message) {
        if (message.getMessage().equals("TRUE") && message.getContent() != null) {
            Client.loggedIn(message.getUser().getAccess());
            Client.setLogged(true);
        } else {
            Client.getLoginForm().setErrorLabel("Invalid login/password", true);
        }
    }

    // Structure
    
    /**
     * The method sends message to the server using stream with ask to get a structure of the current directory.
     * @param path desired path to get the structure.
     * @throws java.io.IOException handle possibility of error during sending message (incorrect stream)
     */
    public static void askForStructure(String path) {
        try {
            Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.GetStructure, path));
        } catch (IOException ex) {
            System.err.println("Cannot ask for structure: " + ex);
        }
    }
    
    /**
     * This method is getting message with structure of the desired directory and invoking the method to display the structure.
     * @param message received message from the server
     * @see frames.MainFrame#updateTable(java.util.List) <b>updateTable()</b> - method that display the structure
     */
    public static void recieveStructure(Message message) {
        if (message.getStructure() != null) {
            List<String> structure = message.getStructure();
            Client.getMainFrame().updateTable(structure);
        } else {
            System.err.println("Returnal structure is NULL");
        }
    }

    
    // Opening file
    
    /**
     * This method is sending message to server to ask for a file content.
     * @param fileName name of desired file to open
     * @throws java.io.IOException handle possibility of error during sending message (incorrect stream)
     */
    public static void askForOpeningFile(String fileName) {
        String fileAndPath = ClientData.getSrc() + fileName;
        try {
            Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.OpenFile, fileAndPath));
        } catch (IOException ex) {
            System.err.println("Cannot ask for opening file: " + ex);
        }
    }

    
    // Creating new File / Folder
    
    /**
     * This method is requesting server to create a file or directory.
     * @param fileName name of file/directory to create
     * @param isFile decides if it is a file or directory
     * @throws java.io.IOException handle possibility of error during sending message (incorrect stream)
     */
    public static void askForCreatingNewFileOrFolder(String fileName, boolean isFile) {
        String fileAndPath = ClientData.getSrc() + fileName;
        try {
            if (isFile) {
                Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.CreateFile, fileAndPath));
            } else {
                Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.CreateFolder, fileAndPath));
            }
        } catch (IOException ex) {
            System.err.println("Cannot ask for creating file: " + ex);
        }
    }

    // Removing file / folder
    
    /**
     * This method is requesting server to remove file or directory.
     * @param fileName name of file/directory to remove
     * @param isFile decides if it is a file or directory
     * @throws java.io.IOException handle possibility of error during sending message (incorrect stream)
     */
    public static void askForRemovingFileOrFolder(String fileName, boolean isFile) {
        String fileAndPath = ClientData.getSrc() + fileName;
        try {
            if (isFile) {
                Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.RemoveFile, fileAndPath));
            } else {
                Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.RemoveFolder, fileAndPath));
            }
        } catch (IOException ex) {
            System.err.println("Cannot ask for opening file: " + ex);
        }
    }

    // Modyfying file:
    
    /**
     * This method is rending request to server to apply changes of modyfing file
     * @param fileName name of the file being modified
     * @param content content that should be placed into the file
     * @throws java.io.IOException handle possibility of error during sending message (incorrect stream)
     */
    public static void requestFileModifying(String fileName, String content) {
        String fileAndPath = ClientData.getSrc() + fileName;

        try {
            Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.ModifyFile, fileAndPath, content));

        } catch (IOException ex) {
            System.err.println("Cannot ask for opening file: " + ex);
        }
    }

    // Downloading files
    
    /**
     * This method is asking the server to download file
     * @param fileName name of the file to download
     * @throws java.io.IOException handle possibility of error during sending message (incorrect stream)
     */
    public static void askForDownload(String fileName) {
        String fileAndPath = ClientData.getSrc() + fileName;
        try {
            Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.DownloadFile, fileAndPath));
        } catch (IOException ex) {
            System.err.println("Cannot ask for structure: " + ex);
        }
    }
    
    // Uploading files
    
    /**
     * This method is uploading the file to the server
     * @param fileName name of the file to upload
     * @param uploadingPath the path where the file should be uploaded to the server
     * @throws java.io.IOException handle possibility of error during sending message (incorrect stream)
     */
    public static void requestUploadingFile(String fileName, String uploadingPath) {
        String fileAndPath = ClientData.getSrc() + fileName;
        try {
            File file = new File(uploadingPath);

            if (file.exists()) {
                Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.UploadFile, fileAndPath));

                FileInputStream fileInputStream = new FileInputStream(file);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    Client.getObjectOutputStream().write(buffer, 0, bytesRead);
                }

                // Sending additional mesege, because the stream wasn't ending (Only after doing other operation)
                Client.getObjectOutputStream().writeObject(new Message(ActionsEnum.UploadFile, "EOF"));
                fileInputStream.close();
                askForStructure(ClientData.getSrc());
            } else {
                System.err.println("File doesn't exist!");
            }
        } catch (IOException ex) {
            System.err.println("Cannot send File to download!");
        }
    }
}
