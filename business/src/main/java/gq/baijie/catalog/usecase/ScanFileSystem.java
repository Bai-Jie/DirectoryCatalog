package gq.baijie.catalog.usecase;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import gq.baijie.catalog.entity.DirectoryFile;
import gq.baijie.catalog.entity.RegularFile;

import static java.nio.file.FileVisitResult.CONTINUE;

public class ScanFileSystem implements UseCase {

    private final DirectoryFile rootDirectoryFile;

    public ScanFileSystem(DirectoryFile rootDirectoryFile) {
        this.rootDirectoryFile = rootDirectoryFile;
    }

    @Override
    public void execute() {
        try {
            execute0();
        } catch (IOException e) {
            throw new RuntimeException(
                    "can't scan " + rootDirectoryFile.getPath() + " directory on the file system.");
        }
    }

    private void execute0() throws IOException {
        Files.walkFileTree(
                rootDirectoryFile.getPath(), new DirectoryFileVisitor(rootDirectoryFile));
    }

    private static class DirectoryFileVisitor implements FileVisitor<Path> {

        private DirectoryFile mCurrentDirectory;

        public DirectoryFileVisitor(DirectoryFile root) {
            mCurrentDirectory = root;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
            final DirectoryFile subDirectory = new DirectoryFile(dir);
            mCurrentDirectory.getContent().add(subDirectory);
            mCurrentDirectory = subDirectory;
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            mCurrentDirectory.getContent().add(new RegularFile(file));
            return CONTINUE;
        }

        // Print each directory visited.
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            mCurrentDirectory = mCurrentDirectory.getParent();
            return CONTINUE;
        }

        // If there is some error accessing
        // the file, let the user know.
        // If you don't override this method
        // and an error occurs, an IOException
        // is thrown.
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            System.err.println(String.format("visitFileFailed(%s, %s)", file, exc));
            exc.printStackTrace();
            return CONTINUE;
        }
    }

}
