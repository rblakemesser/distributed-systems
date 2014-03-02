import java.io.File;
import java.util.ArrayList;

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

        int pid = 0; // TODO: Pull process id (server or client) from file name

        for (String i: splitInput) {
            instructionsForProcess.add(i.split(" "));
        }
        if (programModeArg.toLowerCase().startsWith("-s")) {
            new LibraryServer(instructionsForProcess, pid);
        }
        else if (programModeArg.toLowerCase().startsWith("-c")) {
            new LibraryClient(instructionsForProcess, pid);
        }
    }
}
