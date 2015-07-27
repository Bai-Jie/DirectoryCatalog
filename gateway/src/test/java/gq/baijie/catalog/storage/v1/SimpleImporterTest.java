package gq.baijie.catalog.storage.v1;


import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import gq.baijie.catalog.entity.Hash;

public class SimpleImporterTest {

    private static final String SAMPLE;

    private static final String SAMPLE_DIRECTORY_TREE;

    private static final String SAMPLE_HASH_TABLE_MD5;

    private static final String SAMPLE_HASH_TABLE_SHA1;

    private static final String SAMPLE_HASH_TABLE_SHA256;

    static {
        try {
            SAMPLE = loadTextFile("sample.txt");
            SAMPLE_DIRECTORY_TREE = loadTextFile("sample_directory_tree.txt");
            SAMPLE_HASH_TABLE_MD5 = loadTextFile("sample_hash_table_md5.txt");
            SAMPLE_HASH_TABLE_SHA1 = loadTextFile("sample_hash_table_sha1.txt");
            SAMPLE_HASH_TABLE_SHA256 = loadTextFile("sample_hash_table_sha256.txt");
        } catch (IOException | URISyntaxException e) {
            throw new Error(e);
        }
    }

    @Test
    public void test() {
        final SimpleImporter simpleImporter = new SimpleImporter(SAMPLE);
        simpleImporter.parse();

        Assert.assertEquals(SAMPLE_DIRECTORY_TREE, simpleImporter.getDirectoryTree());
        final List<SimpleImporter.HashTable> hashTables = simpleImporter.getHashTables();
        Assert.assertEquals(3, hashTables.size());
        Assert.assertEquals(Hash.Algorithm.MD5.toString(), hashTables.get(0).getAlgorithm());
        Assert.assertEquals(SAMPLE_HASH_TABLE_MD5, hashTables.get(0).getHashTable());
        Assert.assertEquals(Hash.Algorithm.SHA1.toString(), hashTables.get(1).getAlgorithm());
        Assert.assertEquals(SAMPLE_HASH_TABLE_SHA1, hashTables.get(1).getHashTable());
        Assert.assertEquals(Hash.Algorithm.SHA256.toString(), hashTables.get(2).getAlgorithm());
        Assert.assertEquals(SAMPLE_HASH_TABLE_SHA256, hashTables.get(2).getHashTable());

        System.out.println("************************** Directory Tree *************************");
        System.out.print('[');
        System.out.print(simpleImporter.getDirectoryTree());
        System.out.println(']');
        System.out.println("**************************  Hash Tables  *************************");
        System.out.println(simpleImporter.getHashTables());
    }


    private static String loadTextFile(String path) throws IOException, URISyntaxException {
        return loadTextFile(SimpleImporterTest.class.getResource(path));
    }

    private static String loadTextFile(URL path) throws IOException, URISyntaxException {
        return loadTextFile(Paths.get(path.toURI()));
    }

    private static String loadTextFile(Path path) throws IOException {
        final byte[] allBytes = Files.readAllBytes(path);
        return new String(allBytes, StandardCharsets.UTF_8);
    }

}
