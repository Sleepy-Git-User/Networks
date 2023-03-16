import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;

class layer {

    byte[] add(int hash, int pos, byte[] audio){
        short header;
        ByteBuffer bb = ByteBuffer.allocate(4+audio.length);
        bb.putShort((short) hash);
        bb.putShort((short) pos);
        bb.put(audio);
        return bb.array();
    }

    byte[] remove(byte[] payload){
        byte[] audio = new byte[payload.length-4];
        ByteBuffer bb = ByteBuffer.wrap(payload);
        // Grabs the short value from the front of the byte array
        short hash = bb.getShort();
        short header = bb.getShort();
        bb.get(audio);
        return audio;
    }

    short getHeader(byte[] audio){
        ByteBuffer bb = ByteBuffer.wrap(audio);
        bb.getShort();
        return bb.getShort();
    }
    short getHash(byte[] audio){
        ByteBuffer bb = ByteBuffer.wrap(audio);
        return bb.getShort();
    }

}




