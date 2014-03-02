import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryCLI {
    String programModeArg;
    String inputFilename;
    public static void main(String[] args) {
        new LibraryCLI(args);
    }
    private LibraryCLI(String[] args) {
        programModeArg = args[0];
        inputFilename = args[1];
        File iFile = new File(inputFilename);
        String inputFileContents = LibraryFileHandler.getContents(iFile);
        String[] splitInput = inputFileContents.split("\n");
        ArrayList<String[]> instructionsForProcess = new ArrayList<String[]>();
        System.out.println("input filename: " + inputFilename);

        Pattern p = Pattern.compile("([/_a-z]+)([0-9]+)(\\.in)");
        Matcher m = p.matcher(inputFilename);
        if (m.find()) {
            String pid = m.group(2);
            int processId = Integer.parseInt(pid);
            System.out.println("ID number parsed from filename: " + processId);
        }
        else {
            System.out.println("ERROR! input filename did not pass validation! No ID number detected.");
        }
        int pid = 0; // TODO: Pull process id (server or client) from file name

        for (String i: splitInput) {
            instructionsForProcess.add(i.split(" "));
        }
        if (programModeArg.toLowerCase().startsWith("-s")) {
            System.out.println("server mode");
            new LibraryServer(instructionsForProcess, pid);
        }
        else if (programModeArg.toLowerCase().startsWith("-c")) {
            System.out.println("client mode");
            new LibraryClient(instructionsForProcess, pid);
        }
    }
}
