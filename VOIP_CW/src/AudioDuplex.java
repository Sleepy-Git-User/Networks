/*
 * TextDuplex.java
 */

import SecurityLayer.Keys;
import SecurityLayer.RSAKeyGenerator;
import SecurityLayer.xor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author  abj
 */
public class AudioDuplex {
    public static int DefinedPort = 55555;
    public static  InetAddress DefinedIp;

    static {
        try {
            DefinedIp = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) throws InterruptedException {

        // Creates your set of keys. Storeing them in rsa.Sender.mykeys, which can be accessed anywhere.
//        rsaSender.keyGen = new RSAKeyGenerator(2000);
//        rsaSender.Mykeys = new Keys(rsaSender.keyGen.getPrivateKey(),rsaSender.keyGen.getPublicKey(),rsaSender.keyGen.getModulus());
//        //rsaSender.xorKey = xor.generateKey(522);
//        rsaSender.xorKey = new byte[]{7, 84, -12, -117, 67, 3, 73, 28, 15, 124, -31, -20, -63, 44, 4, -59, -42, -67, -79, -58, 80, 0, -42, 25, 105, -11, -6, -56, -102, 117, 8, 126, 3, -81, 16, 44, 76, -67, -19, 13, 118, 61, 70, 76, -113, 112, 96, -92, 65, -9, 19, -111, 109, 6, 38, -19, 105, 43, -108, 43, -123, -118, 75, 81, -58, -123, 0, 33, -107, 117, 52, -66, 33, 105, 37, -10, -120, -106, 15, -20, 57, -68, -38, 99, -77, -128, 66, 64, 85, -95, 46, -1, -71, -116, 97, 109, 45, 43, 126, 91, 42, -110, -109, -36, -121, -120, -3, 26, 116, -108, 48, -124, 32, -63, -63, -84, 110, -12, -116, -90, -114, -64, 85, -70, -30, -105, 97, 27, -62, 54, -109, 77, 42, 111, -44, -53, -4, -14, -77, 100, -34, 83, -24, -115, 51, 35, -42, -35, -70, -1, -88, -55, 43, -95, -17, 59, 53, 121, -103, 78, 107, 77, 46, 47, -55, 67, -60, 112, 114, 55, -65, 83, 40, -88, -90, 71, 111, 103, -49, -12, -75, 11, 60, 51, -116, -1, -8, 49, 81, -26, 65, -66, 104, -57, -115, -10, -73, -90, -87, -6, -94, 76, -72, 21, 49, -118, 75, -60, -81, -64, 104, 86, 75, -70, -74, -48, -69, -3, -28, -118, 27, 79, 31, -10, 4, 98, 100, -29, -34, -111, 16, -81, 70, 75, -26, -16, 41, 22, 29, -88, 16, 51, -36, -23, 72, 97, 52, -123, -67, -108, 12, -68, -119, -12, -99, 120, 26, 52, 28, -67, -105, 120, 110, -127, -83, 107, -34, -46, -49, 3, 44, -69, -98, 37, 122, -47, 111, -39, 83, -97, -69, -106, 11, 32, 108, -104, -76, -94, 117, 106, -31, -13, 22, -57, -66, -51, -70, 11, -73, -124, 17, -25, 98, -5, 119, -111, -40, 48, 86, 56, 44, -99, -34, 22, 6, -37, 66, 117, 111, 63, -111, -2, 123, 13, -61, 80, 79, 102, -59, 37, 32, 99, 7, 0, -31, -27, 96, 117, 4, -37, -69, 121, 75, 68, 73, 53, 27, -109, -24, -81, 108, 106, -25, 56, -26, -16, -78, 42, -97, -25, 108, -100, 123, -58, -60, 68, 107, 91, -13, 12, -87, -67, 124, -118, 107, -65, -8, -5, -126, -119, 62, 33, -71, 122, 37, -82, -74, -85, -95, 42, 2, -74, -61, 110, -127, -123, 84, -59, 32, 52, 106, 38, -31, -3, 4, -56, 2, -124, -22, -65, -76, 105, -106, -67, -36, 101, 51, 123, 63, 4, 7, -25, 93, 42, -117, -1, -57, 17, -88, -55, 4, -17, -65, 92, -106, 69, -32, -124, -85, 46, 125, -124, -107, -8, 104, 64, -115, 118, -65, 84, -36, -57, 82, 125, 25, 68, -35, -32, 50, 80, 76, 103, -103, -68, 105, 88, 92, 72, -54, 80, -100, -16, -77, 35, 78, -66, -48, 26, 71, 8, -66, -116, -93, 48, -97, 10, -28, -84, -57, -123, -31, -119, -47, 77, -119, -56, 101, 56, -93, 7, 23, 104, 25, -84, -18, 64, -85, -122, 56, -90, -128, 27, -124, -122};
//        Random rand = new Random();
//        rsaSender.priority = (short) (rand.nextInt(1000) + 1);
//
//        // Creates the threads for the handshakes
//        rsaSender sender = new rsaSender();
//        rsaReceiver receiver = new rsaReceiver();
//
//
//        // Starts the threads for the handshakes
//        receiver.start();
//        sender.start();
//        // Will wait till the handshakes are complete before ending the threads processes
//        receiver.thread.join();
//        sender.thread.join();

        // Once the handshakes are done we spawn the threads of the voip, where we can now encrypt and decrypt data.
        AudioReceiver receiver1 = new AudioReceiver();
        AudioSender sender1 = new AudioSender();
        receiver1.start();
        sender1.start();

    }
}