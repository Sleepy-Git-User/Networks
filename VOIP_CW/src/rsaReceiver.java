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
   public static boolean haveTheirKeys = false;

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
                byte[] buffer = new byte[514];

                //Payload will store the public key.
                byte[] payload = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);

                receiving_socket.receive(packet);

                // Creates a ByteBuffer object and allocates it to the size of buffer.
                ByteBuffer bb = ByteBuffer.wrap(buffer);
                // Grabs the short value from the front of the byte array
                short header = bb.getShort(0);


                //***************************************************

                //***************************************************
                switch (header) {
                    case 0: // We get a packet with a public key in it
                        bb.get(payload);
                        String str = new String(payload);
                        // This makes a new key object which will store the received keys.
                        theirKeys = new Keys(new BigInteger("0"), new BigInteger("65537"),new BigInteger(str.substring(2,509).trim()));
                        haveTheirKeys = true;
                        System.out.println("Got their keys");
                        break;
                    case 1:
                        if (!rsaSender.acknowledgement){
                            bb.get(payload); // Gets the payload
                            String EncryptedAcknowledgement = new String(payload); // Converts the payload to string
                            BigInteger Decrypted = RSAEncryptDecrypt.decrypt(new BigInteger(EncryptedAcknowledgement.substring(2,509).trim()),rsaSender.Mykeys.getPrivateKey(),rsaSender.Mykeys.getModulus());
                            System.out.println("Got a message to decrypt");
                            // If the Decypted value is the same as their public key then we know we both have eachothers keys and we can now end the loop and goto the voip system.
                            if (Decrypted.equals(theirKeys.getPublicKey())){
                                rsaSender.acknowledgement = true;
                                rsaSender.finished = true;
                                running = false;
                                System.out.println("Done I think");
                            }
                        }
                        break;
                    case 2:
                        // code to execute if header is 2
                        break;
                    default:
                        // code to execute if header is none of the above values
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
            }

        //***************************************************
    }
        receiving_socket.close();
}}