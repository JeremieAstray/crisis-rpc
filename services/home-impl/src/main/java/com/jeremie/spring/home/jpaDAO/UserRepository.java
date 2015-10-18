package com.jeremie.spring.home.jpaDAO;

import com.jeremie.spring.home.entity.User;
import com.jeremie.spring.jpaDAO.BaseDAO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;

/**
 * @author guanhong 15/8/18 下午4:06.
 */
@Repository
@Table(name = "user")
public interface UserRepository extends BaseDAO<User> {

    @Modifying
    @Query("update User u set u.username= :username where u.id= :id")
    void update(@Param("username") String username, @Param("id") Long id);

    User findByUsername(String username);

    User findByIdAndValidTrue(Long id);

}
