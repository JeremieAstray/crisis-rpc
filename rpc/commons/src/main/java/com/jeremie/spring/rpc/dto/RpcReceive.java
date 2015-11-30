package com.jeremie.spring.rpc.dto;

import java.io.Serializable;

/**
 * @author guanhong 15/10/18 下午2:45.
 */
public class RpcReceive implements Serializable {

    private String clientId;

    public enum Status{
        SUCCESS,ERR0R
    }
    private Status status;

    private Object returnPara;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Object getReturnPara() {
        return returnPara;
    }

    public void setReturnPara(Object returnPara) {
        this.returnPara = returnPara;
    }
}
