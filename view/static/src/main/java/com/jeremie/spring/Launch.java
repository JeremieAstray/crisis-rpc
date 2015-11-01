package com.jeremie.spring;

import com.jeremie.spring.rpc.RpcInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author guanhong 15/9/10 下午5:04.
 */
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
@SpringBootApplication
public class Launch  extends WebMvcConfigurerAdapter {

    private static String[] packagesList = {"com.jeremie.spring.*.jpaService"};

    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder();
        RpcInitializer rpcInitializer = new RpcInitializer();
        rpcInitializer.setPackagesList(Arrays.asList(packagesList));
        springApplicationBuilder
                .sources(Launch.class)
                .initializers(rpcInitializer::rpcInit)
                .run(args);
    }

}
