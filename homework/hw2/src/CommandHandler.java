
public class CommandHandler {
    private int[] bookList;

    public CommandHandler(int[] bookList) {
        this.bookList = bookList;
    }

    public String reserveBook(int clientNum, int bookNum){
        if (bookNum > bookList.length ||
                bookNum < 1 ||
                bookList[bookNum-1] != 0){
            return "fail " + clientNum + " " + bookNum;
        }else if (bookList[bookNum-1] == 0){
            bookList[bookNum-1] = clientNum;
            return clientNum + " " + bookNum;
        }
        return "fail " + clientNum + " " + bookNum;
    }

    public String returnBook(int clientNum, int bookNum){
        if (bookList[bookNum-1] == clientNum){
            bookList[bookNum-1] = 0;
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

    public String handleCommand(String clientCommand){
        String response;
        String[] splitCommand = clientCommand.split(" ");
        int clientId = Integer.parseInt(splitCommand[0]);
        int bookNum = Integer.parseInt(splitCommand[1]);
        String command = splitCommand[2];

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
        return response;
    }
}
