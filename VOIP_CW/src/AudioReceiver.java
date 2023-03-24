/*
 * TextReceiver.java
 */

/**
 *
 * @author  abj
 */
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;


import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;
import SecurityLayer.RSAEncryptDecrypt;
import SecurityLayer.xor;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;


import javax.sound.sampled.LineUnavailableException;

public class AudioReceiver implements Runnable {
    static int DS = 2;
    static DatagramSocket2 receiving_socket;
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
            receiving_socket = new DatagramSocket2(PORT);
        } catch (SocketException e){
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.

        boolean running = true;
        int interleave = 9;
        sequenceLayer sl = new sequenceLayer();
        int count = 0; //Count to keep track of the number of packets in the array
        byte[][] send = new byte[interleave][]; //Array play packets
        HashSet<Integer> set = new HashSet<Integer>(); //Set to store the headers to check for duplicates
        HashMap<Integer, byte[]> hashmap = new HashMap<Integer, byte[]>(); //Hashmap to store the packets to be played later
        fileWriter fs = new fileWriter("RDS1.txt");
        compensation comp = new compensation(interleave);

        // for testing packet loss/corruption
        int blockNum = 0;
        Queue<byte[]> queue = new LinkedList<>(); // compensation
        while (running){
            try{
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] buffer = new byte[514];
                //Created a byte array to store the audio minus the 2 bytes for the header.

                DatagramPacket packet = new DatagramPacket(buffer, 0, 514);

                receiving_socket.receive(packet);
                    //buffer = sl.removeTime(buffer);

                short header = sl.getHeader(buffer);
                    /*
                    If the header is 3 it signifies the start of a new packet
                    and if the count is more than 15 it signifies that the array is full
                    Both cause the array to be played
                     */
                    //Increments the count
                    if (count < interleave & header != 2) {
//                    if(count<16){
                        if (set.contains((int) header)) { //Adds to hashmap if the header is already in the set
                            hashmap.put((int) header, buffer); //Stores in hashmap to be played later
                            count++;
                            continue;
                        }

                        if (header >= 0 && header < interleave - 1) {
                            send[header] = buffer; //Adds to the array to be played
                            set.add((int) header); //Adds to the set
                        }
                        count++;
                    } else {
                        byte[][] temp = send;

                        set.clear(); //Clears the set
                        count = 0; //Resets the

                        for (int i = 0; i < interleave; i++) { //Plays all the packets in the array
                            if (send[i] == null) {
                                  // datagramsocket2
                                i = comp.compensation(queue, send, blockNum, i, false); // repeating packets
                            }
                            if (send[i] != null) {
                                header = sl.getHeader(send[i]);
                                i = comp.playAudio(queue, send, blockNum, i, sl); // playing audio

                                //Checks if the packet is in the hashmap if it is add it to the array
                                if (hashmap.containsKey(i)) { //If the packet is in the hashmap remove it
                                    temp[i] = hashmap.get(i);
                                    set.add((int) sl.getHeader(hashmap.get(i)));
                                    hashmap.remove(i);
                                    count++; //Increment the count
                                } else temp[i] = null;
                            }
                        }


                        send = temp;
                        if (header >= 0 && header < interleave - 1) {
                            // checking size to case not max value of short
                                set.add((int) header); //Adds the new header to the set
                                send[header] = buffer; //Adds the new packet to the array
                        }
                        count++;
                        blockNum++;
                        System.out.println("block count " + blockNum + "\n");
                        fs.writeLine(blockNum + "\t" + System.currentTimeMillis());
                    }


            } catch (IOException e){
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }


        }
        //Close the socket
        receiving_socket.close();
        //System.out.println("Packet Loss Amount: " + packetLossAmount);
        //System.out.println("Corrupted Header: " + corruptedHeader);
        //System.out.println("Corrupted Audio: " + corrupted);
        //***************************************************
    }
}