package name.svistun.sequel;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import java.util.regex.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

class SequelRename {
    private static String sequelDir;
    private static String regex;
    private static boolean dryRun = false;
    private static boolean outParent = false;

    public static void main(String[] args) {
        init(args);
        execute();
    }

    private static void init(String[] args){
        processArgs(args);
    }

    private static void execute() {
        Path root = FileSystems.getDefault().getPath(sequelDir);
        Pattern p = Pattern.compile(regex);
        try {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
             @Override
             public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                     throws IOException {
                 Matcher m = p.matcher(file.getFileName().toString());
                 if (m.matches()) {
                     Path newFile = file.getParent();
                     if (outParent) {
                         System.out.println(newFile);
                         newFile = newFile.getParent();
                         System.out.println(newFile);
                     }
                     newFile = newFile.resolve(m.group(1));
                     System.out.println(String.format("Moving %s to %s ", file, newFile));
                     if (! dryRun)
                        Files.move(file, newFile);
                 }
                 return FileVisitResult.CONTINUE;
             }
             @Override
             public FileVisitResult postVisitDirectory(Path dir, IOException e)
                     throws IOException {
                 if (e == null) {
                     File[] files = dir.toFile().listFiles();
                     if (files != null && files.length == 0) {
                         System.out.println(String.format("Deleting dir %s ", dir));
                         Files.delete(dir);
                     }
                     return FileVisitResult.CONTINUE;
                 } else {
                     throw e;
                 }
            }
        });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void processArgs(String[] args) {
        Options options = new Options();
        options.addOption(new Option("r", "regex", true, "Java patterned regex according with" +
                "https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html. " +
                "Define only one group. File will be renamed with content of the group"
                ));
        options.addOption(new Option("op", "out-parent", false, "Move sequel out of its parent dir."));
        options.addOption(new Option("dr", "dry-run", false, "Just output how rename will occur"));
        options.addOption("h", "help", false, "Print help message");
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(83);
        try {
            CommandLine cl = parser.parse(options, args);
            outParent = cl.hasOption("out-parent");
            dryRun = cl.hasOption("dry-run");
            if (cl.hasOption("help") || cl.getArgs().length == 0) {
                formatter.printHelp(String.format("java -jar %s.jar [OPTION]... <PATH>", name.svistun.sequel.SequelRename.class.getSimpleName()), "Options:", options, "");
                System.exit(cl.hasOption("help") ? 0 : 1);
            }
            sequelDir = cl.getArgs()[0];
            regex = cl.getOptionValue("regex");
            if (regex == null) {
                throw new UnrecognizedOptionException("Regex has to be defined. See --help output");
            }
        } catch (UnrecognizedOptionException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ParseException e) {
            System.err.println(e.toString() + System.lineSeparator()
                    + StringUtils.join(e.getStackTrace(), System.lineSeparator()));
            System.exit(1);
        }
        if (!new File(sequelDir).isDirectory()) {
            System.err.println(String.format("[%s] is not a directory. Exiting.", sequelDir));
            System.exit(1);
        }
    }
}
