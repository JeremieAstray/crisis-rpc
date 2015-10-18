package com.jeremie.spring.rpc.dto;

import java.io.Serializable;

/**
 * @author guanhong 15/10/18 下午1:33.
 */
public class RPCDto implements Serializable{
    private String destClazz;
    private String method;
    private Class[] paramsType;
    private Object[] params;

    public String getDestClazz() {
        return destClazz;
    }

    public void setDestClazz(String destClazz) {
        this.destClazz = destClazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Class[] getParamsType() {
        return paramsType;
    }

    public void setParamsType(Class[] paramsType) {
        this.paramsType = paramsType;
    }
}
