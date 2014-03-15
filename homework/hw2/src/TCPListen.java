import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListen extends Thread {
    int port;
    static int timeToWait;
    static int killCounter;
    CommandHandler ch;
    public static int currentMessageNumber = 0;

    public TCPListen(int port, CommandHandler commandHandler, int kc, int ttw) {
        this.port = port;
        this.ch = commandHandler;
        killCounter = kc;
        timeToWait = ttw;
    }

    @Override
    public void run() {
        try {
            ServerSocket tcpSocket = new ServerSocket(port);
            while (true) {
                Socket connectionSocket = tcpSocket.accept();
                LibraryCLI.safePrintln(killCounter + " " + currentMessageNumber + " " + timeToWait);
                if ((killCounter > 0) && (currentMessageNumber > 0) && (currentMessageNumber % killCounter == 0)) {
                    LibraryCLI.safePrintln("SLEEPING!!");
                    try {
                        Thread.sleep(timeToWait);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LibraryCLI.safePrintln("AWAKE!!");
                }
                else {
                    Connection c = new Connection(connectionSocket, ch);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
