package gq.baijie.catalog;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import gq.baijie.catalog.controllor.FilesScanner;
import gq.baijie.catalog.entity.FileInformation;
import gq.baijie.catalog.util.DirectoryTreeRender;
import gq.baijie.catalog.util.Printer;
import gq.baijie.catalog.util.Scanner;
import gq.baijie.catalog.util.TreeNode;

public class Main {

    public static void main(String[] args) throws IOException {
        final String time = String
                .format("%1$ty%1$tm%1$td%1$tH%1$tM%1$tS", System.currentTimeMillis());
        Path testOutfile = Paths.get("..", "temp", time + ".txt");
        Files.createDirectories(testOutfile.getParent());

        try (PrintStream testOutStream =
                     new PrintStream(testOutfile.toFile(), StandardCharsets.UTF_8.name())) {
            Path rootPath = Paths.get(".");
            TreeNode<FileInformation> tree = FilesScanner.walk(rootPath);
            FilesScanner.hashFiles(tree, MessageDigest.getInstance("MD5"));
            Printer printer = new Printer().setDirectoryTree(tree);
            testOutStream.println("---------------------- hash ----------------------");
            String hashTable = printer.printHash();
            testOutStream.println(hashTable);
            testOutStream.println("---------------------- directory tree 1 ----------------------");
            testOutStream.println(printer.renderDirectoryTree());
            testOutStream.println("---------------------- directory tree 2 ----------------------");
            testOutStream.println(DirectoryTreeRender.newInstance(false).renderDirectoryTree(tree));
            testOutStream.println("-------------------- tree from HashTable --------------------");
            TreeNode<FileInformation> treeFromHashTable =
                    Scanner.fromHashTable(hashTable, rootPath);
            String hashTableOfParsed = printer.setDirectoryTree(treeFromHashTable).printHash();
            testOutStream.println(hashTableOfParsed);
            if (hashTable.equals(hashTableOfParsed)) {
                System.out.println("hashTable.equals(hashTableOfParsed)");
            } else {
                System.err.println("*not* hashTable.equals(hashTableOfParsed)");
            }
            if (FilesScanner.verifyFiles(treeFromHashTable, MessageDigest.getInstance("MD5"))) {
                System.out.println("FilesScanner.verifyFiles() == true");
            } else {
                System.err.println("FilesScanner.verifyFiles() == *false*");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
