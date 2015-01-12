package gq.baijie.catalog.controllor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;

import gq.baijie.catalog.entity.FileInformation;
import gq.baijie.catalog.util.Hash;
import gq.baijie.catalog.util.TreeNode;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FilesScanner {


    public static TreeNode<FileInformation> walk(Path start) throws IOException {
        TreeNode<FileInformation> root = new TreeNode<>(new FileInformation());
        Files.walkFileTree(start, new MyFileVisitor(root));
        return root.getChildren().get(0);
    }

    public static void hashFiles(
            TreeNode<FileInformation> tree, MessageDigest messageDigest) throws IOException {
        FileInformation fileInformation = tree.getData();
        if (fileInformation.isDirectory()) {
            for (TreeNode<FileInformation> subtree : tree.getChildren()) {
                hashFiles(subtree, messageDigest);
            }
        } else {
            fileInformation.setHash(Hash.hashFile(fileInformation.getPath(), messageDigest));
        }
    }


    private static class MyFileVisitor implements FileVisitor<Path> {

        private TreeNode<FileInformation> mCurrentDirectory;

        public MyFileVisitor(TreeNode<FileInformation> root) {
            mCurrentDirectory = root;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
            mCurrentDirectory = mCurrentDirectory.addChild(new FileInformation()
                    .setPath(dir)
                    .setAttributes(attrs)
                    .setDirectory(true));
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            mCurrentDirectory.addChild(new FileInformation()
                    .setPath(file)
                    .setAttributes(attrs)
                    .setDirectory(false));
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
