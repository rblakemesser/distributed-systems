import java.util.HashMap;

/**
 * Created by blake on 1/27/14.
 */
public class OrderHandler {
    private HashMap<Integer,String> seatMap;
    private final int numSeats;

    public OrderHandler(HashMap<Integer, String> seatMap, int numSeats) {
        this.seatMap = seatMap;
        this.numSeats = numSeats;
    }

    public String bookSeat(String patronName, int seatNumber){
        if (this.seatMap.containsKey(seatNumber)){
            return seatNumber + " is not available";
        }
        this.seatMap.put(seatNumber, patronName);
        return "Seat assigned to you is " + seatNumber;
    }

    public String reserve(String patronName) {
        if (this.seatMap.containsValue(patronName)){
            return "Seat already booked against the name provided";
        }
        else if (this.seatMap.size() >= numSeats) {
            return "Sold out -- No seat available";
        }
        else {
            for (int i = 0; i < this.seatMap.size(); i++){
                if (this.seatMap.get(i).length() == 0) {
                    bookSeat(patronName, i);
                }
            }
            return "Error! Couldnt find a seat for " + patronName + " even though there should have been one available";
        }
    }

    public String search(String patronName) {
        if (!(this.seatMap.containsValue(patronName))) {
            return "No reservation found for " + patronName;
        }
        else {
            for (int i = 0; i < this.seatMap.size(); i++) {
                if (this.seatMap.get(i) == patronName) {
                    return "seat " + i;
                }
            }
        }
        return "Error-- the patron was supposed to be in the dict but wasn't";
    }

    public String delete(String patronName) {
        if (!(this.seatMap.containsValue(patronName))) {
            return "No reservation found for " + patronName;
        }
        else {
            for (int i = 0; i < this.seatMap.size(); i++) {
                if (this.seatMap.get(i) == patronName) {
                    this.seatMap.remove(i);
                }
            }
        }
        return "Error-- the patron was supposed to be in the dict but wasn't";
    }
}
