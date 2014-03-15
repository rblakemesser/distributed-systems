import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListen extends Thread {
    int port;
    static int timeToWait;
    CommandHandler ch;
    static int killCounter;
    static boolean sleepMode = false;
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
                try {
                    Socket connectionSocket = tcpSocket.accept();
                    if (!sleepMode) {
                        Connection c = new Connection(connectionSocket, ch);
                    }
                    else {
                        LibraryCLI.safePrintln("SLEEPING!!");
                        Thread.sleep(TCPListen.timeToWait);
                        LibraryCLI.safePrintln("AWAKE!!");
                    }
                    // c.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
