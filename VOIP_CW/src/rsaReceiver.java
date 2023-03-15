/*
 * TextReceiverThread.java
 */

/**
 *
 * @author  abj
 */
import SecurityLayer.Keys;
import SecurityLayer.RSAEncryptDecrypt;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class rsaReceiver implements Runnable{

   public static Keys theirKeys;
   public static boolean haveKeys = false;

    static DatagramSocket receiving_socket;

    public Thread thread;
    public void start(){
        thread = new Thread(this);
        thread.start();
    }

    public void run (){

        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************

        //***************************************************

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
                //Receive a DatagramPacket of 512 bytes
                byte[] buffer = new byte[512];

                //Payload will store the public key.
                byte[] publicKey = new byte[510];
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);

                receiving_socket.receive(packet);
                // This trims the buffer of the unused bytes at the end
                byte[] filterdBuffer = new String(buffer).trim().getBytes();

                // Creates a ByteBuffer object and allocates it to the size of buffer.
                ByteBuffer bb = ByteBuffer.wrap(buffer);
                // Grabs the short value from the front of the byte array
                short header = bb.getShort(0);

                //***************************************************

                //***************************************************

                // If we have their keys AND we have confirmation they have our keys then this run to confirm we do infact have eachothers keys.
                if (rsaSender.TheyHaveKeys && haveKeys){
                    // This gets the Public Key
                    bb.get(publicKey);
                    // Converts the publicKey in to a string.
                    String str = new String(publicKey);
                    // This will decrypt the encrypted exponent part of their public key, which they encrypted using our public key.
                    BigInteger Decrypted = RSAEncryptDecrypt.decrypt(new BigInteger(str.substring(2,509).trim()),rsaSender.Mykeys.getPrivateKey(),rsaSender.Mykeys.getModulus());
                    //System.out.println(Decrypted);

                    // If the Decypted value is the same as their public key then we know we both have eachothers keys and we can now end the loop and goto the voip system.
                    if (Decrypted.equals(theirKeys.getPublicKey())){
                        System.out.println("We both have confirmed we have eachothers keys. RSA DONE");
                        rsaSender.dontHaveKeys = false;
                        running = false;
                    }
                }

                //***************************************************

                //***************************************************

                // The header is set to 6 to say that they recieved our public key, and we can move on to confirming this.
                if (header == 6){
                    rsaSender.TheyHaveKeys = true;
                    System.out.println("They got our Keys");
                }
                // The header is set to 5 when communication first begins, this is to let the reciever know that the packet contains a public key.
                if (header == 5){
                    System.out.println("We got their public Key");
                    bb.get(publicKey);
                    String str = new String(publicKey);
                    // This makes a new key object which will store the recieved keys.
                    theirKeys = new Keys(new BigInteger("0"), new BigInteger("65537"),new BigInteger(str.substring(2,509).trim()));
                    haveKeys = true;

            }
        } catch (IOException e) {
                e.printStackTrace();
            }

        //***************************************************
    }
        receiving_socket.close();
}}