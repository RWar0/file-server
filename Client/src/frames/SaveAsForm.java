package frames;

import data.ClientData;
import java.io.File;
import javax.swing.*;
/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * This class is creating window that represents choosing desired location of saving file that will be downloaded from the server.
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/javax/swing/JFileChooser.html">JFileChooser</a>
 */
public class SaveAsForm extends JFileChooser {
    
    private String fileLocation = "";
    
    /**
     * Constructor creating saving form
     * @param fileName name of the file on the server, that will be passed as name of file on client's device
     */
    public SaveAsForm(String fileName) {
        super(".");
        
        setSelectedFile(new File(fileName));
        
        setVisible(true);
        
        int val = showSaveDialog(null);
        
        if (val == JFileChooser.APPROVE_OPTION) {
            fileLocation = getSelectedFile().getPath();
            ClientData.setDownloadLocation(fileLocation);
        }
    }
}