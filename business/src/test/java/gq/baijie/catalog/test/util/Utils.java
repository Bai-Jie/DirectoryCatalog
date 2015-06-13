package gq.baijie.catalog.test.util;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    public static Path getPath(String path) {
        try {
            return Paths.get(Utils.class.getResource(path).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
