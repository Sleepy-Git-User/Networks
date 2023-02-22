/*
 * TextSender.java
 */

/**
 *
 * @author  abj
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;


import CMPC3M06.AudioRecorder;

import javax.sound.sampled.LineUnavailableException;

public class VoiceSender {

    static DatagramSocket sending_socket;

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

        //BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //***************************************************

        //***************************************************
        //Main loop.

        boolean running = true;

        while (running){
            try{
                //Vector used to store audio blocks (32ms/512bytes each)
                //Vector<byte[]> voiceVector = new Vector<byte[]>();

                //Initialise AudioPlayer and AudioRecorder objects
                AudioRecorder recorder = new AudioRecorder();

                //Recording time in seconds
                int recordTime = 10;

                //Capture audio data and add to voiceVector
                System.out.println("Recording Audio...");

                for (int i = 0; i < Math.ceil(recordTime / 0.032); i++) {
                    byte[] block = recorder.getBlock();
                    //Make a DatagramPacket from it, with client address and port number

                    DatagramPacket packet = new DatagramPacket(block, block.length, clientIP, PORT);

                    //Send it
                    sending_socket.send(packet);
                }

                //Close audio input
                recorder.close();


            } catch (IOException e){
                System.out.println("ERROR: TextSender: Some random IO error occured!");
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }
        //Close the socket
        sending_socket.close();
        //***************************************************
    }
}

