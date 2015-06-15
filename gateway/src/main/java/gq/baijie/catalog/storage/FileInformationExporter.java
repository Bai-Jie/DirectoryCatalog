package gq.baijie.catalog.storage;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.File;

public interface FileInformationExporter {

    public void exportFileInformation(@Nonnull File fileInformation);

}
