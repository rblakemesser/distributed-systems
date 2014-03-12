import java.io.*;
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
            while (true) {
                try {
                    Socket connectionSocket = tcpSocket.accept();
                    //BufferedReader clientReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    //DataOutputStream clientReply = new DataOutputStream(connectionSocket.getOutputStream());
                    //String clientCommand;
                    Connection c = new Connection(connectionSocket, ch);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
                    /*
                    while ((clientCommand = clientReader.readLine()) != null){


                    // clientReader.close();
                    LibraryCLI.safePrintln("TCPListen: message received: " + clientCommand);
                    // Parse and handle the command
                    //    capture the reply from the server in response

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

                        } catch (IOException e) {
                            LibraryCLI.safePrintln("TCPListen: IOException after CommandHandler");
                            e.printStackTrace();
                        }
                    }
                    }
                    clientReply.close();
                    connectionSocket.close();
                }
                catch (SocketException se){
                    System.err.println(se);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } */


    class Connection extends Thread {
        Socket s;
        CommandHandler ch;

        Connection(Socket s, CommandHandler ch) {
            this.s = s;
            this.ch = ch;
            this.start();
        }

        public void run() {
            try {
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter clientReply = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                String clientCommand;
                while ((clientCommand = clientReader.readLine()) != null)
                {
                    LibraryCLI.safePrintln("TCPListen/Connection - Received from client: " + clientCommand);
                    boolean initConnection = clientCommand.split(" ")[0].equals("initConnection");
                    if (!initConnection) {
                        currentMessageNumber++;
                    }
                    if (!initConnection && killCounter > 0 && (currentMessageNumber % killCounter == 0)) {
                        sleepMode = true;
                    }
                    if (sleepMode) {
                        try {
                            LibraryCLI.safePrintln("received " + killCounter + "th message, sleeping for: " + timeToWait);
                            Thread.sleep(timeToWait);
                        } catch (InterruptedException e) {
                            LibraryCLI.safePrintln("interrupted while 'dead' (shouldnt happen)");
                        }
                    } else {
                        LibraryCLI.safePrintln("TCPListen: Sending message to CommandHandler");
                        String response = ch.handleCommand(clientCommand);
                        try {
                            clientReply.println(response + "\n");
                            LibraryCLI.safePrintln("TCPListen: wrote bytes to the socket: " + response);
                            //clientReply.close();
                            //s.close();

                        } catch (Exception e) {
                            LibraryCLI.safePrintln("TCPListen: IOException after CommandHandler");
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
