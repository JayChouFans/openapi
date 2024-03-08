package io.github.jaychoufans.server;

import io.github.jaychoufans.core.AppDetails;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAppDetailsService implements AppDetailsService {

	private final Map<String, AppDetails> apps = new ConcurrentHashMap<>();

	@Override
	public void saveApp(AppDetails appDetails) {
		apps.put(appDetails.getAppId(), appDetails);
	}

	@Override
	public AppDetails loadByAppId(String appId) {
		return apps.get(appId);
	}

}
