package com.jeremie.spring.rpc;

import java.io.Serializable;

/**
 * @author guanhong 15/10/18 下午2:45.
 */
public class RpcResult implements Serializable {

    private String clientId;
    private Status status;
    private Object returnPara;
    private Exception exception;

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

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public enum Status {
        SUCCESS, ERR0R
    }
}
