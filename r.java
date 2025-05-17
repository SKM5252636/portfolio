import java.math.BigInteger;
import java.util.Random;

public class CustomRSA {
    public static void main(String[] args) {
        // Generate two large primes
        BigInteger p = BigInteger.probablePrime(512, new Random());
        BigInteger q = BigInteger.probablePrime(512, new Random());
        BigInteger n = p.multiply(q);                // n = p * q
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // Choose e
        BigInteger e = BigInteger.valueOf(65537);    // Common choice
        while (!phi.gcd(e).equals(BigInteger.ONE)) {
            e = e.add(BigInteger.TWO);
        }

        // Compute d
        BigInteger d = e.modInverse(phi);

        // Display keys
        System.out.println("Public Key: (e = " + e + ", n = " + n + ")");
        System.out.println("Private Key: (d = " + d + ", n = " + n + ")");

        // Encrypt message
        String message = "42";
        BigInteger plaintext = new BigInteger(message.getBytes());
        BigInteger ciphertext = plaintext.modPow(e, n);
        System.out.println("Encrypted: " + ciphertext);

        // Decrypt message
        BigInteger decrypted = ciphertext.modPow(d, n);
        System.out.println("Decrypted: " + new String(decrypted.toByteArray()));
    }
}
