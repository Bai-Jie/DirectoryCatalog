package gq.baijie.catalog.util;

import org.junit.Test;

import java.io.IOException;

import gq.baijie.catalog.controllor.FilesScanner;
import gq.baijie.catalog.entity.FileInformation;

import static gq.baijie.catalog.test.util.Constant.DIRECTORY_A;
import static gq.baijie.catalog.test.util.Constant.DIRECTORY_CHANGED_A;
import static gq.baijie.catalog.test.util.Constant.DIRECTORY_ONE_FILE_CHANGED_A;
import static gq.baijie.catalog.test.util.Constant.DIRECTORY_SAME_TO_A;
import static gq.baijie.catalog.test.util.Utils.getPath;

//TODO test empty directory
public class PrinterTest {

    @Test
    public void testRenderDirectoryTree1() {
        printDirectoryTree(DIRECTORY_A);
    }

    @Test
    public void testRenderDirectoryTree2() {
        printDirectoryTree(DIRECTORY_CHANGED_A);
    }

    @Test
    public void testRenderDirectoryTree3() {
        printDirectoryTree(DIRECTORY_ONE_FILE_CHANGED_A);
    }

    @Test
    public void testRenderDirectoryTree4() {
        printDirectoryTree(DIRECTORY_SAME_TO_A);
    }

    private void printDirectoryTree(String directory) {
        TreeNode<FileInformation> a;
        try {
            a = FilesScanner.walk(getPath(directory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String directoryTree = new Printer().setDirectoryTree(a).renderDirectoryTree();
        System.out.println(directoryTree);
    }

}
