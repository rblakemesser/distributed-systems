import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryServer {
    // private DirectClock dc;
    private int myId;
    private int timeToWait = 0;
    private int killCounter; // may be null!
    private BookDatabase bookDatabase;
    private ServerList servers;
    private int myPort;
    private LamportMutex lm;

    public LibraryServer(String[] splitConfigContents, int pid) {
        String[] universalServerConfigVars = splitConfigContents[0].split(" ");
        int numServers = Integer.parseInt(universalServerConfigVars[0]);
        int numBooks = Integer.parseInt(universalServerConfigVars[1]);
        myId = pid;
        bookDatabase = new BookDatabase(numBooks);

        Linker linker = null;
        try {
            linker = new Linker("libserve", myId, numServers, servers);
            lm = new LamportMutex(linker);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not start linker");
        }



        // find the correct localHost listener
        servers = new ServerList(splitConfigContents);
        myPort = servers.getAvailableLocalPort();
        System.out.println("LibraryServer: Found a port I can use: " + myPort);

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
                    System.out.println("LibraryServer: INCONSISTENT IDs DETECTED FOR THIS SERVER");
                    System.out.println("LibraryServer: " + myId + " from filename and " + redundantId + " from config file contents");
                }
            }
            else {
                System.out.println("LibraryServer: COULD NOT GET ID FROM LAST LINE OF CONFIG FILE");
            }
            killCounter = Integer.parseInt(localServerConfigVars[1]);
            timeToWait = Integer.parseInt(localServerConfigVars[2]);
        }
    }
}
