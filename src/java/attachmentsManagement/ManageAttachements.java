
package attachmentsManagement;

import dataObjects.Item;
import dataObjects.Task;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * This class is a Facade allowing to save or get files corresponding to
 * the attachments of an Item.
 * @author Durban
 */
public class ManageAttachements {

     /**
     * Saves all the attachements into memory given the following
     * attachments files architecture:
     * ROOT_PATH/[task|message]/YEAR_VALUE/MONTH_VALUE/DAY_VALUE/ITEM_ID/filename.extension
     * The directories are all created when they don't exist. If the file already exists,
     * it will be rewritten/replaced.
     * @param it Item from which the attachments will be stored.
     */
     public static void createAndSaveAttachments(Item it) {
         SaveAttachments.createAndSaveAttachments(it);
     }

     /**
     * Saves all the attachements into memory given the following
     * attachments files architecture:
     * ROOT_PATH/[task|message]/YEAR_VALUE/MONTH_VALUE/DAY_VALUE/ITEM_ID/filename.extension
     * The directories are all created when they don't exist. If the file already exists,
     * it will be rewritten/replaced.
     * @param it Item from which the attachments will be stored.
     * @param filename Name of the file to save.
     * @param fis File input stream of the file to save.
     */
     public static void createAndSaveAttachments(Item it, String filename,InputStream fis) {
         SaveAttachments.createAndSaveAttachments(it,filename, fis);
     }

     /**
     * Gives the list of attachments files of the Item.
     * @param it Item from which to retrieve the attachments files.
     * @return the list of attachments files.
     */
     public static  ArrayList<File> getAttachementsFiles(Item it) {
         return GetAttachments.getAttachementsFiles(it);
     }

     /**
     * Gives a precise attachment of an item.
     * @param it Item from which to get the file.
     * @param filename Name of the file to give.
     * @return The file found, or null.
     */
    public static File getAttachmentFile(Item it, String filename) {
        return GetAttachments.getAttachmentFile(it, filename);
    }

     /**
     * Gives the attachments into a zip file.
     * @param it Item from which to retrieve the attachments files.
     * @return the zip file
     */
    public static File getAttachementsFilesInZip(Item it) {
        return GetAttachments.getAttachementsFilesInZip(it);
    }

    /**
     * Gives the current root path of the attachments files,
     * based on the properties file.
     * @return the current root path of the attachments files
     */
    protected static String getRootPath() {
        Properties prop = new Properties();
        String path;
        try {
    		prop.load(ManageAttachements.class.getResourceAsStream("attachmentsProperties.properties"));
                path = prop.getProperty("root_path");
    	} catch (Exception ex) {
    		path = "";
        }
        return path;
    }

    /**
     * Build the absolute path for the corresponding Item and Attachment name.
     * @param it Parent item of the attachment.
     * @param attName attachment file name.
     * @return the absolute path of the attachment.
     */
    protected static String getAttachementPath(Item it, String attName) {
        String path = getRootPath();
        if(it instanceof Task) {
            path += File.separatorChar+"task";
        } else {
            path += File.separatorChar+"message";
        }
        path +=File.separatorChar;
        int year = it.getCreationDate().getTime().getYear()+1900;
        int month = it.getCreationDate().getTime().getMonth()+1;
        int day = it.getCreationDate().getTime().getDate();
        path += String.valueOf(year)+File.separatorChar+
                String.valueOf(month)+File.separatorChar+
                String.valueOf(day)+File.separatorChar+
                String.valueOf(it.getId())+File.separatorChar+
                attName;

        return path;
    }

    /**
     * Removes all the attachments of the item on the disk.
     * @param it Item from which to remove all the attachments.
     */
    public static void removeAttachments(Item it) {
        DeleteAttachments.removeAttachments(it);
    }
}
