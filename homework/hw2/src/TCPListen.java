import sun.plugin2.message.Message;

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
    public int currentMessageNumber;
    private final MessageProcessor messageProcessor;

    public TCPListen(int port, CommandHandler ch, int killCounter, int timeToWait) {
        this.port = port;
        this.killCounter = killCounter;
        this.timeToWait = timeToWait;
        this.currentMessageNumber = 0;
        this.messageProcessor = new MessageProcessor(ch);
        messageProcessor.start();
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
                    /* Parse and handle the command
                        capture the reply from the server in response
                    */
                    currentMessageNumber++;
                    if (killCounter > 0 && currentMessageNumber % killCounter == 0) {
                        try {
                            System.out.println("received " + killCounter + "th message, sleeping for: " + timeToWait);
                            Thread.sleep(timeToWait);
                        }
                        catch (InterruptedException e) {
                            System.out.println("interrupted while 'dead' (shouldnt happen)");
                        }
                    }
                    else {
                        messageProcessor.addMessage(clientCommand, clientReply);
                        //String response = ch.handleCommand(clientCommand, clientReply);
                        //clientReply.writeBytes(response + "\n");
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
