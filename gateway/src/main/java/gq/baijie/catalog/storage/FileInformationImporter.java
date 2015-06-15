package gq.baijie.catalog.storage;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.File;

public interface FileInformationImporter {

    @Nonnull
    public File importFileInformation();

}
