package gq.baijie.catalog.test.util;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import gq.baijie.catalog.util.HashTest;

public class Utils {

    public static Path getPath(String path) {
        try {
            return Paths.get(HashTest.class.getResource(path).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
