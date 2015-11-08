package com.jeremie.spring.ds;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import org.apache.log4j.Logger;

/**
 * @author guanhong 15/11/7 下午2:15.
 */
public class EurekaServer {
    private static final DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory
            .getInstance();


    private static final Logger logger = Logger.getLogger(EurekaServer.class);


    public void registerWithEureka() {
        // Register with Eureka
        DiscoveryManager.getInstance().initComponent(
                new MyDataCenterInstanceConfig(),
                new DefaultEurekaClientConfig());
        ApplicationInfoManager.getInstance().setInstanceStatus(
                InstanceInfo.InstanceStatus.UP);
        String vipAddress = configInstance.getStringProperty(
                "eureka.vipAddress", "sampleservice.mydomain.net").get();
        InstanceInfo nextServerInfo = null;
        while (nextServerInfo == null) {
            try {
                nextServerInfo = DiscoveryManager.getInstance()
                        .getDiscoveryClient()
                        .getNextServerFromEureka(vipAddress, false);
            } catch (Throwable e) {
                logger.info("Waiting for service to register with eureka..");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    logger.error(e1.getMessage(), e1);
                }
            }
        }
        logger.info("Service started and ready to process requests..");
    }



    public void unRegisterWithEureka() {
        // Un register from eureka.
        DiscoveryManager.getInstance().shutdownComponent();
    }

    public static void main(String[] args) {
        EurekaServer eurekaServer = null;
        try {
            eurekaServer = new EurekaServer();
            eurekaServer.registerWithEureka();
        } finally {
            if (eurekaServer != null)
                eurekaServer.unRegisterWithEureka();
        }
    }

}