package gq.baijie.catalog.client.cli.command;

import com.beust.jcommander.JCommander;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MainCommandTest {

    private MainCommand mMainCommand;

    private JCommander mJCommander;

    @Before
    public void setUp() {
        mMainCommand = new MainCommand();
        mJCommander = new JCommander(mMainCommand);
        assertFalse("before test", mMainCommand.isHelp());
    }

    @After
    public void tearDown() {
        mJCommander = null;
        mMainCommand = null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // test no Options
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testNoOptions() {
        mJCommander.parse();
        assertFalse("no options", mMainCommand.isHelp());
    }

    ////////////////////////////////////////////////////////////////////////////
    // test Help Option
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testLongOptionHelp() {
        mJCommander.parse("--help");
        assertTrue("--help option", mMainCommand.isHelp());
    }

    @Test
    public void testShortOptionHelp() {
        mJCommander.parse("-h");
        assertTrue("-h option", mMainCommand.isHelp());
    }

}
