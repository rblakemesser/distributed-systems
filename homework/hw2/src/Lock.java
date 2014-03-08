
public interface Lock extends MsgHandler {
    public void requestCS();
    public void releaseCS();
}
