
public class Server {
    public static void main(String[] args) {
        Sender sender = new Sender();
        Receiver receiver = new Receiver();
        sender.start();
        receiver.start();
    }
}