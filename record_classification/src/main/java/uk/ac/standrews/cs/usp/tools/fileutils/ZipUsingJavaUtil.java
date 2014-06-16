package uk.ac.standrews.cs.usp.tools.fileutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import uk.ac.standrews.cs.usp.tools.Utils;

/**
 * This classes provides methods to package files into a zip file.
 * 
 * @author jkc25
 * 
 */
public class ZipUsingJavaUtil {

    /**
     * Zip function zip all files and folders.
     * @param args not used.
     */
    public static void main(final String[] args) {

        ZipUsingJavaUtil zip = new ZipUsingJavaUtil();
        try {
            if (zip.zipFiles("ModernDataRun1/parsedData91a", "parsed91JAavaTesta.zip")) {
                Utils.deleteDirectory(new File("ModernDataRun1/parsedData91a"));
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            System.out.println("Program Start zipping the given files: " + srcFolder);
            /*
             * send to the zip procedure
             */
            zipFolder(srcFolder, destZipFile);
            result = true;
            System.out.println("Given files are successfully zipped");
        }
        catch (Exception e) {
            System.out.println("Some Errors happned during the zip process");
            System.out.println(e);
            e.printStackTrace();
        }
        return result;

    }

    /*
     * zip the folders
     */
    private void zipFolder(final String srcFolder, final String destZipFile) throws Exception {

        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        /*
         * create the output stream to zip file result
         */
        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        /*
         * add the folder to the zip
         */
        addFolderToZip("", srcFolder, zip);
        /*
         * close the zip objects
         */
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
                byte[] buf = new byte[1024];
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    /*
                     * Write the Result
                     */
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

        /*
         * check the empty folder
         */
        if (folder.list().length == 0) {
            System.out.println(folder.getName());
            addFileToZip(path, srcFolder, zip, true);
        }
        else {
            /*
             * list the files in the folder
             */
            for (String fileName : folder.list()) {
                if (path.equals("")) {
                    addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
                }
                else {
                    addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
                }
            }
        }
    }

}
