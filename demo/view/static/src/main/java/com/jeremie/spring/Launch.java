package com.jeremie.spring;

import com.jeremie.spring.rpc.config.RpcInitializerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author guanhong 15/9/10 下午5:04.
 */
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "rpc-consumer")
public class Launch extends WebMvcConfigurerAdapter {

    @Autowired
    private RpcInitializerConfiguration rpcInitializerConfiguration;

    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder();
        springApplicationBuilder
                .sources(Launch.class)
                .run(args);
    }

}
