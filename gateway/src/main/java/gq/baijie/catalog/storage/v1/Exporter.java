package gq.baijie.catalog.storage.v1;


import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.storage.FileInformationExporter;

// format:
/*
Directory Tree
================
[output of Printer.renderDirectoryTree]

Hash Table
================
[output of Printer.printHash]

 */
public class Exporter implements FileInformationExporter {

    static final String UNDERLINED = "================";

    static final String HEAD_DIRECTORY_TREE = "Directory Tree";

    static final String HEAD_HASH_TABLE = "Hash Table";

    @Nonnull
    private final StringBuilder stringBuilder;

    @Nonnull
    private String lineBreak = System.getProperty("line.separator");

    public Exporter(@Nonnull StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    public void setLineBreak(@Nonnull String lineBreak) {
        this.lineBreak = lineBreak;
    }

    public String exportTxtFile(File data) {
        Printer printer = new Printer().setLineBreak(lineBreak).setFile(data);
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

    @Override
    public void exportFileInformation(@Nonnull File fileInformation) {
        exportTxtFile(fileInformation);
    }

}
