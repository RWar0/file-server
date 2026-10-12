package files;

import data.DataAdministration;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import utils.User;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * This class contains methods that are responsible for files.
 */
public class FileParser {

    private static final String RESOURCES_LOCATION;
    private static final String DATABASE_LOCATION;
    private static final String USERS_DATA;

    static {
        RESOURCES_LOCATION = "resources";
        DATABASE_LOCATION = "database";
        USERS_DATA = "Users.dat";
    }
    
    /**
     * This method is initializating files at the beggining and parsing users from file to the list
     * @see data.DataAdministration#getUsers() <b>List of users</b>
     */
    public static void initFiles() {
        createStructure();
        parseUsersToList();
    }
    
    private static void createStructure() {
        createFolder(RESOURCES_LOCATION);
        createFolder(DATABASE_LOCATION);
        createFile(DATABASE_LOCATION, USERS_DATA);
    }

    private static void createFolder(String name) {
        File folder = new File(name);
        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (Exception ex) {
                System.err.println("Something goes wrong during creating folder!: " + ex);
            }
        }
    }

    private static void createFile(String parent, String name) {
        File file = new File(parent, name);
        boolean isExist = file.exists();
        if (!isExist) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                System.err.println("Something gone wrong during creating file: " + name);
            } catch (Exception ex) {
                System.err.println("Other error | Create file: " + ex);
            }
        }
    }

    private static void parseUsersToList() {
        File file = new File(DATABASE_LOCATION, USERS_DATA);

        if (!file.exists() || !file.isFile()) {
            System.out.println("There isn't such a file!");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                User user = (User) in.readObject();
                DataAdministration.getUsers().add(user);
            }

        } catch (EOFException ex) {

        } catch (IOException ex) {
            System.err.println("Error during opening file: " + ex);
        } catch (ClassNotFoundException ex) {
            System.err.println("Class 'Product' not found: " + ex);
        } catch (Exception ex) {
            System.err.println("Other error: " + ex);
        }
    }
    
    /**
     * This method is parsing all users (saved in server's memory) to the file
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html">Thread</a>
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/io/ObjectOutputStream.html">ObjectOutputStream</a>
     * @throws java.io.IOException handle possibility of error during saving object to the file
     * @throws Exception handle possibility of any other error
     */
    public static void parseUsersToFile() {
        new Thread(() -> {
            System.out.println("Users file has been updated!");
            File file = new File(DATABASE_LOCATION, USERS_DATA);
            ObjectOutputStream out;
            try {
                out = new ObjectOutputStream(new FileOutputStream(file));
                for (User user : DataAdministration.getUsers()) {
                    out.writeObject(user);
                }
                out.close();
            } catch (IOException ex) {
                System.err.println("Error during opening file: " + ex);
            } catch (Exception ex) {
                System.err.println("Other error: " + ex);
            }
        }).start();
    }
    
    /**
     * This method is adding all names of the files and the directories to the list of the string
     * @param path the path to the desired directory to get its structure.
     * @return the list of names of all files and directories
     */
    public static List<String> getStructure(String path) {
        String location = RESOURCES_LOCATION + File.separator + path;
        File openFolder = new File(location);

        List<String> returnal = new ArrayList<>();

        if (openFolder.exists() && openFolder.isDirectory()) {
            File[] files = openFolder.listFiles();

            for (File file : files) {
                returnal.add(file.getName());
            }

        } else {
            System.err.println("There isn't such a folder!");
        }

        return returnal;
    }
    
    /**
     * This method is modifying the file - parsing new content
     * @param finalPath path to the file
     * @param contentData new data to put into the file
     * @throws java.io.IOException handle possibility of error during modifying the file
     * @throws Exception handle possibility of any other error
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/io/BufferedWriter.html">BufferedWriter</a>
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/io/OutputStreamWriter.html">OutputStreamWriter</a>
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/io/FileOutputStream.html">FileOutputStream</a>
     */
    public static void parseDataToFile(String finalPath, String contentData) {
        File file = new File(finalPath);

        if (!file.exists() || !file.isFile()) {
            System.out.println("There isn't such a file!");
            return;
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bufferedWriter.write(contentData);
            bufferedWriter.close();
        } catch (IOException ex) {
            System.err.println("Error during saving to file: " + ex);
        } catch (Exception ex) {
            System.err.println("Other error: " + ex);
        }
    }

    // getters
    
    /**
     * This method return path to the "resources" directory.<br />
     * <b>In resources directory, there are shared all files and directories for users.</b>
     * @return This method return string of the path to "resources" directory.
     */
    public static String getRESOURCES_LOCATION() {
        return RESOURCES_LOCATION;
    }

}
