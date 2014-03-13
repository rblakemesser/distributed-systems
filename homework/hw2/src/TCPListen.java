import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListen extends Thread {
    int port;
    int killCounter;
    int timeToWait;
    CommandHandler ch;
    static boolean sleepMode = false;
    public static int currentMessageNumber = 0;

    public TCPListen(int port, CommandHandler commandHandler, int killCounter, int timeToWait) {
        this.port = port;
        this.killCounter = killCounter;
        this.timeToWait = timeToWait;
        this.ch = commandHandler;
    }

    @Override
    public void run() {
        try {
            ServerSocket tcpSocket = new ServerSocket(port);
            while (true) {
                try {
                    Socket connectionSocket = tcpSocket.accept();
                    new Connection(connectionSocket, ch);
                    // c.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                BufferedReader connectionReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter connectionReply = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                String connectionMessage = connectionReader.readLine();
                while (connectionMessage != null) {
                    LibraryCLI.safePrintln("TCPListen/Connection - Received from client: " + connectionMessage);

                    // handle initConnections from other servers
                    boolean initConnection = connectionMessage.split(" ")[0].equals("initConnection");
                    if (!initConnection) {
                        currentMessageNumber++;
                    }
                    if (!initConnection && killCounter > 0 && (currentMessageNumber % killCounter == 0)) {
                        TCPListen.sleepMode = true;
                    }
                    if (TCPListen.sleepMode) {
                        // TODO do sleep mode stuff
                    }
                    else { // if not sleep mode, then handle the message
                        LibraryCLI.safePrintln("TCPListen: Sending message to CommandHandler");
                        String response = ch.handleCommand(connectionMessage);
                        try {
                            connectionReply.println(response + "\n");
                            LibraryCLI.safePrintln("TCPListen: wrote bytes to the socket: " + response);
                            connectionReply.flush();
                            s.close();

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
