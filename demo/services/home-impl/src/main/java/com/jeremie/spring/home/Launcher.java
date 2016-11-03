package com.jeremie.spring.home;

//import com.jeremie.spring.rpc.server.common.RpcConfiguration;
import com.jeremie.spring.rpc.server.mina.Launch;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author guanhong 15/11/30 下午2:31.
 */
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
//@SpringBootApplication
//@EnableEurekaClient
//@EnableConfigurationProperties(RpcConfiguration.class)
public class Launcher extends Launch {

    public static void main(String[] args) {
        //SpringApplication.run(Launcher.class, args);
    }
}
