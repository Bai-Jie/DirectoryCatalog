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
import gq.baijie.catalog.util.TreeNode;

public class Main {

    public static void main(String[] args) throws IOException {
        final String time = String
                .format("%1$ty%1$tm%1$td%1$tH%1$tM%1$tS", System.currentTimeMillis());
        Path testOutfile = Paths.get("..", "temp", time + ".txt");
        Files.createDirectories(testOutfile.getParent());

        try (PrintStream testOutStream =
                     new PrintStream(testOutfile.toFile(), StandardCharsets.UTF_8.name())) {
            TreeNode<FileInformation> tree = FilesScanner.walk(Paths.get("."));
            FilesScanner.hashFiles(tree, MessageDigest.getInstance("MD5"));
            Printer printer = new Printer().setDirectoryTree(tree);
            testOutStream.println("---------------------- hash ----------------------");
            testOutStream.println(printer.printHash());
            testOutStream.println("---------------------- directory tree 1 ----------------------");
            testOutStream.println(printer.renderDirectoryTree());
            testOutStream.println("---------------------- directory tree 2 ----------------------");
            testOutStream.println(DirectoryTreeRender.newInstance(false).renderDirectoryTree(tree));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
