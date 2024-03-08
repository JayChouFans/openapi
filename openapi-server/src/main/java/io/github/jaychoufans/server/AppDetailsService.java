package io.github.jaychoufans.server;

import io.github.jaychoufans.core.AppDetails;

public interface AppDetailsService {

	void saveApp(AppDetails appDetails);

	AppDetails loadByAppId(String appId);

}
