package gq.baijie.catalog.entity;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class DirectoryFile extends File {

    @Nonnull
    private final List<File> content = new LinkedList<>();

    public DirectoryFile(Path path) {
        super(path);
    }

    @Nonnull
    public List<File> getContent() {
        return content;
    }

}
