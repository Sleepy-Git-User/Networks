import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

class sequenceLayer extends layer {


    byte[] add(int hash, int pos, byte[] audio){
        short header;
        ByteBuffer bb = ByteBuffer.allocate(4+audio.length);
        bb.putShort((short) hash);
        bb.putShort((short) pos);
        bb.put(audio);
        return bb.array();
    }

    byte[] getAudio(byte[] audio){
        byte[] audio2 = new byte[audio.length-4];
        ByteBuffer bb = ByteBuffer.wrap(audio);
        bb.getShort();
        bb.getShort();
        bb.get(audio2);
        return audio2;
    }

    byte[][] rotateLeft(byte[][] audio){
        byte[][] left = new byte[audio.length][];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                left[4 * (3 - j) + i] = audio[4 * i + j];
                
            }
        }
        return left;
    }
}
