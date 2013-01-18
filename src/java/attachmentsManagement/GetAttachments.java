
package attachmentsManagement;

import dataObjects.Attachment;
import dataObjects.Item;
import dataObjects.Task;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * This class allows to get the Files onto memory corresponding to the
 * attachments of the Item.
 * @author Durban
 */
public class GetAttachments {

    /**
     * Gives the list of attachments files of the Item.
     * @param it Item from which to retrieve the attachments files.
     * @return the list of attachments files.
     */
    protected static ArrayList<File> getAttachementsFiles(Item it) {
        ArrayList<File> attachments = new ArrayList<File>();
        if(it != null) {
            for(Attachment att : it.getAttachments()) {
                File attachment = new File(getAttachementPath(it, att.getName()));
                //We add the file only if it actually exists.
                if(attachment.exists()) {
                    attachments.add(attachment);
                } else {
                    System.err.println("Error: Can't locate the attachement file "
                            +attachment.getAbsolutePath()+".");
                }
            }
        }
        return attachments;
    }

    /**
     * Gives a precise attachment of an item.
     * @param it Item from which to get the file.
     * @param filename Name of the file to give.
     * @return The file found, or null.
     */
    protected static File getAttachmentFile(Item it, String filename) {
        if(it != null) {
            File f = null;
            for(Attachment att : it.getAttachments()) {
                if(att.getName().equals(filename)) {
                    File attachment = new File(getAttachementPath(it, att.getName()));
                    //We add the file only if it actually exists.
                    if(attachment.exists()) {
                        return attachment;
                    } else {
                        System.err.println("Error: Can't locate the attachement file "
                            +attachment.getAbsolutePath()+".");
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gives the attachments into a zip file. If the zip file doesn't already exist,
     * it will be created, otherwise, it won't be changed.
     * @param it Item from which to retrieve the attachments files.
     * @return the zip file
     */
    protected static File getAttachementsFilesInZip(Item it) {
        if(it.hasAttachments()) {
            File zip = new File(getAttachementPath(it,"attachments.zip"));
            if(!zip.exists()) {
                try {
                    int comp = 6;
                    Properties prop = new Properties();
                    try {
                            prop.load(ManageAttachements.class.getResourceAsStream("attachmentsProperties.properties"));
                            comp = Integer.parseInt(prop.getProperty("zip_compression"));
                    } catch (Exception ex) {
                            comp = 6;
                    }
                    zip.createNewFile();
                    ZipMaker.fileToZip(getAttachementsFiles(it), zip, comp);
                } catch (IOException ioe) {
                    System.err.println("Error: Can't create the new zip file "
                                +zip.getAbsolutePath()+".");
                } catch (Exception ex) {
                    System.err.println("Error: Can't build the zip file "
                                +zip.getAbsolutePath()+".");
                }
            }
            return zip;
        }
        return null;
    }

    /**
     * Build the absolute path for the corresponding Item and Attachment name.
     * @param it Parent item of the attachment.
     * @param attName attachment file name.
     * @return the absolute path of the attachment.
     */
    private static String getAttachementPath(Item it, String attName) {
        String path = ManageAttachements.getRootPath();
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
}
