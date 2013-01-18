
package attachmentsManagement;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

/**
 * This class build a zip file from a file, a directory or a list of file.
 * @author Durban
 */
public class ZipMaker {
    private static int BUFFER_SIZE = 32768;

    /**
     * Put a file or a directory into a zip file. The zip file must have been
     * already created.
     * @param file file or directory to put into the archive.
     * @param zipFile zip file of destination.
     * @param compressionLevel compression level (0-9)
     * @throws Exception
     */
     public static void fileToZip(File file, File zipFile, int compressionLevel) throws Exception {
        FileOutputStream fout = new FileOutputStream(zipFile);
        ZipOutputStream zout = null;
        try {
            zout = new ZipOutputStream(new BufferedOutputStream(fout));
            zout.setLevel(compressionLevel);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++)
                    fileToZip(files[i], zout, file);
            } else if (file.isFile()) {
                fileToZip(file, zout, file.getParentFile());
            }
        } finally {
            if (zout != null)
                zout.close();
        }
    }

     /**
      * Put a list of files into a zip file. The zip file must have been
      * already created.
      * @param files files list to put into the archive.
      * @param zipFile file of destination.
      * @param compressionLevel compression level (0-9)
      * @throws Exception
      */
    public static void fileToZip(ArrayList<File> files, File zipFile, int compressionLevel) throws Exception {
        FileOutputStream fout = new FileOutputStream(zipFile);
        ZipOutputStream zout = null;
        try {
            zout = new ZipOutputStream(new BufferedOutputStream(fout));
            zout.setLevel(compressionLevel);
            for(File f: files) {
                fileToZip(f, zout, f.getParentFile());
            }
        } finally {
            if (zout != null)
                zout.close();
        }
    }

     /**
      * Put a file or a directory into a zip file. Recurcive call.
      * @param file file or directory to put into the archive.
      * @param zout output stream of a zip file.
      * @param baseDir base directory from which to start to save into the zip file.
      * @throws Exception
      */
    private static void fileToZip(File file, ZipOutputStream zout, File baseDir) throws Exception {
        String entryName = file.getPath().substring(baseDir.getPath().length() + 1);
        //Separators must be a /
        if (File.separatorChar != '/')
          entryName = entryName.replace(File.separator, "/");
        if (file.isDirectory()) {
            zout.putNextEntry(new ZipEntry(entryName + "/"));
            zout.closeEntry();
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++)
                fileToZip(files[i], zout, baseDir);
        } else {
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                zout.putNextEntry(new ZipEntry(entryName));
                streamCopy(is, zout);
            } finally {
                zout.closeEntry();
                if (is != null)
                    is.close();
            }
        }
    }

    /**
     * Copy the bytes from the input stream to the output one.
     * @param is input stream to read.
     * @param os output stream to write in.
     * @throws IOException
     */
    private static void streamCopy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
    }
}
