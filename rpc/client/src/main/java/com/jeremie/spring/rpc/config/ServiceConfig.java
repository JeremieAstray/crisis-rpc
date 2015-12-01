package com.jeremie.spring.rpc.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhong 15/12/1 下午2:30.
 */
public class ServiceConfig implements Serializable {

    private String name;
    private String method;
    private List<String> packages = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }
}
