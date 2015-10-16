package com.jeremie.spring.home.web;


import com.jeremie.spring.home.entity.User;
import com.jeremie.spring.home.jpaService.UserService;
import com.jeremie.spring.web.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author guanhong 15/7/27 下午6:58.
 */

@Controller
public class HelloWorld extends BaseController{

    @Autowired
    private UserService jpaUserService;

    @ResponseBody
    @RequestMapping("/")
    public String home() {
        return "HelloWorld";
    }

    @RequestMapping("/testvm")
    public String test(Model model, Long id) throws Exception{
        if(id ==null) {
            model.addAttribute("user", "null");
            model.addAttribute("vmchange", "vmchangetest");
            return "test";
        }
        model.addAttribute("vmtest", "guanhong对了~测一下中文");
        //model.addAttribute("vmchange", "vmchange");
        model.addAttribute("vmchange", "vmchangetest");
        User user;
        try {
            user = jpaUserService.getById(id);
            user.getId();
        }catch (Exception e){
            log.error("entity error",e);
            user = null;
        }
        if (user != null)
            model.addAttribute("user", user.getUsername());
        else
            model.addAttribute("user", "null");
        return "test";
    }

    @RequestMapping("/updateUser")
    public String updateUser(RedirectAttributes redirectAttributes,Long id,String name) throws Exception{
        if(id ==null)
            id = 1l;
        if(StringUtils.isBlank(name))
            name = "guanhong!";
        jpaUserService.updateUserById(name,id);
        redirectAttributes.addAttribute("id",id);
        return "redirect:/testvm";
    }

    @ResponseBody
    @RequestMapping("/invalidUser")
    public String invalidUser(Long id) throws Exception{
        jpaUserService.deleteUser(id);
        return "success";
    }
}
