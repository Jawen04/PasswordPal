package restserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AESKeyProvider {

    private final SecretKey secretKey;

    public AESKeyProvider(@Value("${encryption.aes.key}") String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public SecretKey getKey() {
        return secretKey;
    }
}

