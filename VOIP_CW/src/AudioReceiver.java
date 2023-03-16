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
    static DatagramSocket4 receiving_socket;
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
            receiving_socket = new DatagramSocket4(PORT);
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
        HashSet<Integer> set = new HashSet<Integer>(); //Set to store the headers to check for duplicates
        HashMap<Integer, byte[]> hashmap = new HashMap<Integer, byte[]>(); //Hashmap to store the packets to be played later

        fileWriter fs = new fileWriter("receiver.txt");

        // for testing packet loss/corruption
        int packetLossAmount = -16;
        int corruptedHeader = 0;
        int corrupted = 0;

        int blockNum = 0;
        Queue<byte[]> queue = new LinkedList<>(); // compensation

        while (running){


            try{
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] buffer = new byte[524];
                //Created a byte array to store the audio minus the 2 bytes for the header.

                DatagramPacket packet = new DatagramPacket(buffer, 0, 524);

                receiving_socket.receive(packet);


                byte[] ciphertext = xor.decrypt(buffer, rsaSender.xorKey);

                buffer = ciphertext;

                long timeStamp = sl.getTime(buffer);
                buffer = sl.removeTime(buffer);
//                System.out.println("Current "+System.currentTimeMillis());
//                System.out.println("Received "+timeStamp);
                //Gets header

                short hash = sl.getHash(buffer);
                short header = sl.getHeader(buffer);
                long delay = System.currentTimeMillis() - timeStamp;
                String line = header+ "," + timeStamp + ","+ delay;
                fs.writeLine(line);


                /*
                If the header is 3 it signifies the start of a new packet
                and if the count is more than 15 it signifies that the array is full
                Both cause the array to be played
                 */
                //Increments the count
                if(count<16 & header != 3){
                    if(header>15){
                        continue;
                    }
                    if(set.contains((int) header)){ //Adds to hashmap if the header is already in the set
                        hashmap.put((int) header, buffer); //Stores in hashmap to be played later
                        count++;
                        continue;
                    }

                    if(header >= 0 && header < 16) {

                        int sum = 0;
                        for(byte b : sl.getAudio(buffer)){
                            sum +=b & 0xFF; // signed byte to unsigned int
                        }
                        short newHash = (short)(sum % 65535); // checking size to case not max value of short

                        if(newHash == hash){
                            send[header] = buffer; //Adds to the array to be played
                            set.add((int) header); //Adds to the set
                        }
//                        else{
//                            corrupted++;
//                        }
                    }
//                    else{
//                        corruptedHeader++;
//                    }
                    count++;
                }

                else{
                    byte[][] temp = send;
                    count = 0; //Resets the count
                    set.clear(); //Clears the set
                    for(int i=0; i<16; i++){ //Plays all the packets in the array
                        if (send[i] == null) {
                            Stack<byte[]> tempStack = new Stack<>();
                            for(byte[] b : queue){
                                tempStack.push(b);
                            }
                            int nullCount = 0;
                            int collectPacket = 0;
                            int num = i;

                            while (send[num] == null && num< 15) {
                                nullCount++;
                                num++;
                            }
                            if(num == 15){
                                nullCount++;
                            }
                            packetLossAmount += nullCount;

                            //System.out.println("packet loss amount " + nullCount);
                            if(nullCount > 3){ // large amount of packet loss
                                i = num;
                            }
                            else{ // repeat previous packets
                                byte[][] collectedP = new byte[nullCount][];
                                while(nullCount != collectPacket){
                                    if(blockNum == 0){ // first empty block
                                        collectPacket = nullCount;
                                    }
                                    else{
                                        if(!tempStack.empty()){
                                            collectedP[collectPacket] = tempStack.pop();
                                            collectPacket++;
                                        }
                                    }

                                }
                                num = i;
                                for(int b = collectedP.length-1; b >= 0; b--){ // adding in previous packets
                                    if(collectedP[b] != null){
                                        send[num] = collectedP[b];
                                        queue.add(collectedP[b]);
                                    }
                                    num++;
                                }
                                if(blockNum == 0){ // first empty block
                                    i = 15;
                                }
                                else{
                                    i = num-nullCount;
                                }
                            }

                        }


                        if (send[i] != null) {
                           //Play packet
                            queue.add(send[i]);
                            System.out.println("...");
                            //System.out.println("Receiver " +  i  + ": " + Arrays.toString(send[i]));
                            ap.playBlock(sl.getAudio(send[i]));
                            if(blockNum>1){
                                queue.remove();
                            }
                            //Checks if the packet is in the hashmap if it is add it to the array
                            if(hashmap.containsKey(i)){ //If the packet is in the hashmap remove it
                                temp[i] = hashmap.get(i);
                                set.add((int) sl.getHeader(hashmap.get(i)));
                                hashmap.remove(i);
                                count++; //Increment the count
                            }
                            else temp[i] = null;
                        }
                    }


                    send = temp;
                    if(header >= 0 && header < 16) {
                        set.add((int) header); //Adds the new header to the set
                        send[header] = buffer; //Adds the new packet to the array
                    }

                    count++;
                    blockNum++;
//                    System.out.println("block count "  + blockNum);

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