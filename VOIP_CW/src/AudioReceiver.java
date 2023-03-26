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
    static int DS = 3;
    static DatagramSocket3 receiving_socket;
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
    public void run () {
        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************

        //***************************************************
        //Open a socket to receive from on port PORT

        //DatagramSocket receiving_socket;
        try {
            receiving_socket = new DatagramSocket3(PORT);
        } catch (SocketException e) {
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Main loop.

        boolean running = true;
        int interleave = 0;
        sequenceLayer sl = new sequenceLayer();
        int count = 0; //Count to keep track of the number of packets in the array
        byte[][] send = new byte[4][]; //Array play packets
        byte[][] temp = new byte[4][]; //Array play packets
        HashSet<Integer> set = new HashSet<Integer>(); //Set to store the headers to check for duplicates
        HashMap<Integer, byte[]> hashmap = new HashMap<Integer, byte[]>(); //Hashmap to store the packets to be played later
        fileWriter fs = new fileWriter("RDS3.txt");
        compensation comp = new compensation(interleave);

        // for testing packet loss/corruption
        int blockNum = 0;
        Queue<byte[]> queue = new LinkedList<>(); // compensation
        boolean decrypt = false;
        int block = 0;
        byte[] prev = new byte[514];
        while (running) {


            try {
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] buffer = new byte[514];
                //Created a byte array to store the audio minus the 2 bytes for the header.

                DatagramPacket packet = new DatagramPacket(buffer, 0, 514);

                receiving_socket.receive(packet);


                if (receiving_socket instanceof DatagramSocket) {
                    System.out.println("Header : " + Arrays.toString(buffer));
                    if (count <= 4) {
                        if (set.contains((int) sl.getHeader(buffer)))
                            temp[sl.getHeader(buffer)] = buffer;
                        else {
                            send[sl.getHeader(buffer)] = buffer;
                            set.add((int) sl.getHeader(buffer));
                        }
                    }
                    if (count == 4) {
                        for (int i = 0; i < 4; i++) {
                            fs.writeLine(i + "\t" + System.currentTimeMillis());
                            if (send[i] == null) ap.playBlock(sl.getAudio(send[i - 1]));
                            else ap.playBlock(sl.getAudio(send[i]));
                        }
                        count = 0;
                        set.clear();
                        for(int x= 0; x<temp.length; x++){
                            if(temp[x]!=null){
                                send[x] = temp[x];
                                set.add(x);
                            }
                        }
                        temp = new byte[4][];

                    }
                    count++;
                }
//                    if (sl.getHeader(buffer) != count) {
//                        if(prev!=null){
//                            ap.playBlock(sl.getAudio(prev));
//                        }
//                        count = sl.getHeader(buffer);
//
//                    } else {
//                        short header = sl.getHeader(buffer);
//                        System.out.println(header);
//
//                            fs.writeLine(header + "\t" + System.currentTimeMillis());
//                            ap.playBlock(sl.getAudio(buffer));
//                            prev = buffer;
//                    }
//                    count++;
//                }

                //buffer = sl.removeTime(buffer);


//                if (decrypt) {
////                    byte[] ciphertext = xor.decrypt(buffer, rsaSender.xorKey);
//
////                    buffer = ciphertext;
//
//
////                    buffer = sl.removeTime(buffer);
//                    //                System.out.println("Current "+System.currentTimeMillis());
//                    //                System.out.println("Received "+timeStamp);
//                    //Gets header
//
//                    short hash = sl.getHash(buffer);
//                    short header = sl.getHeader(buffer);
//                    /*
//                    If the header is 3 it signifies the start of a new packet
//                    and if the count is more than 15 it signifies that the array is full
//                    Both cause the array to be played
//                     */
//                    //Increments the count
//                    if (count < interleave & header != 3) {
////                    if(count<16){
//                        if (header > 3) {
//                            continue;
//                        }
//                        if (set.contains((int) header)) { //Adds to hashmap if the header is already in the set
//                            hashmap.put((int) header, buffer); //Stores in hashmap to be played later
//                            count++;
//                            continue;
//                        }
//
//                        if (header >= 0 && header < interleave - 1) {
//                            short newHash = sl.hash(sl.getAudio(buffer)); // checking size to case not max value of short
//                            if (newHash == hash) {
//                                send[header] = buffer; //Adds to the array to be played
//                                set.add((int) header); //Adds to the set
//                            }
//                        }
//                        count++;
//                    } else {
//                        byte[][] temp = send;
//
//                        set.clear(); //Clears the set
//                        count = 0; //Resets the
//
//                        for (int i = 0; i < interleave; i++) { //Plays all the packets in the array
//                            if (send[i] == null) {
//                                if (DS == 1 || DS == 3 || DS == 4) { // datagramsocket1/3/4
//                                    i = comp.compensation(queue, send, blockNum, i, true); // repeating packets
//
//                                } else if (DS == 2) { // datagramsocket2
//                                    i = comp.compensation(queue, send, blockNum, i, false); // repeating packets
//                                }
//
//                            }
//                            if (send[i] != null) {
//                                i = comp.playAudio(queue, send, blockNum, i, sl); // playing audio
//
//                                //Checks if the packet is in the hashmap if it is add it to the array
//                                if (hashmap.containsKey(i)) { //If the packet is in the hashmap remove it
//                                    temp[i] = hashmap.get(i);
//                                    set.add((int) sl.getHeader(hashmap.get(i)));
//                                    hashmap.remove(i);
//                                    count++; //Increment the count
//                                } else temp[i] = null;
//                            }
//                        }
//
//
//                        send = temp;
//                        if (header >= 0 && header < interleave - 1) {
//                            short newHash = sl.hash(sl.getAudio(buffer)); // checking size to case not max value of short
//                            if (newHash == hash) {
//                                set.add((int) header); //Adds the new header to the set
//                                send[header] = buffer; //Adds the new packet to the array
//                            }
//
//                        }
//
//                        count++;
//                        blockNum++;
//                        System.out.println("block count " + blockNum + "\n");
//
//                    }
//                } else {
//                    if (set.contains((int) sl.getHeader(buffer))) {
//                        count = interleave + 1;
//
//                    } else {
//                        set.add((int) sl.getHeader(buffer));
//                        send[sl.getHeader(buffer)] = buffer;
//                        count++;
//                    }
//                    if (count >= interleave) {
//                        if (block < 30) {
//                            fs.writeLine(block + "\t" + sl.getHeader(buffer) + "\t" + System.currentTimeMillis());
//                            block++;
//                        }
//                        for (int i = 0; i < interleave; i++) {
//
//                            if (send[i] == null) {
//                                if (i + 1 < interleave && send[i + 1] != null) {
//                                    System.out.println("Replaying next :" + (i));
//                                    send[i] = send[i + 1];
//                                } else {
//                                    System.out.println("Replaying prev :" + (i));
//                                    ap.playBlock(sl.getAudio(send[i - 1]));
//                                }
//                            } else {
//                                System.out.println("Playing :" + (i));
//                                ap.playBlock(sl.getAudio(send[i]));
//                            }
//                        }
//
//                        set.clear();
//                        send = new byte[interleave][];
//                        if (count > interleave) {
//                            send[sl.getHeader(buffer)] = buffer;
//                            set.add((int) sl.getHeader(buffer));
//                            count = 1;
//                        } else {
//                            count = 0;
//                        }
//                    }
//                }
//



        } catch(IOException e){
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