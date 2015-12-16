package com.jeremie.spring.monitor.server;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author guanhong 15/11/9 下午1:55.
 */
@ComponentScan(basePackages = {"com.jeremie.spring.monitor"})
@EnableAutoConfiguration
@RestController
public class Monitor extends WebMvcConfigurerAdapter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Monitor.class).web(true).run(args);
    }
}
