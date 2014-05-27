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

    public static void deleteDirectory(String directory_path) throws IOException {

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

    public static void createFileIfDoesNotExist(final Path path) throws IOException {

        if (!Files.exists(path)) {

            createParentDirectoryIfDoesNotExist(path);
            Files.createFile(path);
        }
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

    public static List<Path> getDirectoryEntries(Path directory) throws IOException {

        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                result.add(entry);
            }
        } catch (DirectoryIteratorException e) {
            throw e.getCause();
        }
        return result;
    }
}
