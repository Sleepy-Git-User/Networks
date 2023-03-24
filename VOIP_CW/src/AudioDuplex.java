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
import java.util.Scanner;

/**
 *
 * @author  abj
 */
public class AudioDuplex {
    public static int DefinedPort = 55555;
    public static  InetAddress DefinedIp;

    static {
        try {
            //DefinedIp = InetAddress.getByName("localhost");
            DefinedIp = InetAddress.getByName("localhost"); //laptop ip
            //DefinedIp = InetAddress.getByName("192.168.1.206"); //desktop ip
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) throws InterruptedException {

        while (!rsaSender.handShaken) {
                // Creates the threads for the handshakes
                rsaSender sender = new rsaSender();
                rsaReceiver receiver = new rsaReceiver();


                // Starts the threads for the handshakes
                receiver.start();
                sender.start();
                // Will wait till the handshakes are complete before ending the threads processes
                receiver.thread.join();
                sender.thread.join();
        }
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        System.out.println("Xor Keys Successfully exchanged :P");
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press any key to continue...");
        Thread.sleep(1000); // wait for 5 seconds
        scanner.nextLine(); // wait for user input
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");
        System.out.println("Starting Audio Threads...");
        System.out.println("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=");

        // Once the handshakes are done we spawn the threads of the voip, where we can now encrypt and decrypt data.
        AudioReceiver receiver1 = new AudioReceiver();
        AudioSender sender1 = new AudioSender();
        receiver1.start();
        sender1.start();

    }
}