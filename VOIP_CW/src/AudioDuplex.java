/*
 * TextDuplex.java
 */

/**
 *
 * @author  abj
 */
public class AudioDuplex {

    public static void main (String[] args){
        //Spanws 2 threads to allow audio to be sent and received.
        AudioReceiver receiver = new AudioReceiver();
        AudioSender sender = new AudioSender();

        receiver.start();
        sender.start();

    }
}