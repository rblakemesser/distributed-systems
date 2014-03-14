import java.util.ArrayList;
import java.util.Arrays;

public class CommandHandler {
    public static ArrayList<Integer> bookStatuses;
    private LamportMutex lamportMutex;
    int serverId;
    int numBooks;

    public CommandHandler(int numBooks, int id) {
        serverId = id;
        this.numBooks = numBooks;
        bookStatuses = new ArrayList<Integer>();
        for (int i=0; i<numBooks; i++){
            bookStatuses.add(i, 0);
        }
    }

    public synchronized String reserveBook(int clientNum, int bookNum){
        // bookStatus = 0 means book is not checked out
        if (bookNum > bookStatuses.size() || bookNum < 1 || bookStatuses.get(bookNum - 1) != 0){
            return "fail c" + clientNum + " b" + bookNum;
        }
        else if (bookStatuses.get(bookNum-1) == 0){
            bookStatuses.set(bookNum-1, clientNum);
            return "c" + clientNum + " b" + bookNum;
        }
        return "fail c" + clientNum + " b" + bookNum;
    }

    public synchronized String returnBook(int clientNum, int bookNum) {
        if (bookStatuses.get(bookNum-1) == clientNum) {
            bookStatuses.set(bookNum-1, 0);
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
        int senderIdx = Integer.parseInt(splitCommand[0]);
        int receiverIdx = Integer.parseInt(splitCommand[1]);
        String msgType = splitCommand[2];
        int timestamp = Integer.parseInt(splitCommand[3]);  // remove the #

        if (s.contains("?")){
            // must be a release message
            String bookList = s.split("\\?")[1]; // take everything after the question mark
            setSerializedBookList(bookList.substring(0, bookList.length() - 1)); // remove the hash off the end
            LibraryCLI.safePrintln("CommandHandler: received book list: " + getSerializedBookList());
        }

        LibraryCLI.safePrintln("CommandHandler: sent by: " + senderIdx);
        //int dest = Integer.parseInt(splitCommand[2]);
        if (splitCommand[0].equals("initConnection")) {
            LibraryCLI.safePrintln("initial connection: " + Arrays.toString(splitCommand));
            response = "ok";
        }
        else {
            LibraryCLI.safePrintln("new INCOMING server communication" + Arrays.toString(splitCommand));

            String myVector = String.valueOf(lamportMutex.v.getValue(serverId-1));
            response = lamportMutex.handleMsg(timestamp, senderIdx, splitCommand[2]);
        }
        return response;
    }

    public void registerMutex(LamportMutex mutex) {
        this.lamportMutex = mutex;
    }

    public String getSerializedBookList() {
        String sbooklist = "";
        for (int b : bookStatuses) {
            sbooklist += b + ",";
        }
        return sbooklist.substring(0, sbooklist.length() - 1);
    }

    public void setSerializedBookList(String s) {
        String[] sbooklist = s.split(",");
        ArrayList<Integer> newBookList = new ArrayList<Integer>();
        for (String b : sbooklist) {
            newBookList.add(Integer.parseInt(b));
        }
        bookStatuses = newBookList;
    }
}
