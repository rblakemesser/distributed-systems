import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryServer {
    // private DirectClock dc;
    private int myId;
    private int timeToWait = 0;
    private int killCounter = -1;
    private ServerList servers;
    private int myPort;
    private LamportMutex lm;
    private TCPListen listener;
    private CommandHandler commandHandler;
    Linker linker;

    public LibraryServer(String[] splitConfigContents, int pid) {
        String[] universalServerConfigVars = splitConfigContents[0].split(" ");
        int numServers = Integer.parseInt(universalServerConfigVars[0]);
        int numBooks = Integer.parseInt(universalServerConfigVars[1]);
        myId = pid;
        ArrayList<String> serverLines = new ArrayList<String>();
        for (String configLine : splitConfigContents) {
            if (configLine.split(":").length == 2){
                serverLines.add(configLine);
            }
        }
        servers = new ServerList(serverLines);
        // find the correct localHost listener
        myPort = servers.getServer(pid).port;
        servers.getServer(pid).me = true;
        LibraryCLI.safePrintln("My port is: " + myPort);

        // detect optional last line of server config
        if (splitConfigContents.length == 2 + numServers) {
            // Set timeToWait and killCounter based on last line
            String[] localServerConfigVars = splitConfigContents[1 + numServers].split(" ");
            String stringId = localServerConfigVars[0];
            Pattern p = Pattern.compile("s([0-9]+)");
            Matcher m = p.matcher(stringId);
            if (m.find()) {
                int redundantId = Integer.parseInt(m.group(1));
                if (redundantId != myId) {
                    LibraryCLI.safePrintln("LibraryServer: INCONSISTENT IDs DETECTED FOR THIS SERVER");
                    LibraryCLI.safePrintln("LibraryServer: " + myId + " from filename and " + redundantId + " from config file contents");
                }
            }
            else {
                LibraryCLI.safePrintln("LibraryServer: COULD NOT GET ID FROM LAST LINE OF CONFIG FILE");
            }
            killCounter = Integer.parseInt(localServerConfigVars[1]);
            timeToWait = Integer.parseInt(localServerConfigVars[2]);
        }

        commandHandler = new CommandHandler(numBooks, myId);
        listener = new TCPListen(myPort, commandHandler, killCounter, timeToWait);
        listener.start();

        try {
            linker = new Linker("libserver", myId, servers);
            lm = new LamportMutex(linker, commandHandler);
            linker.connect("libserver",linker.dataIn,linker.dataOut);
        }
        catch (IOException e) {
            System.err.println("Could not start linker");
            e.printStackTrace();
        }
    }
}
