package io.github.jaychoufans.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.jaychoufans.core.SignUtils;
import io.github.jaychoufans.server.KeyGenerator;
import org.junit.jupiter.api.BeforeAll;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Test {

    static String appId = "appId";
    static String appSecret = "appSecret"; // 客户端和服务端各自保存，不做传输

    static Map<String, String> pubKeyMap = new HashMap<>(); // 客户端保存
    static Map<String, String> priKeyMap = new HashMap<>(); // 服务端保存

    @BeforeAll
    public static void init() throws NoSuchAlgorithmException {

        // 生成并存储公私钥
        KeyGenerator keyGenerator = new KeyGenerator();
        keyGenerator.generate((pubKey, priKey) -> {
            System.out.println("pubKey:" + pubKey);
            System.out.println("priKey:" + priKey);
            pubKeyMap.put(appId, pubKey);
            priKeyMap.put(appId, priKey);
        });

    }

    @org.junit.jupiter.api.Test
    public void test() throws NoSuchAlgorithmException, JsonProcessingException, InvalidKeySpecException, SignatureException, InvalidKeyException {

        // 客户端请求参数
        Map<String, String> body = new TreeMap<>(String::compareTo);
        body.put("p1", "p1");
        body.put("p2", "p2");

        String sign = SignUtils.sign(body);
        String nonce = new Random().nextInt(1000) + ""; // 唯一随机数，可以填入流水号
        String timestamp = System.currentTimeMillis() + "";

        // 生成摘要
        String appSign = SignUtils.appSign(appId, appSecret, nonce, sign, timestamp, priKeyMap.get(appId));

        // 请求头
        Map<String, String> header = new HashMap<>();
        header.put("appId", appId);
        header.put("nonce", nonce);
        header.put("sign", sign);
        header.put("timestamp", timestamp);
        header.put("appSign", appSign);

        // 服务端验证
        server(header, body);
    }

    private void server(Map<String, String> header, Map<String, String> body) throws NoSuchAlgorithmException, JsonProcessingException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        String sign = SignUtils.sign(body);
        if (!sign.equals(header.get("sign"))) {
            throw new RuntimeException("sign不正确");
        }

        String appId = header.get("appId");
        String appSecret = Test.appSecret;
        String nonce = header.get("nonce");
        String timestamp = header.get("timestamp");

        String appSign = header.get("appSign");
        if (!SignUtils.verifyAppSign(appId,appSecret,nonce,sign,timestamp,appSign,pubKeyMap.get(appId))){
            throw new RuntimeException("appSign不正确");
        }
    }

}
