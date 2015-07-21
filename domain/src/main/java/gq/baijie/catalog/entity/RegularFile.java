package gq.baijie.catalog.entity;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

public class RegularFile extends File {

    @Nonnull
    private List<Hash> hashs = new LinkedList<>();

    public RegularFile(Path path) {
        super(path);
    }

    ////////////////////////////////////////////////////////////////////////////
    // getters and setters
    ////////////////////////////////////////////////////////////////////////////

    @Nonnull
    public List<Hash> getHashs() {
        return hashs;
    }

    ////////////////////////////////////////////////////////////////////////////
    // overridden Object methods
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 31)
                .appendSuper(super.hashCode())
                .append(getHashs())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        RegularFile rightHand = (RegularFile) obj;
        return Objects.equals(getHashs(), rightHand.getHashs());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("hashes", getHashs())
                .toString();
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
