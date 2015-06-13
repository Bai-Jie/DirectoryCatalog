package gq.baijie.catalog.entity;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class RegularFile extends File {

    @Nonnull
    private final List<Hash> hashs = new LinkedList<>();

    public RegularFile(Path path) {
        super(path);
    }

    @Nonnull
    public List<Hash> getHashs() {
        return hashs;
    }

}
