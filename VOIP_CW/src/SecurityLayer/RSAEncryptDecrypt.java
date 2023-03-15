package SecurityLayer;

import java.math.BigInteger;

public class RSAEncryptDecrypt {

    // This function encrypts a message given a public key and modulus.
    public static BigInteger encrypt(BigInteger message, BigInteger publicKey, BigInteger modulus) {
        // modPow performs a modular exponentiation of the message with the public key and modulus.
        return message.modPow(publicKey, modulus);
    }

    // This function decrypts a ciphertext given a private key and modulus.
    public static BigInteger decrypt(BigInteger ciphertext, BigInteger privateKey, BigInteger modulus) {
        // modPow performs a modular exponentiation of the ciphertext with the private key and modulus.
        return ciphertext.modPow(privateKey, modulus);
    }

    // This main function demonstrates how to use the encrypt and decrypt functions to encrypt and decrypt a message.
    public static void main(String[] args) {
        // Generate a pair of RSA keys.
        RSAKeyGenerator keyGen = new RSAKeyGenerator(2048);

        // Define the plaintext message as a string.
        String plaintext = "Hello world!";

        // Convert the plaintext message to a BigInteger.
        BigInteger message = new BigInteger(plaintext.getBytes());

        // Encrypt the plaintext using the generated public key.
        BigInteger ciphertext = encrypt(message, keyGen.getPublicKey(), keyGen.getModulus());

        // Print the ciphertext.
        System.out.println("Ciphertext: " + ciphertext);

        // Decrypt the ciphertext using the generated private key.
        BigInteger decryptedText = decrypt(ciphertext, keyGen.getPrivateKey(), keyGen.getModulus());

        // Convert the decrypted text from a BigInteger to a string and print it.
        String decryptedString = new String(decryptedText.toByteArray());
        System.out.println("Decrypted Text: " + decryptedString);
    }
}
