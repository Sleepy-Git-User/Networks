import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
class sequenceLayer extends layer {


    byte[] add(int pos, byte[] audio){
        short header;
        ByteBuffer bb = ByteBuffer.allocate(2+audio.length);
        bb.putShort((short) pos);
        bb.put(audio);
        return bb.array();
    }


    byte[] getAudio(byte[] audio){
        byte[] audio2 = new byte[audio.length-2];
        ByteBuffer bb = ByteBuffer.wrap(audio);
        bb.getShort();
        bb.get(audio2);
        return audio2;
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
