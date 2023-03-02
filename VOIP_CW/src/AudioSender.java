/*
 * TextSender.java
 */

/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;


import javax.sound.sampled.LineUnavailableException;
import javax.xml.crypto.Data;

public class AudioSender implements Runnable{

    static DatagramSocket4 sending_socket;
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
            sending_socket = new DatagramSocket4();
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
        int sequenceNumber = 0;
        while (running) {


            try {

                //byte[] audio = ar.getBlock();
                // Generate random audio data for testing
                Random random = new Random();
                byte[] audio = new byte[510];
                random.nextBytes(audio);


                //Creates a ByteBuffer object called bb. With 2 bytes for the header and the length of the audio allocated in size.
                ByteBuffer bb = ByteBuffer.allocate(2 + audio.length);
                //Slapped a value of 3 in to the bb array. As a short.
                bb.putShort((short) sequenceNumber);
                sequenceNumber++;
                //Slapped the audio byte array in to bb after the header.
                bb.put(audio);

                //Stores the bb.array in to buffer ready to be sent off.
                byte[] buffer = bb.array();

                //Make a DatagramPacket from it, with client address and port number
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientIP, PORT);

//                matrix[count] = packet;
//                DatagramPacket[] sorted = new DatagramPacket[packet.getLength()];
//                count++;
//                if(count == 16){
//                    for (int i = 0; i < 4; i++) {
//                        for (int j = 0; j <4; j++) {
//                            sorted[4 * (3 - j) + i] = matrix[4 * i + j];
//                        }
//                    }
//                    for (int i = 0; i < 16; i++) {
//                        sending_socket.send(sorted[i]);
//                    }
//                    count = 0;
//                }
                //This is where interleaving and packet structure will be implemented


                //Send it
                sending_socket.send(packet);

                //The user can type EXIT to quit
                /*
                if (str.equals("EXIT")){
                    running=false;
                }*/

            } catch (IOException e) {
                System.out.println("ERROR: TextSender: Some random IO error occured!");
                e.printStackTrace();
            }
        }

            //Close the socket
            sending_socket.close();

        //***************************************************
    }
}