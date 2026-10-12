package client;

import data.ActionsEnum;
import frames.*;
import handlers.ClientReadThread;
import java.net.*;
import java.io.*;
import java.util.Scanner;
import messages.Message;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * <b>This class is starting the clients application.</b>
 * There is information about the connection to the server.
 */
public class Client {

    private static final int SERVER_PORT = 12345;
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static InetAddress iAddress = null;
    private static Socket socket = null;
    private static ObjectOutputStream out = null;
    private static Scanner scanner = new Scanner(System.in);
    private static LoginForm loginForm;
    private static MainFrame mainFrame;
    
    private static boolean logged = false;
    private static boolean properConnection = false;
    
    /**
     * <b>This method runs the Client application.</b><br />
     * It runs the connection with the server and the login form.
     */
    public static void main(String[] args) {
        try {
            iAddress = InetAddress.getByName(SERVER_ADDRESS);
            System.out.println("Connecting to address: " + iAddress);
            socket = new Socket(iAddress, SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Connecting fails");
        } catch (Exception e) {
            System.exit(0);
        }
        
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            ClientReadThread readFromServer = new ClientReadThread(socket);
            readFromServer.start();
            
            properConnection = true;
            
            loginForm = new LoginForm();
            loginForm.setVisible(true);
            
        } catch (Exception ex) {
            System.err.println("Cannot connect to server!");
            System.err.println("Occurred error: " + ex);
            
            showsThatConnectionBroken();
        }
        
        while (properConnection) {
            String mess;

            System.out.print("What to say to server? -> ");
            mess = scanner.nextLine();

            try {
                out.writeObject(new Message(ActionsEnum.Info, mess));
                out.flush();
                
                if (mess.equals("END")) {
                    break;
                }
                
            } catch (Exception e) {
                break;
            }
        }
        try {
            out.close();
            socket.close();
        } catch (Exception e) {
        }
    }
    
    // Methods
    /**
     * This method is invoke after successful loggin (when server sends appropriate message)
     * @param access level of security assigned to user, that allows doing more operations. (Server response sends this)
     */
    public static void loggedIn(int access) {
        loginForm.dispose();
        mainFrame = new MainFrame(access);
        mainFrame.setVisible(true);
    }
    /**
     * This method is closing program with all windows. It also send a message to server to make sure that disconnected.
     */
    public static void close() {
        try {
            out.writeObject(new Message(ActionsEnum.End, "END"));
            out.flush();
        } catch(Exception ex) {}
        properConnection = false;
        loginForm.dispose();
        mainFrame.dispose();
        System.exit(0);
    }
    
    /**
     * This method closes all current windows and creates login form with the information that 'Server connection is invalid' and disabled all options.
     */
    public static void showsThatConnectionBroken() {
        properConnection = false;
        
        if(loginForm != null) {
            loginForm.dispose();
        }
        if(mainFrame != null) {
            mainFrame.dispose();
        }
        
        loginForm = new LoginForm();
        loginForm.setInvalidConnectionSetting();
        loginForm.setVisible(true);
    }
    
    
    // Getters
    /**
     * This method is getting and returning object of login form
     * @return This method returns object of login form
     */
    public static LoginForm getLoginForm() {
        return loginForm;
    }
    /**
     * This method is getting and returning object of main frame
     * @return This method returns object of main frame
     */
    public static MainFrame getMainFrame() {
        return mainFrame;
    }
    
    /**
     * This method is getting and returning object of object stream associated with the server.
     * @return This method returns object stream connected with server
     */
    public static ObjectOutputStream getObjectOutputStream() {
        return out;
    }
    
    /**
     * This method is getting and returning information if client is logged in or not.
     * @return This method returns bool with information if client is logged in.
     */
    public static boolean isLogged() {
        return logged;
    }
    /**
     * This method is setting if client is logged in or not.
     * @param logged the value deciding if the user is connected or not.
     */
    public static void setLogged(boolean logged) {
        Client.logged = logged;
    }
    
    /**
     * This method is getter of information about proper connection with the server.
     * @return This method returns bool that informs about proper connection with the server.
     */
    public static boolean isProperConnection() {
        return properConnection;
    }
    
}