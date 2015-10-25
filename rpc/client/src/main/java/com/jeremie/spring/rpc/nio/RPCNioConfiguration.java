package com.jeremie.spring.rpc.nio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanhong 15/10/24 下午9:32.
 */
@Configuration
public class RPCNioConfiguration {
    @Bean(destroyMethod = "destroy")
    public RPCNioBean rpcNioBean(){
        return new RPCNioBean();
    }
}
