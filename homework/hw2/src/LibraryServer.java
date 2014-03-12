import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryServer {
    // private DirectClock dc;
    private int myId;
    private int timeToWait = 0;
    private int killCounter = -1;
    private BookDatabase bookDatabase;
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
        bookDatabase = new BookDatabase(numBooks);
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
                System.out.println("LibraryServer: COULD NOT GET ID FROM LAST LINE OF CONFIG FILE");
            }
            killCounter = Integer.parseInt(localServerConfigVars[1]);
            timeToWait = Integer.parseInt(localServerConfigVars[2]);
        }

        commandHandler = new CommandHandler(bookDatabase);
        MessageProcessor mp = new MessageProcessor(commandHandler);
        mp.start();
        listener = new TCPListen(myPort, mp, killCounter, timeToWait);
        listener.start();

        try {
            linker = new Linker("libserver", myId, servers);
            lm = new LamportMutex(linker, commandHandler);
        }
        catch (IOException e) {
            System.err.println("Could not start linker");
            e.printStackTrace();
        }

    }
}
