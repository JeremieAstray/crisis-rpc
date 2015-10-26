package com.jeremie.spring.rpc.http;

import com.jeremie.spring.commons.BaseRepositoryFactoryBean;
import com.jeremie.spring.rpc.dto.RPCDto;
import com.jeremie.spring.rpc.dto.RPCReceive;
import com.jeremie.spring.rpc.util.SerializeTool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.lang.reflect.Method;

/**
 * @author guanhong 15/9/10 下午5:04.
 */
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
@EnableJpaRepositories(basePackages = "${spring.ioc.jpaRepositories.basePackages}", repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EntityScan(basePackages = "${spring.ioc.entityScan.basePackages}")
@SpringBootApplication
@Controller
public class Launch extends WebMvcConfigurerAdapter {
    protected Logger logger = Logger.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(Launch.class, args);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/", produces = "text/html; charset=utf-8")
    @ResponseBody
    public String rpcServer(String rpcDtoStr, Model model) throws Exception {
        Object object = SerializeTool.stringToObject(rpcDtoStr);
        RPCDto rpcDto = null;
        if (object instanceof RPCDto)
            rpcDto = (RPCDto) object;
        else
            throw new ClassCastException("rpcDtoStr not cast to rpcDto");
        Class clazz = Class.forName(rpcDto.getDestClazz());
        Object o1 = applicationContext.getBean(clazz);
        Method method = clazz.getMethod(rpcDto.getMethod(), rpcDto.getParamsType());
        Object result = method.invoke(o1, rpcDto.getParams());
        RPCReceive rpcReceive = new RPCReceive();
        rpcReceive.setReturnPara(result);
        rpcReceive.setStatus(RPCReceive.Status.SUCCESS);
        return SerializeTool.objectToString(rpcReceive);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e) {
        logger.error(e.getMessage(),e);
        RPCReceive rpcReceive = new RPCReceive();
        rpcReceive.setReturnPara(null);
        rpcReceive.setStatus(RPCReceive.Status.ERR0R);
        return SerializeTool.objectToString(rpcReceive);
    }


}
