/**
 * Created by douglasgunter on 2/1/14.
 */
import java.io.*;
import java.net.*;

public class TCPListen {
    int port;
    int len = 1024;

    public TCPListen() throws Exception{
        this(2018);
    }

    public TCPListen(int port) throws Exception{
        this.port = port;
        ServerSocket tcpSocket = new ServerSocket(port);

        while(true){
            Socket connectionSocket = tcpSocket.accept();
            //byte[] buf = new byte[len];
            /* Read the packet, find out what to do with it, and
               pass it to the appropriate function
             */
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream clientReply = new DataOutputStream(connectionSocket.getOutputStream());
            String clientCommand = clientReader.readLine();


            /* Parse and handle the command
               capture the reply from the server in response
             */
            String response = null;

            clientReply.writeBytes(response);
        }
    }
}
