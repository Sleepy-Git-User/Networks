/*
 * TextReceiver.java
 */

/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;


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
        sequenceLayer sl = new sequenceLayer();
        int count = 0;
        byte[][] send = new byte[16][];
        Queue<byte[]> q = new LinkedList<byte[]>();
        int group = 0;
        while (running){

            try{
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)

                byte[] buffer = new byte[514];
                //Created a byte array to store the audio minus the 2 bytes for the header.

                //Was here before I was. Default lab jazz.
                DatagramPacket packet = new DatagramPacket(buffer, 0, 514);




                receiving_socket.receive(packet);

                short header = sl.getHeader(buffer);
                short pgroup = sl.getGroup(buffer);


                if(count<=16){
                    if((int) pgroup!=group){
                        if()
                        System.out.println(pgroup + " : " + group);
                        q.add(buffer);
                    }else{
                        sl.remove(buffer);

                        send[header] = sl.remove(buffer);
                    }
                    count++;
                }
                else{
                   for(int i=0; i<16; i++){
                       System.out.println("Receiver = " + Arrays.toString(send[i]));
                       if(send[i]!=null) {
                           ap.playBlock(send[i]);
                       }
                    }
                    count = 0;
                    send = new byte[16][];
                    group++;

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