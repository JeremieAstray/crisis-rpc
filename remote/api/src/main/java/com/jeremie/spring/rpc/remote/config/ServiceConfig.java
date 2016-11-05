package com.jeremie.spring.rpc.remote.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guanhong on 2015/12/2 15:32
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