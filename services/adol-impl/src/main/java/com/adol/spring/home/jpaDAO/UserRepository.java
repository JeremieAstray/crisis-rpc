package com.adol.spring.home.jpaDAO;


import com.adol.spring.home.entity.User;
import com.jeremie.spring.jpaDAO.BaseDAO;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;

/**
 * @author guanhong 15/8/18 下午4:06.
 */
@Repository
@Table(name = "user")
public interface UserRepository extends BaseDAO<User> {

}
