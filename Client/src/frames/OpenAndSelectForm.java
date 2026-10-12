package frames;

import handlers.CommandsHandlers;
import javax.swing.*;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * This class is creating window that represents choosing desired file that will be updloaded from the user to the server.
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/javax/swing/JFileChooser.html">JFileChooser</a>
 */
public class OpenAndSelectForm extends JFileChooser {
    
    public OpenAndSelectForm() {
        super(".");

        setVisible(true);

        int val = showOpenDialog(null);

        if (val == JFileChooser.APPROVE_OPTION) {
            String fileName = getSelectedFile().getName();
            String filePath = getSelectedFile().getPath();
            CommandsHandlers.requestUploadingFile(fileName, filePath);
        }
    }
}
