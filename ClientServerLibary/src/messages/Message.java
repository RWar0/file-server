package messages;

import data.ActionsEnum;
import java.io.*;
import java.util.*;
import utils.User;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * The class is as object in communicatinon between client and server
 */
public class Message implements Serializable {
    private ActionsEnum action;
    private String message;
    private Object content;
       
    /**
     * Constructor for messages with text content
     * @param action it is type of message function
     * @param message it is string content
     */
    public Message(ActionsEnum action, String message) {
        this.action = action;
        this.message = message;
    }
    
    /**
     * Constructor for messages with text content
     * @param action it is type of message function
     * @param message it is string content
     * @param content it is an Object that will later be converted to the expected class object
     */
    public Message(ActionsEnum action, String message, Object content) {
        this.action = action;
        this.message = message;
        this.content = content;
    }
    
    // Methods to convert content
    /**
     * @return the method returns User, that is converted from Object
     */
    public User getUser() {
        if(content instanceof User) {
            return (User) content;
        }
        else {
            return null;
        }
    }
    
    /**
     * @return the method returns List of strings, that is converted from Object. It gives structure of the file.
     */
    public List<String> getStructure() {
        if(content instanceof List) {
            return (List<String>) content;
        }
        else {
            return null;
        }
    }
    
    /**
     * @return the method returns string that is converted from Object
     */
    public String getStringContent() {
        if(content instanceof String) {
            return (String) content;
        }
        else {
            return null;
        }
    }
    
    // Getters and setters
    public ActionsEnum getAction() {
        return action;
    }
    public String getMessage() {
        return message;
    }
    public Object getContent() {
        return content;
    }
}
