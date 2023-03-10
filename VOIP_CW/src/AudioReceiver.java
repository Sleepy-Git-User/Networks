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
    public void run (){

        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************

        //***************************************************
        //Open a socket to receive from on port PORT

        //DatagramSocket receiving_socket;
        try{
            receiving_socket = new DatagramSocket3(PORT);
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
        byte[][] history = new byte[16][];
        byte[][] send = new byte[16][];
        HashSet<Integer> set = new HashSet<Integer>();
        Queue<byte[]> q = new LinkedList<byte[]>();
        while (running){

            try{
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] buffer = new byte[514];
                //Created a byte array to store the audio minus the 2 bytes for the header.

                //Was here before I was. Default lab jazz.
                DatagramPacket packet = new DatagramPacket(buffer, 0, 514);

                receiving_socket.receive(packet);


                short header = sl.getHeader(buffer);
                byte[] prevPacket = new byte[514];
                byte[] emptyPacket = new byte[514];

                if(count<15 & header != 3){
                    //System.out.println("Receiver " + (int) header);
                    if(set.contains((int) header)){
                        //System.out.println("Packet Lost");
                        q.add((buffer));
                        count++;
                        continue;
                    }

                    send[header] = buffer;
                    set.add((int) header);
                    count++;
                }

                else {
                    System.out.println("\n");

                    for (int i = 0; i < 16; i++) {

                        /// issue with the queue? repeating a packet that has header that doesnt exist in that set

                        if (send[i] == null) {
                            int nullCount = 0;
                            int collectPacket = 0;
                            int pivot = i-1;
                            while (send[i] == null && i< 15) {
                                nullCount++;
                                i++;
                            }
                            byte[][] collectedP = new byte[nullCount][];
                            while(nullCount != collectPacket){
                                if(pivot == - 15){
                                    collectPacket = nullCount;
                                }
                                else if((pivot)< 0){ // in history
                                    if(history[history.length+pivot] != null){
                                        collectedP[collectPacket] = history[history.length+pivot];
                                        collectPacket++;
                                    }
                                    pivot--;
                                }
                                else if((pivot)>=0){ // in send
                                    if(send[pivot] != null){
                                        collectedP[collectPacket] = send[pivot];
                                        collectPacket++;
                                    }
                                    pivot--;
                                }
                            }
                            for(int b = collectedP.length-1; b >= 0; b--){
                                if(collectedP[b] != null){
                                    System.out.println("Repeated Receiver " + " : " + Arrays.toString(collectedP[b]));
                                    ap.playBlock(sl.getAudio(collectedP[b]));
                                }
                            }

                        }
                       else if (sl.getHeader(send[i]) == (short) i) {
                        //prevPacket = send[i];
                        System.out.println("Receiver " + i + " : " + Arrays.toString(send[i]));
                        ap.playBlock(sl.getAudio(send[i]));
                    }
                }
                    history = send;
                    send = new byte[16][];
                    set.clear();
                    set.add((int) header);
                    send[header] = buffer;
                    count = 1;
                    //System.out.println("Checking Queue");
                    for(int i=0; i<q.size(); i++){
                        if(q.peek() != null){
                            //System.out.println("Receiver " + sl.getHeader(q.peek()));
                            set.add((int) sl.getHeader(q.peek()));
                            send[sl.getHeader(q.peek())] = q.poll();

                            count++;
                        }
                    }


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