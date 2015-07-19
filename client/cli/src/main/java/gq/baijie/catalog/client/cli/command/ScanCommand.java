package gq.baijie.catalog.client.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.CommaParameterSplitter;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import gq.baijie.catalog.client.cli.converter.AlgorithmConverter;
import gq.baijie.catalog.entity.Hash;

@Parameters(commandDescription = "Scan the files")
public class ScanCommand {

    @Parameter(names = {"--algorithms", "-a"},
            converter = AlgorithmConverter.class,
            splitter = CommaParameterSplitter.class,
            description = "the hash algorithms should be used(MD5|SHA1|SHA256)")
    private List<Hash.Algorithm> mAlgorithms = Collections.emptyList();

    @Parameter(required = true, description = "Path of the root directory should be scanned")
    private List<Path> mRootPath;

    public List<Hash.Algorithm> getAlgorithms() {
        return mAlgorithms;
    }

    public List<Path> getRootPath() {
        return mRootPath;
    }

}
