/*
 * Copyright 2015 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module util.
 *
 * util is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * util is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with util. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.standrews.cs.storr.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class provides a set of utilities function over the file system to manage
 * files and directories.
 *
 * Created by al on 11/05/2014.
 */
public class FileManipulation {

    public static final Charset FILE_CHARSET = Charset.forName("UTF-8");

    /**
     * Delete the specified directory recursively
     * @param directory_path
     * @throws IOException
     */
    public static void deleteDirectory(final String directory_path) throws IOException {

        deleteDirectory(Paths.get(directory_path));
    }

    /**
     * Delete the specified directory recursively
     * @param directory
     * @throws IOException
     */
    public static void deleteDirectory(final Path directory) throws IOException {

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {

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

    public static void createDirectoryIfDoesNotExist(final String directory_path) throws IOException {

        createDirectoryIfDoesNotExist(new File(directory_path));
    }

    public static void createDirectoryIfDoesNotExist(final File directory) throws IOException {

        createDirectoryIfDoesNotExist(Paths.get(directory.getAbsolutePath()));
    }

    public static void createDirectoryIfDoesNotExist(final Path path) throws IOException {

        Files.createDirectories(path);
    }

    public static void createParentDirectoryIfDoesNotExist(final Path path) throws IOException {

        Path parent_dir = path.getParent();
        if (parent_dir != null) {
            createDirectoryIfDoesNotExist(parent_dir);
        }
    }

    public static List<Path> getDirectoryEntries(final Path directory) throws IOException {

        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {

            for (Path entry : stream) {
                result.add(entry);
            }
        } catch (DirectoryIteratorException e) {
            throw new IOException(e);
        }
        return result;
    }

    public static void assertThatFilesHaveSameContent(final Path path1, final Path path2) throws IOException {

        try (BufferedReader reader1 = Files.newBufferedReader(path1, FILE_CHARSET);
             BufferedReader reader2 = Files.newBufferedReader(path2, FILE_CHARSET)) {

            String line1;
            while ((line1 = reader1.readLine()) != null) {
                final String line2 = reader2.readLine();
                assertEquals(line1, line2);
            }
            assertNull(reader2.readLine());
        }
    }
}
