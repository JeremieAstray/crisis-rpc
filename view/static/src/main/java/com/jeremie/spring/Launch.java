package com.jeremie.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author guanhong 15/9/10 下午5:04.
 */
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
@SpringBootApplication
public class Launch  extends WebMvcConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(Launch.class, args);
    }
}
