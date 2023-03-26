import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Queue;
import java.util.Stack;

class layer {

    byte[] add(int hash, int pos, byte[] audio) {
        short header;
        ByteBuffer bb = ByteBuffer.allocate(4 + audio.length);
        bb.putShort((short) hash);
        bb.putShort((short) pos);
        bb.put(audio);
        return bb.array();
    }

    byte[] addTime(byte[] audio) {
        long time = System.currentTimeMillis();
//        System.out.println("Time :"+time);
        ByteBuffer bb = ByteBuffer.allocate(8 + audio.length);
        bb.putLong(time);
        bb.put(audio);
        return bb.array();
    }

    byte[] remove(byte[] payload) {
        byte[] audio = new byte[payload.length - 4];
        ByteBuffer bb = ByteBuffer.wrap(payload);
        // Grabs the short value from the front of the byte array
        short hash = bb.getShort();
        short header = bb.getShort();
        bb.get(audio);
        return audio;
    }

    short getHeader(byte[] audio) {
        ByteBuffer bb = ByteBuffer.wrap(audio);
        return bb.getShort();
    }

    short hash(byte[] audio) {
        int sum = 0;
        for (byte b : audio) {
            sum += b & 0xFF; // convert signed byte to unsigned int
        }
        return (short) (sum % 65535); // max value of short
    }

    short getHash(byte[] audio) {
        ByteBuffer bb = ByteBuffer.wrap(audio);
        return bb.getShort();
    }




}




