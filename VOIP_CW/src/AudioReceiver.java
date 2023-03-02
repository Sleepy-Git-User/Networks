/*
 * TextReceiver.java
 */

/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;


import javax.sound.sampled.LineUnavailableException;

public class AudioReceiver implements Runnable {

    static DatagramSocket receiving_socket;
    static AudioPlayer ap;

    static {
        try {
            ap = new AudioPlayer();
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

                //Created a buffer of 512 bytes for the incoming packets.
                byte[] buffer = new byte[514];
                //Created a byte array to store the audio minus the 2 bytes for the header.
                byte[] audio = new byte[512];
                //Was here before I was. Default lab jazz.
                DatagramPacket packet = new DatagramPacket(buffer, 0, 514);
                DatagramPacket[] send = new DatagramPacket[16];

                receiving_socket.receive(packet);

                //Creates a ByteBuffer Object and using the wrap method allocates it to the size of the buffer.
                ByteBuffer bb = ByteBuffer.wrap(buffer);

                //Short variable to store the header data. Removes it from the ByteArray.
                short header = bb.getShort();
                //Assignes the remaining Byte Array to audio. This is the audio data.
                bb.get(audio);
                //Playes the Audio
                ap.playBlock(audio);
                // Prints the header for each packet.
                System.out.println(header);

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