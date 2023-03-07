import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
class sequenceLayer extends layer {


    byte[] add(int pos, int group, byte[] audio){
        short header;
        ByteBuffer bb = ByteBuffer.allocate(4+audio.length);
        bb.putShort((short) pos);
        bb.putShort((short) group);
        bb.put(audio);
        return bb.array();
    }

    short getGroup(byte[] audio){
        ByteBuffer bb = ByteBuffer.wrap(audio);
        bb.getShort();
        return bb.getShort();
    }

    DatagramPacket[] rotateLeft(DatagramPacket[] audio){
        DatagramPacket[] left = new DatagramPacket[audio.length];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                left[4 * (3 - j) + i] = audio[4 * i + j];
            }
        }


        return left;
    }

    DatagramPacket[] rotateRight(DatagramPacket[] audio){
        DatagramPacket[] right = new DatagramPacket[audio.length];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                right[4 * j + i] = audio[4 * i + j];
            }
        }
        return right;
    }
}
