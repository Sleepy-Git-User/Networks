/*
 * TextReceiverThread.java
 */

/**
 *
 * @author  abj
 */
import SecurityLayer.Keys;
import SecurityLayer.RSAEncryptDecrypt;
import SecurityLayer.xor;

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
                byte[] buffer = new byte[3000];

                //Payload will store the public key.
                byte[] payload = new byte[3000];
                byte[] smallP = new byte[100];
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
                        theirKeys = new Keys(new BigInteger("0"), new BigInteger("65537"),new BigInteger(str.substring(2,2999).trim()));
                        haveTheirKeys = true;
                        System.out.println("Got their keys");
                        break;
                    case 1:
                        if (!rsaSender.acknowledgement){
                            bb.get(payload); // Gets the payload
                            String EncryptedAcknowledgement = new String(payload); // Converts the payload to string
                            BigInteger Decrypted = RSAEncryptDecrypt.decrypt(new BigInteger(EncryptedAcknowledgement.substring(2,2999).trim()),rsaSender.Mykeys.getPrivateKey(),rsaSender.Mykeys.getModulus());
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
                        System.out.println("case 2");
                        short theirPriority = bb.getShort();
                        bb.get(smallP);
                        String theirKey = new String(payload);
                        System.out.println(theirKey);
                        BigInteger Decrypted = RSAEncryptDecrypt.decrypt(new BigInteger(theirKey.substring(0,99).trim()),rsaSender.Mykeys.getPrivateKey(),rsaSender.Mykeys.getModulus());
                        if (theirPriority > rsaSender.priority) {
                            rsaSender.priority = theirPriority;
                            rsaSender.xorKey = Decrypted.toByteArray();
                        }
                            haveTheirKeys = true;
                        rsaSender.acknowledgement = true;
                        rsaSender.haveXor = true;
                        running = false;
                        System.out.println("have xor key sroted");
                        break;
                    case 3:
                        System.out.println("header 3 here");
                        ByteBuffer encryptedTest = ByteBuffer.allocate(2);
                        int payloadTest = 0;
                        encryptedTest.putShort(bb.getShort(payloadTest));
                        byte[] ciphertext = xor.decrypt(payload, rsaSender.xorKey);
                        ByteBuffer priority = ByteBuffer.allocate(2);
                        priority.putShort(rsaSender.priority);
                        if (ciphertext.equals(priority)) {
                            System.out.println("we have the same xor key pog");
                            rsaSender.acknowledgement = true;
                            rsaSender.haveXor = true;
                        }
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