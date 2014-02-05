/**
 * Created by douglasgunter on 2/1/14.
 */
import java.io.*;
import java.net.*;

public class TCPListen extends Thread {
    int port;
    OrderHandler oh;

    public TCPListen(OrderHandler oh){
        this(2018, oh);
    }

    public TCPListen(int port, OrderHandler oh) {
        this.oh = oh;
        this.port = port;
    }


    @Override
    public void run() {
        try{
            ServerSocket tcpSocket = new ServerSocket(port);
            while(true){
                try {
                    Socket connectionSocket = tcpSocket.accept();

                    BufferedReader clientReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream clientReply = new DataOutputStream(connectionSocket.getOutputStream());
                    String clientCommand = clientReader.readLine();
                    /* Parse and handle the command
                        capture the reply from the server in response
                    */
                    String response = oh.handleCommand(clientCommand);

                    clientReply.writeBytes(response + "\n");
                }catch (SocketException se){
                    System.err.println(se);
                }

                }
            } catch (IOException e) {
                e.printStackTrace();
        }
    }
}
