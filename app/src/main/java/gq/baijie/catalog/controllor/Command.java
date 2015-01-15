package gq.baijie.catalog.controllor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import gq.baijie.catalog.entity.FileInformation;
import gq.baijie.catalog.util.Scanner;
import gq.baijie.catalog.util.TreeNode;

public abstract class Command implements Runnable {

    protected String mAlgorithm = "MD5";

    protected Path mDirectory;

    public static Command newInstance(CommandType commandType) {
        switch (commandType) {
            case SCAN:
                return new ScanCommand();
            case VERIFY:
                return new VerifyCommand();
            default:
                throw new IllegalArgumentException("Unknown command type:" + commandType);
        }
    }

    public Command setAlgorithm(String algorithm) {
        switch (algorithm) {
            case "MD5":
                mAlgorithm = "MD5";
                break;
            case "SHA1":
                mAlgorithm = "SHA-1";
                break;
            case "SHA256":
                mAlgorithm = "SHA-256";
                break;
            default:
                throw new IllegalArgumentException("Unknown Hash Algorithm:" + algorithm);
        }
        return this;
    }

    public Command setDirectory(String directory) {
        mDirectory = Paths.get(directory);
        return this;
    }

    @Override
    public abstract void run();

    protected MessageDigest getMessageDigest() {
        assertArguments();
        try {
            return MessageDigest.getInstance(mAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }

    protected Path getDirectory() {
        assertArguments();
        return mDirectory;
    }

    protected Path getFile() {
        assertArguments();
        return mDirectory.resolve(mDirectory.getFileName() + ".txt");
    }

    protected void assertArguments() {
        if (mAlgorithm == null || mDirectory == null) {
            throw new IllegalStateException("haven't set arguments.");
        }
    }

    public static enum CommandType {
        SCAN,
        VERIFY
    }

    private static class ScanCommand extends Command {

        @Override
        public void run() {
            Path directory = getDirectory();
            Path file = getFile();
            try {
                TreeNode<FileInformation> result = FilesScanner.walk(directory);
                FilesScanner.hashFiles(result, getMessageDigest());
                Files.write(
                        file,
                        Collections.singletonList(Exporter.exportTxtFile(result)),
                        StandardOpenOption.CREATE_NEW);
            } catch (FileAlreadyExistsException e) {
                System.err.printf("File \"%s\" already exists!%n", file);
            } catch (IOException e) {
                System.err.println("encounter IOException!");
                e.printStackTrace();
            }
        }
    }

    private static class VerifyCommand extends Command {

        @Override
        public Command setAlgorithm(String algorithm) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void run() {
            Path directory = getDirectory();
            Path file = getFile();
            try {
                byte[] allBytes = Files.readAllBytes(file);
                String input = new String(allBytes, StandardCharsets.UTF_8);
                Importer importer = new Importer().setSource(input);
                TreeNode<FileInformation> directoryTree =
                        Scanner.fromHashTable(importer.getHashTable(), directory);
                if (FilesScanner.verifyFiles(directoryTree)) {
                    System.out.println("All files are verified successfully.");
                } else {
                    System.out.println("Verify Failure!");
                }
            } catch (IOException e) {
                System.err.println("encounter IOException!");
                e.printStackTrace();
            }
        }
    }
}
