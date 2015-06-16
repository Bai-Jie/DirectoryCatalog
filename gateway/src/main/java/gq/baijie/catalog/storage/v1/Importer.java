package gq.baijie.catalog.storage.v1;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.storage.FileInformationImporter;

public class Importer implements FileInformationImporter {

    @Nonnull
    private final String source;

    @Nonnull
    private final Path root;

    public Importer(@Nonnull String source, @Nonnull Path root) {
        this.source = source;
        this.root = root;
    }

    @Nonnull
    @Override
    public File importFileInformation() {
        SimpleImporter simpleImporter = new SimpleImporter(source);
        return Scanner.fromHashTable(simpleImporter.getHashTable(), root);
    }

}
