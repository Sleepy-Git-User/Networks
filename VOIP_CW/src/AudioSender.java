/*
 * TextSender.java
 */

/**
 *
 * @author  abj
 */
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.zip.CRC32;


import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;
import SecurityLayer.RSAEncryptDecrypt;
import SecurityLayer.xor;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;


import javax.sound.sampled.LineUnavailableException;



public class AudioSender implements Runnable{

    static DatagramSocket3 sending_socket;
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
            sending_socket = new DatagramSocket3();
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
        byte[][] matrix = new byte[16][];
        int count = 0;

        sequenceLayer sl = new sequenceLayer();
        fileWriter fs = new fileWriter("sender.txt");


        while (running){
            try{

                byte[] audio = ar.getBlock();
                //int hash = Arrays.hashCode(audio);
                short hash = sl.hash(audio);
                byte[] buffer = sl.add(hash, count, audio);


                matrix[count] = buffer;
                count++;
                if(count == 16){
                    byte[][] sorted = sl.rotateLeft(matrix);
                    for (int i = 0; i < 16; i++) {
                        short header = sl.getHeader(sorted[i]);
                        sorted[i] = sl.addTime(sorted[i]);
                        fs.writeLine(header + "\t"+ sl.getTime(sorted[i]));
                        byte[] ciphertext = xor.encrypt(sorted[i], rsaSender.xorKey);
                        sorted[i] = ciphertext;

                        sending_socket.send(new DatagramPacket(sorted[i], sorted[i].length, clientIP, PORT));
                    }
                    count = 0;

                    matrix = new byte[16][];

                }

            } catch (IOException e){
                System.out.println("ERROR: TextSender: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        sending_socket.close();
        //***************************************************
    }
}