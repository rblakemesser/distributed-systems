import java.util.HashMap;

/**
 * Created by blake on 1/27/14.
 */
public class TheaterServer {
    private int numSeats;
    public HashMap<Integer, String> seatMap;
    public OrderHandler orderHandler;
    public OrderReceiver orderReceiver;
    public UDPListen dgServer;

    public TheaterServer(int numSeats) {
        this.numSeats = numSeats;
        this.seatMap = new HashMap<Integer, String>();
        this.orderHandler = new OrderHandler(seatMap, numSeats);
        this.orderReceiver = new OrderReceiver(orderHandler);
        this.dgServer = new UDPListen(orderReceiver);
    }


}
