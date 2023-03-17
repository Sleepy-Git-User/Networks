import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;
import java.util.Stack;

public class compensation {

    int compensation(Queue<byte[]> queue, byte[][] send, int blockNum, int i, boolean comp) {
        Stack<byte[]> tempStack = getStack(queue);

        int[] count = missingPackets(send, i);
        int num = count[1];
        int nullCount = count[0];

        if(!comp && nullCount >= 2){
            i = num;
        }
        if(comp && nullCount > 3) { // large amount of packet loss
            i = num;
        } else { // repeat previous packets
            byte[][] collectedP = getPreviousPackets(tempStack, send, i, nullCount, blockNum);
            num = i;

            num = addPreviousPackets(send, num, queue, collectedP);

            if (blockNum == 0) { // first empty block
                i = 15;
            } else {
                i = num - nullCount;
            }
        }
        return i;
    }

    int addPreviousPackets(byte[][] send, int num, Queue<byte[]> queue,byte[][] collectedP){
        for (int b = collectedP.length - 1; b >= 0; b--) { // adding in previous packets
            if (collectedP[b] != null) {
                send[num] = collectedP[b];
                queue.add(collectedP[b]);
            }
            num++;
        }
        return num;
    }

    byte[][] getPreviousPackets(Stack<byte[]> tempStack, byte[][] send, int i, int nullCount, int blockNum){
        int collectPacket = 0;
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
        return collectedP;
    }

    Stack<byte[]> getStack(Queue<byte[]> queue){
        Stack<byte[]> tempStack = new Stack<>();
        for (byte[] b : queue) {
            tempStack.push(b);
        }
        return tempStack;
    }

    int[] missingPackets(byte[][] send, int i){
        int[] count = new int[2];
        int num = i;
        int nullCount = 0;
        while (send[num] == null && num < 15) {
            nullCount++;
            num++;
        }
        if (num == 15) {
            nullCount++;
        }
        count[0] = nullCount;
        count[1] = num;
        return count;
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
}
