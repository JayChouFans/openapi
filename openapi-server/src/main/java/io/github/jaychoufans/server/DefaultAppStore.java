package io.github.jaychoufans.server;

import io.github.jaychoufans.core.AppInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultAppStore {

    static Map<String, AppInfo> appInfoMap = new ConcurrentHashMap<>();

    public static void addAppInfo(AppInfo appInfo) {
        appInfoMap.put(appInfo.getAppId(), appInfo);
    }

    public static AppInfo getAppInfo(String appId) {
        return appInfoMap.get(appId);
    }

}
