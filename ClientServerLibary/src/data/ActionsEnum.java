package data;

import java.io.Serializable;

/**
 * @author Rafal Waremczuk
 * @version 0.0.1
 */

/**
 * This enumeration is used to choosing type of sending messages
 */
public enum ActionsEnum implements Serializable {
    Loggin,
    GetStructure,
    OpenFile,
    DownloadFile,
    UploadFile,
    CreateFile,
    CreateFolder,
    RemoveFile,
    RemoveFolder,
    ModifyFile,
    End,
    Info
}
