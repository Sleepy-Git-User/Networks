import java.nio.ByteBuffer;

public class layer {

    byte[] add(int pos, byte[] audio){
        short header;
        ByteBuffer bb = ByteBuffer.allocate(2+audio.length);
        header = (short) pos;
        bb.putShort(header);
        bb.put(audio);
        return bb.array();
    }

    byte[] remove(byte[] audio){
        byte[] payload = new byte[audio.length-2];
        System.arraycopy(audio, 2, payload, 0, audio.length-2);
        return payload;
    }

    short getHeader(byte[] audio){
        ByteBuffer bb = ByteBuffer.wrap(audio);
        return bb.getShort();
    }
}



