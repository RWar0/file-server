package data;

import files.FileParser;
import java.util.ArrayList;
import java.util.List;
import utils.User;

/**
 * @author Rafal Waremczuk
 */

/**
 * This class is containing server data.
 * It contains user accounts data
 */
public class DataAdministration {
    private static List<User> users;
    
    static {
        users = new ArrayList<>();
    }
    
    /**
     * This method search and return the user account by given data.
     * @param login user login
     * @param password user password
     * @return This method return object of user account if data is valid, otherwise NULL
     */
    public static User getUser(String login, String password) {
        return users.stream().filter(item -> item.getLogin().equals(login) && item.getPassword().equals(password)).findAny().orElse(null);
    }
    
    /**
     * This method search and return the user account by given id
     * @param userId searching user's id.
     * @return This method return object of user account if found, otherwise NULL
     */
    public static User getUserById(int userId) {
        return users.stream().filter(item -> item.getUserId() == userId).findAny().orElse(null);
    }
    
    /**
     * This method is adding a user to List of users and to the file
     * @param login new user login
     * @param password new user password
     * @param accessLevel the access level
     * @see files.FileParser#parseUsersToFile() <b>parseUsersToFile()</b> - the method parse every user account to the file
     */
    public static void addUser(String login, String password, int accessLevel) {
        users.add(new User(login, password, accessLevel));
        FileParser.parseUsersToFile();
    }
    
    /**
     * This method remove user by given id.
     * @param userId user id to delete.
     */
    public static void deleteUserById(int userId) {
        users.removeIf(item -> item.getUserId() == userId);
    }
    
    // Getters
    /**
     * This method returns list of the users.
     * @return This method return list of the users.
     */
    public static List<User> getUsers() {
        return users;
    }
}
