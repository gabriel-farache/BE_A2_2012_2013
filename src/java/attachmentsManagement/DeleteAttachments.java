package attachmentsManagement;

import dataObjects.Attachment;
import dataObjects.Item;
import java.io.File;

/**
 * This class removes the attachments on the hard disk of an Item.
 * @author Durban
 */
public class DeleteAttachments {

    /**
     * Removes all the attachments of the item on the disk.
     * @param it Item from which to remove all the attachments.
     */
    protected static void removeAttachments(Item it) {
        File target = null;
        if(it.getAttachments().size() > 0) {
            for(Attachment att : it.getAttachments()) {
                target = new File(ManageAttachements.getAttachementPath(it, att.getName()));
                if(target.exists()) {
                    System.out.println("Trying to remove the file: "+target.getAbsolutePath()+".");
                    if(!target.delete()) {
                        System.err.println("Can't remove the file "+target.getAbsolutePath()+".");
                    }
                }
            }
            //Removing others files than the attachments (like the .zip file).
            target = new File(ManageAttachements.getAttachementPath(it, it.getAttachments().get(0).getName()));
            for(File f: target.getParentFile().listFiles()) {
                System.out.println("Trying to remove the file: "+f.getAbsolutePath()+".");
                if(!f.delete()) {
                    System.err.println("Can't remove the file "+f.getAbsolutePath()+".");
                }
            }
            removeDirs(it);
        }
    }

    /**
     * Removes the directories left empty after removing the attachments files.
     * @param it Item from which the attachments have been removed.
     */
    private static void removeDirs(Item it) {
        File target = new File(ManageAttachements.getAttachementPath(it,
                it.getAttachments().get(0).getName()));
        target = target.getParentFile();
        while(target.isDirectory() && target.listFiles().length == 0) {
            System.out.println("Trying to remove the directory: "+target.getAbsolutePath()+".");
            if(!target.delete()) {
                System.err.println("Can't remove the directory "+target.getAbsolutePath()+".");
            }
            target = target.getParentFile();
        }
    }
}
