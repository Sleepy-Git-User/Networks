import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;
import java.util.Stack;

public class compensation {
    fileWriter fs;

    public compensation() {
        this.fs = new fileWriter("ds4r.txt");
    }

    int compensation(Queue<byte[]> queue, byte[][] send, int blockNum, int i, boolean comp) {
        Stack<byte[]> tempStack = getStack(queue); // converting queue to stack

        int[] count = missingPackets(send, i); // calculating amount of packets lost
        int num = count[1]; // next packet to play after loss
        int nullCount = count[0]; // number of packets lost together

        if(!comp){ // compensation for datagramsocket2
            if(nullCount >= 2){ // so only repeat single packet loss
                i = num; // move to next packet
            }
            else { // repeat previous packets
                byte[][] collectedP = getPreviousPackets(tempStack, send, i, nullCount, blockNum); // collecting previous packets
                num = i; // start from when packet is lost

                num = addPreviousPackets(send, num, queue, collectedP); // adding previous packets to array and queue

                if (blockNum == 0) { // first empty block
                    i = 15; // move on
                } else {
                    i = num - nullCount; // return counter from for loop back to play repeated packets
                }
            }
        }
        if(comp){ // compensation for datagramsocket3
            if(nullCount > 3) { // so only repeat for less than 3 packet loss
                i = num; // move to next packet
            }
            else { // repeat previous packets
                byte[][] collectedP = getPreviousPackets(tempStack, send, i, nullCount, blockNum); // collecting previous packets
                num = i; // start from when packet is lost

                num = addPreviousPackets(send, num, queue, collectedP); // adding previous packets to array and queue

                if (blockNum == 0) { // first empty block
                    i = 15;
                } else {
                    i = num - nullCount; // return counter from for loop back to play repeated packets
                }
            }
        }

        return i; // returning position to play from
    }

    int addPreviousPackets(byte[][] send, int num, Queue<byte[]> queue,byte[][] collectedP){
        for (int b = collectedP.length - 1; b >= 0; b--) { // looping packets collected
            if (collectedP[b] != null) {
                send[num] = collectedP[b]; // adding in previous packets to play
                queue.add(collectedP[b]); // adding to queue so can repeat in future
            }
            num++;
        }
        return num; // position
    }

    byte[][] getPreviousPackets(Stack<byte[]> tempStack, byte[][] send, int i, int nullCount, int blockNum){
        int collectPacket = 0; // counter to loop
        byte[][] collectedP = new byte[nullCount][]; // to store packets to repeat
        while (nullCount != collectPacket) { // looping for amount lost
            if (blockNum == 0) { // first empty block
                collectPacket = nullCount; // stopping loop
            } else {
                if (!tempStack.empty()) { // no previous audio
                    collectedP[collectPacket] = tempStack.pop(); // getting previous packet + removing
                    collectPacket++;
                }
            }
        }
        return collectedP;
    }

    Stack<byte[]> getStack(Queue<byte[]> queue){ // converting from queue to stack
        Stack<byte[]> tempStack = new Stack<>();
        for (byte[] b : queue) {
            tempStack.push(b); // adding all elements
        }
        return tempStack;
    }

    int[] missingPackets(byte[][] send, int i){ // for number packets lost
        int[] count = new int[2]; // for returning
        int num = i; // storing position
        int nullCount = 0; // number of packets lost
        while (send[num] == null && num < 15) { // looping until next packet isn't null
            nullCount++;
            num++;
        }
        if (num == 15) { // final packet loss
            nullCount++;
        }
        // returning
        count[0] = nullCount;
        count[1] = num;
        return count;
    }

    int playAudio(Queue<byte[]> queue, byte[][] send, int blockNum, int i, sequenceLayer sl) throws IOException { //Play packet
        queue.add(send[i]); // adding audio played to queue to be able to repeat
        //System.out.println("...");
        System.out.println("Receiver " +  i  + ": " + Arrays.toString(send[i]));
        fs.writeLine(0+ "\t"+ System.currentTimeMillis());
        AudioReceiver.ap.playBlock(sl.getAudio(send[i])); // playing audio
        if(blockNum>1){ // don't remove elements until at least 1 block played
            queue.remove(); // remove a packet
        }
        return i;
    }
}
