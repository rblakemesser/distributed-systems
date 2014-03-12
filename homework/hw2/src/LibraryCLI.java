import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryCLI {
    String programModeArg;
    String inputFilename;
    int pid;
    public static void main(String[] args) {
        new LibraryCLI(args);
    }
    private LibraryCLI(String[] args) {
        programModeArg = args[0];
        inputFilename = args[1];
        File iFile = new File(inputFilename);
        String inputFileContents = LibraryFileHandler.getContents(iFile);
        String[] splitInput = inputFileContents.split("\n");
        LibraryCLI.safePrintln("input filename: " + inputFilename);

        Pattern p = Pattern.compile("([/_a-z]+)([0-9]+)(\\.in)");
        Matcher m = p.matcher(inputFilename);
        if (m.find()) {
            String processId = m.group(2);
            pid = Integer.parseInt(processId);
            LibraryCLI.safePrintln("ID number parsed from filename: " + processId);
        }
        else {
            LibraryCLI.safePrintln("ERROR! input filename did not pass validation! No ID number detected.");
        }

        if (programModeArg.toLowerCase().startsWith("-s")) {
            LibraryCLI.safePrintln("server mode");
            new LibraryServer(splitInput, pid);
        }

        else if (programModeArg.toLowerCase().startsWith("-c")) {
            LibraryCLI.safePrintln("client mode");
            new LibraryClient(splitInput, pid);
        }
    }

    public static void safePrintln(String s) {
        synchronized (System.out) {
            System.out.println(s);
        }
    }
}
