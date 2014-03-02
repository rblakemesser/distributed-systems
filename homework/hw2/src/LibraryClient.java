import com.sun.tools.javac.util.Pair;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

public class LibraryClient {
    private int numServers;
    // Server list
    // Hashmap - server ID to (address, port)
    private HashMap<Integer, Pair<InetAddress, Integer>> serverList = new HashMap<Integer, Pair<InetAddress, Integer>>();

    public LibraryClient(ArrayList<String[]> input, int pid) {
        numServers = Integer.parseInt(input.get(0)[0]);
        for (int i = 1; i < 1 + numServers; i++){
            // populate the server list
        }
    }

    public void processClientFile(ArrayList<String[]> input){
        ListIterator commands = input.listIterator(numServers+1);
        try{
            String[] splitCommand = (String[]) commands.next();
            if (splitCommand.length == 2){
                // Go dormant for splitCommand[1] milliseconds
            }else {
                // Process the command - send it to one of the servers and wait for a response
            }

        }catch (IndexOutOfBoundsException e){
            // Done with this file
            return;
        }
    }
}
