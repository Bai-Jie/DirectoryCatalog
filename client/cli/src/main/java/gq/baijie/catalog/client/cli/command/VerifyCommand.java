package gq.baijie.catalog.client.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.nio.file.Path;
import java.util.List;

@Parameters(commandDescription = "Verify the files")
public class VerifyCommand {

    @Parameter(required = true, description = "Path of the root directory to be verified")
    private List<Path> mRootPath;

    public List<Path> getRootPath() {
        return mRootPath;
    }

}
