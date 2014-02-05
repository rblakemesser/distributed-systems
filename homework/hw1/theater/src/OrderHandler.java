import java.util.HashMap;

public class OrderHandler {
    private final int numSeats;
    private HashMap<String,Integer> seatMap;

    public OrderHandler(int numSeats) {
        this.numSeats = numSeats;
        seatMap = new HashMap<String, Integer>();
    }

    public String bookSeat(String patronName, int seatNumber){
        if (!this.seatMap.containsValue(seatNumber)) {
            if (!this.seatMap.containsKey(patronName)) {
                if (seatNumber < numSeats) {
                    this.seatMap.put(patronName, seatNumber);
                    return "Seat assigned to you is " + seatNumber;
                }
                else {
                    return "That seat doesn't exist!";
                }
            }
            else {
                return "Seat already booked against the name provided";
            }
        }
        return seatNumber + " is not available";
    }

    public String reserve(String patronName) {
        for (int i=0; i < numSeats; i++) {
            if (!seatMap.containsValue(i)) {
                return bookSeat(patronName, i);
            }
        }
        return "Sold out - no seats available";
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
            int seatNum = seatMap.get(patronName);
            seatMap.remove(patronName);
            return seatNum + "";
        }
    }

    public String handleCommand(String clientCommand) {
        String response;
        String[] splitCommand = clientCommand.split(" ");
        if (splitCommand[0].equals("reserve") && splitCommand.length == 2) {
            response = this.reserve(splitCommand[1]);
        } else if(splitCommand[0].equals("bookSeat") && splitCommand.length == 3) {
            response = this.bookSeat(splitCommand[1], Integer.parseInt(splitCommand[2]));
        } else if(splitCommand[0].equals("search") && splitCommand.length == 2) {
            response = this.search(splitCommand[1]);
        } else if(splitCommand[0].equals("delete") && splitCommand.length == 2) {
            response = this.delete(splitCommand[1]);
        } else {
            response = "ERROR"; // reply "I don't understand the command"
        }
        return response;
    }
}

