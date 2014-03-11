import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class MessageProcessor extends Thread {

    private final CommandHandler ch;
    private final LinkedList<CommandAddress> commandQueue;

    public MessageProcessor(CommandHandler ch){
        this.ch = ch;
        this.commandQueue = new LinkedList<CommandAddress>();
    }

    @Override
    public void run(){
        while (true) {
            if (this.commandQueue.size() > 0) {
                CommandAddress newCommand = commandQueue.pop();
                String response = ch.handleCommand(newCommand.cmd);
                try {
                    newCommand.addy.writeBytes(response + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addMessage(String msg, DataOutputStream addy) {
        this.commandQueue.add(new CommandAddress(msg, addy));
    }
}
