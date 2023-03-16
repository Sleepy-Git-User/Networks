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
    public static boolean acknowledgement;
    public static boolean AmImissingThierKey = true;
    public static byte[] xorKey;
    public static short priority;
    public static boolean haveXor;
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
            // Creates a Bytebuffer and allocates the size to 2 bytes for the short header and then to the size of text.length

            if (!acknowledgement){ // Sends our public key with the header 0 to signify this packet holds a public key.
                ByteBuffer bb = ByteBuffer.allocate(512);
                bb.putShort((short)0);
                bb.put(publicKey);
                PS.send(bb);
                System.out.printf("sent public Key");

            }
            if (rsaReceiver.haveTheirKeys){
                ByteBuffer bb = ByteBuffer.allocate(512);
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


        //***************************************************
    }
}