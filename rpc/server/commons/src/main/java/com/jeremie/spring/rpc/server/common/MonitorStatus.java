package com.jeremie.spring.rpc.server.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guanhong 15/12/4 下午2:51.
 */
public class MonitorStatus {

    public static int ConnectCount = 0;
    public static Map<String, MethodStatus> methodStatusMap = new ConcurrentHashMap<>();

}
