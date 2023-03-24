import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

class sequenceLayer extends layer {


    byte[] add(int hash, int pos, byte[] audio){
        short header;
        ByteBuffer bb = ByteBuffer.allocate(516);
        bb.putShort((short) hash);
        bb.putShort((short) pos);
        bb.put(audio);
        return bb.array();
    }
    byte[] addLast(byte[] audio, byte[] last){
        ByteBuffer bb = ByteBuffer.allocate(1028);
        bb.put(audio);
        bb.put(last);
        return bb.array();
    }

    byte[] getAudio(byte[] audio){
        byte[] audio2 = new byte[audio.length-4];
        ByteBuffer bb = ByteBuffer.wrap(audio);
        bb.getShort();
        bb.getShort();
        bb.get(audio2, 0, 512);
        return audio2;
    }

    byte[] getLast(byte[] audio){
        byte[] last = new byte[512];
        ByteBuffer bb = ByteBuffer.wrap(audio);
        bb.getShort();
        bb.getShort();
        bb.position(512);
        bb.get(last, 0, 512);
        return last;
    }

    byte[][] rotateLeft(byte[][] audio){
        byte[][] left = new byte[audio.length][];

        if(audio.length==4){
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    left[2 * (1 - j) + i] = audio[2 * i + j];
                }
            }
            return left;
        }else if(audio.length==9){
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    left[3 * (2 - j) + i] = audio[3 * i + j];
                }
            }
            return left;
        }else if(audio.length==16){
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    left[4 * (3 - j) + i] = audio[4 * i + j];
                }
            }
            return left;
        }else{
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    left[5 * (4 - j) + i] = audio[5 * i + j];
                }
            }
            return left;
        }
    }
}
