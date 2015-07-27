package gq.baijie.catalog.storage.v1;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.storage.FileInformationImporter;
import gq.baijie.catalog.storage.v1.util.FileUtils;

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
        simpleImporter.parse();
        final List<File> files = new LinkedList<>();
        for (SimpleImporter.HashTable hashTable : simpleImporter.getHashTables()) {
            files.add(Scanner.fromHashTable(hashTable.getHashTable(), root));
        }
        if (files.isEmpty()) {
            throw new UnsupportedOperationException("No HashTable Unsupported");//TODO
        }
        return FileUtils.mergeFiles(files.toArray(new File[files.size()]));
    }

}
