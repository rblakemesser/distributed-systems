import java.io.IOException;

/**
 * Created by douglasgunter on 1/25/14.
 */
public interface Lock extends MsgHandler {
    public void requestCS();
    public void releaseCS();
}
