/*
 * Understanding Scotland's People (USP) project.
The aim of the project is to produce a linked pedigree for all publicly available Scottish birth/death/marriage records from 1855 to the present day.
Digitisation of the records is being carried out by the ESRC-funded Digitising | | Scotland project, run by University of St Andrews and National Records of Scotland.
The project is led by Chris Dibben at the Longitudinal Studies Centre at St Andrews. The other project members are Lee Williamson (also at the Longitudinal Studies Centre)
Graham Kirby, Alan Dearle and Jamie Carson at the School of Computer Science at St Andrews; and Eilidh Garret and Alice Reid at the Department of Geography at Cambridge.
 */

package uk.ac.standrews.cs.digitising_scotland.tools;

import com.google.common.io.Files;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.DataClerkingWriter;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * Utility classes related to writing to files and other often used methods.
 *
 * @author jkc25
 */
public final class Utils {

    /**
     * Instantiates a new utils.
     */
    private Utils() {

        // private constructor for utility class.
    }

    /**
     * Generates a storage folder name based on the baseDirectory and prefix.
     * Form of storage folder returned is baseDirectory/prefix + (highestFolder+1)
     * for example target/exampleDir1
     *
     * @param baseDirectory the baseDirectory
     * @param prefix        the prefix
     * @return the storage name
     */
    public static File getStorageName(final File baseDirectory, final String prefix) {

        int highest = calculateHighestFolderNumber(baseDirectory, prefix);
        String newName = prefix + highest + 1;
        return new File(newName);
    }

