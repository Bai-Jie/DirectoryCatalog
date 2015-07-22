package gq.baijie.catalog.storage.v1;


import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.storage.FileInformationExporter;
import gq.baijie.catalog.storage.v1.util.FileUtils;

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
        Hash.Algorithm[] usedAlgorithms = FileUtils.getUsedAlgorithms(data);
        if (usedAlgorithms.length > 1) {//TODO
            throw new UnsupportedOperationException("unsupported multiple Algorithms just now");
        } else if (usedAlgorithms.length == 1) {
            stringBuilder.append(printer.printHash(usedAlgorithms[0]));
        } else {
            stringBuilder.append(printer.printHash(Hash.Algorithm.MD5));
        }
        stringBuilder.append(lineBreak);
        return stringBuilder.toString();
    }

    @Override
    public void exportFileInformation(@Nonnull File fileInformation) {
        exportTxtFile(fileInformation);
    }

}
