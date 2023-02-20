import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;


/**
 * CMPC3M06 Audio Test
 *
 *  This class is designed to test the audio player and recorder.
 *
 * @author Philip Harding
 */
public class AudioTest4 {
    public static void main(String args[]) throws Exception {
        //Vector used to store audio blocks (32ms/512bytes each)
        Vector<byte[]> voiceVector = new Vector<byte[]>();

        //Initialise AudioPlayer and AudioRecorder objects
        AudioRecorder recorder = new AudioRecorder();
        AudioPlayer player = new AudioPlayer();

        //Recording time in seconds
        int recordTime = 10;

        //Capture audio data and add to voiceVector
        System.out.println("Recording Audio...");

        for (int i = 0; i < Math.ceil(recordTime / 0.032); i++) {
            byte[] block = recorder.getBlock();
            voiceVector.add(block);
            //System.out.println(block);

        }

        //Close audio input
        recorder.close();

        for(int i = 0; i < voiceVector.size(); i++){
            Random rand = new Random();
            //int int_random = rand.nextInt(2); // 50%
            //int int_random = rand.nextInt(11); // 10%
            int int_random = rand.nextInt(101); // 1%
            if(int_random == 1){
                voiceVector.set(i, new byte[0]);
            }
        }

        //Iterate through voiceVector and play out each audio block
        System.out.println("Playing Audio...");

        Iterator<byte[]> voiceItr = voiceVector.iterator();
        while (voiceItr.hasNext()) {
            player.playBlock(voiceItr.next());
        }

        //Close audio output
        player.close();
    }
}