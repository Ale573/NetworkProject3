import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class Receiver extends Thread {

    private static final int BUFFER_SIZE = 100;
    private static final int PORT = 6789;

    public void run() {

        // Create a byte array
        //byte[] ACK = new byte[4];
        byte[] receiveData = new byte[BUFFER_SIZE];

        try {
            // Create a socket
            DatagramSocket socket = new DatagramSocket(PORT);

            while (true) {

                // Receive the packet
                DatagramPacket receive_packet = new DatagramPacket(receiveData, receiveData.length);
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
                
                //Convert sequence No. into 'int' using Byte Buffer
                int x = ByteBuffer.wrap(rSequenceNo).getInt();
                
                // Convert message to String
                String message = new String(rMessage, 0, BUFFER_SIZE-4);

                System.out.println("FROM SENDER: " + message);

                // Get packet's IP and port
                InetAddress IPAddress = receive_packet.getAddress();
                int port = receive_packet.getPort();

                // Send AKC
                //DatagramPacket send_packet = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                //socket.send(send_packet);

                // Exit the server if the sender sends "exit"
                if (message.equals("exit")){
                    socket.close();
                    System.out.println("Sender sent exit.....EXITING");
                    break;
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Receiver r1 = new Receiver();
        r1.start();
    }
}