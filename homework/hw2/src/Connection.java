import java.io.*;
import java.net.Socket;

class Connection extends Thread {
    Socket s;
    CommandHandler ch;

    Connection(Socket s, CommandHandler ch) {
        this.s = s;
        this.ch = ch;
        this.start();
    }

    public void run() {
        String response;
        try {
            BufferedReader connectionReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter connectionReply = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            String connectionMessage = connectionReader.readLine();
            while (connectionMessage != null) {
                LibraryCLI.safePrintln("TCPListen/Connection - Received from client: " + connectionMessage);

                // handle initConnections from other servers
                boolean initConnection = connectionMessage.split(" ")[0].equals("initConnection");
                if (!initConnection) {
                    TCPListen.currentMessageNumber++;
                }
                if (!initConnection && TCPListen.killCounter > 0 && (TCPListen.currentMessageNumber % TCPListen.killCounter == 0)) {
                    TCPListen.sleepMode = true;
                }
                if (TCPListen.sleepMode) {
                    // TODO do sleep mode stuff
                }
                else { // if not sleep mode, then handle the message
                    LibraryCLI.safePrintln("TCPListen: Sending message to CommandHandler");
                    if(isClientMessage(connectionMessage)) {
                        response = ch.handleClientCommand(connectionMessage);
                    }
                    else {
                        response = ch.handleServerMessage(connectionMessage);
                    }
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
    public boolean isClientMessage(String msg) {
        String[] splitCommand = msg.split(" ");
        return splitCommand.length == 3;
    }
}

