package io.github.jaychoufans.client.apache;

import io.github.jaychoufans.client.AbstractOpenApiInterceptor;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class OpenApiHttpRequestInterceptor extends AbstractOpenApiInterceptor implements HttpRequestInterceptor {

	@Override
	public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {

		String appSign1 = Optional.ofNullable(httpRequest.getFirstHeader("appSign")).map(NameValuePair::getValue)
				.orElse(null);

		if (isSigned(() -> appSign1)) {
			return;
		}

		if (httpRequest instanceof HttpEntityEnclosingRequest) {
			HttpEntityEnclosingRequest httpPost = (HttpEntityEnclosingRequest) httpRequest;

			String bodyStr = IOUtils.toString(httpPost.getEntity().getContent(), StandardCharsets.UTF_8);

			sign(bodyStr, (appId, nonce, sign, timestamp, appSign) -> {
				httpRequest.addHeader("appId", appId);
				httpRequest.addHeader("nonce", nonce);
				httpRequest.addHeader("sign", sign);
				httpRequest.addHeader("timestamp", timestamp);
				httpRequest.addHeader("appSign", appSign);
			});
		}
	}

}
