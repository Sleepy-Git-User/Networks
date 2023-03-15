/**
 *
 * @author  abj
 */
import SecurityLayer.Keys;
import SecurityLayer.RSAEncryptDecrypt;
import SecurityLayer.RSAKeyGenerator;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class rsaSender implements Runnable{

    public static RSAKeyGenerator keyGen;
    public static Keys Mykeys;
    public static boolean TheyHaveKeys;
    public static boolean dontHaveKeys = true;

    static DatagramSocket sending_socket;
    public Thread thread;
    public void start(){
        thread = new Thread(this);
        thread.start();
    }

    public void run (){

        //***************************************************
        //Port to send to
        int PORT = 55555;
        //IP ADDRESS to send to
        InetAddress clientIP = null;
        try {
            clientIP = InetAddress.getByName("localhost");  //CHANGE localhost to IP or NAME of client machine
        } catch (UnknownHostException e) {
            System.out.println("ERROR: Lab2UDP.TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }
        try{
            sending_socket = new DatagramSocket();
        } catch (SocketException e){
            System.out.println("ERROR: Lab2UDP.TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (dontHaveKeys){

            try{


                byte[] publicKey = String.valueOf(Mykeys.getModulus()).getBytes(StandardCharsets.UTF_8);
                // Creates a Bytebuffer and allocates the size to 2 bytes for the short header and then to the size of text.length
                ByteBuffer bb = ByteBuffer.allocate(512);
                // This will set the header to either 6 or 5. With 6 being picked if havekeys is true and 5 if fales.
                bb.putShort(rsaReceiver.haveKeys?(short) 6:(short) 5);

                // If we havent recieved confirmation regarding them having our keys we will put our public key in to the packet.
                if (!TheyHaveKeys){
                    bb.put(publicKey);
                    System.out.println("Sent our Public Key");
                }
                // If we have confirmation they have our keys and we have their keys, then we encrypt the exponent part of our public keys to double check.
                if (TheyHaveKeys && rsaReceiver.haveKeys){

                    BigInteger Encrypted = RSAEncryptDecrypt.encrypt(Mykeys.getPublicKey(),rsaReceiver.theirKeys.getPublicKey(),rsaReceiver.theirKeys.getModulus());
                    System.out.println("length of key" + Mykeys.getPublicKey().bitLength() + " Length of encrypted key "+ Encrypted.bitLength());
                    // After encryption we put this value in the packet to send off.
                    bb.put(String.valueOf(Encrypted).getBytes(StandardCharsets.UTF_8));
                    System.out.println("Sent Encrypted message to confirm we have eachothers keys");
                }

                // Assigns buffer to bb object
                byte[] buffer = bb.array();

                //Make a DatagramPacket from it, with client address and port number
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientIP, PORT);

                // Sends Packet
                sending_socket.send(packet);


            } catch (IOException e){
                System.out.println("ERROR: Lab2UDP.TextSender: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        sending_socket.close();
        //***************************************************
    }
}