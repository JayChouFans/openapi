package io.github.jaychoufans.client.okhttp3;

import io.github.jaychoufans.client.AbstractOpenApiInterceptor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

import java.io.IOException;

public class OpenApiInterceptor extends AbstractOpenApiInterceptor implements Interceptor {

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();

		if (isSigned(() -> request.headers().get("appSign"))) {
			return chain.proceed(request);
		}

		Buffer buffer = new Buffer();
		request.body().writeTo(buffer);
		String bodyStr = buffer.readUtf8();

		Request.Builder builder = request.newBuilder();

		sign(bodyStr, (appId, nonce, sign, timestamp, appSign) -> {
			builder.addHeader("appId", appId);
			builder.addHeader("nonce", nonce);
			builder.addHeader("sign", sign);
			builder.addHeader("timestamp", timestamp);
			builder.addHeader("appSign", appSign);
		});

		return chain.proceed(builder.build());
	}

}
