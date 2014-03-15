import java.util.ArrayList;
import java.util.Arrays;

public class CommandHandler {
    public static ArrayList<Integer> bookStatuses = null;
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
        // Message format: <tag> <from id> <to id> <message> <null>
        String response;
        String[] splitCommand = s.split(" ");
        if (splitCommand[0].equals("initConnection")) {
            LibraryCLI.safePrintln("initial connection: " + Arrays.toString(splitCommand));
            return "ok";
        }

        LibraryCLI.safePrintln("CommandHandler: identified as server message: " + Arrays.toString(splitCommand));
        int senderIdx = Integer.parseInt(splitCommand[0]);
        String msgType = splitCommand[2];
        String tsComponent = splitCommand[3];
        int timestamp = tsComponent.contains("#") ? Integer.parseInt(tsComponent.substring(0, tsComponent.length() - 1)) : Integer.parseInt(tsComponent);  // remove the #

        if (s.contains("?")){
            // must be a release message
            String bookList = s.split("\\?")[1]; // take everything after the question mark
            setSerializedBookList(bookList.substring(0, bookList.length() - 1)); // remove the hash off the end
            LibraryCLI.safePrintln("CommandHandler: received book list: " + getSerializedBookList());
        }
        response = lamportMutex.handleMsg(timestamp, senderIdx, msgType);
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
