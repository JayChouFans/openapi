package io.github.jaychoufans.openapi.server;

import java.security.*;
import java.util.Base64;
import java.util.function.BiConsumer;

public class KeyGenerator {

    public void generate(BiConsumer<String, String> keyConsumer) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        keyConsumer.accept(
                new String(Base64.getEncoder().encode(publicKey.getEncoded())),
                new String(Base64.getEncoder().encode(privateKey.getEncoded())));
    }

}
