package com.jeremie.spring.commons;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author guanhong 15/9/15 上午11:23.
 */
@SpringApplicationConfiguration(classes = BaseTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest
@EnableJpaRepositories(basePackages = "${spring.ioc.jpaRepositories.basePackages}",repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@ComponentScan(basePackages = "${spring.ioc.componentScan.basePackages}")
@EntityScan(basePackages = "${spring.ioc.entityScan.basePackages}")
@SpringBootApplication
public abstract class BaseTest {

}
