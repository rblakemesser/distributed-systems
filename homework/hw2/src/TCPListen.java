import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPListen extends Thread {
    int port;
    CommandHandler ch;

    public TCPListen(int port, CommandHandler ch) {
        this.ch = ch;
        this.port = port;
    }

    @Override
    public void run() {
        try {
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
                    String response = ch.handleCommand(clientCommand);

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
