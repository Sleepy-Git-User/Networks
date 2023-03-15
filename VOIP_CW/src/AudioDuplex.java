/*
 * TextDuplex.java
 */

import SecurityLayer.Keys;
import SecurityLayer.RSAKeyGenerator;

/**
 *
 * @author  abj
 */
public class AudioDuplex {

    public static void main (String[] args) throws InterruptedException {

        // Creates your set of keys. Storeing them in rsa.Sender.mykeys, which can be accessed anywhere.
        rsaSender.keyGen = new RSAKeyGenerator(1024);
        rsaSender.Mykeys = new Keys(rsaSender.keyGen.getPrivateKey(),rsaSender.keyGen.getPublicKey(),rsaSender.keyGen.getModulus());

        // Creates the threads for the handshakes
        rsaSender sender = new rsaSender();
        rsaReceiver receiver = new rsaReceiver();


        // Starts the threads for the handshakes
        receiver.start();
        sender.start();
        // Will wait till the handshakes are complete before ending the threads processes
        receiver.thread.join();
        sender.thread.join();

        // Once the handshakes are done we spawn the threads of the voip, where we can now encrypt and decrypt data.
        AudioReceiver receiver1 = new AudioReceiver();
        AudioSender sender1 = new AudioSender();
        receiver1.start();
        sender1.start();

    }
}