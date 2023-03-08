import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;

class layer {

    byte[] add(int pos, byte[] audio){
        short header;
        ByteBuffer bb = ByteBuffer.allocate(2+audio.length);
        header = (short) pos;
        bb.putShort(header);
        bb.put(audio);
        return bb.array();
    }

    byte[] remove(byte[] payload){
        byte[] audio = new byte[payload.length-2];
        ByteBuffer bb = ByteBuffer.wrap(payload);
        // Grabs the short value from the front of the byte array
        short header = bb.getShort();
        bb.get(audio);
        return audio;
    }

    short getHeader(byte[] audio){
        ByteBuffer bb = ByteBuffer.wrap(audio);
        return bb.getShort();
    }
}




