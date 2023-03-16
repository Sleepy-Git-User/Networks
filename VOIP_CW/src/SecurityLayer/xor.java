package SecurityLayer;

import java.security.SecureRandom;

public class xor {

    public static byte[] generateKey(int length) {
        byte[] key = new byte[length];
        new SecureRandom().nextBytes(key);
        return key;
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte) (plaintext[i] ^ key[i % key.length]);
        }
        return ciphertext;
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key) {
        return encrypt(ciphertext, key);
    }
}
