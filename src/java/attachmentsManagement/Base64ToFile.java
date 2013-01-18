package attachmentsManagement;

import org.apache.commons.codec.binary.Base64;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This class creates and saves a file from a base64 formatted String.
 * @author Durban
 */
public class Base64ToFile {

    /**
     * This class saves into the destFile destination File given by its
     * absolute path the data contained in the base64Date base64 formatted String.
     * @param base64Data the base64 formatted String.
     * @param destFile the absolutate path of the destination file
     * @return the number of bytes written.
     */
    public static int save(String base64Data, String destFile){
        byte [] bytes;
        int nbWrote=0;
        FileOutputStream fos = null;

        try {
            //Trying to open the dest file before any process.
            fos = new FileOutputStream(destFile);
            //Decoding the base64 data into bytes.
            bytes = Base64.decodeBase64(base64Data);
            nbWrote = bytes.length;
            //Writes into the stream/file.
            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException fnfe) {
            System.err.println("Error: Destination file ["+destFile+"] not found.");
        } catch (IOException ioe) {
            System.err.println("Error while writing into "+destFile+".");
        }
        return nbWrote;
    }
}
