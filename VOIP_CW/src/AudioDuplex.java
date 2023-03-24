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
        AudioReceiver receiver1 = new AudioReceiver();
        AudioSender sender1 = new AudioSender();
        receiver1.start();
        sender1.start();

    }
}