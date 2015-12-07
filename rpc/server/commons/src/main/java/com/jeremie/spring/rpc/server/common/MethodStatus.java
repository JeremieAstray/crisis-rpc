package com.jeremie.spring.rpc.server.common;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author guanhong 15/12/4 下午3:23.
 */
public class MethodStatus implements Serializable {

    /**
     * 方法名
     *
     */
    private String method;

    /**
     * 错误统计
     *
     */
    private AtomicLong errorCount = new AtomicLong(0);

    /**
     * 调用统计
     *
     */
    private AtomicLong invokeCount = new AtomicLong(0);

    /**
     * 异常队列
     */
    private Queue<ExceptionStatus> exceptionQueue = new ConcurrentLinkedQueue<>();

    /**
     * 调用队列
     *
     */
    private Queue<InvokeMethodStatus> invokeMethodStatuses = new ConcurrentLinkedQueue<>();


    public MethodStatus(String method) {
        this.method = method;
    }

    public class InvokeMethodStatus {

        public InvokeMethodStatus(long invokeTime, long invokeElapsed) {
            this.invokeTime = invokeTime;
            this.invokeElapsed = invokeElapsed;
        }

        /**
         * 调用时间
         */
        private long invokeTime;

        /**
         * 调用时长
         */
        private long invokeElapsed;

        public long getInvokeTime() {
            return invokeTime;
        }

        public long getInvokeElapsed() {
            return invokeElapsed;
        }
    }

    public class ExceptionStatus{
        private long appearTime;
        private Exception exception;

        public ExceptionStatus(long appearTime, Exception exception) {
            this.appearTime = appearTime;
            this.exception = exception;
        }

        public long getAppearTime() {
            return appearTime;
        }

        public Exception getException() {
            return exception;
        }
    }

    public String getMethod() {
        return method;
    }

    public long getErrorCount() {
        return errorCount.get();
    }

    public long increaseErrorCount() {
        return errorCount.incrementAndGet();
    }

    public long getInvokeCount() {
        return invokeCount.get();
    }

    public long increaseInvokeCount() {
        return invokeCount.incrementAndGet();
    }

    public List<ExceptionStatus> getExceptionList() {
        return exceptionQueue.stream().collect(Collectors.toList());
    }

    public Queue<ExceptionStatus> getExceptionQueue() {
        return exceptionQueue;
    }

    public void addException(long appearTime, Exception exception) {
        exceptionQueue.add(new ExceptionStatus(appearTime,exception));
    }

    @JSONField(serialize = false)
    public Queue<InvokeMethodStatus> getInvokeMethodStatuses() {
        return invokeMethodStatuses;
    }
    public List<InvokeMethodStatus> getInvokeMethodStatusList() {
        return invokeMethodStatuses.stream().collect(Collectors.toList());
    }

    public void addInvokeMethodStatuses(long invokeTime,long invokeElapsed) {
        invokeMethodStatuses.add(new InvokeMethodStatus(invokeTime,invokeElapsed));
    }

}
