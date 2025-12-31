package model.security;

import org.junit.jupiter.api.Test;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {
    private SecretKey randomAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128, SecureRandom.getInstanceStrong());
        return keyGenerator.generateKey();
    }

    @Test
    void encryptDecrypt_roundTrip_testoNonVuoto() throws Exception {
        SecretKey key = randomAESKey();
        String original = "thisIsATestString123!@#";

        String encrypted = CryptoUtils.encrypt(key, original);
        String decrypted = CryptoUtils.decrypt(key, encrypted);

        assertEquals(original, decrypted);
    }

    @Test
    void encryptDecrypt_roundTrip_testoVuoto() throws Exception {
        SecretKey key = randomAESKey();

        String encrypted = CryptoUtils.encrypt(key, "");
        String decrypted = CryptoUtils.decrypt(key, encrypted);

        assertEquals("", decrypted);
    }

    @Test
    void encryptDecrypt_roundTrip_unicode() throws Exception {
        SecretKey key = randomAESKey();
        String text = "thisIsAÜñîçødëТест字符串🚀";

        String encrypted = CryptoUtils.encrypt(key, text);
        String decrypted = CryptoUtils.decrypt(key, encrypted);

        assertEquals(text, decrypted);
    }

    @Test
    void encrypt_dueEncryptDiversi_generanoCipherDiversi() throws Exception {
        SecretKey key = randomAESKey();

        String c1 = CryptoUtils.encrypt(key, "mango");
        String c2 = CryptoUtils.encrypt(key, "mango");

        assertNotEquals(c1, c2);
    }

    @Test
    void decrypt_conChiaveErrata_lanciaException() throws Exception {
        SecretKey keyCorrect = randomAESKey();
        SecretKey keyWrong = randomAESKey();

        String encrypted = CryptoUtils.encrypt(keyCorrect, "fratmango");

        assertThrows(Exception.class, () ->
                CryptoUtils.decrypt(keyWrong, encrypted)
        );
    }

    @Test
    void decrypt_ciphertextCorrotto_lanciaException() throws Exception {
        SecretKey key = randomAESKey();

        String corrupted = "thisIsNotBase64!!";

        assertThrows(Exception.class, () ->
                CryptoUtils.decrypt(key, corrupted)
        );
    }
}
