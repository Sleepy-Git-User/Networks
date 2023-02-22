/*
 * TextReceiver.java
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
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, 0, 512);

                receiving_socket.receive(packet);

                //Play audio
                ap.playBlock(buffer);

                //Get a string from the byte buffer
                //String str = new String(buffer);
                //Display it
                //System.out.print(str + "\n");

                //The user can type EXIT to quit
                /*if (str.substring(0,4).equals("EXIT")){
                    running=false;
                }*/
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