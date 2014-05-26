package uk.ac.standrews.cs.digitising_scotland.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by al on 11/05/2014.
 */
public class FileManipulation {

    public static final Charset FILE_CHARSET = Charset.forName("UTF-8");

    public static void deleteDirectory1(String directory_path) throws IOException {

        File directory = new File(directory_path);

        File[] files = directory.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory1(f.getAbsolutePath());
                } else {
                    if (!f.delete()) throw new IOException("Could not delete file: " + f.getAbsolutePath());
                }
            }
        }

        if (!directory.delete()) throw new IOException("Could not delete directory: " + directory.getAbsolutePath());
    }

    public static void deleteDirectory2(String directory_path) throws IOException {

        delete(new File(directory_path));
    }

    private static void delete(File file) throws IOException {

        if (file.isDirectory()) {
            for (File deletee : file.listFiles()) {
                delete(deletee);
            }
        }
        if (!file.delete()) {
            throw new IOException("Could not delete file/directory: " + file.getAbsolutePath());
        }
    }

    public static void deleteDirectory3(String directory_path) throws IOException {

        Path directory = Paths.get(directory_path);

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void deleteDirectory4(String directory_path) throws IOException {

        Path directory = Paths.get(directory_path);

        delete(directory);
    }

    private static void delete(Path directory_path) throws IOException {

        List<Path> entries = getEntries(directory_path);

        for (Path entry : entries) {
            if (Files.isDirectory(entry)) {
                delete(entry);
            } else {
                Files.delete(entry);
            }
        }

        Files.delete(directory_path);
    }

    public static void createDirectoryIfDoesNotExist(String directory_path) throws IOException {

        createDirectoryIfDoesNotExist(new File(directory_path));
    }

    public static void createDirectoryIfDoesNotExist(File directory) throws IOException {

        createDirectoryIfDoesNotExist(Paths.get(directory.getAbsolutePath()));
    }

    public static void createDirectoryIfDoesNotExist(Path path) throws IOException {

        Files.createDirectories(path);
    }

    public static void createParentDirectoryIfDoesNotExist(Path path) throws IOException {

        Path parent_dir = path.getParent();
        if (parent_dir != null) createDirectoryIfDoesNotExist(parent_dir);
    }

    private static List<Path> getEntries(Path dir) throws IOException {

        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                result.add(entry);
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
        return result;
    }
}
