/*
 * TextSender.java
 */

/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;

import javax.sound.sampled.LineUnavailableException;
import javax.xml.crypto.Data;

public class AudioSender {

    static DatagramSocket sending_socket;
    static AudioRecorder ar;

    static {
        try {
            ar = new AudioRecorder();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main (String[] args){

        //***************************************************
        //Port to send to
        int PORT = 55555;
        //IP ADDRESS to send to
        InetAddress clientIP = null;
        try {
            clientIP = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: TextSender: Could not find client IP");
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
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
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
        DatagramPacket[] matrix = new DatagramPacket[16];
        int count = 0;
        while (running){
            try{

                byte[] audio = ar.getBlock();

                byte[] lower = sequenceNumbering.lowerBitRate(audio, audio.length);

                //Read in a string from the standard input
                //String str = in.readLine();

                //Convert it to an array of bytes
                byte[] buffer = audio;

                //Make a DatagramPacket from it, with client address and port number
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientIP, PORT);

                matrix[count] = packet;
                DatagramPacket[] sorted = new DatagramPacket[packet.getLength()];
                count++;
                if(count == 16){
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j <4; j++) {
                            sorted[4 * (3 - j) + i] = matrix[4 * i + j];
                        }
                    }
                    for (int i = 0; i < 16; i++) {
                        sending_socket.send(sorted[i]);
                    }
                    count = 0;
                }
                //This is where interleaving and packet structure will be implemented


                //Send it
                sending_socket.send(packet);

                //The user can type EXIT to quit
                /*
                if (str.equals("EXIT")){
                    running=false;
                }*/

            } catch (IOException e){
                System.out.println("ERROR: TextSender: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        sending_socket.close();
        //***************************************************
    }
}