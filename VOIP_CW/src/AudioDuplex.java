/*
 * TextDuplex.java
 */

/**
 *
 * @author  abj
 */
public class AudioDuplex {

    public static void main (String[] args){

        AudioReceiver receiver = new AudioReceiver();
        AudioSender sender = new AudioSender();

        receiver.start();
        sender.start();

    }
}