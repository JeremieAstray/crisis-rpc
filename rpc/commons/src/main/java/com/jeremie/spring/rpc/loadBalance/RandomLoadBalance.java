package com.jeremie.spring.rpc.loadBalance;

import com.netflix.appinfo.InstanceInfo;

import java.util.List;
import java.util.Random;

/**
 * @author guanhong 15/12/24 下午5:18.
 */
public class RandomLoadBalance implements LoadBalance {


    private static Random random = new Random();

    @Override
    public InstanceInfo select(List<InstanceInfo> instanceInfos) {
        int length = instanceInfos.size();
        return instanceInfos.get(random.nextInt(length));
    }
}
