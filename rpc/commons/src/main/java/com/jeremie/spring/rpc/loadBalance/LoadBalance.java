package com.jeremie.spring.rpc.loadBalance;

import com.netflix.appinfo.InstanceInfo;

import java.util.List;

/**
 * Deprecated
 * using eureka server loadBalance
 * use Netflix Ribbon instead of this
 * @author guanhong 15/12/24 下午5:01.
 */
@Deprecated
public interface LoadBalance {
    static LoadBalance getLoadBalance(String name) {
        switch (name) {
            case "random":
                return new RandomLoadBalance();
            default:
                return new RandomLoadBalance();
        }
    }

    InstanceInfo select(List<InstanceInfo> instanceInfos);
}
