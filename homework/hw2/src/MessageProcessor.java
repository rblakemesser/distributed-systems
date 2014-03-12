import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class MessageProcessor extends Thread {

    private final CommandHandler ch;
    private LinkedList<MessageTrace> commandQueue;

    public MessageProcessor(CommandHandler ch){
        this.ch = ch;
        this.commandQueue = new LinkedList<MessageTrace>();
    }

    @Override
    public void run(){
        while (true) {
            if (this.commandQueue.size() > 0) {
                LibraryCLI.safePrintln("MessageProcessor: the stack trace definitely lies");
                MessageTrace msgTrace = commandQueue.pop();
                LibraryCLI.safePrintln("MessageProcessor: received message-- processing: " + msgTrace.cmd);
                String response = ch.handleCommand(msgTrace.cmd);
                try {
                    msgTrace.addy.writeBytes(response + "\n");
                    LibraryCLI.safePrintln("MessageProcessor: wrote bytes to the socket");
                    msgTrace.addy.close();
                    msgTrace.sock.close();
                } catch (IOException e) {
                    LibraryCLI.safePrintln("MessageProcessor: the stack trace lies");
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void addMessage(String msg, DataOutputStream addy, Socket tcpSocket) {
        LibraryCLI.safePrintln("MessageProcessor: received message " + msg);
        this.commandQueue.add(new MessageTrace(msg, addy, tcpSocket));
    }
}
