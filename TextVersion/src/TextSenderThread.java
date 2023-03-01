/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
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
                byte[] header = new byte[4];
                header[0] = (byte) (text.length >> 24);
                header[1] = (byte) (text.length >> 16);
                header[2] = (byte) (text.length >> 8);
                header[3] = (byte) (text.length);
                System.out.println("Header " +Arrays.toString(header));

                //Convert it to an array of bytes
                byte[] buffer = new byte[header.length+ text.length];
                System.arraycopy(header, 0, buffer, 0, 4);
                System.arraycopy(text, 0, buffer, 4, text.length);


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