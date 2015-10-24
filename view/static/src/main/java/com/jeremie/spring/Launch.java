package com.jeremie.spring;

import com.jeremie.spring.rpc.RpcInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author guanhong 15/9/10 下午5:04.
 */
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
@SpringBootApplication
public class Launch  extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder();
        springApplicationBuilder
                .initializers(RpcInitializer::rpcInit)
                .sources(Launch.class)
                .run(args);
    }

}
