import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

public class RSADigitalSignature {

    // Simple hash function using SHA-256
    public static BigInteger hashMessage(String message) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(1, hash);
    }

    public static void main(String[] args) throws Exception {
        // Generate two large primes
        BigInteger p = BigInteger.probablePrime(512, new Random());
        BigInteger q = BigInteger.probablePrime(512, new Random());
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // Public exponent
        BigInteger e = BigInteger.valueOf(65537);
        while (!phi.gcd(e).equals(BigInteger.ONE)) {
            e = e.add(BigInteger.TWO);
        }

        // Private exponent
        BigInteger d = e.modInverse(phi);

        // Message
        String message = "Trust but verify!";
        BigInteger messageHash = hashMessage(message);

        // Sign: sig = hash^d mod n
        BigInteger signature = messageHash.modPow(d, n);
        System.out.println("Digital Signature: " + signature);

        // Verify: sig^e mod n == hash
        BigInteger verifiedHash = signature.modPow(e, n);
        boolean isValid = verifiedHash.equals(messageHash);

        System.out.println("Original Hash: " + messageHash);
        System.out.println("Verified Hash: " + verifiedHash);
        System.out.println("Signature Valid: " + isValid);
    }
}
