package gq.baijie.catalog.client.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import gq.baijie.catalog.client.cli.command.MainCommand;
import gq.baijie.catalog.client.cli.command.ScanCommand;
import gq.baijie.catalog.client.cli.command.VerifyCommand;
import gq.baijie.catalog.controller.Command;

import static gq.baijie.catalog.controller.Command.CommandType.SCAN;
import static gq.baijie.catalog.controller.Command.CommandType.VERIFY;

public class Main {

    private static final String SCAN_COMMAND_NAME = "scan";

    private static final String VERIFY_COMMAND_NAME = "verify";

    final MainCommand mainCommand = new MainCommand();

    final ScanCommand scanCommand = new ScanCommand();

    final VerifyCommand verifyCommand = new VerifyCommand();

    final JCommander jCommander = new JCommander(mainCommand);

    {
        jCommander.addCommand(SCAN_COMMAND_NAME, scanCommand);
        jCommander.addCommand(VERIFY_COMMAND_NAME, verifyCommand);
    }

    public static void main(String[] args) {
        new Main().start(args);
    }

    public void start(String[] args) {
        try {
            jCommander.parse(args);

            if (mainCommand.isHelp()) {
                jCommander.usage();
            } else {
                final String parsedCommand = jCommander.getParsedCommand();
                if (parsedCommand == null) {
                    throw new ParameterException("No Command.");
                } else {
                    executeCommand(parsedCommand);
                }
            }

        } catch (ParameterException e) {
            System.out.println("Command Error!");
            e.printStackTrace();
            System.out.println();
            jCommander.usage();
        }
    }

    private void executeCommand(final String parsedCommand) {
        switch (parsedCommand) {
            case SCAN_COMMAND_NAME:
                final Command command = Command.newInstance(SCAN);
                command.addAllAlgorithms(scanCommand.getAlgorithms());
                command.setDirectory(scanCommand.getRootPath().get(0));
                command.run();
                break;
            case VERIFY_COMMAND_NAME:
                Command.newInstance(VERIFY)
                        .setDirectory(verifyCommand.getRootPath().get(0))
                        .run();
                break;
            default:
                throw new RuntimeException("shouldn't goto there.");
        }
    }

}
