package com.jeremie.spring.web;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.time.Instant;
import java.util.Date;

/**
 * 控制器基类
 *
 * @author guanhong 15/9/10 下午6:40.
 */
public abstract class BaseController {

    protected Logger log = Logger.getLogger(this.getClass());
    public final static String ERROR = "error";
    public final static String SUCCESS = "success";

    /**
     * 添加Model消息
     *
     * @param message
     */
    protected void addMessage(Model model, String message) {
        model.addAttribute("message", message);
    }

    /**
     * 添加Model消息
     * @param type 消息类型
     * @param message
     */
    protected void addMessage(Model model,String type, String message) {
        model.addAttribute("message", message);
        model.addAttribute("type", type);
    }

    /**
     * 添加Flash消息
     *
     * @param message
     */
    protected void addMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("message", message);
    }

    /**
     * 添加Flash消息
     * @param type 消息类型
     * @param message
     */
    protected void addMessage(RedirectAttributes redirectAttributes, String type, String message) {
        redirectAttributes.addFlashAttribute("message", message);
        redirectAttributes.addFlashAttribute("type", type);
    }

    /**
     * 初始化数据绑定
     * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
     * 2. 将字段中Date类型转换为String类型
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null ? null : text.trim());
            }

            @Override
            public String getAsText() {
                Object value = getValue();
                return value != null ? value.toString() : "";
            }
        });
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(Date.from(Instant.parse(text)));
            }
        });
    }


    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        //model.addAttribute("exMsg", ex.getMessage());
        return "error/exception";
    }


}
