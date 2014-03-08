import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Linker {
    PrintWriter[] dataOut;
    BufferedReader[] dataIn;
    BufferedReader dIn;
    int myId, N;
    Connector connector;
    public IntLinkedList neighbors = new IntLinkedList();
    public Linker(String basename, int id, int numProc, ServerList servers) throws Exception {
        myId = id;
        N = numProc;
        dataIn = new BufferedReader[numProc];
        dataOut = new PrintWriter[numProc];
        // Replacement for Topology - assume all processes are neighbors
        for (int j = 0; j < N; j++)
            if (j != myId) neighbors.add(j);
        connector = new Connector();
        connector.Connect(basename, myId, numProc, dataIn, dataOut, servers);
    }
    public void sendMsg(int destId, String tag, String msg) {
        //System.out.println("Sending msg to " + destId + ":" +tag + " " + msg);
        dataOut[destId].println(myId + " " + destId + " " + tag + " " + msg + "#");
        dataOut[destId].flush();
    }
    public void sendMsg(int destId, String tag) {
        sendMsg(destId, tag, " 0 ");
    }
    public void multicast(IntLinkedList destIds, String tag, String msg) {
        for (int i=0; i<destIds.size(); i++) {
            sendMsg(destIds.getEntry(i), tag, msg);
        }
    }
    public Msg receiveMsg(int fromId) throws IOException {
        String getline = dataIn[fromId].readLine();
        //System.out.println(" received message " + getline);
        StringTokenizer st = new StringTokenizer(getline);
        int srcId = Integer.parseInt(st.nextToken());
        int destId = Integer.parseInt(st.nextToken());
        String tag = st.nextToken();
        String msg = st.nextToken("#");
        return new Msg(srcId, destId, tag, msg);
    }
    public int getMyId() { return myId; }
    public int getNumProc() { return N; }
    public void close() {connector.closeSockets();}
    /*public static void main(String[] args) throws Exception {
        new Linker(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }*/
}

