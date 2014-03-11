import java.util.ArrayList;

public class BookDatabase {
    private final int numBooks;
    public final ArrayList<Integer> bookStatuses;
    public BookDatabase(int numBooks) {
        this.numBooks = numBooks;
        this.bookStatuses = new ArrayList<Integer>();
        for (int i=0; i<numBooks; i++){
            bookStatuses.add(i, 0);
        }
    }
    public String reserveBook(int clientNumber, int bookNumber) {
        if (bookStatuses.get(bookNumber) == null) {
            return "cant someone has it already";
        }
        else {
            bookStatuses.set(bookNumber, clientNumber);
            return "ok client no " + clientNumber + " now has book no " + bookNumber;
        }
    }
    public String returnBook(int clientNumber, int bookNumber) {
        if (!(bookStatuses.get(bookNumber) == clientNumber)) {
            return "you dont even have that book how can you reserver it";
        }
        else {
            bookStatuses.set(bookNumber, null);
            return "ok client no " + clientNumber + " successfully returned book no " + bookNumber;
        }
    }
}
