import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by douglasgunter on 3/2/14.
 */
public class Connector {
    ServerSocket listener;
    Socket[] link;
    public void Connect(String basename, int myId, int numProc,
                        BufferedReader[] dataIn, PrintWriter[] dataOut, ServerList servers) throws Exception{
        link = new Socket[numProc];
        int localport = getLocalPort(myId);
        listener = new ServerSocket(localport);

        // Accept connections from all smaller processes
        for(int i=0; i<myId; i++){
            Socket s = listener.accept();
            BufferedReader dIn = new BufferedReader(
                    new InputStreamReader(s.getInputStream()));
            String getline = dIn.readLine();
            StringTokenizer st = new StringTokenizer(getline);
            int hisId = Integer.parseInt(st.nextToken());
            int destId = Integer.parseInt(st.nextToken());
            String tag = st.nextToken();
            if (tag.equals("hello")){
                link[hisId] = s;
                dataIn[hisId] = dIn;
                dataOut[hisId] = new PrintWriter(s.getOutputStream());
            }
        }

        // Contact all the bigger processes
        for(int i=myId+1; i < numProc; i++){
            OtherServer os = servers.searchId(i); // TODO: What happens if the destination server is not found?  Can it be missing?
            link[i] = new Socket(os.getAddress(), os.getPort());
            dataOut[i] = new PrintWriter(link[i].getOutputStream());
            dataIn[i] = new BufferedReader(new InputStreamReader(link[i].getInputStream()));

            // Send a hello message to P_i
            dataOut[i].println(myId + " " + i + " " + "hello" + " " + "null");
            dataOut[i].flush();
        }
    }

    int getLocalPort(int id){
        // TODO: find what this should be
        //return Symbols.ServerPort + 10 + id;
        return 0;
    }
    public void closeSockets(){
        try {
            listener.close();
            for(int i=0; i<link.length; i++) link[i].close();
        }catch(Exception e) {System.err.println(e);}
    }
}
