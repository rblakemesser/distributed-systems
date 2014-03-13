import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
                String connectionMessage;
                while ((connectionMessage = connectionReader.readLine()) != null)
                {
                    LibraryCLI.safePrintln("TCPListen/Connection - Received from client: " + connectionMessage);
                    boolean initConnection = connectionMessage.split(" ")[0].equals("initConnection");
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
                    }
                    else {
                        LibraryCLI.safePrintln("TCPListen: Sending message to CommandHandler");
                        String response = ch.handleCommand(connectionMessage);
                        try {
                            connectionReply.println(response + "\n");
                            LibraryCLI.safePrintln("TCPListen: wrote bytes to the socket: " + response);

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
