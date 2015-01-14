package gq.baijie.catalog;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import gq.baijie.catalog.controllor.Exporter;
import gq.baijie.catalog.controllor.FilesScanner;
import gq.baijie.catalog.controllor.Importer;
import gq.baijie.catalog.entity.FileInformation;
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
            String exported = Exporter.exportTxtFile(tree);
            testOutStream.print(exported);
            Importer importer = new Importer().setSource(exported);
            testOutStream.println("------------------ exported directory tree ------------------");
            testOutStream.println(importer.getDirectoryTree());
            testOutStream.println("-------------------- exported hash table --------------------");
            testOutStream.println(importer.getHashTable());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
