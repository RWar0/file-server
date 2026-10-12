package data;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * This class storages current path in server and path where the file has to be downloaded.
 */
public class ClientData {
    private static String src;
    private static String downloadLocation;
    
    static {
        src = "";
        downloadLocation = "";
    }
    
    /**
     * This method changes the current server path by extending it.
     * @param value the value that has to be added to path
     */
    public static void appendSrc(String value) {
        src += value + File.separator;
    }
    
    /**
     * This method is removing last part of the path.
     * It symbolizes returning to the previous folder
     */
    public static void removeLastPartOfSrc() {
        String arr[] = src.split(Pattern.quote(File.separator));
        String temp = "";
        for (int i = 0; i < arr.length - 2; i++) {
            temp += arr[i];
            if(i != arr.length-2) {
                temp += File.separator;
            }
        }
        src = temp;
    }
    
    // Getters and Setters:
    
    /**
     * This method is getter of the current path.
     * @return the method return current path on the server
     */
    public static String getSrc() {
        return src;
    }
    public static void setSrc(String src) {
        ClientData.src = src;
    }

    public static String getDownloadLocation() {
        return downloadLocation;
    }

    public static void setDownloadLocation(String downloadLocation) {
        ClientData.downloadLocation = downloadLocation;
    }
    
}
