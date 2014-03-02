import java.util.ArrayList;

public class BookDatabase {
    private final int numBooks;
    private final ArrayList<Integer> bookStatuses;
    public BookDatabase(int numBooks) {
        this.numBooks = numBooks;
        this.bookStatuses = new ArrayList<Integer>(this.numBooks);
    }
    public String reserveBook(int clientNumber, int bookNumber) {
        return "ok";
    }
    public String returnBook(int bookNumber) {
        return "ok";
    }
}
