package io.github.jaychoufans.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.jaychoufans.core.SignUtils;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.function.Supplier;

public abstract class AbstractOpenApiInterceptor {

	@Setter
	private String appId = "appId";

	@Setter
	private String appSecret;

	@Setter
	private String priKey;

	protected boolean isSigned(Supplier<String> appSignSupplier) {
		String appSign = appSignSupplier.get();
		return StringUtils.hasText(appSign);
	}

	protected void sign(String bodyStr, SignHeader signHeader) {

		String sign = null;
		try {
			sign = SignUtils.sign(bodyStr);
		}
		catch (NoSuchAlgorithmException | JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		String nonce = new Random().nextInt(1000) + ""; // 唯一随机数，可以填入流水号
		String timestamp = System.currentTimeMillis() + "";

		// 生成摘要
		String appSign = null;
		try {
			appSign = SignUtils.appSign(appId, appSecret, nonce, sign, timestamp, priKey);
		}
		catch (NoSuchAlgorithmException | SignatureException | InvalidKeySpecException | InvalidKeyException
				| JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		signHeader.signHeader(appId, nonce, sign, timestamp, appSign);
	}

	@FunctionalInterface
	public interface SignHeader {

		void signHeader(String appId, String nonce, String sign, String timestamp, String appSign);

	}

}
