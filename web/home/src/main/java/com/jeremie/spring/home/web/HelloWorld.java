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

import java.util.Arrays;

/**
 * @author guanhong 15/7/27 下午6:58.
 */

@Controller
public class HelloWorld extends BaseController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping("/")
    public String home() {
        return "HelloWorld";
    }

    @RequestMapping("/testvm")
    public String test(Model model, Long id) throws Exception {
        if (id == null) {
            model.addAttribute("user", "null");
            model.addAttribute("vmchange", "vmchangetest");
            return "test";
        }
        model.addAttribute("vmtest", "guanhong对了~测一下中文");
        //model.addAttribute("vmchange", "vmchange");
        model.addAttribute("vmchange", "vmchangetest");
        User user = userService.getById(id);
        String test = userService.testGetString();

        //测试基本类型
        String[] stringArray = userService.testStringArray();
        long[] longArray = userService.testlongArray();
        long num = userService.testLong();
        Arrays.asList(stringArray).forEach(System.out::println);
        for (long a :longArray)
            System.out.println(a);
        System.out.println(num);

        model.addAttribute("testGetString" , test);
        if (user != null)
            model.addAttribute("user", user.getUsername());
        else
            model.addAttribute("user", "null");
        return "test";
    }

    @RequestMapping("/updateUser")
    public String updateUser(RedirectAttributes redirectAttributes, Long id, String name) throws Exception {
        if (id == null)
            id = 1l;
        if (StringUtils.isBlank(name))
            name = "guanhong!";
        userService.updateUserById(name, id);
        redirectAttributes.addAttribute("id", id);
        return "redirect:/testvm";
    }

    @ResponseBody
    @RequestMapping("/invalidUser")
    public String invalidUser(Long id) throws Exception {
        userService.deleteUser(id);
        return "success";
    }
}
