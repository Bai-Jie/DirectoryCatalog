package gq.baijie.catalog.entity;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class RegularFile extends File implements Cloneable {

    @Nonnull
    private List<Hash> hashs = new LinkedList<>();

    public RegularFile(Path path) {
        super(path);
    }

    @Nonnull
    public List<Hash> getHashs() {
        return hashs;
    }

    @Nonnull
    @Override
    public RegularFile clone() throws CloneNotSupportedException {
        RegularFile clone = (RegularFile) super.clone();
        clone.hashs = new LinkedList<>();
        clone.hashs.addAll(hashs);
        return clone;
    }

}
