package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Finder extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;
    private FileProcessor fileProcesser;
    private int numMatches = 0;

    Finder(final String pattern, final FileProcessor fileProcesser) {

        this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        this.fileProcesser = fileProcesser;
    }

    // Compares the glob pattern against
    // the file or directory name.
    void find(final Path file) throws IOException {

        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            numMatches++;
            System.out.println(file);
            fileProcesser.process(file);
        }
    }

    void done() {

        System.out.println("Matched: " + numMatches);
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {

        find(file);
        return CONTINUE;
    }

    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {

        find(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file, final IOException exc) {

        System.err.println(exc);
        return CONTINUE;
    }
}
