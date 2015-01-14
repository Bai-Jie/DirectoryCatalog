package gq.baijie.catalog.controllor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import gq.baijie.catalog.entity.FileInformation;
import gq.baijie.catalog.util.Printer;
import gq.baijie.catalog.util.Scanner;
import gq.baijie.catalog.util.TreeNode;

import static gq.baijie.catalog.test.util.Constant.DIRECTORY_A;
import static gq.baijie.catalog.test.util.Constant.DIRECTORY_CHANGED_A;
import static gq.baijie.catalog.test.util.Constant.DIRECTORY_ONE_FILE_CHANGED_A;
import static gq.baijie.catalog.test.util.Constant.DIRECTORY_SAME_TO_A;
import static gq.baijie.catalog.test.util.Utils.getPath;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilesScannerTest {

    private static MessageDigest sMessageDigestMD5;

    private static MessageDigest sMessageDigestSHA1;

    private static MessageDigest sMessageDigestSHA256;

    @BeforeClass
    public static void initialize() {
        try {
            sMessageDigestMD5 = MessageDigest.getInstance("MD5");
            sMessageDigestSHA1 = MessageDigest.getInstance("SHA-1");
            sMessageDigestSHA256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void reset() {
        sMessageDigestMD5.reset();
        sMessageDigestSHA1.reset();
        sMessageDigestSHA256.reset();
    }

    @Test
    public void testVerifyFiles1() throws IOException {
        Path rootPath = getPath(DIRECTORY_A);
        TreeNode<FileInformation> a = FilesScanner.walk(rootPath);
        FilesScanner.hashFiles(a, sMessageDigestMD5);
        String hashTable = new Printer().setDirectoryTree(a).printHash();
        TreeNode<FileInformation> fromHashTable = Scanner.fromHashTable(hashTable, rootPath);
        assertTrue(FilesScanner.verifyFiles(fromHashTable, sMessageDigestMD5));
        System.out.println("-------------------- testVerifyFiles1 --------------------");
        System.out.println(hashTable);
    }

    @Test
    public void testVerifyFiles2() throws IOException {
        TreeNode<FileInformation> sameToA = FilesScanner.walk(getPath(DIRECTORY_SAME_TO_A));
        FilesScanner.hashFiles(sameToA, sMessageDigestMD5);
        String hashTable = new Printer().setDirectoryTree(sameToA).printHash();
        TreeNode<FileInformation> fromHashTable =
                Scanner.fromHashTable(hashTable, getPath(DIRECTORY_A));
        assertTrue(FilesScanner.verifyFiles(fromHashTable, sMessageDigestMD5));
        System.out.println("-------------------- testVerifyFiles2 --------------------");
        System.out.println(hashTable);
    }

    @Test
    public void testVerifyFiles3() throws IOException {
        TreeNode<FileInformation> changedA = FilesScanner
                .walk(getPath(DIRECTORY_ONE_FILE_CHANGED_A));
        FilesScanner.hashFiles(changedA, sMessageDigestMD5);
        String hashTable = new Printer().setDirectoryTree(changedA).printHash();
        TreeNode<FileInformation> fromHashTable =
                Scanner.fromHashTable(hashTable, getPath(DIRECTORY_A));
        assertFalse(FilesScanner.verifyFiles(fromHashTable, sMessageDigestMD5));
        System.out.println("-------------------- testVerifyFiles3 --------------------");
        System.out.println(hashTable);
    }

    @Test
    public void testVerifyFiles4() throws IOException {
        TreeNode<FileInformation> changedA = FilesScanner.walk(getPath(DIRECTORY_CHANGED_A));
        FilesScanner.hashFiles(changedA, sMessageDigestMD5);
        String hashTable = new Printer().setDirectoryTree(changedA).printHash();
        TreeNode<FileInformation> fromHashTable =
                Scanner.fromHashTable(hashTable, getPath(DIRECTORY_A));
        assertFalse(FilesScanner.verifyFiles(fromHashTable, sMessageDigestMD5));
        System.out.println("-------------------- testVerifyFiles4 --------------------");
        System.out.println(hashTable);
    }
}
