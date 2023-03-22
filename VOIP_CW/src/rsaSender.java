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
import java.util.Random;

public class rsaSender implements Runnable{

    public static RSAKeyGenerator keyGen;
    public static Keys Mykeys;
    public static boolean acknowledgement;
    public static byte[] xorKey;
    public static short priority;
    public static boolean haveXor;
    public static boolean finished;
    public static int attempts;

    // Creates your set of keys. Storeing them in rsa.Sender.mykeys, which can be accessed anywhere.


    public Thread thread;
    public void start(){
        thread = new Thread(this);
        thread.start();
    }

    public void run (){
        keyGen = new RSAKeyGenerator(4176);
        Mykeys = new Keys(keyGen.getPrivateKey(),keyGen.getPublicKey(),keyGen.getModulus());
        xorKey = xor.generateKey(522);
        //rsaSender.xorKey = new byte[]{7, 84, -12, -117, 67, 3, 73, 28, 15, 124, -31, -20, -63, 44, 4, -59, -42, -67, -79, -58, 80, 0, -42, 25, 105, -11, -6, -56, -102, 117, 8, 126, 3, -81, 16, 44, 76, -67, -19, 13, 118, 61, 70, 76, -113, 112, 96, -92, 65, -9, 19, -111, 109, 6, 38, -19, 105, 43, -108, 43, -123, -118, 75, 81, -58, -123, 0, 33, -107, 117, 52, -66, 33, 105, 37, -10, -120, -106, 15, -20, 57, -68, -38, 99, -77, -128, 66, 64, 85, -95, 46, -1, -71, -116, 97, 109, 45, 43, 126, 91, 42, -110, -109, -36, -121, -120, -3, 26, 116, -108, 48, -124, 32, -63, -63, -84, 110, -12, -116, -90, -114, -64, 85, -70, -30, -105, 97, 27, -62, 54, -109, 77, 42, 111, -44, -53, -4, -14, -77, 100, -34, 83, -24, -115, 51, 35, -42, -35, -70, -1, -88, -55, 43, -95, -17, 59, 53, 121, -103, 78, 107, 77, 46, 47, -55, 67, -60, 112, 114, 55, -65, 83, 40, -88, -90, 71, 111, 103, -49, -12, -75, 11, 60, 51, -116, -1, -8, 49, 81, -26, 65, -66, 104, -57, -115, -10, -73, -90, -87, -6, -94, 76, -72, 21, 49, -118, 75, -60, -81, -64, 104, 86, 75, -70, -74, -48, -69, -3, -28, -118, 27, 79, 31, -10, 4, 98, 100, -29, -34, -111, 16, -81, 70, 75, -26, -16, 41, 22, 29, -88, 16, 51, -36, -23, 72, 97, 52, -123, -67, -108, 12, -68, -119, -12, -99, 120, 26, 52, 28, -67, -105, 120, 110, -127, -83, 107, -34, -46, -49, 3, 44, -69, -98, 37, 122, -47, 111, -39, 83, -97, -69, -106, 11, 32, 108, -104, -76, -94, 117, 106, -31, -13, 22, -57, -66, -51, -70, 11, -73, -124, 17, -25, 98, -5, 119, -111, -40, 48, 86, 56, 44, -99, -34, 22, 6, -37, 66, 117, 111, 63, -111, -2, 123, 13, -61, 80, 79, 102, -59, 37, 32, 99, 7, 0, -31, -27, 96, 117, 4, -37, -69, 121, 75, 68, 73, 53, 27, -109, -24, -81, 108, 106, -25, 56, -26, -16, -78, 42, -97, -25, 108, -100, 123, -58, -60, 68, 107, 91, -13, 12, -87, -67, 124, -118, 107, -65, -8, -5, -126, -119, 62, 33, -71, 122, 37, -82, -74, -85, -95, 42, 2, -74, -61, 110, -127, -123, 84, -59, 32, 52, 106, 38, -31, -3, 4, -56, 2, -124, -22, -65, -76, 105, -106, -67, -36, 101, 51, 123, 63, 4, 7, -25, 93, 42, -117, -1, -57, 17, -88, -55, 4, -17, -65, 92, -106, 69, -32, -124, -85, 46, 125, -124, -107, -8, 104, 64, -115, 118, -65, 84, -36, -57, 82, 125, 25, 68, -35, -32, 50, 80, 76, 103, -103, -68, 105, 88, 92, 72, -54, 80, -100, -16, -77, 35, 78, -66, -48, 26, 71, 8, -66, -116, -93, 48, -97, 10, -28, -84, -57, -123, -31, -119, -47, 77, -119, -56, 101, 56, -93, 7, 23, 104, 25, -84, -18, 64, -85, -122, 56, -90, -128, 27, -124, -122};
        Random rand = new Random();
        priority = (short) (rand.nextInt(1000) + 1);
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        System.out.println("Starting RSA Key Exchange");
        while (!finished){
            try {
                packetSender PS = new packetSender(AudioDuplex.DefinedIp,AudioDuplex.DefinedPort);

            byte[] publicKey = String.valueOf(Mykeys.getModulus()).getBytes(StandardCharsets.UTF_8);
                //System.out.println(publicKey.length);
            // Creates a Bytebuffer and allocates the size to 2 bytes for the short header and then to the size of text.length

            if (!acknowledgement){ // Sends our public key with the header 0 to signify this packet holds a public key.
                ByteBuffer bb = ByteBuffer.allocate(3000);
                bb.putShort((short)0);
                bb.put(publicKey);
                PS.send(bb);
                //System.out.println("sent public Key");

            }
            if (rsaReceiver.haveTheirKeys){
                ByteBuffer bb = ByteBuffer.allocate(3000);
                bb.putShort((short) 1);
                BigInteger Encrypted = RSAEncryptDecrypt.encrypt(Mykeys.getPublicKey(),rsaReceiver.theirKeys.getPublicKey(),rsaReceiver.theirKeys.getModulus());
                bb.put(String.valueOf(Encrypted).getBytes(StandardCharsets.UTF_8));
                PS.send(bb);
                //System.out.println("sent encrypted message to test");
            }
                try {
                    Thread.sleep(1000); // sleep for 1 second
                } catch (InterruptedException e) {
                    // handle the exception
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        acknowledgement = false;
        rsaReceiver.haveTheirKeys = false;
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        System.out.println("Key Exchange Successful :D ");
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        System.out.println("Starting Xor Key Exchange");
        while (!haveXor){
            try {
                packetSender PS = new packetSender(AudioDuplex.DefinedIp,AudioDuplex.DefinedPort);
                // Creates a Bytebuffer and allocates the size to 2 bytes for the short header and then to the size of text.length

                if (!acknowledgement){ // Sends our public key with the header 0 to signify this packet holds a public key.

                    BigInteger Encrypted = RSAEncryptDecrypt.encrypt(new BigInteger(xorKey),rsaReceiver.theirKeys.getPublicKey(),rsaReceiver.theirKeys.getModulus());
                    ByteBuffer bb = ByteBuffer.allocate(4+String.valueOf(Encrypted).getBytes(StandardCharsets.UTF_8).length);
                    bb.putShort((short)2);
                    bb.putShort(priority);
                    bb.put(String.valueOf(Encrypted).getBytes(StandardCharsets.UTF_8));
                    //System.out.println(String.valueOf(Encrypted).length());
                    PS.send(bb);
                    //System.out.println("sent xor Key and priority number");
                }
                if (rsaReceiver.haveTheirKeys){
                    ByteBuffer prioritybb = ByteBuffer.allocate(2+522);
                    prioritybb.putShort(priority);
                    prioritybb.put(xorKey);
                    byte[] ciphertext = xor.encrypt(prioritybb.array(), rsaSender.xorKey);
                    ByteBuffer bb = ByteBuffer.allocate(2+ciphertext.length);
                    bb.putShort((short) 3);
                    bb.put(ciphertext);
                    PS.send(bb);
                    //System.out.println("sent xor message to test");
                }
                try {
                    Thread.sleep(1000); // sleep for 1 second
                } catch (InterruptedException e) {
                    // handle the exception
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        System.out.println("Xor Keys Successfully exchanged :P");
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");

        //***************************************************
    }
}