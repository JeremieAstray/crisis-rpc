package com.jeremie.spring.home;

import com.jeremie.spring.rpc.server.mina.MinaRpcServerBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by jeremie on 2016/11/7.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        MinaRpcServerBean minaRpcServerBean = beanFactory.getBean(MinaRpcServerBean.class);
        minaRpcServerBean.init();
        try {
            while (true){
                Thread.sleep(500);
            }
        }finally {
            minaRpcServerBean.destroy();
        }
    }
}
