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
import java.util.Stack;

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

    private Stack<TransactionStatus> transactionStatuses = new Stack<>();


    @Before("execution(public * com.jeremie.spring.*.jpaService.*.*(..))")
    public void txAdviceBegin(JoinPoint point) {
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
    }

    @AfterReturning("execution(public * com.jeremie.spring.*.jpaService.*.*(..))")
    public void txAdviceCommit(JoinPoint point) {
        //if (getMethodsPrefix().stream().anyMatch(prefix -> point.getSignature().getName().startsWith(prefix)))
        // transactionManager.commit(transactionStatus);
        //else
        if (!transactionStatuses.isEmpty()) {
            TransactionStatus transactionStatus = transactionStatuses.pop();
            if (transactionStatus != null)
                if (!transactionStatus.isRollbackOnly())
                    transactionManager.commit(transactionStatus);
                else
                    transactionManager.rollback(transactionStatus);
        }
    }

    @AfterThrowing(value = "execution(public * com.jeremie.spring.*.jpaService.*.*(..))", throwing = "e")
    public void txAdviceRollBack(JoinPoint point, Exception e) {
        //if (getMethodsPrefix().stream().anyMatch(prefix -> point.getSignature().getName().startsWith(prefix)))
        if (!transactionStatuses.isEmpty()) {
            TransactionStatus transactionStatus = transactionStatuses.pop();
            if (transactionStatus != null)
                transactionManager.rollback(transactionStatus);
            logger.error("roll back exception", e);
        }
    }
}
