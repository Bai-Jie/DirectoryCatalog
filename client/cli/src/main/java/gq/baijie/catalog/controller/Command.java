package gq.baijie.catalog.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Locale;

import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.storage.v1.Exporter;
import gq.baijie.catalog.storage.v1.Importer;

public abstract class Command implements Runnable {

    protected Hash.Algorithm mAlgorithm = Hash.Algorithm.MD5;

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
        algorithm = algorithm.toUpperCase(Locale.US);
        switch (algorithm) {
            case "MD5":
                mAlgorithm = Hash.Algorithm.MD5;
                break;
            case "SHA1":
                mAlgorithm = Hash.Algorithm.SHA1;
                break;
            case "SHA256":
                mAlgorithm = Hash.Algorithm.SHA256;
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
                final StringBuilder stringBuilder = new StringBuilder();
                final Session session = new Session();
                session.gatherFileInformation(directory, new Hash.Algorithm[]{mAlgorithm});
                session.exportFileInformation(new Exporter(stringBuilder));
                Files.write(
                        file,
                        Collections.singletonList(stringBuilder.toString()),
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
                final byte[] allBytes = Files.readAllBytes(file);
                final String input = new String(allBytes, StandardCharsets.UTF_8);
                final Session session = new Session();
                final FileCheckerListener fileCheckerListener = new FileCheckerListener(true);
                session.importFileInformation(new Importer(input, directory));
                session.checkFileInformation(fileCheckerListener);
                if (fileCheckerListener.isAllFileOk()) {
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
