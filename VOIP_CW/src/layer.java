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

    byte[] removeTime(byte[] payload) {
        byte[] audio = new byte[payload.length - 8];
        ByteBuffer bb = ByteBuffer.wrap(payload);
        // Grabs the short value from the front of the byte array
        long time = bb.getLong();
        bb.get(audio);
        return audio;
    }

    long getTime(byte[] payload) {
        ByteBuffer bb = ByteBuffer.wrap(payload);
        long time = bb.getLong();
        return time;
    }

    short getHeader(byte[] audio) {
        ByteBuffer bb = ByteBuffer.wrap(audio);
        bb.getShort();
        return bb.getShort();
    }

    short hash(byte[] audio) {
        int sum = 0;
        for (byte b : audio) {
            sum += b & 0xFF;
        }
        return (short) (sum % 65535);
    }

    short getHash(byte[] audio) {
        ByteBuffer bb = ByteBuffer.wrap(audio);
        return bb.getShort();
    }

    int playAudio(Queue<byte[]> queue, byte[][] send, int blockNum, int i, sequenceLayer sl) throws IOException {
        //Play packet
        queue.add(send[i]);
        //System.out.println("...");
        System.out.println("Receiver " +  i  + ": " + Arrays.toString(send[i]));
        AudioReceiver.ap.playBlock(sl.getAudio(send[i]));
        if(blockNum>1){
            queue.remove();
        }
        return i;
    }

    int repetition(Queue<byte[]> queue, byte[][] send, int blockNum, int i) {
        Stack<byte[]> tempStack = new Stack<>();
        for (byte[] b : queue) {
            tempStack.push(b);
        }
        int nullCount = 0;
        int collectPacket = 0;
        int num = i;

        while (send[num] == null && num < 15) {
            nullCount++;
            num++;
        }
        if (num == 15) {
            nullCount++;
        }


        //System.out.println("packet loss amount " + nullCount);
        if (nullCount > 3) { // large amount of packet loss
            i = num;
        } else { // repeat previous packets
            byte[][] collectedP = new byte[nullCount][];
            while (nullCount != collectPacket) {
                if (blockNum == 0) { // first empty block
                    collectPacket = nullCount;
                } else {
                    if (!tempStack.empty()) {
                        collectedP[collectPacket] = tempStack.pop();
                        collectPacket++;
                    }
                }

            }
            num = i;
            for (int b = collectedP.length - 1; b >= 0; b--) { // adding in previous packets
                if (collectedP[b] != null) {
                    send[num] = collectedP[b];
                    queue.add(collectedP[b]);
                }
                num++;
            }
            if (blockNum == 0) { // first empty block
                i = 15;
            } else {
                i = num - nullCount;
            }
        }
        return i;
    }


}




