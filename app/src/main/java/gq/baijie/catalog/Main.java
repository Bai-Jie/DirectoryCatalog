package gq.baijie.catalog;

import java.io.IOException;

import gq.baijie.catalog.controllor.Command;

import static gq.baijie.catalog.controllor.Command.CommandType.SCAN;
import static gq.baijie.catalog.controllor.Command.CommandType.VERIFY;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            Command.newInstance(SCAN).setDirectory(args[0]).run();
            System.exit(0);
        } else if (args.length == 2) {
            if (args[0].matches("^(?i:-v|--verify)$")) {
                Command.newInstance(VERIFY).setDirectory(args[1]).run();
                System.exit(0);
            }
        } else if (args.length == 3) {
            if (args[0].matches("^(?i:-a|--algorithm)$")) {
                if (args[1].matches("^(?i:MD5|SHA1|SHA256)$")) {
                    Command.newInstance(SCAN).setAlgorithm(args[1]).setDirectory(args[2]).run();
                    System.exit(0);
                }
            }
        }
        printUsage();
        System.exit(-1);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        String javaCommand = String.format("    java %s ", Main.class.getSimpleName());
        System.out.println(javaCommand + "[-a|--algorithm (MD5|SHA1|SHA256)] <directory>");
        System.out.println(javaCommand + "(-v|--verify) <directory>");
    }

}
