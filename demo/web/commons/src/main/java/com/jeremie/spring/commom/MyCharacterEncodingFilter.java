package com.jeremie.spring.commom;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.annotation.WebFilter;

/**
 * @author guanhong 15/10/4 下午4:39.
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(filterName = "characterEncodingFilter", urlPatterns = "/*")
public class MyCharacterEncodingFilter extends CharacterEncodingFilter {
    public MyCharacterEncodingFilter() {
        super();
        setEncoding("UTF-8");
    }
}
