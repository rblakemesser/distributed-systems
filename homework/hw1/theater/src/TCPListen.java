/**
 * Created by douglasgunter on 2/1/14.
 */
import java.io.*;
import java.net.*;

public class TCPListen {
    int port;
    int len = 1024;

    public TCPListen(OrderHandler oh){
        this(2018, oh);
    }

    public TCPListen(int port, OrderHandler oh){
        try {
            this.port = port;

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
                    String response = null;
                    String[] splitCommand = clientCommand.split(" ");
                    System.out.println(splitCommand[0]);
                    if (splitCommand[0].equals("reserve") && splitCommand.length == 2) {
                        response = oh.reserve(splitCommand[1]);
                    } else if(splitCommand[0].equals("bookSeat") && splitCommand.length == 3) {
                        response = oh.bookSeat(splitCommand[1], Integer.parseInt(splitCommand[2]));
                    } else if(splitCommand[0].equals("search") && splitCommand.length == 2) {
                        response = oh.search(splitCommand[1]);
                    } else if(splitCommand[0].equals("delete") && splitCommand.length == 2) {
                        response = oh.delete(splitCommand[1]);
                    } else {
                        response = "ERROR"; // reply "I don't understand the command"
                    }

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
