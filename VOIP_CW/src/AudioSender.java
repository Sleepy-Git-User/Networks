/*
 * TextSender.java
 */

/**
 *
 * @author  abj
 */
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;


import javax.sound.sampled.LineUnavailableException;



public class AudioSender implements Runnable{

    static DatagramSocket sending_socket;
    static AudioRecorder ar;

    static {
        try {
            ar = new AudioRecorder();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
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

        sequenceLayer sl = new sequenceLayer();
        while (running){
            try{

                byte[] audio = ar.getBlock();
                byte[] buffer = sl.add(count, audio);
//                System.out.println("Sender " + count +" = "+ Arrays.toString(buffer));
                //Stores the bb.array in to buffer ready to be sent off.

                //Make a DatagramPacket from it, with client address and port number
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientIP, PORT);


                matrix[count] = packet;
                count++;
                if(count == 16){
                    DatagramPacket[] sorted = sl.rotateLeft(matrix);
                    for (int i = 0; i < 16; i++) {
                        sending_socket.send(sorted[i]);
                    }
                    count = 0;

                    matrix = new DatagramPacket[16];

                }





                //Send it

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