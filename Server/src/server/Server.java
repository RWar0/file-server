package server;

import files.FileParser;
import forms.ServerFrame;
import handlers.ClientHandler;
import java.net.*;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * <b>This class is starting the server application.</b>
 */
public class Server {
    
    private static int PORT = 12345;
    private static Socket socket = null;
    private static ServerSocket s = null;
    
    private static ServerFrame serverFrame;
    
    public static void main(String[] args) {
        
        serverFrame = new ServerFrame();
        serverFrame.setVisible(true);
        
        Thread runFilesConnection = new Thread(() -> {
            serverFrame.updateTerminal("Starting connecting with files...");
            FileParser.initFiles();
        });
        
        Thread runServer = new Thread(() -> {
            try {
                runFilesConnection.join();
                serverFrame.updateTerminal("Files connected. \nStarting server...");
                
                try {
                    s = new ServerSocket(PORT);
                    serverFrame.updateTerminal("Server has started.");
                    while (true) {
                        
                        socket = s.accept();
                        serverFrame.updateTerminal("Client connected: " + socket);
                        
                        
                        ClientHandler client = new ClientHandler(socket);
                        client.start();
                    }
                }
                catch (Exception e) {
                    System.out.println("Error: " + e);
                    serverFrame.updateTerminal("Error: " + e);
                }
                
                try {
                    s.close();
                } catch (Exception e) {}
                
            }
            catch (InterruptedException ex) {
                System.err.println("Cannot start Thread");
                serverFrame.updateTerminal("Cannot start Thread");
            }
        });
        
        runFilesConnection.start();
        runServer.start();
    }

    public static ServerFrame getServerFrame() {
        return serverFrame;
    }
}