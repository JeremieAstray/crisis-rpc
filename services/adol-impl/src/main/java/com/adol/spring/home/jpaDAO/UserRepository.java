package com.adol.spring.home.jpaDAO;


/**
 * @author guanhong 15/8/18 下午4:06.
 */
@Repository
@Table(name = "user")
public interface UserRepository extends BaseDAO<User> {

}
