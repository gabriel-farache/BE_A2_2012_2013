
package attachmentsManagement;

import dataObjects.Attachment;
import dataObjects.Item;
import dataObjects.Task;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.fileupload.FileItemStream;

/**
 * This class browses the attachments of an Item and create the attachement
 * file at the appropriated location.
 *  * @author Durban
 */
public class SaveAttachments {

    /**
     * Saves all the attachements into memory given the following
     * attachments files architecture:
     * ROOT_PATH/[task|message]/YEAR_VALUE/MONTH_VALUE/DAY_VALUE/ITEM_ID/filename.extension
     * The directories are all created when they don't exist. If the file already exists,
     * it will be rewritten/replaced.
     * @param it Item from which the attachments will be stored.
     */
    protected static void createAndSaveAttachments(Item it) {
        //Possible paths are ROOT_PATH/<task|message>/YEAR_VALUE/MONTH_VALUE/DAY_VALUE/ITEM_ID/filename.extension
        if(it != null && it.hasAttachments()) {
            File working_dir = prepareDirectories(it);
            try {
                String working_path = working_dir.getAbsolutePath();

                //Now we can save all the attachments found.
                for(Attachment att : it.getAttachments()) {
                    //Creating the file and replacing it if needed.
                    working_dir = new File(working_path+File.separatorChar+att.getName());
                    if(!working_dir.exists() || !working_dir.isDirectory()) {
                        working_dir.createNewFile();
                        //Trying to save the file
                        System.out.println("Trying to save "+working_dir.getAbsolutePath()+".");
                        System.out.println("Bytes written "+
                                Base64ToFile.save(att.getContent(), working_dir.getAbsolutePath()));
                    }
                }
            } catch (IOException ioe) {
                System.err.println("Error: An error occurred while trying to create the directory "
                        +working_dir.getAbsolutePath()+".");
            }
        }
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
    protected static void createAndSaveAttachments(Item it, String filename, InputStream fist) {
        if(it != null && it.hasAttachments()) {
            File working_dir = prepareDirectories(it);
            try {
                String working_path = working_dir.getAbsolutePath();
                InputStream fis = ((fist instanceof FileItemStream) ? ((FileItemStream)fist).openStream() : fist);
                    //Creating the file and replacing it if needed.
                    working_dir = new File(working_path+File.separatorChar+filename);
                    if(!working_dir.exists() || !working_dir.isDirectory()) {
                        System.out.println("Trying to save "+working_dir.getAbsolutePath()+".");
                        working_dir.createNewFile();
                        //Trying to save the file
                        FileOutputStream fos = new FileOutputStream(working_dir);
                        byte[] buffer = new byte[1024];
                        int nbRead = 0;
                        int totalRead = 0;
                        while((nbRead=fis.read(buffer)) != -1) {
                            fos.write(buffer,0,nbRead);
                            totalRead += nbRead;
                        }
                        fis.close();
                        fos.close();
                        System.out.println("Bytes written "+totalRead+".");

                    }
            } catch (IOException ex) {
                Logger.getLogger(SaveAttachments.class
                    .getName()).log(Level.SEVERE, null, ex);
                System.err.println("Error: An error occurred while trying to create the directory "
                        +working_dir.getAbsolutePath()+".");
            }
        }
    }

    /**
     * Prepare the directories in the attachments tree on the disk.
     * @param it the item from which to retrieve the attachments.
     * @return the deepest directory.
     */
    public static File prepareDirectories(Item it) {
        String working_path = ManageAttachements.getRootPath();
        File working_dir = null;
        //First step: main attachments directory
        working_dir = new File(working_path);
        if(!working_dir.exists() || !working_dir.isDirectory()) {
            working_dir.mkdir();
        }
        //Second step: Item type directory, message or task
        String type="message";
        if(it instanceof Task) {
            type = "task";
        }

        int year = it.getCreationDate().getTime().getYear()+1900;
        int month = it.getCreationDate().getTime().getMonth()+1;
        int day = it.getCreationDate().getTime().getDate();
        //Building the consecutives dir names
        String [] dirs = {  File.separatorChar+type,
                            File.separatorChar+String.valueOf(year),
                            File.separatorChar+String.valueOf(month),
                            File.separatorChar+String.valueOf(day),
                            File.separatorChar+String.valueOf(it.getId())
        };
        //Creating the dirs if they don't exist.
        for(int i=0 ; i<dirs.length ; i++) {
            working_path += dirs[i];
            working_dir = new File(working_path);
            if(!working_dir.exists() || !working_dir.isDirectory()) {
                working_dir.mkdir();
            }
        }

        return working_dir;
    }
}
