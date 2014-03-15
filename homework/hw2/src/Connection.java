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

            String connectionMessage;
            while (connectionReader.ready()){
                connectionMessage = connectionReader.readLine();
                LibraryCLI.safePrintln("Connection - Received: " + connectionMessage);

                // handle initConnections from other servers
                boolean initConnection = connectionMessage.split(" ")[0].equals("initConnection");
                if (!initConnection) {
                    TCPListen.currentMessageNumber++;
                }
                LibraryCLI.safePrintln("Connection: Sending message to CommandHandler: " + connectionMessage);
                if(isClientMessage(connectionMessage)) {
                    response = ch.handleClientCommand(connectionMessage);
                }
                else {
                    response = ch.handleServerMessage(connectionMessage);
                }
                try {
                    connectionReply.println(response);
                    LibraryCLI.safePrintln("Connection: wrote bytes to the socket: " + response);
                    connectionReply.flush();
                    //connectionReader.close();
                    //connectionReply.close();
                    //s.close();

                } catch (Exception e) {
                    LibraryCLI.safePrintln("Connection: IOException after CommandHandler");
                    e.printStackTrace();
                }
            }
            connectionReader.close();
            connectionReply.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isClientMessage(String msg) {
        String[] splitCommand = msg.split(" ");
        return (splitCommand.length == 3 && splitCommand[0].startsWith("c"));
    }
}
