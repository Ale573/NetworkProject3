import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Scanner;

public class Sender extends Thread {

    private static final int BUFFER_SIZE = 100;
    private static final String HOSTNAME = "localhost";
    private static final int BASE_SEQUENCE_NUMBER = 32;

    public void run() {

        final Scanner sc = new Scanner(System.in);

        // Create the sequence number
        Integer sequenceNumber = BASE_SEQUENCE_NUMBER;

        try {

            // Create a socket
            final DatagramSocket socket = new DatagramSocket();
            //socket.setSoTimeout(1000);

            while (true) {

                final Random discard = new Random();
                final Random duplicate = new Random();

                // Create a byte array
                byte[] seqNo = new byte[4];
                byte[] byteMessage = new byte[BUFFER_SIZE-4];
                byte[] sendData = new byte[BUFFER_SIZE];
                //byte[] ACK = new byte[4];

                // Read sender message
                String senderMessage = sc.nextLine();

                // Add sequence number to the message
                seqNo = ByteBuffer.allocate(4).putInt(sequenceNumber).array();
                System.arraycopy(seqNo, 0, sendData, 0, 4);

                // Convert the string input into the byte array and it to the message
                byteMessage = senderMessage.getBytes();
                System.arraycopy(byteMessage, 0, sendData, 5, byteMessage.length);

                // Get IP address of receiver
                InetAddress IPAddress = InetAddress.getByName(HOSTNAME);

                // Packet to send
                DatagramPacket send_packet = new DatagramPacket(sendData, sendData.length, IPAddress, 6789);

                // Discard packet
                if(discard.nextDouble() <= 0.20) {
                    System.out.println("The packet was discarded.");
                }
                else {
                    // Duplicate packet
                    if(duplicate.nextDouble() <= 0.10) {
                        socket.send(send_packet);
                        System.out.println("The packet was duplicated.");
                    }

                    while(true){
                    // Send the UDP packet to receiver
                    socket.send(send_packet);
                    sequenceNumber++;

                    //acknowledge received packet
                    byte[] ack = new byte[4];
                    DatagramPacket ackPacket = new DatagramPacket(ack, 4, IPAddress, 6789);

                    //wait 1 second to receive or resend
                    socket.setSoTimeout(1000);
                    try{
                        socket.receive(ackPacket);
                        System.out.println(ByteBuffer.wrap(ack).getInt(0));
                    }catch(SocketTimeoutException e){
                        System.out.println("Packet was not received, resending");
                        sequenceNumber--;
                    }finally{
                        if(ByteBuffer.wrap(ack).getInt(0)==sequenceNumber){
                            break;
                        }
                    }
                    }
                }

                // break the loop if sender enters "exit"
                if (senderMessage.equals("exit")) {
                    sc.close();
                    socket.close();
                    break;
                }
            }

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) { // If there is not AKC, throw exception
            System.out.println("Timeout: Sequence Number " + sequenceNumber);
            sequenceNumber--;
        }
    }

    public static void main(final String args[]) {
        final Sender s1 = new Sender();
        s1.start();
    }
}
