package com.jeremie.spring.entity;

import org.springframework.context.annotation.Lazy;

import java.io.Serializable;

/**
 * @author guanhong 15/9/12 下午3:35.
 */
@Lazy(false)
public class BaseEntity implements Serializable {

    private Long id;

    private Boolean valid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
