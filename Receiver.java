import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Random;

public class Receiver extends Thread {

    private static final int BUFFER_SIZE = 100;
    private static final int PORT = 6789;
    private static final int BASE_SEQUENCE_NUMBER = 32;
    private float discardPercent=0;

    public void run() {

        // Create a byte array
        byte[] receiveData = new byte[BUFFER_SIZE];

        // Create the sequence number
        Integer sequenceNumber = BASE_SEQUENCE_NUMBER;

        try {
            // Create a socket
            DatagramSocket socket = new DatagramSocket(PORT);
            // socket.connect(InetAddress.getLocalHost(), PORT);

            while (true) {

                final Random discard = new Random();
                InetAddress IPAddress = InetAddress.getLocalHost();
                
                // Receive the packet
                DatagramPacket receive_packet = new DatagramPacket(receiveData, receiveData.length,IPAddress,PORT);
                socket.receive(receive_packet);

                // Get the seqNo and message
                byte[] data = receive_packet.getData();

                //Define a new byte array for storing sequence no. as 4 bytes
                byte[] rSequenceNo = new byte[4];

                //Define a new byte array for storing the message
                byte[] rMessage = new byte[BUFFER_SIZE-4];

                //Getting Sequence No. as a byte array
    		    for (int i = 0; i < 4; i++)
                    rSequenceNo[i] = data[i];

                //Getting Message as a byte array
                for (int i = 0; i < BUFFER_SIZE-4; i++)
                    rMessage[i] = data[i+4];
                
                // Convert message to String
                String message = new String(rMessage, 0, BUFFER_SIZE-4);

                // receive seqNum
                Integer ack = ByteBuffer.wrap(data).getInt(0);

                // Get packet's IP and port
                int port = receive_packet.getPort();
                
                 // Verify if the packet is duplicated
                 if(sequenceNumber == ack) {

                    // Discard packet
                    if(discard.nextDouble() < discardPercent) {
                        System.out.println("The packet was discarded.");
                    }
                    else {
                        System.out.println("FROM SENDER: " + message);

                        sequenceNumber++;

                        //Create ackNum
                        DatagramPacket ack_packet = new DatagramPacket(ByteBuffer.allocate(4).putInt(sequenceNumber).array(), 4, IPAddress, port);
                        socket.send(ack_packet);
                        System.out.println("SeqNum sent: "+ ack);
                    }
                }
                else {
                    System.out.println("Packet duplicated and ignored.");
                    System.out.println("\tSeqNo: " + sequenceNumber);
                    System.out.println("\tACK: " + ack);
                }
                // Exit the server if the sender sends "exit"
                if (message.equals("exit")){
                    socket.close();
                    System.out.println("EXITING RECEIVER");
                    break;
                }
            }
            

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
public void setPercent(float percent){
    discardPercent = percent;
}
    public static void main(String args[]) {
        Receiver r1 = new Receiver();
        r1.start();
    }
}
