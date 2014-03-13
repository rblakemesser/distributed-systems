import java.util.Arrays;

public class CommandHandler {
    private BookDatabase bookDb;
    private LamportMutex lamportMutex;
    int serverId;

    public CommandHandler(BookDatabase bookDb, int id) {
        this.bookDb = bookDb;
        serverId = id;
    }

    public String reserveBook(int clientNum, int bookNum){
        // bookStatus = 0 means book is not checked out
        if (bookNum > bookDb.bookStatuses.size() || bookNum < 1 || bookDb.bookStatuses.get(bookNum - 1) != 0){
            return "fail c" + clientNum + " b" + bookNum;
        }
        else if (bookDb.bookStatuses.get(bookNum-1) == 0){
            bookDb.bookStatuses.set(bookNum-1, clientNum);
            return clientNum + " " + bookNum;
        }
        return "fail c" + clientNum + " b" + bookNum;
    }

    public String returnBook(int clientNum, int bookNum) {
        if (bookDb.bookStatuses.get(bookNum-1) == clientNum) {
            bookDb.bookStatuses.set(bookNum-1, 0);
            return "free c" + clientNum + " b" + bookNum;
        }
        else {
            return "fail c" + clientNum + " b" + bookNum;
        }
    }

    public String handleClientCommand(String s) {
        String response;
        LibraryCLI.safePrintln("CommandHandler: identified as client command: " + s);
        while (this.lamportMutex == null) {
            LibraryCLI.safePrintln("CommandHandler: Waiting for mutex to init before processing client command.");
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String[] splitCommand = s.split(" ");
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
        return response;
    }

    public String handleServerMessage(String s) {
        String response;
        String[] splitCommand = s.split(" ");
        LibraryCLI.safePrintln("CommandHandler: identified as server message: " + Arrays.toString(splitCommand));
        int senderIdx = Integer.parseInt(splitCommand[1]);

        LibraryCLI.safePrintln("CommandHandler: sent by: " + senderIdx);
        //int dest = Integer.parseInt(splitCommand[2]);
        if (splitCommand[0].equals("initConnection")) {
            LibraryCLI.safePrintln("initial connection: " + Arrays.toString(splitCommand));
            response = "ok";
        }
        else {
            LibraryCLI.safePrintln("new INCOMING server communication" + Arrays.toString(splitCommand));

            Msg msgObject = new Msg(Integer.parseInt(splitCommand[0]),
                                    Integer.parseInt(splitCommand[1]),
                                    "msg",
                                    String.valueOf(lamportMutex.v.getValue(serverId-1)));

            response = lamportMutex.handleMsg(msgObject, senderIdx, splitCommand[2]);
        }
        return response;
    }

    public void registerMutex(LamportMutex mutex) {
        this.lamportMutex = mutex;
    }
}
