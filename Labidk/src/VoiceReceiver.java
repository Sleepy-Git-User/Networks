/*
 * TextReceiver.java
 */

/**
 *
 * @author  abj
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;

public class VoiceReceiver {

    static DatagramSocket receiving_socket;

    public static void main (String[] args){

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
                AudioPlayer player = new AudioPlayer();

                receiving_socket.setSoTimeout(500);
                receiving_socket.receive(packet);


                player.playBlock(buffer);

            } catch(SocketTimeoutException e){
                System.out.println(".");
            } catch (IOException e){
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }

        }
        //Close the socket
        receiving_socket.close();
        //***************************************************
    }
}