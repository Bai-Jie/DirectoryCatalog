package gq.baijie.catalog.controller;

import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import gq.baijie.catalog.entity.DirectoryFile;
import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.storage.FileInformationExporter;
import gq.baijie.catalog.storage.FileInformationImporter;
import gq.baijie.catalog.usecase.CheckFileInformation;
import gq.baijie.catalog.usecase.GatherFileInformation;

public class Session {

    @Nullable
    private DirectoryFile rootDirectoryFile; //TODO support File

    public void gatherFileInformation(@Nonnull Path rootDirectory,
            @Nonnull Hash.Algorithm[] algorithms) {
        final DirectoryFile rootDirectoryFile = new DirectoryFile(rootDirectory);
        new GatherFileInformation(rootDirectoryFile, algorithms).execute();
        this.rootDirectoryFile = rootDirectoryFile;
    }

    public void checkFileInformation(
            @Nonnull CheckFileInformation.FileCheckerListener fileCheckerListener) {
        ensureRootDirectoryFile();
        assert rootDirectoryFile != null;
        new CheckFileInformation(rootDirectoryFile, fileCheckerListener).execute();
    }

    public void exportFileInformation(@Nonnull FileInformationExporter exporter) {
        ensureRootDirectoryFile();
        assert rootDirectoryFile != null;
        exporter.exportFileInformation(rootDirectoryFile);
    }

    public void importFileInformation(@Nonnull FileInformationImporter importer) {
        final File fileInformation = importer.importFileInformation();
        if (fileInformation instanceof DirectoryFile) {
            rootDirectoryFile = (DirectoryFile) fileInformation;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void ensureRootDirectoryFile() {
        if (rootDirectoryFile == null) {
            throw new IllegalStateException(
                    "Haven't set target file. Please import or gather file information first.");
        }
    }

}
