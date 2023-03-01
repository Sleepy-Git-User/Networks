import java.net.DatagramPacket;

public class sequenceNumbering {
    byte[] header = new byte[4];
    byte[] buffer = new byte[512];
    public static byte[] generateHeader(int pos){
        byte[] header = new byte[4];
        header[0] = (byte) (pos >> 24);
        header[1] = (byte) (pos >> 16);
        header[2] = (byte) (pos >> 8);
        header[3] = (byte) (pos);
        return header;
    }

    public static byte[] generatePayload(byte[] audio, int pos){
        byte[] payload = new byte[516];
        byte[] header = generateHeader(pos);
        System.arraycopy(header, 0, payload, 0, 4);
        System.arraycopy(audio, 0, payload, 4, 512);
        return payload;
    }
    public static DatagramPacket[] rotateLeft(DatagramPacket[] grid){
        DatagramPacket[] left = new DatagramPacket[grid.length];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                left[4 * (3 - j) + i] = grid[4 * i + j];
            }
        }
        return left;
    }

    //Send in packets - Packet structure needs to have built in redundant stream
    //Store in 4x4 grid
    //Interleave packets
    static byte[] lowerBitRate(byte[] audio, int bits){
        int shift = 8 - bits;
        for(int i = 0; i < audio.length; i++){
            audio[i] = (byte) (audio[i] >> shift);
        }
        return audio;
    }
    public static void main(String[] args) {

        int[] test = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        int[] test2 = new int[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                test2[4 * (3 - j) + i] = test[4 * i + j];
            }
        }

        for (int i = 0; i < test2.length; i++) {
            System.out.println(test2[i]);
        }
    }
}
