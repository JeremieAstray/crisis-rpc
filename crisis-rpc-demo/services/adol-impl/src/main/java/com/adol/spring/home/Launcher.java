package com.adol.spring.home;

import com.jeremie.spring.rpc.remote.http.Launch;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author guanhong 15/11/30 下午2:31.
 */
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
//@SpringBootApplication
//@EnableEurekaClient
public class Launcher extends Launch {

    public static void main(String[] args) {
        //SpringApplication.run(Launcher.class, args);
    }
}
