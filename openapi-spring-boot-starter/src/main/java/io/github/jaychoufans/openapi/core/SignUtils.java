package io.github.jaychoufans.openapi.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class SignUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String sign(Object object) throws NoSuchAlgorithmException, JsonProcessingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] digested = messageDigest.digest(objectMapper.writeValueAsString(object).getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(digested);
    }

    public static String appSign(String appId,
                                 String appSecret,
                                 String nonce,
                                 String sign,
                                 String timestamp,
                                 String priKey) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        Map<String, String> data = new TreeMap<>(String::compareTo);
        data.put("appId", appId);
        data.put("nonce", nonce);
        data.put("sign", sign);
        data.put("timestamp", timestamp);
        data.put("appSecret", appSecret);

        // 私钥签名
        byte[] key = Base64.getDecoder().decode(priKey); // 这里需要传入生成的私钥
        byte[] bytes = objectMapper.writeValueAsString(data).getBytes();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(bytes);
        return new String(Base64.getEncoder().encode(signature.sign()));
    }

    public static boolean verifyAppSign(String appId,
                                        String appSecret,
                                        String nonce,
                                        String sign,
                                        String timestamp,
                                        String appSign,
                                        String pubKey) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException, JsonProcessingException, SignatureException {
        Map<String, String> data = new TreeMap<>(String::compareTo);
        data.put("appId", appId);
        data.put("nonce", nonce);
        data.put("sign", sign);
        data.put("timestamp", timestamp);
        data.put("appSecret", appSecret);

        // 验证签名
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKey));
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(objectMapper.writeValueAsString(data).getBytes());
        return signature.verify(Base64.getDecoder().decode(appSign));
    }

}
