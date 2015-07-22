package gq.baijie.catalog.storage.v1.util;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gq.baijie.catalog.entity.DirectoryFile;
import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.Hash.Algorithm;
import gq.baijie.catalog.entity.RegularFile;


@SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
public class FileUtilsTest {

    private static final Path SAMPLE_PATH = Paths.get("/d/sub_dir/sample/");

    private static final Path SAMPLE_PATH_DIRECTORY = Paths.get("/d/");

    private static final Path SAMPLE_PATH_DIRECTORY_SUB_DIR = Paths.get("/d/sub_dir/");

    private static final Map<Hash.Algorithm, Hash> SAMPLE_HASHES;

    static {
        Map<Hash.Algorithm, Hash> sampleHashes = new EnumMap<>(Algorithm.class);
        for (Hash.Algorithm algorithm : Algorithm.values()) {
            sampleHashes.put(algorithm,
                    new Hash(RandomUtils.nextBytes(algorithm.getBitsLength() / 8), algorithm));
        }
        SAMPLE_HASHES = Collections.unmodifiableMap(sampleHashes);
    }

    @Test
    public void testGetUsedAlgorithmsNoAlgorithms() {
        DirectoryFile directoryFile = new DirectoryFile(SAMPLE_PATH_DIRECTORY);
        DirectoryFile subDirectoryFile = new DirectoryFile(SAMPLE_PATH_DIRECTORY_SUB_DIR);
        directoryFile.addChild(subDirectoryFile);
        directoryFile.addChild(getSampleRegularFile());
        subDirectoryFile.addChild(getSampleRegularFile());

        Algorithm[] usedAlgorithms = FileUtils.getUsedAlgorithms(directoryFile);

        Assert.assertTrue(usedAlgorithms.length == 0);
    }

    @Test
    public void testGetUsedAlgorithmsA() {
        DirectoryFile directoryFile = new DirectoryFile(SAMPLE_PATH_DIRECTORY);
        directoryFile.addChild(getSampleRegularFile(Algorithm.MD5));

        Algorithm[] usedAlgorithms = FileUtils.getUsedAlgorithms(directoryFile);

        Assert.assertTrue(usedAlgorithms.length == 1);
        Assert.assertEquals(Algorithm.MD5, usedAlgorithms[0]);
    }

    @Test
    public void testGetUsedAlgorithmsB() {
        DirectoryFile directoryFile = new DirectoryFile(SAMPLE_PATH_DIRECTORY);
        directoryFile.addChild(getSampleRegularFile(Algorithm.MD5, Algorithm.SHA1));

        Algorithm[] usedAlgorithms = FileUtils.getUsedAlgorithms(directoryFile);

        Assert.assertTrue(usedAlgorithms.length == 2);
        Assert.assertEquals(EnumSet.of(Algorithm.MD5, Algorithm.SHA1),
                EnumSet.of(usedAlgorithms[0], usedAlgorithms));
    }

    @Test
    public void testGetUsedAlgorithmsC() {
        DirectoryFile directoryFile = new DirectoryFile(SAMPLE_PATH_DIRECTORY);
        directoryFile.addChild(getSampleRegularFile(Algorithm.MD5));
        directoryFile.addChild(getSampleRegularFile(Algorithm.SHA256));

        Algorithm[] usedAlgorithms = FileUtils.getUsedAlgorithms(directoryFile);

        Assert.assertTrue(usedAlgorithms.length == 2);
        Assert.assertEquals(EnumSet.of(Algorithm.MD5, Algorithm.SHA256),
                EnumSet.of(usedAlgorithms[0], usedAlgorithms));
    }

    @Test
    public void testGetUsedAlgorithmsD() {
        DirectoryFile directoryFile = new DirectoryFile(SAMPLE_PATH_DIRECTORY);
        DirectoryFile subDirectoryFile = new DirectoryFile(SAMPLE_PATH_DIRECTORY_SUB_DIR);
        directoryFile.addChild(subDirectoryFile);
        directoryFile.addChild(getSampleRegularFile(Algorithm.SHA256));
        subDirectoryFile.addChild(getSampleRegularFile(Algorithm.SHA1, Algorithm.SHA256));

        Algorithm[] usedAlgorithms = FileUtils.getUsedAlgorithms(directoryFile);

        Assert.assertTrue(usedAlgorithms.length == 2);
        Assert.assertEquals(EnumSet.of(Algorithm.SHA1, Algorithm.SHA256),
                EnumSet.of(usedAlgorithms[0], usedAlgorithms));
    }

    @Test
    public void testGetUsedAlgorithms1() {
        testGetUsedAlgorithms1WithThese();
        testGetUsedAlgorithms1WithThese(Algorithm.MD5);
        testGetUsedAlgorithms1WithThese(Algorithm.SHA1);
        testGetUsedAlgorithms1WithThese(Algorithm.SHA256);
        testGetUsedAlgorithms1WithThese(Algorithm.MD5, Algorithm.SHA1);
        testGetUsedAlgorithms1WithThese(Algorithm.MD5, Algorithm.SHA256);
        testGetUsedAlgorithms1WithThese(Algorithm.SHA1, Algorithm.SHA256);
        testGetUsedAlgorithms1WithThese(Algorithm.MD5, Algorithm.SHA1, Algorithm.SHA256);
    }

    public void testGetUsedAlgorithms1WithThese(Algorithm... algorithms) {
        RegularFile regularFile = getSampleRegularFile(algorithms);

        Set<Hash.Algorithm> usedAlgorithms = FileUtils.getUsedAlgorithms1(regularFile);

        if (algorithms.length == 0) {
            Assert.assertTrue(usedAlgorithms.isEmpty());
        } else {
            Assert.assertEquals(EnumSet.of(algorithms[0], algorithms), usedAlgorithms);
        }
    }

    private static RegularFile getSampleRegularFile(Algorithm... algorithms) {
        RegularFile regularFile = new RegularFile(SAMPLE_PATH);
        for (Hash.Algorithm algorithm : algorithms) {
            regularFile.getHashes().put(algorithm, SAMPLE_HASHES.get(algorithm));
        }
        return regularFile;
    }

}
