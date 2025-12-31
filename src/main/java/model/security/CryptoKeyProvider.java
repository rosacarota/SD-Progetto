package model.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoKeyProvider {

  private static SecretKey key;

  public static SecretKey getKey() {
    if (key == null) {
      String base64 = System.getenv("AES_KEY_BASE64");
      if (base64 == null) {
        throw new IllegalStateException("AES_KEY_BASE64 environment variable not set");
      }
      byte[] decoded = Base64.getDecoder().decode(base64);
      key = new SecretKeySpec(decoded, "AES");
    }
    return key;
  }
}

