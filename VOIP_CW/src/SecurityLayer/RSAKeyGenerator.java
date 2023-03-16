package SecurityLayer;// Imports Required Class
import java.math.BigInteger;
import java.security.SecureRandom;

// Defines SecurityLayer.RSAKeyGenerator class + methods
public class RSAKeyGenerator {

    // one is a BigInteger used to simplify calculations
    private final static BigInteger one = new BigInteger("1");
    // random is a Secure Random Number used to generate prime numbers.
    private final static SecureRandom random = new SecureRandom();

    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;


    public RSAKeyGenerator(int bitLength) {
        // Generates two large prime numbers p + q. With the length of bitLength / 2
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
        // Calculates the Euler totient function phi of p + q
        BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));

        // Calculates the modulus by multiplying p and q
        modulus = p.multiply(q);
        // Sets the public key exponent to 65537 which is a common value for RSA public keys.
        publicKey = new BigInteger("65537"); // Common public exponent
        // Calculates the private key by computing the modular multiplicative inverse of the public key modulo phi
        privateKey = publicKey.modInverse(phi);
    }
    // Defines the getters for Module, Private and Public SecurityLayer.Keys
    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public BigInteger getModulus() {
        return modulus;
    }

    // This main function generates an instance of SecurityLayer.RSAKeyGenerator and displays the public, private and modulus values
    public static void main(String[] args) {
        RSAKeyGenerator keyGen = new RSAKeyGenerator(2048);
        System.out.println("Private Key: " + keyGen.getPrivateKey());
        System.out.println("Public Key: " + keyGen.getPublicKey());
        System.out.println("Modulus: " + keyGen.getModulus());
    }

}