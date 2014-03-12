import java.util.Arrays;

public class CommandHandler {
    private BookDatabase bookDb;
    private LamportMutex lamportMutex;

    public CommandHandler(BookDatabase bookDb) {
        this.bookDb = bookDb;
    }

    public String reserveBook(int clientNum, int bookNum){
        // bookStatus = 0 means book is not checked out
        if (bookNum > bookDb.bookStatuses.size() || bookNum < 1 || bookDb.bookStatuses.get(bookNum - 1) != 0){
            return "fail " + clientNum + " " + bookNum;
        }
        else if (bookDb.bookStatuses.get(bookNum-1) == 0){
            bookDb.bookStatuses.set(bookNum-1, clientNum);
            return clientNum + " " + bookNum;
        }
        return "fail " + clientNum + " " + bookNum;
    }

    public String returnBook(int clientNum, int bookNum) {
        if (bookDb.bookStatuses.get(bookNum-1) == clientNum) {
            bookDb.bookStatuses.set(bookNum-1, 0);
            return "free " + clientNum + " " + bookNum;
        }
        else {
            return "fail " + clientNum + " " + bookNum;
        }
    }

    public void synchServers(){
        /* Synchronize the allServers - make sure they all have up to date
           book lists, client lists, server lists
         */
    }

    public synchronized String handleCommand(String msg){
        LibraryCLI.safePrintln("CommandHandler: received communication: " + msg);
        String response;
        String[] splitCommand = msg.split(" ");
        if (splitCommand.length == 3) { // client command
            while (this.lamportMutex == null) {
                LibraryCLI.safePrintln("CommandHandler: Waiting for mutex to init before processing client command.");
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int clientId = Integer.parseInt(splitCommand[0].replace("c", ""));
            int bookNum = Integer.parseInt(splitCommand[1].replace("b", ""));
            String command = splitCommand[2];
            lamportMutex.requestCS();
            if (command.equals("reserve")){
                // run reserveBook
                response = reserveBook(clientId, bookNum);
            }
            else if (command.equals("return")){
                // run returnBook
                response = returnBook(clientId, bookNum);
            }
            else {
                response = "Error";
            }
            lamportMutex.releaseCS();
        }
        else { // must be server command
            if (splitCommand[0].equals("initConnection")) {
                LibraryCLI.safePrintln("initial connection: " + Arrays.toString(splitCommand));
                response = "ok";
            }
            else {
                LibraryCLI.safePrintln("new server communication" + Arrays.toString(splitCommand));
                response = "okayyy";
            }
        }
        return response;
    }

    public void registerMutex(LamportMutex mutex) {
        this.lamportMutex = mutex;
    }
}
