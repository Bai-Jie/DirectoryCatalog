package gq.baijie.catalog.client.cli.command;

import com.beust.jcommander.Parameter;

public class MainCommand {

    @Parameter(names = {"--help", "-h"}, help = true, description = "show help information")
    private boolean help;

    public boolean isHelp() {
        return help;
    }

}
