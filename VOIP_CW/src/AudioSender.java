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

    static DatagramSocket2 sending_socket;
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
        int PORT = AudioDuplex.DefinedPort;
        //IP ADDRESS to send to
        InetAddress clientIP = AudioDuplex.DefinedIp;
        //***************************************************

        //***************************************************
        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.

        //DatagramSocket sending_socket;
        try{
            sending_socket = new DatagramSocket2();
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
        int block = 0;
        int interleave = 9;
        byte[][] matrix = new byte[interleave][];
        int count = 0;


        sequenceLayer sl = new sequenceLayer();
        fileWriter fs = new fileWriter("SDS.txt");

        try {
            fs.writeLine(block + "\t"+ System.currentTimeMillis());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (running){
            try{
                byte[] audio = ar.getBlock();
                    byte[] buffer = sl.add(count, audio);
                    matrix[count] = buffer;

                    byte[][] sorted;
                    count++;
                    if (count >= interleave) {
                        sorted = sl.rotateLeft(matrix);
                        for (byte[] bytes : sorted) {

                            sending_socket.send(new DatagramPacket(bytes, bytes.length, clientIP, PORT));
                            //2 hash 2 header 512 audio 4 int 4 int
                        }
                        count = 0;
                        matrix = new byte[interleave][];
                        fs.writeLine(block + "\t"+ System.currentTimeMillis());
                        block++;
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