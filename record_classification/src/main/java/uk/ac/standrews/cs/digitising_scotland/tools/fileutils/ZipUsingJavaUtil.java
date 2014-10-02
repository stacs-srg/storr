package uk.ac.standrews.cs.digitising_scotland.tools.fileutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * This classes provides methods to package files into a zip file.
 * 
 * @author jkc25
 * 
 */
public class ZipUsingJavaUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUsingJavaUtil.class);

    /**
     * Zip function zip all files and folders.
     * @param args String[] argument 0 is file to folder or file, argument 1 is output file.
     */
    public static void main(final String[] args) {

        ZipUsingJavaUtil zip = new ZipUsingJavaUtil();

        try {
            if (zip.zipFiles(args[0], args[1])) {
                Utils.deleteDirectory(new File("ModernDataRun1/parsedData91a"));
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Zip files from srcFolder to destZipFile.
     * @param srcFolder Folder containing file to be zipped.
     * @param destZipFile Name of destination zip file.
     * @return True if sucessful, false otherwise.
     */
    public boolean zipFiles(final String srcFolder, final String destZipFile) {

        boolean result = false;
        try {
            LOGGER.info("Program Start zipping the given files: " + srcFolder);
            zipFolder(srcFolder, destZipFile);
            result = true;
            LOGGER.info("Given files are successfully zipped");
        }
        catch (Exception e) {
            LOGGER.info("Some Errors happned during the zip process");
            LOGGER.error(e.getMessage(), e);
        }
        return result;

    }

    /*
     * zip the folders
     */
    private void zipFolder(final String srcFolder, final String destZipFile) throws Exception {

        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        // create the output stream to zip file result
        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        // add the folder to the zip
        addFolderToZip("", srcFolder, zip);
        // close the zip objects
        zip.flush();
        zip.close();
        fileWriter.flush();
        fileWriter.close();

    }

    /*
     * recursively add files to the zip files
     */
    private void addFileToZip(final String path, final String srcFile, final ZipOutputStream zip, final boolean flag) throws Exception {

        /*
         * create the file object for inputs
         */
        File folder = new File(srcFile);

        /*
         * if the folder is empty add empty folder to the Zip file
         */
        if (flag) {
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
        }
        else { /*
               * if the current name is directory, recursively traverse it to
               * get the files
               */
            if (folder.isDirectory()) {
                /*
                 * if folder is not empty
                 */
                addFolderToZip(path, srcFile, zip);
            }
            else {
                /*
                 * write the file to the output
                 */
                final int bufferSize = 1024;
                byte[] buf = new byte[bufferSize];
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }

                in.close();
            }
        }
    }

    /*
     * add folder to the zip file
     */
    private void addFolderToZip(final String path, final String srcFolder, final ZipOutputStream zip) throws Exception {

        File folder = new File(srcFolder);

        //  check the empty folder
        if (folder.list().length == 0) {
            LOGGER.info(folder.getName());
            addFileToZip(path, srcFolder, zip, true);
        }
        else {
            /*
             * list the files in the folder
             */
            for (String fileName : folder.list()) {
                if ("".equals(path)) {
                    addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
                }
                else {
                    addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
                }
            }
        }
    }

}
