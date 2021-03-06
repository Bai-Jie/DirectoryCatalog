package gq.baijie.catalog.client.cli.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Collections;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VerifyCommandTest {

    private VerifyCommand mVerifyCommand;

    private JCommander mJCommander;


    @Before
    public void setUp() {
        mVerifyCommand = new VerifyCommand();
        mJCommander = new JCommander(mVerifyCommand);
        assertNull("before test", mVerifyCommand.getRootPath());
    }

    @After
    public void tearDown() {
        mJCommander = null;
        mVerifyCommand = null;
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
                mVerifyCommand.getRootPath());
        System.out.println(mVerifyCommand.getRootPath());
    }

    @SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
    @Test(expected = Exception.class)
    public void testMainParameterWithIllegalChars() {
        mJCommander.parse(Constants.ILLEGAL_PATH);
    }

}
