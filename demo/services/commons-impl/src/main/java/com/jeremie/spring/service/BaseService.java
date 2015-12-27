package com.jeremie.spring.service;

import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * @author guanhong 15/9/13 上午11:22.
 */
public class BaseService implements Serializable {

    protected Logger log = Logger.getLogger(this.getClass());

    protected String getKeyWords(String keyWords) {
        return "%" + keyWords + "%";
    }

    protected int parseInt(Object obj) {
        return ((Long) obj).intValue();
    }

}
