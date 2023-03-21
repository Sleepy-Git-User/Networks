/**
 *
 * @author  abj
 */
import SecurityLayer.Keys;
import SecurityLayer.RSAEncryptDecrypt;
import SecurityLayer.RSAKeyGenerator;
import SecurityLayer.xor;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class rsaSender implements Runnable{

    public static RSAKeyGenerator keyGen;
    public static Keys Mykeys;
    public static boolean acknowledgement;
    public static boolean AmImissingThierKey = true;
    public static byte[] xorKey;
    public static short priority;
    public static boolean haveXor = true;
    public static boolean finished;



    public Thread thread;
    public void start(){
        thread = new Thread(this);
        thread.start();
    }

    public void run (){

        while (!finished){
            try {
                packetSender PS = new packetSender(AudioDuplex.DefinedIp,AudioDuplex.DefinedPort);

            byte[] publicKey = String.valueOf(Mykeys.getModulus()).getBytes(StandardCharsets.UTF_8);
                System.out.println(publicKey.length);
            // Creates a Bytebuffer and allocates the size to 2 bytes for the short header and then to the size of text.length

            if (!acknowledgement){ // Sends our public key with the header 0 to signify this packet holds a public key.
                ByteBuffer bb = ByteBuffer.allocate(800);
                bb.putShort((short)0);
                bb.put(publicKey);
                PS.send(bb);
                System.out.println("sent public Key");

            }
            if (rsaReceiver.haveTheirKeys){
                ByteBuffer bb = ByteBuffer.allocate(800);
                bb.putShort((short) 1);
                BigInteger Encrypted = RSAEncryptDecrypt.encrypt(Mykeys.getPublicKey(),rsaReceiver.theirKeys.getPublicKey(),rsaReceiver.theirKeys.getModulus());
                bb.put(String.valueOf(Encrypted).getBytes(StandardCharsets.UTF_8));
                PS.send(bb);
                System.out.println("sent encrypted message to test");
            }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        acknowledgement = false;
        rsaReceiver.haveTheirKeys = false;

        while (!haveXor){
            try {
                packetSender PS = new packetSender(AudioDuplex.DefinedIp,AudioDuplex.DefinedPort);
                // Creates a Bytebuffer and allocates the size to 2 bytes for the short header and then to the size of text.length

                if (!acknowledgement){ // Sends our public key with the header 0 to signify this packet holds a public key.
                    ByteBuffer bb = ByteBuffer.allocate(600);
                    bb.putShort((short)2);
                    bb.putShort(priority);
                    bb.put(xorKey);
                    BigInteger Encrypted = RSAEncryptDecrypt.encrypt(new BigInteger(1,bb.array()),rsaReceiver.theirKeys.getPublicKey(),rsaReceiver.theirKeys.getModulus());
                    bb.put(String.valueOf(Encrypted).getBytes(StandardCharsets.UTF_8));
                    PS.send(bb);
                    System.out.println("sent xor Key and priority number");
                }
                if (rsaReceiver.haveTheirKeys){
                    ByteBuffer bb = ByteBuffer.allocate(522);
                    bb.putShort((short) 3);
                    byte[] bytes = ByteBuffer.allocate(2).putShort(priority).array();
                    byte[] ciphertext = xor.encrypt(bytes, rsaSender.xorKey);
                    bb.put(ciphertext);
                    PS.send(bb);
                    System.out.println("sent encrypted message to test");
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        //***************************************************
    }
}