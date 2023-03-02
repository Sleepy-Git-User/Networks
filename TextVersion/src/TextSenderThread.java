/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TextSenderThread implements Runnable{

    static DatagramSocket sending_socket;

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run (){

        //***************************************************
        //Port to send to
        int PORT = 55555;
        //IP ADDRESS to send to
        InetAddress clientIP = null;
        try {
            clientIP = InetAddress.getByName("localhost");  //CHANGE localhost to IP or NAME of client machine
        } catch (UnknownHostException e) {
            System.out.println("ERROR: Lab2UDP.TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.

        //DatagramSocket sending_socket;
        try{
            sending_socket = new DatagramSocket();
        } catch (SocketException e){
            System.out.println("ERROR: Lab2UDP.TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Get a handle to the Standard Input (console) so we can read user input

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //***************************************************

        //***************************************************
        //Main loop.

        boolean running = true;
        int counter = 0;
        while (running){

            try{
                counter++;
                //Read in a string from the standard input
                String str = in.readLine();

                byte[] text = str.getBytes();
                System.out.println(Arrays.toString(text));
                // Creates a Bytebuffer and allocates the size to 2 bytes for the short header and then to the size of text.length
                ByteBuffer bb = ByteBuffer.allocate(2+text.length);
                // Slaps a short of 5 on to the bb object
                bb.putShort((short) 5);
                // Slaps the value of Text next.
                bb.put(text);
                // Assigns buffer to bb object
                byte[] buffer = bb.array();

                //Make a DatagramPacket from it, with client address and port number
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientIP, PORT);

                //Send it
                sending_socket.send(packet);

                //The user can type EXIT to quit
                if (str.equals("EXIT")){
                    running=false;
                }

            } catch (IOException e){
                System.out.println("ERROR: Lab2UDP.TextSender: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        sending_socket.close();
        //***************************************************
    }
}