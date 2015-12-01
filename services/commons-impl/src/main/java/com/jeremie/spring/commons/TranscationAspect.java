package com.jeremie.spring.commons;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guanhong 15/9/10 上午12:18.
 */

@Aspect
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.jpaTransactionManager.txAdvice")
public class TranscationAspect {

    private static Logger logger = Logger.getLogger(TranscationAspect.class);

    private List<String> methodsPrefix = new ArrayList<>();

    /**
     * important!!!!!!
     *
     * @return
     */
    public List<String> getMethodsPrefix() {
        return methodsPrefix;
    }

    @Autowired
    private JpaTransactionManager transactionManager;

    private Map<Thread,Stack<TransactionStatus>> threadStackMap = new ConcurrentHashMap<>();


    @Before("execution(public * com.jeremie.spring.*.jpaService.*.*(..))")
    public void txAdviceBegin(JoinPoint point) {
        Thread thread = Thread.currentThread();
        if(!threadStackMap.containsKey(thread));
        threadStackMap.put(thread,new Stack<>());
        Stack<TransactionStatus> transactionStatuses = threadStackMap.get(thread);
        if (getMethodsPrefix().stream().anyMatch(prefix -> point.getSignature().getName().startsWith(prefix))) {
            DefaultTransactionDefinition transactionDefinition =
                    new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
            transactionDefinition.setReadOnly(false);
            TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
            transactionStatuses.push(transactionStatus);
        } else {
            DefaultTransactionDefinition transactionDefinition =
                    new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
            transactionDefinition.setReadOnly(true);
            TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
            transactionStatuses.push(transactionStatus);
        }
        threadStackMap.put(thread,transactionStatuses);
    }

    @AfterReturning("execution(public * com.jeremie.spring.*.jpaService.*.*(..))")
    public void txAdviceCommit(JoinPoint point) {
        Thread thread = Thread.currentThread();
        Stack<TransactionStatus> transactionStatuses = threadStackMap.get(thread);
        if (!transactionStatuses.isEmpty()) {
            TransactionStatus transactionStatus = transactionStatuses.pop();
            if (transactionStatus != null)
                if (!transactionStatus.isRollbackOnly())
                    transactionManager.commit(transactionStatus);
                else
                    transactionManager.rollback(transactionStatus);
        }
        threadStackMap.put(thread,transactionStatuses);
        if(transactionStatuses.isEmpty())
            threadStackMap.remove(thread);
    }

    @AfterThrowing(value = "execution(public * com.jeremie.spring.*.jpaService.*.*(..))", throwing = "e")
    public void txAdviceRollBack(JoinPoint point, Exception e) {
        Thread thread = Thread.currentThread();
        Stack<TransactionStatus> transactionStatuses = threadStackMap.get(thread);
        if (!transactionStatuses.isEmpty()) {
            TransactionStatus transactionStatus = transactionStatuses.pop();
            if (transactionStatus != null)
                transactionManager.rollback(transactionStatus);
            logger.error("roll back exception", e);
        }
        threadStackMap.put(thread,transactionStatuses);
        if(transactionStatuses.isEmpty())
            threadStackMap.remove(thread);
    }



    @Before("execution(public * com.adol.spring.*.jpaService.*.*(..))")
    public void txAdviceBeginAdol(JoinPoint point) {
        Thread thread = Thread.currentThread();
        if(!threadStackMap.containsKey(thread));
        threadStackMap.put(thread,new Stack<>());
        Stack<TransactionStatus> transactionStatuses = threadStackMap.get(thread);
        if (getMethodsPrefix().stream().anyMatch(prefix -> point.getSignature().getName().startsWith(prefix))) {
            DefaultTransactionDefinition transactionDefinition =
                    new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
            transactionDefinition.setReadOnly(false);
            TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
            transactionStatuses.push(transactionStatus);
        } else {
            DefaultTransactionDefinition transactionDefinition =
                    new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
            transactionDefinition.setReadOnly(true);
            TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
            transactionStatuses.push(transactionStatus);
        }
        threadStackMap.put(thread,transactionStatuses);
    }

    @AfterReturning("execution(public * com.adol.spring.*.jpaService.*.*(..))")
    public void txAdviceCommitAdol(JoinPoint point) {
        Thread thread = Thread.currentThread();
        Stack<TransactionStatus> transactionStatuses = threadStackMap.get(thread);
        if (!transactionStatuses.isEmpty()) {
            TransactionStatus transactionStatus = transactionStatuses.pop();
            if (transactionStatus != null)
                if (!transactionStatus.isRollbackOnly())
                    transactionManager.commit(transactionStatus);
                else
                    transactionManager.rollback(transactionStatus);
        }
        threadStackMap.put(thread,transactionStatuses);
        if(transactionStatuses.isEmpty())
            threadStackMap.remove(thread);
    }

    @AfterThrowing(value = "execution(public * com.adol.spring.*.jpaService.*.*(..))", throwing = "e")
    public void txAdviceRollBackAdol(JoinPoint point, Exception e) {
        Thread thread = Thread.currentThread();
        Stack<TransactionStatus> transactionStatuses = threadStackMap.get(thread);
        if (!transactionStatuses.isEmpty()) {
            TransactionStatus transactionStatus = transactionStatuses.pop();
            if (transactionStatus != null)
                transactionManager.rollback(transactionStatus);
            logger.error("roll back exception", e);
        }
        threadStackMap.put(thread,transactionStatuses);
        if(transactionStatuses.isEmpty())
            threadStackMap.remove(thread);
    }
}
