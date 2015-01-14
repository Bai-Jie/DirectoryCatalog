package gq.baijie.catalog.controllor;

import gq.baijie.catalog.entity.FileInformation;
import gq.baijie.catalog.util.Printer;
import gq.baijie.catalog.util.TreeNode;

// format:
/*
Directory Tree
================
[output of Printer.renderDirectoryTree]

Hash Table
================
[output of Printer.printHash]

 */
public class Exporter {

    static final String UNDERLINED = "================";

    static final String HEAD_DIRECTORY_TREE = "Directory Tree";

    static final String HEAD_HASH_TABLE = "Hash Table";

    public static String exportTxtFile(TreeNode<FileInformation> data) {
        return exportTxtFile(data, System.getProperty("line.separator"));
    }

    public static String exportTxtFile(TreeNode<FileInformation> data, String lineBreak) {
        StringBuilder stringBuilder = new StringBuilder();
        Printer printer = new Printer().setLineBreak(lineBreak).setDirectoryTree(data);
        // export Directory Tree
        stringBuilder.append(HEAD_DIRECTORY_TREE).append(lineBreak);
        stringBuilder.append(UNDERLINED).append(lineBreak);
        stringBuilder.append(printer.renderDirectoryTree());
        stringBuilder.append(lineBreak);
        // export Hash Table
        stringBuilder.append(HEAD_HASH_TABLE).append(lineBreak);
        stringBuilder.append(UNDERLINED).append(lineBreak);
        stringBuilder.append(printer.printHash());
        stringBuilder.append(lineBreak);
        return stringBuilder.toString();
    }

}
