import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class packetSender {
    static DatagramSocket sending_socket;
    protected InetAddress clientIP;
    protected int PORT;

    public packetSender(InetAddress clientIP, int PORT) throws IOException {
        this.clientIP = clientIP;
        this.PORT = PORT;
    }

    public void send(byte[] data) throws IOException {
        //DatagramSocket sending_socket;
        try{
            DatagramSocket sending_socket = new DatagramSocket();
            //***************************************************
            //Send the data
            DatagramPacket packet = new DatagramPacket(data, data.length, clientIP, PORT);
            sending_socket.send(packet);
            //***************************************************
            //Close the socket
            sending_socket.close();

        } catch (SocketException e){
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
    }

}