    /**
     * Calculate highest folder number of storage folder as generated by Utils.getStorageName().
     *
     * @param baseDirectory the base directory
     * @param prefix        the prefix
     * @return the int
     */
    private static int calculateHighestFolderNumber(final File baseDirectory, final String prefix) {

        int highest = 0;
        int current = 0;

        File[] files = baseDirectory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith(prefix)) {
                current = Integer.parseInt(files[i].getName().split(prefix)[1]);
                if (current > highest) {
                    highest = current;
                }
            }
        }
        return highest;
    }

    /**
     * Gets the storage name.
     *
     * @param baseDirectory the baseDirectory
     * @param prefix        the prefix
     * @return the storage name
     */
    public static File getLastStorageName(final File baseDirectory, final String prefix) {

        int highest = calculateHighestFolderNumber(baseDirectory, prefix);
        String newName = prefix + highest;
        return new File(newName);
    }

    /**
     * Move all files in toMove array to toHere locations.
     *
     * @param toMove List of files to move
     * @param toHere where we want the files moved to.
     */
    public static void moveFiles(final File[] toMove, final File toHere) {

        for (int i = 0; i < toMove.length; i++) {
            if (toMove[i].exists()) {
                try {
                    if (toMove[i].isDirectory()) {
                        File newFolder = new File(toHere + "/" + toMove[i].getName());

                        if (!newFolder.exists()) {
                            if (!newFolder.mkdirs()) {
                                System.out.println("Error creating " + newFolder.getName());
                            }
                        }
                        moveFiles(toMove[i].listFiles(), newFolder);
                    } else {
                        Files.copy(toMove[i], new File(toHere.getAbsolutePath() + "/" + toMove[i].getName()));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Moves files in the toMove array to the toHere location using the Apache FileUtils library.
     *
     * @param toMove Move these files
     * @param toHere to this location
     */
    public static void moveFilesApache(final File[] toMove, final File toHere) {

        for (int i = 0; i < toMove.length; i++) {
            if (toMove[i].isFile()) {
                try {
                    FileUtils.moveFile(toMove[i], toHere);
                } catch (IOException e) {

                    e.printStackTrace();
                }
            } else {
                try {
                    FileUtils.moveDirectory(toMove[i], toHere);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Moves the files specified.
     *
     * @param listOfFilesToMove String array of file names to move
     * @param storage           Folder to store files in.
     */
    public static void moveFilesTo(final String[] listOfFilesToMove, final File storage) {

        File home = new File(".");
        File[] files = home.listFiles();
        Arrays.sort(files, new Comparator<File>() {

            public int compare(final File f1, final File f2) {

                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        if (!storage.exists()) {
            System.err.println("folder already exists: " + storage.getAbsolutePath());
        }

        if (!storage.mkdirs()) {
            System.err.println("Problem creating folder " + storage.getAbsolutePath());
        }

        File[] toMove = new File[listOfFilesToMove.length];

        for (int i = 0; i < listOfFilesToMove.length; i++) {
            toMove[i] = new File(listOfFilesToMove[i]);
        }

        moveFiles(toMove, storage);

        for (int i = 0; i < toMove.length; i++) {
            Utils.deleteDirectory(toMove[i]);
        }

        File[] otherFilesToMove = new File[1];
        File sgdModel = new File("crossFoldModel/crossFoldModel.model");
        otherFilesToMove[0] = sgdModel;
        moveFiles(otherFilesToMove, storage);

    }

    /**
     * Returns a common regex that catchs commas when not surrounded by quotation marks. This is useful for parsing CSV
     * files.
     *
     * @return String regex ",\\s*(?=([^\"]*\"[^\"]*\")*[^\"]*$)"
     */
    public static String getCSVComma() {

        return ",\\s*(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    }

    /**
     * Writes a string to a file. Can append or overwrite.
     *
     * @param content  String to write.
     * @param fileName Name of file to write to.
     * @param append   Append.
     */
    public static void writeToFile(final String content, final String fileName, final boolean append) {

        FileWriterWithEncoding fstream = null;

        try {
            fstream = new FileWriterWithEncoding(fileName, Charset.forName("UTF-8"), append);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(content);
            // Close the output stream
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes given string to the given file name. Handles opening and closing of buffered writers etc.
     *
     * @param lloutput output String to be written to file.
     * @param filename the name of the file to write to.
     */
    public static void writeToFile(final String lloutput, final String filename) {

        FileWriterWithEncoding fstream;
        try {
            fstream = new FileWriterWithEncoding(filename, Charset.forName("UTF-8"));
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(lloutput);
            out.flush();
            // Close the output stream
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * writes a comma seperated 2D array to file.
     *
     * @param data Date to write.
     * @param name Name of file to write to.
     */
    public static void writeToFile(final int[][] data, final String name) {

        FileWriterWithEncoding fstream;
        try {
            fstream = new FileWriterWithEncoding(name, Charset.forName("UTF-8"), false);
            BufferedWriter out = new BufferedWriter(fstream);

            for (int i = 0; i < data.length; i++) {
                out.write(i + ",");
                for (int j = 0; j < data[i].length; j++) {
                    out.write(data[i][j] + ",");
                }
                out.write("\n");
            }

            // Close the output stream
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Returns the number of lines in a file.
     *
     * @param file the File to read.
     * @return number of lines.
     * @throws IOException Will throw is we can't din the file.
     */
    @SuppressFBWarnings(value = "RV_DONT_JUST_NULL_CHECK_READLINE")
    public static int getNumberOfLines(final File file) throws IOException {

        try (BufferedReader reader = java.nio.file.Files.newBufferedReader(Paths.get(file.getAbsolutePath()), FileManipulation.FILE_CHARSET)) {

            int lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }

    /**
     * Deletes the specified directory.
     *
     * @param directory to delete.
     * @return True is deleted. False if there is a problem.
     */
    public static boolean deleteDirectory(final File directory) {

        try {
            FileManipulation.deleteDirectory(directory.getAbsolutePath());
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    /**
     * Count the number of files in a directory.
     *
     * @param dirPath the directory path to count files in
     * @param count   the starting count
     * @return int number of files in directroy
     */
    public static int countFiles(final String dirPath, int count) {

        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    count++;
                    count = countFiles(file.getAbsolutePath(), count);
                } else {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Sorts a map that implements Comparable from biggest to smallest.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map
     * @return a sorted map in descending order
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(final Map<K, V> map) {

        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {

            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {

                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Sorts a map that implements Comparable from biggest to smallest.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map
     * @return a sorted map in descending order
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueAscending(final Map<K, V> map) {

        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {

            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {

                return o1.getValue().compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Performs a check to make sure that the testing split start position is compatible with the size of the set.
     * Reduces start position if not.
     *
     * @param startPosition    start position
     * @param percentTestFiles size of test split
     * @return start position if OK, new start position if moved.
     */
    public static int checkStartPosition(final int startPosition, final int percentTestFiles) {

        if (startPosition <= (100 - percentTestFiles)) {
            return startPosition;
        } else {
            return startPosition - (startPosition % (100 - percentTestFiles));
        }

    }

    /**
     * Closes a reader.
     *
     * @param bufferedReader reader to close.
     */
    public static void closeReader(final BufferedReader bufferedReader) {

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a bucket to a file (target/NRSoutput.txt) in the NRS output format.
     *
     * @param bucketToWrite Bucket to write to file
     * @throws IOException Indicates I/O error
     */
    public static void writeBucketToFileNrsFormat(final Bucket bucketToWrite) throws IOException {

        DataClerkingWriter writer = new DataClerkingWriter(new File("target/NRSOutput.txt"));

        for (Record record : bucketToWrite) {
            writer.write(record);
        }
        writer.close();
        System.out.println(bucketToWrite);
    }

}
