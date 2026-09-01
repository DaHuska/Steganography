package bg.tusofia.steganography;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESImpl {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String SALT = "asd123sdggd";
    private static final GCMParameterSpec initializationVector = generateIv();


    // Default constructor
    public AESImpl() {}

    public static String encryptMessage(String message, String password) throws NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        SecretKey secretKeySender = getKeyFromPassword(password, SALT);
        String cipherText = encrypt(ALGORITHM, message, secretKeySender, initializationVector);

        return cipherText;
    }

    public static String decryptMessage(String password, String cipher) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        SecretKey secretKeyReceiver = getKeyFromPassword(password, SALT);
        String message = decrypt(ALGORITHM, cipher, secretKeyReceiver, initializationVector);

        return message;
    }

    private static String encrypt(String algorithm, String input, SecretKey key,
                                 GCMParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] cipherText = cipher.doFinal(input.getBytes());

        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    private static String decrypt(String algorithm, String cipherText, SecretKey key,
                                 GCMParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));

        return new String(plainText);
    }

    private static GCMParameterSpec generateIv() {
        byte[] initializationVector = new byte[12];
        new SecureRandom().nextBytes(initializationVector);

        return new GCMParameterSpec(128, initializationVector);
    }

    private static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);

        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");

        return secret;
    }
}
