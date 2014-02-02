import java.util.HashMap;

public class OrderHandler {
    private final int numSeats;
    private HashMap<String,Integer> seatMap;

    public OrderHandler(int numSeats) {
        this.numSeats = numSeats;
        seatMap = new HashMap<String, Integer>();
    }

    public String bookSeat(String patronName, int seatNumber){
        if (this.seatMap.containsValue(seatNumber)){
            return seatNumber + " is not available";
        }
        this.seatMap.put(patronName, seatNumber);
        return "Seat assigned to you is " + seatNumber;
    }

    public String reserve(String patronName) {
        if (this.seatMap.containsKey(patronName)){
            return "Seat already booked against the name provided";
        }
        else if (this.seatMap.size() >= numSeats) {
            return "Sold out -- No seat available";
        }
        else {
            for (int i=0; i < numSeats; i++) {
                if (!seatMap.containsValue(i)) {
                    return bookSeat(patronName, i);
                }
            }
            return "Error! Couldnt find a seat for " + patronName + " even though there should have been one available";
        }
    }

    public String search(String patronName) {
        if (!(seatMap.containsKey(patronName))) {
            return "No reservation found for " + patronName;
        }
        else {
            return "seat " + seatMap.get(patronName);
        }
    }

    public String delete(String patronName) {
        if (!(seatMap.containsKey(patronName))) {
            return "No reservation found for " + patronName;
        }
        else {
            seatMap.remove(patronName);
            return patronName + "'s reservation has been deleted";
        }
    }
}

