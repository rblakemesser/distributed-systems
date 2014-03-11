import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ListIterator;

public class LibraryClient {
    private int numServers;
    private ServerList serverList;
    private PrintWriter output;
    private int myId;

    public LibraryClient(String[] splitConfigContents, int pid) {
        numServers = Integer.parseInt(splitConfigContents[0]);
        myId = pid;

        String[] instructions = Arrays.copyOfRange(splitConfigContents, numServers+1, splitConfigContents.length);

        ArrayList<String> serverLines = new ArrayList<String>();
        for (String configLine : splitConfigContents) {
            if (configLine.split(":").length == 2){
                serverLines.add(configLine);
            }
        }
        serverList = new ServerList(serverLines);
        // TODO: Process client commands
        processInstructions(instructions);
    }

    public void processInstructions(String[] input) {
        // Set up the output file.
        File outFile = new File("c" + myId + ".out");
        if (!outFile.exists()){
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            output = new PrintWriter(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("processing instructions:\n" + Arrays.toString(input));

        for (String command : input){
            String[] splitCommand = command.split(" ");
            System.out.println("first command: \n" + Arrays.toString(splitCommand));
            if (splitCommand.length == 2) {
                long time = new Date().getTime();
                int timeToWait = Integer.parseInt(splitCommand[1]);
                try {
                    Thread.sleep(timeToWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (new Date().getTime() - time < timeToWait) {
                    // do nothing in sleep phase
                }
            }
            else {
                System.out.println(makeRequest(splitCommand));
                //output.println(makeRequest(splitCommand));
            }
        }
        output.flush();
        output.close();
    }

    public String makeRequest(String[] request) {
        return serverList.clientQuery(request);
    }


}
