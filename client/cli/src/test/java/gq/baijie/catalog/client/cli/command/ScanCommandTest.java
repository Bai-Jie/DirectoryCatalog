package gq.baijie.catalog.client.cli.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gq.baijie.catalog.entity.Hash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ScanCommandTest {

    private ScanCommand mScanCommand;

    private JCommander mJCommander;


    @Before
    public void setUp() {
        mScanCommand = new ScanCommand();
        mJCommander = new JCommander(mScanCommand);
        assertTrue("before test", mScanCommand.getAlgorithms().isEmpty());
        assertNull("before test", mScanCommand.getRootPath());
    }

    @After
    public void tearDown() {
        mJCommander = null;
        mScanCommand = null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // test Algorithms Option
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testLongOptionAlgorithms1() {
        mJCommander.parse("--algorithms", "md5,sha1,sha256", "/d/");
        assertEquals(
                Arrays.asList(Hash.Algorithm.MD5, Hash.Algorithm.SHA1, Hash.Algorithm.SHA256),
                mScanCommand.getAlgorithms());
    }

    @Test
    public void testLongOptionAlgorithms2() {
        mJCommander.parse("--algorithms", "MD5,SHA1,SHA256", "/d/");
        assertEquals(
                Arrays.asList(Hash.Algorithm.MD5, Hash.Algorithm.SHA1, Hash.Algorithm.SHA256),
                mScanCommand.getAlgorithms());
    }

    @Test
    public void testLongOptionAlgorithms3() {
        mJCommander.parse("--algorithms", "md5,sha-1,sha-256", "/d/");
        assertEquals(
                Arrays.asList(Hash.Algorithm.MD5, Hash.Algorithm.SHA1, Hash.Algorithm.SHA256),
                mScanCommand.getAlgorithms());
    }

    @Test
    public void testLongOptionAlgorithms4() {
        mJCommander.parse("--algorithms", "MD5,SHA-1,SHA-256", "/d/");
        assertEquals(
                Arrays.asList(Hash.Algorithm.MD5, Hash.Algorithm.SHA1, Hash.Algorithm.SHA256),
                mScanCommand.getAlgorithms());
    }

    @Test
    public void testLongOptionAlgorithms5() {
        mJCommander.parse("--algorithms", "Md5,ShA-1,sHa256", "/d/");
        assertEquals(
                Arrays.asList(Hash.Algorithm.MD5, Hash.Algorithm.SHA1, Hash.Algorithm.SHA256),
                mScanCommand.getAlgorithms());
    }

    @Test(expected = ParameterException.class)
    public void testLongOptionAlgorithmsWithUnknownAlgorithm1() {
        mJCommander.parse("--algorithms", "MD-5", "/d/");
    }

    @Test(expected = ParameterException.class)
    public void testLongOptionAlgorithmsWithUnknownAlgorithm2() {
        mJCommander.parse("--algorithms", "MD1", "/d/");
    }

    @Test
    public void testShortOptionAlgorithms() {
        mJCommander.parse("-a", "md5,sha1,sha256", "/d/");
        assertEquals(
                Arrays.asList(Hash.Algorithm.MD5, Hash.Algorithm.SHA1, Hash.Algorithm.SHA256),
                mScanCommand.getAlgorithms());
    }

    ////////////////////////////////////////////////////////////////////////////
    // test Main Parameter
    ////////////////////////////////////////////////////////////////////////////.

    @Test(expected = ParameterException.class)
    public void testNoMainParameter() {
        mJCommander.parse();
    }

    @SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
    @Test
    public void testMainParameter() {
        mJCommander.parse(Constants.COMPLEX_PATH);
        assertEquals(
                Collections.singletonList(Paths.get(Constants.COMPLEX_PATH)),
                mScanCommand.getRootPath());
        System.out.println(mScanCommand.getRootPath());
    }

    @SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
    @Test(expected = Exception.class)
    public void testMainParameterWithIllegalChars() {
        mJCommander.parse(Constants.ILLEGAL_PATH);
    }

}
