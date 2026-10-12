package utils;

import java.io.Serializable;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */
public class User implements Serializable {

    private static int ID;

    private String login;
    private String password;
    private int userId;
    private int access;

    static {
        ID = 0;
    }

    /**
     * Constructor for asking to login
     * @param login user login
     * @param password user password
     */
    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /**
     * Constructor for creating new
     * @param login user login
     * @param password user password
     * @param access assigned user's access
     */
    public User(String login, String password, int access) {
        this.login = login;
        this.password = password;
        this.userId = ++ID;
        this.access = access;
    }

    /**
     * Constructor for adding Users from list
     * @param login user login
     * @param password user password
     * @param userId assigned user's ID
     * @param access assigned user's access
     */
    public User(String login, String password, int userId, int access) {
        this.login = login;
        this.password = password;
        this.userId = userId;
        this.access = access;

        if (userId > ID) {
            ID = userId;
        }
    }

    // getters and setters:
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

}
