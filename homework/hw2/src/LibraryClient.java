import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ListIterator;

public class LibraryClient {
    private int numServers;
    private ServerList serverList;

    public LibraryClient(String[] splitConfigContents, int pid) {
        numServers = Integer.parseInt(splitConfigContents[0]);
        serverList = new ServerList(splitConfigContents);
        // TODO: Process client commands
    }

    public void processInstructions(ArrayList<String[]> input) {
        System.out.println("processing instructions:\n" + input);
        ListIterator commandList = input.listIterator(numServers + 1);
        while (commandList.hasNext()) {
            String[] splitCommand = (String[]) commandList.next();
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
            }
        }
    }

    public String makeRequest(String[] request) {
        return serverList.clientQuery(request);
    }
}
