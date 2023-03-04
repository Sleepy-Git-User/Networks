import java.nio.ByteBuffer;

public class sequenceLayer extends layer {

    byte[] add(int pos, byte[] audio){
        return super.add(pos, audio);
    }

    byte[] remove(byte[] audio){
        return super.remove(audio);
    }

    short getHeader(byte[] audio){
        return super.getHeader(audio);
    }

    byte[] rotateLeft(byte[] audio){
        byte[] left = new byte[audio.length];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                left[4 * (3 - j) + i] = audio[4 * i + j];
            }
        }
        return left;
    }

    byte[] rotateRight(byte[] audio){
        byte[] right = new byte[audio.length];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                right[4 * j + i] = audio[4 * i + j];
            }
        }
        return right;
    }



}
