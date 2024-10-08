import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class packetSender {
    static DatagramSocket sending_socket;
    protected InetAddress clientIP;
    protected int PORT;


    public packetSender(InetAddress clientIP, int PORT) throws SocketException {
        this.clientIP = clientIP;
        this.PORT = PORT;
    }

    public ByteBuffer add(ByteBuffer data, int counter){
        ByteBuffer bb = ByteBuffer.allocate(data.capacity()+2);
        bb.putShort((short) counter);
        return bb;
    }

    public void send(ByteBuffer data) throws IOException {
        sending_socket = new DatagramSocket();
        //DatagramSocket sending_socket;
        try{
            if(data.hasArray()){

                DatagramPacket packet = new DatagramPacket(data.array(), data.array().length, clientIP, PORT);

                sending_socket.send(packet);
                //***************************************************
                //Close the socket
                sending_socket.close();
            }
            //***************************************************
            //Send the data

        } catch (SocketException e){
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
    }

}
