import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPListen extends Thread {
    int port;
    int killCounter;
    int timeToWait;
    CommandHandler ch;
    boolean sleepMode = false;
    public int currentMessageNumber;

    public TCPListen(int port, CommandHandler commandHandler, int killCounter, int timeToWait) {
        this.port = port;
        this.killCounter = killCounter;
        this.timeToWait = timeToWait;
        this.currentMessageNumber = 0;
        this.ch = commandHandler;
    }

    @Override
    public void run() {
        try {
            ServerSocket tcpSocket = new ServerSocket(port);
            while(true) {
                try {
                    Socket connectionSocket = tcpSocket.accept();
                    BufferedReader clientReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream clientReply = new DataOutputStream(connectionSocket.getOutputStream());
                    String clientCommand = clientReader.readLine();
                    // clientReader.close();
                    LibraryCLI.safePrintln("TCPListen: message received: " + clientCommand);
                    /* Parse and handle the command
                        capture the reply from the server in response
                    */
                    boolean initConnection = clientCommand.split(" ")[0].equals("initConnection");
                    if (!initConnection){
                        currentMessageNumber++;
                    }
                    if (!initConnection && killCounter > 0 && (currentMessageNumber % killCounter == 0)) {
                        sleepMode = true;
                    }
                    if (sleepMode) {
                        try {
                            LibraryCLI.safePrintln("received " + killCounter + "th message, sleeping for: " + timeToWait);
                            Thread.sleep(timeToWait);
                        }
                        catch (InterruptedException e) {
                            LibraryCLI.safePrintln("interrupted while 'dead' (shouldnt happen)");
                        }
                    }
                    else {
                        LibraryCLI.safePrintln("TCPListen: Sending message to CommandHandler");
                        String response = ch.handleCommand(clientCommand);
                        try {
                            clientReply.writeBytes(response + "\n");
                            LibraryCLI.safePrintln("TCPListen: wrote bytes to the socket");
                            clientReply.close();
                            connectionSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (SocketException se){
                    System.err.println(se);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
