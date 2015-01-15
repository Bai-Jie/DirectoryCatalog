package gq.baijie.catalog.entity;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import gq.baijie.catalog.util.HEX;

public class FileInformation {

    private Path path;

    private BasicFileAttributes attributes;

    private boolean isDirectory;

    private byte[] hash;

    public Path getPath() {
        return path;
    }

    public FileInformation setPath(Path path) {
        this.path = path;
        return this;
    }

    public BasicFileAttributes getAttributes() {
        return attributes;
    }

    public FileInformation setAttributes(BasicFileAttributes attributes) {
        this.attributes = attributes;
        return this;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public FileInformation setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
        return this;
    }

    public byte[] getHash() {
        return Arrays.copyOf(hash, hash.length);
    }

    public String getHashAsHex() {
        return hash != null ? HEX.bytesToHex(hash) : null;
    }

    public FileInformation setHash(byte[] hash) {
        this.hash = Arrays.copyOf(hash, hash.length);
        return this;
    }

    @Override
    public String toString() {
        return "FileInformation{" +
                "path=" + path +
                ", attributes=" + attributes +
                ", isDirectory=" + isDirectory +
                ", hash=" + getHashAsHex() +
                '}';
    }
}
