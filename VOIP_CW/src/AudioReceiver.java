/*
 * TextReceiver.java
 */

/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import java.util.*;
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
        sequenceLayer sl = new sequenceLayer();
        int count = 0; //Count to keep track of the number of packets in the array
        byte[][] send = new byte[16][]; //Array play packets
        byte[][] history = new byte[16][]; //Array to store the played packets
        HashSet<Integer> set = new HashSet<Integer>(); //Set to store the headers to check for duplicates
        HashMap<Integer, byte[]> hashmap = new HashMap<Integer, byte[]>(); //Hashmap to store the packets to be played later
        while (running){

            try{
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] buffer = new byte[514];
                //Created a byte array to store the audio minus the 2 bytes for the header.

                //Was here before I was. Default lab jazz.
                DatagramPacket packet = new DatagramPacket(buffer, 0, 514);

                receiving_socket.receive(packet);

                //Gets header
                short header = sl.getHeader(buffer);

                /*
                If the header is 3 it signifies the start of a new packet
                and if the count is more than 15 it signifies that the array is full
                Both cause the array to be played
                 */
                if(count<15 & header != 3){
                    //System.out.println("Receiver " + (int) header);
                    if(set.contains((int) header)){ //Adds to hashmap if the header is already in the set
//                        System.out.println("Packet Lost");
                        hashmap.put((int) header, buffer); //Stores in hashmap to be played later
                        count++;
                        continue;
                    }

                    send[header] = buffer; //Adds to the array to be played
                    set.add((int) header); //Adds to the set
                    count++;
                }

                else{
                    for(int i=0; i<16; i++){ //Plays all the packets in the array
                        count = 0; //Resets the count
//                       System.out.println("Receiver " +  Arrays.toString(send[i]));
                        history[i] = send[i];
                        //Stores the packets in the history array
                       //Which can be used for compensation
                        if (send[i] != null) {
                           //Play packet
                            if (sl.getHeader(send[i]) == (short) i) {
                                ap.playBlock(sl.getAudio(send[i]));
                               //Checks if the packet is in the hashmap if it is add it to the array
                                send[i] = hashmap.getOrDefault(i, null); //If not add null to the array
                                if(hashmap.remove(i) != null){ //If the packet is in the hashmap remove it
                                    count++; //Increment the count
                                }
//                               send[i] = hashmap.get(i);
                            }
                        }
                    }
                    set.clear(); //Clears the set
                    set.add((int) header); //Adds the new header to the set
                    send[header] = buffer; //Adds the new packet to the array
                    count++; //Increments the count
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