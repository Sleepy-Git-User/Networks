package SecurityLayer;

import java.math.BigInteger;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Keys {
    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;

    public Keys(BigInteger privateKey, BigInteger publicKey, BigInteger modulus) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.modulus = modulus;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public BigInteger getModulus() {
        return modulus;
    }
    public byte[] toByteArray(BigInteger input) {
        return String.valueOf(input).getBytes(StandardCharsets.UTF_8);
    }

    public String fromByteArray(byte[] byteArray) {
        return new String(byteArray, StandardCharsets.UTF_8);
    }
}