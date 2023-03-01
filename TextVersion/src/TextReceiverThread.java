/*
 * TextReceiverThread.java
 */

/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TextReceiverThread implements Runnable{

    static DatagramSocket receiving_socket;

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run (){

        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************

        //***************************************************
        //Open a socket to receive from on port PORT

        //DatagramSocket receiving_socket;
        try{
            receiving_socket = new DatagramSocket(PORT);
        } catch (SocketException e){
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.

        boolean running = true;

        while (running){

            try{
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] buffer = new byte[82];
                byte[] text = new byte[80];
                DatagramPacket packet = new DatagramPacket(buffer, 0, 82);

                receiving_socket.receive(packet);

                // Creates a ByteBuffer object and allocates it to the size of buffer.
                ByteBuffer bb = ByteBuffer.wrap(buffer);
                // Grabs the short value from the front of the byte array
                short header = bb.getShort();
                bb.get(text);
                //Get a string from the byte buffer
                String str = new String(text);
                //Display it
                System.out.println(header);
                System.out.print(str);

                //The user can type EXIT to quit
                if (str.substring(0,4).equals("EXIT")){
                    running=false;
                }
            } catch (IOException e){
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        receiving_socket.close();
        //***************************************************
    }
}